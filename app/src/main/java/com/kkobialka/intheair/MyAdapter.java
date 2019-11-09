package com.kkobialka.intheair;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<ItemView> mItemViews;
    private OnItemClickListener mListener;



    public interface OnItemClickListener{
        void onItemClick(int position);
        void onDeleteClick(int position);
        void onRefreshClick(int position);

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView itemCityName;
        public TextView itemCityGetName;

        public ImageButton buttonDelete;



        public MyViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            itemCityName = itemView.findViewById(R.id.item_city_text);
            itemCityGetName = itemView.findViewById(R.id.item_city_get_text);

            buttonDelete = itemView.findViewById(R.id.button_delete);
            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, CityActivity.class);
                    intent.putExtra("city_name",itemCityGetName.getText().toString());
                    context.startActivity(intent);
                }
            });
        }
    }

    public MyAdapter(ArrayList<ItemView> itemViews){
        mItemViews = itemViews;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view, mListener);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        ItemView currentItem = mItemViews.get(position);

        holder.itemCityName.setText(currentItem.getItemCityName());
        holder.itemCityGetName.setText(currentItem.getItemCityGetText());

    }

    @Override
    public int getItemCount() {
        return mItemViews.size();
    }
}
