package com.yozhikovd.nexus.config;

import com.yozhikovd.nexus.dao.ProductDao;
import com.yozhikovd.nexus.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.math.BigDecimal;

@Component
public class InitDataBase {

    @Autowired
    ProductDao productDao;

    @PostConstruct
    public void initDataBase() {
        productDao.save(new Product("Кекс", new BigDecimal(50)));
        productDao.save(new Product("Мочевой торт", new BigDecimal(100)));
        productDao.save(new Product("Отрыжка запеченная", new BigDecimal(400)));
        productDao.save(new Product("Пирожок кошачья радость", new BigDecimal(35)));
        productDao.save(new Product("Защеканка", new BigDecimal(1000000)));
        productDao.save(new Product("Подзалупный творожок", new BigDecimal(0.15)));
    }

    @PreDestroy
    public void destroyDataBase(){
        productDao.deleteAll();
    }
}
