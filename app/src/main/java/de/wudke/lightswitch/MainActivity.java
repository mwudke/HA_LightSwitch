package de.wudke.lightswitch;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences sharedpreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        String HA_TOKEN = sharedpreferences.getString("HA_TOKEN", "HA-Token");
        String HA_URL = sharedpreferences.getString("HA_URL", "HA-URL");
        String HA_ENTITY = sharedpreferences.getString("HA_ENTITY", "HA-ENTITY");


        final EditText editText_HA_Token = findViewById(R.id.editText_HA_Token);
        editText_HA_Token.setText(HA_TOKEN);
        final EditText editText_HA_URL = findViewById(R.id.editText_HA_Url);
        editText_HA_URL.setText(HA_URL);
        final EditText editText_HA_Entity = findViewById(R.id.editText_HA_Entity);
        editText_HA_Entity.setText(HA_ENTITY);


        final Button button = findViewById(R.id.button_update_token);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedpreferences.edit()
                        .putString("HA_TOKEN", editText_HA_Token.getText().toString())
                        .putString("HA_URL", editText_HA_URL.getText().toString())
                        .putString("HA_ENTITY", editText_HA_Entity.getText().toString())
                        .apply();
            }
        });
    }

}
