package gift.api.option;

import gift.storage.option.Option;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public final class OptionDto {

    private OptionDto() {
    }

    public record Request(
        @NotBlank String name,
        @Min(1) @Max(99_999_999) int quantity
    ) {
    }

    public record Response(
        Long id,
        String name,
        int quantity
    ) {
        public static Response from(Option option) {
            return new Response(option.getId(), option.getName(), option.getQuantity());
        }
    }
}
