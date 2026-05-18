package gift.api.option;

import gift.application.option.OptionService;
import gift.storage.option.Option;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/options")
public class OptionController {

    private final OptionService optionService;

    public OptionController(OptionService optionService) {
        this.optionService = optionService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OptionDto.Response>>> getOptions(@PathVariable Long productId) {
        List<OptionDto.Response> options = optionService.findByProductId(productId).stream()
            .map(OptionDto.Response::from)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(options));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<OptionDto.Response>> createOption(
        @PathVariable Long productId,
        @Valid @RequestBody OptionDto.Request request
    ) {
        Option saved = optionService.register(productId, request.name(), request.quantity());
        URI location = URI.create("/api/v1/products/" + productId + "/options/" + saved.getId());
        return ResponseEntity.created(location).body(ApiResponse.success(OptionDto.Response.from(saved)));
    }

    @DeleteMapping("/{optionId}")
    public ResponseEntity<Void> deleteOption(
        @PathVariable Long productId,
        @PathVariable Long optionId
    ) {
        optionService.remove(productId, optionId);
        return ResponseEntity.noContent().build();
    }
}
