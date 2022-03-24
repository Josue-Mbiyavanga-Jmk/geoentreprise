package com.iduyatech.geoentreprise.Adaptaters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.iduyatech.geoentreprise.Entites.ECategorie;
import com.iduyatech.geoentreprise.Entites.EProduit;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.R;

import java.lang.ref.WeakReference;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import customfonts.MyTextView_Roboto_Regular;


public class RecyclerAdapterProduitChoix extends RecyclerView.Adapter<RecyclerAdapterProduitChoix.ViewHolder> {
    private List<EProduit> items;
    private AppCompatActivity activity;

    public interface ItemButtonListener {

        void onDeleteClickListener(int position);
       // void onItemClickListener(int position);
    }

    private  final ItemButtonListener callback_click;


    public RecyclerAdapterProduitChoix(AppCompatActivity appCompatActivity, List<EProduit> items, ItemButtonListener callback) {
        this.items = items;
        this.activity=appCompatActivity;
        callback_click=callback;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapterProduitChoix.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                     int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_choix_categorie, parent, false);

        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final EProduit item =items !=null ? items.get(position):null;


        if(item!=null)
        {

            holder.txt_name.setText(item.getName());

        }
                holder.updateWith(this.callback_click);
                holder.updateItemClick(this.callback_click);
        }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(EProduit item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(EProduit item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void change(EProduit item) {
        int position = items.indexOf(item);
        notifyItemChanged(position);
    }



    public void notify(List<EProduit> list_items) {

        //items.clear();
//        items.addAll(list_items);
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
       public MyTextView_Roboto_Regular txt_name,btn_delete;



        public ViewHolder(View v) {
            super(v);

            txt_name = v.findViewById(R.id.txt_name);
            btn_delete = v.findViewById(R.id.btn_delete);

        }

        private WeakReference<ItemButtonListener> callbackWeakRef;

        public void updateWith(ItemButtonListener callback){

            this.btn_delete.setOnClickListener(this);
            this.callbackWeakRef = new WeakReference<>(callback);
        }


        public void updateItemClick(ItemButtonListener callback){

            //3 - Implement Listener on ImageButton
            if(this.callbackWeakRef!=null)
            {
                itemView.setOnClickListener(this);
            }
            else
            {
                this.callbackWeakRef = new WeakReference<>(callback);
                itemView.setOnClickListener(this);
            }

        }


        @Override
        public void onClick(View v) {
            // 5 - When a click happens, we fire our listener.
            ItemButtonListener callback = callbackWeakRef.get();
            if (callback != null)
            {


               switch (v.getId())
                {
                    case R.id.btn_delete:
                        callback.onDeleteClickListener(getAdapterPosition());
                        break;
                    default:
                       // callback.onItemClickListener(getAdapterPosition());
                        break;
                }

            }
        }
    }

}
