package gift.storage.category;

import gift.domain.category.Category;
import gift.domain.category.CategoryRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CategoryRepositoryImpl implements CategoryRepository {

    private final CategoryJpaRepository categoryJpaRepository;

    public CategoryRepositoryImpl(CategoryJpaRepository categoryJpaRepository) {
        this.categoryJpaRepository = categoryJpaRepository;
    }

    @Override
    public Category save(Category category) {
        CategoryEntity entity = CategoryEntity.from(category);
        return categoryJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Category> findById(Long id) {
        return categoryJpaRepository.findById(id).map(CategoryEntity::toDomain);
    }

    @Override
    public List<Category> findAll() {
        return categoryJpaRepository.findAll().stream()
            .map(CategoryEntity::toDomain)
            .toList();
    }

    @Override
    public void deleteById(Long id) {
        categoryJpaRepository.deleteById(id);
    }

    @Override
    public void update(Category category) {
        CategoryEntity entity = categoryJpaRepository.findById(category.getId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "카테고리를 찾을 수 없습니다."));
        entity.update(category);
    }
}
