package com.agamilabs.smartshop.Interfaces;

import java.util.ArrayList;

public interface ProductDetailsInterfaceSale {
    void dataParsingMethod(boolean continueScanning, int orgNo, String productID, String productName, String productQuantity, String product_price, String totalBill,
                           ArrayList<String> item_id, String unitid, String expirydate, String discount_percentage);

    void dialogDismissHandler();
}
