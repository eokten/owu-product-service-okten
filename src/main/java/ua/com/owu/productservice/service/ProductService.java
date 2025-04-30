package ua.com.owu.productservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import ua.com.owu.productservice.api.rest.model.CreateProductRequestDto;
import ua.com.owu.productservice.api.rest.model.PatchProductRequestDto;
import ua.com.owu.productservice.api.rest.model.ProductResponseDto;
import ua.com.owu.productservice.api.rest.model.UpdateProductRequestDto;
import ua.com.owu.productservice.mapper.ProductMapper;
import ua.com.owu.productservice.model.Product;
import ua.com.owu.productservice.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RefreshScope
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final UserService userService;

    @Value("${app.allowed-shop-ids}")
    private final List<String> allowedShopIds;

    public ProductResponseDto createProduct(CreateProductRequestDto createProductDto) {
        String shopId = createProductDto.getShopId();

        if (!allowedShopIds.contains(shopId)) {
            throw new ResourceAccessException("Shop id '%s' is not allowed".formatted(shopId));
        }

        if (!userService.getUserAssignedShopIds().contains(shopId)) {
            throw new ResourceAccessException("User is not assigned to this shop");
        }

        Product product = productMapper.toProduct(createProductDto);
        Product savedProduct = productRepository.save(product);
        return productMapper.toProductDto(savedProduct);
    }

    public Optional<ProductResponseDto> findProduct(String id) {
        return productRepository.findById(id).map(productMapper::toProductDto);
    }

    public List<ProductResponseDto> findAllProducts() {
        return productRepository.findAll().stream().map(productMapper::toProductDto).toList();
    }

    public List<ProductResponseDto> findAllProductsWithPriceGreaterThan(BigDecimal minPrice) {
        return productRepository.findByPriceGreaterThan(minPrice)
                .stream()
                .map(productMapper::toProductDto)
                .toList();
    }

    public Optional<ProductResponseDto> updateProduct(String id, UpdateProductRequestDto updateProductDto) {
        return productRepository.findById(id)
                .map(product -> {
                    if (!userService.getUserAssignedShopIds().contains(product.getShopId())) {
                        throw new ResourceAccessException("User is not assigned to this shop");
                    }

                    productMapper.updateProduct(product, updateProductDto);
                    return productRepository.save(product);
                })
                .map(productMapper::toProductDto);
    }

    public Optional<ProductResponseDto> patchProduct(String id, PatchProductRequestDto patchProductDto) {
        return productRepository.findById(id)
                .map(product -> {
                    if (!userService.getUserAssignedShopIds().contains(product.getShopId())) {
                        throw new ResourceAccessException("User is not assigned to this shop");
                    }

                    productMapper.patchProduct(product, patchProductDto);
                    return productRepository.save(product);
                })
                .map(productMapper::toProductDto);
    }

    public void deleteProduct(String id) {
        productRepository.findById(id)
                .ifPresent(product -> {
                    if (!userService.getUserAssignedShopIds().contains(product.getShopId())) {
                        throw new ResourceAccessException("User is not assigned to this shop");
                    }

                    productRepository.deleteById(product.getId());
                });
    }
}
