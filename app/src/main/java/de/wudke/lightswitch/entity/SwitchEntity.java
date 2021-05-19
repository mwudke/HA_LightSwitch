package de.wudke.lightswitch.entity;

import android.content.Context;

import de.wudke.lightswitch.HAUtils;
import okhttp3.Callback;

public class SwitchEntity extends Entity {

    public SwitchEntity(String entityID, String entityState, String entityFriendlyName) {
        super(entityID);
        this.state = entityState;
        this.friendlyName = entityFriendlyName;
    }

    @Override
    public void QuickAction(Context context, Callback callback) {
        HAUtils haUtils = new HAUtils(context);
        haUtils.toggleSwitch(this, callback);
    }

}
