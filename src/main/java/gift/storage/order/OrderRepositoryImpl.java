package gift.storage.order;

import gift.domain.order.Order;
import gift.domain.order.OrderRepository;
import gift.storage.option.OptionEntity;
import gift.storage.option.OptionJpaRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OptionJpaRepository optionJpaRepository;

    public OrderRepositoryImpl(
        OrderJpaRepository orderJpaRepository,
        OptionJpaRepository optionJpaRepository
    ) {
        this.orderJpaRepository = orderJpaRepository;
        this.optionJpaRepository = optionJpaRepository;
    }

    @Override
    public Order save(Order order) {
        OptionEntity optionEntity = optionJpaRepository.findById(order.getOption().getId())
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "옵션을 찾을 수 없습니다."));
        OrderEntity entity = OrderEntity.from(order, optionEntity);
        return orderJpaRepository.save(entity).toDomain();
    }

    @Override
    public Page<Order> findByMemberId(Long memberId, Pageable pageable) {
        return orderJpaRepository.findByMemberId(memberId, pageable)
            .map(OrderEntity::toDomain);
    }
}
