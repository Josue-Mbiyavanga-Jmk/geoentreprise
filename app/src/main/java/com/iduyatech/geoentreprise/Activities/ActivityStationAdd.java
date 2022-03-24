package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterCategorieChoix;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterProduitChoix;
import com.iduyatech.geoentreprise.App.AppController;
import com.iduyatech.geoentreprise.Dao.CategorieDao;
import com.iduyatech.geoentreprise.Dao.EntrepriseDao;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EHelper;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EServeur;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.Memory.Keys;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.NetWork.HttpRequest;
import com.iduyatech.geoentreprise.R;
import com.iduyatech.geoentreprise.Utils.ETypeMessage;
import com.iduyatech.geoentreprise.Utils.GPS;
import com.iduyatech.geoentreprise.Utils.HttpCallbackString;
import com.iduyatech.geoentreprise.Utils.UtilEServeur;
import com.iduyatech.geoentreprise.Utils.UtilTimeStampToDate;
import com.iduyatech.geoentreprise.Utils.UtilsConnexionData;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityStationAdd extends AppCompatActivity  {

    private View id_view_station_step_one,id_view_station_step_last;
    private ImageView circle1,circle2;
    private EditText edit_name,edit_phone,edit_ref_lieu, edit_entite,edit_entite_description;
    private Switch switch_social_id,switch_gps_id;
    private Button BtnSave,BtnPreview;
    private SearchableSpinner spinner_produit;
   // private RecyclerView recyclerview_choix_produit;
   // private ImageButton img_btn_add_produit;
    private RelativeLayout bottom;
    private SpinKitView progress_load;
    //pour le controle de position des étapes
    private int FocusActivity = 0;
    private boolean social_siege=false;
    private boolean entreprise_gps=false;
    //pour le controle de l'affichage du spinner categorie
    private String produitName="";
   // private List<EProduit> items_produit;
   // private RecyclerAdapterProduitChoix mAdapter;
    //objet Station
    private EStation eStation;
    //manipulation pour le GPS
    EHelper.Gps coordonneeGPS;
    private BroadcastReceiver gpsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().matches(LocationManager.PROVIDERS_CHANGED_ACTION)) {
                //GPS status change
                if(!checkGps())
                {
                    createGpsDisabledAlert();
                }
                else
                {
                    AppController global=(AppController) (ActivityStationAdd.this.getApplicationContext());
                    global.gp=new GPS(ActivityStationAdd.this);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_add);
        initWidget();
        //essaie
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }

    @Override
    protected void onResume() {
        super.onResume();
        events();
    }

    private void initWidget(){
        // circular steep
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        //Les portion
        id_view_station_step_one = findViewById(R.id.id_view_station_step_one);
        id_view_station_step_last = findViewById(R.id.id_view_station_step_last);
        //Buttons
        BtnSave = findViewById(R.id.BtnSave);
        BtnPreview = findViewById(R.id.BtnPreview);
        BtnPreview.setVisibility(View.GONE);
        //ImageButton
        //img_btn_add_produit = findViewById(R.id.img_btn_add_produit);
        //Spinner
        spinner_produit = findViewById(R.id.spinner_produit);
        //RelativeLayout
        bottom = findViewById(R.id.bottom);
        //Progress
        progress_load = findViewById(R.id.spin_kit_load);
        //Recyclerview
       // recyclerview_choix_produit = findViewById(R.id.recyclerview_choix_produit);
        //EditText
        edit_name = findViewById(R.id.edit_name);
        edit_ref_lieu = findViewById(R.id.edit_ref_lieu);
        edit_phone = findViewById(R.id.edit_phone);
        edit_entite = findViewById(R.id.edit_entite);
        edit_entite_description = findViewById(R.id.edit_entite_description);
        //Switch
        switch_social_id = findViewById(R.id.switch_social_id);
        switch_gps_id = findViewById(R.id.switch_gps_id);
        //others
        /*items_produit = new ArrayList<>();
        mAdapter = new RecyclerAdapterProduitChoix(ActivityStationAdd.this,items_produit,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityStationAdd.this);
        recyclerview_choix_produit.setLayoutManager(mLayoutManager);
        recyclerview_choix_produit.setItemAnimator(new DefaultItemAnimator());
        recyclerview_choix_produit.setAdapter(mAdapter);*/
        //chargement
        loadProduits();

    }

    private void events(){
        //Précédent
        BtnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (FocusActivity) {
                    case 1:
                        //si c'est 1, alors aller à 0
                        changeFocus(0);
                        visibilityControl(0);

                        BtnPreview.setVisibility(View.GONE);
                        FocusActivity=0;
                        BtnSave.setText("Suivant");

                        break;


                    default:
                        break;
                }
            }
        });

        BtnSave.setOnClickListener(new View.OnClickListener() {
                                       @SuppressLint("RestrictedApi")
                                       @Override
                                       public void onClick(View v) {
                   switch (FocusActivity) {
                         case 0:
                         //si c'est O, alors aller à 1
                         //on vérifie si les champs sont non vides avant de passer
                         boolean b = isEmptyFields(new Object[]{edit_name, edit_phone, edit_ref_lieu});
                         if (!b) {
                                  changeFocus(1);
                                  visibilityControl(1);

                                  BtnPreview.setVisibility(View.VISIBLE);
                                  FocusActivity = 1;
                                  BtnSave.setText("Enregistrer");
                                  }
                         break;

                       case 1:
                           //si c'est 1,conclure
                           //on vérifie si les champs sont non vides avant de passer
                           boolean b1 = isEmptyFields(new Object[]{edit_entite, edit_entite_description});
                           if (!b1) {
                               //le produit n'est pas obligatoire
                               /*if(produitName.equals("-- Produit -- *"))
                               {
                                   showCFAlertDialog(ActivityStationAdd.this,"Avertissement","Vous devez sélectionner un produit ou service valide avant de continuer", ETypeMessage.ERROR);
                                   return;
                               }*/

                               //on verifie si le spinner a le bon élément
                               //enregistrer en ligne uniquement pour l'instant
                               Boolean connect = UtilsConnexionData.isConnected(ActivityStationAdd.this);
                               //connexion existante
                               if(connect){
                                   prepareInsert(1);

                               }else {
                                   showCFAlertDialog(ActivityStationAdd.this,"Pas de connexion",
                                           "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
                               }
                               /*//enregistrer en ligne ou en local
                               @SuppressLint("RestrictedApi")
                               MaterialDialog mDialog = new MaterialDialog.Builder(ActivityStationAdd.this)
                                       .setTitle("Information")
                                       .setMessage("Voulez-vous enregistrer cette station directement en ligne ???")
                                       .setCancelable(false)
                                       .setPositiveButton("Oui",  new MaterialDialog.OnClickListener() {
                                           @Override
                                           public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                               // process en ligne
                                               dialogInterface.dismiss();
                                               Boolean connect = UtilsConnexionData.isConnected(ActivityStationAdd.this);
                                               //connexion existante
                                               if(connect){
                                                   prepareInsert(1);

                                               }else {
                                                   showCFAlertDialog(ActivityStationAdd.this,"Pas de connexion",
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
                               mDialog.show();*/

                           }

                           break;

                         default:
                         break;
                                           }
                                       }
                                   });

        //Obtenir une categorie
        spinner_produit.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                produitName=spinner_produit.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Obtenir un siège social
        switch_social_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    social_siege = true;

                }else {
                    social_siege = false;
                }
            }
        });
        //Obtenir GPS
        switch_gps_id.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    //si c'est coché, alors recuperer le GPS
                    if(!checkGps())
                    {
                        createGpsDisabledAlert();

                    }
                    else
                    {
                        coordonneeGPS= getGPS();
                        /*AppController global=(AppController) (ActivityStationAdd.this.getApplicationContext());
                        if (global.gp == null) {
                            global.gp=new GPS(ActivityStationAdd.this);
                            //custom my GPS
                            coordonneeGPS = new EHelper.Gps();
                            coordonneeGPS.setLat(global.gp.getLatitude());
                            coordonneeGPS.setLgt(global.gp.getLongitude());
                        }else {
                            //custom my GPS
                            coordonneeGPS = new EHelper.Gps();
                            coordonneeGPS.setLat(global.gp.getLatitude());
                            coordonneeGPS.setLgt(global.gp.getLongitude());
                        }
*/

                    }
                }else {
                    ///si ce n'est pas coché, ne rien faire pour avoir le gps
                }
            }
        });
    }

    private void prepareInsert(int mode){
        //demarrage du progress une fois pour toute
        progress_load.setVisibility(View.VISIBLE);
        bottom.setVisibility(View.GONE);
        // Traitement général de données
        String name = edit_name.getText().toString();
        String phone = edit_phone.getText().toString();
        String ref_lieu = edit_ref_lieu.getText().toString();
        String entite = edit_entite.getText().toString();
        String entite_description = edit_entite_description.getText().toString();
        //
        eStation = new EStation();
        //former l'objet GPS
        EHelper.Gps test = coordonneeGPS;
        coordonneeGPS =getGPS();
        eStation.setGps(coordonneeGPS);
        eStation.setName(name);
        eStation.setRef_lieu(ref_lieu);
        eStation.setPhone("+243"+phone);
        eStation.setRef_entreprise(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
        eStation.setSocialSiege(social_siege);
        eStation.setCreate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
        eStation.setDate_create(UtilTimeStampToDate.getTimeStamp());
        eStation.setDate_update(UtilTimeStampToDate.getTimeStamp());
        eStation.setStatut(1);//online
        eStation.setUpdate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
        //former l'objet EntiteAdmin
        EHelper.EntiteAdministrative entiteAdministrative = new EHelper.EntiteAdministrative();
        entiteAdministrative.setName(entite);
        entiteAdministrative.setDescription(entite_description);
        eStation.setEntiteAdministrative(entiteAdministrative);
        //former les objets Produit: >1 parce qu'il y a un élément déjà par defaut
        List<EHelper.ProduitStation> list= new ArrayList<>();
        if(!produitName.equals("-- Produit -- *")){
            EProduit prod = ProduitDao.getByName(produitName);
            EHelper.ProduitStation o = new EHelper.ProduitStation();
            o.setRef_produit(prod.get_id());
            o.setDate_create(UtilTimeStampToDate.getTimeStamp());
            o.setCreate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
            o.setStatut(1);
            list.add(o);
        }

       /* if(items_produit.size()>1){
            for(EProduit c:items_produit){
                EHelper.ProduitStation o = new EHelper.ProduitStation();
                o.setRef_produit(c.get_id());
                o.setDate_create(UtilTimeStampToDate.getTimeStamp());
                o.setCreate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
                o.setStatut(1);
                list.add(o);//ici voir s'il ne faut pas déjà vers la conversion Java-Json
            }
        }*/
        eStation.setListProduit(list);
        //save
        if(mode==1){
             insertStation();
            ///showCFAlertDialog(ActivityStationAdd.this,"Avertissement","GPS est: lat="+coordonneeGPS.getLat()+" et long="+coordonneeGPS.getLgt(), ETypeMessage.ERROR);
        }else {
            /// showCFAlertDialog(ActivityStationAdd.this,"Avertissement","GPS est: lat="+coordonneeGPS.getLat()+" et long="+coordonneeGPS.getLgt(), ETypeMessage.ERROR);
            eStation.setStatut(0);//offline
            Boolean b = StationDao.create(eStation);
            if(b){
                //enregistrer en ligne ou en local
                progress_load.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationAdd.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Info")
                        .setCancelable(false)
                        .setMessage("Votre station est bien enregistrée sur votre téléphone et sera synchronisée plustard.")
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

    private void insertStation(){
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
       // Gson gson_detail = new GsonBuilder().serializeNulls().create();
        Gson gson_detail = new GsonBuilder().create();
        final String param= gson_detail.toJson(eStation);
        HttpRequest.addStation(ActivityStationAdd.this, eServeur, new String[]{param}, new HttpCallbackString() {
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
                            EStation station = new Gson().fromJson(data.toString(), EStation.class);
                            Gson gson = new Gson();
                            String asObject = gson.toJson(station.getGps());//save Object GPS as String in the DB
                            station.setSaveGps(asObject);
                            String asObject1 = gson.toJson(station.getEntiteAdministrative());//save Object entiteAdmin as String in the DB
                            station.setSaveEntiteAdministrative(asObject1);
                            String asList = gson.toJson(station.getListProduit()); //save list produit as String in the DB
                            station.setSavelistProduit(asList);
                            boolean b = StationDao.create(station);
                            if (b) {
                                progress_load.setVisibility(View.GONE);
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationAdd.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Opération réussie")
                                        .setCancelable(false)
                                        .setMessage("Votre station est bien enregistrée sur votre téléphone et aussi synchronisée dans le serveur.")
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
                        bottom.setVisibility(View.VISIBLE);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationAdd.this)
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
                    bottom.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationAdd.this)
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

    //utilitaire pour les spinner
    private void refresh(SearchableSpinner spinner,List<String> list) {
        ArrayAdapter<String> adp = new ArrayAdapter<String>(ActivityStationAdd.this,android.R.layout.simple_dropdown_item_1line,list);
        spinner.setAdapter(adp);
    }

    private void loadProduits(){
        List<String> produitName=new ArrayList<>();
        List<EProduit> produitsObjects;
        produitsObjects= ProduitDao.getActiveAll();
        //le premier élément de la liste
        produitName.add("-- Produit -- *");
        if(produitsObjects.size()>0){
            for (EProduit c:produitsObjects) {
                produitName.add(c.getName());
            }
        }
        refresh(spinner_produit,produitName);
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

    ///méthodes pour controle des étapes
    //method custom visibility
    private void visibilityControl(int position)
    {
        switch (position)
        {
            case 0:
                //la portion à afficher et celles à cacher
                id_view_station_step_one.setVisibility(View.VISIBLE);
                id_view_station_step_last.setVisibility(View.GONE);

                break;

            case 1:

                //la portion à afficher et celles à cacher
                id_view_station_step_one.setVisibility(View.GONE);
                id_view_station_step_last.setVisibility(View.VISIBLE);

                break;

            default:

                break;

        }

    }

    //method custom focus
    private void changeFocus(int position)
    {
        switch (position)
        {
            case 0:
                circle1.setBackground(ActivityStationAdd.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                circle2.setBackground(ActivityStationAdd.this.getResources().getDrawable(R.drawable.circlebarre));
                FocusActivity=position;
                break;

            case 1:

                circle1.setBackground(ActivityStationAdd.this.getResources().getDrawable(R.drawable.circlebarre));
                circle2.setBackground(ActivityStationAdd.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                FocusActivity = position;
                break;

            default:

                break;

        }

    }

    //utilitaire pour le GPS
    @SuppressLint("RestrictedApi")
    private void createGpsDisabledAlert() {

        MaterialDialog mDialog = new MaterialDialog.Builder(ActivityStationAdd.this)
                .setTitle("Localisation")
                .setMessage("Vous devez impérativement activer votre GPS sans quoi vous n'allez pas continuer cette opération.\n\nVoulez-vous l'activer maintenant ?")
                .setCancelable(false)
                .setPositiveButton("Activer GPS",  new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        // on scan un compteur
                        dialogInterface.dismiss();
                        showGpsOptions();
                        //

                    }
                })
                .setNegativeButton("Annuler",  new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        if(!checkGps())
                        {
                           /// onBackPressed(); //s'il annule, ne rien faire
                        }

                    }
                }).build();

        // Show Dialog
        mDialog.show();

    }

    private void showGpsOptions() {
        startActivity(new Intent("android.settings.LOCATION_SOURCE_SETTINGS"));
        //getGPS();//après activation,recuperer GPS
    }

    private  boolean checkGps()
    {
        final LocationManager manager = (LocationManager)ActivityStationAdd.this. getSystemService( Context.LOCATION_SERVICE );

        try {

            assert manager != null;
            return manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception  e)
        {
            return  false;
        }

    }

    private EHelper.Gps getGPS(){
        EHelper.Gps gps=null;
        AppController global=(AppController) (ActivityStationAdd.this.getApplicationContext());
        if (global.gp == null) {
            global.gp=new GPS(ActivityStationAdd.this);
            //custom my GPS
            gps = new EHelper.Gps();
            gps.setLat(global.gp.getLatitude());
            gps.setLgt(global.gp.getLongitude());
        }else {
            //custom my GPS
            gps = new EHelper.Gps();
            gps.setLat(global.gp.getLatitude());
            gps.setLgt(global.gp.getLongitude());
        }
        return gps;
    }
}
