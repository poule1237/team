package Pos;

public class MenuVO {
	private int id;
	private int categoryId;
	private String name;
	private int price;

	// 생성자
	public MenuVO() {
	}

	public MenuVO(int id, int categoryId, String name, int price) {
		this.id = id;
		this.categoryId = categoryId;
		this.name = name;
		this.price = price;
	}

	// getter/setter
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

}