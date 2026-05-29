package gift.storage.option;

import gift.domain.option.Option;
import gift.domain.option.OptionRepository;
import gift.storage.product.ProductEntity;
import gift.storage.product.ProductJpaRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class OptionRepositoryImpl implements OptionRepository {

    private final OptionJpaRepository optionJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public OptionRepositoryImpl(
        OptionJpaRepository optionJpaRepository,
        ProductJpaRepository productJpaRepository
    ) {
        this.optionJpaRepository = optionJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Option save(Option option) {
        ProductEntity productEntity = findProductEntity(option.getProduct().getId());
        OptionEntity entity = OptionEntity.from(option, productEntity);
        return optionJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Option> findById(Long id) {
        return optionJpaRepository.findById(id).map(OptionEntity::toDomain);
    }

    @Override
    public List<Option> findByProductId(Long productId) {
        return optionJpaRepository.findByProductId(productId).stream()
            .map(OptionEntity::toDomain)
            .toList();
    }

    @Override
    public boolean existsByProductIdAndName(Long productId, String name) {
        return optionJpaRepository.existsByProductIdAndName(productId, name);
    }

    @Override
    public void delete(Option option) {
        optionJpaRepository.findById(option.getId())
            .ifPresent(optionJpaRepository::delete);
    }

    @Override
    public void update(Option option) {
        optionJpaRepository.findById(option.getId())
            .ifPresent(entity -> entity.updateQuantity(option.getQuantity().value()));
    }

    private ProductEntity findProductEntity(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }
}
