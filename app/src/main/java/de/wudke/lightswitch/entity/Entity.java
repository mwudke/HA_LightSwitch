package de.wudke.lightswitch.entity;

import android.content.Context;
import android.os.Handler;
import android.preference.PreferenceManager;
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

public abstract class Entity {
    public String entityID, state, friendlyName;

    public Entity(String entityID) {
        this.entityID = entityID;
    }

    public abstract void QuickAction(Context context, Callback callback);

    public String getEntityID() {
        return entityID;
    }

    public void setEntityID(String entityID) {
        this.entityID = entityID;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {
        this.friendlyName = friendlyName;
    }
}
