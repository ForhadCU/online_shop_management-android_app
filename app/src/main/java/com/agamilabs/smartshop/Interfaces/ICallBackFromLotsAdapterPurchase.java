package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public interface ICallBackFromLotsAdapterPurchase {
    void clickHandleRadioBtn(boolean b, ArrayList<LotsModel> lotsModelArrayList, int qty, String currentSku, int lastClickedPosition);
    void clickHandleAddNewBtn(boolean blank, ArrayList<LotsModel> lotsModelArrayList, int lastClickPosition);

}
