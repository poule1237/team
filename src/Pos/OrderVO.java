package Pos;

//주문 정보 저장
public class OrderVO {
	private int order_id;
	private int menu_id;
	private int quantity;
	private int table_num;
	private boolean isPaid;

	// 생성자
	public OrderVO() {
	}

	public OrderVO(int id, int menuId, int quantity, int tableNum, boolean isPaid) {
		this.order_id = id;
		this.menu_id = menuId;
		this.quantity = quantity;
		this.table_num = tableNum;
		this.isPaid = isPaid;
	}

	// getter/setter
	public int getId() {
		return order_id;
	}

	public void setId(int id) {
		this.order_id = id;
	}

	public int getMenuId() {
		return menu_id;
	}

	public void setMenuId(int menuId) {
		this.menu_id = menuId;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public int getTableNum() {
		return table_num;
	}

	public void settablenum(int tablenum) {
		this.table_num = tablenum;
	}

	public boolean isPaid() {
		return isPaid;
	}

	public void setPaid(boolean isPaid) {
		this.isPaid = isPaid;
	}
}