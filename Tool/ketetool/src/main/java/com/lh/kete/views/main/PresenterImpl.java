package com.lh.kete.views.main;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;
import com.lh.kete.annotations.BackgroundThread;
import com.lh.kete.annotations.UiThread;
import com.lh.kete.util.ThreadUtils;
import com.lh.kete.util.UI;
import com.lh.kete.views.devicechooser.DeviceChooser;
import com.lh.kete.views.loader.Loader;
import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;

class PresenterImpl extends MainPresenter {
    private IDevice rememberedDevice;

    PresenterImpl(MainView view) {
        super(view);
    }

    @Override
    @UiThread
    void onPreview() {
        final String json = getView().getCurrentText();
        new Thread(() -> onPreview(json)).start();
        Loader.show();
    }

    @BackgroundThread
    private void onPreview(String json) {
        // Only loading if have Loader.
        if (Loader.isReady())
            return;
        // Set text
        Loader.updateText("Checking JSON...");
        ThreadUtils.sleep(500);
        if (checkValid(json)) {
            onPrepareAdb(json);
        } else {
            Loader.hide();
            UI.run(() -> JOptionPane.showConfirmDialog(getView().getComponent(), "Invalid JSON."));
        }
    }

    @BackgroundThread
    private void onPrepareAdb(String json) {
        Loader.updateText("Preparing ADB...");
        ThreadUtils.sleep(500);
        AndroidDebugBridge adb = AndroidDebugBridge.getBridge();
        if (!adb.isConnected()) {
            Loader.hide();
            UI.run(() -> JOptionPane.showConfirmDialog(getView().getComponent(), "ADB isn't ready."));
        }

        // On connected
        IDevice[] devices = adb.getDevices();
        if (devices.length == 0) {
            Loader.hide();
            UI.run(() -> JOptionPane.showConfirmDialog(getView().getComponent(), "Android devices aren't found."));
            return;
        }

        if (rememberedDevice != null && rememberedDevice.isOnline()) {
            onStartActivity(rememberedDevice, json);
            return;
        } else
            rememberedDevice = null;

        if (devices.length == 1) {
            onStartActivity(devices[0], json);
            return;
        }

        // Number of devices > 1
        Loader.hide();
        DeviceChooser component;
        String[] deviceNames = new String[devices.length];
        for (int i = 0; i < devices.length; i++)
            deviceNames[i] = devices[i].getAvdName();

        component = DeviceChooser.getView(deviceNames);
        UI.run(() -> {
            boolean check = showDeviceChooser(component);
            if (!check)
                return;
            // Get device and start activity
            IDevice device = devices[component.cmbDevices.getSelectedIndex()];
            if (component.remembered.isSelected())
                PresenterImpl.this.rememberedDevice = device;
            else
                PresenterImpl.this.rememberedDevice = null;
            new Thread(() -> onStartActivity(device, json)).start();
            Loader.show();
        });
    }

    @BackgroundThread
    private void onStartActivity(IDevice currentDevice, String json) {
        // TODO: start preview
    }

    @BackgroundThread
    private boolean checkValid(String json) {
        try {
            JSONObject object = new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    @UiThread
    private boolean showDeviceChooser(DeviceChooser component) {
        // TODO: show device chooser
        return false;
    }
}
