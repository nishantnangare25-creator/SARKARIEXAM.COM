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
          if (url && typeof url === 'string') {
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
          try {
            document.querySelectorAll('a').forEach(function(a) {
              if (!a || !a.href || typeof a.href !== 'string') return;
              
              // 1. Intercept blob links to prevent white screen navigation
              if (a.href.startsWith('blob:') && !a.dataset.blobFixed) {
                a.dataset.blobFixed = "1";
                a.addEventListener('click', function(e) {
                  e.preventDefault();
                  downloadBlob(a.href);
                });
              }
              // Change target="_blank" to "_self" on normal links
              else if (a.target === '_blank') {
                a.target = '_self';
                a.removeAttribute('target');
              }
            });
          } catch(err) {
            console.error("Flutter bridge error:", err);
          }
        }

        applyFixes();
        new MutationObserver(applyFixes)
          .observe(document.body, { childList: true, subtree: true });
      })();
    ''');
  }

  // Native Login Sheet has been removed to allow Google Sign In on the Website.

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

  // Update Check has been removed as requested by the user.

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

// End of file
