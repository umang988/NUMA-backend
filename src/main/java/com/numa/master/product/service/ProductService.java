package com.numa.master.product.service;

import com.numa.master.product.entity.Product;
import org.springframework.data.domain.Page;

public interface ProductService {

    Product getProductById(Long id);
    Page<Product> getAllProducts(int page, int size, String searchQuery, String sortBy, String sortOrder);
    Product updateProduct(Long id, Product product);
    Product createProduct(Product product);
    void deleteProduct(Long id);
}