package com.agamilabs.smartshop.model;

import java.io.Serializable;
import java.util.ArrayList;

public class InvoiceModel implements Serializable {
    String invoiceNo;
    String customerName;
    String currentDate, dueDate;
    String currency = "BDT";

    ArrayList<InvoiceItem> invoiceItemArrayList = new ArrayList<>();

    double discount;
    double deduction;
    double amount;


    /*    public InvoiceModel(String invoiceNo, String customer, String currentDate, String dueDate, double discount, double deduction, double amount) {
            this.invoiceNo = invoiceNo;
            this.customerName = customer;
            this.currentDate = currentDate;
            this.dueDate = dueDate;
            this.discount = discount;
            this.deduction = deduction;
            this.amount = amount;
        }*/
    public InvoiceModel() {
    }

    public InvoiceModel(String invoiceNo, String customerName, String currentDate, String dueDate, ArrayList<InvoiceItem> invoiceItemArrayList, double discount, double deduction, double amount) {
        this.invoiceNo = invoiceNo;
        this.customerName = customerName;
        this.currentDate = currentDate;
        this.dueDate = dueDate;
        this.invoiceItemArrayList = invoiceItemArrayList;
        this.discount = discount;
        this.deduction = deduction;
        this.amount = amount;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public ArrayList<InvoiceItem> getInvoiceItemArrayList() {
        return invoiceItemArrayList;
    }

    public void setInvoiceItemArrayList(ArrayList<InvoiceItem> invoiceItemArrayList) {
        this.invoiceItemArrayList = invoiceItemArrayList;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getDeduction() {
        return deduction;
    }

    public void setDeduction(double deduction) {
        this.deduction = deduction;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
