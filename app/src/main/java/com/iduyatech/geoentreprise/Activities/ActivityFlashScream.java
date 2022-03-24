package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.iduyatech.geoentreprise.Dao.CategorieDao;
import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EServeur;
import com.iduyatech.geoentreprise.Memory.Keys;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.NetWork.HttpRequest;
import com.iduyatech.geoentreprise.R;
import com.iduyatech.geoentreprise.Utils.HttpCallbackJSON;
import com.iduyatech.geoentreprise.Utils.UtilEServeur;
import com.iduyatech.geoentreprise.Utils.UtilsConnexionData;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ActivityFlashScream extends AppCompatActivity {

    public static final int MULTIPLE_PERMISSIONS = 10;

    private customfonts.MyTextView_Roboto_Regular BtNext;
    private SpinKitView progress_load;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_scream);

        String conf = Preferences.get(Keys.PreferencesKeys.CONFIG_IP);
        if(conf==null)
        {
             Preferences.save(Keys.PreferencesKeys.CONFIG_IP,"192.168.43.103");
            Preferences.save(Keys.PreferencesKeys.CONFIG_PORT,"8080");
        }

        String date_inf = Preferences.get(Keys.PreferencesKeys.TIME_STAMP_REF);
        if(date_inf==null)
        {
            Preferences.save(Keys.PreferencesKeys.TIME_STAMP_REF,"2000-03-30 12:05:08.347075");
        }

        initialiseWidget();
        event();
        //les permissions
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.CAMERA,
                Manifest.permission.VIBRATE,
                Manifest.permission.CALL_PHONE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

            }

        }).check();

    }

    private void initialiseWidget()
    {
        BtNext =findViewById(R.id.BtNext);
        progress_load =findViewById(R.id.spin_kit_load);
    }

     private void event()
    {
        BtNext .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress_load.setVisibility(View.VISIBLE);

                    String load = Preferences.get(Keys.PreferencesKeys.LOAD_DATA_FIRST);
                    if(load == null){

                        Boolean connect = UtilsConnexionData.isConnected(ActivityFlashScream.this);
                        //connexion existante

                        if(connect){

                            //chargement des éléments de départ
                            loadElement();

                        }
                        //pas de connexion
                        else {
                            progress_load.setVisibility(View.GONE);
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityFlashScream.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Pas de connexion")
                                    .setCancelable(false)
                                    .setMessage("Problème de connexion survenu, veuillez la vérifier.")
                                    .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.show(); // Show
                        }

                    }
                    //lorsqu'il y a eu déjà des éléments alors continuer sans charger
                    else {
                        progress_load.setVisibility(View.GONE);
                        //
                        Intent signin = new Intent(ActivityFlashScream.this, ActivityLogin.class);
                        startActivity(signin);
                        finish();
                    }

            }
        });
    }


    private void loadElement(){
        //
        EServeur eServeur= UtilEServeur.getServeur();
        HttpRequest.loadCategories(ActivityFlashScream.this, eServeur, new HttpCallbackJSON() {
            @Override
            public void onSuccess(JSONObject response) {
                //parsing
                try {
                    String success = response.getString("status");
                    if(success.equals("success")){
                        JSONObject object = response.getJSONObject("result");
                        JSONArray categories = object.getJSONArray("data");
                        DatabaseManager.clearCategorie();
                        for(int i=0;i<categories.length();i++){
                            JSONObject unObject = categories.getJSONObject(i);
                            ECategorie eCategorie =  new Gson().fromJson(unObject.toString(),ECategorie.class);
                            CategorieDao.create(eCategorie);

                        }
                        //Après la boucle
                        Preferences.save(Keys.PreferencesKeys.LOAD_DATA_FIRST,"YES");
                        progress_load.setVisibility(View.GONE);
                        //
                        Intent signin = new Intent(ActivityFlashScream.this, ActivityLogin.class);
                        startActivity(signin);
                        finish();

                    }else {
                        //les éléments pas chargées
                        progress_load.setVisibility(View.GONE);
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    progress_load.setVisibility(View.GONE);
                }

            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityFlashScream.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Opération non aboutie.")
                        .setCancelable(false)
                        .setMessage("Veuillez vérifier la connexion et reprendre l'action.")
                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                builder.show(); // Show
            }
        });
    }
}
