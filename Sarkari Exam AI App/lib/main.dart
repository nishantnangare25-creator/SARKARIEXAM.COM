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

class _WebViewScreenState extends State<WebViewScreen> {
  late final WebViewController _controller;
  bool _isLoading = true;
  bool _hasError = false;
  final String _currentVersion = "1.0.8";
  final String _homeUrl = 'https://sarkariexamai.com';

  // Firebase Web API key (same as in the website)
  static const String _fbApiKey = 'AIzaSyCWoAYg_1WQPABOS8WzFxoQCcgDY5Rgyzc';

  @override
  void initState() {
    super.initState();
    _initWebView();
    _checkForUpdates();
  }

  // ── WebView Initialization ──────────────────────────────────
  void _initWebView() {
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      // Standard Chrome Mobile UA — accepted by Google OAuth
      ..setUserAgent(
          'Mozilla/5.0 (Linux; Android 13; Pixel 7) AppleWebKit/537.36 '
          '(KHTML, like Gecko) Chrome/120.0.6099.144 Mobile Safari/537.36')
      ..setBackgroundColor(Colors.white)

      // ── JS Channel: PDF Download ──────────────────────────
      ..addJavaScriptChannel(
        'FlutterDownload',
        onMessageReceived: (msg) => _handleDownload(msg.message),
      )

      // ── JS Channel: Trigger native Google/Login sheet ─────
      ..addJavaScriptChannel(
        'FlutterGoogleSignIn',
        onMessageReceived: (_) => _showLoginSheet(),
      )

      ..setNavigationDelegate(NavigationDelegate(
        onPageStarted: (_) => setState(() {
          _isLoading = true;
          _hasError = false;
        }),
        onPageFinished: (_) {
          setState(() => _isLoading = false);
          _injectBridge(); // inject JS bridge after page loads
        },
        onWebResourceError: (e) {
          debugPrint('WebView error: ${e.description}');
          if (e.isForMainFrame == true) setState(() => _hasError = true);
        },
        onNavigationRequest: (req) {
          final url = req.url;
          // Open external links and PDF files properly
          if (url.startsWith('mailto:') || url.startsWith('tel:') || url.toLowerCase().endsWith('.pdf')) {
            launchUrl(Uri.parse(url), mode: LaunchMode.externalApplication);
            return NavigationDecision.prevent;
          }
          // Allow Google OAuth to proceed natively in the WebView
          if (url.contains('accounts.google.com') ||
              url.contains('google.com/o/oauth2')) {
            return NavigationDecision.navigate;
          }
          return NavigationDecision.navigate;
        },
      ))
      ..loadRequest(Uri.parse(_homeUrl));
  }

  // ── Inject JS bridge so web page knows it's inside Flutter ──
  void _injectBridge() {
    _controller.runJavaScript(r'''
      (function () {
        if (window.__flutterBridgeInjected) return;
        window.__flutterBridgeInjected = true;

        // Mark as Flutter WebView
        window.__isFlutterApp = true;

        // Handle Blob URLs (like generated PDFs) to prevent white screens
        function downloadBlob(blobUrl) {
          if (!window.FlutterDownload) return false;
          fetch(blobUrl)
            .then(function(res) { return res.blob(); })
            .then(function(blob) {
               var reader = new FileReader();
               reader.onload = function() {
                  var b64 = reader.result.split(',')[1];
                  window.FlutterDownload.postMessage(JSON.stringify({
                    data: b64,
                    fileName: "Mock_Test_Result.pdf"
                  }));
               };
               reader.readAsDataURL(blob);
            });
          return true;
        }

        // Force window.open to redirect in the same tab or download if it's a blob
        window.open = function(url, target, features) {
          if (url) {
            if (url.startsWith('blob:')) {
               downloadBlob(url);
            } else {
               window.location.href = url;
            }
          }
          return window; // Prevent JS exceptions if site expects a window object back
        };

        // Intercept functions
        function applyFixes() {
          document.querySelectorAll('a').forEach(function(a) {
            // 1. Intercept blob links to prevent white screen navigation
            if (a.href && a.href.startsWith('blob:') && !a.dataset.blobFixed) {
              a.dataset.blobFixed = "1";
              a.addEventListener('click', function(e) {
                e.preventDefault();
                downloadBlob(a.href);
              });
            }
            // 2. Change target="_blank" to "_self" on normal links
            else if (a.target === '_blank') {
              a.target = '_self';
              a.removeAttribute('target');
            }
          });
        }

        applyFixes();
        new MutationObserver(applyFixes)
          .observe(document.body, { childList: true, subtree: true });
      })();
    ''');
  }

