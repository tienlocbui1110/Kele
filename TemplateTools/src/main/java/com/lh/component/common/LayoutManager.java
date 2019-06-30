package com.lh.component.common;

import com.lh.IPackage.ILayoutManager;
import com.lh.IPackage.IResourceManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

public class LayoutManager implements ILayoutManager {
    private JSONObject mObject;
    private String mLayoutId = null;
    private HashMap<String, Point> buttonMapper = null;

    public LayoutManager(IResourceManager resourceManager) {
        mObject = new JSONObject(resourceManager.readContent());
    }

    @Override
    public String getLayoutId() {
        if (mLayoutId == null) {
            mLayoutId = mObject.getString("id");
        }
        return mLayoutId;
    }

    @Override
    public HashMap<String, Point> getButtonMapper() {
        if (buttonMapper == null) {
            buttonMapper = new HashMap<>();
            JSONArray mButtons = mObject.getJSONArray("button");
            for (int i = 0; i < mButtons.length(); i++) {
                JSONObject button = mButtons.getJSONObject(i);
                double posX = button.getDouble("x") + button.getDouble("width") / 2;
                double posY = button.getDouble("y") + button.getDouble("height") / 2;
                if (button.has("computing_char")) {
                    buttonMapper.put(button.getString("computing_char"), new Point(posX, posY));
                } else {
                    buttonMapper.put(button.getString("char"), new Point(posX, posY));
                }
            }
        }
        return buttonMapper;
    }
}