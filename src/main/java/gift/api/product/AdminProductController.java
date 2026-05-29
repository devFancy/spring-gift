package gift.api.product;

import gift.application.category.CategoryService;
import gift.application.product.ProductService;
import gift.domain.product.Product;
import gift.support.error.CoreException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

// record 매개변수는 @ModelAttribute 명시 없이도 Spring MVC 가 단일/객체 타입을 추론해 폼 파라미터를 필드별로 자동 binding 한다.
@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public AdminProductController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("products", productService.findAll());
        return "product/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categories", categoryService.findAll());
        return "product/new";
    }

    @PostMapping
    public String create(ProductDto.Request request, Model model) {
        try {
            productService.create(request.name(), request.price(), request.imageUrl(), request.categoryId(), true);
        } catch (CoreException e) {
            populateNewForm(model, List.of(e.getMessage()), request);
            return "product/new";
        }
        return "redirect:/admin/products";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        Product product = productService.findById(id);
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.findAll());
        return "product/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id, ProductDto.Request request, Model model) {
        try {
            productService.update(id, request.name(), request.price(), request.imageUrl(), request.categoryId(), true);
        } catch (CoreException e) {
            Product product = productService.findById(id);
            populateEditForm(model, product, List.of(e.getMessage()), request);
            return "product/edit";
        }
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        productService.delete(id);
        return "redirect:/admin/products";
    }

    private void populateNewForm(Model model, List<String> errors, ProductDto.Request request) {
        model.addAttribute("errors", errors);
        model.addAttribute("name", request.name());
        model.addAttribute("price", request.price());
        model.addAttribute("imageUrl", request.imageUrl());
        model.addAttribute("categoryId", request.categoryId());
        model.addAttribute("categories", categoryService.findAll());
    }

    private void populateEditForm(Model model, Product product, List<String> errors, ProductDto.Request request) {
        model.addAttribute("errors", errors);
        model.addAttribute("product", product);
        model.addAttribute("name", request.name());
        model.addAttribute("price", request.price());
        model.addAttribute("imageUrl", request.imageUrl());
        model.addAttribute("categoryId", request.categoryId());
        model.addAttribute("categories", categoryService.findAll());
    }
}
