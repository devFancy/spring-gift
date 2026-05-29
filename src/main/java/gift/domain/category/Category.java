package gift.domain.category;

import gift.domain.category.vo.CategoryColor;
import gift.domain.category.vo.CategoryImage;
import gift.domain.category.vo.CategoryName;

public class Category {

    private final Long id;
    private CategoryName name;
    private CategoryColor color;
    private CategoryImage imageUrl;
    private String description;

    public Category(Long id, String name, String color, String imageUrl, String description) {
        this.id = id;
        this.name = new CategoryName(name);
        this.color = new CategoryColor(color);
        this.imageUrl = new CategoryImage(imageUrl);
        this.description = description;
    }

    public Category(String name, String color, String imageUrl, String description) {
        this(null, name, color, imageUrl, description);
    }

    public void update(String name, String color, String imageUrl, String description) {
        this.name = new CategoryName(name);
        this.color = new CategoryColor(color);
        this.imageUrl = new CategoryImage(imageUrl);
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public CategoryName getName() {
        return name;
    }

    public CategoryColor getColor() {
        return color;
    }

    public CategoryImage getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
