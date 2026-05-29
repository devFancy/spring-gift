package gift.storage.wish;

import gift.domain.wish.Wish;
import gift.storage.product.ProductEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "wish")
public class WishEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    protected WishEntity() {
    }

    public Wish toDomain() {
        return new Wish(id, memberId, product.toDomain());
    }

    public static WishEntity from(Wish wish, ProductEntity productEntity) {
        WishEntity entity = new WishEntity();
        entity.memberId = wish.getMemberId();
        entity.product = productEntity;
        return entity;
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public ProductEntity getProduct() {
        return product;
    }
}
