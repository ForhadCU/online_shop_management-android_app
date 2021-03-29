package com.agamilabs.smartshop;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agamilabs.smartshop.Interfaces.ICallBackFromLotsAdapter;
import com.agamilabs.smartshop.Interfaces.ProductDetailsInterface;
import com.agamilabs.smartshop.adapter.LotsAdapter;
import com.agamilabs.smartshop.model.InvoiceModel;
import com.agamilabs.smartshop.model.LotsModel;

import java.util.ArrayList;

public class ScanerDilogFragmentActivity extends DialogFragment implements View.OnClickListener, ICallBackFromLotsAdapter {
    private Button button_addProduct, button_discard;
    private ImageButton button_productQuantityIncrease, button_productQuantityDecrease, button_productDiscountIncrease, button_productDiscountDecrease;
    private ImageView imageView_productImage;
    private RecyclerView recyclerViewSku;
    private InvoiceModel invoiceModel;
    private TextView tv_productName, tv_productQuantity, tv_productPrice, tv_productTotalPrice, tv_availableStock;
    private EditText editText_singleProdDiscount;
    private CheckBox checkBoxContinueScanning;
    private Spinner spinnerSku;
    private Handler repeatUpdateHandler = new Handler();
    public int mValue;
    private static final  int REP_DELAY = 50;

    private String customer;
    private String product_name;
    private String product_price;
    private String itemNo;
    private ArrayList<String> item_id;
    private String unitid;
    private String expirydate;
    private String discount_percentage;
    private ProductDetailsInterface productDetailsInterface;
    private double totalPrice1;
    private double totalPrice2;
    private double totalPriceCommon;
    private double discount = 0;
    private boolean continueScanning;
    private ArrayAdapter ad;
    private Context context;
    private ArrayList<LotsModel> lotsModelArrayList;
    private LotsAdapter lotsAdapter;
    public int totalQty = 0;
    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;

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
    public ScanerDilogFragmentActivity(Context context, String itemNo, String product_name, String productRate, String unitid, String expirydate, String discount_percentage, ArrayList<LotsModel> lotsModelArrayList) {
        this.customer = customer;
        this.context = context;
        this.product_name = product_name;
        this.product_price = productRate;
        this.itemNo = itemNo;
        this.unitid = unitid;
        this.expirydate = expirydate;
        this.discount_percentage = discount_percentage;
        this.lotsModelArrayList = lotsModelArrayList;
        totalPriceCommon = Double.parseDouble(product_price);
    }

    public ScanerDilogFragmentActivity() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        productDetailsInterface = (ProductDetailsInterface) getActivity();
    }

    public ScanerDilogFragmentActivity(String itemNo, String product_name, String productRate, String unitid, String expirydate, String discount_percentage, ArrayList<String> arrayListSku) {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.scaner_dialog_fragment_final, null);

        button_addProduct = view.findViewById(R.id.btn_AddProductViewDialog);
        button_discard = view.findViewById(R.id.btn_dismissProductViewDialog);
        button_productQuantityIncrease = view.findViewById(R.id.btn_quantityIncrease);
        button_productQuantityDecrease = view.findViewById(R.id.btn_quantityDecrease);
        tv_productName = view.findViewById(R.id.tv_productName);
        tv_productQuantity = view.findViewById(R.id.tv_productQuantity);
        tv_productPrice = view.findViewById(R.id.tv_productPrice);
        tv_productTotalPrice = view.findViewById(R.id.tv_productTotalPrice);
        imageView_productImage = view.findViewById(R.id.imgV_productImage);
        tv_availableStock = view.findViewById(R.id.tv_availableStock);
//        editText_singleProdDiscount = view.findViewById(R.id.edtTxt_singleProductDiscount);
        checkBoxContinueScanning = view.findViewById(R.id.checkbox_continueScanning);
        recyclerViewSku = view.findViewById(R.id.rv_sku);


        recyclerViewSku.setHasFixedSize(true);
        recyclerViewSku.setLayoutManager(new LinearLayoutManager(context));

        lotsAdapter = new LotsAdapter(context, lotsModelArrayList, this);
        recyclerViewSku.setAdapter(lotsAdapter);
