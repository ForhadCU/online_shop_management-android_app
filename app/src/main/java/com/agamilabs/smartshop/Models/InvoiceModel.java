package com.agamilabs.smartshop.Models;

import java.io.Serializable;
import java.util.ArrayList;

public class InvoiceModel implements Serializable{
    String invoiceNo;
    String customer;
    String currentDate, dueDate;
    String currency = "BDT";

    ArrayList<InvoiceItem> items = new ArrayList<>();

    double discount;
    double deduction;


}
