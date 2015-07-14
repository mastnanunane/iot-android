package com.eclubprague.iot.android.weissmydeweiss.cloud.sensors;

public enum SensorType {
	THERMOMETER(0x41, "thermometer"), // 0x41
	LED(0x1, "led");

	private final int code;
	private final String name;

	public int getCode() {
		return code;
	}

	public String getName() {
		return name;
	}

	SensorType(int code, String name) {
		this.code = code;
		this.name = name;
	}
}