package com.lh.IPackage;

import com.lh.component.common.Point;

import java.util.HashMap;

public interface ILayoutManager {
    String getLayoutId();

    HashMap<String, Point> getButtonMapper();
}
