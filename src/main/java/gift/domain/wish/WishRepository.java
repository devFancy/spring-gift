package gift.domain.wish;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface WishRepository {

    Wish save(Wish wish);

    Optional<Wish> findById(Long id);

    Page<Wish> findByMemberId(Long memberId, Pageable pageable);

    Optional<Wish> findByMemberIdAndProductId(Long memberId, Long productId);

    void delete(Wish wish);
}
