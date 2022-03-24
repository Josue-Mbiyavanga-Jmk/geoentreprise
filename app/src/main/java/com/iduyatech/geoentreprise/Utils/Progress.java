package com.iduyatech.geoentreprise.Utils;

import android.app.ProgressDialog;
import android.content.Context;

public class Progress {

    public static void showProgress(Context context, ProgressDialog progressDialog)
    {
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);

        progressDialog.show();
    }

    public static void showProgressHorizontal(Context context, ProgressDialog progressDialog, int max)
    {
        progressDialog.setMessage("Veuillez patienter...");
        progressDialog.setCancelable(false);
        progressDialog.setMax(max);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        progressDialog.show();
    }
    public static void hiddenProgress(ProgressDialog progressDialog)
    {
        progressDialog.dismiss();
    }
}
