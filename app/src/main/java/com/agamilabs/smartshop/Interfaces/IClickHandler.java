package com.agamilabs.smartshop.Interfaces;

public interface IClickHandler {
//    void mProductClick(String name, String id);
    void mCatClick(String name, String id, String pId);
    void mBrandClick(String name, String id);
    void mGenericClick(String name, String id);
}
