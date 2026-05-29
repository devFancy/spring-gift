package gift.application.product;

import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.domain.product.policy.ProductNamePolicy;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    @Transactional
    public Product create(String name, int price, String imageUrl, Long categoryId, boolean allowKakaoInName) {
        ProductNamePolicy.validateKakaoUsage(name, allowKakaoInName);
        Category category = findCategory(categoryId);
        return productRepository.save(new Product(name, price, imageUrl, category));
    }

    @Transactional
    public Product update(Long id, String name, int price, String imageUrl, Long categoryId, boolean allowKakaoInName) {
        ProductNamePolicy.validateKakaoUsage(name, allowKakaoInName);
        Product product = findById(id);
        Category category = findCategory(categoryId);
        product.update(name, price, imageUrl, category);
        productRepository.update(product);
        return product;
    }

    @Transactional
    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    private Category findCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "카테고리를 찾을 수 없습니다."));
    }
}
