package ua.com.owu.productservice.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import ua.com.owu.productservice.dto.CreateProductDto;
import ua.com.owu.productservice.dto.PatchProductDto;
import ua.com.owu.productservice.dto.ProductDto;
import ua.com.owu.productservice.dto.UpdateProductDto;
import ua.com.owu.productservice.model.Product;
import ua.com.owu.productservice.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ProductDto createProduct(@RequestBody @Valid CreateProductDto createProductDto) {
        return productService.createProduct(createProductDto);
    }

    @PutMapping("/{productId}/image")
    public ResponseEntity<?> uploadProductImage(
            @PathVariable("productId") String productId,
            @RequestParam("file") MultipartFile file
    ) {
        productService.uploadProductImage(productId, file);
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable("productId") String productId) {
        return ResponseEntity.of(productService.findProduct(productId));
    }

    @GetMapping
    public List<Product> getProducts() {
        return productService.findAllProducts();
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable("productId") String productId, @RequestBody @Valid UpdateProductDto updateProductDto) {
        return ResponseEntity.of(productService.updateProduct(productId, updateProductDto));
    }

    @PatchMapping("/{productId}")
    public ResponseEntity<ProductDto> patchProduct(@PathVariable("productId") String productId, @RequestBody @Valid PatchProductDto patchProductDto) {
        return ResponseEntity.of(productService.patchProduct(productId, patchProductDto));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ProductDto> deleteProduct(@PathVariable("productId") String productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
