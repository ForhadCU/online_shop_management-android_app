package com.agamilabs.smartshop;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromLotsAdapterPurchase;
import com.agamilabs.smartshop.Interfaces.ProductDetailsInterfacePurchase;
import com.agamilabs.smartshop.adapter.LotsAdapterPurchase;
import com.agamilabs.smartshop.adapter.RvSummeryAdapter;
import com.agamilabs.smartshop.model.LotsModel;
import com.agamilabs.smartshop.model.PurchaseItemModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class ScanerDilogFragmentActivityPurchase extends DialogFragment implements View.OnClickListener, ICallBackFromLotsAdapterPurchase, View.OnTouchListener,
        TextView.OnEditorActionListener {
    private Button button_addProduct, button_discard;
    private ImageButton button_productQuantityIncrease, button_productQuantityDecrease;
    private RecyclerView recyclerViewSku;
    private TextView tv_productName, tv_productQuantity, tv_productPrice, tv_productTotalPrice, tv_availableStock, tv_newStock, tv_resetExpDateBtn;
    private TextView tv_totalLots, tv_stockQty, tv_stockValue;
    private EditText editTextPurchaseRate, editTextSaleRate;
    private CheckBox checkBoxContinueScanning;
    private Handler repeatUpdateHandler = new Handler();
    private static final int REP_DELAY = 50;
    private LinearLayout llScannerDialogFrag, llSummeryHeading, llInputField;
    private RecyclerView rv_selectedSkuList;
    private Calendar calendar;
    private DatePicker datePicker;
    private EditText editTextExpDate;
    private ImageView imageViewExpDateCalender;
    private CheckBox checkBoxNoExpiry;
    private SimpleDateFormat simpleDateFormat;
    private ArrayList<PurchaseItemModel> purchaseItemModelArrayList;

    private String customer;
    private String product_name;
    private String purchaseRate;
    private String itemNo;
    private ArrayList<String> item_id;
    private String unitid;
    private String expirydate;
    private String discount_percentage;
    private String saleRate;
    private String taxrate;
    private ProductDetailsInterfacePurchase productDetailsInterfacePurchase;
    private double totalPrice1;
    private double totalPrice2;
    private double totalPriceCommon = 0;
    private double discount = 0;
    private boolean continueScanning;
    private Context context;
    private ArrayList<LotsModel> lotsModelArrayList;
    private ArrayList<LotsModel> summeryArrayList;
    private LotsAdapterPurchase lotsAdapterPurchase;
    private RvSummeryAdapter rvSummeryAdapter;
    public int totalQty = 0;
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;
    private String currentSku;
    private int orgNo;
    private int lastCheckedPosition = -1;
    private int allIncreasedQty = 0;
    private int day, month, year;
    private String getCurrentExpDate;
    private LotsModel current;

    /**
     * add Lots model ArrayList
     *
     * @param context
     * @param itemNo
     * @param product_name
     * @param productRate
     * @param unitid
     * @param expirydate
     * @param discount_percentage
     */
    public ScanerDilogFragmentActivityPurchase(Context context, int orgNo, String itemNo, String product_name, String productRate, String saleRate, String unitid,
                                               String expirydate, String discount_percentage, ArrayList<LotsModel> lotsModelArrayList, ArrayList<PurchaseItemModel> purchaseItemModelArrayList, String taxrate) {
        this.context = context;
        this.product_name = product_name;
        this.purchaseRate = productRate;
        this.itemNo = itemNo;
        this.unitid = unitid;
        this.expirydate = expirydate;
        this.discount_percentage = discount_percentage;
        this.lotsModelArrayList = lotsModelArrayList;
        this.orgNo = orgNo;
        this.saleRate = saleRate;
        this.purchaseItemModelArrayList = purchaseItemModelArrayList;
        this.taxrate = taxrate;
//        totalPriceCommon = Double.parseDouble(product_price);
    }

    public ScanerDilogFragmentActivityPurchase() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        productDetailsInterfacePurchase = (ProductDetailsInterfacePurchase) getActivity();
    }

    public ScanerDilogFragmentActivityPurchase(String itemNo, String product_name, String productRate, String unitid, String expirydate, String discount_percentage, ArrayList<String> arrayListSku) {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.scaner_dialog_fragment_final_purchase, null);

        button_addProduct = view.findViewById(R.id.btn_AddProductViewDialog);
        button_discard = view.findViewById(R.id.btn_dismissProductViewDialog);
        button_productQuantityIncrease = view.findViewById(R.id.btn_quantityIncrease);
        button_productQuantityDecrease = view.findViewById(R.id.btn_quantityDecrease);
        tv_productName = view.findViewById(R.id.tv_productName);
        tv_productQuantity = view.findViewById(R.id.tv_productQuantity);
        tv_productPrice = view.findViewById(R.id.tv_productPrice);
        tv_productTotalPrice = view.findViewById(R.id.tv_productTotalPrice);
        tv_availableStock = view.findViewById(R.id.tv_availableStock);
        tv_newStock = view.findViewById(R.id.tv_newStock);
        tv_totalLots = view.findViewById(R.id.tv_totalLots);
        tv_stockQty = view.findViewById(R.id.tv_stockQty);
        tv_stockValue = view.findViewById(R.id.tv_stockValue);
        editTextPurchaseRate = view.findViewById(R.id.edtTxt_purchaseRate);
        editTextSaleRate = view.findViewById(R.id.edtTxt_saleRate);
        checkBoxContinueScanning = view.findViewById(R.id.checkbox_continueScanning);
        recyclerViewSku = view.findViewById(R.id.rv_sku);
        llScannerDialogFrag = view.findViewById(R.id.llScannerDialogFrag);
        rv_selectedSkuList = view.findViewById(R.id.rv_selectedSkuList);
        llSummeryHeading = view.findViewById(R.id.l5);
        editTextExpDate = view.findViewById(R.id.edtTxt_expDate);
        imageViewExpDateCalender = view.findViewById(R.id.imgV_expDateCalander);
        checkBoxNoExpiry = view.findViewById(R.id.checkboxNoExpiry);
        tv_resetExpDateBtn = view.findViewById(R.id.tv_resetExpDate);

