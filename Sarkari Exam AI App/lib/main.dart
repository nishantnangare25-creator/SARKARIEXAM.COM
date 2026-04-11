import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_filex/open_filex.dart';
import 'package:permission_handler/permission_handler.dart';

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
// WebView Screen
// ─────────────────────────────────────────────────────────────
class WebViewScreen extends StatefulWidget {
  const WebViewScreen({super.key});

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

// ── KEY FIX: WidgetsBindingObserver detects app background/foreground ──
class _WebViewScreenState extends State<WebViewScreen>
    with WidgetsBindingObserver {
  late final WebViewController _controller;

  bool _isLoading = true;
  bool _hasError = false;
  bool _bridgeInjected = false;
  String _currentUrl = '';

  // Track last known good URL so we can restore it after background kill
  String _lastGoodUrl = 'https://sarkariexamai.com';

  // Track app lifecycle
  AppLifecycleState _lastLifecycleState = AppLifecycleState.resumed;

  static const String _homeUrl = 'https://sarkariexamai.com';
  static const String _fbApiKey = 'AIzaSyCWoAYg_1WQPABOS8WzFxoQCcgDY5Rgyzc';

  @override
  void initState() {
    super.initState();
    // Register to receive lifecycle events
    WidgetsBinding.instance.addObserver(this);
    _initWebView();
  }

  @override
  void dispose() {
    // Always unregister to prevent memory leaks
    WidgetsBinding.instance.removeObserver(this);
    super.dispose();
  }

  // ── KEY FIX: App lifecycle handler ──────────────────────────
  // This fires when user switches apps, locks screen, or resumes.
  @override
  void didChangeAppLifecycleState(AppLifecycleState state) {
    debugPrint('[Lifecycle] App state changed: $state');

    if (state == AppLifecycleState.resumed &&
        _lastLifecycleState != AppLifecycleState.resumed) {
      // App came back from background — check if WebView is still alive
      _onAppResumed();
    }

    _lastLifecycleState = state;
  }

  // ── Check WebView health on app resume ──────────────────────
  // After 10-15 mins in background, Android kills the WebView renderer.
  // The WebView then shows blank. We detect this and reload.
  Future<void> _onAppResumed() async {
    debugPrint('[Resume] Checking WebView health...');

    // Give the WebView a moment to restore itself naturally
    await Future.delayed(const Duration(milliseconds: 800));

    if (!mounted) return;

    try {
      // Run a simple JS health check — if WebView is blank/dead,
      // document.body will be null or document.title will be empty
      final result = await _controller.runJavaScriptReturningResult(
        'document.body ? (document.body.children.length > 0 ? "ok" : "empty") : "dead"',
      );

      final health = result.toString().replaceAll('"', '').trim();
      debugPrint('[Resume] WebView health: $health');

      if (health == 'empty' || health == 'dead' || health == 'null') {
        debugPrint('[Resume] WebView is blank — reloading last known URL: $_lastGoodUrl');
        _reloadWebView(_lastGoodUrl);
      } else {
        // WebView is alive — just re-inject bridge in case it was lost
        _bridgeInjected = false;
        _injectBridge();
        debugPrint('[Resume] WebView alive — bridge re-injected');
      }
    } catch (e) {
      // If JS evaluation itself fails, WebView renderer is dead
      debugPrint('[Resume] JS eval failed ($e) — reloading WebView');
      _reloadWebView(_lastGoodUrl);
    }
  }

  // ── Safe reload method ────────────────────────────────────────
  void _reloadWebView(String url) {
    if (!mounted) return;
    setState(() {
      _isLoading = true;
      _hasError = false;
      _bridgeInjected = false;
    });
    _controller.loadRequest(Uri.parse(url));
  }

  // ── WebView Initialization ──────────────────────────────────
  void _initWebView() {
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setUserAgent(
          'Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 '
          '(KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36')
      ..setBackgroundColor(Colors.white)

      // ── JS Channel: PDF Download ──────────────────────────
      ..addJavaScriptChannel(
        'FlutterDownload',
        onMessageReceived: (msg) => _handleDownload(msg.message),
      )

      // ── JS Channel: Native Login Sheet ────────────────────
      ..addJavaScriptChannel(
        'FlutterGoogleSignIn',
        onMessageReceived: (msg) {
          bool isReg = msg.message == 'signup';
          _showLoginSheet(isRegister: isReg);
        },
      )

      // ── JS Channel: Page ready signal ─────────────────────
      ..addJavaScriptChannel(
        'FlutterPageReady',
        onMessageReceived: (_) {
          if (mounted && _isLoading) {
            setState(() => _isLoading = false);
          }
        },
      )

      ..setNavigationDelegate(NavigationDelegate(
        onPageStarted: (url) {
          final isRealNavigation = _isRealPageChange(url);
          if (isRealNavigation) {
            _bridgeInjected = false;
            if (mounted) {
              setState(() {
                _isLoading = true;
                _hasError = false;
              });
            }
          }
          _currentUrl = url;

          // FIX: Always track the last known REAL URL (not blob/data/about)
          if (url.startsWith(_homeUrl)) {
            _lastGoodUrl = url;
          }
        },

        onPageFinished: (url) {
          _injectBridge();

          if (mounted && _isLoading) {
            // Give React 600ms to hydrate before hiding spinner
            Future.delayed(const Duration(milliseconds: 600), () {
              if (mounted && _isLoading) {
                setState(() => _isLoading = false);
              }
            });
          }

          // Update last good URL on successful finish
          if (url.startsWith(_homeUrl)) {
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

          // Allow internal site (including /#/hash routes)
          if (url.startsWith(_homeUrl)) return NavigationDecision.navigate;

          // Allow React-internal schemes
          if (url.startsWith('about:') ||
              url.startsWith('blob:') ||
              url.startsWith('data:')) {
            return NavigationDecision.navigate;
          }

          // Allow Google OAuth & Firebase redirects
          if (url.contains('accounts.google.com') ||
              url.contains('google.com/o/oauth2') ||
              url.contains('firebaseapp.com') ||
              url.contains('googleapis.com')) {
            return NavigationDecision.navigate;
          }

          // Block everything else, open externally
          _launchExternal(url);
          return NavigationDecision.prevent;
        },
      ))
      ..loadRequest(Uri.parse(_homeUrl));
  }

  /// Returns true only for genuine cross-origin/path navigations,
  /// NOT for SPA hash changes (#/dashboard → #/mock-test)
  bool _isRealPageChange(String newUrl) {
    if (_currentUrl.isEmpty) return true;
    try {
      final current = Uri.parse(_currentUrl);
      final next = Uri.parse(newUrl);
      return current.host != next.host || current.path != next.path;
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

  // ── Inject JS bridge ──────────────────────────────────────────
  void _injectBridge() {
    if (_bridgeInjected) return;
    _bridgeInjected = true;

    _controller.runJavaScript(r'''
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

        // ── window.open fix — THE main cause of blank 2nd page ──
        window.open = function(url, target, features) {
          if (url && typeof url === 'string') {
            if (url.startsWith('blob:')) {
              downloadBlob(url);
            } else {
              window.location.href = url;
            }
          }
          return {
            closed: false,
            close: function() {},
            focus: function() {},
            document: { write: function() {}, close: function() {} }
          };
        };

        // ── Fix <a> tags ──────────────────────────────────
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
              } else if (a.target === '_blank') {
                a.target = '_self';
                a.removeAttribute('rel');
              }
            });
          } catch(err) {
            console.error('Flutter bridge link fix error:', err);
          }
        }

        applyLinkFixes();

        // Watch for dynamically added React elements
        new MutationObserver(applyLinkFixes)
          .observe(document.body, { childList: true, subtree: true });

        // ── Signal Flutter that page is ready ────────────
        if (window.FlutterPageReady) {
          window.FlutterPageReady.postMessage('ready');
        }

        // ── Visibility change: re-signal on tab/app restore ──
        // This fires when user switches back to the app
        document.addEventListener('visibilitychange', function() {
          if (!document.hidden && window.FlutterPageReady) {
            setTimeout(function() {
              window.FlutterPageReady.postMessage('visible');
            }, 500);
          }
        });

        console.log('[FlutterBridge] v2 injected successfully');
      })();
    ''');
  }

  // ── Show native Login bottom sheet ───────────────────────────
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
          _controller.loadRequest(Uri.parse('$_homeUrl/#/login'));
          Future.delayed(const Duration(milliseconds: 1500), () {
            if (mounted) {
              _controller.runJavaScript(
                  "document.querySelector('.google-btn')?.click();");
            }
          });
        },
        onSubmit: (email, pw, isReg) async {
          Navigator.pop(context);
          await _doWebLogin(email, pw, isReg);
        },
      ),
    );
  }

