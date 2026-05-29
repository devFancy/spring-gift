package gift.storage.category;

import gift.domain.category.Category;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "category")
public class CategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String color;
    private String imageUrl;
    private String description;

    protected CategoryEntity() {
    }

    public Category toDomain() {
        return new Category(id, name, color, imageUrl, description);
    }

    public static CategoryEntity from(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.name = category.getName();
        entity.color = category.getColor();
        entity.imageUrl = category.getImageUrl();
        entity.description = category.getDescription();
        return entity;
    }

    public void update(Category category) {
        this.name = category.getName();
        this.color = category.getColor();
        this.imageUrl = category.getImageUrl();
        this.description = category.getDescription();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getDescription() {
        return description;
    }
}
