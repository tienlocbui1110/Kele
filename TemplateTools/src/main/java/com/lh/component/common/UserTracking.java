package com.lh.component.common;

import com.lh.IPackage.ILayoutManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class UserTracking implements ReadableMySQL.QueryCallback {
    private int numberOfPoints;
    private ILayoutManager layoutManager;
    private ArrayList<User> mTracker;
    private ReadableMySQL db;

    public UserTracking(ILayoutManager layoutManager, int numberOfPoints) {
        this.numberOfPoints = numberOfPoints;
        this.layoutManager = layoutManager;
        init();
    }

    public int size() {
        return mTracker.size();
    }

    public User getUser(int index) {
        return mTracker.get(index);
    }

    private void init() {
        mTracker = new ArrayList<>();
        db = new ReadableMySQL(KeteDatabaseConfig.DB_NAME, KeteDatabaseConfig.USER, KeteDatabaseConfig.PASSWORD);
        // UserTracking.id |  UserTracking.time | UserTracking.chosen | PointModelDetails.idx | PointModelDetails.x | PointModelDetails.y
        // Condition: PointModel.n = numberOfPoints && PointModel.layout = layoutManager.getLayoutId()
        // Sorting by : UserTracking.id, PointModelDetails.idx
        db.query("SELECT u.id, u.time, u.user_chosen, pmd.idx, pmd.x, pmd.y  FROM UserTracking u JOIN PointModel p ON u.points = p.id AND p.n = " + numberOfPoints + " AND p.layout = '" + layoutManager.getLayoutId() + "' JOIN PointModelDetails pmd ON pmd.model = p.id ORDER BY u.id, pmd.idx", this);
    }

    @Override
    public void onQuery(ResultSet resultSet) {
        // UserTracking.id - INT|  UserTracking.time - FLOAT | UserTracking.chosen - String | PointModelDetails.idx - INT | PointModelDetails.x - FLOAT | PointModelDetails.y - FLOAT
        int userTrackingId = -1;
        float userTime = -1;
        String userWord = "";
        Polyline userSwipe = new Polyline();
        try {
            while (resultSet.next()) {
                int nextId = resultSet.getInt(1);
                if (nextId != userTrackingId) {
                    // Save old data
                    if (userTrackingId != -1) {
                        saveDataToTracker(userTrackingId, userTime, userWord, userSwipe);
                    }
                    // Begin new tracking
                    userTrackingId = nextId;
                    userTime = resultSet.getFloat(2);
                    userWord = resultSet.getString(3);
                    userSwipe = new Polyline();
                }
                userSwipe.addPoint(resultSet.getFloat(5), resultSet.getFloat(6));
            }

            // Save last data
            if (userTrackingId != -1)
                saveDataToTracker(userTrackingId, userTime, userWord, userSwipe);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void saveDataToTracker(int userTrackingId, float userTime, String userWord, Polyline userSwipe) {
        User user = new User(userTrackingId, userSwipe, userTime, userWord);
        mTracker.add(user);
    }
}