/*
        spinnerSku = view.findViewById(R.id.spinner_sku);

        spinnerSku.setEnabled(false);
        ad = new ArrayAdapter(context, android.R.layout.simple_spinner_item, arrayListSku);
        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSku.setAdapter(ad);
*/


        setValueMethod();

        button_addProduct.setOnClickListener(this);
        button_discard.setOnClickListener(this);
        button_productQuantityIncrease.setOnClickListener(this);
        button_productQuantityDecrease.setOnClickListener(this);
//        spinnerSku.setOnItemSelectedListener(this);
/*        button_productDiscountIncrease.setOnClickListener(this);
        button_productDiscountDecrease.setOnClickListener(this);*/

//        discountHandler();

        button_productQuantityIncrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoIncrement = true;
                repeatUpdateHandler.post( new RptUpdater() );
                return false;
            }
        });
        button_productQuantityIncrease.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                }
                return false;
            }
        });

        button_productQuantityDecrease.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mAutoDecrement = true;
                repeatUpdateHandler.post( new RptUpdater() );
                return false;
            }
        });
        button_productQuantityDecrease.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                }
                return false;
            }
        });

        setCancelable(false);
        return view;

    }

    private void discountHandler() {
        editText_singleProdDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s.toString().trim())) {
                    discount = 0;
                    tv_productTotalPrice.setText("\u09F3 " + String.valueOf(String.format("%.2f", totalPriceCommon)));
                } else {
                    discount = Double.parseDouble(s.toString());
                    double temp = totalPriceCommon - discount;
                    tv_productTotalPrice.setText("\u09F3 " + String.valueOf(String.format("%.2f", temp)));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

//    @Override
//    public void onDismiss(@NonNull DialogInterface dialog) {
//        super.onDismiss(dialog);
//        getActivity().finish();
//    }

    private void setValueMethod() {
        tv_productName.setText(product_name);
//        tv_productQuantity.setText(String.valueOf(totalQty));
        tv_productQuantity.setText("1");
//        tv_availableStock.setText(String.valueOf(totalQty));
        tv_productPrice.setText(String.format("\u09F3 " + "%.2f", Double.parseDouble(product_price)));
        tv_productTotalPrice.setText(String.format("\u09F3 " + "%.2f", totalPriceCommon));

//        tv_productDiscount.setText(product_discount);
//        tv_productPrice.setText(product_price);
//        Picasso.with(getContext()).load(product_imgUrl).into(imageView_productImage);
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    productDetailsInterface.dialogDismissHandler();
                    dismiss();
                    return true;
                } else
                    return false;
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_dismissProductViewDialog:
                productDetailsInterface.dialogDismissHandler();
                dismiss();
                break;

            case R.id.btn_AddProductViewDialog:
                String productName = tv_productName.getText().toString();
                String productQuantity = tv_productQuantity.getText().toString();
                String temp_totalBill = tv_productTotalPrice.getText().toString();
//                String productDiscount = editText_singleProdDiscount.getText().toString();
                String totalBill = null;

        /*        if (!TextUtils.isEmpty(productDiscount)) {
                    if (Double.parseDouble(productDiscount) <= totalPriceCommon && Double.parseDouble(productDiscount) >= 0) {
                        double temp = totalPriceCommon - Double.parseDouble(productDiscount);
                        totalBill = String.valueOf((int) temp);

                        if (checkBoxContinueScanning.isChecked()) {
                            continueScanning = true;
                        } else
                            continueScanning = false;

                        productDetailsInterface.dataParsingMethod(continueScanning, itemNo, productName, productQuantity, product_price, totalBill, item_id, unitid, expirydate, discount_percentage);

                        dismiss();
                    } else {
                        Toast.makeText(getActivity(), "Please, fix your discount", Toast.LENGTH_SHORT).show();
                        productDetailsInterface.dialogDismissHandler();
                    }
                } else {
                    totalBill = String.valueOf(totalPriceCommon);
                    if (checkBoxContinueScanning.isChecked()) {
                        continueScanning = true;
                    } else
                        continueScanning = false;

                    productDetailsInterface.dataParsingMethod(continueScanning, itemNo, productName, productQuantity, product_price, totalBill,  item_id, unitid, expirydate, discount_percentage);

                    dismiss();
                }*/
                ArrayList<String> arrayListSku = new ArrayList<>();
                for (int i = 0; i < lotsModelArrayList.size(); i++) {
                    LotsModel current = lotsModelArrayList.get(i);
                    if (current.isSelected()) {
                        arrayListSku.add(current.getSku());
                    }
                }

                if (!arrayListSku.isEmpty()) {
                    totalBill = String.valueOf(totalPriceCommon);
                    if (checkBoxContinueScanning.isChecked()) {
                        continueScanning = true;
                    }
                    else
                        continueScanning = false;


//                    Toast.makeText(context, productQuantity, Toast.LENGTH_SHORT).show();
                    productDetailsInterface.dataParsingMethod(continueScanning, itemNo, productName, productQuantity, product_price, totalBill, arrayListSku, unitid, expirydate, discount_percentage);

                    dismiss();
                } else
                    Toast.makeText(context, "Please select at least one product", Toast.LENGTH_SHORT).show();


                break;

            case R.id.btn_quantityIncrease:
                int quantityNumber1 = Integer.parseInt(tv_productQuantity.getText().toString());
                quantityNumber1 = quantityNumber1 + 1;

                if (quantityNumber1 <= totalQty) {
                    totalPrice1 = quantityNumber1 * Double.parseDouble(product_price);
                    totalPriceCommon = totalPrice1;
                    tv_productQuantity.setText(String.valueOf(quantityNumber1));
                    tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
                }

                break;

            case R.id.btn_quantityDecrease:
                int quantityNumber2 = Integer.parseInt(tv_productQuantity.getText().toString());
                quantityNumber2 = quantityNumber2 - 1;
                if (quantityNumber2 > 0) {
                    totalPrice2 = quantityNumber2 * Double.parseDouble(product_price);
                    totalPriceCommon = totalPrice2;
                    tv_productQuantity.setText(String.valueOf(quantityNumber2));
                    tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
                }

                break;


/*            case R.id.btn_discountIncrease:
                int discountNumber1 = Integer.parseInt(tv_productDiscount.getText().toString());
                discountNumber1 = discountNumber1 + 1;
                tv_productDiscount.setText(String.valueOf(discountNumber1));
                break;

            case R.id.btn_discountDecrease:
                int discountNumber2 = Integer.parseInt(tv_productDiscount.getText().toString());
                discountNumber2 = discountNumber2 - 1;
                tv_productDiscount.setText(String.valueOf(discountNumber2));
                break;*/
        }
    }


    @Override
    public void mBoolean(boolean b, ArrayList<LotsModel> list, int q) {
        if (b) {
            totalQty += q;
            if (Integer.parseInt(tv_productQuantity.getText().toString()) == 0) {
                tv_productQuantity.setText("1");
                totalPriceCommon = Double.parseDouble(product_price);
                tv_productTotalPrice.setText(String.format("\u09F3 " + "%.2f", totalPriceCommon));
            }
//            setValueMethod();
        } else {
            totalQty -= q;
            if (totalQty < Integer.parseInt(tv_productQuantity.getText().toString())) {
                tv_productQuantity.setText(String.valueOf(totalQty));
                totalPriceCommon = (totalQty * Double.parseDouble(product_price));
                tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));

            }
