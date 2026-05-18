package gift.application.option;

import gift.api.validator.option.OptionNameFormatValidator;
import gift.domain.option.policy.OptionPolicy;
import gift.storage.option.Option;
import gift.storage.option.OptionRepository;
import gift.storage.product.Product;
import gift.storage.product.ProductRepository;
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
        OptionNameFormatValidator.validate(name);
        Product product = findProduct(productId);
        OptionPolicy.ensureNameNotDuplicated(optionRepository.existsByProductIdAndName(productId, name));
        return optionRepository.save(new Option(product, name, quantity));
    }

    @Transactional
    public void remove(Long productId, Long optionId) {
        ensureProductExists(productId);
        OptionPolicy.ensureNotLastOption(optionRepository.findByProductId(productId).size());
        Option option = findOptionInProduct(productId, optionId);
        optionRepository.delete(option);
    }

    private Product findProduct(Long productId) {
        return productRepository.findById(productId)
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다."));
    }

    private void ensureProductExists(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CoreException(ErrorType.NOT_FOUND, "상품을 찾을 수 없습니다.");
        }
    }

    private Option findOptionInProduct(Long productId, Long optionId) {
        return optionRepository.findById(optionId)
            .filter(option -> option.getProduct().getId().equals(productId))
            .orElseThrow(() -> new CoreException(ErrorType.NOT_FOUND, "옵션을 찾을 수 없습니다."));
    }
}
