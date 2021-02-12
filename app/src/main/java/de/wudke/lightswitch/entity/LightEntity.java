package de.wudke.lightswitch.entity;

import android.content.Context;

import de.wudke.lightswitch.HAUtils;
import okhttp3.Callback;

public class LightEntity extends Entity {
    int brightness;

    public LightEntity(String entityID) {
        super(entityID);
    }

    @Override
    public void QuickAction(Context context, Callback callback) {
        HAUtils haUtils = new HAUtils(context);
        haUtils.toggleLight(callback);
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }
}
