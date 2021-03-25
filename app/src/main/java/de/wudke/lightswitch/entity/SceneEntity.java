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

public class SceneEntity extends Entity {

    public SceneEntity(String entityID, String entityFriendlyName) {
        super(entityID);
        this.friendlyName = entityFriendlyName;
    }

    @Override
    public void QuickAction(Context context, Callback callback) {
        HAUtils haUtils = new HAUtils(context);
        haUtils.setScene(this, callback);
    }
}
