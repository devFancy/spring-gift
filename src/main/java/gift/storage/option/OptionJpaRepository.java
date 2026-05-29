package gift.storage.option;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OptionJpaRepository extends JpaRepository<OptionEntity, Long> {

    List<OptionEntity> findByProductId(Long productId);

    boolean existsByProductIdAndName(Long productId, String name);
}
