package com.agamilabs.smartshop.Interfaces;

public interface ICallbackCustomerSearchClickHandler {
    void customerClickHandler(String id, String name);
    void productClickHandler(String name, String id, int productPageNo);
}
