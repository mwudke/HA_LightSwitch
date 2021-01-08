package de.wudke.lightswitch.tileService;

import android.os.Handler;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import de.wudke.lightswitch.HAUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LightSwitchTileService extends TileService {

    private int state = Tile.STATE_INACTIVE;

    private HAUtils haUtils;

    @Override
    public void onCreate() {
        haUtils = new HAUtils(getBaseContext());
        callHAState();
    }

    @Override
    public void onStartListening() {
        callHAState();
    }

    @Override
    public void onClick() {
        switch (getQsTile().getState()) {
            case Tile.STATE_ACTIVE:
                getQsTile().setState(Tile.STATE_INACTIVE);
                break;
            case Tile.STATE_INACTIVE:
                getQsTile().setState(Tile.STATE_ACTIVE);
                break;
        }
        getQsTile().updateTile();

        callHAToggle();
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
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {e.printStackTrace();

                    }

                    callHAState();
                }
            }
        };
        haUtils.toggleLight(callback);
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

                        if ("on".equals(newState)) {
                            state = Tile.STATE_ACTIVE;
                        } else {
                            state = Tile.STATE_INACTIVE;
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

        haUtils.getLightState(callback);
    }
}
