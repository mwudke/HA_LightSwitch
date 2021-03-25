package de.wudke.lightswitch.entity;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.wudke.lightswitch.HAUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

public class LightEntity extends Entity {
    int brightness;

    public LightEntity(String entityID, String entityState, String entityFriendlyName, int brightness) {
        super(entityID);
        this.state = entityState;
        this.friendlyName = entityFriendlyName;
        this.brightness = brightness;
    }

    @Override
    public void QuickAction(Context context, Callback callback) {
        HAUtils haUtils = new HAUtils(context);
        haUtils.toggleLight(callback);
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public static ArrayList<LightEntity> getEntityList(final Context context) {
        HAUtils haUtils = new HAUtils(context);
        final ArrayList<LightEntity> entities = new ArrayList<>();

        Callback callback = new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull final Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Handler mainHandler = new Handler(getMainLooper());

                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, response.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        JSONArray jEntityArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        for (int i = 0; i < jEntityArray.length(); i++) {

                            JSONObject jEntity = jEntityArray.getJSONObject(i);

                            String entityID = jEntity.getString("entity_id");
                            String entityState = jEntity.getString("state");
                            String entityFriendlyName = jEntity.getJSONObject("attributes").getString("friendly_name");

                            int brightness = 0;

                            if ("on".equals(entityState)) {
                                brightness = jEntity.getJSONObject("attributes").getInt("brightness");
                            }

                            if (entityID.startsWith("light.")) {
                                entities.add(new LightEntity(entityID, entityState, entityFriendlyName, brightness));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        haUtils.getAllStates(callback);

        return entities;
    }
}
