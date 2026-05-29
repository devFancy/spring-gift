package gift.api.order;

import gift.domain.order.Order;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public final class OrderDto {

    private OrderDto() {
    }

    public record Request(
        @NotNull Long optionId,
        @Min(1) int quantity,
        String message
    ) {
    }

    public record Response(
        Long id,
        Long optionId,
        int quantity,
        String message,
        LocalDateTime orderDateTime
    ) {
        public static Response from(Order order) {
            return new Response(
                order.getId(),
                order.getOption().getId(),
                order.getQuantity(),
                order.getMessage(),
                order.getOrderDateTime()
            );
        }
    }
}