  // ── Firebase REST login → inject auth into WebView ────────────
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
        final msg = data['error']['message'] ?? 'Login failed';
        _showSnack(_friendlyError(msg));
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
      case 'EMAIL_NOT_FOUND': return 'No account found with this email.';
      case 'INVALID_PASSWORD': return 'Wrong password. Please try again.';
      case 'EMAIL_EXISTS': return 'Email is already registered. Try logging in.';
      case 'WEAK_PASSWORD : Password should be at least 6 characters':
        return 'Password must be at least 6 characters.';
      case 'INVALID_LOGIN_CREDENTIALS': return 'Invalid email or password.';
      default: return code.replaceAll('_', ' ');
    }
  }

  // ── PDF Download Handler ──────────────────────────────────────
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

  // ── Snackbar ──────────────────────────────────────────────────
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

  // ── Build ─────────────────────────────────────────────────────
  @override
  Widget build(BuildContext context) {
    return PopScope(
      canPop: false,
      onPopInvokedWithResult: (didPop, result) async {
        if (didPop) return;
        final canGoBack = await _controller.canGoBack();
        if (canGoBack) {
          await _controller.goBack();
        } else {
          if (context.mounted) Navigator.of(context).pop();
        }
      },
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Stack(children: [
            // ── Error state ─────────────────────────────────
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

            // ── Loading overlay ──────────────────────────────
            if (_isLoading && !_hasError)
              Container(
                color: Colors.white,
                child: const Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      CircularProgressIndicator(
                        strokeWidth: 4,
                        color: Color(0xFFF97316),
                      ),
                      SizedBox(height: 20),
                      Text(
                        'Loading Sarkari Exam AI…',
                        style: TextStyle(
                          color: Color(0xFF64748B),
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                    ],
                  ),
                ),
              ),
          ]),
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
              shape: RoundedRectangleBorder(
                  borderRadius: BorderRadius.circular(14)),
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
    setState(() { _loading = true; _error = ''; });
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
        left: 24, right: 24, top: 20,
        bottom: MediaQuery.of(context).viewInsets.bottom + 24,
      ),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(24)),
      ),
      child: Column(mainAxisSize: MainAxisSize.min, children: [
        Center(
          child: Container(
            width: 40, height: 4,
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
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold)),
            const Text('Sarkari Exam AI',
                style: TextStyle(color: Colors.grey, fontSize: 12)),
          ]),
          const Spacer(),
          TextButton(
            onPressed: () => setState(() { _isRegister = !_isRegister; _error = ''; }),
            child: Text(_isRegister ? 'Login' : 'Sign up',
              style: const TextStyle(color: Color(0xFF2563EB), fontWeight: FontWeight.w600)),
          ),
        ]),
        const SizedBox(height: 12),
        SizedBox(
          width: double.infinity, height: 50,
          child: OutlinedButton.icon(
            onPressed: widget.onGoogleLogin,
            icon: const Icon(Icons.g_mobiledata_rounded, color: Colors.black, size: 36),
            label: const Text('Sign in with Google',
              style: TextStyle(color: Colors.black, fontSize: 15, fontWeight: FontWeight.w600)),
            style: OutlinedButton.styleFrom(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
              side: BorderSide(color: Colors.grey[300]!, width: 1.5),
            ),
          ),
        ),
        const SizedBox(height: 16),
        Row(children: [
          Expanded(child: Divider(color: Colors.grey[300])),
          const Padding(
            padding: EdgeInsets.symmetric(horizontal: 10),
            child: Text('OR', style: TextStyle(color: Colors.grey, fontSize: 12, fontWeight: FontWeight.bold)),
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
            child: Text(_error, style: TextStyle(color: Colors.red[700], fontSize: 13)),
          ),
        TextField(
          controller: _emailCtrl,
          keyboardType: TextInputType.emailAddress,
          textInputAction: TextInputAction.next,
          decoration: InputDecoration(
            labelText: 'Email',
            prefixIcon: const Icon(Icons.email_outlined),
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            filled: true, fillColor: Colors.grey[50],
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
            border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
            filled: true, fillColor: Colors.grey[50],
          ),
        ),
        const SizedBox(height: 20),
        SizedBox(
          width: double.infinity, height: 52,
          child: ElevatedButton(
            onPressed: _loading ? null : _submit,
            style: ElevatedButton.styleFrom(
              backgroundColor: const Color(0xFF2563EB),
              foregroundColor: Colors.white,
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
              elevation: 0,
            ),
            child: _loading
                ? const SizedBox(width: 22, height: 22,
                    child: CircularProgressIndicator(color: Colors.white, strokeWidth: 2.5))
                : Text(_isRegister ? 'Create Account' : 'Sign In',
                    style: const TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
          ),
        ),
        const SizedBox(height: 10),
      ]),
    );
  }
}
