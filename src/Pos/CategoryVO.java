package Pos;

public class CategoryVO {
    private int id;
    private String emoji;
    private String name;
    private String explanation;

    public CategoryVO() {}

    public CategoryVO(int id, String emoji, String name, String explanation) {
        this.id = id;
        this.emoji = emoji;
        this.name = name;
        this.explanation = explanation;
    }

    public int getId() {
        return id;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return explanation;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String explanation) {
        this.explanation = explanation;
    }

	public int getexplanation() {
		// TODO Auto-generated method stub
		return 0;
	}
}