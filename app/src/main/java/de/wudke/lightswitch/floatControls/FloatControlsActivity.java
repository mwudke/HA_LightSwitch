package de.wudke.lightswitch.floatControls;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.wudke.lightswitch.R;

public class FloatControlsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_float_controls);
    }

    public void dismiss(View view) {
        finish();
    }
}
