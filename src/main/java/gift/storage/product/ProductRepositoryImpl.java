package gift.storage.product;

import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.storage.category.CategoryEntity;
import gift.storage.category.CategoryJpaRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ProductRepositoryImpl implements ProductRepository {

    private final ProductJpaRepository productJpaRepository;
    private final CategoryJpaRepository categoryJpaRepository;

    public ProductRepositoryImpl(
        ProductJpaRepository productJpaRepository,
        CategoryJpaRepository categoryJpaRepository
    ) {
        this.productJpaRepository = productJpaRepository;
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Product save(Product product) {
        CategoryEntity categoryEntity = findCategoryEntity(product.getCategory().getId());
        ProductEntity entity = ProductEntity.from(product, categoryEntity);
        return productJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productJpaRepository.findById(id).map(ProductEntity::toDomain);
    }

    @Override
    public Page<Product> findAll(Pageable pageable) {
        return productJpaRepository.findAll(pageable).map(ProductEntity::toDomain);
    }

    @Override
    public List<Product> findAll() {
        return productJpaRepository.findAll().stream()
            .map(ProductEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public void update(Product product) {
        ProductEntity entity = productJpaRepository.findById(product.getId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
        CategoryEntity categoryEntity = findCategoryEntity(product.getCategory().getId());
        entity.update(product, categoryEntity);
    }

    private CategoryEntity findCategoryEntity(Long categoryId) {
        return categoryJpaRepository.findById(categoryId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "카테고리를 찾을 수 없습니다."));
    }
}
