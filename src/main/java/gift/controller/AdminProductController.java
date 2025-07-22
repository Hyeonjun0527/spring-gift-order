package gift.controller;

import gift.dto.ProductRequestDto;
import gift.dto.ProductResponseDto;
import gift.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/products")
public class AdminProductController {

    private final ProductService productService;

    public AdminProductController(ProductService productService) {
        this.productService = productService;
    }

    //목록 화면
    @GetMapping
    public String getProducts(Pageable pageable, Model model) {
        Page<ProductResponseDto> products = productService.getProducts(pageable);
        model.addAttribute("products", products);
        return "admin/products";            // templates/admin/products.html
    }

    //등록 화면
    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("form", new ProductRequestDto());
        return "admin/product-form";
    }

    @PostMapping("/new")
    public String createProduct(@ModelAttribute("form") @Valid ProductRequestDto requestDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        productService.createProduct(requestDto);
        return "redirect:/admin/products";
    }


    //수정 화면
    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        ProductResponseDto responseDto = productService.getProduct(id);

        ProductRequestDto requestDto = new ProductRequestDto();
        requestDto.setName(responseDto.getName());
        requestDto.setPrice(responseDto.getPrice());
        requestDto.setImageUrl(responseDto.getImageUrl());
        model.addAttribute("form", requestDto);
        model.addAttribute("productId", id);
        return "admin/product-form";
    }

    @PostMapping("/{id}/edit")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("form") @Valid ProductRequestDto requestDto,
                                BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "admin/product-form";
        }

        productService.updateProduct(id, requestDto);
        return "redirect:/admin/products";
    }

    @PostMapping("/{id}/delete")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return "redirect:/admin/products";
    }

}
