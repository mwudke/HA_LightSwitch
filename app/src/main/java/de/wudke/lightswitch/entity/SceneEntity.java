package de.wudke.lightswitch.entity;

import android.content.Context;

import de.wudke.lightswitch.HAUtils;
import okhttp3.Callback;

public class SceneEntity extends Entity {

    public SceneEntity(String entityID) {
        super(entityID);
    }

    @Override
    public void QuickAction(Context context, Callback callback) {
        HAUtils haUtils = new HAUtils(context);
        haUtils.setScene(this, callback);
    }
}
