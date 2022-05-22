package com.yozhikovd.nexus.dao;

import com.yozhikovd.nexus.models.Product;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductDao extends CrudRepository<Product,Long> {
}
