package com.lh.component.common;

public class User {
    public final int trackId;
    public final Polyline swipeModel;
    public final float swipeTime;
    public final String chosenWord;

    public User(int trackId, Polyline swipeModel, float swipeTime, String chosenWord) {
        this.trackId = trackId;
        this.swipeModel = swipeModel;
        this.swipeTime = swipeTime;
        this.chosenWord = chosenWord;
    }
}
