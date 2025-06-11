package Pos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//주문/결제 데이터베이스 접근을 담당하는 클래스
public class OrderDAO {
	// 주문 등록
	public static void addOrder(int menuId, int quantity, int tableNum) throws SQLException {
		String query = "insert into orders ";
		query += "(menu_id, quantity, table_num)";
		query += " values" + " (?, ?, ?)";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, menuId); // 메뉴번호
			pstmt.setInt(2, quantity); // 수량
			pstmt.setInt(3, tableNum); // 테이블번호
			pstmt.executeUpdate();
		}
	}

	// 주문 조회 (전체)
	public static List<OrderVO> getAllOrders() throws SQLException {
		List<OrderVO> list = new ArrayList<>();
		String query = "select * from orders";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			while (rs.next()) {
				list.add(new OrderVO(rs.getInt("order_id"),
						rs.getInt("menu_id"),
						rs.getInt("quantity"),
						rs.getInt("table_num"),
						rs.getBoolean("ispaid")));
			}
		}
		return list;
	}

	// 주문 조회 (테이블별)
	public static List<OrderVO> getOrdersByTable(int tableNum) throws SQLException {
		List<OrderVO> list = new ArrayList<>();
		String query = "select * from orders";
		query += " where table_num = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, tableNum);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				list.add(new OrderVO(rs.getInt("oreder_id"),
						rs.getInt("menu_id"), 
						rs.getInt("quantity"),
						rs.getInt("table_num"), 
						rs.getBoolean("ispaid")));
			}
		}
		return list;
	}

	// 주문 조회 (결제 여부별)
	public static List<OrderVO> getOrdersByPaid(boolean isPaid) throws SQLException {
		List<OrderVO> list = new ArrayList<>();
		String query = "select * from orders where ispaid = ?";

		try (Connection conn = DBConnection.getConnection();PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setBoolean(1, isPaid);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new OrderVO(rs.getInt("order_id"),
						rs.getInt("menu_id"),
						rs.getInt("quantity"),
						rs.getInt("table_num"),
						rs.getBoolean("ispaid")));
			}
		}
		return list;
	}

	// 주문 조회 (단일)
	public static OrderVO getOrder(int order_id) throws SQLException {
		String query = "select * from orders";
				query += " where order_id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, order_id);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return new OrderVO(rs.getInt("order_id"),
						rs.getInt("menu_id"),
						rs.getInt("quantity"),
						rs.getInt("table_num"),
						rs.getBoolean("ispaid"));
			}
		}
		return null;
	}

	// 주문 수정 (수량, 테이블 등)
	public static void updateOrder(int order_id, int quantity, int tableNum) throws SQLException {
		String query = "update orders set ";
				query += "quantity=?,";
				query += " table_no=? ";
				query += "WHERE id=?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, quantity);
			pstmt.setInt(2, tableNum);
			pstmt.setInt(3, order_id);
			pstmt.executeUpdate();
		}
	}

	// 주문 삭제
	public static void deleteOrder(int order_id) throws SQLException {
		String query = "delete from orders";
				query += " where order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, order_id);
			pstmt.executeUpdate();
		}
	}

	// 주문 결제 (매출 처리)
	public static void payOrder(int order_id) throws SQLException {
		String query = "delete from orders ";
				query += "where order_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, order_id);
			pstmt.executeUpdate();
		}
	}

	// 테이블 전체 결제(주문 삭제 방식)
	public static void payTable(int tableNum) throws SQLException {
		String query = "delete from orders";
				query += " where table_num = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, tableNum);
			pstmt.executeUpdate();
		}
	}

	// 매출 조회 (결제된 주문만)
	public static int getTotalSales() throws SQLException {
		String query = "select sum(quantity * (select price from menu where id = menu_id)) as total";
				query += " from orders where ispaid = TRUE";

		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement();
				ResultSet rs = stmt.executeQuery(query)) {

			if (rs.next()) {
				return rs.getInt("total");
			}
		}
		return 0;
	}

	// 카테고리별 매출 조회
	public static int getSalesByCategory(int categoryId) throws SQLException {
		String query = "select sum(o.quantity * m.price) as total";
				query += " from orders o";
				query += " join menu m ON o.menu_id = m.id";
				query += " where o.ispaid = TRUE AND m.category_id = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, categoryId);
			ResultSet rs = pstmt.executeQuery();

			if (rs.next()) {
				return rs.getInt("total");
			}
		}
		return 0;
	}
}