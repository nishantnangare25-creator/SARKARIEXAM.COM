import 'dart:io';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:webview_flutter/webview_flutter.dart';
import 'package:http/http.dart' as http;
import 'package:url_launcher/url_launcher.dart';
import 'package:path_provider/path_provider.dart';
import 'package:open_file_plus/open_file_plus.dart';
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

class WebViewScreen extends StatefulWidget {
  const WebViewScreen({super.key});

  @override
  State<WebViewScreen> createState() => _WebViewScreenState();
}

class _WebViewScreenState extends State<WebViewScreen> {
  late final WebViewController _controller;
  bool _isLoading = true;
  final String _currentVersion = "1.0.4"; // Standard version comparison

  @override
  void initState() {
    super.initState();
    
    _initializeWebView();
    _checkForUpdates();
  }

  void _initializeWebView() {
    _controller = WebViewController()
      ..setJavaScriptMode(JavaScriptMode.unrestricted)
      ..setUserAgent("Mozilla/5.0 (Linux; Android 10) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/119.0.6045.163 Mobile Safari/537.36")
      ..setBackgroundColor(Colors.white)
      ..addJavaScriptChannel(
        'FlutterDownload',
        onMessageReceived: (JavaScriptMessage message) {
          _handleDownload(message.message);
        },
      )
      ..setNavigationDelegate(
        NavigationDelegate(
          onPageStarted: (String url) {
            setState(() => _isLoading = true);
          },
          onPageFinished: (String url) {
            setState(() => _isLoading = false);
          },
          onWebResourceError: (WebResourceError error) {
            debugPrint("WebView Error: ${error.description}");
          },
          onNavigationRequest: (NavigationRequest request) {
            if (request.url.startsWith("mailto:") || request.url.startsWith("tel:")) {
              launchUrl(Uri.parse(request.url), mode: LaunchMode.externalApplication);
              return NavigationDecision.prevent;
            }
            return NavigationDecision.navigate;
          },
        ),
      )
      ..loadRequest(Uri.parse('https://sarkariexamai.com'));
  }

  Future<void> _handleDownload(String message) async {
    try {
      final decoded = json.decode(message);
      final String base64Data = decoded['data'];
      final String fileName = decoded['fileName'];

      // Request storage permission
      if (Platform.isAndroid) {
        await Permission.storage.request();
      }

      final bytes = base64Decode(base64Data);
      final directory = Platform.isAndroid 
          ? await getTemporaryDirectory() 
          : await getApplicationDocumentsDirectory();

      final file = File('${directory.path}/$fileName');
      await file.writeAsBytes(bytes);

      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(
            content: Text("Download Complete: $fileName"),
            action: SnackBarAction(
              label: "OPEN",
              onPressed: () => OpenFile.open(file.path),
            ),
          ),
        );
        // Automatically try to open
        await OpenFile.open(file.path);
      }
    } catch (e) {
      debugPrint("Download failed: $e");
    }
  }

  Future<void> _checkForUpdates() async {
    try {
      final response = await http.get(Uri.parse('https://sarkariexamai.com/version.json'));
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final String latestVersion = data['version'];
        
        if (latestVersion != _currentVersion && mounted) {
           _showUpdateDialog(latestVersion, data['download_url'], data['release_notes']);
        }
      }
    } catch (e) {
      debugPrint("Update check failed: $e");
    }
  }

  void _showUpdateDialog(String version, String url, String notes) {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => AlertDialog(
        title: const Text("🚀 New Update Available!"),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
             Text("Version $version is now available.", style: const TextStyle(fontWeight: FontWeight.bold)),
             const SizedBox(height: 8),
             Text(notes, style: const TextStyle(fontSize: 0.9, color: Colors.grey)),
          ],
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text("Skip")),
          ElevatedButton(
            style: ElevatedButton.styleFrom(backgroundColor: const Color(0xFFF97316), foregroundColor: Colors.white),
            onPressed: () => launchUrl(Uri.parse(url), mode: LaunchMode.externalApplication), 
            child: const Text("Download Update")
          ),
        ],
      ),
    );
  }

  Future<bool> _handleBackPress() async {
    if (await _controller.canGoBack()) {
      await _controller.goBack();
      return false;
    }
    return true;
  }

  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: _handleBackPress,
      child: Scaffold(
        backgroundColor: Colors.white,
        body: SafeArea(
          child: Stack(
            children: [
              WebViewWidget(controller: _controller),
              if (_isLoading)
                const Center(
                  child: CircularProgressIndicator(
                    strokeWidth: 5,
                    color: Color(0xFFF97316), // Saffron Color
                  ),
                ),
            ],
          ),
        ),
      ),
    );
  }
}

}
