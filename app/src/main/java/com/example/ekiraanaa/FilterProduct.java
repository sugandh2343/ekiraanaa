package com.example.ekiraanaa;

import android.widget.Filter;

import com.example.ekiraanaa.Adapters.AdapterProductSeller;
import com.example.ekiraanaa.Models.ModelProducts;

import java.util.ArrayList;

public class FilterProduct extends Filter {
    private AdapterProductSeller adapter;
    private ArrayList<ModelProducts> filterList;

    public FilterProduct(AdapterProductSeller adapter, ArrayList<ModelProducts> filterList) {
        this.adapter = adapter;
        this.filterList= filterList;
    }

    @Override
    protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results=new FilterResults();

        if(constraint!=null&&constraint.length()>0){
            constraint=constraint.toString().toUpperCase();

            ArrayList<ModelProducts> filterModels=new ArrayList<>();
            for(int i=0;i<filterList.size();i++){

                if(filterList.get(i).getProductTitle().toUpperCase().contains(constraint)|
            filterList.get(i).getProductCategory().toUpperCase().contains(constraint)){
                    filterModels.add(filterList.get(i));
                }


            }
        }
        results.count=filterList.size();
        results.values=filterList;
        return results ;
    }

    @Override
    protected void publishResults(CharSequence constraint, FilterResults results) {
        adapter.productList=(ArrayList<ModelProducts>) results.values;
        adapter.notifyDataSetChanged();

    }
}
