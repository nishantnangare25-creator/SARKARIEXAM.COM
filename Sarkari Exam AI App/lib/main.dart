import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_filex/open_filex.dart';
import 'package:permission_handler/permission_handler.dart';
import 'package:flutter_web_auth_2/flutter_web_auth_2.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const SarkariExamApp());
}

class SarkariExamApp extends StatelessWidget {
  const SarkariExamApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Sarkari Exam AI',
      theme: ThemeData(
        primaryColor: const Color(0xFF2563EB),
        colorScheme: ColorScheme.fromSeed(seedColor: const Color(0xFF2563EB)),
        useMaterial3: true,
      ),
      debugShowCheckedModeBanner: false,
      home: const WebViewScreen(),
    );
  }
}

// ─────────────────────────────────────────────────────────────
// Data class for popup WebView entries
// ─────────────────────────────────────────────────────────────
class _PopupEntry {
  final String url;
  final WebViewController controller;
  bool isLoading;
  bool hasError;

  _PopupEntry({
    required this.url,
    required this.controller,
    this.isLoading = true,
    this.hasError = false,
  });
}

// ─────────────────────────────────────────────────────────────
// WebView Screen
// ─────────────────────────────────────────────────────────────
class WebViewScreen extends StatefulWidget {
  const WebViewScreen({super.key});

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends State<WebViewScreen>
    with WidgetsBindingObserver {
  late final WebViewController _controller;

  bool _isLoading = true;
  bool _hasError = false;
  bool _bridgeInjected = false;
  String _currentUrl = '';

  // Popup WebView stack — each window.open() pushes here
  final List<_PopupEntry> _popupStack = [];

  // Track last known good URL for background-restore
  String _lastGoodUrl = 'https://sarkariexamai.com';

  // Track app lifecycle
  AppLifecycleState _lastLifecycleState = AppLifecycleState.resumed;

  static const String _homeUrl = 'https://sarkariexamai.com';
  static const String _fbApiKey = 'AIzaSyCWoAYg_1WQPABOS8WzFxoQCcgDY5Rgyzc';

  static const String _googleClientId =
      '868025142353-web.apps.googleusercontent.com';
  static const String _redirectScheme = 'com.sarkariexamai.app';
  static const String _redirectUrl = 'com.sarkariexamai.app:/oauth2redirect';

  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addObserver(this);
    _initWebView();
  }

  @override
  void dispose() {
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  // ── App lifecycle handler ────────────────────────────────────
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    debugPrint('[Lifecycle] App state changed: $state');
    if (state == AppLifecycleState.resumed &&
        _lastLifecycleState != AppLifecycleState.resumed) {
      _onAppResumed();
    }
    _lastLifecycleState = state;
  }

  Future<void> _onAppResumed() async {
    debugPrint('[Resume] Checking WebView health...');
    await Future.delayed(const Duration(milliseconds: 1000));
    if (!mounted) return;
    try {
      final result = await _controller.runJavaScriptReturningResult(
        '''(function() {
          if (!document.body) return "dead";
          var root = document.getElementById("root") || document.getElementById("app");
          if (!root) return (document.body.children.length > 0 ? "ok" : "empty");
          return (root.children.length > 0 ? "ok" : "empty");
        })()''',
      );
      final health = result.toString().replaceAll('"', '').trim();
      debugPrint('[Resume] WebView health: $health');
      if (health == 'empty' || health == 'dead' || health == 'null') {
        _reloadWebView(_lastGoodUrl);
      } else {
        _bridgeInjected = false;
        await Future.delayed(const Duration(milliseconds: 200));
        _injectBridge(_controller);
      }
    } catch (e) {
      debugPrint('[Resume] JS eval failed ($e) — reloading: $_lastGoodUrl');
      _reloadWebView(_lastGoodUrl);
    }
  }

  void _reloadWebView(String url) {
    if (!mounted) return;
    setState(() {
      _isLoading = true;
      _hasError = false;
      _bridgeInjected = false;
    });
    _controller.loadRequest(Uri.parse(url));
  }

  // ─────────────────────────────────────────────────────────────
  // CORE FIX: Push a new popup WebView instead of loading
  // in the same WebView. This avoids React state conflicts.
  // ─────────────────────────────────────────────────────────────
  void _pushPopupWebView(String url) {
    if (!mounted) return;
    debugPrint('[Popup] Opening in new WebView layer: $url');

    final popupController = _buildPopupController(url);
    final entry = _PopupEntry(url: url, controller: popupController);

    setState(() {
      _popupStack.add(entry);
    });
  }

  void _closeTopPopup() {
    if (_popupStack.isEmpty) return;
    setState(() {
      _popupStack.removeLast();
    });
  }

