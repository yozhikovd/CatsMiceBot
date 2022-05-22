package com.yozhikovd.nexus.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "orders")
public class Order implements Serializable {

    @Id
    @GeneratedValue
    private Long id;
    private String product;
    private String customerName;
    private String mobileNumberCustomer;
    private String orderStatus;
    private String customerTelegramId;

    public Order(String product, String customerName, String orderStatus, String customerTelegramId) {
        this.product = product;
        this.customerName = customerName;
        this.orderStatus = orderStatus;
        this.customerTelegramId = customerTelegramId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(product, order.product) && Objects.equals(customerName, order.customerName) && Objects.equals(mobileNumberCustomer, order.mobileNumberCustomer) && Objects.equals(orderStatus, order.orderStatus) && Objects.equals(customerTelegramId, order.customerTelegramId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, product, customerName, mobileNumberCustomer, orderStatus, customerTelegramId);
    }

    @Override
    public String toString() {
        return "№" + id + "| Позиция - " + product + "| TG покупателя - " + customerName + "| Статус заказа - " + orderStatus;
    }
}
