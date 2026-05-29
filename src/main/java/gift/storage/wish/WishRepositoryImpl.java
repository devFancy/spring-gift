package gift.storage.wish;

import gift.domain.wish.Wish;
import gift.domain.wish.WishRepository;
import gift.storage.product.ProductEntity;
import gift.storage.product.ProductJpaRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class WishRepositoryImpl implements WishRepository {

    private final WishJpaRepository wishJpaRepository;
    private final ProductJpaRepository productJpaRepository;

    public WishRepositoryImpl(
        WishJpaRepository wishJpaRepository,
        ProductJpaRepository productJpaRepository
    ) {
        this.wishJpaRepository = wishJpaRepository;
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Wish save(Wish wish) {
        ProductEntity productEntity = findProductEntity(wish.getProduct().getId());
        WishEntity entity = WishEntity.from(wish, productEntity);
        return wishJpaRepository.save(entity).toDomain();
    }

    @Override
    public Optional<Wish> findById(Long id) {
        return wishJpaRepository.findById(id).map(WishEntity::toDomain);
    }

    @Override
    public Page<Wish> findByMemberId(Long memberId, Pageable pageable) {
        return wishJpaRepository.findByMemberId(memberId, pageable)
            .map(WishEntity::toDomain);
    }

    @Override
    public Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId) {
        return wishJpaRepository.findByMemberIdAndProductId(memberId, productId)
            .map(WishEntity::toDomain);
    }

    @Override
    public void delete(Wish wish) {
        wishJpaRepository.findById(wish.getId())
            .ifPresent(wishJpaRepository::delete);
    }

    @Override
    public long count() {
        return wishJpaRepository.count();
    }

    @Override
    public List<Wish> findAll() {
        return wishJpaRepository.findAll().stream()
            .map(WishEntity::toDomain)
            .toList();
    }

    private ProductEntity findProductEntity(Long productId) {
        return productJpaRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }
}
