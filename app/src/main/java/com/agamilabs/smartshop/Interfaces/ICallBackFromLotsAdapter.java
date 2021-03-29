package com.agamilabs.smartshop.Interfaces;

import com.agamilabs.smartshop.adapter.LotsAdapter;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public interface ICallBackFromLotsAdapter {
    void mBoolean(boolean b, ArrayList<LotsModel> lotsModelArrayList, int qty);
}
