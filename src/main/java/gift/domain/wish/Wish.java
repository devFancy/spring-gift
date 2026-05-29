package gift.domain.wish;

import gift.domain.product.Product;

public class Wish {

    private final Long id;
    private final Long memberId;
    private final Product product;

    public Wish(Long id, Long memberId, Product product) {
        this.id = id;
        this.memberId = memberId;
        this.product = product;
    }

    public Wish(Long memberId, Product product) {
        this(null, memberId, product);
    }

    public Long getId() {
        return id;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Product getProduct() {
        return product;
    }
}
