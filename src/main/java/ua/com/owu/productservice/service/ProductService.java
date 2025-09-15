package ua.com.owu.productservice.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.Binary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ua.com.owu.productservice.dto.CreateProductDto;
import ua.com.owu.productservice.dto.PatchProductDto;
import ua.com.owu.productservice.dto.ProductDto;
import ua.com.owu.productservice.dto.SendEmailDto;
import ua.com.owu.productservice.dto.UpdateProductDto;
import ua.com.owu.productservice.mapper.ProductMapper;
import ua.com.owu.productservice.model.Product;
import ua.com.owu.productservice.repository.ProductRepository;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    private final ProductMapper productMapper;

    private final EmailService emailService;

    public ProductDto createProduct(CreateProductDto createProductDto) {
        Product product = productMapper.toProduct(createProductDto);
        Product savedProduct = productRepository.save(product);
        emailService.sendEmailWithTemplate(SendEmailDto.builder()
                .to(createProductDto.ownerEmail())
                .subject("Product Created")
                .templateName("product-created")
                .contextData(Map.of(
                        "productName", createProductDto.name(),
                        "productPrice", createProductDto.price()
                )).build());
        return productMapper.toProductDto(savedProduct);
    }

    public Optional<ProductDto> findProduct(String id) {
        return productRepository.findById(id).map(productMapper::toProductDto);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public Optional<ProductDto> updateProduct(String id, UpdateProductDto updateProductDto) {
        return productRepository.findById(id)
                .map(product -> {
                    productMapper.updateProduct(product, updateProductDto);
                    return productRepository.save(product);
                })
                .map(productMapper::toProductDto);
    }

    public Optional<ProductDto> patchProduct(String id, PatchProductDto patchProductDto) {
        return productRepository.findById(id)
                .map(product -> {
                    productMapper.patchProduct(product, patchProductDto);
                    return productRepository.save(product);
                })
                .map(productMapper::toProductDto);
    }

    public void deleteProduct(String id) {
        productRepository.deleteById(id);
    }

    public void uploadProductImage(String productId, MultipartFile file) {
        productRepository.findById(productId)
                .ifPresent(product -> {
                    try {
                        product.setImage(new Binary(file.getBytes()));
                        emailService.sendEmailWithTemplate(SendEmailDto.builder()
                                .to(product.getOwnerEmail())
                                .subject("Product Image Uploaded")
                                .templateName("product-image-uploaded")
                                .contextData(Map.of(
                                        "productName", product.getName(),
                                        "attachment", file.getBytes()
                                )).build());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    productRepository.save(product);
                });
    }
}
