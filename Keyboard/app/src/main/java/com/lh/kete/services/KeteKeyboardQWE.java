package com.lh.kete.services;

import android.inputmethodservice.InputMethodService;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.WorkerThread;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.google.gson.GsonBuilder;
import com.lh.kete.algorithm.Algorithm;
import com.lh.kete.algorithm.common.Path;
import com.lh.kete.algorithm.common.Point;
import com.lh.kete.algorithm.common.PolylineModel;
import com.lh.kete.algorithm.predictor.EuclidPredictor;
import com.lh.kete.algorithm.predictor.PredictorResult;
import com.lh.kete.data.Information;
import com.lh.kete.data.KeteConfig;
import com.lh.kete.db.SQLiteHelper;
import com.lh.kete.listener.KeteGestureListener;
import com.lh.kete.listener.OnWorkerThreadListener;
import com.lh.kete.threadpool.KeteExec;
import com.lh.kete.utils.KeteUtils;
import com.lh.kete.views.KeteButton;
import com.lh.kete.views.KeteLayout;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

@SuppressWarnings("ALL")
public class KeteKeyboardQWE extends InputMethodService implements KeteGestureListener, OnWorkerThreadListener, Algorithm.Callback<PredictorResult> {
    // Variable
    private String selectedText = "";
    private KeteLayout mLayout;
    private KeteConfig mConfig;
    private EuclidPredictor predictor;

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

            // ENVIRONMENT
            Information.Companion.setLAYOUT_ID(keteConfig.getId());
            Information.Companion.setLAYOUT_HASH(KeteUtils.INSTANCE.md5(layoutFile));
            Information.Companion.setEPSILON(0f);
            Information.Companion.setLAYOUT_ASSET("qwe_keyboard.json");
            PolylineModel.Companion.setN_POINTS(50);

            // Verify database
            SQLiteHelper sqLiteHelper = new SQLiteHelper(this);
            sqLiteHelper.verify(this);
            // End verify


            predictor = new EuclidPredictor(this, keteConfig, this);
            mLayout = layout;
            mConfig = keteConfig;
            return layout;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdate(int percent, @NotNull String infoText) {
        // DO NOTHING
    }

    @Override
    public void onCompleted() {
        // DO NOTHING
    }

