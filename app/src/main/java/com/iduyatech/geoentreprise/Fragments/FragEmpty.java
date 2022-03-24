package com.iduyatech.geoentreprise.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.R;

import androidx.fragment.app.Fragment;


public class FragEmpty extends Fragment {

    View root;
    private View contenaireHelp;
    private TextView helptext;
    private static String origine;
    public FragEmpty() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragEmpty newInstance(String origin) {
        FragEmpty fragment = new FragEmpty();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        origine = origin;
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        root = inflater.inflate(R.layout.empty_station, container, false);
        initWidget();

        contenaireHelp.setVisibility(View.VISIBLE);
        if(origine.equals("produit")){
            helptext.setText(getResources().getString(R.string.empty_produit));
        }else {
            helptext.setText(getResources().getString(R.string.empty_station));
            }

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    void initWidget()
    {
        contenaireHelp=root.findViewById(R.id.contenaireHelp);
        helptext=root.findViewById(R.id.helptext);
    }



}