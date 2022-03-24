package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import customfonts.MyTextView_Roboto_Regular;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Dao.EntrepriseDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Dao.UserAgentDao;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EServeur;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.Entites.EUserAgent;
import com.iduyatech.geoentreprise.Memory.Keys;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.NetWork.HttpRequest;
import com.iduyatech.geoentreprise.R;
import com.iduyatech.geoentreprise.Utils.ETypeMessage;
import com.iduyatech.geoentreprise.Utils.HttpCallbackString;
import com.iduyatech.geoentreprise.Utils.UtilEServeur;
import com.iduyatech.geoentreprise.Utils.UtilTimeStampToDate;
import com.iduyatech.geoentreprise.Utils.UtilsConnexionData;

import org.json.JSONException;
import org.json.JSONObject;

import static com.iduyatech.geoentreprise.Utils.Utils.isEmptyFields;
import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityUserConfig extends AppCompatActivity {

    private EditText edit_pseudo,edit_password,edit_password_repet;
    private MyTextView_Roboto_Regular BtConnect;
    private TextView txt_main;
    private SpinKitView progress_load;
    private String pseudo,pass,confirmPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_config);
        setTitle("Profil utilisateur");
        initWidget();
    }

    @Override
    protected void onStart() {
        super.onStart();
        events();
    }

    private void initWidget(){
        edit_pseudo = findViewById(R.id.edit_pseudo);
        edit_password = findViewById(R.id.edit_password);
        edit_password_repet = findViewById(R.id.edit_password_repet);
        txt_main = findViewById(R.id.txt_main);
        BtConnect = findViewById(R.id.BtConnect);
        progress_load = findViewById(R.id.spin_kit_load);
        //utilisateur
        txt_main.setText(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));

    }
    private void events(){
        BtConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                   // BtConnect.setEnabled(false);//on desactive le temps de la requete
                    Boolean connect = UtilsConnexionData.isConnected(ActivityUserConfig.this);
                    //connexion existante
                    if(connect) {
                         pseudo = edit_pseudo.getText().toString();
                         pass = edit_password.getText().toString();
                         confirmPass = edit_password_repet.getText().toString();
                        //verifier les champs vides
                        boolean b_finish = isEmptyFields(new Object[]{edit_password, edit_password_repet});

                        if (!b_finish) {
                            //verifier la longueur du mot de passe
                            if (pass.length() < 8) {
                                showCFAlertDialog(ActivityUserConfig.this, "Info", "Votre mot de passe doit avoir au moins 8 caractères", ETypeMessage.ERROR);
                                return;
                            }

                            if (confirmPass.length() < 8) {
                                showCFAlertDialog(ActivityUserConfig.this, "Info", "Votre mot de passe de confirmation doit avoir au moins 8 caractères", ETypeMessage.ERROR);
                                return;

                            }

                            if (!pass.equals(confirmPass)) {

                                showCFAlertDialog(ActivityUserConfig.this, "Info", "Les deux mot de passes saisis doivent être identiques", ETypeMessage.ERROR);
                            }
                            else {
                                updateProfilAgent(); //changer l'utilisateur
                            }

                        }
                    }

                    else {
                        showCFAlertDialog(ActivityUserConfig.this,"Pas de connexion",
                                "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
                    }

            }
        });

    }

    private void updateProfilAgent() {
        //
        progress_load.setVisibility(View.VISIBLE);
        BtConnect.setEnabled(false);
        //former l'objet
        EEntreprise eEntreprise= new EEntreprise();
        //avoir le _id
        eEntreprise.set_id(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
        eEntreprise.setCreate_uid(pseudo);
        eEntreprise.setUpdate_uid(pseudo);
        //mettre seulement agent
        final EUserAgent user = new EUserAgent();
        user.setPseudo(pseudo);
        user.setPassword(pass);
        user.setProfil("admin-entreprise");
        user.setDate_update(UtilTimeStampToDate.getTimeStamp());
        eEntreprise.setUserAgent(user);
        //faire le http
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().create();
        final String param= gson_detail.toJson(eEntreprise);
        HttpRequest.updateEntreprise(ActivityUserConfig.this, eServeur, new String[]{param}, new HttpCallbackString() {
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
                           /* JSONObject data = resultObject.getJSONObject("data");
                            EEntreprise uneEntreprise = new Gson().fromJson(data.toString(), EEntreprise.class);
                            uneEntreprise.setStatut(1);//déjà synchronisé
                            UserAgentDao.create(uneEntreprise.getUserAgent()); //save user of firme
                            Gson gson = new Gson();
                            String asList = gson.toJson(uneEntreprise.getListCategorie()); //save list catégorie as String in the DB
                            uneEntreprise.setSaveListCategorie(asList);*/
                            //
                            EEntreprise entrepriseSave= EntrepriseDao.get(1);
                            entrepriseSave.setStatut(1);//déjà synchronisé
                            UserAgentDao.create(user); //save user of firme

                            boolean b = EntrepriseDao.update(entrepriseSave);
                            if (b) {
                                progress_load.setVisibility(View.GONE);
                                BtConnect.setEnabled(true);
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityUserConfig.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Info")
                                        .setCancelable(false)
                                        .setMessage("L'utilisateur a bien été modifié.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //
                                                txt_main.setText(pseudo);
                                                Preferences.save(Keys.PreferencesKeys.USER_PSEUDO, pseudo);
                                                Preferences.save(Keys.PreferencesKeys.USER_PASS_WORD, pass);
                                                //
                                                edit_pseudo.setText("");
                                                edit_password.setText("");
                                                edit_password_repet.setText("");

                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        progress_load.setVisibility(View.GONE);
                        BtConnect.setEnabled(true);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityUserConfig.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage("Echec. L'utilisateur n'a pas été mise à jour. Prière de réessayer ultérieurement!!!")
                                .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        //on peut faire que si en ligne ça ne marche pas, garder en local
                                    }
                                });

                        builder.show(); // Show
                    }
                }catch (JSONException e){
                    progress_load.setVisibility(View.GONE);
                    BtConnect.setEnabled(true);

                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                BtConnect.setEnabled(true);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityUserConfig.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Echec")
                        .setCancelable(false)
                        .setMessage("Veuillez vérifier la connexion et reprendre l'action.")
                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //on peut faire que si en ligne ça ne marche pas, garder en local
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
