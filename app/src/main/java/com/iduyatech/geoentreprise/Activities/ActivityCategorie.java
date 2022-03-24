package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterCategorieChoix;
import com.iduyatech.geoentreprise.Dao.CategorieDao;
import com.iduyatech.geoentreprise.Dao.EntrepriseDao;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Dao.UserAgentDao;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EHelper;
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
import com.kaopiz.kprogresshud.KProgressHUD;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityCategorie extends AppCompatActivity implements RecyclerAdapterCategorieChoix.ItemButtonListener {

    private RecyclerView recyclerview_choix_categorie;
    private ImageButton img_btn_add_categorie;
    private SearchableSpinner spinner_categorie;
    //pour le controle de l'affichage du spinner categorie
    private String categorieName="";
    private List<ECategorie> items_categorie;
    private RecyclerAdapterCategorieChoix mAdapter;
    private RelativeLayout head_list;
    //objet
    private EEntreprise eEntreprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);
        setTitle("Associer Catégorie");
        initWidgets();
        events();
    }

    private void initWidgets(){
        //ImageButton
        img_btn_add_categorie = findViewById(R.id.img_btn_add_categorie);
        //Spinner
        spinner_categorie = findViewById(R.id.spinner_categorie);
        //textView
        head_list = findViewById(R.id.head_list);
        //Recyclerview
        recyclerview_choix_categorie = findViewById(R.id.recyclerview_choix_categorie);
        //others
        items_categorie = new ArrayList<>();
        mAdapter = new RecyclerAdapterCategorieChoix(ActivityCategorie.this,items_categorie,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityCategorie.this);
        recyclerview_choix_categorie.setLayoutManager(mLayoutManager);
        recyclerview_choix_categorie.setItemAnimator(new DefaultItemAnimator());
        recyclerview_choix_categorie.setAdapter(mAdapter);
        //chargement
        loadCategories();
        loadFocus();

    }

    private void events(){

        //Afficher les catégories choisies
        img_btn_add_categorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!categorieName.equals("-- Catégorie -- *"))
                {
                    //on ajoute une categorie dans la liste d'affichage
                    ECategorie uneCategorie = CategorieDao.getByName(categorieName);
                    if(items_categorie.size()==0){

                       addCategorie(uneCategorie);
                    }else {
                        for(ECategorie c:items_categorie){
                            if(c.get_id().equals(uneCategorie.get_id())){
                                showCFAlertDialog(ActivityCategorie.this,"Avertissement","Cette catégorie a déjà été selectionnée.", ETypeMessage.ERROR);
                                return;
                            }

                        }

                        addCategorie(uneCategorie);

                    }

                }
                else {
                    showCFAlertDialog(ActivityCategorie.this,"Avertissement","Vous devez sélectionner votre une catégorie avant de continuer", ETypeMessage.ERROR);
                }
            }
        });
        //Obtenir une categorie
        spinner_categorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorieName=spinner_categorie.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadFocus(){
        eEntreprise = EntrepriseDao.get(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
        String cat = eEntreprise.getSaveListCategorie();
        List<EHelper.EntrepriseCategorie> list = Arrays.asList(new GsonBuilder().create().fromJson(cat, EHelper.EntrepriseCategorie[].class));//on transforme String en liste
        if(list.size()==0){
            recyclerview_choix_categorie.setVisibility(View.GONE);
            head_list.setVisibility(View.VISIBLE);
        }else {
            head_list.setVisibility(View.GONE);
            recyclerview_choix_categorie.setVisibility(View.VISIBLE);
            items_categorie.addAll(CategorieDao.getAllWithEntrepriseCategorie(list));
            mAdapter.notifyDataSetChanged(); //la liste s'actualise

        }
    }

    @Override
    public void onDeleteClickListener(int position) {
        if(items_categorie.size()<=1){
            showCFAlertDialog(ActivityCategorie.this,"Avertissement","Vous ne pouvez supprimer cette unique catégorie qui est indispensable", ETypeMessage.ERROR);
        }else {
            //supprimer de la liste de catégorie
            final KProgressHUD hud = KProgressHUD.create(ActivityCategorie.this)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                    .setLabel("Veuillez patienter")
                    .setDetailsLabel("Actualisation encours ...")
                    .setCancellable(false)
                    .setAnimationSpeed(2)
                    .setDimAmount(0.5f)
                    .show();
            //prepare
            eEntreprise = EntrepriseDao.get(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
            final int index = position;
            final ECategorie categorie = items_categorie.get(position);
            //enlever de l'objet categorie
            String aslist = eEntreprise.getSaveListCategorie();//on recupere la liste dans la BD
            List<EHelper.EntrepriseCategorie> listfinalise = new ArrayList<>();
            List<EHelper.EntrepriseCategorie> list = Arrays.asList(new GsonBuilder().create().fromJson(aslist, EHelper.EntrepriseCategorie[].class));//on transforme String en liste
            for (EHelper.EntrepriseCategorie ec : list) {
                if (!ec.getRef_categorie().equals(categorie.get_id())) {
                    listfinalise.add(ec);//new list n'aura plus le produit supprimé
                }
            }
            eEntreprise.setListCategorie(listfinalise);//pour les catégories
            EUserAgent user = UserAgentDao.getPseudo(Preferences.get(Keys.PreferencesKeys.USER_PSEUDO));
            user.setPassword(Preferences.get(Keys.PreferencesKeys.USER_PASS_WORD));
            eEntreprise.setUserAgent(user);//pour l'utilisateur
            //request
            EServeur eServeur = UtilEServeur.getServeur();
            //préparation de gson
            Gson gson_detail = new GsonBuilder().serializeNulls().create();
            final String param = gson_detail.toJson(eEntreprise);
            HttpRequest.updateEntreprise(ActivityCategorie.this, eServeur, new String[]{param}, new HttpCallbackString() {
                @Override
                public void onSuccess(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String success = jsonObject.getString("status");
                        if (success.equals("success")) {
                            //il faut recuperer l'objet et le garder
                            JSONObject resultObject = jsonObject.getJSONObject("result");
                            int code = resultObject.getInt("code");
                            if (code == 200) {
                                eEntreprise.setStatut(1);//déjà synchronisé
                                Gson gson = new Gson();
                                String asList = gson.toJson(eEntreprise.getListCategorie()); //save list catégorie as String in the DB
                                eEntreprise.setSaveListCategorie(asList);
                                boolean b = EntrepriseDao.update(eEntreprise);
                                if (b) {
                                    //arret de la progression
                                    hud.dismiss();
                                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
                                            .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                            .setTitle("Info")
                                            .setCancelable(false)
                                            .setMessage("Votre suppression de catégorie a été effectué avec succès.")
                                            .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    //enlever de la liste
                                                    items_categorie.remove(index);
                                                    mAdapter.notifyDataSetChanged();//rafraichir recyclerview
                                                }
                                            });

                                    builder.show(); // Show
                                }
                            }


                        } else {
                            //arret de la progression
                            hud.dismiss();
                            CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
                                    .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                    .setTitle("Info")
                                    .setCancelable(false)
                                    .setMessage("Echec. La catégorie n'a pas pu etre supprimée de cette entreprise. Prière de réessayer ultérieurement!!!")
                                    .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            //on peut faire que si en ligne ça ne marche pas, garder en local
                                        }
                                    });

                            builder.show(); // Show
                        }
                    } catch (JSONException e) {
                        //arret de la progression
                        hud.dismiss();

                    }
                }

                @Override
                public void onError(String message) {
                    //arret de la progression
                    hud.dismiss();
                    CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
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

        }//fin else

    }

    private void loadCategories(){
        List<String> categoriesName=new ArrayList<>();
        List<ECategorie> categoriesObjects;
        eEntreprise = EntrepriseDao.get(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
        String cat = eEntreprise.getSaveListCategorie();
        List<EHelper.EntrepriseCategorie> list = Arrays.asList(new GsonBuilder().create().fromJson(cat, EHelper.EntrepriseCategorie[].class));

      //  categoriesObjects= CategorieDao.getAllWithEntrepriseCategorie(list);
        categoriesObjects= CategorieDao.getAll();
        //le premier élément de la liste
        categoriesName.add("-- Catégorie -- *");
        for (ECategorie c:categoriesObjects) {
            categoriesName.add(c.getName());
        }
        refresh(spinner_categorie,categoriesName);
    }

    //utilitaire pour les spinner
    private void refresh(SearchableSpinner spinner,List<String> list) {
        ArrayAdapter<String> adp = new ArrayAdapter<String>(ActivityCategorie.this,android.R.layout.simple_dropdown_item_1line,list);
        spinner.setAdapter(adp);
    }

    private void addCategorie(final ECategorie categorie){
        final KProgressHUD hud = KProgressHUD.create(ActivityCategorie.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Veuillez patienter")
                .setDetailsLabel("Actualisation encours ...")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
        //prepare
        eEntreprise = EntrepriseDao.get(Preferences.get(Keys.PreferencesKeys.STORE_ENTREPRISE__ID));
        String idEntreprise = eEntreprise.get_id();
        final EHelper.EntrepriseCategorie entrepriseCategorie = new EHelper.EntrepriseCategorie();
        entrepriseCategorie.setRef_categorie(categorie.get_id());
        entrepriseCategorie.setDate_create(UtilTimeStampToDate.getTimeStamp());
        //request
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().serializeNulls().create();
        final String param= gson_detail.toJson(entrepriseCategorie);
        HttpRequest.addCategorieEntreprise(ActivityCategorie.this, eServeur, new String[]{idEntreprise,param}, new HttpCallbackString() {
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
                           //// EEntreprise obj = new Gson().fromJson(data.toString(), EEntreprise.class);
                            String cat = eEntreprise.getSaveListCategorie();
                            List<EHelper.EntrepriseCategorie> list = Arrays.asList(new GsonBuilder().create().fromJson(cat, EHelper.EntrepriseCategorie[].class));
                            List<EHelper.EntrepriseCategorie> listFinalise = new ArrayList<>();
                            for(EHelper.EntrepriseCategorie o:list){
                                listFinalise.add(o);
                            }
                            listFinalise.add(entrepriseCategorie);
                            Gson gson = new Gson();
                            String asList = gson.toJson(listFinalise);
                            eEntreprise.setSaveListCategorie(asList);
                            //appliquer le changement :
                            boolean b = EntrepriseDao.update(eEntreprise);
                            if(b){
                                //arret de la progression
                                hud.dismiss();
                                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
                                        .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                        .setTitle("Info")
                                        .setCancelable(false)
                                        .setMessage("Votre ajout de catégorie à cette entreprise a été effectué avec succès.")
                                        .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                if(items_categorie.size()==0){
                                                    loadFocus();
                                                }else {
                                                    items_categorie.add(categorie);
                                                    mAdapter.notifyDataSetChanged();//rafraichir recyclerview
                                                }

                                            }
                                        });

                                builder.show(); // Show
                            }
                        }


                    }else {
                        //arret de la progression
                        hud.dismiss();
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Info")
                                .setCancelable(false)
                                .setMessage("Echec. L'ajout de catégorie n'a pas été effectué. Prière de réessayer ultérieurement!!!")
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
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityCategorie.this)
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