  // ─────────────────────────────────────────────────────────────
  // Build a WebViewController for popup WebView
  // ─────────────────────────────────────────────────────────────
  WebViewController _buildPopupController(String url) {
    final ctrl = WebViewController();
    ctrl
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setUserAgent(
          'Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 '
          '(KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36')
      ..setBackgroundColor(Colors.white)
      ..addJavaScriptChannel(
        'FlutterDownload',
        onMessageReceived: (msg) => _handleDownload(msg.message),
      )
      ..addJavaScriptChannel(
        'FlutterGoogleSignIn',
        onMessageReceived: (msg) {
          bool isReg = msg.message == 'signup';
          _showLoginSheet(isRegister: isReg);
        },
      )
      ..addJavaScriptChannel(
        'FlutterPageReady',
        onMessageReceived: (_) {
          // Find this popup and mark it loaded
          final idx = _popupStack.indexWhere((e) => e.controller == ctrl);
          if (idx != -1 && mounted) {
            setState(() => _popupStack[idx].isLoading = false);
          }
        },
      )
      // Nested window.open → open another popup layer
      ..addJavaScriptChannel(
        'FlutterNavigation',
        onMessageReceived: (msg) {
          final newUrl = msg.message.trim();
          if (newUrl.isEmpty || newUrl.startsWith('blob:')) return;
          final resolved =
              newUrl.startsWith('http') ? newUrl : '$_homeUrl$newUrl';
          _pushPopupWebView(resolved);
        },
      )
      ..setNavigationDelegate(NavigationDelegate(
        onPageStarted: (pageUrl) {
          final idx = _popupStack.indexWhere((e) => e.controller == ctrl);
          if (idx != -1 && mounted) {
            // For popups, we usually want to show loading on the first load
            // Hash changes in popups are rarer but should be handled.
            final isReal = _isRealPageChangeInPopup(idx, pageUrl);
            if (isReal) {
              setState(() {
                _popupStack[idx].isLoading = true;
                _popupStack[idx].hasError = false;
              });
            } else {
              // Hash change: re-inject bridge immediately
              _injectBridgeInto(ctrl);
            }
          }
        },
        onPageFinished: (pageUrl) {
          _injectBridgeInto(ctrl);
          final idx = _popupStack.indexWhere((e) => e.controller == ctrl);
          if (idx != -1 && mounted) {
            Future.delayed(const Duration(milliseconds: 800), () {
              if (mounted && idx < _popupStack.length) {
                setState(() => _popupStack[idx].isLoading = false);
              }
            });
          }
          // Track last good URL
          if (pageUrl.contains('sarkariexamai.com') &&
              !pageUrl.startsWith('blob:') &&
              !pageUrl.startsWith('data:') &&
              !pageUrl.startsWith('about:')) {
            _lastGoodUrl = pageUrl;
          }
        },
        onWebResourceError: (e) {
          if (e.isForMainFrame == true) {
            final idx = _popupStack.indexWhere((e2) => e2.controller == ctrl);
            if (idx != -1 && mounted) {
              setState(() => _popupStack[idx].hasError = true);
            }
          }
        },
        onNavigationRequest: (req) {
          final navUrl = req.url;
          if (navUrl.contains('sarkariexamai.com')) {
            return NavigationDecision.navigate;
          }
          if (navUrl.startsWith('about:') ||
              navUrl.startsWith('blob:') ||
              navUrl.startsWith('data:')) {
            return NavigationDecision.navigate;
          }
          if (navUrl.contains('accounts.google.com') ||
              navUrl.contains('google.com/o/oauth2') ||
              navUrl.contains('firebaseapp.com') ||
              navUrl.contains('googleapis.com')) {
            return NavigationDecision.navigate;
          }
          _launchExternal(navUrl);
          return NavigationDecision.prevent;
        },
      ))
      ..loadRequest(Uri.parse(url));

    return ctrl;
  }

