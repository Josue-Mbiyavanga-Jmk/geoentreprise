package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.iduyatech.geoentreprise.R;

public class ActivityParametres extends AppCompatActivity {

    private LinearLayout param_categorie;
    private LinearLayout param_user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parametres);
        setTitle("Parametres");
        initWidgets();
        events();

    }

    private void initWidgets(){
        param_categorie = findViewById(R.id.param_categorie);
        param_user = findViewById(R.id.param_user);
    }

    private void events(){

        param_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aller dans l'activité d'utilisateur
                Intent go = new Intent(ActivityParametres.this, ActivityUserConfig.class);
                startActivity(go);
            }
        });

        param_categorie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //aller dans l'activité d'acceuil
                Intent go = new Intent(ActivityParametres.this, ActivityCategorie.class);
                startActivity(go);
            }
        });
    }
}
