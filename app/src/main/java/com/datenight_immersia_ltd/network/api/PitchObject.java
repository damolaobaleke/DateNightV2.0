package com.datenight_immersia_ltd.network.api;

public class PitchObject {
    boolean success;
    String message;
    Pitch data; //pitch, serialize the name**

    public PitchObject(boolean success, String message, Pitch data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    // @SerializedName("data")--Not working
    public Pitch getPitch() {
        return data;
    }
}

