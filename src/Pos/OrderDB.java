package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class OrderDB {

	// 데이터베이스 연결을 담당하는 클래스
	    private static final String DRIVER = "com.mysql.cj.jdbc.Driver";
	    static final String DB_URL = "jdbc:mysql://127.0.0.1:3306/pos_db";
	    static final String USER = "pos";
	    static final String PASS = "pos";

	    public static Connection getConnection() throws SQLException {
	        try {
	            Class.forName(DRIVER); // 드라이버 클래스 명시적 로딩
	        } catch (ClassNotFoundException e) {
	            e.printStackTrace();
	        }
	        return DriverManager.getConnection(DB_URL, USER, PASS);
	    }
	}


