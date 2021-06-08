package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public interface ICallbackClickHandler {
    void customerClickHandler(String id, String name);
    void productClickHandler(String name, String id);
    void catClickHandler(String id, String name);
    void dataParsedStockLots(String itemName, ArrayList<LotsModel> lotsModelArrayList);
    void handleBottomSheet();

}