    @Override
    @WorkerThread
    public void onDone(@Nullable Object obj, final PredictorResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (result.getResult().size() == 0) {
                    // DO NOTHING
                } else {
                    if (selectedText.length() != 0) {
                        selectedText += " ";
                        commitComposingText(getCurrentInputConnection());
                    } else {
                        CharSequence charSequence = getCurrentInputConnection().getTextBeforeCursor(1, 0);
                        if (charSequence.length() != 0 && charSequence.charAt(0) != ' ') {
                            getCurrentInputConnection().commitText(" ", 1);
                        }
                    }
                    // Commit result
                    String tmpCommit = result.getResult().get(0).getSecond() + " ";
                    if (mLayout.isCap()) {
                        tmpCommit = tmpCommit.toUpperCase();
                    } else {
                        tmpCommit = tmpCommit.toLowerCase();
                    }
                    getCurrentInputConnection().commitText(tmpCommit, 1);
                }
            }
        });
    }

    @Override
    public void onClick(@NotNull MotionEvent event, @NotNull KeteButton button) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (button != null)
            handleButton(inputConnection, button);
    }

    private void handleButton(InputConnection inputConnection, KeteButton button) {
        // SPACE
        if ("SPACE".equals(button.getConfig().getComputingChar())) {
            selectedText += " ";
            commitComposingText(inputConnection);
            return;
        }
        if ("DEL".equals(button.getConfig().getComputingChar())) {
            if (selectedText.length() > 0) {
                selectedText = selectedText.substring(0, selectedText.length() - 1);
                inputConnection.setComposingText(selectedText, 1);
                return;
            } else {
                inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                return;
            }
        }
        if ("ENTER".equals(button.getConfig().getComputingChar())) {
            if (selectedText.length() > 0) {
                commitComposingText(inputConnection);
            }
            inputConnection.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            return;
        }

        if ("CAP".equals(button.getConfig().getComputingChar())) {
            mLayout.changeCapBehavior();
            return;
        }

        // SIGN
        if (button.getConfig().getComputingChar() != null) {
            String computingChar = button.getConfig().getComputingChar();
            if ("0".compareTo(computingChar) <= 0 && "9".compareTo(computingChar) >= 0) {
                addSignToSelectedText(inputConnection, button);
                inputConnection.setComposingText(selectedText, 1);
                return;
            }
        }

        // Normal button
        selectedText += button.getConfig().getChar();
        inputConnection.setComposingText(selectedText, 1);
    }

    private void commitComposingText(InputConnection inputConnection) {
        inputConnection.commitText(selectedText, 1);
        selectedText = "";
    }

    @Override
    public void onLongClick(@NotNull MotionEvent event, @NotNull KeteButton button) {
        if ("DEL".equals(button.getConfig().getComputingChar())) {
            KeteExec.Companion.doBackground(new Runnable() {
                @Override
                public void run() {
                    while (!isKeyUp) {
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (selectedText.length() > 0) {
                                    selectedText = selectedText.substring(0, selectedText.length() - 1);
                                    getCurrentInputConnection().setComposingText(selectedText, 1);
                                    return;
                                } else {
                                    getCurrentInputConnection().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
                                    return;
                                }
                            }
                        });
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }
    }

    // ---------- HANDLE SWIPE --------------------------------- //

    private boolean isSwipe = false;
    private float firstX, firstY;
    private Path.Builder pathBuilder = new Path.Builder();
    private boolean isKeyUp = true;

    @Override
    public void onSwipe(@NotNull MotionEvent startPos, @NotNull MotionEvent endPos, @Nullable KeteButton startButton, @Nullable KeteButton endButton) {
        isSwipe = true;
        float x = (endPos.getX() / mLayout.getWidth()) * 100;
        float y = (endPos.getY() / mLayout.getHeight()) * 100;
        pathBuilder.appendPoint(new Point(x, y));
    }

    @Override
    public void onKeyUp(@NotNull MotionEvent event) {
        if (isSwipe) {
            // Predict
            KeteExec.Companion.doBackground(new Runnable() {
                @Override
                public void run() {
                    predictor.doCalculate(null, pathBuilder.build(), KeteKeyboardQWE.this);
                }
            });
        }
        isSwipe = false;
        isKeyUp = true;
    }

    @Override
    public void onPressDown(@NotNull MotionEvent event, @Nullable KeteButton button) {
        isKeyUp = false;
        firstX = (event.getX() / mLayout.getWidth()) * 100;
        firstY = (event.getY() / mLayout.getHeight()) * 100;
        pathBuilder = new Path.Builder();
        pathBuilder.appendPoint(new Point(firstX, firstY));
    }

    private String readAssets() throws IOException {
        StringBuilder sb = new StringBuilder();
        InputStream is = getAssets().open("qwe_keyboard.json");
        BufferedReader br = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
        String str;
        while ((str = br.readLine()) != null) {
            sb.append(str);
        }
        br.close();
        return sb.toString();
    }

    // ---------------------------- ADD SIGN ---------------------------- //
    private void addSignToSelectedText(InputConnection inputConnection, KeteButton button) {
        String computingChar = button.getConfig().getComputingChar();
        if (selectedText.length() == 0) {
            selectedText += computingChar;
            inputConnection.setComposingText(selectedText, 1);
            return;
        }
        // Get last char
        Character lastChar = selectedText.charAt(selectedText.length() - 1);
        String tmp = selectedText.substring(0, selectedText.length() - 1);
        // D d
        if (computingChar.equals("9")) {
            switch (lastChar) {
                case 'D':
                    selectedText = tmp + 'Đ';
                    break;
                case 'd':
                    selectedText = tmp + 'đ';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("0")) {
            selectedText = selectedText + computingChar;
        } else if (computingChar.equals("8")) {
            switch (lastChar) {
                case 'a':
                    selectedText = tmp + 'ă';
                    break;
                case 'A':
                    selectedText = tmp + 'Ă';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("7")) {
            switch (lastChar) {
                case 'O':
                    selectedText = tmp + 'Ơ';
                    break;
                case 'o':
                    selectedText = tmp + 'ơ';
                    break;
                case 'U':
                    selectedText = tmp + 'Ư';
                    break;
                case 'u':
                    selectedText = tmp + 'ư';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("6")) {
            switch (lastChar) {
                case 'O':
                    selectedText = tmp + 'Ô';
                    break;
                case 'o':
                    selectedText = tmp + 'ô';
                    break;
                case 'E':
                    selectedText = tmp + 'Ê';
                    break;
                case 'e':
                    selectedText = tmp + 'ê';
                    break;
                case 'A':
                    selectedText = tmp + 'Â';
                    break;
                case 'a':
                    selectedText = tmp + 'â';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("5")) {
            switch (lastChar) {
                case 'A':
                    selectedText = tmp + 'Ạ';
                    break;
                case 'I':
                    selectedText = tmp + 'Ị';
                    break;
                case 'U':
                    selectedText = tmp + 'Ụ';
                    break;
                case 'E':
                    selectedText = tmp + 'Ẹ';
                    break;
                case 'O':
                    selectedText = tmp + 'Ọ';
                    break;
                case 'Ă':
                    selectedText = tmp + 'Ặ';
                    break;
                case 'Â':
                    selectedText = tmp + 'Ậ';
                    break;
                case 'Ư':
                    selectedText = tmp + 'Ự';
                    break;
                case 'Ê':
                    selectedText = tmp + 'Ệ';
                    break;
                case 'Ô':
                    selectedText = tmp + 'Ộ';
                    break;
                case 'Ơ':
                    selectedText = tmp + 'Ợ';
                    break;
                case 'Y':
                    selectedText = tmp + 'Ỵ';
                    break;
                case 'a':
                    selectedText = tmp + 'ạ';
                    break;
                case 'i':
                    selectedText = tmp + 'ị';
                    break;
                case 'u':
                    selectedText = tmp + 'ụ';
                    break;
                case 'e':
                    selectedText = tmp + 'ẹ';
                    break;
                case 'o':
                    selectedText = tmp + 'ọ';
                    break;
                case 'ă':
                    selectedText = tmp + 'ặ';
                    break;
                case 'â':
                    selectedText = tmp + 'ậ';
                    break;
                case 'ư':
                    selectedText = tmp + 'ự';
                    break;
                case 'ê':
                    selectedText = tmp + 'ệ';
                    break;
                case 'ô':
                    selectedText = tmp + 'ộ';
                    break;
                case 'ơ':
                    selectedText = tmp + 'ợ';
                    break;
                case 'y':
                    selectedText = tmp + 'ỵ';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("4")) {
            switch (lastChar) {
                case 'A':
                    selectedText = tmp + 'Ã';
                    break;
                case 'I':
                    selectedText = tmp + 'Ĩ';
                    break;
                case 'U':
                    selectedText = tmp + 'Ũ';
                    break;
                case 'E':
                    selectedText = tmp + 'Ẽ';
                    break;
                case 'O':
                    selectedText = tmp + 'Õ';
                    break;
                case 'Ă':
                    selectedText = tmp + 'Ẵ';
                    break;
                case 'Â':
                    selectedText = tmp + 'Ẫ';
                    break;
                case 'Ư':
                    selectedText = tmp + 'Ữ';
                    break;
                case 'Ê':
                    selectedText = tmp + 'Ễ';
                    break;
                case 'Ô':
                    selectedText = tmp + 'Ỗ';
                    break;
                case 'Ơ':
                    selectedText = tmp + 'Ỡ';
                    break;
                case 'Y':
                    selectedText = tmp + 'Ỹ';
                    break;
                case 'a':
                    selectedText = tmp + 'ã';
                    break;
                case 'i':
                    selectedText = tmp + 'ĩ';
                    break;
                case 'u':
                    selectedText = tmp + 'ũ';
                    break;
                case 'e':
                    selectedText = tmp + 'ẽ';
                    break;
                case 'o':
                    selectedText = tmp + 'õ';
                    break;
                case 'ă':
                    selectedText = tmp + 'ẵ';
                    break;
                case 'â':
                    selectedText = tmp + 'ẫ';
                    break;
                case 'ư':
                    selectedText = tmp + 'ữ';
                    break;
                case 'ê':
                    selectedText = tmp + 'ễ';
                    break;
                case 'ô':
                    selectedText = tmp + 'ỗ';
                    break;
                case 'ơ':
                    selectedText = tmp + 'ỡ';
                    break;
                case 'y':
                    selectedText = tmp + 'ỹ';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("3")) {
            switch (lastChar) {
                case 'A':
                    selectedText = tmp + 'Ả';
                    break;
                case 'I':
                    selectedText = tmp + 'Ỉ';
                    break;
                case 'U':
                    selectedText = tmp + 'Ủ';
                    break;
                case 'E':
                    selectedText = tmp + 'Ẻ';
                    break;
                case 'O':
                    selectedText = tmp + 'Ỏ';
                    break;
                case 'Ă':
                    selectedText = tmp + 'Ẳ';
                    break;
                case 'Â':
                    selectedText = tmp + 'Ẩ';
                    break;
                case 'Ư':
                    selectedText = tmp + 'Ử';
                    break;
                case 'Ê':
                    selectedText = tmp + 'Ể';
                    break;
                case 'Ô':
                    selectedText = tmp + 'Ổ';
                    break;
                case 'Ơ':
                    selectedText = tmp + 'Ở';
                    break;
                case 'Y':
                    selectedText = tmp + 'Ỷ';
                    break;
                case 'a':
                    selectedText = tmp + 'ả';
                    break;
                case 'i':
                    selectedText = tmp + 'ỉ';
                    break;
                case 'u':
                    selectedText = tmp + 'ủ';
                    break;
                case 'e':
                    selectedText = tmp + 'ẻ';
                    break;
                case 'o':
                    selectedText = tmp + 'ỏ';
                    break;
                case 'ă':
                    selectedText = tmp + 'ẳ';
                    break;
                case 'â':
                    selectedText = tmp + 'ẩ';
                    break;
                case 'ư':
                    selectedText = tmp + 'ử';
                    break;
                case 'ê':
                    selectedText = tmp + 'ể';
                    break;
                case 'ô':
                    selectedText = tmp + 'ổ';
                    break;
                case 'ơ':
                    selectedText = tmp + 'ở';
                    break;
                case 'y':
                    selectedText = tmp + 'ỷ';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("2")) {
            switch (lastChar) {
                case 'A':
                    selectedText = tmp + 'À';
                    break;
                case 'I':
                    selectedText = tmp + 'Ì';
                    break;
                case 'U':
                    selectedText = tmp + 'Ù';
                    break;
                case 'E':
                    selectedText = tmp + 'È';
                    break;
                case 'O':
                    selectedText = tmp + 'Ò';
                    break;
                case 'Ă':
                    selectedText = tmp + 'Ằ';
                    break;
                case 'Â':
                    selectedText = tmp + 'Ầ';
                    break;
                case 'Ư':
                    selectedText = tmp + 'Ừ';
                    break;
                case 'Ê':
                    selectedText = tmp + 'Ề';
                    break;
                case 'Ô':
                    selectedText = tmp + 'Ồ';
                    break;
                case 'Ơ':
                    selectedText = tmp + 'Ờ';
                    break;
                case 'Y':
                    selectedText = tmp + 'Ỳ';
                    break;
                case 'a':
                    selectedText = tmp + 'à';
                    break;
                case 'i':
                    selectedText = tmp + 'ì';
                    break;
                case 'u':
                    selectedText = tmp + 'ù';
                    break;
                case 'e':
                    selectedText = tmp + 'è';
                    break;
                case 'o':
                    selectedText = tmp + 'ò';
                    break;
                case 'ă':
                    selectedText = tmp + 'ằ';
                    break;
                case 'â':
                    selectedText = tmp + 'ầ';
                    break;
                case 'ư':
                    selectedText = tmp + 'ừ';
                    break;
                case 'ê':
                    selectedText = tmp + 'ề';
                    break;
                case 'ô':
                    selectedText = tmp + 'ồ';
                    break;
                case 'ơ':
                    selectedText = tmp + 'ờ';
                    break;
                case 'y':
                    selectedText = tmp + 'ỳ';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        } else if (computingChar.equals("1")) {
            switch (lastChar) {
                case 'A':
                    selectedText = tmp + 'Á';
                    break;
                case 'I':
                    selectedText = tmp + 'Í';
                    break;
                case 'U':
                    selectedText = tmp + 'Ú';
                    break;
                case 'E':
                    selectedText = tmp + 'É';
                    break;
                case 'O':
                    selectedText = tmp + 'Ó';
                    break;
                case 'Ă':
                    selectedText = tmp + 'Ắ';
                    break;
                case 'Â':
                    selectedText = tmp + 'Ấ';
                    break;
                case 'Ư':
                    selectedText = tmp + 'Ứ';
                    break;
                case 'Ê':
                    selectedText = tmp + 'Ế';
                    break;
                case 'Ô':
                    selectedText = tmp + 'Ố';
                    break;
                case 'Ơ':
                    selectedText = tmp + 'Ớ';
                    break;
                case 'Y':
                    selectedText = tmp + 'Ý';
                    break;
                case 'a':
                    selectedText = tmp + 'á';
                    break;
                case 'i':
                    selectedText = tmp + 'í';
                    break;
                case 'u':
                    selectedText = tmp + 'ú';
                    break;
                case 'e':
                    selectedText = tmp + 'é';
                    break;
                case 'o':
                    selectedText = tmp + 'ó';
                    break;
                case 'ă':
                    selectedText = tmp + 'ắ';
                    break;
                case 'â':
                    selectedText = tmp + 'ấ';
                    break;
                case 'ư':
                    selectedText = tmp + 'ứ';
                    break;
                case 'ê':
                    selectedText = tmp + 'ế';
                    break;
                case 'ô':
                    selectedText = tmp + 'ố';
                    break;
                case 'ơ':
                    selectedText = tmp + 'ớ';
                    break;
                case 'y':
                    selectedText = tmp + 'ý';
                    break;
                default:
                    selectedText = selectedText + computingChar;
            }
        }
    }
}