//        button_productQuantityIncrease.setEnabled(false);
        editTextExpDate.setEnabled(false);
        checkBoxNoExpiry.setEnabled(false);
        imageViewExpDateCalender.setEnabled(false);
        imageViewExpDateCalender.setEnabled(false);
        editTextPurchaseRate.setSelectAllOnFocus(true);
        editTextSaleRate.setSelectAllOnFocus(true);
        calendar = Calendar.getInstance();
//        getToadyDate();
        summeryArrayList = new ArrayList<>();
        recyclerViewSku.setHasFixedSize(true);
        recyclerViewSku.setLayoutManager(new LinearLayoutManager(context));
        rv_selectedSkuList.setHasFixedSize(true);
        rv_selectedSkuList.setLayoutManager(new LinearLayoutManager(context));
        setValueMethod();
        lotsAdapterPurchase = new LotsAdapterPurchase(context, lotsModelArrayList, null, this);
        recyclerViewSku.setAdapter(lotsAdapterPurchase);
        mSetSummeryAdapter();
        simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
/*
        spinnerSku = view.findViewById(R.id.spinner_sku);
        spinnerSku.setEnabled(false);
        ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayListSku);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSku.setAdapter(ad);
*/
        button_addProduct.setOnClickListener(this);
        button_discard.setOnClickListener(this);
        button_productQuantityIncrease.setOnClickListener(this);
        button_productQuantityDecrease.setOnClickListener(this);
        editTextPurchaseRate.setOnEditorActionListener(this);
        editTextSaleRate.setOnEditorActionListener(this);
        editTextExpDate.setOnEditorActionListener(this);
        llScannerDialogFrag.setOnTouchListener(this);
        imageViewExpDateCalender.setOnClickListener(this);
        checkBoxNoExpiry.setOnClickListener(this);
        tv_resetExpDateBtn.setOnClickListener(this);

        button_productQuantityIncrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                repeatUpdateHandler.post(new RptUpdater());
                return false;
            }
        });
        button_productQuantityIncrease.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement) {
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        button_productQuantityDecrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoDecrement = true;
                repeatUpdateHandler.post(new RptUpdater());
                return false;
            }
        });
        button_productQuantityDecrease.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if ((event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement) {
                    mAutoDecrement = false;
                }
                return false;
            }
        });

        editTextPurchaseRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim()) || Double.parseDouble(s.toString().trim()) <= 0) {
                    purchaseRate = "0.00";
                    tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", Double.parseDouble(purchaseRate) * allIncreasedQty));
                    mSetSummeryAdapter();
                } else {
                    purchaseRate = (s.toString().trim());
                    tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", Double.parseDouble(purchaseRate) * allIncreasedQty));
//                    rvSummeryAdapter.notifyDataSetChanged();
                    mSetSummeryAdapter();

//                    editTextPurchaseRate.setFocusableInTouchMode(true);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        editTextSaleRate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim()) || Double.parseDouble(s.toString().trim()) <= 0) {
                    saleRate = "0.00";
                } else {
                    saleRate = (s.toString().trim());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        setCancelable(false);
        return view;
    }

    private void getForwardDate() {
        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH) + 30;
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);
    }

    private void mSetSummeryAdapter() {
        rvSummeryAdapter = new RvSummeryAdapter(context, summeryArrayList, purchaseRate);
        rv_selectedSkuList.setAdapter(rvSummeryAdapter);
        rvSummeryAdapter.notifyDataSetChanged();
    }

    private void setValueMethod() {
        tv_productName.setText(product_name);
        tv_productQuantity.setText("0");
        if (purchaseRate.equals("null"))
            purchaseRate = "0.00";
        editTextPurchaseRate.setText(String.format("%.2f", Double.parseDouble(purchaseRate)));
        purchaseRate = editTextPurchaseRate.getText().toString().trim();
        tv_productPrice.setText(String.format("\u09F3 " + "%.2f", Double.parseDouble(purchaseRate)));
        tv_productTotalPrice.setText(String.format("\u09F3 " + "%.2f", totalPriceCommon));
        if (saleRate.equals("null"))
            saleRate = "0.00";
        editTextSaleRate.setText(String.format("%.2f", Double.parseDouble(saleRate)));
        tv_totalLots.setText(String.valueOf(lotsModelArrayList.size()));
        int stockQty = mGetStockQty();
        tv_stockQty.setText(String.valueOf(stockQty));
        tv_stockValue.setText(String.format("\u09F3 " + "%.2f", Double.parseDouble(purchaseRate) * stockQty));

        boolean existedItem = false;
        if (purchaseItemModelArrayList.size() > 0) {
            for (int i = 0; i < purchaseItemModelArrayList.size(); i++) {
                PurchaseItemModel current = purchaseItemModelArrayList.get(i);
                if (current.getOrgNo() == orgNo && current.getItemNO() == Double.parseDouble(itemNo)) {
                    existedItem = true;
                    tv_productName.setText(current.getItemName());
                    purchaseRate = String.valueOf(current.getPurchaseRate());
                    editTextPurchaseRate.setText(String.format("%.2f", Double.parseDouble(purchaseRate)));
                    editTextSaleRate.setText(String.format("%.2f", current.getSaleRate()));
                    totalPriceCommon = current.getTotalAmount();
                    allIncreasedQty = current.getAllIncreaseQty();
                    tv_productTotalPrice.setText(String.format("\u09F3 " + "%.2f", totalPriceCommon));
                    lotsModelArrayList = current.getLotsModelArrayList();
                    summeryArrayList = current.getSummeryArrayList();
                    llSummeryHeading.setVisibility(View.VISIBLE);
                }

            }
        }
        if (!existedItem)
            lotsModelArrayList.add(new LotsModel(Integer.parseInt(itemNo), orgNo, -1, "sku number", 0, "null", false, true, 0));


/*        if (invoiceItemModelArrayList.size() > 0) {
            selectedSkuArrayList = new ArrayList<>();
            for (int i = 0; i < invoiceItemModelArrayList.size(); i++) {
                InvoiceItemModel current = invoiceItemModelArrayList.get(i);
                if (current.getItemno() == Integer.parseInt(itemNo)) {
                    tv_productQuantity.setText(String.valueOf(current.getQty()));
                    updatableIndex = i;
                    for (int j = 0; j < current.getItem_id().size(); j++) {
                        selectedSkuArrayList.add(current.getItem_id().get(j));
                    }
                }
            }
        }*/
//        tv_productDiscount.setText(product_discount);
//        tv_productPrice.setText(product_price);
//        Picasso.with(getContext()).load(product_imgUrl).into(imageView_productImage);
    }

    private int mGetStockQty() {
        int qty = 0;
        for (int i = 0; i < lotsModelArrayList.size(); i++) {
            LotsModel current = lotsModelArrayList.get(i);
            qty += current.getQty();
        }
        return qty;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    productDetailsInterfacePurchase.dialogDismissHandler();
                    dismiss();
                    return true;
                } else
                    return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        if (!TextUtils.isEmpty(editTextPurchaseRate.getText().toString().trim()) || Double.parseDouble(editTextPurchaseRate.getText().toString().trim()) >= 0) {
//            boolean isChecked = false;

            switch (v.getId()) {
                case R.id.btn_dismissProductViewDialog:
                    productDetailsInterfacePurchase.dialogDismissHandler();
                    dismiss();
                    break;

                case R.id.btn_AddProductViewDialog:
                    String product_name = tv_productName.getText().toString();
                    String str = tv_productTotalPrice.getText().toString();
                    double totalBill = Double.parseDouble(str.substring(2));
                    String purchase_Rate = editTextPurchaseRate.getText().toString().trim();

                    if (summeryArrayList.size() > 0) {

                        if (checkBoxContinueScanning.isChecked()) {
                            continueScanning = true;
                        } else
                            continueScanning = false;
                        productDetailsInterfacePurchase.dataParsingMethod(continueScanning, orgNo, Integer.parseInt(itemNo),
                                product_name, Double.parseDouble(purchase_Rate), Double.parseDouble(saleRate),
                                totalBill, unitid, expirydate, discount_percentage, allIncreasedQty, summeryArrayList, lotsModelArrayList, taxrate);

                        dismiss();
                    } else
                        Toast.makeText(context, "Please select at least one product", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.btn_quantityIncrease:
                    int quantityNumber1 = Integer.parseInt(tv_productQuantity.getText().toString());
                    quantityNumber1 = quantityNumber1 + 1;
//                    if (!tv_availableStock.getText().toString().equals("0") || lastCheckedPosition == lotsModelArrayList.size()-1) {
                    if (!(lastCheckedPosition == -1)) {
                        tv_newStock.setText(String.valueOf(quantityNumber1 + totalQty));
//                    int newStock = Integer.parseInt(tv_newStock.getText().toString());
                        allIncreasedQty = allIncreasedQty + 1;
                        totalPrice1 = allIncreasedQty * Double.parseDouble(purchaseRate);
                        totalPriceCommon = totalPrice1;
                        tv_productQuantity.setText(String.valueOf(quantityNumber1));
                        tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
//                        lotsModelArrayList.get(lastCheckedPosition).setDeletable(true);

                        lotsModelArrayList.get(lastCheckedPosition).setQtyIncrease(quantityNumber1);
                        if (current.getLotno() == -1) {
                            lotsModelArrayList.get(lastCheckedPosition).setLotno(lastCheckedPosition + 1);
                        }
                        if (quantityNumber1 == 1) {
                            llSummeryHeading.setVisibility(View.VISIBLE);
                            summeryArrayList.add(new LotsModel(lotsModelArrayList.get(lastCheckedPosition).getLotno(), lotsModelArrayList.get(lastCheckedPosition).getSku(), lotsModelArrayList.get(lastCheckedPosition).getQty(),
                                    lotsModelArrayList.get(lastCheckedPosition).getQtyIncrease(), lastCheckedPosition));
                        }
                        for (int i = 0; i < summeryArrayList.size(); i++) {
                            if (summeryArrayList.get(i).getLastIndexPosition() == lastCheckedPosition) {
                                summeryArrayList.get(i).setQtyIncrease(quantityNumber1);
                            }
                        }
                        lotsAdapterPurchase.notifyItemChanged(lastCheckedPosition);
                        rvSummeryAdapter.notifyDataSetChanged();
                    }/* else if (lastCheckedPosition == -1) {
                        tv_newStock.setText(String.valueOf(quantityNumber1 + totalQty));
//                    int newStock = Integer.parseInt(tv_newStock.getText().toString());
                        allIncreasedQty = allIncreasedQty + 1;
                        totalPrice1 = allIncreasedQty * Double.parseDouble(purchaseRate);
                        totalPriceCommon = totalPrice1;
                        tv_productQuantity.setText(String.valueOf(quantityNumber1));
                        tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
                    }*/ else {
                        Toast.makeText(context, "Please, select sku first.", Toast.LENGTH_SHORT).show();
                        editTextPurchaseRate.setFocusable(true);
                    }
                    break;

                case R.id.btn_quantityDecrease:
                    int quantityNumber2 = Integer.parseInt(tv_productQuantity.getText().toString());
                    quantityNumber2 = quantityNumber2 - 1;
                    if (quantityNumber2 >= 0) {
                        tv_newStock.setText(String.valueOf(quantityNumber2 + totalQty));
                        allIncreasedQty = allIncreasedQty - 1;
                        totalPrice2 = allIncreasedQty * Double.parseDouble(purchaseRate);
                        totalPriceCommon = totalPrice2;
                        tv_productQuantity.setText(String.valueOf(quantityNumber2));
                        tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
                        lotsModelArrayList.get(lastCheckedPosition).setQtyIncrease(quantityNumber2);
                        for (int i = 0; i < summeryArrayList.size(); i++) {
                            if (summeryArrayList.get(i).getLastIndexPosition() == lastCheckedPosition) {
                                summeryArrayList.get(i).setQtyIncrease(quantityNumber2);
                            }
                        }
                        if (quantityNumber2 == 0) {
                            for (int i = 0; i < summeryArrayList.size(); i++) {
                                if (summeryArrayList.get(i).getQtyIncrease() == 0) {
                                    summeryArrayList.remove(i);
//                                    rvSummeryAdapter.notifyItemRemoved(i);
//                                    rvSummeryAdapter.notifyItemRangeChanged(i, summeryArrayList.size());
//                                    rvSummeryAdapter.notifyDataSetChanged();
                                }
                            }
                            if (summeryArrayList.size() == 0)
                                llSummeryHeading.setVisibility(View.GONE);

                            if (lastCheckedPosition == lotsModelArrayList.size() - 1) {
//                                current.setSku("sku name");
                                current.setLotno(-1);
                                current.setQty(0);
                                current.setExpirydate("null");
                                current.setClickControl(true);
                                button_productQuantityIncrease.setEnabled(false);

                            }
                        }

//                    LotsModel current = lotsModelArrayList.get(lastCheckedPosition);
                        lotsAdapterPurchase.notifyItemChanged(lastCheckedPosition);
//                        lotsAdapterPurchase.notifyDataSetChanged();
                        rvSummeryAdapter.notifyDataSetChanged();

                    }
                    break;

                case R.id.imgV_expDateCalander:
                    // Get Current Date

                    DatePickerDialog datePickerDialog = null;
                    if (editTextExpDate.getText().toString().equals("-") || TextUtils.isEmpty(editTextExpDate.getText().toString()) || editTextExpDate.getText().toString().equals("0000-00-00")) {
                        /*year = calendar.get(Calendar.YEAR);
                        month = calendar.get(Calendar.MONTH);
                        day = calendar.get(Calendar.DAY_OF_MONTH);*/
                        getForwardDate();

                        datePickerDialog = new DatePickerDialog(context,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, monthOfYear);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        lotsModelArrayList.get(lastCheckedPosition).setExpirydate(simpleDateFormat.format(calendar.getTime()));
                                        editTextExpDate.setText(simpleDateFormat.format(calendar.getTime()));
                                    }
                                }, year, month, day);
                    } else {
                        year = Integer.parseInt(editTextExpDate.getText().toString().substring(0, 4));
                        month = Integer.parseInt(editTextExpDate.getText().toString().substring(5, 7));
                        day = Integer.parseInt(editTextExpDate.getText().toString().substring(8));

                        datePickerDialog = new DatePickerDialog(context,
                                new DatePickerDialog.OnDateSetListener() {
                                    @Override
                                    public void onDateSet(DatePicker view, int year,
                                                          int monthOfYear, int dayOfMonth) {
                                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                                        calendar.set(Calendar.YEAR, year);
                                        calendar.set(Calendar.MONTH, monthOfYear);
                                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                                        current.setUpdatedExpiryDate(simpleDateFormat.format(calendar.getTime()));
                                        editTextExpDate.setText(simpleDateFormat.format(calendar.getTime()));
                                    }
                                }, year, month - 1, day);
                    }

                    Objects.requireNonNull(datePickerDialog).show();

                    break;

                case R.id.checkboxNoExpiry:
                    boolean isChecked = ((CheckBox) v).isChecked();

                    if (isChecked) {
                        checkBoxNoExpiry.setTextColor(0xFF4CAF50);
                        getCurrentExpDate = editTextExpDate.getText().toString().trim();
//                        editTextExpDate.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL);
                        editTextExpDate.setText("-");
                        imageViewExpDateCalender.setEnabled(false);
                        tv_resetExpDateBtn.setEnabled(false);

                        current.setUpdatedExpiryDate(getCurrentExpDate);
                        current.setNoExpiry(true);
                        Toast.makeText(context, "Checked", Toast.LENGTH_SHORT).show();
                    } else {
                        if (current.getUpdatedExpiryDate().equals("null")) {
                            getForwardDate();
                            calendar.set(Calendar.YEAR, year);
                            calendar.set(Calendar.MONTH, month);
                            calendar.set(Calendar.DAY_OF_MONTH, day);
                            current.setUpdatedExpiryDate(simpleDateFormat.format(calendar.getTime()));
                        }
                        editTextExpDate.setText(current.getUpdatedExpiryDate());
                        imageViewExpDateCalender.setEnabled(true);
                        tv_resetExpDateBtn.setEnabled(true);
                        current.setNoExpiry(false);

                        Toast.makeText(context, "Uncheck", Toast.LENGTH_SHORT).show();
                    }
                    break;

                case R.id.tv_resetExpDate:

                    if (current.getExpirydate().equals("null")) {
                        checkBoxNoExpiry.setEnabled(true);
                        checkBoxNoExpiry.setChecked(true);
                        imageViewExpDateCalender.setEnabled(false);
                        tv_resetExpDateBtn.setEnabled(false);
                        editTextExpDate.setText("-");
                    } else {
                        checkBoxNoExpiry.setEnabled(true);
                        checkBoxNoExpiry.setChecked(false);
                        imageViewExpDateCalender.setEnabled(true);
                        tv_resetExpDateBtn.setEnabled(true);
                        editTextExpDate.setText(current.getExpirydate());
                    }
                    current.setUpdatedExpiryDate(current.getExpirydate());

            }
        } else {
            Toast.makeText(context, "Warning!! Purchase rate is Empty", Toast.LENGTH_SHORT).show();
            editTextPurchaseRate.setFocusable(true);
        }
    }

    @Override
    public void clickHandleRadioBtn(boolean b, ArrayList<LotsModel> list, int q, String sku, int lastCheckedPos) {
        if (b) {
            totalQty = q;
            button_productQuantityIncrease.setEnabled(true);
            tv_productQuantity.setText(String.valueOf(list.get(lastCheckedPos).getQtyIncrease()));
            tv_availableStock.setText(String.valueOf(totalQty));
            tv_newStock.setText(String.valueOf(totalQty + Integer.parseInt(tv_productQuantity.getText().toString())));
//        lotsModelArrayList = list;
            currentSku = sku;
//        LotsModel current =
            lastCheckedPosition = lastCheckedPos;
            lotsModelArrayList = list;
            current = lotsModelArrayList.get(lastCheckedPosition);

            if (current.getUpdatedExpiryDate() == null) {
                current.setUpdatedExpiryDate(current.getExpirydate());
            }
            if (current.getUpdatedExpiryDate().equals("null")) {
                checkBoxNoExpiry.setEnabled(true);
                checkBoxNoExpiry.setChecked(true);
                imageViewExpDateCalender.setEnabled(false);
                tv_resetExpDateBtn.setEnabled(false);
                current.setNoExpiry(true);
                editTextExpDate.setText("-");
            } else {
                if (!current.isNoExpiry()) {
                    checkBoxNoExpiry.setEnabled(true);
                    checkBoxNoExpiry.setChecked(false);
                    imageViewExpDateCalender.setEnabled(true);
                    tv_resetExpDateBtn.setEnabled(true);
                    editTextExpDate.setText(current.getUpdatedExpiryDate());
                } else {
                    checkBoxNoExpiry.setEnabled(true);
                    checkBoxNoExpiry.setChecked(true);
                    imageViewExpDateCalender.setEnabled(false);
                    tv_resetExpDateBtn.setEnabled(false);
                    editTextExpDate.setText("-");
                }
            }


        }
    }

    @Override
    public void clickHandleAddNewBtn(boolean blank, ArrayList<LotsModel> list, int pos) {

        if (!blank) {
            checkBoxNoExpiry.setEnabled(true);
            tv_productQuantity.setText("0");
            tv_availableStock.setText("0");
            tv_newStock.setText("0");
            lastCheckedPosition = pos;
            lotsModelArrayList = list;
            current = lotsModelArrayList.get(lastCheckedPosition);

            current.setUpdatedExpiryDate(current.getExpirydate());
        } else {
            checkBoxNoExpiry.setEnabled(false);
            checkBoxNoExpiry.setChecked(true);
            imageViewExpDateCalender.setEnabled(false);
            tv_resetExpDateBtn.setEnabled(false);
            editTextExpDate.setText("-");
            button_productQuantityIncrease.setEnabled(true);

            tv_productQuantity.setText("0");
            tv_newStock.setText("0");
            tv_availableStock.setText("0");
            totalQty = 0;
            lastCheckedPosition = -1;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (editTextPurchaseRate.isFocused()) {
                Rect outRect = new Rect();
                editTextPurchaseRate.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (TextUtils.isEmpty(editTextPurchaseRate.getText().toString().trim()) || Double.parseDouble(editTextPurchaseRate.getText().toString().trim()) <= 0) {
                        editTextPurchaseRate.setText("0.00");
                    }
                    editTextPurchaseRate.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            if (editTextSaleRate.isFocused()) {
                Rect outRect = new Rect();
                editTextSaleRate.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (TextUtils.isEmpty(editTextSaleRate.getText().toString().trim()) ||
                            Double.parseDouble(editTextSaleRate.getText().toString().trim()) <= 0) {
                        editTextSaleRate.setText("0.00");
                    }
                    editTextSaleRate.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            if (editTextExpDate.isFocused()) {
                Rect outRect = new Rect();
                editTextExpDate.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    if (TextUtils.isEmpty(editTextExpDate.getText().toString().trim())) {
                        editTextExpDate.setText(lotsModelArrayList.get(lastCheckedPosition).getExpirydate());
                    }
                    editTextExpDate.clearFocus();
                    InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return false;
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (TextUtils.isEmpty(editTextPurchaseRate.getText().toString().trim()) ||
                    Double.parseDouble(editTextPurchaseRate.getText().toString().trim()) <= 0) {
                editTextPurchaseRate.setText("0.00");
            } else if (TextUtils.isEmpty(editTextSaleRate.getText().toString().trim()) ||
                    Double.parseDouble(editTextSaleRate.getText().toString().trim()) <= 0) {
                editTextSaleRate.setText("0.00");
            } else if (TextUtils.isEmpty(editTextSaleRate.getText().toString().trim())) {
                editTextExpDate.setText(lotsModelArrayList.get(lastCheckedPosition).getExpirydate());
            }
            editTextExpDate.clearFocus();
            editTextSaleRate.clearFocus();
            editTextPurchaseRate.clearFocus();
            InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return true;
        } else
            return false;
    }

    class RptUpdater implements Runnable {
        public void run() {
            if (mAutoIncrement) {
                increment();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            } else if (mAutoDecrement) {
                decrement();
                repeatUpdateHandler.postDelayed(new RptUpdater(), REP_DELAY);
            }
        }
    }

    public void increment() {
        if (!TextUtils.isEmpty(editTextPurchaseRate.getText().toString().trim()) || Double.parseDouble(editTextPurchaseRate.getText().toString().trim()) >= 0) {
            int quantityNumber1 = Integer.parseInt(tv_productQuantity.getText().toString());
            quantityNumber1 = quantityNumber1 + 1;
            if (!(lastCheckedPosition == -1)) {
                tv_newStock.setText(String.valueOf(quantityNumber1 + totalQty));
                allIncreasedQty = allIncreasedQty + 1;
                totalPrice1 = allIncreasedQty * Double.parseDouble(purchaseRate);
                totalPriceCommon = totalPrice1;
                tv_productQuantity.setText(String.valueOf(quantityNumber1));
                tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));

                lotsModelArrayList.get(lastCheckedPosition).setQtyIncrease(quantityNumber1);
                lotsModelArrayList.get(lastCheckedPosition).setLotno(lastCheckedPosition + 1);

                if (quantityNumber1 == 1) {
                    llSummeryHeading.setVisibility(View.VISIBLE);
                    summeryArrayList.add(new LotsModel(lotsModelArrayList.get(lastCheckedPosition).getLotno(), lotsModelArrayList.get(lastCheckedPosition).getSku(), lotsModelArrayList.get(lastCheckedPosition).getQty(),
                            lotsModelArrayList.get(lastCheckedPosition).getQtyIncrease(), lastCheckedPosition));
                }
                for (int i = 0; i < summeryArrayList.size(); i++) {
                    if (summeryArrayList.get(i).getLastIndexPosition() == lastCheckedPosition) {
                        summeryArrayList.get(i).setQtyIncrease(quantityNumber1);
                    }
                }
                lotsAdapterPurchase.notifyItemChanged(lastCheckedPosition);
                rvSummeryAdapter.notifyDataSetChanged();

            }
        } else {
            Toast.makeText(context, "Warning!! Purchase rate is Empty", Toast.LENGTH_SHORT).show();
            editTextPurchaseRate.setFocusable(true);
        }

    }

    public void decrement() {
        if (!TextUtils.isEmpty(editTextPurchaseRate.getText().toString().trim()) || Double.parseDouble(editTextPurchaseRate.getText().toString().trim()) >= 0) {
            int quantityNumber2 = Integer.parseInt(tv_productQuantity.getText().toString());
            quantityNumber2 = quantityNumber2 - 1;
            if (quantityNumber2 >= 0) {
                tv_newStock.setText(String.valueOf(quantityNumber2 + totalQty));
                allIncreasedQty = allIncreasedQty - 1;
                totalPrice2 = allIncreasedQty * Double.parseDouble(purchaseRate);
                totalPriceCommon = totalPrice2;
                tv_productQuantity.setText(String.valueOf(quantityNumber2));
                tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
                lotsModelArrayList.get(lastCheckedPosition).setQtyIncrease(quantityNumber2);
                for (int i = 0; i < summeryArrayList.size(); i++) {
                    if (summeryArrayList.get(i).getLastIndexPosition() == lastCheckedPosition) {
                        summeryArrayList.get(i).setQtyIncrease(quantityNumber2);
                    }
                }
                if (quantityNumber2 == 0) {
                    for (int i = 0; i < summeryArrayList.size(); i++) {
                        if (summeryArrayList.get(i).getLastIndexPosition() == lastCheckedPosition) {
                            summeryArrayList.remove(i);
                        }
                    }
                    if (summeryArrayList.size() == 0)
                        llSummeryHeading.setVisibility(View.GONE);

                    if (lastCheckedPosition == lotsModelArrayList.size() - 1) {
//                                current.setSku("sku name");
                        current.setLotno(-1);
                        current.setQty(0);
                        current.setExpirydate("null");
                        current.setClickControl(true);
                        button_productQuantityIncrease.setEnabled(false);
                    }


                }
                lotsAdapterPurchase.notifyItemChanged(lastCheckedPosition);
                rvSummeryAdapter.notifyDataSetChanged();
            }

        } else {
            Toast.makeText(context, "Warning!! Purchase rate is Empty", Toast.LENGTH_SHORT).show();
            editTextPurchaseRate.setFocusable(true);
        }
    }
}
