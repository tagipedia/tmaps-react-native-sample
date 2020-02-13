package com.tmaps_react_native_sample;

import android.view.ViewGroup;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.common.MapBuilder;
import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import java.util.LinkedHashMap;
import java.util.Map;

public class TGMapManager  extends SimpleViewManager<TGMapView> {
    private ReactApplicationContext reactApplicationContext;
    public static final int COMMAND_SEARCH_ITEM = 1;

    public TGMapManager(ReactApplicationContext reactApplicationContext) {
        this.reactApplicationContext = reactApplicationContext;
    }

    public static final String REACT_CLASS = "TGMapView";

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @Override
    public TGMapView createViewInstance(ThemedReactContext context) {
        TGMapView webView = new TGMapView(reactApplicationContext);

        // Fixes broken full-screen modals/galleries due to body height being 0.
        webView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT));

        return webView;
    }

    @ReactProp(name = "mapId")
    public void setMapId(TGMapView view, String mapId) {
        view.SetMapID(mapId);
    }
    @ReactProp(name = "tenants")
    public void setTenants(TGMapView view, ReadableArray tenants) {
        view.setTenants(tenants);
    }
    @ReactProp(name = "featureId")
    public void setFeatureId(TGMapView view, String featureId) {
        view.SetFeatureId(featureId);
    }
    @ReactProp(name = "routeTo")
    public void setRouteTo(TGMapView view, String routeTo) {
        view.SetRouteTo(routeTo);
    }
    @ReactProp(name = "primaryColor")
    public void setPrimaryColor(TGMapView view, String primaryColor) {
        view.setPrimaryColor(primaryColor);
    }
    @ReactProp(name = "secondaryColor")
    public void setSecondaryColor(TGMapView view, String secondaryColor) {
        view.setSecandaryColor(secondaryColor);
    }

    @Override
    public Map<String, Integer> getCommandsMap() {
        // You need to implement this method and return a map with the readable
        // name and constant for each of your commands. The name you specify
        // here is what you'll later use to access it in react-native.
        return MapBuilder.of(
                "search_item",
                COMMAND_SEARCH_ITEM
        );

    }

    public void receiveCommand(final TGMapView root, int commandId, ReadableArray args) {
        // This will be called whenever a command is sent from react-native.
        switch (commandId) {
            case COMMAND_SEARCH_ITEM:
                root.dispatchMessage(new LinkedHashMap<String, Object>() {
                    {
                        put("type", "SEARCH_ITEM");
                        put("featureIdOrCategoryName", args.getString(0));
                    }
                });

                break;
        }
    }


    @Override
    public Map getExportedCustomBubblingEventTypeConstants() {
        return MapBuilder.builder()
                .put(
                        "topLoadingProgress",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onProgressChanged")))
                .put(
                        "profileButtonClicked",
                        MapBuilder.of(
                                "phasedRegistrationNames",
                                MapBuilder.of("bubbled", "onProfileButtonClicked")))

                .build();
    }
}
