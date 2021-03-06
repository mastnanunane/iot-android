package com.eclubprague.iot.android.weissmydeweiss.cloud.sensors;

import com.eclubprague.iot.android.weissmydeweiss.cloud.hubs.Hub;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.supports.NameValuePair;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.supports.SensorType;
import com.eclubprague.iot.android.weissmydeweiss.cloud.sensors.supports.cloud_entities.SensorEntity;
import com.google.gson.Gson;

import java.util.List;

/**
 * Created by Dat on 28.7.2015.
 */
public class Accelerometer extends Sensor {

    protected String unit = "m/s^2";
    protected float x = 0;
    protected float y = 0;
    protected float z = 0;

    public Accelerometer(SensorEntity entity) {
        super(entity);
    }

    public Accelerometer(String uuid, String secret, Hub hub, String name) {
        super(uuid, SensorType.ACCELEROMETER, secret, hub, name, null);
    }

    @Override
    public void readPayload(byte[] payload) {
        //TODO
    }

    @Override
    public String printData() {
        return ("x = " + x + ", y = " + y + ", z = " + z + "(m/s^2)");
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getZ() {
        return z;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public void setData(float values[]) {
        x = values[0];
        y = values[1];
        z = values[2];
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    @Override
    public List<NameValuePair> getDataList() {
        measured.clear();
        measured.add(new NameValuePair("x", Float.toString(x)));
        measured.add(new NameValuePair("y", Float.toString(y)));
        measured.add(new NameValuePair("z", Float.toString(z)));
        return measured;
    }
}