package logic.utils;

import java.sql.Connection;
import java.io.IOException;
import java.sql.DriverManager;
import java.sql.SQLException;// ??

public class SingletonDBSession {

    private static SingletonDBSession instance = null;
    private String username;
    private String password;
    private static Connection conn = null;  // perché static?

    public Connection startConnection() throws SQLException {  // capire differenza con codice Matteo
        if (conn == null || conn.isClosed()) {
            String url = "jdbc:mysql://127.0.0.1:3306/theserpentinndatabase";
            conn = DriverManager.getConnection(url, "user", "password");
        }

        return conn;

    }

    public static synchronized SingletonDBSession getInstance() {
        //singleton method
        if (SingletonDBSession.instance == null) {
            SingletonDBSession.instance = new SingletonDBSession();
        }
        return SingletonDBSession.instance;
    }
    public void closeConnection() {
        try {
            this.conn.close();
        } catch (SQLException e) {
            System.out.println("Connection can't be closed: ERROR");
            // logger.log(Level.SEVERE, e.getMessage());
        }
    }



}