  // ─────────────────────────────────────────────────────────────
  // Main WebView initialization
  // ─────────────────────────────────────────────────────────────
  void _initWebView() {
    _controller = WebViewController();
    _controller
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setUserAgent(
          'Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 '
          '(KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36')
      ..setBackgroundColor(Colors.white)

      ..addJavaScriptChannel(
        'FlutterDownload',
        onMessageReceived: (msg) => _handleDownload(msg.message),
      )
      ..addJavaScriptChannel(
        'FlutterPageError',
        onMessageReceived: (msg) {
          debugPrint('[JS ERROR] ${msg.message}');
          if (mounted) setState(() => _hasError = true);
        },
      )
      ..addJavaScriptChannel(
        'FlutterGoogleSignIn',
        onMessageReceived: (msg) {
          bool isReg = msg.message == 'signup';
          _showLoginSheet(isRegister: isReg);
        },
      )
      ..addJavaScriptChannel(
        'FlutterPageReady',
        onMessageReceived: (msg) {
          if (msg.message == 'ready') {
            if (mounted && _isLoading) {
              setState(() => _isLoading = false);
            }
          } else if (msg.message == 'timeout') {
            // Only show timeout/white screen if we're STILL loading
            if (mounted && _isLoading) {
              // Instead of just hiding, we could show a 'Taking long' message
              // For now, let's at least ensure we don't stay white forever
              setState(() => _isLoading = false);
            }
          }
        },
      )

      // ── THE FIX: window.open → popup overlay, NOT same WebView ──
      ..addJavaScriptChannel(
        'FlutterNavigation',
        onMessageReceived: (msg) {
          final url = msg.message.trim();
          if (url.isEmpty) return;
          if (url.startsWith('blob:')) return;
          debugPrint('[FlutterNavigation] → Popup: $url');
          final resolved = url.startsWith('http') ? url : '$_homeUrl$url';
          _pushPopupWebView(resolved);
        },
      )

      ..setNavigationDelegate(NavigationDelegate(
        onPageStarted: (url) {
          final isReal = _isRealPageChange(url);
          if (isReal) {
            _bridgeInjected = false;
            if (mounted) {
              setState(() {
                _isLoading = true;
                _hasError = false;
              });
            }
          } else {
            // Hash change: re-inject bridge immediately
            _bridgeInjected = false;
            _injectBridge(_controller);
          }
          _currentUrl = url;

          // START/RESET SAFETY TIMEOUT (15s)
          _controller.runJavaScript('''
            if (window.__loadingTimer) clearTimeout(window.__loadingTimer);
            window.__loadingTimer = setTimeout(() => {
              if (window.FlutterPageReady) window.FlutterPageReady.postMessage("timeout");
            }, 15000);
          ''');

          if (url.contains('sarkariexamai.com') &&
              !url.startsWith('blob:') &&
              !url.startsWith('data:') &&
              !url.startsWith('about:')) {
            _lastGoodUrl = url;
          }
        },

        onPageFinished: (url) {
          _injectBridge(_controller);

          if (mounted && _isLoading) {
            Future.delayed(const Duration(milliseconds: 800), () {
              if (mounted && _isLoading) {
                setState(() => _isLoading = false);
              }
            });
          }

          if (url.contains('sarkariexamai.com') &&
              !url.startsWith('blob:') &&
              !url.startsWith('data:') &&
              !url.startsWith('about:')) {
            _lastGoodUrl = url;
          }
        },

        onWebResourceError: (e) {
          debugPrint('WebView error: ${e.description} (${e.errorCode})');
          if (e.isForMainFrame == true) {
            if (mounted) setState(() => _hasError = true);
          }
        },

        onNavigationRequest: (req) {
          final url = req.url;
          if (url.contains('sarkariexamai.com')) return NavigationDecision.navigate;
          if (url.startsWith('about:') ||
              url.startsWith('blob:') ||
              url.startsWith('data:')) {
            return NavigationDecision.navigate;
          }
          if (url.contains('accounts.google.com') ||
              url.contains('google.com/o/oauth2') ||
              url.contains('firebaseapp.com') ||
              url.contains('googleapis.com')) {
            return NavigationDecision.navigate;
          }
          _launchExternal(url);
          return NavigationDecision.prevent;
        },
      ))
      ..loadRequest(Uri.parse(_homeUrl));
  }

  bool _isRealPageChange(String newUrl) {
    if (_currentUrl.isEmpty) return true;
    try {
      final current = Uri.parse(_currentUrl);
      final next = Uri.parse(newUrl);
      if (current.host != next.host || current.scheme != next.scheme) return true;
      if (current.path != next.path) return true;
      if (_currentUrl == '' ||
          _currentUrl.startsWith('about:') ||
          _currentUrl.startsWith('data:')) {
        return true;
      }
      return false;
    } catch (_) {
      return true;
    }
  }

  bool _isRealPageChangeInPopup(int idx, String newUrl) {
    if (idx < 0 || idx >= _popupStack.length) return true;
    final currentUrl = _popupStack[idx].url;
    if (currentUrl.isEmpty) return true;
    try {
      final current = Uri.parse(currentUrl);
      final next = Uri.parse(newUrl);
      if (current.host != next.host || current.scheme != next.scheme) return true;
      if (current.path != next.path) return true;
      return false;
    } catch (_) {
      return true;
    }
  }

  void _launchExternal(String url) {
    try {
      launchUrl(Uri.parse(url), mode: LaunchMode.externalApplication);
    } catch (e) {
      debugPrint('External URL launch failed: $url — $e');
    }
  }

  // ─────────────────────────────────────────────────────────────
  // JS Bridge injection — works for both main & popup controllers
  // ─────────────────────────────────────────────────────────────
  void _injectBridge(WebViewController ctrl) {
    if (ctrl == _controller) {
      if (_bridgeInjected) return;
      _bridgeInjected = true;
    }
    _injectBridgeInto(ctrl);
  }

