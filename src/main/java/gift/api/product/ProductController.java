package gift.api.product;

import gift.application.product.ProductService;
import gift.storage.product.Product;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductDto.Response>>> getProducts(Pageable pageable) {
        Page<ProductDto.Response> products = productService.findAll(pageable).map(ProductDto.Response::from);
        return ResponseEntity.ok(ApiResponse.success(products));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto.Response>> getProduct(@PathVariable Long id) {
        Product product = productService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(ProductDto.Response.from(product)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto.Response>> createProduct(@Valid @RequestBody ProductDto.Request request) {
        Product saved = productService.create(request.name(), request.price(), request.imageUrl(), request.categoryId(), false);
        return ResponseEntity.created(URI.create("/api/products/" + saved.getId()))
            .body(ApiResponse.success(ProductDto.Response.from(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDto.Response>> updateProduct(
        @PathVariable Long id,
        @Valid @RequestBody ProductDto.Request request
    ) {
        Product saved = productService.update(id, request.name(), request.price(), request.imageUrl(), request.categoryId(), false);
        return ResponseEntity.ok(ApiResponse.success(ProductDto.Response.from(saved)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
