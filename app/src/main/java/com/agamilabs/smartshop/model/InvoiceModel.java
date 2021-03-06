package com.agamilabs.smartshop.model;

import java.io.Serializable;

public class InvoiceModel implements Serializable {
    int customerNo;
    String sDate;
    String dueDate;
    int currency;
    int statusId;
    int orgNo;
    int invoiceNo;
    double discount;
    double paid;
    int addedBy;
    double deduction;
    double amount;
    String cShortText;
    String cText;
    String customerName;
    double totalAmountToPay;
    double unPaidAmount;

//    ArrayList<InvoiceItem> invoiceItemArrayList = new ArrayList<>();




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

    public InvoiceModel(String customerName) {
        this.customerName = customerName;
    }

    public InvoiceModel(int customerNo, String sDate, String dueDate, int currency, int statusId, int orgNo, int invoiceNo, double discount, double paid, int addedBy, double deduction, double amount, String cShortText, String cText, String customerName, double totalAmountToPay, double unPaidAmount) {
        this.customerNo = customerNo;
        this.sDate = sDate;
        this.dueDate = dueDate;
        this.currency = currency;
        this.statusId = statusId;
        this.orgNo = orgNo;
        this.invoiceNo = invoiceNo;
        this.discount = discount;
        this.paid = paid;
        this.addedBy = addedBy;
        this.deduction = deduction;
        this.amount = amount;
        this.cShortText = cShortText;
        this.cText = cText;
        this.customerName = customerName;
        this.totalAmountToPay = totalAmountToPay;
        this.unPaidAmount = unPaidAmount;
    }

    public int getCustomerNo() {
        return customerNo;
    }

    public String getsDate() {
        return sDate;
    }

    public String getDueDate() {
        return dueDate;
    }

    public int getCurrency() {
        return currency;
    }

    public int getStatusId() {
        return statusId;
    }

    public int getOrgNo() {
        return orgNo;
    }

    public int getInvoiceNo() {
        return invoiceNo;
    }

    public double getDiscount() {
        return discount;
    }

    public double getPaid() {
        return paid;
    }

    public int getAddedBy() {
        return addedBy;
    }

    public double getDeduction() {
        return deduction;
    }

    public double getAmount() {
        return amount;
    }

    public String getcShortText() {
        return cShortText;
    }

    public String getcText() {
        return cText;
    }

    public String getCustomerName() {
        return customerName;
    }

    public double getTotalAmountToPay() {
        return totalAmountToPay;
    }

    public double getUnPaidAmount() {
        return unPaidAmount;
    }
}
