package de.wudke.lightswitch.floatControls;


import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.service.quicksettings.Tile;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;

import de.wudke.lightswitch.HAUtils;
import de.wudke.lightswitch.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FloatControlsFragment extends Fragment {

    public static FloatControlsFragment newInstance() {
        return new FloatControlsFragment();
    }

    HAUtils haUtils;
    boolean lightState = false;
    int brightness = 0;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.float_controls_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        haUtils = new HAUtils(this.getContext());


        final SeekBar brightnessSeekBar = getView().findViewById(R.id.brightness_seekBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 84) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_low_white_24dp, null));
                } else if (progress >= 84 && progress <= 170) {
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

        final ImageButton imageButton = getView().findViewById(R.id.imageButton);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                            lightState = !lightState;
                            setUIState();

                            try {
                                Thread.sleep(100);
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

        updateLightState();
    }

    public void updateLightState() {
        haUtils.getLightState(new Callback() {
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
                        JSONObject res = new JSONObject(Objects.requireNonNull(response.body()).string());

                        String state = null;
                        try {
                            state = res.getString("state");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        switch (state) {
                            case "on":
                                lightState = true;
                                brightness = res.getJSONObject("attributes").getInt("brightness");
                                break;
                            default:
                                lightState = false;
                                brightness = 0;
                                break;
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
        final ImageButton imageButton = getView().findViewById(R.id.imageButton);
        if (lightState) {
            imageButton.setBackgroundTintList(this.getContext().getResources().getColorStateList(R.color.white, null));
            imageButton.setImageTintList(this.getContext().getResources().getColorStateList(R.color.black, null));
        } else {
            imageButton.setBackgroundTintList(this.getContext().getResources().getColorStateList(R.color.black, null));
            imageButton.setImageTintList(this.getContext().getResources().getColorStateList(R.color.white, null));
        }

        final SeekBar brightnessSeekBar = getView().findViewById(R.id.brightness_seekBar);
        brightnessSeekBar.setProgress(brightness);
    }

}
