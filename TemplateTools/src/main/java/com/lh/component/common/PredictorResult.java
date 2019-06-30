package com.lh.component.common;

import java.util.LinkedList;
import java.util.List;

public class PredictorResult {
    private final int MAX_STACK = 5;
    private final LinkedList<Pair<Double, String>> result = new LinkedList<>();

    public void addResult(String prediction, double avgDistance) {
        for (int i = 0; i < result.size(); i++) {
            if (avgDistance < result.get(i).first) {
                result.add(i, new Pair<>(avgDistance, prediction));
                break;
            }
        }
        if (result.size() < MAX_STACK)
            result.add(new Pair<>(avgDistance, prediction));
        verify();
    }

    public List<Pair<Double, String>> getResult() {
        return result;
    }

    public void remove(String s) {
        Pair<Double, String> x = null;
        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).second.equals(s)) {
                x = result.get(i);
                break;
            }
        }
        if (x != null)
            result.remove(x);
    }

    private void verify() {
        while (result.size() > MAX_STACK)
            result.remove(result.size() - 1);
    }
}
