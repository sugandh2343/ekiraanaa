package com.example.ekiraanaa.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ekiraanaa.Models.ModelCategory;
import com.example.ekiraanaa.R;

import java.util.ArrayList;

public class AdapterCategory extends RecyclerView.Adapter<AdapterCategory.HolderCategory> {
    private Context context;
    private ArrayList<ModelCategory> categories;

    public AdapterCategory(Context contex, ArrayList<ModelCategory> categories) {
        this.context = contex;
        this.categories = categories;
    }

    @NonNull
    @Override
    public HolderCategory onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.category_single_column,parent,false);
        return new HolderCategory(view);

    }

    @Override
    public void onBindViewHolder(@NonNull HolderCategory holder, int position) {
        ModelCategory modelCategory=categories.get(position);
        String name=modelCategory.getCategory_name();
        String image=modelCategory.getCategory_image();
        holder.txt_category_name.setText(""+name);
        holder.img_category.setImageResource(R.drawable.ic_home);

    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    class HolderCategory extends RecyclerView.ViewHolder {
        private TextView txt_category_name;
        private ImageView img_category;

        public HolderCategory(@NonNull View itemView) {
            super(itemView);
            txt_category_name=itemView.findViewById(R.id.txt_category_name);
            img_category=itemView.findViewById(R.id.img_category);




        }
    }
}
