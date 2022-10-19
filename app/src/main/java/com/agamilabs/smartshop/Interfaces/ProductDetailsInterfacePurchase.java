package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.PurchaseItemModel;
//import com.google.android.gms.common.data.SingleRefDataBufferIterator;

import java.util.ArrayList;

public interface ProductDetailsInterfacePurchase {
    void dataParsingMethod(boolean continueScanning, int orgNo, int itemNO, String itemName, double purchaseRate, double saleRate,
                           double totalAmount, String unitId,
                           String expiryDate, String discountPercentage, int totalIncreaseQty, ArrayList<LotsModel> SummeryArrayList, ArrayList<LotsModel> lotsModelArrayList, String taxrate);

    void dialogDismissHandler();
}
