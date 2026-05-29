package gift.storage.order;

import gift.domain.order.Order;
import gift.storage.option.OptionEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "option_id")
    private OptionEntity option;

    private Long memberId;
    private int quantity;
    private String message;
    private LocalDateTime orderDateTime;

    protected OrderEntity() {
    }

    public Order toDomain() {
        return new Order(id, option.toDomain(), memberId, quantity, message, orderDateTime);
    }

    public static OrderEntity from(Order order, OptionEntity optionEntity) {
        OrderEntity entity = new OrderEntity();
        entity.option = optionEntity;
        entity.memberId = order.getMemberId();
        entity.quantity = order.getQuantity();
        entity.message = order.getMessage();
        entity.orderDateTime = LocalDateTime.now();
        return entity;
    }

    public Long getId() {
        return id;
    }

    public OptionEntity getOption() {
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
