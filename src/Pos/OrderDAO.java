package Pos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    // 필드
    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    private String driver = "com.mysql.cj.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/pos_db";
    private String id = "pos";
    private String pw = "pos";

    // 생성자
    public OrderDAO() {}

    // DB 연결 메소드
    private void connect() {
        try {
            Class.forName(driver);
            conn = DriverManager.getConnection(url, id, pw);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("error: DB 연결 실패 - " + e.getMessage());
        }
    }

    // 자원 정리 메소드
    private void close() {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (conn != null) conn.close();
        } catch (SQLException e) {
            System.out.println("error: 자원 해제 실패 - " + e.getMessage());
        }
    }

    // 주문 등록
    public int insertOrder(int menuId, int quantity, int tableNum) {
        int count = -1;
        connect();

        try {
            String query = "INSERT INTO orders (menu_id, quantity, table_num) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, menuId);
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, tableNum);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e);
        }

        close();
        return count;
    }

    // 주문 전체 조회
    public List<OrderVO> selectAllOrders() {
        List<OrderVO> orderList = new ArrayList<>();
        connect();

        try {
            String query = "SELECT * FROM orders";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(
                    rs.getInt("order_id"),
                    rs.getInt("menu_id"),
                    rs.getInt("quantity"),
                    rs.getInt("table_num"),
                    rs.getBoolean("ispaid")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return orderList;
    }

    
    
    
    // 테이블별 주문 조회
    public List<OrderVO> selectOrdersByTable(int tableNum) {
        List<OrderVO> orderList = new ArrayList<>();
        connect();

        try {
            String query = "SELECT * FROM orders WHERE table_num = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, tableNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(
                    rs.getInt("order_id"),
                    rs.getInt("menu_id"),
                    rs.getInt("quantity"),
                    rs.getInt("table_num"),
                    rs.getBoolean("ispaid")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return orderList;
    }

    // 결제 여부별 주문 조회
    public List<OrderVO> selectOrdersByPaid(boolean isPaid) {
        List<OrderVO> orderList = new ArrayList<>();
        connect();

        try {
            String query = "SELECT * FROM orders WHERE ispaid = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setBoolean(1, isPaid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(
                    rs.getInt("order_id"),
                    rs.getInt("menu_id"),
                    rs.getInt("quantity"),
                    rs.getInt("table_num"),
                    rs.getBoolean("ispaid")
                );
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return orderList;
    }

    // 주문 1건 조회
    public OrderVO selectOrder(int orderId) {
        OrderVO order = null;
        connect();

        try {
            String query = "SELECT * FROM orders WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new OrderVO(
                    rs.getInt("order_id"),
                    rs.getInt("menu_id"),
                    rs.getInt("quantity"),
                    rs.getInt("table_num"),
                    rs.getBoolean("ispaid")
                );
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return order;
    }

    // 주문 수정
    public int updateOrder(int orderId, int quantity, int tableNum) {
        int count = -1;
        connect();

        try {
            String query = "UPDATE orders SET quantity = ?, table_num = ? WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, quantity);
            pstmt.setInt(2, tableNum);
            pstmt.setInt(3, orderId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return count;
    }

    // 주문 삭제
    public int deleteOrder(int orderId) {
        int count = -1;
        connect();

        try {
            String query = "DELETE FROM orders WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return count;
    }

    // 주문 결제 처리 (ispaid 업데이트 방식 사용 권장)
    public int payOrder(int orderId) {
        int count = -1;
        connect();

        try {
            String query = "UPDATE orders SET ispaid = TRUE WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return count;
    }

    // 테이블 전체 결제 처리
    public int payTable(int tableNum) {
        int count = -1;
        connect();

        try {
            String query = "UPDATE orders SET ispaid = TRUE WHERE table_num = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, tableNum);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return count;
    }

    // 총 매출 조회
    public int selectTotalSales() {
        int total = 0;
        connect();

        try {
            String query = "SELECT SUM(o.quantity * m.price) AS total " +
                           "FROM orders o JOIN menu m ON o.menu_id = m.menu_id " +
                           "WHERE o.ispaid = TRUE";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return total;
    }

    // 카테고리별 매출 조회
    public int selectSalesByCategory(int categoryId) {
        int total = 0;
        connect();

        try {
            String query = "SELECT SUM(o.quantity * m.price) AS total " +
                           "FROM orders o JOIN menu m ON o.menu_id = m.menu_id " +
                           "WHERE o.ispaid = TRUE AND m.category_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, categoryId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        close();
        return total;
    }

	public void addOrder(int menuId, int quantity, int tableNum) {
		
		
	}

	
	}

