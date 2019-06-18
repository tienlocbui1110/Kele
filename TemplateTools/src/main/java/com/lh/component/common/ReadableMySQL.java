package com.lh.component.common;

import java.sql.*;

public class ReadableMySQL {
    private String mDatabaseName;
    private String mUserName;
    private String mPassword;

    public ReadableMySQL(String databaseName, String userName, String password) {
        this.mDatabaseName = databaseName;
        this.mUserName = userName;
        this.mPassword = password;
    }

    public void query(String query, QueryCallback callback) {
        try {
            Connection cnn = DriverManager.getConnection("jdbc:mysql://localhost:3306/" + mDatabaseName,
                    mUserName, mPassword);
            Statement stmt = cnn.createStatement();
            ResultSet resultSet = stmt.executeQuery(query);
            callback.onQuery(resultSet);
            cnn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public interface QueryCallback {
        void onQuery(ResultSet resultSet);
    }
}
