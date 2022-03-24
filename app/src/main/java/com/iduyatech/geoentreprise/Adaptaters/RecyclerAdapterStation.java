package com.iduyatech.geoentreprise.Adaptaters;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.iduyatech.geoentreprise.Entites.EStation;
import com.iduyatech.geoentreprise.R;
import com.iduyatech.geoentreprise.Utils.UtilTimeStampToDate;

import java.lang.ref.WeakReference;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import customfonts.MyTextView_Roboto_Regular;


public class RecyclerAdapterStation extends RecyclerView.Adapter<RecyclerAdapterStation.ViewHolder> {
    private List<EStation> items;
    private AppCompatActivity activity;

    public interface ItemButtonListener {

        void onUpdateClickListener(int position);
        void onItemClickListener(int position);
    }

    private  final ItemButtonListener callback_click;


    public RecyclerAdapterStation(AppCompatActivity appCompatActivity, List<EStation> items, ItemButtonListener callback) {
        this.items = items;
        this.activity=appCompatActivity;
        callback_click=callback;
    }



    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapterStation.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_station, parent, false);

        return new ViewHolder(v);

    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final EStation item =items !=null ? items.get(position):null;


        if(item!=null)
        {
            holder.txt_name.setText(item.getName());
            holder.txt_ref_lieu.setText(item.getRef_lieu());
            String date="";
            boolean b =UtilTimeStampToDate.secondOrMillisecond(item.getDate_update());
            if(b){
                long date_format = UtilTimeStampToDate.getTimeSecondFormat(item.getDate_update());
                date= UtilTimeStampToDate.convert(date_format);
            }else {
                date= UtilTimeStampToDate.convert(item.getDate_update());
            }

            String[] tab=date.split(" ");
            holder.txt_date.setText(tab[0]);

            if(item.getStatut()==0){
                holder.img_status.setBackground(activity.getResources().getDrawable(R.drawable.circle_offline_station));

            }else {
                holder.img_status.setBackground(activity.getResources().getDrawable(R.drawable.circle_online_station));

            }



        }
                holder.updateWith(this.callback_click);
                holder.updateItemClick(this.callback_click);
        }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public void add(EStation item, int position) {
        items.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(EStation item) {
        int position = items.indexOf(item);
        items.remove(position);
        notifyItemRemoved(position);
    }

    public void change(EStation item) {
        int position = items.indexOf(item);
        notifyItemChanged(position);
    }



    public void notify(List<EStation> list_items) {

        //items.clear();
//        items.addAll(list_items);
        notifyDataSetChanged();
    }



    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
       public MyTextView_Roboto_Regular txt_name,txt_ref_lieu,txt_date;
       public ImageView img_status;


        public ViewHolder(View v) {
            super(v);

            txt_name = v.findViewById(R.id.txt_name);
            txt_ref_lieu = v.findViewById(R.id.txt_ref_lieu);
            txt_date = v.findViewById(R.id.txt_date);
            img_status = v.findViewById(R.id.img_status);

        }

        private WeakReference<ItemButtonListener> callbackWeakRef;

        public void updateWith(ItemButtonListener callback){

            //this.BtUpdate.setOnClickListener(this);
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

                callback.onItemClickListener(getAdapterPosition());
             /*  switch (v.getId())
                {
                    case R.id.BtUpdate:
                        callback.onUpdateClickListener(getAdapterPosition());
                        break;
                    default:
                        callback.onItemClickListener(getAdapterPosition());
                        break;
                }*/

            }
        }
    }

}
