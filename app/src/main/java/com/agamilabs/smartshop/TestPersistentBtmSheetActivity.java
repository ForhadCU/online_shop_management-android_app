package com.agamilabs.smartshop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class TestPersistentBtmSheetActivity extends AppCompatActivity implements View.OnClickListener {

    Button buttonBottomSheet;
    private RelativeLayout layoutBottomSheet;
    private BottomSheetBehavior sheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_persistent_btm_sheet);

        buttonBottomSheet = findViewById(R.id.btn_bottom_sheet);
        layoutBottomSheet = findViewById(R.id.bottom_sheet);
        sheetBehavior = BottomSheetBehavior.from(layoutBottomSheet);

        buttonBottomSheet.setOnClickListener(this);
        sheetBehaviorHandler();
    }

    private void sheetBehaviorHandler() {
        sheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                /*switch (newState) {
                    case BottomSheetBehavior.STATE_HIDDEN:
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED: {
                        buttonBottomSheet.setText("Close Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_COLLAPSED: {
                        buttonBottomSheet.setText("Expand Sheet");
                    }
                    break;
                    case BottomSheetBehavior.STATE_DRAGGING:
                        buttonBottomSheet.setText("Expanding Sheet...");
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        buttonBottomSheet.setText("State setting...");
                        break;
                }*/
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_bottom_sheet) {
            if (sheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                buttonBottomSheet.setText("Close sheet");
            }
            else if (sheetBehavior.getState() != BottomSheetBehavior.STATE_COLLAPSED) {
                sheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                buttonBottomSheet.setText("Expand sheet");
            }
        }
    }
}