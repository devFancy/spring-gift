package gift.api.product;

import gift.storage.product.Product;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public final class ProductDto {

    private ProductDto() {
    }

    public record Request(
        @NotBlank String name,
        @Positive int price,
        @NotBlank String imageUrl,
        @NotNull Long categoryId
    ) {
    }

    public record Response(
        Long id,
        String name,
        int price,
        String imageUrl,
        Long categoryId
    ) {
        public static Response from(Product product) {
            return new Response(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getImageUrl(),
                product.getCategory().getId()
            );
        }
    }
}
