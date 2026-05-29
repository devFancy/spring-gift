package gift.domain.option;

import java.util.List;
import java.util.Optional;

public interface OptionRepository {

    Option save(Option option);

    Optional<Option> findById(Long id);

    List<Option> findByProductId(Long productId);

    boolean existsByProductIdAndName(Long productId, String name);

    void delete(Option option);

    boolean existsById(Long id);

    void update(Option option);
}
