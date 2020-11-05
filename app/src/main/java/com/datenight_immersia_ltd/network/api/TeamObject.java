package com.datenight_immersia_ltd.network.api;

public class TeamObject {
    String name;
    String position;
    String details;

    public TeamObject(String name, String position, String details) {
        this.name = name;
        this.position = position;
        this.details = details;
    }

    public String getName() {
        return name;
    }

    public String getPosition() {
        return position;
    }

    public String getDetails() {
        return details;
    }
}