  void _injectBridgeInto(WebViewController ctrl) {
    ctrl.runJavaScript(r'''
      (function () {
        if (window.__flutterBridgeInjected) return;
        window.__flutterBridgeInjected = true;
        window.__isFlutterApp = true;

        // ── Blob URL → base64 download ────────────────────
        function downloadBlob(blobUrl) {
          if (!window.FlutterDownload) return;
          fetch(blobUrl)
            .then(function(res) { return res.blob(); })
            .then(function(blob) {
               var reader = new FileReader();
               reader.onload = function() {
                  var b64 = reader.result.split(',')[1];
                  window.FlutterDownload.postMessage(JSON.stringify({
                    data: b64,
                    fileName: 'Mock_Test_Result.pdf'
                  }));
               };
               reader.readAsDataURL(blob);
            })
            .catch(function(err) {
               console.error('Blob download error:', err);
            });
        }

        // ── window.open → FlutterNavigation (popup overlay) ──
        // This is the CORE FIX: instead of trying to open a new tab
        // (which WebView can't do), we signal Flutter to create a
        // NEW WebView overlay above this one. No state conflicts.
        window.open = function(url, target, features) {
          if (url && typeof url === 'string') {
            if (url.startsWith('blob:')) {
              downloadBlob(url);
            } else if (window.FlutterNavigation) {
              window.FlutterNavigation.postMessage(url);
            } else {
              window.location.href = url;
            }
          }
          // Return a stable fake window object
          var fakeWin = {
            closed: false,
            name: '_blank',
            opener: window,
            close: function() { fakeWin.closed = true; },
            focus: function() {},
            blur: function() {},
            postMessage: function() {},
            location: { href: url || '', assign: function() {}, replace: function() {} },
            document: { write: function() {}, close: function() {}, readyState: 'complete' }
          };
          return fakeWin;
        };

        // ── Fix <a> tags with target="_blank" ────────────────
        function applyLinkFixes() {
          try {
            document.querySelectorAll('a').forEach(function(a) {
              if (!a || !a.href || typeof a.href !== 'string') return;

              if (a.href.startsWith('blob:') && !a.dataset.blobFixed) {
                a.dataset.blobFixed = '1';
                a.addEventListener('click', function(e) {
                  e.preventDefault();
                  e.stopPropagation();
                  downloadBlob(a.href);
                });
              } else if (a.href.includes('/login') && !a.dataset.loginFixed) {
                a.dataset.loginFixed = '1';
                a.addEventListener('click', function(e) {
                  if (window.FlutterGoogleSignIn) {
                    e.preventDefault();
                    e.stopPropagation();
                    var mode = a.href.includes('mode=signup') ? 'signup' : 'login';
                    window.FlutterGoogleSignIn.postMessage(mode);
                  }
                }, true);
              } else if (a.target === '_blank' && !a.dataset.blankFixed) {
                a.dataset.blankFixed = '1';
                a.removeAttribute('rel');
                a.addEventListener('click', function(e) {
                  if (a.href && !a.href.startsWith('blob:') && window.FlutterNavigation) {
                    e.preventDefault();
                    e.stopPropagation();
                    window.FlutterNavigation.postMessage(a.href);
                  }
                });
              }
            });
          } catch(err) {
            console.error('Flutter bridge link fix error:', err);
          }
        }

        applyLinkFixes();
        new MutationObserver(applyLinkFixes)
          .observe(document.body, { childList: true, subtree: true });

        if (window.FlutterPageReady) {
          window.FlutterPageReady.postMessage('ready');
        }

        document.addEventListener('visibilitychange', function() {
          if (!document.hidden && window.FlutterPageReady) {
            setTimeout(function() {
              window.FlutterPageReady.postMessage('visible');
            }, 500);
          }
        });

        console.log('[FlutterBridge] v3 popup-mode injected');
      })();
    ''');
  }

