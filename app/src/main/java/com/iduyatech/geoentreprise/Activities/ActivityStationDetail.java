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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterProduitChoix;
import com.iduyatech.geoentreprise.App.AppController;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityStationDetail extends AppCompatActivity implements RecyclerAdapterProduitChoix.ItemButtonListener {

    private SearchableSpinner spinner_produit;
    private ImageButton img_btn_add_produit;
    private ImageView btn_back,btn_gps_locate;
    private RecyclerView recyclerview_choix_produit;
    private RelativeLayout contenaireHelp;
    private String produitName="";
    private List<EProduit> items_produit;
    private RecyclerAdapterProduitChoix mAdapter;
    private SpinKitView progress_load;
    private int station_id;
    private EStation station;
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
                    AppController global=(AppController) (ActivityStationDetail.this.getApplicationContext());
                    global.gp=new GPS(ActivityStationDetail.this);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_detail);
        setTitle("Associer produit ou service");
        Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            station_id = bundle.getInt("station_id");
            //objet
             station = StationDao.get(station_id);
        }
        initWidget();
        events();
        loadProduits();
        loadFocus();
        //essaie
        registerReceiver(gpsReceiver, new IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION));

    }


    @Override
    protected void onStart() {
        super.onStart();

    }

    private void initWidget(){
        img_btn_add_produit = findViewById(R.id.img_btn_add_produit);
        btn_back = findViewById(R.id.btn_back);
        btn_gps_locate = findViewById(R.id.btn_gps_locate);
        spinner_produit = findViewById(R.id.spinner_produit);
        contenaireHelp = findViewById(R.id.contenaireHelp);
        recyclerview_choix_produit = findViewById(R.id.recyclerview_choix_produit);
        progress_load =findViewById(R.id.spin_kit_load);
        items_produit = new ArrayList<>();
        mAdapter = new RecyclerAdapterProduitChoix(ActivityStationDetail.this,items_produit,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityStationDetail.this);
        recyclerview_choix_produit.setLayoutManager(mLayoutManager);
        recyclerview_choix_produit.setItemAnimator(new DefaultItemAnimator());
        recyclerview_choix_produit.setAdapter(mAdapter);
    }

    private void events(){
        //retour en arrière
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        //Obtenir GPS
        btn_gps_locate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //si c'est coché, alors recuperer le GPS
                if(!checkGps())
                {
                    createGpsDisabledAlert();

                }
                else {
                    coordonneeGPS = getGPS();

                }
                //
                @SuppressLint("RestrictedApi")
                MaterialDialog mDialog = new MaterialDialog.Builder(ActivityStationDetail.this)
                        .setTitle("Information")
                        .setMessage("Voulez-vous obtenir les nouvelles coordonnées GPS pour cette station ???")
                        .setCancelable(false)
                        .setPositiveButton("Oui",  new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                             //mise à jour de GPS
                                dialogInterface.dismiss();
                                updateGpsStation();
                            }
                        })
                        .setNegativeButton("Non",  new MaterialDialog.OnClickListener() {
                            @Override
                            public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                                dialogInterface.dismiss();
                                //on quitte
                            }
                        }).build();

                // Show Dialog
                mDialog.show();
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
        //
        img_btn_add_produit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!produitName.equals("-- Produit -- *"))
                {
                    EProduit unProduit = ProduitDao.getByName(produitName);
                    if(items_produit.size()==0){
                        /*items_produit.add(unProduit);
                        mAdapter.notifyDataSetChanged();*/
                        localOrRemote(unProduit);
                    }else {
                        for(EProduit c:items_produit){
                            if(c.get_id().equals(unProduit.get_id())){
                                showCFAlertDialog(ActivityStationDetail.this,"Avertissement","Ce produit ou service a déjà été selectionné.", ETypeMessage.ERROR);
                                return;
                            }

                        }

                        /*items_produit.add(unProduit);
                        mAdapter.notifyDataSetChanged();*/
                        localOrRemote(unProduit);

                    }

                }
                else {
                    showCFAlertDialog(ActivityStationDetail.this,"Avertissement","Vous devez sélectionner un produit ou service valide avant de continuer", ETypeMessage.ERROR);
                }
            }
        });
    }

    //utilitaire pour les spinner
    private void refresh(SearchableSpinner spinner,List<String> list) {
        ArrayAdapter<String> adp = new ArrayAdapter<String>(ActivityStationDetail.this,android.R.layout.simple_dropdown_item_1line,list);
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

    private void loadFocus(){
        EStation station = StationDao.get(station_id);
        String aslist = station.getSavelistProduit();//on recupere la liste dans la BD
        List<EHelper.ProduitStation> list= Arrays.asList(new GsonBuilder().create().fromJson(aslist, EHelper.ProduitStation[].class));//on transforme String en liste
        if(list.size()==0){
            recyclerview_choix_produit.setVisibility(View.GONE);
            contenaireHelp.setVisibility(View.VISIBLE);
        }else {
            recyclerview_choix_produit.setVisibility(View.VISIBLE);
            items_produit.addAll(ProduitDao.getAllWithProduitStation(list));
            mAdapter.notifyDataSetChanged(); //la liste s'actualise
            contenaireHelp.setVisibility(View.GONE);
        }
    }

    @SuppressLint("RestrictedApi")
    private void localOrRemote(final EProduit o){
        //En ligne uniquement
        Boolean connect = UtilsConnexionData.isConnected(ActivityStationDetail.this);
        //connexion existante
        if(connect){
            addToStation(o,1);

        }else {
            showCFAlertDialog(ActivityStationDetail.this,"Pas de connexion",
                    "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
        }
        //En ligne ou en local
        /*@SuppressLint("RestrictedApi")
        MaterialDialog mDialog = new MaterialDialog.Builder(ActivityStationDetail.this)
                .setTitle("Information")
                .setMessage("Voulez-vous ajouter ce produit ou service à cette station directement en ligne ???")
                .setCancelable(false)
                .setPositiveButton("Oui",  new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        // process en ligne
                        dialogInterface.dismiss();
                        Boolean connect = UtilsConnexionData.isConnected(ActivityStationDetail.this);
                        //connexion existante
                        if(connect){
                            addToStation(o,1);

                        }else {
                            showCFAlertDialog(ActivityStationDetail.this,"Pas de connexion",
                                    "Problème de connexion survenu, veuillez la vérifier.", ETypeMessage.ERROR);
                        }

                    }
                })
                .setNegativeButton("Non",  new MaterialDialog.OnClickListener() {
                    @Override
                    public void onClick(com.shreyaspatil.MaterialDialog.interfaces.DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                        addToStation(o,0);

                    }
                }).build();

        // Show Dialog
        mDialog.show();
*/
    }

    private void addToStation(final EProduit prod,int mode){
        progress_load.setVisibility(View.VISIBLE);//lancer le progress
        //penser comment desactiver le recyclerview pendant le moment du progress
        EHelper.ProduitStation produitStation = new EHelper.ProduitStation();
        produitStation.setRef_produit(prod.get_id());
        produitStation.setCreate_uid(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
        produitStation.setDate_create(UtilTimeStampToDate.getTimeStamp());
        produitStation.setStatut(1);//en ligne
        //
        //EStation station = StationDao.get(station_id);
        String aslist = station.getSavelistProduit();//on recupere la liste dans la BD
        List<EHelper.ProduitStation> listfinalise = new ArrayList<>();
        List<EHelper.ProduitStation> list= Arrays.asList(new GsonBuilder().create().fromJson(aslist, EHelper.ProduitStation[].class));//on transforme String en liste
        for(EHelper.ProduitStation ep:list){
            listfinalise.add(ep);
        }
        listfinalise.add(produitStation);//on ajoute l'element
        Gson gson = new Gson();
        aslist = gson.toJson(listfinalise); //transforme list to string
        station.setSavelistProduit(aslist);//save list produit as String in the DB
        station.setListProduit(listfinalise);//mettre list produit comme json
        String gps = station.getSaveGps();//pour le gps
        EHelper.Gps o = gson.fromJson(gps,EHelper.Gps.class);
        station.setGps(o);
        String entite = station.getSaveEntiteAdministrative();
        EHelper.EntiteAdministrative obj = gson.fromJson(entite,EHelper.EntiteAdministrative.class);
        station.setEntiteAdministrative(obj);

        if(mode==1){
            updateProduitStation(station,prod);
        }else {
            station.setStatut(0);//pour le synchroniser
            Boolean b= StationDao.update(station);
            if(b){
               // items_produit.add(prod);//ajout dans la liste produits
               // mAdapter.notifyDataSetChanged(); //la liste des produits s'actualise
                progress_load.setVisibility(View.GONE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                        .setTitle("Info")
                        .setCancelable(false)
                        .setMessage("Le produit ou service est bien ajouter à cette station ici en local.")
                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                if(items_produit.size()==0){
                                    loadFocus();
                                }else {
                                    items_produit.add(prod);//ajout dans la liste produits
                                    mAdapter.notifyDataSetChanged(); //la liste des produits s'actualise
                                }

                            }
                        });

                builder.show(); // Show
            }
        }


    }

    private void updateProduitStation(final EStation eStation,final EProduit prod){
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().serializeNulls().create();
        final String param= gson_detail.toJson(eStation);
        HttpRequest.updateStation(ActivityStationDetail.this, eServeur, new String[]{param}, new HttpCallbackString() {
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
                            eStation.setStatut(1);//déjà synchronisé
                            eStation.setDate_update(station.getDate_update());//appliquer le seul changement : date
                            boolean b = StationDao.update(eStation);
                            if (b) {
                                progress_load.setVisibility(View.GONE);
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Info")
                                        .setCancelable(false)
                                        .setMessage("Le produit ou service est bien ajouter à cette station puis synchronisé dans le serveur.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if(items_produit.size()==0){
                                                    loadFocus();
                                                }else {
                                                    items_produit.add(prod);//ajout dans la liste produits
                                                    mAdapter.notifyDataSetChanged(); //la liste des produits s'actualise
                                                }

                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        progress_load.setVisibility(View.GONE);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage("Echec. Le produit ou service n'a pu etre ajouté à cette station. Prière de réessayer ultérieurement!!!")
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

                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
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

    @Override
    public void onDeleteClickListener(int position) {
        final KProgressHUD hud = KProgressHUD.create(ActivityStationDetail.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Veuillez patienter")
                .setDetailsLabel("Suppression encours ...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        //
        final int index = position;
        final EProduit produit = items_produit.get(position);
        final EStation station = StationDao.get(station_id);
        //enlever de l'objet station
        String aslist = station.getSavelistProduit();//on recupere la liste dans la BD
        List<EHelper.ProduitStation> listfinalise = new ArrayList<>();
        List<EHelper.ProduitStation> list= Arrays.asList(new GsonBuilder().create().fromJson(aslist, EHelper.ProduitStation[].class));//on transforme String en liste
        for(EHelper.ProduitStation ep:list){
            if(!ep.getRef_produit().equals(produit.get_id())){
                listfinalise.add(ep);//new list n'aura plus le produit supprimé
            }
        }

        Gson gson = new Gson();
        aslist = gson.toJson(listfinalise); //transforme list to string
        station.setSavelistProduit(aslist);//save list produit as String in the DB
        //pour la suite en ligne,préparer l'objet station.
        station.setListProduit(listfinalise);//mettre list produit comme json
        String gps = station.getSaveGps();//pour le gps
        EHelper.Gps o = gson.fromJson(gps,EHelper.Gps.class);
        station.setGps(o);
        String entite = station.getSaveEntiteAdministrative();
        EHelper.EntiteAdministrative obj = gson.fromJson(entite,EHelper.EntiteAdministrative.class);
        station.setEntiteAdministrative(obj);
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().serializeNulls().create();
        final String param= gson_detail.toJson(station);
        HttpRequest.updateStation(ActivityStationDetail.this, eServeur, new String[]{param}, new HttpCallbackString() {
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
                            EStation o = new Gson().fromJson(data.toString(), EStation.class);
                            station.setStatut(1);//déjà synchronisé
                            station.setDate_update(o.getDate_update());//appliquer le seul changement : date
                            boolean b = StationDao.update(station);
                            if(b){
                                //arret de la progression
                                hud.dismiss();
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Info")
                                        .setCancelable(false)
                                        .setMessage("Votre suppression du produit ou service a été effectué avec succès.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                //enlever de la liste
                                                items_produit.remove(index);
                                                mAdapter.notifyDataSetChanged();//rafraichir recyclerview
                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        //arret de la progression
                        hud.dismiss();
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage("Echec. Le produit ou service n'a pu etre supprimer de cette station. Prière de réessayer ultérieurement!!!")
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
                    //arret de la progression
                    hud.dismiss();

                }
            }

            @Override
            public void onError(String message) {
                //arret de la progression
                hud.dismiss();
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
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

    //utilitaire pour le GPS
    @SuppressLint("RestrictedApi")
    private void createGpsDisabledAlert() {

        MaterialDialog mDialog = new MaterialDialog.Builder(ActivityStationDetail.this)
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
        final LocationManager manager = (LocationManager)ActivityStationDetail.this. getSystemService( Context.LOCATION_SERVICE );

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
        AppController global=(AppController) (ActivityStationDetail.this.getApplicationContext());
        if (global.gp == null) {
            global.gp=new GPS(ActivityStationDetail.this);
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

    private void updateGpsStation(){
        final KProgressHUD hud = KProgressHUD.create(ActivityStationDetail.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Veuillez patienter")
                .setDetailsLabel("Actualisation encours ...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        //
        final EStation station = StationDao.get(station_id);
        EHelper.Gps o = null;
        if(coordonneeGPS.getLat()== 0 || coordonneeGPS.getLgt()==0){
            o =getGPS();
        }else {
            o=coordonneeGPS;
        }
        //GPS
        station.setGps(o);
        Gson gson = new Gson();
        String asObject = gson.toJson(station.getGps());//save Object GPS as String in the DB
        station.setSaveGps(asObject);
        //Entité admin
        /*String entite = station.getSaveEntiteAdministrative();
        EHelper.EntiteAdministrative obj = gson.fromJson(entite,EHelper.EntiteAdministrative.class);
        station.setEntiteAdministrative(obj);*/
        //produits
        /*String aslist = station.getSavelistProduit();//on recupere la liste dans la BD
        List<EHelper.ProduitStation> list= Arrays.asList(new GsonBuilder().create().fromJson(aslist, EHelper.ProduitStation[].class));
        station.setListProduit(list);*/
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().create();
        final String param= gson_detail.toJson(station);
        HttpRequest.updateStation(ActivityStationDetail.this, eServeur, new String[]{param}, new HttpCallbackString() {
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
                            EStation o = new Gson().fromJson(data.toString(), EStation.class);
                            station.setStatut(1);//déjà synchronisé
                            station.setDate_update(o.getDate_update());//appliquer le seul changement : date
                            boolean b = StationDao.update(station);
                            if(b){
                                //arret de la progression
                                hud.dismiss();
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Info")
                                        .setCancelable(false)
                                        .setMessage("Votre actualisation de la localisation de cette station a été effectué avec succès.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();

                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        //arret de la progression
                        hud.dismiss();
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage("Echec de l'actualisation de la localisation de cette station. Prière de réessayer ultérieurement!!!")
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
                    //arret de la progression
                    hud.dismiss();

                }
            }

            @Override
            public void onError(String message) {
                //arret de la progression
                hud.dismiss();
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityStationDetail.this)
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
}
