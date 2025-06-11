package Pos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

//주문/결제 데이터베이스 접근을 담당하는 클래스
public class OrderDAO {
	private final String driver = "com.mysql.cj.jdbc.Driver";
	private final String url = "jdbc:mysql://localhost:3306/pos_db";
	private final String id = "pos";
	private final String pw = "pos";

	private  Connection conn = null;
	private  PreparedStatement pstmt = null;
	private ResultSet rs = null;
	

	private void connect() {
		try {
			Class.forName(driver);
			conn = DriverManager.getConnection(url, id, pw);
		} catch (ClassNotFoundException | SQLException e) {
			System.out.println("DB 연결 실패: " + e.getMessage());
		}
	}

	private void close() {
		try {
			if (pstmt != null)
				pstmt.close();
			if (conn != null)
				conn.close();
		} catch (SQLException e) {
			System.out.println("DB 자원 해제 실패: " + e.getMessage());
		}
	}

	// 주문 등록
	public  int addOrder(int menuId, int quantity, int tableNum) {
		int count = -1;

		this.connect();

		try {
			String query = "";
			query += "insert into orders ";
			query += "(menu_id, quantity, table_num)";
			query += " values" + " (?, ?, ?)";

			pstmt = conn.prepareStatement(query);
			pstmt.setInt(1, menuId); // 메뉴번호
			pstmt.setInt(2, quantity); // 수량
			pstmt.setInt(3, tableNum); // 테이블번호
			count = pstmt.executeUpdate();

		} catch (SQLException e) {
			System.out.println("error:" + e);
		}
		this.close();

		return count;
	}

	public List<OrderVO> getAllOrders() {
		List<OrderVO> list = new ArrayList<>();
		this.connect(); // 연결

		

		try {
			String query = "select * from orders";
			
			Statement stmt = conn.createStatement(); 
			ResultSet rs = stmt.executeQuery(query); 

			while (rs.next()) {
				list.add(new OrderVO(rs.getInt("order_id"), rs.getInt("menu_id"), rs.getInt("quantity"),
						rs.getInt("table_num"), rs.getBoolean("ispaid")));
			}

			rs.close();
			stmt.close();
		} catch (SQLException e) {
			System.out.println("error:" + e.getMessage());
		} finally {
			this.close(); // 자원 정리
		}

		return list;
	}

	// 주문 조회 (테이블별)
	public List<OrderVO> getOrdersByTable(int tableNum) {
	    List<OrderVO> list = new ArrayList<>();
	    this.connect(); 

	    String query = "select * from orders where table_num = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, tableNum);
	        try (ResultSet rs = pstmt.executeQuery()) {
	            while (rs.next()) {
	                list.add(new OrderVO(
	                    rs.getInt("order_id"),    
	                    rs.getInt("menu_id"),
	                    rs.getInt("quantity"),
	                    rs.getInt("table_num"),
	                    rs.getBoolean("ispaid")
	                ));
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close(); 
	    }

	    return list;
	}


	// 주문 조회 (결제 여부별)
	public  List<OrderVO> getOrdersByPaid(boolean isPaid) {
		
		this.connect();
		
		List<OrderVO> list = new ArrayList<>();
		String query = "select * from orders where ispaid = ?";

		try ( PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setBoolean(1, isPaid);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				list.add(new OrderVO(rs.getInt("order_id"),
						rs.getInt("menu_id"),
						rs.getInt("quantity"),
						rs.getInt("table_num"),
						rs.getBoolean("ispaid")));
			}
		}catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close(); 
	    }

	    return list;
	}

	// 주문 조회 (단일)
	public OrderVO getOrder(int order_id) {

	    this.connect();

	    String query = "SELECT * FROM orders WHERE order_id = ?";
	    
	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, order_id);

	        try (ResultSet rs = pstmt.executeQuery()) {
	            if (rs.next()) {
	                return new OrderVO(
	                    rs.getInt("order_id"),
	                    rs.getInt("menu_id"),
	                    rs.getInt("quantity"),
	                    rs.getInt("table_num"),
	                    rs.getBoolean("ispaid")
	                );
	            }
	        }
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close();
	    }

	    return null; //주문을 찾지못했을때 null 반환
	}
	// 주문 수정 (수량, 테이블 등)
	public int updateOrder(int order_id, int quantity, int tableNum){
		
		this.connect();
		
		int result = 0;
		
		String query = "update orders set quantity = ?,";
				query+= " table_num = ?";
				query+= " where order_id = ?";

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, quantity);
	        pstmt.setInt(2, tableNum);
	        pstmt.setInt(3, order_id);
	        result = pstmt.executeUpdate(); // 영향받은 행 수 반환
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close();
	    }

	    return result;
	}
	

	// 주문 삭제
	public int deleteOrder(int order_id){
		
		this.connect();
		
		int result = 0;
		
		String query = "delete from orders";
		query += " where order_id = ?";

		try ( PreparedStatement pstmt = conn.prepareStatement(query)) {
			pstmt.setInt(1, order_id);
			result = pstmt.executeUpdate();
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close();
	    }
	    
	    return result;
	}

	// 주문 결제 (매출 처리)
	public int payOrder(int order_id){
		
		this.connect();
		
		String query = "delete from orders ";
		query += "where order_id = ?";

		 int result = 0;

		    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
		        pstmt.setInt(1, order_id);
		        result = pstmt.executeUpdate();  // 삭제된 행 수 저장
		    } catch (SQLException e) {
		        System.out.println("error: " + e.getMessage());
		    } finally {
		        this.close();
		    }

		    return result;  // 영향을 받은 행 수 반환
		}

	// 테이블 전체 결제(주문 삭제 방식)
	public int payTable(int tableNum) {
		
		this.connect();
		
		String query = "delete from orders";
		query += " where table_num = ?";
		int result = 0;

	    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
	        pstmt.setInt(1, tableNum);
	        result = pstmt.executeUpdate();  // 삭제된 행 개수 저장
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close();  // 자원 정리
	    }

	    return result;
	}
	// 매출 조회 (결제된 주문만)
	public int getTotalSales() {
		
		this.connect();
		String query = "select sum(quantity * (select price from menu where id = menu_id)) as total";
		query += " from orders where ispaid = TRUE";

		int total = 0;

	    try (Statement stmt = conn.createStatement();
	         ResultSet rs = stmt.executeQuery(query)) {

	        if (rs.next()) {
	            total = rs.getInt("total");
	        }
	    } catch (SQLException e) {
	        System.out.println("error: " + e.getMessage());
	    } finally {
	        this.close(); // 커넥션 및 자원 닫기
	    }

	    return total;
	}

	// 카테고리별 매출 조회
	public int getSalesByCategory(int categoryId){
		
		this.connect();
		
		String query = "select sum(o.quantity * m.price) as total";
		query += " from orders o";
		query += " join menu m ON o.menu_id = m.id";
		query += " where o.ispaid = TRUE AND m.category_id = ?";

		 int total = 0;

		    try (PreparedStatement pstmt = conn.prepareStatement(query)) {
		        pstmt.setInt(1, categoryId);

		        try (ResultSet rs = pstmt.executeQuery()) {
		            if (rs.next()) {
		                total = rs.getInt("total");
		            }
		        }
		    } catch (SQLException e) {
		        System.out.println("error: " + e.getMessage());
		    } finally {
		        this.close();
		    }

		    return total;
		}
}