package com.lh.kete.services;

import android.inputmethodservice.InputMethodService;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.google.gson.GsonBuilder;
import com.lh.kete.data.KeteConfig;
import com.lh.kete.listener.KeteGestureListener;
import com.lh.kete.views.KeteButton;
import com.lh.kete.views.KeteLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class KeteKeyboard extends InputMethodService implements KeteGestureListener {
    @Override
    public View onCreateInputView() {
        try {
            String layoutFile = readAssets();
            KeteConfig keteConfig = new GsonBuilder()
                    .serializeNulls()
                    .excludeFieldsWithoutExposeAnnotation()
                    .create()
                    .fromJson(layoutFile, KeteConfig.class);
            KeteLayout layout = new KeteLayout(this, keteConfig);
            layout.setOnGestureListener(this);
            return layout;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onClick(@NotNull MotionEvent event, @NotNull KeteButton button) {
        InputConnection inputConnection = getCurrentInputConnection();
        inputConnection.setComposingText(button.getConfig().getChar(), 1);
    }

    @Override
    public void onLongClick(@NotNull MotionEvent event, @NotNull KeteButton button) {

    }

    @Override
    public void onSwipe(@NotNull MotionEvent startPos, @NotNull MotionEvent endPos, @Nullable KeteButton startButton, @Nullable KeteButton endButton) {

    }

    @Override
    public void onKeyUp(@NotNull MotionEvent event) {

    }

    @Override
    public void onPressDown(@NotNull MotionEvent event, @Nullable KeteButton button) {

    }

    private String readAssets() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = getAssets().open("vietnamese_layout.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        br.close();
        return sb.toString();
    }
}
