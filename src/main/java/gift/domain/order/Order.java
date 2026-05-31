package gift.domain.order;

import gift.domain.option.Option;
import gift.domain.order.vo.OrderMessage;
import gift.domain.order.vo.Quantity;

import java.time.LocalDateTime;

public class Order {

    private final Long id;
    private final Option option;
    private final Long memberId;
    private final Quantity quantity;
    private final OrderMessage message;
    private final LocalDateTime orderDateTime;

    public Order(Long id, Option option, Long memberId, int quantity, String message, LocalDateTime orderDateTime) {
        this.id = id;
        this.option = option;
        this.memberId = memberId;
        this.quantity = new Quantity(quantity);
        if (message != null) {
            this.message = new OrderMessage(message);
        } else {
            this.message = null;
        }
        this.orderDateTime = orderDateTime;
    }

    public Order(Option option, Long memberId, int quantity, String message, LocalDateTime orderDateTime) {
        this(null, option, memberId, quantity, message, orderDateTime);
    }

    public Long getId() {
        return id;
    }

    public Option getOption() {
        return option;
    }

    public Long getMemberId() {
        return memberId;
    }

    public Quantity getQuantity() {
        return quantity;
    }

    public OrderMessage getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
