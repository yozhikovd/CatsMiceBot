package com.yozhikovd.nexus.dao;

import com.yozhikovd.nexus.models.Order;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDao extends CrudRepository<Order,Long> {
}
