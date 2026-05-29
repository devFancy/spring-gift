package gift.domain.product;

import gift.domain.category.Category;
import gift.domain.product.vo.ProductImage;
import gift.domain.product.vo.ProductName;
import gift.domain.product.vo.ProductPrice;

public class Product {

    private final Long id;
    private ProductName name;
    private ProductPrice price;
    private ProductImage imageUrl;
    private Category category;

    public Product(Long id, String name, int price, String imageUrl, Category category) {
        this.id = id;
        this.name = new ProductName(name);
        this.price = new ProductPrice(price);
        this.imageUrl = new ProductImage(imageUrl);
        this.category = category;
    }

    public Product(String name, int price, String imageUrl, Category category) {
        this(null, name, price, imageUrl, category);
    }

    public void update(String name, int price, String imageUrl, Category category) {
        this.name = new ProductName(name);
        this.price = new ProductPrice(price);
        this.imageUrl = new ProductImage(imageUrl);
        this.category = category;
    }

    public Long getId() {
        return id;
    }

    public ProductName getName() {
        return name;
    }

    public ProductPrice getPrice() {
        return price;
    }

    public ProductImage getImageUrl() {
        return imageUrl;
    }

    public Category getCategory() {
        return category;
    }
}
