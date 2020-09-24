package de.wudke.lightswitch;

import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LightSwitchTileService extends TileService {

    private SharedPreferences sharedpreferences;
    private String HA_TOKEN;
    private String HA_URL;
    private String HA_ENTITY;

    private int state = Tile.STATE_INACTIVE;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;


    @Override
    public void onCreate() {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
            public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
                loadPref();
            }
        };
        sharedpreferences.registerOnSharedPreferenceChangeListener(listener);

        loadPref();
        callHAState();
    }

    @Override
    public void onDestroy(){
        sharedpreferences.unregisterOnSharedPreferenceChangeListener(listener);
    }

    private void loadPref(){
        HA_TOKEN = sharedpreferences.getString("HA_TOKEN", "null");
        HA_URL = sharedpreferences.getString("HA_URL", "null");
        HA_ENTITY = sharedpreferences.getString("HA_ENTITY", "null");
    }

    @Override
    public void onClick() {
        callHAToggle();
    }

    private void callHA(String mod, String endpoint, Callback callback){
        if (prefCheck()) {
            OkHttpClient client = new OkHttpClient();

            Request.Builder requestBuilder = new Request.Builder()
                    .url(HA_URL + endpoint)
                    .addHeader("Authorization", "Bearer " + HA_TOKEN)
                    .addHeader("Content-Type", "application/json");

            switch (mod.toLowerCase()) {
                case "post":
                    requestBuilder.post(RequestBody.create("{\"entity_id\": \"light." + HA_ENTITY + "\"}", MediaType.parse("application/json")));
                    break;
                default:
                    requestBuilder.get();
                    break;
            }
            final Request request = requestBuilder.build();

            client.newCall(request).enqueue(callback);
        } else {
            Handler mainHandler = new Handler(getMainLooper());
            mainHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), getString(R.string.Pref_Error), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private boolean prefCheck() {
        return HA_URL != "" &&
                HA_ENTITY != "" &&
                HA_TOKEN != "";
    }

    private void callHAToggle() {

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Handler mainHandler = new Handler(getMainLooper());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    throw new IOException("Unexpected code " + response);
                } else {
                    switch (getQsTile().getState()){
                        case Tile.STATE_ACTIVE: getQsTile().setState(Tile.STATE_INACTIVE); break;
                        case Tile.STATE_INACTIVE: getQsTile().setState(Tile.STATE_ACTIVE); break;
                    }
                    getQsTile().updateTile();

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {e.printStackTrace();

                    }
                    callHAState();
                }
            }
        };

        callHA("POST", "/api/services/light/toggle", callback);
    }

    private void callHAState() {
        Callback callback =  new Callback() {
            @Override
            public void onFailure(@NotNull Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Handler mainHandler = new Handler(getMainLooper());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            // Do your stuff here related to UI, e.g. show toast
                            Toast.makeText(getBaseContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    throw new IOException("Unexpected code " + response);
                } else {
                    JSONObject mainObject;
                    try {
                        mainObject = new JSONObject(Objects.requireNonNull(response.body()).string());
                        String newState = mainObject.getString("state");
//                        System.out.println(newState);

                        switch (newState){
                            case "on": state = Tile.STATE_ACTIVE; break;
                            default: state = Tile.STATE_INACTIVE; break;
                        }

                        getQsTile().setState(state);
                        getQsTile().updateTile();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        state = Tile.STATE_INACTIVE;
                    }

                }
            }
        };

        callHA("GET", "/api/states/light." + HA_ENTITY, callback);
    }
}
