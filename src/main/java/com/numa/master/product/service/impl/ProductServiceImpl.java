package com.numa.master.product.service.impl;

import com.numa.generic.GenericSpecification;
import com.numa.master.product.dao.ProductRepository;
import com.numa.master.product.entity.Product;
import com.numa.master.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private GenericSpecification genericSpecification;



    @Override
    public Product getProductById(Long id) {
        return genericSpecification.getEntityById(id, productRepository, "Product");
    }

    @Override
    public Page<Product> getAllProducts(int page, int size, String searchQuery, String sortBy, String sortOrder) {
        return genericSpecification.getAllEntities(Product.class, productRepository, page, size, searchQuery, sortBy, sortOrder);
    }

    @Override
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    @Override
    public Product updateProduct(Long id, Product product) {
        return genericSpecification.saveOrUpdateEntityWithResponse(id, product, new Product(), productRepository);
    }

    @Override
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}