  // ─────────────────────────────────────────────────────────────
  // Login sheet
  // ─────────────────────────────────────────────────────────────
  void _showLoginSheet({bool isRegister = false}) {
    if (!mounted) return;
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _LoginSheet(
        initialIsRegister: isRegister,
        onGoogleLogin: () {
          Navigator.pop(context);
          _nativeGoogleSignIn();
        },
        onSubmit: (email, pw, isReg) async {
          Navigator.pop(context);
          await _doWebLogin(email, pw, isReg);
        },
      ),
    );
  }

  // ─────────────────────────────────────────────────────────────
  // Native Google Sign In
  // ─────────────────────────────────────────────────────────────
  Future<void> _nativeGoogleSignIn() async {
    _showSnack('Opening Google Sign In…', loading: true);
    try {
      final googleAuthUrl = Uri.https('accounts.google.com', '/o/oauth2/v2/auth', {
        'client_id': _googleClientId,
        'redirect_uri': _redirectUrl,
        'response_type': 'token',
        'scope': 'email profile openid',
        'prompt': 'select_account',
      });

      final result = await FlutterWebAuth2.authenticate(
        url: googleAuthUrl.toString(),
        callbackUrlScheme: _redirectScheme,
      );

      final uri = Uri.parse(result);
      final params = Uri.splitQueryString(uri.fragment);
      final accessToken = params['access_token'];

      if (accessToken == null || accessToken.isEmpty) {
        _showSnack('Google Sign In failed: no token received.');
        return;
      }

      _showSnack('Signing in with Google…', loading: true);

      final res = await http.post(
        Uri.parse(
            'https://identitytoolkit.googleapis.com/v1/accounts:signInWithIdp?key=$_fbApiKey'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'postBody': 'access_token=$accessToken&providerId=google.com',
          'requestUri': 'http://localhost',
          'returnIdpCredential': true,
          'returnSecureToken': true,
        }),
      );

      final data = json.decode(res.body);

      if (data['error'] != null) {
        _showSnack('Error: ${data['error']['message'] ?? 'Google login failed'}');
        return;
      }

      final idToken = data['idToken'] ?? '';
      final uid = data['localId'] ?? '';
      final userEmail = data['email'] ?? '';
      final displayName = data['displayName'] ?? userEmail.split('@')[0];
      final photoUrl = data['photoUrl'] ?? '';
      final refreshToken = data['refreshToken'] ?? '';
      final expiresIn =
          int.tryParse(data['expiresIn']?.toString() ?? '3600') ?? 3600;
      final expirationTime = DateTime.now()
          .add(Duration(seconds: expiresIn))
          .millisecondsSinceEpoch;

      final userJson = json.encode({
        "uid": uid,
        "email": userEmail,
        "emailVerified": true,
        "displayName": displayName,
        "photoURL": photoUrl,
        "isAnonymous": false,
        "providerData": [
          {
            "providerId": "google.com",
            "uid": uid,
            "email": userEmail,
            "displayName": displayName,
            "photoURL": photoUrl
          }
        ],
        "stsTokenManager": {
          "refreshToken": refreshToken,
          "accessToken": idToken,
          "expirationTime": expirationTime
        },
        "createdAt": "${DateTime.now().millisecondsSinceEpoch}",
        "lastLoginAt": "${DateTime.now().millisecondsSinceEpoch}",
        "apiKey": _fbApiKey,
        "appName": "[DEFAULT]"
      });

      await _controller.runJavaScript('''
        (function() {
          try {
            var req = indexedDB.open('firebaseLocalStorageDb', 1);
            req.onupgradeneeded = function(e) {
              e.target.result.createObjectStore('firebaseLocalStorage', { keyPath: 'fbase_key' });
            };
            req.onsuccess = function(e) {
              var db = e.target.result;
              var tx = db.transaction(['firebaseLocalStorage'], 'readwrite');
              var store = tx.objectStore('firebaseLocalStorage');
              var authKey = 'firebase:authUser:${_fbApiKey}:[DEFAULT]';
              store.put({ fbase_key: authKey, value: $userJson });
              tx.oncomplete = function() {
                window.location.href = '/#/dashboard';
              };
            };
          } catch(err) {
            localStorage.setItem(
              'firebase:authUser:${_fbApiKey}:[DEFAULT]',
              JSON.stringify($userJson)
            );
            window.location.href = '/#/dashboard';
          }
        })();
      ''');

      _showSnack('✅ Signed in with Google successfully!');
    } catch (e) {
      final errStr = e.toString();
      if (errStr.contains('CANCELED') || errStr.contains('canceled')) {
        _showSnack('Google Sign In cancelled.');
      } else {
        debugPrint('Google Sign In error: $e');
        _showSnack('Google Sign In failed. Please try again.');
      }
    }
  }

  // ─────────────────────────────────────────────────────────────
  // Email/Password login
  // ─────────────────────────────────────────────────────────────
  Future<void> _doWebLogin(String email, String pw, bool isRegister) async {
    _showSnack('Signing in…', loading: true);
    try {
      final endpoint = isRegister
          ? 'https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=$_fbApiKey'
          : 'https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=$_fbApiKey';

      final res = await http.post(
        Uri.parse(endpoint),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'email': email,
          'password': pw,
          'returnSecureToken': true,
        }),
      );

      final data = json.decode(res.body);

      if (data['error'] != null) {
        _showSnack(_friendlyError(data['error']['message'] ?? 'Login failed'));
        return;
      }

      final idToken = data['idToken'];
      final uid = data['localId'];
      final userEmail = data['email'];
      final displayName = data['displayName'] ?? userEmail.split('@')[0];
      final refreshToken = data['refreshToken'];
      final expiresIn = int.tryParse(data['expiresIn'].toString()) ?? 3600;
      final expirationTime = DateTime.now()
          .add(Duration(seconds: expiresIn))
          .millisecondsSinceEpoch;

      final userJson = json.encode({
        "uid": uid,
        "email": userEmail,
        "emailVerified": false,
        "displayName": displayName,
        "isAnonymous": false,
        "providerData": [
          {"providerId": "password", "uid": userEmail, "email": userEmail}
        ],
        "stsTokenManager": {
          "refreshToken": refreshToken,
          "accessToken": idToken,
          "expirationTime": expirationTime
        },
        "createdAt": "${DateTime.now().millisecondsSinceEpoch}",
        "lastLoginAt": "${DateTime.now().millisecondsSinceEpoch}",
        "apiKey": _fbApiKey,
        "appName": "[DEFAULT]"
      });

      await _controller.runJavaScript('''
        (function() {
          try {
            var req = indexedDB.open('firebaseLocalStorageDb', 1);
            req.onupgradeneeded = function(e) {
              e.target.result.createObjectStore('firebaseLocalStorage', { keyPath: 'fbase_key' });
            };
            req.onsuccess = function(e) {
              var db = e.target.result;
              var tx = db.transaction(['firebaseLocalStorage'], 'readwrite');
              var store = tx.objectStore('firebaseLocalStorage');
              var authKey = 'firebase:authUser:${_fbApiKey}:[DEFAULT]';
              store.put({ fbase_key: authKey, value: $userJson });
              tx.oncomplete = function() { window.location.href = '/#/dashboard'; };
            };
          } catch(err) {
            localStorage.setItem(
              'firebase:authUser:${_fbApiKey}:[DEFAULT]',
              JSON.stringify($userJson)
            );
            window.location.href = '/#/dashboard';
          }
        })();
      ''');

      _showSnack('✅ Signed in successfully!');
    } catch (e) {
      _showSnack('Error: ${e.toString()}');
    }
  }

  String _friendlyError(String code) {
    switch (code) {
      case 'EMAIL_NOT_FOUND':
        return 'No account found with this email.';
      case 'INVALID_PASSWORD':
        return 'Wrong password. Please try again.';
      case 'EMAIL_EXISTS':
        return 'Email is already registered. Try logging in.';
      case 'WEAK_PASSWORD : Password should be at least 6 characters':
        return 'Password must be at least 6 characters.';
      case 'INVALID_LOGIN_CREDENTIALS':
        return 'Invalid email or password.';
      default:
        return code.replaceAll('_', ' ');
    }
  }

  // ─────────────────────────────────────────────────────────────
  // PDF Download Handler
  // ─────────────────────────────────────────────────────────────
  Future<void> _handleDownload(String message) async {
    try {
      final decoded = json.decode(message);
      final String base64Data = decoded['data'];
      final String fileName = decoded['fileName'] ?? 'document.pdf';
      final bytes = base64Decode(base64Data);

      File file;

      if (Platform.isAndroid) {
        final sdkInt = await _androidSdkInt();
        if (sdkInt < 29) {
          final perm = await Permission.storage.request();
          if (!perm.isGranted) {
            _showSnack('Storage permission denied.');
            return;
          }
        }
        const downloadsPath = '/storage/emulated/0/Download';
        final downloadsDir = Directory(downloadsPath);
        if (await downloadsDir.exists()) {
          file = File('$downloadsPath/$fileName');
        } else {
          final extDir = await getExternalStorageDirectory();
          file = File('${extDir!.path}/$fileName');
        }
      } else {
        final dir = await getApplicationDocumentsDirectory();
        file = File('${dir.path}/$fileName');
      }

      await file.writeAsBytes(bytes);
      debugPrint('PDF saved: ${file.path}');

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(SnackBar(
          content: Text('✅ PDF saved: $fileName'),
          backgroundColor: const Color(0xFF10B981),
          duration: const Duration(seconds: 6),
          action: SnackBarAction(
            label: 'OPEN',
            textColor: Colors.white,
            onPressed: () => OpenFilex.open(file.path),
          ),
        ));
        await Future.delayed(const Duration(milliseconds: 600));
        await OpenFilex.open(file.path);
      }
    } catch (e) {
      debugPrint('Download error: $e');
      _showSnack('PDF download failed: $e');
    }
  }

  Future<int> _androidSdkInt() async {
    if (!Platform.isAndroid) return 0;
    return 29;
  }

  void _showSnack(String msg, {bool loading = false}) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(SnackBar(
      content: Row(children: [
        if (loading) ...[
          const SizedBox(
              width: 18,
              height: 18,
              child: CircularProgressIndicator(
                  strokeWidth: 2, color: Colors.white)),
          const SizedBox(width: 12),
        ],
        Expanded(child: Text(msg)),
      ]),
      duration: Duration(seconds: loading ? 2 : 4),
    ));
  }

  // ─────────────────────────────────────────────────────────────
  // Build
  // ─────────────────────────────────────────────────────────────
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) return;

        // If popup stack has entries, close the top popup first
        if (_popupStack.isNotEmpty) {
          _closeTopPopup();
          return;
        }

        // Otherwise navigate back in main WebView
        final canGoBack = await _controller.canGoBack();
        if (canGoBack) {
          await _controller.goBack();
        } else {
          if (context.mounted) {
            SystemNavigator.pop();
          }
        }
      },
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Stack(children: [
            // ── Main WebView ─────────────────────────────────
            if (_hasError)
              _ErrorView(onRetry: () {
                setState(() {
                  _hasError = false;
                  _isLoading = true;
                  _bridgeInjected = false;
                });
                _controller.loadRequest(Uri.parse(_lastGoodUrl));
              })
            else
              WebViewWidget(controller: _controller),

            // ── Main loading overlay ─────────────────────────
            if (_isLoading && !_hasError && _popupStack.isEmpty)
              _LoadingOverlay(),

            // ── Popup WebView stack ──────────────────────────
            // Each popup slides up from bottom like a new browser tab
            ..._popupStack.asMap().entries.map((entry) {
              final idx = entry.key;
              final popup = entry.value;
              return _PopupWebViewLayer(
                key: ValueKey('popup_$idx'),
                entry: popup,
                onClose: _closeTopPopup,
              );
            }),
          ]),
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────
// Popup WebView Layer Widget
// Shows a new WebView above the main one, with a close button
// ─────────────────────────────────────────────────────────────
class _PopupWebViewLayer extends StatelessWidget {
  final _PopupEntry entry;
  final VoidCallback onClose;

