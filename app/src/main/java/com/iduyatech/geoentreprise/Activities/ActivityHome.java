package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.common.StringUtils;
import com.iduyatech.geoentreprise.Dao.EntrepriseDao;
import com.iduyatech.geoentreprise.Dao.UserAgentDao;
import com.iduyatech.geoentreprise.Entites.EEntreprise;
import com.iduyatech.geoentreprise.Entites.EUserAgent;
import com.iduyatech.geoentreprise.Memory.Keys;
import com.iduyatech.geoentreprise.Memory.Preferences;
import com.iduyatech.geoentreprise.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ActivityHome extends AppCompatActivity {
    private ImageView BtGestStation, BtGestProduit, BtSetting;
    private LinearLayout contenaireGestStation, contenaireGestProduit, contenaireSetting;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setTitle("Accueil");

        initWidget();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //start animation
        scaleViewAnimation(contenaireGestStation,300);
        scaleViewAnimation(contenaireGestProduit,400);
        scaleViewAnimation(contenaireSetting,500);
        //
        events();
    }

    private void initWidget(){
       BtGestStation = findViewById(R.id.BtGestStation);
       BtGestProduit = findViewById(R.id.BtGestProduit);
       BtSetting = findViewById(R.id.BtSetting);
       contenaireGestStation = findViewById(R.id.contenaireGestStation);
       contenaireGestProduit = findViewById(R.id.contenaireGestProduit);
       contenaireSetting = findViewById(R.id.contenaireSetting);
   }

   private void events(){
       BtGestStation.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent go = new Intent(ActivityHome.this, ActivityStationList.class);
               startActivity(go);
           }
       });

       BtGestProduit.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent go = new Intent(ActivityHome.this, ActivityProduitList.class);
               startActivity(go);
           }
       });

       BtSetting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent go = new Intent(ActivityHome.this, ActivityParametres.class);
               startActivity(go);
           }
       });
   }

    //Animation
    private void scaleViewAnimation(View view, int startDelay){
        // Reset view
        view.setScaleX(0);
        view.setScaleY(0);
        // Animate view
        view.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setInterpolator(new FastOutSlowInInterpolator())
                .setStartDelay(startDelay)
                .setDuration(500)
                .start();
    }


}
