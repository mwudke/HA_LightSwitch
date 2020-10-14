package de.wudke.lightswitch.floatControls;

import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;

import de.wudke.lightswitch.R;

public class FloatControlsFragment extends Fragment {

    private FloatControlsViewModel mViewModel;

    public static FloatControlsFragment newInstance() {
        return new FloatControlsFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.float_controls_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(FloatControlsViewModel.class);

        final SeekBar brightnessSeekBar = getView().findViewById(R.id.brightness_seekBar);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 34) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_low_white_24dp, null));
                } else if (progress > 33 && progress < 66) {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_medium_white_24dp, null));
                } else {
                    seekBar.setThumb(getResources().getDrawable(R.drawable.ic_brightness_high_white_24dp, null));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }
}
