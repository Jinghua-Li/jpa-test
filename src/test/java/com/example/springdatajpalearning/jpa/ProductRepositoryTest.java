package com.example.springdatajpalearning.jpa;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {
    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    public void initData() {
        Product product = new Product();
        product.setPrice(2.0d);
        product.setName("iphone");

        Product product1 = new Product();
        product1.setPrice(3.0d);
        product1.setName("iphone max");

        Product product2 = new Product();
        product2.setPrice(3.0d);
        product2.setName("ipad");

        Product product3 = new Product();
        product3.setPrice(1.0d);
        product3.setName("max");

        productRepository.saveAll(Arrays.asList(product, product1, product2, product3));
    }

    @Test
    void should_sorted_by_name_and_price() {
        Sort.TypedSort<Product> person = Sort.sort(Product.class);

        //先使用price正序排列，再使用name倒叙
        Sort sort = person.by(Product::getPrice).ascending()
                .and(person.by(Product::getName).descending());
        final List<Product> products = new ArrayList<>();
        productRepository.findAll(sort).forEach(product -> products.add(product));

        assertEquals(4, products.size());
        assertEquals("max", products.get(0).getName());
        assertEquals(1.0, products.get(0).getPrice());
        assertEquals("ipad", products.get(3).getName());
    }

    @Test
    void should_paged_and_sorted() {
        Sort.TypedSort<Product> person = Sort.sort(Product.class);
        Sort sort = person.by(Product::getPrice).ascending();
        final Page<Product> pagedProducts = productRepository.findAll(PageRequest.of(0, 2, sort));

        assertEquals(4, pagedProducts.getTotalElements());
        assertEquals(2, pagedProducts.getTotalPages());
        assertEquals(2, pagedProducts.getContent().size());
        assertEquals("max", pagedProducts.getContent().get(0).getName());
    }
}
