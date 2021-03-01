package com.agamilabs.smartshop.model;

public class Products {
    private String id;
    private String sku;
    private String lot_num;
    private String item_name;
    private String qty;


    public Products() {
    }

    public Products(String id, String sku, String lot_num, String item_name, String qty) {
        this.id = id;
        this.sku = sku;
        this.lot_num = lot_num;
        this.item_name = item_name;
        this.qty = qty;
    }

    public String getId() {
        return id;
    }

    public String getSku() {
        return sku;
    }

    public String getLot_num() {
        return lot_num;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getQty() {
        return qty;
    }
}