//            setValueMethod();
        }
        tv_availableStock.setText(String.valueOf(totalQty));
        lotsModelArrayList = list;
    }

    class RptUpdater implements Runnable {
        public void run() {
            if( mAutoIncrement ){
                increment();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            } else if( mAutoDecrement ){
                decrement();
                repeatUpdateHandler.postDelayed( new RptUpdater(), REP_DELAY );
            }
        }
    }
    public void increment(){
        int quantityNumber1 = Integer.parseInt(tv_productQuantity.getText().toString());
        quantityNumber1 = quantityNumber1 + 1;

        if (quantityNumber1 <= totalQty) {
            totalPrice1 = quantityNumber1 * Double.parseDouble(product_price);
            totalPriceCommon = totalPrice1;
            tv_productQuantity.setText(String.valueOf(quantityNumber1));
            tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
        }
    }
    public void decrement(){
        int quantityNumber2 = Integer.parseInt(tv_productQuantity.getText().toString());
        quantityNumber2 = quantityNumber2 - 1;
        if (quantityNumber2 > 0) {
            totalPrice2 = quantityNumber2 * Double.parseDouble(product_price);
            totalPriceCommon = totalPrice2;
            tv_productQuantity.setText(String.valueOf(quantityNumber2));
            tv_productTotalPrice.setText("\u09F3 " + String.format("%.2f", totalPriceCommon));
        }
    }
}
