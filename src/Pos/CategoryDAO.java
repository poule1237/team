package Pos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDAO {

    // 전체 카테고리 조회
    public static List<CategoryVO> getAllCategories() throws SQLException {
        List<CategoryVO> list = new ArrayList<>();
        String sql = "SELECT * FROM category";
        
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                list.add(new CategoryVO(
                    rs.getInt("category_id"),
                    rs.getString("emoji"),
                    rs.getString("name"),
                    rs.getString("explanation")
                ));
            }
        }
        return list;
    }

    // 단일 카테고리 조회
    public static CategoryVO getCategory(int category_id) throws SQLException {
        String sql = "SELECT * FROM category WHERE category_id = ?\r\n"+ "";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, category_id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new CategoryVO(
                    rs.getInt("category_id"),
                    rs.getString("emoji"),
                    rs.getString("name"),
                    rs.getString("explanation")
                );
            }
        }
        return null;
    }

 // 카테고리 등록
    public static void addCategory(String emoji, String name, String explanation) throws SQLException {
        String sql = "INSERT INTO category (emoji, name, explanation) VALUES (?, ?, ?)";
        //                     ↑ 여기서 description -> explanation 으로 수정
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emoji);
            pstmt.setString(2, name);
            pstmt.setString(3, explanation);
            pstmt.executeUpdate();
        }
    }

    // 카테고리 수정
    public static void updateCategory(int category_id, String emoji, String name, String explanation) throws SQLException {
        String sql = "UPDATE category SET emoji = ?, name = ?, explanation = ? WHERE category_id = ?";
        //                     ↑ 여기서 description -> explanation 으로 수정
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, emoji);
            pstmt.setString(2, name);
            pstmt.setString(3, explanation);
            pstmt.setInt(4, category_id);
            pstmt.executeUpdate();
        }
    }


    // 카테고리 삭제
    public static void deleteCategory(int category_id) throws SQLException {
        String sql = "DELETE FROM category WHERE category_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, category_id);
            pstmt.executeUpdate();
        }
    }
}