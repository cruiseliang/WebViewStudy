package yuan.com.webviewstudy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * webview和原生交互四个步骤
 * 1. ws.setJavaScriptEnabled(true);//设置允许执行js脚本
 * 2.添加通信接口：  webView.addJavascriptInterface(new ImageClickInterface(this), "injectedObject");//参1是回调接口的实现;参2是js回调对象的名称
 * 3.js调取安卓:InterfaceName.MethodName
 * 4.安卓调取js:webview.loadUrl("javascript:functionName()")
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.bt_baidu)
    Button btBaidu;
    @BindView(R.id.bt_java_js)
    Button btJavaJs;
    private WebView mWebview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        btBaidu.setOnClickListener(this);
        btJavaJs.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_baidu:
                String baiDuUrl = "http://www.baidu.com";
                WebViewActivity.loadUrl(this, baiDuUrl, false);
                break;
            case R.id.bt_java_js:
                String javaJs = "file:///android_asset/java_js.html";
                WebViewActivity.loadUrl(this, javaJs, false);
                break;

        }
    }


}

