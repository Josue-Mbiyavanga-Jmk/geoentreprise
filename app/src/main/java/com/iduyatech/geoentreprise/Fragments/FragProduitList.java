package com.iduyatech.geoentreprise.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterProduit;
import com.iduyatech.geoentreprise.Adaptaters.RecyclerAdapterStation;
import com.iduyatech.geoentreprise.Dao.ProduitDao;
import com.iduyatech.geoentreprise.Dao.StationDao;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.R;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


public class FragProduitList extends Fragment implements RecyclerAdapterProduit.ItemButtonListener {

    View root;
    private RecyclerView recycler_view;
    private RecyclerAdapterProduit mAdapter;
    private ProgressDialog progressDialog;
    private List<EProduit> list_items;

    public FragProduitList() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragProduitList newInstance() {
        FragProduitList fragment = new FragProduitList();
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
        root = inflater.inflate(R.layout.frag_produit_list_item, container, false);

        initWidget();
        return root;
    }

    @Override
    public void onResume() {
        list_items.clear();
        list_items.addAll(ProduitDao.getAll());
        mAdapter.notifyDataSetChanged();
        super.onResume();
    }

    void initWidget()
    {
        list_items=new ArrayList<>();
        recycler_view = root. findViewById(R.id.recyclerview);

        mAdapter = new RecyclerAdapterProduit((AppCompatActivity)getActivity(), list_items,this);
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


    }

}