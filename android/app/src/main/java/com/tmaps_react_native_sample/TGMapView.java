package com.tmaps_react_native_sample;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.webkit.JavascriptInterface;
import android.webkit.PermissionRequest;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.facebook.react.bridge.ActivityEventListener;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.uimanager.events.RCTEventEmitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import im.delight.android.webview.AdvancedWebView;

public class TGMapView  extends AdvancedWebView implements AdvancedWebView.Listener, ActivityEventListener {
    private ReactApplicationContext context;
    private String mapId;
    private ArrayList tenants;
    private String featureId;
    private String routeTo;
    private String primaryColor;
    private String secandaryColor;


    public TGMapView(ReactApplicationContext context) {
        super(context);
        this.context = context;
        this.context.addActivityEventListener(this);
    }


    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        showTagipediaMap();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    protected void showTagipediaMap() {
        this.getSettings().setJavaScriptEnabled(true);
        this.getSettings().setDomStorageEnabled(true);
        this.getSettings().setAllowFileAccessFromFileURLs(true);
        this.getSettings().setAllowUniversalAccessFromFileURLs(true);
        this.setWebViewClient(new WebViewClient(){
            @Override
            public void onLoadResource(WebView view, String url) {
                setTagipediaObjectAndLoadMap();
                super.onLoadResource(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
            }

        });
        this.addJavascriptInterface(new JavascriptInterface(){
            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }

            @JavascriptInterface
            public void dispatch(String message) {
                try {
                    TGMapView.this.onMessageReceived(Utils.jsonToMap(message));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @JavascriptInterface
            public void reload() {
                TGMapView.this.post(new Runnable() { @Override public void run() { TGMapView.this.reload(); } });
            }
        },"__tmaps_bridge__");
        this.reload();
        this.loadUrl("file:///android_asset/tmapswww/index.html");
        this.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onPermissionRequest(final PermissionRequest request) {
                TGMapView.this.post(new Runnable() {
                    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void run() {

                        request.grant(request.getResources());

                    }
                });
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                WritableMap event = Arguments.createMap();
                event.putDouble("progress", (float) newProgress / 100);
                ReactContext reactContext = (ReactContext)view.getContext();
                reactContext.getJSModule(RCTEventEmitter.class).receiveEvent(
                        view.getId(),
                        "topLoadingProgress",
                        event);
            }
        });
    }


    public void dispatchMessage(final HashMap<String, Object> hashMap) {
        final JSONObject jsonObject = new JSONObject(hashMap);
        this.post(new Runnable() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public void run() {
                TGMapView.this.evaluateJavascript(String.format("Tagipedia.dispatch(%s);",jsonObject),null);
            }
        });
    }
    private void setTagipediaObjectAndLoadMap() {
        String tbString =
                "window.__tb__ = {dispatch: function(action){__tmaps_bridge__.dispatch(JSON.stringify(action));}}; window.__reload__ = function(){__tmaps_bridge__.reload();};";
        injectScript(tbString);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void injectScript(String tbString) {
        this.evaluateJavascript(tbString,null);
    }


    public void onMessageReceived(final Map<String, Object> message) {
        System.out.println("map message  " + message);
        if (message.get("type").equals("READY")){
            dispatchMessage(new LinkedHashMap<String, Object>() {
                {
                    put("type", "SET_TENANT_DATA");
                    put("payload", tenants);
                }
            });
//
//            dispatchMessage(new LinkedHashMap<String, Object>() {
//                {
//                    put("type", "SET_DEFAULT_FEATURE_POPUP_TEMPLATE");
//                    put("template", MainActivity.this.getCustomTemplate());
//                }
//            });

//            dispatchMessage(new LinkedHashMap<String, Object>() {
//                {
//                    put("type", "SET_APPLICATION_SECRETS");
//                    put("client_id", getResources().getString(R.string.client_id));
//                    put("client_secret", getResources().getString(R.string.client_secret));
//                }
//            });
//            dispatchMessage(new LinkedHashMap<String, Object>() {
//                {
//                    put("type", "SET_DEVICE_DATA");
//                    put("device_id", Settings.Secure.getString(context.getContentResolver(),Settings.Secure.ANDROID_ID).toString());
//                    put("device_type", "ANDROID");
//                }
//            });
            dispatchMessage(new LinkedHashMap<String, Object>() {
                {
                    put("type", "LOAD_MAP");
                    put("map_id", mapId);
                    put("theme", new HashMap<String, Object>(){{
                        put("primary", primaryColor);
                        put("accent",  secandaryColor);
                    }});
                }
            });
        } else if (message.get("type").equals("MAP_LOADED")){
            WritableMap params = Arguments.createMap();

            final Handler handler = new android.os.Handler();
            Runnable runnable = new Runnable() {
                public void run() {
                    dispatchMessage(new LinkedHashMap<String, Object>() {
                        {
                            put("type", "SET_ZOOM");
                            put("zoom", 18);
                            put("zoom_type", "FLY_TO");
                            if (featureId != null && !featureId.isEmpty()) {
                                put("centroid_feature_id", featureId);
                            }

                        }
                    });
                    handler.removeCallbacks(this);
                }
            };
            handler.postDelayed(runnable, 500);
        } else if(message.get("type").equals("FEATURES_TAPPED")){
            dispatchMessage(new LinkedHashMap<String, Object>() {
                {
                    put("type", "HIGHLIGHT_FEATURE");
                    put("feature_id", ((Map)(((List) message.get("features")).get(0))).get("id"));
                }
            });
        } else if (message.get("type").equals("ERROR")){
            System.out.println("ERROR" );
        } else if (message.get("type").equals("ZOOM_ENDED")){
            if (featureId != null && !featureId.isEmpty()){
                dispatchMessage(new LinkedHashMap<String, Object>() {
                    {
                        put("type", "HIGHLIGHT_FEATURE");
                        put("feature_id", featureId);
                    }
                });
            }
            if (routeTo != null && !routeTo.isEmpty()){
                final LinkedHashMap<String, Object> navigationParams = new LinkedHashMap<String, Object>();
                navigationParams.put("route_to", routeTo);
                dispatchMessage(new LinkedHashMap<String, Object>() {
                    {
                        put("type", "SHOW_NAVIGATION_DIALOG");
                        put("navigation_params", navigationParams);
                    }
                });
            }
        }
    }


    public String getCustomTemplate() {
        try{
            return Utils.readFileFromAssets(context, "template.html");
        } catch (IOException e){
            return null;
        }
    }

    public void SetMapID (String mapId){
        this.mapId = mapId;
    }

    public void setTenants(ReadableArray tenants){
        this.tenants = tenants != null ? tenants.toArrayList() : null;
    }

    public void SetFeatureId(String featureId) { this.featureId = featureId;}
    public void SetRouteTo(String routeTo) { this.routeTo = routeTo;}

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }
    public void setSecandaryColor(String secandaryColor) {
        this.secandaryColor = secandaryColor;
    }

    @Override
    public void onActivityResult(Activity activity, int requestCode, int resultCode, Intent data) {
    }

    @Override
    public void onNewIntent(Intent intent) {

    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {

    }

    @Override
    public void onPageFinished(String url) {

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {

    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {

    }

    @Override
    public void onExternalPageRequest(String url) {

    }
}