  const _PopupWebViewLayer({
    super.key,
    required this.entry,
    required this.onClose,
  });

  @override
  Widget build(BuildContext context) {
    return Positioned.fill(
      child: Material(
        color: Colors.white,
        child: Column(
          children: [
            // ── Top bar with close button ────────────────────
            Container(
              height: 48,
              decoration: const BoxDecoration(
                color: Color(0xFF1E3A5F),
                boxShadow: [
                  BoxShadow(
                      color: Colors.black12,
                      blurRadius: 4,
                      offset: Offset(0, 2))
                ],
              ),
              child: Row(
                children: [
                  IconButton(
                    icon: const Icon(Icons.close_rounded,
                        color: Colors.white, size: 22),
                    onPressed: onClose,
                    tooltip: 'Close',
                  ),
                  Expanded(
                    child: Text(
                      _cleanUrl(entry.url),
                      style: const TextStyle(
                        color: Colors.white70,
                        fontSize: 12,
                        overflow: TextOverflow.ellipsis,
                      ),
                      maxLines: 1,
                    ),
                  ),
                  const SizedBox(width: 8),
                ],
              ),
            ),
            // ── WebView content ──────────────────────────────
            Expanded(
              child: Stack(
                children: [
                  WebViewWidget(controller: entry.controller),
                  if (entry.isLoading && !entry.hasError) _LoadingOverlay(),
                  if (entry.hasError)
                    Center(
                      child: Column(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          const Icon(Icons.wifi_off_rounded,
                              size: 56, color: Colors.grey),
                          const SizedBox(height: 12),
                          const Text('Page failed to load',
                              style: TextStyle(fontSize: 16)),
                          const SizedBox(height: 16),
                          ElevatedButton(
                            onPressed: () =>
                                entry.controller.loadRequest(Uri.parse(entry.url)),
                            child: const Text('Retry'),
                          ),
                        ],
                      ),
                    ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _cleanUrl(String url) {
    try {
      final u = Uri.parse(url);
      return u.host + (u.path.isNotEmpty && u.path != '/' ? u.path : '');
    } catch (_) {
      return url;
    }
  }
}

// ─────────────────────────────────────────────────────────────
// Loading Overlay
// ─────────────────────────────────────────────────────────────
class _LoadingOverlay extends StatelessWidget {
  const _LoadingOverlay();

  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      child: const Center(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            CircularProgressIndicator(
              strokeWidth: 4,
              color: Color(0xFF2563EB),
            ),
            SizedBox(height: 20),
            Text(
              'Initializing Smart Exam Engine…',
              style: TextStyle(
                color: Color(0xFF64748B),
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
            SizedBox(height: 8),
            Text(
              'Building your study session',
              style: TextStyle(
                color: Color(0xFF94A3B8),
                fontSize: 11,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────
// Error View Widget
// ─────────────────────────────────────────────────────────────
class _ErrorView extends StatelessWidget {
  final VoidCallback onRetry;
  const _ErrorView({required this.onRetry});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(mainAxisAlignment: MainAxisAlignment.center, children: [
          const Icon(Icons.wifi_off_rounded, size: 72, color: Color(0xFFCBD5E1)),
          const SizedBox(height: 20),
          const Text('Connection Failed',
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          const Text('Check your internet and try again.',
              textAlign: TextAlign.center,
              style: TextStyle(color: Colors.grey, fontSize: 14)),
          const SizedBox(height: 28),
          ElevatedButton.icon(
            icon: const Icon(Icons.refresh_rounded),
            label: const Text('Retry'),
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2563EB),
              foregroundColor: Colors.white,
              padding: const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
              shape:
                  RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            ),
            onPressed: onRetry,
          ),
        ]),
      ),
    );
  }
}

// ─────────────────────────────────────────────────────────────
// Native Login Bottom Sheet
// ─────────────────────────────────────────────────────────────
class _LoginSheet extends StatefulWidget {
  final Future<void> Function(String email, String pw, bool isRegister) onSubmit;
  final VoidCallback onGoogleLogin;
  final bool initialIsRegister;

  const _LoginSheet({
    required this.onSubmit,
    required this.onGoogleLogin,
    this.initialIsRegister = false,
  });

  @override
  State<_LoginSheet> createState() => _LoginSheetState();
}

class _LoginSheetState extends State<_LoginSheet> {
  final _emailCtrl = TextEditingController();
  final _pwCtrl = TextEditingController();
  bool _showPw = false;
  bool _loading = false;
  late bool _isRegister;
  String _error = '';

  @override
  void initState() {
    super.initState();
    _isRegister = widget.initialIsRegister;
  }

  @override
  void dispose() {
    _emailCtrl.dispose();
    _pwCtrl.dispose();
    super.dispose();
  }

  Future<void> _submit() async {
    final email = _emailCtrl.text.trim();
    final pw = _pwCtrl.text;
    if (email.isEmpty || pw.isEmpty) {
      setState(() => _error = 'Email and password are required.');
      return;
    }
    setState(() {
      _loading = true;
      _error = '';
    });
    try {
      await widget.onSubmit(email, pw, _isRegister);
    } catch (e) {
      if (mounted) setState(() => _error = e.toString());
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(
        left: 24,
        right: 24,
        top: 20,
        bottom: MediaQuery.of(context).viewInsets.bottom + 24,
      ),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Center(
          child: Container(
            width: 40,
            height: 4,
            decoration: BoxDecoration(
                color: Colors.grey[300],
                borderRadius: BorderRadius.circular(2)),
          ),
        ),
        const SizedBox(height: 20),
        Row(children: [
          Container(
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: const Color(0xFF2563EB).withOpacity(0.1),
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(Icons.school_rounded,
                color: Color(0xFF2563EB), size: 24),
          ),
          const SizedBox(width: 12),
          Column(crossAxisAlignment: CrossAxisAlignment.start, children: [
            Text(_isRegister ? 'Create Account' : 'Login',
                style: const TextStyle(
                    fontSize: 20, fontWeight: FontWeight.bold)),
            const Text('Sarkari Exam AI',
                style: TextStyle(color: Colors.grey, fontSize: 12)),
          ]),
          const Spacer(),
          TextButton(
            onPressed: () =>
                setState(() {
                  _isRegister = !_isRegister;
                  _error = '';
                }),
            child: Text(_isRegister ? 'Login' : 'Sign up',
                style: const TextStyle(
                    color: Color(0xFF2563EB), fontWeight: FontWeight.w600)),
          ),
        ]),
        const SizedBox(height: 12),
        SizedBox(
          width: double.infinity,
          height: 50,
          child: OutlinedButton.icon(
            onPressed: widget.onGoogleLogin,
            icon: const Icon(Icons.g_mobiledata_rounded,
                color: Colors.black, size: 36),
            label: const Text('Sign in with Google',
                style: TextStyle(
                    color: Colors.black,
                    fontSize: 15,
                    fontWeight: FontWeight.w600)),
            style: OutlinedButton.styleFrom(
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(14)),
              side: BorderSide(color: Colors.grey[300]!, width: 1.5),
            ),
          ),
        ),
        const SizedBox(height: 16),
        Row(children: [
          Expanded(child: Divider(color: Colors.grey[300])),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 10),
            child: Text('OR',
                style: TextStyle(
                    color: Colors.grey,
                    fontSize: 12,
                    fontWeight: FontWeight.bold)),
          ),
          Expanded(child: Divider(color: Colors.grey[300])),
        ]),
        const SizedBox(height: 16),
        if (_error.isNotEmpty)
          Container(
            width: double.infinity,
            margin: const EdgeInsets.only(bottom: 10),
            padding: const EdgeInsets.all(10),
            decoration: BoxDecoration(
              color: Colors.red[50],
              borderRadius: BorderRadius.circular(8),
              border: Border.all(color: Colors.red[200]!),
            ),
            child:
                Text(_error, style: TextStyle(color: Colors.red[700], fontSize: 13)),
          ),
        TextField(
          controller: _emailCtrl,
          keyboardType: TextInputType.emailAddress,
          textInputAction: TextInputAction.next,
          decoration: InputDecoration(
            labelText: 'Email',
            prefixIcon: const Icon(Icons.email_outlined),
            border:
                OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            filled: true,
            fillColor: Colors.grey[50],
          ),
        ),
        const SizedBox(height: 12),
        TextField(
          controller: _pwCtrl,
          obscureText: !_showPw,
          textInputAction: TextInputAction.done,
          onSubmitted: (_) => _submit(),
          decoration: InputDecoration(
            labelText: 'Password',
            prefixIcon: const Icon(Icons.lock_outline_rounded),
            suffixIcon: IconButton(
              icon: Icon(_showPw ? Icons.visibility_off : Icons.visibility),
              onPressed: () => setState(() => _showPw = !_showPw),
            ),
            border:
                OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            filled: true,
            fillColor: Colors.grey[50],
          ),
        ),
        const SizedBox(height: 20),
        SizedBox(
          width: double.infinity,
          height: 52,
          child: ElevatedButton(
            onPressed: _loading ? null : _submit,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2563EB),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(14)),
              elevation: 0,
            ),
            child: _loading
                ? const SizedBox(
                    width: 22,
                    height: 22,
                    child: CircularProgressIndicator(
                        color: Colors.white, strokeWidth: 2.5))
                : Text(_isRegister ? 'Create Account' : 'Sign In',
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold)),
          ),
        ),
        const SizedBox(height: 10),
      ]),
    );
  }
}
