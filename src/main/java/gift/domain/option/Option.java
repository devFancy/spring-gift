package gift.domain.option;

import gift.domain.option.vo.OptionName;
import gift.domain.option.vo.Quantity;
import gift.domain.product.Product;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;

public class Option {

    private final Long id;
    private Product product;
    private OptionName name;
    private Quantity quantity;

    public Option(Long id, Product product, String name, int quantity) {
        this.id = id;
        this.product = product;
        this.name = new OptionName(name);
        this.quantity = new Quantity(quantity);
    }

    public Option(Product product, String name, int quantity) {
        this(null, product, name, quantity);
    }

    public void subtractQuantity(int amount) {
        if (amount > this.quantity.value()) {
            throw new CoreException(ErrorType.CONFLICT, "차감할 수량이 현재 재고보다 많습니다.");
        }
        this.quantity = new Quantity(this.quantity.value() - amount);
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public OptionName getName() {
        return name;
    }

    public Quantity getQuantity() {
        return quantity;
    }
}
