package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public interface ICallBackFromLotsAdapterSale {
    void mBoolean(boolean b, ArrayList<LotsModel> lotsModelArrayList, int qty, String currentSku);
}
