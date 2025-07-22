package gift.controller.view;

import gift.dto.ProductRequestDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/admin/products")
public class ProductViewController {

    private final ProductService productService;

    public ProductViewController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ModelAndView list(
            @PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Map<String, Object> model = new HashMap<>();
        model.put("products", productService.getAllProducts(pageable));
        return new ModelAndView("product/list", model);
    }

    @GetMapping("/new")
    public ModelAndView createForm() {
        Map<String, Object> model = new HashMap<>();
        model.put("product", new ProductRequestDto(null, null, null, null));
        return new ModelAndView("product/create", model);
    }

    @PostMapping
    public ModelAndView create(
            @Valid @ModelAttribute("product") ProductRequestDto requestDto,
            BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("product/create");
            mav.addObject("product", requestDto);
            return mav;
        }

        productService.create(requestDto);
        return new ModelAndView("redirect:/admin/products");
    }

    @GetMapping("/{id}")
    public ModelAndView detail(@PathVariable Long id) {
        Map<String, Object> model = new HashMap<>();
        model.put("product", productService.find(id));
        return new ModelAndView("product/detail", model);
    }

    @GetMapping("/edit/{id}")
    public ModelAndView editForm(@PathVariable Long id) {
        Map<String, Object> model = new HashMap<>();
        model.put("product", productService.find(id));
        return new ModelAndView("product/edit", model);
    }

    @PostMapping("/{id}")
    public ModelAndView update(@PathVariable Long id,
                               @Valid @ModelAttribute("product") ProductRequestDto requestDto,
                               BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("product/edit");
            mav.addObject("product", requestDto);
            return mav;
        }
        productService.update(id, requestDto);
        return new ModelAndView("redirect:/admin/products");
    }

    @PostMapping("/{id}/delete")
    public ModelAndView delete(@PathVariable Long id) {
        productService.delete(id);
        return new ModelAndView("redirect:/admin/products");
    }
}