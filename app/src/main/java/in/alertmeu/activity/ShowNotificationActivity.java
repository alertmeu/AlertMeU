package in.alertmeu.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import in.alertmeu.R;

public class ShowNotificationActivity extends AppCompatActivity {
    WebView webView;
    String url = "";
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_notification);
        Intent intent = getIntent();
        Uri uri = Uri.parse(intent.getStringExtra("url")); // missing 'http://' will cause crashed
        Intent intent1 = new Intent(Intent.ACTION_VIEW, uri);
        startActivity(intent1);
       // finish();
       /* webView = (WebView) findViewById(R.id.webView1);
        progressDialog = ProgressDialog.show(ShowNotificationActivity.this, "", "Loading...", true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setAppCachePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/cache");
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDatabasePath(getApplicationContext().getFilesDir().getAbsolutePath() + "/databases");
        webView.loadUrl(intent.getStringExtra("url")); // Here You can put your Url
        webView.setWebChromeClient(new WebChromeClient() {
        });

        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
                //Toast.makeText(context, "Page Load Finished", Toast.LENGTH_SHORT).show();
            }
        });
*/
    }

    @Override
    public void onBackPressed() {
        // TODO Auto-generated method stub
        super.onBackPressed();
        Intent setIntent = new Intent(ShowNotificationActivity.this, HomePageActivity.class);
        startActivity(setIntent);
        finish();
    }
}