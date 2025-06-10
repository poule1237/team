package Pos;

import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class PosMain {
	public static void main(String[] args) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("\n********************************************************************");
			System.out.println("                           프로그램 시작                        ");
			System.out.println("********************************************************************");
			System.out.println("\n시작메뉴 ===========================================================");
			System.out.println("      1. 주문 / 결제      2. 매출      3. 메뉴      4. 카테고리");
			System.out.println("====================================================================");
			System.out.print("[시작메뉴 번호를 입력해주세요]        * 0번 프로그램 종료\n시작메뉴 번호 : ");
			int choice = nextInt(sc); // nextInt() 대신 readInt() 사용
			
			try {
				switch (choice) {
				case 0:
					System.out.println("\n********************************************************************");
					System.out.println("                           프로그램 종료                        ");
					System.out.println("********************************************************************");
					return;
				case 1:
					orderPaymentMenu(sc);
					break;
				case 2:
					showSalesMenu(sc);
					break;
				case 3:
					menuManagementMenu(sc);
					break;
				case 4:
					categoryManagementMenu(sc);
					break;
				default:
					System.out.println("잘못된 입력입니다. 다시 입력해주세요.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	// 숫자 입력 시 nextInt() 대신 사용 (입력 꼬임 방지)
	private static int nextInt(Scanner sc) {
		while (true) {
			try {
				return Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.out.print("숫자를 입력해주세요: ");
			}
		}
	}

	// 주문/결제 메뉴
	static void orderPaymentMenu(Scanner sc) throws SQLException {
		while (true) {
			System.out.println("\n주문 / 결제 --------------------------------------------------------");
			System.out.println("      1. 등록                   2. 삭제                   3. 결제");
			System.out.println("--------------------------------------------------------------------");
			System.out.println("<주문현황>");
			List<OrderVO> orders = OrderDAO.getAllOrders();
			for (OrderVO o : orders) {
				System.out.println(o.getId() + " - 메뉴번호(" + o.getMenuId() + ") / 메뉴수량(" + o.getQuantity() + ") / 테이블번호("
						+ o.gettableNum() + ")");
			}
			System.out.print("\n[주문 / 결제 번호를 입력해주세요]        * 0번 상위메뉴\n주문 / 결제 번호 : ");
			int sub = nextInt(sc);
			if (sub == 0)
				break;
			switch (sub) {
			case 1:
				System.out.println("\n등록 ..............................................................");
				System.out.println("위치 : 홈 > 주문 / 결제 > 등록");

				// 현재 등록된 메뉴 목록 출력
				List<MenuVO> menus = MenuDAO.getAllMenus();
				System.out.println("\n<메뉴>");
				for (MenuVO m : menus) {
					System.out.println(m.getId() + " - " + m.getName() + " (" + m.getPrice() + "원)");
				}

				System.out.println("");
				System.out.print("1. 메뉴번호 : ");
				int menuId = nextInt(sc);
				System.out.print("2. 메뉴수량 : ");
				int quantity = nextInt(sc);
				System.out.print("3. 테이블번호 : ");
				int tableNo = nextInt(sc);
				OrderDAO.addOrder(menuId, quantity, tableNo);
				System.out.println("<주문되었습니다.>");
				break;
			case 2:
				System.out.println("\n삭제 ..............................................................");
				System.out.println("위치 : 홈 > 주문 / 결제 > 삭제");
				System.out.print("1. 주문번호 : ");
				int delId = nextInt(sc);
				OrderDAO.deleteOrder(delId);
				System.out.println("<주문 취소되었습니다.>");
				break;
			case 3:
				System.out.println("\n결제 ................................................................");
				System.out.println("      1. 전체결제                    2. 개별결제");
				System.out.println(".....................................................................");
				System.out.print("[결제 번호를 입력해주세요]        * 0번 상위메뉴\n위치 : 홈 > 주문 / 결제 > 결제\n결제 번호 : ");
				int pay = nextInt(sc);
				if (pay == 0)
					continue;
				if (pay == 1) {
					System.out.print("1. 테이블번호 : ");
					int table = nextInt(sc);
					OrderDAO.payTable(table);
					System.out.println("<결제되었습니다.>");
				} else if (pay == 2) {
					System.out.print("1. 주문번호 : ");
					int orderId = nextInt(sc);
					OrderDAO.payOrder(orderId);
					System.out.println("<결제되었습니다.>");
				}
				break;
			default:
				System.out.println("잘못된 입력입니다.");
			}
		}
	}
	
	// 매출 메뉴
	static void showSalesMenu(Scanner sc) throws SQLException {
		while (true) {
			System.out.println("\n매출 ---------------------------------------------------------------");
			System.out.println("      1. 날짜                     2. 분야                ");
			System.out.println("--------------------------------------------------------------------");
			System.out.print("[매출 번호를 입력해주세요]        * 0번 상위메뉴\n매출 번호 : ");
			int sub = nextInt(sc);
			if (sub == 0)
				break;
			switch (sub) {
			case 1:
				System.out.println("\n날짜 ..............................................................");
				System.out.println("위치 : 홈 > 매출 > 날짜");
				System.out.print("날   짜 : ");
				String date = sc.nextLine(); // 날짜 입력 (실제로는 날짜 입력 가능)
				int total = OrderDAO.getTotalSales();
				System.out.println("매출액 : " + total + "원");
				break;
			case 2:
				System.out.println("\n분야 ..............................................................");
				System.out.println("위치 : 홈 > 매출 > 분야");
				System.out.print("날   짜 : ");
				date = sc.nextLine(); // 날짜 입력 (실제로는 날짜 입력 가능)
				List<CategoryVO> categories = CategoryDAO.getAllCategories();
				for (CategoryVO c : categories) {
					int sales = OrderDAO.getSalesByCategory(c.getId());
					System.out.println("카테고리(" + c.getId() + ") : " + sales + "원");
				}
				break;
			default:
				System.out.println("잘못된 입력입니다.");
			}
		}
	}

	// 메뉴 관리 메뉴
	static void menuManagementMenu(Scanner sc) throws SQLException {
		while (true) {
			System.out.println("\n메뉴 ---------------------------------------------------------------");
			System.out.println("      1. 등록                   2. 수정                   3. 삭제");
			System.out.println("--------------------------------------------------------------------");
			System.out.println("<메뉴현황>");
			List<MenuVO> menus = MenuDAO.getAllMenus();
			for (MenuVO m : menus) {
				CategoryVO c = CategoryDAO.getCategory(m.getCategoryId());
				System.out.println(m.getId() + " - 카테고리(" + m.getCategoryId() + "), 이름(" + m.getName() + "), 가격("
						+ m.getPrice() + ")");
			}
			System.out.print("\n[메뉴 번호를 입력해주세요]        * 0번 상위메뉴\n메뉴 번호 : ");
			int sub = readInt(sc);
			if (sub == 0)
				break;
			switch (sub) {
			case 1:
				System.out.println("\n등록 ..............................................................");
				System.out.println("위치 : 홈 > 메뉴 > 등록");
				System.out.println("<카테고리>");
				List<CategoryVO> categories = CategoryDAO.getAllCategories();
				for (CategoryVO c : categories) {
					System.out.println(c.getId() + " - 이모티콘(" + c.getEmoji() + "), 이름(" + c.getName() + "), 설명("
							+ c.getDescription() + ")");
				}
				System.out.print("1. 카테고리 번호 : ");
				int catId = nextInt(sc);
				System.out.print("2. 이 름 : ");
				String name = sc.nextLine();
				System.out.print("3. 가 격 : ");
				int price = nextInt(sc);
				MenuDAO.addMenu(catId, name, price);
				System.out.println("<등록되었습니다.>");
				break;
			case 2:
				System.out.println("\n수정 ..............................................................");
				System.out.println("위치 : 홈 > 메뉴 > 수정");
				System.out.print("1. 메뉴번호 : ");
				int menuId = nextInt(sc);
				System.out.print("2. 이 름 : ");
				String newName = sc.nextLine();
				System.out.print("3. 가 격 : ");
				int newPrice = nextInt(sc);
				MenuDAO.updateMenu(menuId, newName, newPrice);
				System.out.println("<수정되었습니다.>");
				break;
			case 3:
				System.out.println("\n삭제 ..............................................................");
				System.out.println("위치 : 홈 > 메뉴 > 삭제");
				System.out.print("1. 메뉴번호 : ");
				int delId = nextInt(sc);
				MenuDAO.deleteMenu(delId);
				System.out.println("<삭제되었습니다.>");
				break;
			default:
				System.out.println("잘못된 입력입니다.");
			}
		}
	}

	// 카테고리 관리 메뉴
	static void categoryManagementMenu(Scanner sc) throws SQLException {
		while (true) {
			System.out.println("\n카테고리 -----------------------------------------------------------");
			System.out.println("      1. 등록                   2. 수정                   3. 삭제");
			System.out.println("--------------------------------------------------------------------");
			System.out.println("<카테고리>");
			List<CategoryVO> categories = CategoryDAO.getAllCategories();
			for (CategoryVO c : categories) {
				System.out.println(c.getId() + " - 이모티콘(" + c.getEmoji() + "), 이름(" + c.getName() + "), 설명("
						+ c.getDescription() + ")");
			}
			System.out.print("\n[카테고리 번호를 입력해주세요]        * 0번 상위메뉴\n카테고리 번호 : ");
			int sub = nextInt(sc);
			if (sub == 0)
				break;
			switch (sub) {
			case 1:
				System.out.println("\n등록 ..............................................................");
				System.out.println("위치 : 홈 > 카테고리 > 등록");
				System.out.print("1. 이모티콘 : ");
				String emoji = sc.nextLine();
				System.out.print("2. 이 름 : ");
				String name = sc.nextLine();
				System.out.print("3. 설 명 : ");
				String desc = sc.nextLine();
				CategoryDAO.addCategory(emoji, name, desc);
				System.out.println("<등록되었습니다.>");
				break;
			case 2:
				System.out.println("\n수정 ..............................................................");
				System.out.println("위치 : 홈 > 카테고리 > 수정");
				System.out.print("카테고리번호 : ");
				int catId = nextInt(sc);
				System.out.print("1. 이모티콘 : ");
				String newEmoji = sc.nextLine();
				System.out.print("2. 이 름 : ");
				String newName = sc.nextLine();
				System.out.print("3. 설 명 : ");
				String newDesc = sc.nextLine();
				CategoryDAO.updateCategory(catId, newEmoji, newName, newDesc);
				System.out.println("<수정되었습니다.>");
				break;
			case 3:
				System.out.println("\n삭제 ..............................................................");
				System.out.println("위치 : 홈 > 카테고리 > 삭제");
				System.out.print("카테고리번호 : ");
				int delId = nextInt(sc);
				CategoryDAO.deleteCategory(delId);
				System.out.println("<삭제되었습니다.>");
				break;
			default:
				System.out.println("잘못된 입력입니다.");
			}
		}
		
	}
}