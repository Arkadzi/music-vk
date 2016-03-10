package me.gumenniy.arkadiy.vkmusic.app;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.inject.Inject;

import me.gumenniy.arkadiy.vkmusic.R;
import me.gumenniy.arkadiy.vkmusic.rest.UserSession;
import me.gumenniy.arkadiy.vkmusic.utils.Settings;

public class LoginActivity extends AppCompatActivity {
    private ProgressBar progress;
    @Inject UserSession user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        MusicApplication.getApp(this).getComponent().inject(this);

        progress = (ProgressBar) findViewById(R.id.progress);
        WebView webview = (WebView) findViewById(R.id.web);
        webview.getSettings().setJavaScriptEnabled(true);
        webview.setVerticalScrollBarEnabled(false);
        webview.setHorizontalScrollBarEnabled(false);
        webview.clearCache(true);
        webview.setWebViewClient(new VkWebViewClient());

        String url = null;
        try {
            url = String.format("https://oauth.vk.com/authorize?client_id=%s&scope=audio,friends&redirect_uri=%s&response_type=token&display=mobile",
                    Settings.CLIENT_ID,
                    URLEncoder.encode(Settings.REDIRECT_URI, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        webview.loadUrl(url);
        webview.setVisibility(View.VISIBLE);

    }

    private void parseUrl(String url) {
        try {
            if (url == null) {
                return;
            }
            Log.e("URL", url);
            if (url.startsWith(Settings.REDIRECT_URI)) {
                if (!url.contains("error")) {
                    String token = extractValue(url, "access_token");
                    String userId = extractValue(url, "user_id");
//                    Log.e("User", String.valueOf(user));

                    saveUser(token, userId);
                    finishSelf();
                } else {
                    finishSelf();
                }
            }
        } catch (Exception e) {
            Log.e("LoginActivity", e.getMessage());
            finishSelf();
        }
    }

    private void saveUser(String token, String userId) {
        user.update(userId, token);
    }

    private void finishSelf() {
        finish();
    }

    private String extractValue(String url, String key) {
        String[] params = url.split("#")[1].split("&");
        for (String param : params) {
            if (param.startsWith(key)) {
                return param.substring(param.indexOf("=") + 1);
            }
        }
        return null;
    }

    class VkWebViewClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progress.setVisibility(View.VISIBLE);
            parseUrl(url);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (url.startsWith("https://oauth.vk.com/authorize") || url.startsWith("https://oauth.vk.com/oauth/authorize")) {
                progress.setVisibility(View.GONE);
            }
        }
    }
}
