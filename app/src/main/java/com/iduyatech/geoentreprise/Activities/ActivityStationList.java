package com.iduyatech.geoentreprise.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Fragments.FragEmpty;
import com.iduyatech.geoentreprise.Fragments.FragStationList;
import com.iduyatech.geoentreprise.R;

public class ActivityStationList extends AppCompatActivity {
    public  static FragmentTransaction transaction;
    private FloatingActionButton BtnFloatAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        initView();
        events();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFocus();
    }

    private void initView() {
        setTitle("Les stations");
        //
        BtnFloatAdd =  findViewById(R.id.BtnFloatAdd);

    }

    private void events() {


        BtnFloatAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent go = new Intent(ActivityStationList.this,ActivityStationAdd.class);
                startActivity(go);

            }
        });
    }

    private void loadFocus(){
        long count= StationDao.count();

        if(count== 0L)
        {
            transaction = getSupportFragmentManager().beginTransaction().replace(R.id.ConteneurBase, FragEmpty.newInstance("station"));
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.commit();
        }
        else
        {
            transaction = getSupportFragmentManager().beginTransaction().replace(R.id.ConteneurBase, FragStationList.newInstance());
            transaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            transaction.commit();
        }
    }
}
