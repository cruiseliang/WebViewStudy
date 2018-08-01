package yuan.com.webviewstudy;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liang on 2018/7/31 0031.F
 */

public class WebViewActivity extends Activity {
    // 进度条
    @BindView(R.id.pb_progress)
    ProgressBar mProgressBar;
    @BindView(R.id.webview_detail)
    WebView webView;
    // 网页链接
    private String mUrl;
    // 是否是全屏视频链接
    private boolean mIsMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData();
        setContentView(R.layout.activity_web_view);
        ButterKnife.bind(this);
        setTitle("详情");
        initWebView();
        webView.loadUrl(mUrl);
    }

    private void getIntentData() {
        if (getIntent() != null) {
            mIsMovie = getIntent().getBooleanExtra("mIsMovie", false);
            mUrl = getIntent().getStringExtra("mUrl");
        }
    }

    @SuppressLint({"SetJavaScriptEnabled", "AddJavascriptInterface"})
    private void initWebView() {
        mProgressBar.setVisibility(View.VISIBLE);
        WebSettings ws = webView.getSettings();
        // 网页内容的宽度是否可大于WebView控件的宽度
        ws.setLoadWithOverviewMode(false);
        // 保存表单数据
        ws.setSaveFormData(true);
        // 是否应该支持使用其屏幕缩放控件和手势缩放
        ws.setSupportZoom(true);
        ws.setBuiltInZoomControls(true);
        ws.setDisplayZoomControls(false);
        // 启动应用缓存
        ws.setAppCacheEnabled(true);
        // 设置缓存模式
        ws.setCacheMode(WebSettings.LOAD_DEFAULT);
        // setDefaultZoom  api19被弃用
        // 设置此属性，可任意比例缩放。
        ws.setUseWideViewPort(true);

        // 不缩放
        webView.setInitialScale(100);
        // 告诉WebView启用JavaScript执行。默认的是false。
        ws.setJavaScriptEnabled(true);
        //  页面加载好以后，再放开图片
        ws.setBlockNetworkImage(false);
        // 使用localStorage则必须打开
        ws.setDomStorageEnabled(true);
        // 排版适应屏幕
        ws.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        // WebView是否新窗口打开(加了后可能打不开网页)
//        ws.setSupportMultipleWindows(true);

        // webview从5.0开始默认不允许混合模式,https中不能加载http资源,需要设置开启。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ws.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        /** 设置字体默认缩放大小(改变网页字体大小,setTextSize  api14被弃用)*/
        ws.setTextZoom(100);
        // 与js交互
        webView.addJavascriptInterface(new ImageClickInterface(this), "injectedObject");//参1是回调接口的实现;参2是js回调对象的名称
        webView.setWebViewClient(new WebViewClient() {
            //打开网页时不调用系统浏览器， 而是在本WebView中显示；在网页上的所有加载都经过这个方法,这个函数我们可以做很多操作。
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //开始载入页面调用的，我们可以设定一个loading的页面，告诉用户程序在等待网络响应。
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);

            }

            //设定加载结束的操作
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //直接使用webview加载js就可以了
                webView.loadUrl("javascript:wave()");
                addImageClickListener();
            }

            //在加载页面资源时会调用，每一个资源（比如图片）的加载都会调用一次。
            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }

            //加载页面的服务器出现错误时（如404）调用
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                //判断错误码，显示对应的界面
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress < 100) {
                    startProgress(newProgress);
                } else {
                    hideProgressBar();
                }
            }

            //获取Web页中的标题
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
            }
        });
    }


    public void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
    }


    public void showWebView() {
        webView.setVisibility(View.VISIBLE);
    }


    public void hideWebView() {
        webView.setVisibility(View.INVISIBLE);
    }


    public void startProgress(int newProgress) {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setProgress(newProgress);
        if (newProgress == 100) {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    /**
     * android与js交互：
     * 前端嵌入js代码：不能加重复的节点，不然会覆盖
     */
//    @Override
    public void addImageClickListener() {
        // 显示弹窗
        webView.loadUrl("javascript:wave()");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
                return true;

                //退出网页
            } else {
                finish();
            }
        }
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            ViewGroup parent = (ViewGroup) webView.getParent();
            if (parent != null) {
                parent.removeView(webView);
            }
            webView.removeAllViews();
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.setWebViewClient(null);
            webView.destroy();
            webView = null;
        }
    }

    /**
     * 打开网页:
     *
     * @param mContext 上下文
     * @param mUrl     要加载的网页url
     * @param mIsMovie 是否是视频链接(视频链接布局不一致)
     */
    public static void loadUrl(Context mContext, String mUrl, boolean mIsMovie) {


        //方式1. 加载一个网页：
//        mWebview.loadUrl("http://www.csdn.net/");

        //方式2：加载apk包中的html页面
//        mWebview.loadUrl("file:///android_asset/test.html");
//
//        //方式3：加载手机本地的html页面
//        mWebview.loadUrl("content://com.android.htmlfileprovider/sdcard/test.html");
        Intent intent = new Intent(mContext, WebViewActivity.class);
        intent.putExtra("mUrl", mUrl);
        intent.putExtra("mIsMovie", mIsMovie);
        mContext.startActivity(intent);
    }
}
