package project;

import java.sql.Connection;
import java.sql.DriverManager;

public class DBConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/feesdb?useSSL=false&serverTimezone=UTC";
    private static final String DB_USER = "root";
    private static final String DB_PASS = "";

    static {
        try { Class.forName("com.mysql.cj.jdbc.Driver"); }
        catch (Exception e) { e.printStackTrace(); }
    }

    public static Connection getConnection() {
        try { return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS); }
        catch (Exception e) { e.printStackTrace(); return null; }
    }
}
