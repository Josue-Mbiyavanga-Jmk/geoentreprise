package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import customfonts.MyTextView_Roboto_Regular;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.iduyatech.geoentreprise.Dao.CategorieDao;
import com.iduyatech.geoentreprise.Dao.EntrepriseDao;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Dao.UserAgentDao;
import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EProduit;
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
import com.iduyatech.geoentreprise.Utils.UtilsConnexionData;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityLogin extends AppCompatActivity {

    private EditText edit_pseudo,edit_password;
    private TextView txt_register;
    private MyTextView_Roboto_Regular BtConnect;
    private SpinKitView progress_load;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
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
        txt_register = findViewById(R.id.txt_register);
        BtConnect = findViewById(R.id.BtConnect);
        progress_load = findViewById(R.id.spin_kit_load);

    }

    private void events(){
       registerOrNot();
       BtConnect.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               //savoir si c'est une première connexion ou pas
               String firstConnection = Preferences.get(Keys.PreferencesKeys.FIRST_CONNECT);
               if(firstConnection == null) {
                   BtConnect.setEnabled(false);//on desactive le temps de la requete
                   Boolean connect = UtilsConnexionData.isConnected(ActivityLogin.this);
                   //connexion existante
                   if(connect) {
                       remoteLogin();
                   }else {
                       showCFAlertDialog(ActivityLogin.this,"Pas de connexion",
                               "Problème de connexion survenu, veuillez la vérifier.",ETypeMessage.ERROR);
                   }

               } else {
                   BtConnect.setEnabled(false);//on desactive le temps de la requete
                   localLogin();
               }

           }
       });

    }

    private void registerOrNot(){
        String register = Preferences.get(Keys.PreferencesKeys.REGISTER);
        if(register == null) {
            txt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go = new Intent(ActivityLogin.this, ActivityRegister.class);
                    startActivity(go);
                }
            } );

        }else {
            txt_register.setTextColor(getResources().getColor(R.color.red));
            txt_register.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showCFAlertDialog(ActivityLogin.this,"Info","Vous etes déjà enregistré, veuillez vous connecter ou attendez l'autorisation pour vous connecter.Merci!", ETypeMessage.ERROR);
                }
            } );

        }
    }

    private void remoteLogin() {
        progress_load.setVisibility(View.VISIBLE);
        String pseudo = edit_pseudo.getText().toString();
        final String pwd = edit_password.getText().toString();
        EServeur serveur = UtilEServeur.getServeur();
        HttpRequest.loginUserEntreprise(ActivityLogin.this, serveur, new String[]{pseudo, pwd}, new HttpCallbackString() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    if (success.equals("success")) {
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        int code = resultObject.getInt("code");
                        if(code == 200){
                            JSONObject data = resultObject.getJSONObject("data");
                            EEntreprise eEntreprise = new Gson().fromJson(data.toString(), EEntreprise.class);
                            UserAgentDao.create(eEntreprise.getUserAgent()); //save user of firme
                            Preferences.save(Keys.PreferencesKeys.STORE_ENTREPRISE__ID, eEntreprise.get_id());
                            Preferences.save(Keys.PreferencesKeys.USER_PSEUDO, eEntreprise.getUserAgent().getPseudo());
                            Preferences.save(Keys.PreferencesKeys.USER_PASS_WORD, pwd);
                            Gson gson = new Gson();
                            String asList = gson.toJson(eEntreprise.getListCategorie()); //save list catégorie as String in the DB
                            eEntreprise.setSaveListCategorie(asList);

                            boolean b = EntrepriseDao.create(eEntreprise);
                            if (b) {
                                //aller remplir tout ce qui concerne cette entreprise via son user
                                Preferences.save(Keys.PreferencesKeys.FIRST_CONNECT, "YES");//connexion réussie
                                Preferences.save(Keys.PreferencesKeys.REGISTER,"YES");//signaler enregistrement antérieur
                                loadEntrepriseElements();
                            }

                        } else if (code == 201){
                            progress_load.setVisibility(View.GONE);
                            BtConnect.setEnabled(true);//on reactive le temps de la requete
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityLogin.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Info.")
                                    .setCancelable(false)
                                    .setMessage("Le pseudo est incorrect. Veuillez le vérifier.")
                                    .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.show(); // Show
                        }
                        else {
                            progress_load.setVisibility(View.GONE);
                            BtConnect.setEnabled(true);//on reactive le temps de la requete
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityLogin.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Info.")
                                    .setCancelable(false)
                                    .setMessage("Le mot de passe est incorrect. Veuillez le vérifier.")
                                    .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });

                            builder.show(); // Show
                        }



                    } else {
                        //ici on va definir d'autres erreurs
                        progress_load.setVisibility(View.GONE);
                        BtConnect.setEnabled(true);//on reactive le temps de la requete
                        showCFAlertDialog(ActivityLogin.this, "Info", "Vous etes déjà enregistré, veuillez vous connecter ou attendez l'autorisation pour vous connecter.Merci!", ETypeMessage.ERROR);
                    }
                } catch (JSONException e) {
                    progress_load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                BtConnect.setEnabled(true);//on reactive le temps de la requete
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityLogin.this)
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

    private void localLogin(){
        progress_load.setVisibility(View.VISIBLE);
        String pseudo = edit_pseudo.getText().toString();
        String pwd = edit_password.getText().toString();

        String pseudoIntern = Preferences.get(Keys.PreferencesKeys.USER_PSEUDO);
        String passwordIntern = Preferences.get(Keys.PreferencesKeys.USER_PASS_WORD);
        if(pseudo.equals(pseudoIntern) && pwd.equals(passwordIntern)){
            progress_load.setVisibility(View.GONE);
            BtConnect.setEnabled(true);//on reactive le temps de la requete
            Intent signin = new Intent(ActivityLogin.this, ActivityHome.class);
            startActivity(signin);
            finish();
        }else {
            progress_load.setVisibility(View.GONE);
            BtConnect.setEnabled(true);//on reactive le temps de la requete
            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityLogin.this)
                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                    .setTitle("Connexion echouée.")
                    .setCancelable(false)
                    .setMessage("Veuillez vérifier vos identifiants de connexion puis réessayer.")
                    .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            builder.show(); // Show
        }
    }
    //il faut savoir mettre un point de répère si elle ne marche pas à cause de la
    // connexion, réessayer plus tard
    private void loadEntrepriseElements(){
        //
        EServeur eServeur= UtilEServeur.getServeur();
        String pseudo =Preferences.get(Keys.PreferencesKeys.USER_PSEUDO);
        HttpRequest.loadAllEntreprise(ActivityLogin.this, eServeur, new String[]{pseudo}, new HttpCallbackString() {
            @Override
            public void onSuccess(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    if (success.equals("success")) {
                        JSONObject resultObject = jsonObject.getJSONObject("result");
                        int code = resultObject.getInt("code");
                        if(code == 200){
                            //pour les produits
                            JSONArray products = resultObject.getJSONArray("produits");
                            if(products.length()>0){
                                EProduit p = null;
                                DatabaseManager.clearProduit();
                                for(int i=0;i<products.length();i++){
                                    JSONObject data = products.getJSONObject(i);
                                    p=  new Gson().fromJson(data.toString(), EProduit.class);
                                    ProduitDao.create(p);
                                }
                            }
                            //pour les stations
                            JSONArray stations = resultObject.getJSONArray("stations");
                            if(stations.length()>0) {
                                EStation s = null;
                                DatabaseManager.clearStation();
                                for (int i = 0; i < stations.length(); i++) {
                                    JSONObject data = stations.getJSONObject(i);
                                    s = new Gson().fromJson(data.toString(), EStation.class);
                                    StationDao.create(s);
                                }
                            }
                            //fin de chargement

                        }
                        //en toute fin
                        progress_load.setVisibility(View.GONE);
                        BtConnect.setEnabled(true);//on reactive le temps de la requete
                        //aller dans l'activité d'acceuil
                        Intent signin = new Intent(ActivityLogin.this, ActivityHome.class);
                        startActivity(signin);
                        finish();

                    } else {
                        //ici on va le statut = failed (cas rare)
                        progress_load.setVisibility(View.GONE);
                        BtConnect.setEnabled(true);//on reactive le temps de la requete

                    }

                } catch (JSONException e) {
                    progress_load.setVisibility(View.GONE);
                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                BtConnect.setEnabled(true);//on reactive le temps de la requete
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityLogin.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Avertissement!")
                        .setCancelable(false)
                        .setMessage("L'opération n'est pas terminée correctement.")
                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //aller dans l'activité d'acceuil, pour l'instant
                                Intent signin = new Intent(ActivityLogin.this, ActivityHome.class);
                                startActivity(signin);
                                finish();
                            }
                        });

                builder.show(); // Show
            }


        });

    }
}
