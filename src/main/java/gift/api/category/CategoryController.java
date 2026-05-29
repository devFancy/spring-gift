package gift.api.category;

import gift.application.category.CategoryService;
import gift.domain.category.Category;
import gift.support.response.ApiResponse;
import jakarta.validation.Valid;
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
import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryDto.Response>>> getCategories() {
        List<CategoryDto.Response> categories = categoryService.findAll().stream()
            .map(CategoryDto.Response::from)
            .toList();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryDto.Response>> createCategory(@Valid @RequestBody CategoryDto.Request request) {
        Category saved = categoryService.create(request.name(), request.color(), request.imageUrl(), request.description());
        return ResponseEntity.created(URI.create("/api/v1/categories/" + saved.getId()))
            .body(ApiResponse.success(CategoryDto.Response.from(saved)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryDto.Response>> updateCategory(
        @PathVariable Long id,
        @Valid @RequestBody CategoryDto.Request request
    ) {
        Category updated = categoryService.update(id, request.name(), request.color(), request.imageUrl(), request.description());
        return ResponseEntity.ok(ApiResponse.success(CategoryDto.Response.from(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
