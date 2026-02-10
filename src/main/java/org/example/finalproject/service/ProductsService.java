package org.example.finalproject.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.finalproject.dto.ProductCreateDto;
import org.example.finalproject.dto.ProductUpdateDto;
import org.example.finalproject.dto.ProductResponseDto;
import org.example.finalproject.entity.Category;
import org.example.finalproject.entity.Products;
import org.example.finalproject.enums.UserRole;
import org.example.finalproject.exception.ProductInUseException;
import org.example.finalproject.exception.InvalidCategoryOperationException;
import org.example.finalproject.exception.NotFoundException;
import org.example.finalproject.mapper.ProductsMapper;
import org.example.finalproject.repository.AddressRepository;
import org.example.finalproject.repository.CategoryRepository;
import org.example.finalproject.repository.ProductsRepository;
import org.example.finalproject.repository.UsersRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class ProductsService {

    private final ProductsRepository productRepository;
    private final ProductsMapper productsMapper;
    private final CategoryRepository categoryRepository;
    private final UsersRepository usersRepository;
    private final AddressRepository addressRepository;

    public Page<ProductResponseDto> getProducts(Pageable pageable) {
        Page<Products> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productsMapper::toDto);
    }

    public ProductResponseDto getProduct(Long id) {
        var product = productRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Product not found with id: {}", id);
                    return new NotFoundException("Product not found!");
                });

        var user = product.getOwner();

        var address = addressRepository.findByUser(user)
                .orElseThrow(() -> {
                    log.error("Address not found for user with ID {}", user.getId());
                    return new NotFoundException("Address not found!");
                });

        return productsMapper.toDto(product, address);
    }

    public Page<ProductResponseDto> getProductsByCategory(Long categoryId, Pageable pageable) {
        return productRepository.findByCategoryId(categoryId, pageable)
                .map(productsMapper::toDto);
    }

    public Page<ProductResponseDto> getOwnerProducts(Long ownerId, Pageable pageable) {

        Page<Products> products = productRepository.findByOwnerId(ownerId, pageable);

        if (products.isEmpty()) {
            log.warn("Owner has no products: {}", ownerId);
        }

        return products.map(productsMapper::toDto);
    }

    public ProductCreateDto addProduct(ProductCreateDto createDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var owner = usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("Owner not found!"));

        Category category = categoryRepository.findById(createDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found with id: {}", createDto.getCategoryId());
                    return new NotFoundException("Category not found");
                });

        if (category.getParent() == null) {
            log.error("Product cannot be added to a parent category.");
            throw new InvalidCategoryOperationException("Product cannot be added to a parent category.");
        }

        Products products = productsMapper.toEntity(createDto, owner, category);

        productRepository.save(products);

        return productsMapper.toDtoCreate(products);
    }

    public ProductUpdateDto editProduct(ProductUpdateDto requestDto) {
        String currentEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        usersRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productRepository.findById(requestDto.getProductId())
                .orElseThrow(() -> {
                    log.error("Product not found!");
                    return new NotFoundException("Product not found!");
                });

        if(!product.getOwner().getEmail().equals(currentEmail)) {
            throw new ProductInUseException("You can only edit your own products.");
        }

        var category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> {
                    log.error("Category not found: {}", requestDto.getCategoryId());
                    return new  NotFoundException("Category not found!");
                });

        if (requestDto.getCategoryId() != null) {
            product.setCategory(category);
        }

        if (requestDto.getName() != null) {
            product.setName(requestDto.getName());
        }

        if (requestDto.getDescription() != null) {
            product.setDescription(requestDto.getDescription());
        }

        if (requestDto.getPrice() != null) {
            product.setPrice(requestDto.getPrice());
        }

        productRepository.save(product);

        return productsMapper.toDtoUpdate(product);
    }

    public String deleteProduct(Long id) {
        String vendorEmail = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        var user = usersRepository.findByEmail(vendorEmail)
                .orElseThrow(() -> new NotFoundException("User not found!"));

        var product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found!"));

        if (
                user.getUserRole() != UserRole.ADMIN &&
                !product.getOwner().getId().equals(user.getId())) {
            throw new ProductInUseException("You are not allowed to delete this product!");
        }

        if(product.getIsAvailable() == false) {
            log.error("An attempt was made to delete the ordered product.");
            throw new ProductInUseException("Product is in use and cannot be deleted.");
        }

        productRepository.delete(product);

        log.info("Product deleted successfully. \nId: {}\nProduct Name: {}", id, product.getName());
        return "Product deleted successfully.";
    }
}
