package com.iduyatech.geoentreprise.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.crowdfire.cfalertdialog.CFAlertDialog;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterCategorieChoix;
import com.iduyatech.geoentreprise.Dao.CategorieDao;
import com.iduyatech.geoentreprise.DataBases.DatabaseManager;
import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EHelper;
import com.iduyatech.geoentreprise.Entites.EServeur;
import com.iduyatech.geoentreprise.Entites.EUser;
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
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.iduyatech.geoentreprise.Utils.UtilsToast.showCFAlertDialog;

public class ActivityRegister extends AppCompatActivity implements RecyclerAdapterCategorieChoix.ItemButtonListener {

    private View id_view_register_step_one,id_view_register_step_two,
            id_view_register_step_three,id_view_register_step_last;
    private ImageView circle1,circle2,circle3,circle4,img_view_logo;
    private Button BtnSave,BtnPreview;
    private EditText edit_name,edit_phone,edit_email, edit_description,
            edit_idnat,edit_rccm,edit_user_pseudo,edit_user_password,edit_user_password_repet;
    private SearchableSpinner spinner_categorie;
    private RecyclerView recyclerview_choix_categorie;
    private ImageButton img_btn_add_categorie,img_btn_attach_file;
    private RelativeLayout bottom;
    private SpinKitView progress_load;
    //pour l'upload de l'image
    private final int IMG_REQUEST = 1;
    private Bitmap bitmap;
    //pour le controle de position des étapes
    private int FocusActivity = 0;
    //pour le controle de l'affichage du spinner categorie
    private String categorieName="";
    private List<ECategorie> items_categorie;
    private RecyclerAdapterCategorieChoix mAdapter;
    //objet
    private EEntreprise eEntreprise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        initWidget();
        events();

    }

    private void initWidget(){
        // circular steep
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);
        circle3 = findViewById(R.id.circle3);
        circle4 = findViewById(R.id.circle4);
        //Les portion
        id_view_register_step_one = findViewById(R.id.id_view_register_step_one);
        id_view_register_step_two = findViewById(R.id.id_view_register_step_two);
        id_view_register_step_three = findViewById(R.id.id_view_register_step_three);
        id_view_register_step_last = findViewById(R.id.id_view_register_step_last);
        //Buttons
        BtnSave = findViewById(R.id.BtnSave);
        BtnPreview = findViewById(R.id.BtnPreview);
        BtnPreview.setVisibility(View.GONE);
        //Imageview
        img_view_logo = findViewById(R.id.img_view_logo);
        //ImageButton
        img_btn_add_categorie = findViewById(R.id.img_btn_add_categorie);
        img_btn_attach_file = findViewById(R.id.img_btn_attach_file);
        //Spinner
        spinner_categorie = findViewById(R.id.spinner_categorie);
        //RelativeLayout
        bottom = findViewById(R.id.bottom);
        //Progress
        progress_load = findViewById(R.id.spin_kit_load);
        //Recyclerview
        recyclerview_choix_categorie = findViewById(R.id.recyclerview_choix_categorie);
        //EditText
        edit_name = findViewById(R.id.edit_name);
        edit_email = findViewById(R.id.edit_mail);
        edit_phone = findViewById(R.id.edit_phone);
        edit_description = findViewById(R.id.edit_description);
        edit_idnat = findViewById(R.id.edit_idnat);
        edit_rccm = findViewById(R.id.edit_rccm);
        edit_user_pseudo = findViewById(R.id.edit_user_pseudo);
        edit_user_password = findViewById(R.id.edit_user_password);
        edit_user_password_repet = findViewById(R.id.edit_user_password_repet);
        //others
        items_categorie = new ArrayList<>();
        mAdapter = new RecyclerAdapterCategorieChoix(ActivityRegister.this,items_categorie,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ActivityRegister.this);
        recyclerview_choix_categorie.setLayoutManager(mLayoutManager);
        recyclerview_choix_categorie.setItemAnimator(new DefaultItemAnimator());
        recyclerview_choix_categorie.setAdapter(mAdapter);
        //chargement
        loadCategories();

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
                    case 2:
                        //si c'est 2, alors aller à 1
                        changeFocus(1);
                        visibilityControl(1);

                        BtnPreview.setVisibility(View.VISIBLE);
                        FocusActivity=1;
                        BtnSave.setText("Suivant");

                        break;

                    case 3:
                        //si c'est 3, alors aller à 2
                        changeFocus(2);
                        visibilityControl(2);

                        BtnPreview.setVisibility(View.VISIBLE);
                        FocusActivity=2;
                        BtnSave.setText("Suivant");

                        break;

                    case 4:
                        //si c'est 4, alors aller à 3
                        changeFocus(3);
                        visibilityControl(3);

                        BtnPreview.setVisibility(View.VISIBLE);
                        FocusActivity=3;
                        BtnSave.setText("Enregistrer");

                        break;

                    default:
                        break;
                }
            }
        });
        //Suivant
        BtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (FocusActivity) {
                    case 0:
                    //si c'est O, alors aller à 1
                        //on vérifie si les champs sont non vides avant de passer
                        boolean b=isEmptyFields(new Object[]{edit_name,edit_phone,edit_email,edit_description});
                        if(!b)
                        {
                            changeFocus(1);
                            visibilityControl(1);

                            BtnPreview.setVisibility(View.VISIBLE);
                            FocusActivity=1;
                            BtnSave.setText("Suivant");
                        }
                        break;
                    case 1:
                    //si c'est 1, alors aller à 2
                        //on vérifie si les champs sont non vides avant de passer. Ici logo est optionnel
                        boolean b1=isEmptyFields(new Object[]{edit_idnat,edit_rccm});
                        if(!b1)
                        {
                            changeFocus(2);
                            visibilityControl(2);

                            BtnPreview.setVisibility(View.VISIBLE);
                            FocusActivity=2;
                            BtnSave.setText("Suivant");
                        }
                        break;
                    case 2:
                    //si c'est 2, alors aller à 3
                        //vérifier si une catégorie a été choisie
                        if(categorieName.equals("-- Catégorie -- *"))
                        {
                            showCFAlertDialog(ActivityRegister.this,"Avertissement","Vous devez sélectionner une catégorie valide avant de continuer", ETypeMessage.ERROR);
                            return;
                        }

                        if(items_categorie.size()==0)
                        {
                            showCFAlertDialog(ActivityRegister.this,"Avertissement","Vous devez ajouter au moins une catégorie avant de continuer", ETypeMessage.ERROR);
                            return;
                        }

                        changeFocus(3);
                        visibilityControl(3);

                        BtnPreview.setVisibility(View.VISIBLE);
                        FocusActivity=3;
                        BtnSave.setText("Enregistrer");
                        break;
                    case 3:
                    //si c'est 3, alors finir
                        Boolean connect = UtilsConnexionData.isConnected(ActivityRegister.this);
                        //connexion existante
                        if(connect){
                            String pass = edit_user_password.getText().toString();
                            String confirmPass = edit_user_password_repet.getText().toString();
                            //verifier les champs vides
                            boolean b_finish=isEmptyFields(new Object[]{edit_user_password,edit_user_password_repet});

                            if(!b_finish)
                            {
                                //verifier la longueur du mot de passe
                                if(pass.length() < 8)
                                {
                                    showCFAlertDialog(ActivityRegister.this,"Info","Votre mot de passe doit avoir au moins 8 caractères", ETypeMessage.ERROR);
                                    return;
                                }

                                if(confirmPass.length() < 8)
                                {
                                    showCFAlertDialog(ActivityRegister.this,"Info","Votre mot de passe de confirmation doit avoir au moins 8 caractères", ETypeMessage.ERROR);
                                    return;

                                }

                                if (!pass.equals(confirmPass)) {

                                    showCFAlertDialog(ActivityRegister.this,"Info","Les deux mot de passes saisis doivent être identiques", ETypeMessage.ERROR);
                                }
                                else {
                                    //demarrage du progress une fois pour toute
                                    progress_load.setVisibility(View.VISIBLE);
                                    bottom.setVisibility(View.GONE);
                                    // Traitement général de données
                                    String name = edit_name.getText().toString();
                                    String phone = edit_phone.getText().toString();
                                    String mail = edit_email.getText().toString();
                                    String description = edit_description.getText().toString();
                                    String idnat = edit_idnat.getText().toString();
                                    String rccm = edit_rccm.getText().toString();
                                    String pseudo = edit_user_pseudo.getText().toString();
                                    String password = edit_user_password.getText().toString();
                                    if(bitmap!=null){
                                        String logo = imageToString(bitmap);
                                       // eEntreprise.setUrlLog(logo);
                                    }

                                    //former l'objet
                                    eEntreprise=new EEntreprise();
                                    // all step
                                    eEntreprise.setName(name);
                                    eEntreprise.setEmail(mail);
                                    eEntreprise.setPhone("+243"+phone);
                                    eEntreprise.setDescription(description);
                                    eEntreprise.setIdNat(idnat);
                                    eEntreprise.setRccm(rccm);
                                    eEntreprise.setCreate_uid(pseudo);
                                    eEntreprise.setUpdate_uid(pseudo);

                                    EUserAgent user = new EUserAgent();
                                    user.setPseudo(pseudo);
                                    user.setPassword(password);
                                    user.setProfil("admin-entreprise");
                                    user.setDate_update(UtilTimeStampToDate.getTimeStamp());
                                    eEntreprise.setUserAgent(user);

                                    List<EHelper.EntrepriseCategorie> list= new ArrayList<>();
                                    for(ECategorie c:items_categorie){
                                        EHelper.EntrepriseCategorie o = new EHelper.EntrepriseCategorie();
                                        o.setRef_categorie(c.get_id());
                                        o.setDate_create(UtilTimeStampToDate.getTimeStamp());
                                        list.add(o);//ici voir s'il ne faut pas déjà vers la conversion Java-Json
                                    }
                                    eEntreprise.setListCategorie(list);
                                  /*  UserAgent agent = new UserAgent();
                                    agent.setPseudo(pseudo);
                                    agent.setPassword(password);
                                    agent.setProfil("admin-entreprise");
                                    //agent.setDate_update(UtilTimeStampToDate.getTimeStamp()); : parce que date est String niveau back-end
                                    //transforme un objet Java en Json puis stocké en tant que String
                                    Gson gson = new Gson();
                                    String user = gson.toJson(agent);
                                    eEntreprise.setUserAgent(user);
                                    //
                                    List<EntrepriseCategorie> list= new ArrayList<>();
                                    for(ECategorie c:items_categorie){
                                        EntrepriseCategorie o = new EntrepriseCategorie();
                                        o.setRef_categorie(c.get_id());
                                        o.setDate_create(UtilTimeStampToDate.getTimeStamp());
                                        list.add(o);//ici voir s'il ne faut pas déjà vers la conversion Java-Json
                                    }
                                    String listCategorie = gson.toJson(list);
                                    eEntreprise.setListCategorie(listCategorie); */
                                    //
                                    insertEntreprise();

                                }
                            }
                        }
                        else {
                            showCFAlertDialog(ActivityRegister.this,"Pas de connexion",
                                    "Problème de connexion survenu, veuillez la vérifier.",ETypeMessage.ERROR);
                        }
                        break;
                    default:

                        break;
                }
            }
        });
        //Obtenir une image du téléphone
        img_btn_attach_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectionImage();
            }
        });
        //Afficher les catégories choisies
        img_btn_add_categorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!categorieName.equals("-- Catégorie -- *"))
                {
                    //on ajoute une categorie dans la liste d'affichage
                   /* ECategorie uneCategorie = CategorieDao.getByName(categorieName);
                    items_categorie.add(uneCategorie);
                    mAdapter.notifyDataSetChanged();*/
                    ECategorie uneCategorie = CategorieDao.getByName(categorieName);
                    if(items_categorie.size()==0){
                        items_categorie.add(uneCategorie);
                        mAdapter.notifyDataSetChanged();
                    }else {
                        for(ECategorie c:items_categorie){
                            if(c.get_id().equals(uneCategorie.get_id())){
                                showCFAlertDialog(ActivityRegister.this,"Avertissement","Cette catégorie a déjà été selectionnée.", ETypeMessage.ERROR);
                                return;
                            }

                        }

                            items_categorie.add(uneCategorie);
                            mAdapter.notifyDataSetChanged();

                    }

                }
                else {
                    showCFAlertDialog(ActivityRegister.this,"Avertissement","Vous devez sélectionner votre une catégorie avant de continuer", ETypeMessage.ERROR);
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

        /*spinner_categorie.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                categorieName=spinner_categorie.getSelectedItem().toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });
*/

    }

    private void insertEntreprise() {
        //
        EServeur eServeur= UtilEServeur.getServeur();
        //préparation de gson
        Gson gson_detail = new GsonBuilder().serializeNulls().create();
        final String param= gson_detail.toJson(eEntreprise);
        HttpRequest.addEntreprise(ActivityRegister.this, eServeur, new String[]{param}, new HttpCallbackString() {
            @Override
            public void onSuccess(String response) {
                //parsing
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String success = jsonObject.getString("status");
                    if(success.equals("success")){
                        Preferences.save(Keys.PreferencesKeys.REGISTER,"YES");
                        progress_load.setVisibility(View.GONE);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityRegister.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Opération réussie")
                                .setCancelable(false)
                                .setMessage("Prière de patienter pour l'activation de votre entreprise afin de vous connecter. Felicitations!!")
                                .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.POSITIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });

                        builder.show(); // Show

                    }else {
                        //les éléments pas chargées
                        progress_load.setVisibility(View.GONE);
                        bottom.setVisibility(View.VISIBLE);
                        CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityRegister.this)
                                .setDialogStyle(CFAlertDialog.CFAlertStyle.BOTTOM_SHEET)
                                .setTitle("Opération echouée")
                                .setCancelable(false)
                                .setMessage("Un problème est servenu lors de votre enregistrement. Votre pseudo peut etre déjà pris ou un autre incident. Prière de réessayer ultérieurement!!!")
                                .addButton("D'accord", -1, -1, CFAlertDialog.CFAlertActionStyle.NEGATIVE, CFAlertDialog.CFAlertActionAlignment.CENTER, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });

                        builder.show(); // Show
                    }

                }catch (JSONException e){
                    e.printStackTrace();
                    progress_load.setVisibility(View.GONE);
                    bottom.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(String message) {
                progress_load.setVisibility(View.GONE);
                bottom.setVisibility(View.VISIBLE);
                CFAlertDialog.Builder builder = new CFAlertDialog.Builder(ActivityRegister.this)
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

    ///methode pour l'image et sa gestion
    //1. selectionner une image dans son téléphone
    private void selectionImage(){
        Intent go = new Intent();
        go.setType("image/*");
        go.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(go,IMG_REQUEST);
    }

    //2. conversion de l'image en chaine
    private String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgBytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgBytes,Base64.DEFAULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri path = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),path);
                img_view_logo.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    ///méthodes pour controle des étapes
    //method custom visibility
    private void visibilityControl(int position)
    {
        switch (position)
        {
            case 0:
                //la portion à afficher et celles à cacher
                id_view_register_step_one.setVisibility(View.VISIBLE);
                id_view_register_step_two.setVisibility(View.GONE);
                id_view_register_step_three.setVisibility(View.GONE);
                id_view_register_step_last.setVisibility(View.GONE);


                break;

            case 1:

                //la portion à afficher et celles à cacher
                id_view_register_step_one.setVisibility(View.GONE);
                id_view_register_step_two.setVisibility(View.VISIBLE);
                id_view_register_step_three.setVisibility(View.GONE);
                id_view_register_step_last.setVisibility(View.GONE);


                break;

            case 2:

                //la portion à afficher et celles à cacher
                id_view_register_step_one.setVisibility(View.GONE);
                id_view_register_step_two.setVisibility(View.GONE);
                id_view_register_step_three.setVisibility(View.VISIBLE);
                id_view_register_step_last.setVisibility(View.GONE);


                break;

            case 3:

                //la portion à afficher et celles à cacher
                id_view_register_step_one.setVisibility(View.GONE);
                id_view_register_step_two.setVisibility(View.GONE);
                id_view_register_step_three.setVisibility(View.GONE);
                id_view_register_step_last.setVisibility(View.VISIBLE);


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
                circle1.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                circle2.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle3.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle4.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                FocusActivity=position;
                break;

            case 1:

                circle1.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle2.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                circle3.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle4.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                FocusActivity = position;
                break;

            case 2:

                circle1.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle2.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle3.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                circle4.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                FocusActivity = position;
                break;


            case 3:

                circle1.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle2.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle3.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre));
                circle4.setBackground(ActivityRegister.this.getResources().getDrawable(R.drawable.circlebarre_focus));
                FocusActivity = position;
                break;


            default:

                break;

        }


    }
    //utilitaire pour les spinner
    private void refresh(SearchableSpinner spinner,List<String> list) {
        ArrayAdapter<String> adp = new ArrayAdapter<String>(ActivityRegister.this,android.R.layout.simple_dropdown_item_1line,list);
        spinner.setAdapter(adp);
    }
    private void loadCategories(){
        List<String> categoriesName=new ArrayList<>();
        List<ECategorie> categoriesObjects;
        categoriesObjects= CategorieDao.getAll();
        //le premier élément de la liste
         categoriesName.add("-- Catégorie -- *");
        for (ECategorie c:categoriesObjects) {
            categoriesName.add(c.getName());
        }
        refresh(spinner_categorie,categoriesName);
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
    //pour une quelconque suppression de catégorie sur la liste
    @Override
    public void onDeleteClickListener(int position) {
        //final ECategorie categorie = items_categorie.get(position);
        items_categorie.remove(position);
        mAdapter.notifyDataSetChanged();
    }
    //classes utilitaires internes
  /*  public static class EntrepriseCategorie{
        private String ref_categorie;
        private long date_create;

        public EntrepriseCategorie() {
        }

        public String getRef_categorie() {
            return ref_categorie;
        }

        public void setRef_categorie(String ref_categorie) {
            this.ref_categorie = ref_categorie;
        }

        public long getDate_create() {
            return date_create;
        }

        public void setDate_create(long date_create) {
            this.date_create = date_create;
        }
    }*/
}
