package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.PurchaseItemModel;

import java.util.ArrayList;

public interface ICallbackForBillDetails {
//    void dataParsedBillDetails(int orgNo, int itemNO, String itemName, double purchaseRate, double totalAmount, String sku, int qty);
    void handleBottomSheet();
    void dataParsedBillDetails(ArrayList<PurchaseItemModel> purchaseItemModelArrayList);
}
