package gift.domain.order;

import gift.domain.option.Option;

import java.time.LocalDateTime;

public class Order {

    private final Long id;
    private final Option option;
    private final Long memberId;
    private final int quantity;
    private final String message;
    private final LocalDateTime orderDateTime;

    public Order(Long id, Option option, Long memberId, int quantity, String message, LocalDateTime orderDateTime) {
        this.id = id;
        this.option = option;
        this.memberId = memberId;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = orderDateTime;
    }

    public Order(Option option, Long memberId, int quantity, String message) {
        this(null, option, memberId, quantity, message, LocalDateTime.now());
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

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}
