package com.memy.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;


import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.RecyclerView;

import com.memy.R;
import com.memy.listener.AdapterListener;
import com.memy.pojo.CountryListObj;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Class used to list the country list values
 */
public class CountryCodeAdapter extends RecyclerView.Adapter<CountryCodeAdapter.ViewHolder> implements Filterable {

    private ArrayList<CountryListObj> mainListData;
    private ArrayList<CountryListObj> listData;
    private final LayoutInflater inflater;
    private int listLeftSpace =0;
    private int listTopSpace = 0;
    private final AdapterListener listener;
    public static final int SEARCH_DATA_VALUES = 1010;
    public static final int SELECTED_COUNTRY_VALUE = 1020;

    public CountryCodeAdapter(Context context, ArrayList<CountryListObj> data,AdapterListener listener, int h, int w) {
        this.listData = data;
        this.listener = listener;
        listLeftSpace = (int) (w * 0.0232);
        listTopSpace=(int) (h * 0.0135);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View countryViewLayout = inflater.inflate(R.layout.country_list_layout, parent, false);
        return new ViewHolder(countryViewLayout);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        CountryListObj obj = listData.get(position);
        if (obj != null) {
            String cName = obj.getCountryName();
            int cCode = obj.getCountryCode();
            String codeVal = String.valueOf(cCode);
            viewHolder.adapterCountryListNameTextView.setText(cName);
            viewHolder.adapterCountryCodeTextView.setText(codeVal);
            viewHolder.countryListRowLayout.setTag(codeVal);
            viewHolder.countryListRowLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.updateAction(SELECTED_COUNTRY_VALUE,view.getTag());
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                listData = (ArrayList<CountryListObj>) results.values; // has the filtered values
                notifyDataSetChanged();  // notifies the data with new filtered values
                if(listener!=null){
                    listener.updateAction(SEARCH_DATA_VALUES,constraint);
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();        // Holds the results of a filtering operation in values
                ArrayList<CountryListObj> filterList = new ArrayList<>();
                if (mainListData == null) {
                    mainListData = new ArrayList<>(listData); // saves the original data in mOriginalValues
                }

                if (constraint == null || constraint.length() == 0) {
                    filterList.addAll(mainListData);
                } else {
                    constraint = constraint.toString().toLowerCase(Locale.US);
                    for (int i = 0; i < mainListData.size(); i++) {
                        CountryListObj data = mainListData.get(i);
                        if (data.getCountryName().toLowerCase(Locale.US).startsWith(constraint.toString())
                                || String.valueOf(data.getCountryCode()).startsWith(constraint.toString())) {//||data.getsCode().toLowerCase().startsWith(constraint.toString())
                            filterList.add(data);
                        }
                    }
                }
                // set the Original result to return
                results.count = filterList.size();
                results.values = filterList;
                return results;
            }
        };
    }

    @Override
    public int getItemCount() {
        return listData!=null?listData.size():0;
    }

    public CountryListObj getItem(int position) {
        return listData.get(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final AppCompatTextView adapterCountryListNameTextView;
        final AppCompatTextView adapterCountryCodeTextView;
        final RelativeLayout countryListRowLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            adapterCountryListNameTextView = (AppCompatTextView) itemView.findViewById(R.id.adapterCountryListNameTextView);
            adapterCountryCodeTextView = (AppCompatTextView) itemView.findViewById(R.id.adapterCountryCodeTextView);
            countryListRowLayout = (RelativeLayout) itemView.findViewById(R.id.countryListRowLayout);
            countryListRowLayout.setPadding(listLeftSpace, listTopSpace, listLeftSpace, listTopSpace);
        }
    }

}
