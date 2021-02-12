package de.wudke.lightswitch.entity;

import android.content.Context;

import okhttp3.Callback;

public class Entity {
    public String entityID, state, friendlyName;

    public Entity(String entityID) {
        this.entityID = entityID;
    }

    public void QuickAction(Context context, Callback callback) {
    }

    ;

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
