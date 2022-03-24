package com.iduyatech.geoentreprise.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iduyatech.geoentreprise.Activities.ActivityStationDetail;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterStation;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FragStationList extends Fragment implements RecyclerAdapterStation.ItemButtonListener {

    View root;
    private RecyclerView recycler_view;
    private RecyclerAdapterStation mAdapter;
    private ProgressDialog progressDialog;
    private List<EStation> list_items;

    public FragStationList() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragStationList newInstance() {
        FragStationList fragment = new FragStationList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment  list_items = ProduitDao.getAll();
        root = inflater.inflate(R.layout.frag_station_list_item, container, false);

        initWidget();
        return root;
    }

    @Override
    public void onResume() {
        list_items.clear();
        list_items.addAll(StationDao.getAll());
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    void initWidget()
    {
        list_items=new ArrayList<>();
        recycler_view = root. findViewById(R.id.recyclerview);

        mAdapter = new RecyclerAdapterStation((AppCompatActivity)getActivity(), list_items,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setItemAnimator(new DefaultItemAnimator());
        recycler_view.setAdapter(mAdapter);
    }



    @Override
    public void onUpdateClickListener(int position) {

    }

    @Override
    public void onItemClickListener(int position) {

        final EStation station = list_items.get(position);
        final int id = station.getId();
        Intent go = new Intent(getActivity(), ActivityStationDetail.class);
        go.putExtra("station_id", id);
        startActivity(go);

    }

}