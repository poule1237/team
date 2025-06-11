package Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//메뉴 데이터베이스 접근을 담당하는 클래스
public class MenuDAO {
//	// 메뉴 등록
//	public static void addMenu(int categoryId, String name, int price) throws SQLException {
//		String sql = "INSERT INTO menu (menu_id, name, price, category_id) VALUES (null, ?, ?, ?)";
//			try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
//				pstmt.setString(1, name);
//				pstmt.setInt(2, price);
//				pstmt.setInt(3, categoryId);
//				pstmt.executeUpdate();
//			}
//	}
	
	public static void addMenu(int categoryId, String name, int price) throws SQLException {
	    Connection conn = null;
	    PreparedStatement pstmt = null;
	    String sql = "INSERT INTO menu (menu_id, name, price, category_id) VALUES (null, ?, ?, ?)";
	    
	    try {
	        conn = DBConnection.getConnection();
	        pstmt = conn.prepareStatement(sql);
	        pstmt.setString(1, name);
	        pstmt.setInt(2, price);
	        pstmt.setInt(3, categoryId);
	        pstmt.executeUpdate();
	    } finally {
	        // 자원 해제
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) { e.printStackTrace(); }
	        if (conn != null) try { conn.close(); } catch (SQLException e) { e.printStackTrace(); }
	    }
	}


	// 메뉴 조회 (전체)
	public static List<MenuVO> getAllMenus() throws SQLException {
		List<MenuVO> list = new ArrayList<>();
		String sql = "SELECT * FROM menu";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(sql)) {
			while (rs.next()) {
				list.add(new MenuVO(rs.getInt("menu_id"), rs.getInt("category_id"), rs.getString("name"),
						rs.getInt("price")));
			}
		}
		return list;
	}

	// 메뉴 조회 (카테고리별)
	public static List<MenuVO> getMenusByCategory(int categoryId) throws SQLException {
		List<MenuVO> list = new ArrayList<>();
		String sql = "SELECT * FROM menu WHERE category_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, categoryId);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new MenuVO(rs.getInt("menu_id"), rs.getInt("category_id"), rs.getString("name"),
						rs.getInt("price")));
			}
		}
		return list;
	}

	// 메뉴 조회 (단일)
	public static MenuVO getMenu(int id) throws SQLException {
		String sql = "SELECT * FROM menu WHERE menu_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				return new MenuVO(rs.getInt("menu_id"), rs.getInt("category_id"), rs.getString("name"), rs.getInt("price"));
			}
		}
		return null;
	}

	// 메뉴 수정
	public static void updateMenu(int id, String name, int price) throws SQLException {
		String sql = "UPDATE menu SET name=?, price=? WHERE menu_id=?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setString(1, name);
			pstmt.setInt(2, price);
			pstmt.setInt(3, id);
			pstmt.executeUpdate();
		}
	}

	// 메뉴 삭제
	public static void deleteMenu(int id) throws SQLException {
		String sql = "DELETE FROM menu WHERE menu_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setInt(1, id);
			pstmt.executeUpdate();
		}
	}
}