package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EServeur;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.Memory.Keys;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.NetWork.HttpRequest;
import com.iduyatech.geoentreprise.R;
import com.iduyatech.geoentreprise.Utils.ETypeMessage;
import com.iduyatech.geoentreprise.Utils.HttpCallbackString;
import com.iduyatech.geoentreprise.Utils.UtilEServeur;
import com.iduyatech.geoentreprise.Utils.UtilTimeStampToDate;
import com.iduyatech.geoentreprise.Utils.UtilsConnexionData;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

import org.json.JSONException;
import org.json.JSONObject;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityProduitAdd extends AppCompatActivity {

    private EditText edit_name,edit_description;
    private customfonts.MyTextView_Roboto_Regular BtnSave;
    private SpinKitView progress_load;
    private EProduit eProduit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produit_add);
        initWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        events();
    }

    private void initWidget(){
        edit_name = findViewById(R.id.edit_name);
        edit_description = findViewById(R.id.edit_description);
        BtnSave = findViewById(R.id.BtnSave);
        progress_load = findViewById(R.id.spin_kit_load);
    }

    private void events(){
        BtnSave.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                //on vérifie si les champs sont non vides avant de passer
                boolean b1 = isEmptyFields(new Object[]{edit_name, edit_description});
                if (!b1) {
                    //on verifie si le spinner a le bon élément
                    //enregistrer en ligne uniquement
                    Boolean connect = UtilsConnexionData.isConnected(ActivityProduitAdd.this);
                    //connexion existante
                    if(connect){
                        prepareInsert(1);

                    }else {
                        showCFAlertDialog(ActivityProduitAdd.this,"Pas de connexion",
                                "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
                    }
                    //enregistrer en ligne ou en local
                   /* @SuppressLint("RestrictedApi")
                    MaterialDialog mDialog = new MaterialDialog.Builder(ActivityProduitAdd.this)
                            .setTitle("Information")
                            .setMessage("Voulez-vous enregistrer ce produit ou service directement en ligne ???")
                            .setCancelable(false)
                            .setPositiveButton("Oui",  new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    // process en ligne
                                    dialogInterface.dismiss();
                                    Boolean connect = UtilsConnexionData.isConnected(ActivityProduitAdd.this);
                                    //connexion existante
                                    if(!connect){
                                        prepareInsert(1);

                                    }else {
                                        showCFAlertDialog(ActivityProduitAdd.this,"Pas de connexion",
                                                "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
                                    }

                                }
                            })
                            .setNegativeButton("Non",  new MaterialDialog.OnClickListener() {
                                @Override
                                public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                    dialogInterface.dismiss();
                                    prepareInsert(0);

                                }
                            }).build();

                    // Show Dialog
                    mDialog.show();
*/
                }

            }
        });
    }

    private void prepareInsert(int mode){
        //demarrage du progress une fois pour toute
        progress_load.setVisibility(View.VISIBLE);
        BtnSave.setEnabled(false);
        // Traitement général de données
        String name = edit_name.getText().toString();
        String description = edit_description.getText().toString();
        eProduit = new EProduit();
        eProduit.setName(name);
        eProduit.setDescriptions(description);
        eProduit.setCreate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
        eProduit.setUpdate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
        eProduit.setDate_create(UtilTimeStampToDate.getTimeStamp());
        eProduit.setDate_update(UtilTimeStampToDate.getTimeStamp());
        eProduit.setStatut(1);//online
        if(mode==1){
            insertProduit();
        }else {
            eProduit.setStatut(0);//offline
            Boolean b = ProduitDao.create(eProduit);
            if(b){
                //enregistrer en ligne ou en local
                progress_load.setVisibility(View.GONE);
                BtnSave.setEnabled(true);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityProduitAdd.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Info")
                        .setCancelable(false)
                        .setMessage("Votre produit ou service est bien enregistré sur votre téléphone et sera synchronisé plustard.")
                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        });
                builder.show(); // Show
            }
        }

    }

    private void insertProduit(){
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
       // Gson gson_detail = new GsonBuilder().serializeNulls().create();
        Gson gson_detail = new GsonBuilder().create();
        final String param= gson_detail.toJson(eProduit);
        HttpRequest.addProduit(ActivityProduitAdd.this, eServeur, new String[]{param}, new HttpCallbackString() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    if(success.equals("success")){
                        //il faut recuperer l'objet et le garder
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        int code = resultObject.getInt("code");
                        if(code == 200){
                            JSONObject data = resultObject.getJSONObject("data");
                            EProduit produit = new Gson().fromJson(data.toString(), EProduit.class);
                            boolean b = ProduitDao.create(produit);
                            if (b) {
                                progress_load.setVisibility(View.GONE);
                                BtnSave.setEnabled(true);
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityProduitAdd.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Opération réussie")
                                        .setCancelable(false)
                                        .setMessage("Votre produit ou service est bien enregistré sur votre téléphone et aussi synchronisé dans le serveur.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        progress_load.setVisibility(View.GONE);
                        BtnSave.setEnabled(true);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityProduitAdd.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Opération echouée")
                                .setCancelable(false)
                                .setMessage("Un problème est servenu lors de votre enregistrement. Prière de réessayer ultérieurement!!!")
                                .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.show(); // Show
                    }
                }catch (JSONException e){
                    progress_load.setVisibility(View.GONE);
                    BtnSave.setEnabled(true);

                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                BtnSave.setEnabled(true);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityProduitAdd.this)
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

    //utilitaire pour des champs vides
    private Boolean isEmptyFields(Object[] objects){
        boolean b=false;
        for (Object o:objects)
        {
            if(o instanceof EditText )
            {
                String text= ((EditText)o).getText().toString().trim();
                if(text.isEmpty()){
                    ((EditText) o).setError("Remplir ce champ!");
                    b=true;
                }

            }
        }
        return b;
    }
}
