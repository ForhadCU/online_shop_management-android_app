package com.agamilabs.smartshop.Interfaces;

public interface ProductDetailsInterface {
    void dataParsingMethod(boolean continueScanning, String productID, String productName, String productQuantity, String product_price, String totalBill,
                           String item_id,String unitid,String expirydate,String discount_percentage);

    void dialogDismissHandler();
}
