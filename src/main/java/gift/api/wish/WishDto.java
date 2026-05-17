package gift.api.wish;

import gift.storage.wish.Wish;
import jakarta.validation.constraints.NotNull;

public final class WishDto {

    private WishDto() {
    }

    public record Request(@NotNull Long productId) {
    }

    public record Response(
        Long id,
        Long productId,
        String productName,
        int price,
        String imageUrl
    ) {
        public static Response from(Wish wish) {
            return new Response(
                wish.getId(),
                wish.getProduct().getId(),
                wish.getProduct().getName(),
                wish.getProduct().getPrice(),
                wish.getProduct().getImageUrl()
            );
        }
    }
}
