package de.wudke.lightswitch.floatControls;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import de.wudke.lightswitch.HAUtils;
import de.wudke.lightswitch.MainActivity;
import de.wudke.lightswitch.R;
import de.wudke.lightswitch.entity.Entity;
import de.wudke.lightswitch.entity.LightEntity;
import de.wudke.lightswitch.entity.SceneEntity;
import de.wudke.lightswitch.entity.SwitchEntity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.os.Looper.getMainLooper;

//TODO: keep everything in sync
//TODO: cache entities
public class FloatControlsAdvancedFragment extends Fragment {

    private HAUtils haUtils;
    private boolean lightState = false;
    private int brightness = 0;
    private SharedPreferences sharedpreferences;

    EntityAdapter entityAdapter;
    private ArrayList<Entity> entities;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.float_controls_advanced_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        haUtils = new HAUtils(this.getContext());


        //TODO: also add other lights
        this.entities = new ArrayList<>();
        RecyclerView recyclerView = Objects.requireNonNull(getView()).findViewById(R.id.recyclerView_quickActions);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        entityAdapter = new EntityAdapter(this.getContext(), this.entities);
        recyclerView.setAdapter(entityAdapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        updateEntityList();
        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);


        final SeekBar brightnessSeekBar = Objects.requireNonNull(getView()).findViewById(R.id.brightness_seekBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 84) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_low_white_24dp, null));
                } else if (progress <= 170) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_medium_white_24dp, null));
                } else {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_high_white_24dp, null));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(final SeekBar seekBar) {
                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            brightness = seekBar.getProgress() + 1;

                            if (!lightState) {
                                lightState = true;
                                setUIState();
                            }
                        }
                    }
                };

                haUtils.setLightBrightness(seekBar.getProgress() + 1, callback);
            }
        });

        final ImageButton imageButtonToggleLight = getView().findViewById(R.id.imageButton_toggle_light);
        imageButtonToggleLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lightState) {
                    lightState = false;
                    brightness = 0;
                } else {
                    lightState = true;
                }
                setUIState();

                Callback callback = new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, final Response response) throws IOException {
                        if (!response.isSuccessful()) {
                            throw new IOException("Unexpected code " + response);
                        } else {
                            try {
                                Thread.sleep(200);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            updateLightState();
                        }
                    }
                };

                haUtils.toggleLight(callback);
            }
        });

        final ImageButton imageButtonOpenSettings = getView().findViewById(R.id.imageButton_open_settings);
        imageButtonOpenSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MainActivity.class);
                startActivity(intent);
            }
        });

        final ImageButton imageButtonOpenHA = getView().findViewById(R.id.imageButton_open_ha);
        imageButtonOpenHA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getContext().getPackageManager().getLaunchIntentForPackage("io.homeassistant.companion.android");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                } else {
                    Uri webpage = Uri.parse(sharedpreferences.getString("HA_URL", ""));
                    Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
                    startActivity(webIntent);
                }
            }
        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getContext(), LinearLayoutManager.HORIZONTAL, false);

        RecyclerView recyclerViewQuickActions = getView().findViewById(R.id.recyclerView_quickActions);
        recyclerViewQuickActions.setLayoutManager(layoutManager);

        TextView entityLabel = getView().findViewById(R.id.textView_entity_label);
        entityLabel.setText(sharedpreferences.getString("HA_ENTITY", ""));

        updateLightState();
    }

    private void updateLightState() {
        haUtils.getLightState(new Callback() {
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
                            Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        JSONObject res = new JSONObject(Objects.requireNonNull(response.body()).string());

                        String state = "off";
                        try {
                            state = res.getString("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if ("on".equals(state)) {
                            lightState = true;
                            brightness = res.getJSONObject("attributes").getInt("brightness");
                        } else {
                            lightState = false;
                            brightness = 0;
                        }

                        setUIState();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        });
    }

    private void setUIState() {
        final ImageButton imageButton = Objects.requireNonNull(getView()).findViewById(R.id.imageButton_toggle_light);
        if (lightState) {
            imageButton.setBackgroundTintList(Objects.requireNonNull(this.getContext()).getResources().getColorStateList(R.color.white, null));
            imageButton.setImageTintList(this.getContext().getResources().getColorStateList(R.color.black, null));
        } else {
            imageButton.setBackgroundTintList(Objects.requireNonNull(this.getContext()).getResources().getColorStateList(R.color.inactive, null));
            imageButton.setImageTintList(this.getContext().getResources().getColorStateList(R.color.white, null));
        }

        final SeekBar brightnessSeekBar = getView().findViewById(R.id.brightness_seekBar);
        brightnessSeekBar.setProgress(brightness);
    }

    public void updateEntityList() {

        entities.clear();
//        getLightEntityList(this.getContext());
//        getSceneEntityList(this.getContext());


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
                            Toast.makeText(getContext(), response.toString(), Toast.LENGTH_LONG).show();
                        }
                    });

                    throw new IOException("Unexpected code " + response);
                } else {
                    try {
                        ArrayList<Entity> LightEntities = new ArrayList<>();
                        ArrayList<Entity> SceneEntities = new ArrayList<>();
                        ArrayList<Entity> SwitchEntities = new ArrayList<>();

                        JSONArray jEntityArray = new JSONArray(Objects.requireNonNull(response.body()).string());
                        for (int i = 0; i < jEntityArray.length(); i++) {

                            JSONObject jEntity = jEntityArray.getJSONObject(i);

                            String entityID = jEntity.getString("entity_id");


                            if (entityID.startsWith("light.")) {
                                String entityState = jEntity.getString("state");
                                String entityFriendlyName = jEntity.getJSONObject("attributes").getString("friendly_name");

                                int brightness = 0;

                                if ("on".equals(entityState)) {
                                    brightness = jEntity.getJSONObject("attributes").getInt("brightness");
                                }

                                LightEntities.add(new LightEntity(entityID, entityState, entityFriendlyName, brightness));

                            } else if (entityID.startsWith("scene.")) {
                                String entityFriendlyName = jEntity.getJSONObject("attributes").getString("friendly_name");
                                SceneEntities.add(new SceneEntity(entityID, entityFriendlyName));

                            } else if (entityID.startsWith("switch.")) {
                                String entityFriendlyName = jEntity.getJSONObject("attributes").getString("friendly_name");
                                String entityState = jEntity.getString("state");
                                SwitchEntities.add(new SwitchEntity(entityID, entityState, entityFriendlyName));
                            }
                        }

                        entities.addAll(SceneEntities);
                        entities.addAll(LightEntities);
                        entities.addAll(SwitchEntities);
                        new Handler(getMainLooper()).post(new Runnable() {
                            public void run() {
                                FloatControlsAdvancedFragment.this.entityAdapter.notifyDataSetChanged();
//                                AndroidBasicThreadActivity.textView.setText("Hello!! Android Team :-) From child thread.");
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }
        };

        haUtils.getAllStates(callback);
    }
}
