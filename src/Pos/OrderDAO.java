package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// 주문/결제 데이터베이스 접근을 담당하는 클래스
public class OrderDAO {
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/pos_db";
    private final String id = "pos";
    private final String pw = "pos";

    private Connection conn = null;
    private PreparedStatement pstmt = null;
    private ResultSet rs = null;

    // 생성자
    public OrderDAO() {}

    // DB 연결 메소드
    private void connect() {
        try {
            Class.forName(driver);  // JDBC 드라이버 로드
            conn = DriverManager.getConnection(url, id, pw);  // DB 연결
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("error: DB 연결 실패 - " + e.getMessage());
        }
    }

    // 자원 정리 메소드 (ResultSet, PreparedStatement, Connection 닫기)
    private void close() {
        try {
            if (rs != null)
                rs.close();
            if (pstmt != null)
                pstmt.close();
            if (conn != null)
                conn.close();
        } catch (SQLException e) {
            System.out.println("error: 자원 해제 실패 - " + e.getMessage());
        }
    }

    // 주문 등록: 메뉴 아이디, 수량, 테이블 번호를 받아 orders 테이블에 새로운 주문 추가
    public int insertOrder(int menuId, int quantity, int tableNum) {
        int count = -1;
        this.connect();

        try {
            String query = "INSERT INTO orders (menu_id, quantity, table_num, ispaid) VALUES (?, ?, ?, FALSE)"; // 결제해도 주문현황이 안사라지는 상황발생
            pstmt = conn.prepareStatement(query);																//쿼리문에 ispaid false로 추가해 boolean에 기본값을 0으로 만들었다
            pstmt.setInt(1, menuId);																			
            pstmt.setInt(2, quantity);
            pstmt.setInt(3, tableNum);
            count = pstmt.executeUpdate();  
        } catch (SQLException e) {
            System.out.println("error: " + e);
        }

        this.close();
        return count;  // 성공하면 1, 실패하면 -1 반환
    }

    // 주문 리스트 전체 조회 (결제되지 않은 주문만)
    public List<OrderVO> selectAllOrders() {
        List<OrderVO> orderList = new ArrayList<>();
        this.connect();

        try {
            String query = "SELECT * FROM orders WHERE ispaid = FALSE";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                // ResultSet에서 주문 정보를 읽어 OrderVO 객체로 생성 후 리스트에 추가
                OrderVO order = new OrderVO(rs.getInt("order_id"),
                                            rs.getInt("menu_id"),
                                            rs.getInt("quantity"),
                                            rs.getInt("table_num"),
                                            rs.getBoolean("ispaid"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return orderList;
    }

    // 특정 테이블의 결제되지 않은 주문 리스트 조회
    public List<OrderVO> selectOrdersByTable(int tableNum) {
        List<OrderVO> orderList = new ArrayList<>();
        this.connect();

        try {
            String query = "SELECT * FROM orders WHERE table_num = ? AND ispaid = FALSE";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, tableNum);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(rs.getInt("order_id"),
                                            rs.getInt("menu_id"),
                                            rs.getInt("quantity"),
                                            rs.getInt("table_num"),
                                            rs.getBoolean("ispaid"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return orderList;
    }

    // 결제 여부에 따른 주문 조회 (ispaid가 TRUE 또는 FALSE 인 주문 반환)
    public List<OrderVO> selectOrdersByPaid(boolean isPaid) {
        List<OrderVO> orderList = new ArrayList<>();
        this.connect();

        try {
            String query = "SELECT * FROM orders WHERE ispaid = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setBoolean(1, isPaid);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                OrderVO order = new OrderVO(rs.getInt("order_id"),
                                            rs.getInt("menu_id"),
                                            rs.getInt("quantity"),
                                            rs.getInt("table_num"),
                                            rs.getBoolean("ispaid"));
                orderList.add(order);
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return orderList;
    }

    // 특정 주문 번호에 해당하는 주문 1건 조회
    public OrderVO selectOrder(int orderId) {
        OrderVO order = null;
        this.connect();

        try {
            String query = "SELECT * FROM orders WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                order = new OrderVO(rs.getInt("order_id"),
                                    rs.getInt("menu_id"),
                                    rs.getInt("quantity"),
                                    rs.getInt("table_num"),
                                    rs.getBoolean("ispaid"));
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return order;
    }

    // 주문 수정: 주문 수량과 테이블 번호 변경
    public int updateOrder(int orderId, int quantity, int tableNum) {
        int count = -1;
        this.connect();

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

        this.close();
        return count;
    }

    // 주문 삭제: 주문 ID로 특정 주문 삭제
    public int deleteOrder(int orderId) {
        int count = -1;
        this.connect();

        try {
            String query = "DELETE FROM orders WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return count;
    }

    // 단일 주문 결제 처리: ispaid 필드를 TRUE로 업데이트
    public int payOrder(int orderId) {
        int count = -1;
        this.connect();

        try {
            String query = "UPDATE orders SET ispaid = TRUE WHERE order_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, orderId);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return count;
    }

    // 특정 테이블의 모든 주문 결제 처리
    public int payTable(int tableNum) {
        int count = -1;
        this.connect();

        try {
            String query = "UPDATE orders SET ispaid = TRUE WHERE table_num = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, tableNum);
            count = pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return count;
    }

    // 총 매출액 조회: 결제 완료된 주문들의 수량 * 메뉴 가격 합계
    public int selectTotalSales() {
        int total = 0;
        this.connect();

        try {
            String query = "SELECT SUM(o.quantity * m.price) AS total "
                         + "FROM orders o JOIN menu m ON o.menu_id = m.menu_id "
                         + "WHERE o.ispaid = TRUE";
            pstmt = conn.prepareStatement(query);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return total;
    }

    // 카테고리별 매출 조회: 특정 카테고리에 해당하는 메뉴의 결제 완료 주문 매출 합계
    public int selectSalesByCategory(int categoryId) {
        int total = 0;
        this.connect();

        try {
            String query = "SELECT SUM(o.quantity * m.price) AS total "
                         + "FROM orders o JOIN menu m ON o.menu_id = m.menu_id "
                         + "WHERE o.ispaid = TRUE AND m.category_id = ?";
            pstmt = conn.prepareStatement(query);
            pstmt.setInt(1, categoryId);
            rs = pstmt.executeQuery();

            if (rs.next()) {
                total = rs.getInt("total");
            }
        } catch (SQLException e) {
            System.out.println("error: " + e.getMessage());
        }

        this.close();
        return total;
    }

    // addOrder 메소드: insertOrder 호출 (호출 편의성 제공)
    public void addOrder(int menuId, int quantity, int tableNum) {
        insertOrder(menuId, quantity, tableNum);
    }
}
