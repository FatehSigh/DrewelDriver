package com.octalsoftware.drewel.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.octalsoftware.drewel.R;


/**
 * custom progress dialog
 */

public class DrewelProgressDialog extends ProgressDialog {

    /**
     * declare java objects
     */
    public static DrewelProgressDialog msGWProgressDialog;

    TextView titleTv;

    public DrewelProgressDialog(Context context) {
        super(context);
    }

    public DrewelProgressDialog(Context context, int theme) {
        super(context, theme);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.layout_custom_progreebar);
        titleTv = (TextView) findViewById(R.id.title_tv);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setDimAmount(0.20f);

    }


    /**
     * generate singleton for custom progress dialog
     *
     * @param mContext
     * @return
     */
    public static DrewelProgressDialog getProgressDialog(Context mContext) {

        try {
            if (msGWProgressDialog == null) {
                msGWProgressDialog = new DrewelProgressDialog(mContext, R.style.AppCompatAlertDialogStyle);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return msGWProgressDialog;
    }

    /**
     * show dialog if not showing
     */
    public void showDialog1() {

        try {
            if (msGWProgressDialog.isShowing()) {
                msGWProgressDialog.dismiss();
            }
            msGWProgressDialog.show();
            msGWProgressDialog.setCancelable(false);
            //  titleTv.setText(title);
        } catch (Exception ex) {
            msGWProgressDialog = null;
            ex.printStackTrace();
        }
    }

    /*
     * set title of progress dialog*/

    public void setTitle(String title) {
        try {
            if (msGWProgressDialog != null && msGWProgressDialog.isShowing()) {
                titleTv.setText(title);
            }
        } catch (Exception ex) {
            msGWProgressDialog = null;
            ex.printStackTrace();
        }

    }

    /**
     * hide dialog if not showing
     */
    public void dismissDialog1() {
        try {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (msGWProgressDialog != null && msGWProgressDialog.isShowing()) {
                            msGWProgressDialog.dismiss();
                            msGWProgressDialog = null;
                        } else if (msGWProgressDialog != null)
                            msGWProgressDialog = null;
                    } catch (Exception ex) {
                        msGWProgressDialog = null;
                        ex.printStackTrace();
                    }
                }
            }, 500);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


}
