package gift.storage.product;

import gift.domain.category.Category;
import gift.domain.product.Product;
import gift.storage.category.CategoryEntity;
import gift.storage.option.OptionEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int price;
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CategoryEntity category;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OptionEntity> options = new ArrayList<>();

    protected ProductEntity() {
    }

    public Product toDomain() {
        Category domainCategory = category.toDomain();
        return new Product(id, name, price, imageUrl, domainCategory);
    }

    public static ProductEntity from(Product product, CategoryEntity categoryEntity) {
        ProductEntity entity = new ProductEntity();
        entity.name = product.getName().value();
        entity.price = product.getPrice();
        entity.imageUrl = product.getImageUrl();
        entity.category = categoryEntity;
        return entity;
    }

    public void update(Product product, CategoryEntity categoryEntity) {
        this.name = product.getName().value();
        this.price = product.getPrice();
        this.imageUrl = product.getImageUrl();
        this.category = categoryEntity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public CategoryEntity getCategory() {
        return category;
    }
}
