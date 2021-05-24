package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public interface ICallBackFromFullScannerActivityPurchase {
    public void mCallbackQty();
    void mCallBackEditItem(int orgno, int itemNo, String product_name, double productRate,
                           double saleRate, String unitid, String expirydate, String discount_percentage, ArrayList<LotsModel> arrayListSku, String taxrate);

}
