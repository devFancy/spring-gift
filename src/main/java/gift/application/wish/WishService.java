package gift.application.wish;

import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.domain.wish.Wish;
import gift.domain.wish.WishRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class WishService {

    private final WishRepository wishRepository;
    private final ProductRepository productRepository;

    public WishService(WishRepository wishRepository, ProductRepository productRepository) {
        this.wishRepository = wishRepository;
        this.productRepository = productRepository;
    }

    public Page<Wish> findByMember(Long memberId, Pageable pageable) {
        return wishRepository.findByMemberId(memberId, pageable);
    }

    @Transactional
    public Wish add(Long memberId, Long productId) {
        Product product = findProduct(productId);
        return wishRepository.findByMemberIdAndProductId(memberId, product.getId())
            .orElseGet(() -> wishRepository.save(new Wish(memberId, product)));
    }

    @Transactional
    public void remove(Long memberId, Long wishId) {
        Wish wish = findWish(wishId);
        verifyOwner(wish, memberId);
        wishRepository.delete(wish);
    }

    private Wish findWish(Long wishId) {
        return wishRepository.findById(wishId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "위시 항목을 찾을 수 없습니다."));
    }

    private void verifyOwner(Wish wish, Long memberId) {
        if (!wish.getMemberId().equals(memberId)) {
            throw new CoreException(ErrorType.FORBIDDEN, "다른 사용자의 위시 항목입니다.");
        }
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }
}
