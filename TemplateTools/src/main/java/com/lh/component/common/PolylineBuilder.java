package com.lh.component.common;

import com.lh.IPackage.ILayoutManager;
import com.lh.component.exception.ButtonMapperMissingException;

import java.util.HashMap;

public class PolylineBuilder {
    private ILayoutManager mLayoutManager;

    public PolylineBuilder(ILayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public Polyline from(String translatedWord, int numberOfPoints) {
        Polyline polyline = new Polyline();
        HashMap<String, Point> buttonMapper = mLayoutManager.getButtonMapper();
        for (int i = 0; i < translatedWord.length(); i++) {
            String character = String.valueOf(translatedWord.charAt(i));
            if (!buttonMapper.containsKey(character))
                throw new ButtonMapperMissingException("Miss button " + character);
            polyline.addPoint(buttonMapper.get(character));
        }
        polyline.createEquidistant(numberOfPoints);
        return polyline;
    }
}