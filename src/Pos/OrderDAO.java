package pos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class orderdao {

    // 주문 등록
    public static void addorder(int menuid, int quantity, int tablenum) throws SQLException {
        String query = "insert into orders";
        query += " (menu_id, quantity, table_num)";
        query += " values (?, ?, ?)";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, menuid);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, tablenum);
            pstmt.executeUpdate();
        }
    }

    // 전체 주문 조회
    public static List<ordervo> getallorders() throws SQLException {
        List<ordervo> list = new ArrayList<>();
        String query = "select * from orders";
        try (Connection conn = orderdb.getconnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                list.add(new ordervo(rs.getInt("order_id"), rs.getInt("menu_id"),
                        rs.getInt("quantity"), rs.getInt("table_num"), rs.getBoolean("is_paid")));
            }
        }
        return list;
    }

    // 테이블별 주문 조회
    public static List<ordervo> getordersbytable(int tablenum) throws SQLException {
        List<ordervo> list = new ArrayList<>();
        String query = "select * from orders";
        query += " where table_num = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tablenum);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ordervo(rs.getInt("order_id"), rs.getInt("menu_id"),
                        rs.getInt("quantity"), rs.getInt("table_num"), rs.getBoolean("is_paid")));
            }
        }
        return list;
    }

    // 결제 여부로 주문 조회
    public static List<ordervo> getordersbypaid(boolean ispaid) throws SQLException {
        List<ordervo> list = new ArrayList<>();
        String query = "select * from orders";
        query += " where is_paid = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setBoolean(1, ispaid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(new ordervo(rs.getInt("order_id"), rs.getInt("menu_id"),
                        rs.getInt("quantity"), rs.getInt("table_num"), rs.getBoolean("is_paid")));
            }
        }
        return list;
    }

    // 단일 주문 조회
    public static ordervo getorder(int orderid) throws SQLException {
        String query = "select * from orders";
        query += " where order_id = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new ordervo(rs.getInt("order_id"), rs.getInt("menu_id"),
                        rs.getInt("quantity"), rs.getInt("table_num"), rs.getBoolean("is_paid"));
            }
        }
        return null;
    }

    // 주문 수정
    public static void updateorder(int orderid, int quantity, int tablenum) throws SQLException {
        String query = "update orders set quantity = ?";
        query += ", table_num = ?";
        query += " where order_id = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, tablenum);
            pstmt.setInt(3, orderid);
            pstmt.executeUpdate();
        }
    }

    // 주문 삭제
    public static void deleteorder(int orderid) throws SQLException {
        String query = "delete from orders";
        query += " where order_id = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderid);
            pstmt.executeUpdate();
        }
    }

    // 주문 결제
    public static void payorder(int orderid) throws SQLException {
        String query = "delete from orders";
        query += " where order_id = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, orderid);
            pstmt.executeUpdate();
        }
    }

    // 테이블 전체 결제
    public static void paytable(int tablenum) throws SQLException {
        String query = "delete from orders";
        query += " where table_num = ?";
        try (Connection conn = Orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, tablenum);
            pstmt.executeUpdate();
        }
    }

    // 전체 매출 조회
    public static int gettotalsales() throws SQLException {
        String query = "select sum(quantity *";
        query += " (select price from menu where id = menu_id)) as total";
        query += " from orders where is_paid = true";
        try (Connection conn = orderdb.getconnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }

    // 카테고리별 매출 조회
    public static int getsalesbycategory(int categoryid) throws SQLException {
        String query = "select sum(o.quantity * m.price) as total";
        query += " from orders o join menu m on o.menu_id = m.id";
        query += " where o.is_paid = true and m.category_id = ?";
        try (Connection conn = orderdb.getconnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, categoryid);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("total");
            }
        }
        return 0;
    }
}