  // ── Show native Flutter Login bottom sheet ───────────────────
  void _showLoginSheet() {
    if (!mounted) return;
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (_) => _LoginSheet(
        onSubmit: (email, pw, isRegister) async {
          Navigator.pop(context);
          await _doWebLogin(email, pw, isRegister);
        },
      ),
    );
  }

  // ── Authenticate via Firebase REST API then inject into WebView ─
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

      // Write Firebase auth state into the WebView's IndexedDB
      // so Firebase SDK picks it up without any website code changes.
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
            // Write to IndexedDB — Firebase v9 key format
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
              tx.oncomplete = function() { window.location.href = '/dashboard'; };
            };
          } catch(err) {
            // Fallback: localStorage (Firebase v8 compat)
            localStorage.setItem(
              'firebase:authUser:${_fbApiKey}:[DEFAULT]',
              JSON.stringify($userJson)
            );
            window.location.href = '/dashboard';
          }
        })();
      ''');
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
        return 'This email is already registered. Try logging in.';
      case 'WEAK_PASSWORD : Password should be at least 6 characters':
        return 'Password must be at least 6 characters.';
      case 'INVALID_LOGIN_CREDENTIALS':
        return 'Invalid email or password.';
      default:
        return code.replaceAll('_', ' ');
    }
  }

  // ── PDF / File Download Handler ──────────────────────────────
  Future<void> _handleDownload(String message) async {
    try {
      final decoded = json.decode(message);
      final String base64Data = decoded['data'];
      final String fileName = decoded['fileName'] ?? 'document.pdf';
      final bytes = base64Decode(base64Data);

      File file;

      if (Platform.isAndroid) {
        // Android 10+ (API 29+): scoped storage — no permission needed
        // Android 9-: request legacy storage permission
        final sdkInt = await _androidSdkInt();
        if (sdkInt < 29) {
          final perm = await Permission.storage.request();
          if (!perm.isGranted) {
            _showSnack('Storage permission denied.');
            return;
          }
        }

        // Primary: user's Downloads folder
        final downloadsPath = '/storage/emulated/0/Download';
        final downloadsDir = Directory(downloadsPath);
        if (await downloadsDir.exists()) {
          file = File('$downloadsPath/$fileName');
        } else {
          // Fallback: app external storage
          final extDir = await getExternalStorageDirectory();
          file = File('${extDir!.path}/$fileName');
        }
      } else {
        // iOS: Documents folder
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
    try {
      // Use platform channel to get real SDK int if needed
      // returning 29 is safe — assumes Android 10+ scoped storage
      return 29;
    } catch (_) {
      return 29;
    }
  }

  // ── Update Check ─────────────────────────────────────────────
  Future<void> _checkForUpdates() async {
    try {
      final res = await http
          .get(Uri.parse('$_homeUrl/version.json'))
          .timeout(const Duration(seconds: 5));
      if (res.statusCode == 200) {
        final d = json.decode(res.body);
        final latest = d['version'] ?? _currentVersion;
        if (latest != _currentVersion && mounted) {
          _showUpdateDialog(
              latest, d['download_url'] ?? '', d['release_notes'] ?? '');
        }
      }
    } catch (_) {} // silently ignore — update check is optional
  }

  void _showUpdateDialog(String ver, String url, String notes) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (_) => AlertDialog(
        title: const Text('🚀 Update Available!'),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        content: Column(mainAxisSize: MainAxisSize.min, children: [
          Text('Version $ver is now available.',
              style: const TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Text(notes, style: const TextStyle(fontSize: 13, color: Colors.grey)),
        ]),
        actions: [
          TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('Skip')),
          ElevatedButton(
            style: ElevatedButton.styleFrom(
                backgroundColor: const Color(0xFFF97316),
                foregroundColor: Colors.white),
            onPressed: () =>
                launchUrl(Uri.parse(url), mode: LaunchMode.externalApplication),
            child: const Text('Download'),
          ),
        ],
      ),
    );
  }

  // ── Snackbar helper ──────────────────────────────────────────
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

  // ── Back button ──────────────────────────────────────────────
  Future<bool> _handleBackPress() async {
    if (await _controller.canGoBack()) {
      await _controller.goBack();
      return false;
    }
    return true;
  }

  // ── Build ─────────────────────────────────────────────────────
  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _handleBackPress,
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Stack(children: [
            // ── Error state with retry button ─────────────────
            if (_hasError)
              _ErrorView(onRetry: () {
                setState(() => _hasError = false);
                _controller.loadRequest(Uri.parse(_homeUrl));
              })
            else
              WebViewWidget(controller: _controller),

            // ── Loading spinner ───────────────────────────────
            if (_isLoading && !_hasError)
              const Center(
                child: CircularProgressIndicator(
                  strokeWidth: 4,
                  color: Color(0xFFF97316),
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
              padding:
                  const EdgeInsets.symmetric(horizontal: 32, vertical: 14),
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
// (replaces the broken Google OAuth WebView flow)
// ─────────────────────────────────────────────────────────────
class _LoginSheet extends StatefulWidget {
  final Future<void> Function(String email, String pw, bool isRegister)
      onSubmit;
  const _LoginSheet({required this.onSubmit});

  @override
  State<_LoginSheet> createState() => _LoginSheetState();
}

class _LoginSheetState extends State<_LoginSheet> {
  final _emailCtrl = TextEditingController();
  final _pwCtrl = TextEditingController();
  bool _showPw = false;
  bool _loading = false;
  bool _isRegister = false;
  String _error = '';

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
        // Drag handle
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

        // Header
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
            Text(
              _isRegister ? 'Create Account' : 'Login',
              style: const TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
            ),
            const Text('Sarkari Exam AI',
                style: TextStyle(color: Colors.grey, fontSize: 12)),
          ]),
          const Spacer(),
          // Toggle login/register
          TextButton(
            onPressed: () => setState(() {
              _isRegister = !_isRegister;
              _error = '';
            }),
            child: Text(
              _isRegister ? 'Login' : 'Sign up',
              style: const TextStyle(
                  color: Color(0xFF2563EB), fontWeight: FontWeight.w600),
            ),
          ),
        ]),
        const SizedBox(height: 12),

        // Note about Google Sign-In
        Container(
          padding: const EdgeInsets.all(10),
          decoration: BoxDecoration(
            color: const Color(0xFFFFF3CD),
            borderRadius: BorderRadius.circular(8),
            border: Border.all(color: const Color(0xFFFFD700).withOpacity(0.6)),
          ),
          child: const Row(children: [
            Icon(Icons.info_outline_rounded, size: 15, color: Color(0xFF856404)),
            SizedBox(width: 8),
            Expanded(
              child: Text(
                'Use Email + Password for in-app login. Google Sign-In works on the website.',
                style: TextStyle(fontSize: 11, color: Color(0xFF856404)),
              ),
            ),
          ]),
        ),
        const SizedBox(height: 14),

        // Error
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
            child: Text(_error,
                style: TextStyle(color: Colors.red[700], fontSize: 13)),
          ),

        // Email
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

        // Password
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

        // Submit button
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
                : Text(
                    _isRegister ? 'Create Account' : 'Sign In',
                    style: const TextStyle(
                        fontSize: 16, fontWeight: FontWeight.bold),
                  ),
          ),
        ),
        const SizedBox(height: 10),

        // Cancel
        SizedBox(
          width: double.infinity,
          child: TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('Cancel',
                style: TextStyle(color: Colors.grey, fontSize: 14)),
          ),
        ),
      ]),
    );
  }
}
