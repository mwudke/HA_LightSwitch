package de.wudke.lightswitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.quicksettings.Tile;
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

public class HAUtils {
    private SharedPreferences sharedpreferences;
    public String HA_TOKEN;
    public String HA_URL;
    public String HA_ENTITY;

    public HAUtils(Context context) {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        loadPref();
    }

    private void loadPref() {
        HA_TOKEN = sharedpreferences.getString("HA_TOKEN", "");
        HA_URL = sharedpreferences.getString("HA_URL", "");
        HA_ENTITY = sharedpreferences.getString("HA_ENTITY", "");
    }

    private boolean prefCheck() {
        return HA_URL != "" &&
                HA_ENTITY != "" &&
                HA_TOKEN != "";
    }

    public void toggleLight(Callback callback) {
        System.out.println("toggleLight");

        if (prefCheck()) {
            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HA_URL + "/api/services/light/toggle")
                    .addHeader("Authorization", "Bearer " + HA_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create("{\"entity_id\": \"light." + HA_ENTITY + "\"}", MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(callback);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void getLightState(Callback callback) {
        System.out.println("getLightState");

        if (prefCheck()) {
            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HA_URL + "/api/states/light." + HA_ENTITY)
                    .addHeader("Authorization", "Bearer " + HA_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .get()
                    .build();

            client.newCall(request).enqueue(callback);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setLightBrightness(int brightness, Callback callback) {
        System.out.println("setLightBrightness: " + brightness);

        if (prefCheck()) {
            OkHttpClient client = new OkHttpClient();

            final Request request = new Request.Builder()
                    .url(HA_URL + "/api/services/light/turn_on")
                    .addHeader("Authorization", "Bearer " + HA_TOKEN)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create("{\"entity_id\": \"light." + HA_ENTITY + "\",\"brightness\": " + brightness + "}", MediaType.parse("application/json")))
                    .build();

            client.newCall(request).enqueue(callback);
        } else {
            throw new IllegalArgumentException();
        }
    }

}
