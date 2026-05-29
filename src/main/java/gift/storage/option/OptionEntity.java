package gift.storage.option;

import gift.domain.option.Option;
import gift.storage.product.ProductEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "options")
public class OptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private ProductEntity product;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    protected OptionEntity() {
    }

    public Option toDomain() {
        return new Option(id, product.toDomain(), name, quantity);
    }

    public static OptionEntity from(Option option, ProductEntity productEntity) {
        OptionEntity entity = new OptionEntity();
        entity.product = productEntity;
        entity.name = option.getName().value();
        entity.quantity = option.getQuantity();
        return entity;
    }

    public void updateQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }

    public ProductEntity getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }
}
