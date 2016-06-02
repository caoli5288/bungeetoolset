package com.mengcraft.bunllect;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created on 16-5-30.
 */
public class ConnectionFactory {

    private String driver;
    private String url;
    private String user;
    private String password;

    private Connection connection;

    public Connection newConnection() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        return DriverManager.getConnection(url, user, password);
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Connection getConnection() throws SQLException, ClassNotFoundException {
        if (connection == null || !connection.isValid(1)) {
            connection = newConnection();
        }
        return connection;
    }

}
