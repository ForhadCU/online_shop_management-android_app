package com.agamilabs.smartshop.Models;

import java.io.Serializable;

public class InvoiceItem implements Serializable {
    private String org_itemNo;
    private String item_name;
    private String item_sku;

    private double qty;
    private double unit_price;
    private double tax;
    private double item_discount;
    private double item_bill;

    public InvoiceItem() {
    }

    public InvoiceItem(String item_name, double qty, double item_bill) {
        this.item_name = item_name;
        this.qty = qty;
        this.item_bill = item_bill;
    }

    public InvoiceItem(String item_name, double qty, double unit_price, double item_bill) {
        this.item_name = item_name;
        this.qty = qty;
        this.unit_price = unit_price;
        this.item_bill = item_bill;
    }

    public double getItem_bill() {
        return item_bill;
    }

    public String getOrg_itemNo() {
        return org_itemNo;
    }

    public String getItem_name() {
        return item_name;
    }

    public String getItem_sku() {
        return item_sku;
    }

    public double getQty() {
        return qty;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public double getTax() {
        return tax;
    }

    public double getItem_discount() {
        return item_discount;
    }
}
