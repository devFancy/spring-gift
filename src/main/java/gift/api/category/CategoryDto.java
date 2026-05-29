package gift.api.category;

import gift.domain.category.Category;
import jakarta.validation.constraints.NotBlank;

public final class CategoryDto {

    private CategoryDto() {
    }

    public record Request(
        @NotBlank String name,
        @NotBlank String color,
        @NotBlank String imageUrl,
        String description
    ) {
    }

    public record Response(
        Long id,
        String name,
        String color,
        String imageUrl,
        String description
    ) {
        public static Response from(Category category) {
            return new Response(
                category.getId(),
                category.getName(),
                category.getColor(),
                category.getImageUrl(),
                category.getDescription()
            );
        }
    }
}
