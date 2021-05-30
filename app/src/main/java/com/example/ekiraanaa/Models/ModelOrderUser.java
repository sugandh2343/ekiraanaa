package com.example.ekiraanaa.Models;

public class ModelOrderUser {
    String  orderId,orderTime,orderStatus,orderCost,orderBy,orderItemQty;


    public ModelOrderUser() {

    }

    public ModelOrderUser(String orderId, String orderTime, String orderStatus, String orderCost, String orderBy, String orderItemQty) {
        this.orderId = orderId;
        this.orderTime = orderTime;
        this.orderStatus = orderStatus;
        this.orderCost = orderCost;
        this.orderBy = orderBy;
        this.orderItemQty = orderItemQty;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderCost() {
        return orderCost;
    }

    public void setOrderCost(String orderCost) {
        this.orderCost = orderCost;
    }

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public String getOrderItemQty() {
        return orderItemQty;
    }

    public void setOrderItemQty(String orderItemQty) {
        this.orderItemQty = orderItemQty;
    }
}
