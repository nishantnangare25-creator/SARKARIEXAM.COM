// Importing the required package for WebView
import 'package:webview_flutter/webview_flutter.dart';

class _YourClassNameState extends State<YourClassName> {
  @override
  void initState() {
    super.initState();
    // Set the SurfaceAndroidWebView.
    SurfaceAndroidWebView.setWebContentsDebuggingEnabled(true);
    _initWebView();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      body: WebView(
        initialUrl: 'https://your-url.com',
        onWebViewCreated: (WebViewController webViewController) {
          _controller = webViewController;
        },
      ),
    );
  }
}