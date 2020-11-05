package com.datenight_immersia_ltd.network.api;

import java.util.List;

public class PitchLists {
    boolean success;
    String message;
    List<Pitch> data; //pitches, serialize the name**

    public PitchLists(boolean success, String message, List<Pitch> data) {
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

//    @SerializedName("data")--Not working
    public List<Pitch> getPitchLists() {
        return data;
    }
}
