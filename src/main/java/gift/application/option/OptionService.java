package gift.application.option;

import gift.domain.option.Option;
import gift.domain.option.OptionRepository;
import gift.domain.product.Product;
import gift.domain.product.ProductRepository;
import gift.support.error.CoreException;
import gift.support.error.ErrorType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class OptionService {

    private final OptionRepository optionRepository;
    private final ProductRepository productRepository;

    public OptionService(OptionRepository optionRepository, ProductRepository productRepository) {
        this.optionRepository = optionRepository;
        this.productRepository = productRepository;
    }

    public List<Option> findByProductId(Long productId) {
        ensureProductExists(productId);
        return optionRepository.findByProductId(productId);
    }

    @Transactional
    public Option register(Long productId, String name, int quantity) {
        Product product = findProduct(productId);
        ensureNameNotDuplicated(productId, name);
        return optionRepository.save(new Option(product, name, quantity));
    }

    @Transactional
    public void remove(Long productId, Long optionId) {
        ensureProductExists(productId);
        ensureNotLastOption(productId);
        Option option = findOptionInProduct(productId, optionId);
        optionRepository.delete(option);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    private void ensureProductExists(Long productId) {
        if (productRepository.findById(productId).isEmpty()) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
    }

    private void ensureNameNotDuplicated(Long productId, String name) {
        if (optionRepository.existsByProductIdAndName(productId, name)) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "이미 존재하는 옵션명입니다.");
        }
    }

    private void ensureNotLastOption(Long productId) {
        if (optionRepository.findByProductId(productId).size() <= 1) {
            throw new CoreException(ErrorType.INVALID_REQUEST, "옵션이 1개인 상품은 옵션을 삭제할 수 없습니다.");
        }
    }

    private Option findOptionInProduct(Long productId, Long optionId) {
        return optionRepository.findById(optionId)
            .filter(option -> option.getProduct().getId().equals(productId))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "옵션을 찾을 수 없습니다."));
    }
}
