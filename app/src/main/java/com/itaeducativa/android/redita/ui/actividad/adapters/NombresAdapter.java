package com.itaeducativa.android.redita.ui.actividad.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.itaeducativa.android.redita.R;

import java.util.ArrayList;
import java.util.List;

public class NombresAdapter extends ArrayAdapter<String> {
    private List<String> productNameListFull;

    public NombresAdapter(@NonNull Context context, @NonNull List<String> productNameList) {
        super(context, 0, productNameList);
        productNameListFull = new ArrayList<>(productNameList);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return productNameFilter;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        String productName = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.nombre_item, parent, false);
            ViewHolder viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }

        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        if (productName != null) {
            viewHolder.bindBeer(productName);
        }

        return convertView;
    }

    private Filter productNameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<String> suggestionList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestionList.addAll(productNameListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String productName : productNameListFull) {
                    if (productName.toLowerCase().contains(filterPattern)) {
                        suggestionList.add(productName);
                    }
                }
            }
            results.values = suggestionList;
            results.count = suggestionList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            List<String> productNameList = (List<String>) results.values;
            if (productNameList != null) {
                clear();
                addAll(productNameList);
            }
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return (String) resultValue;
        }
    };

    private class ViewHolder {
        private TextView productNameTextView;

        ViewHolder(View itemView) {
            productNameTextView = itemView.findViewById(R.id.product_name_text_view);
        }

        void bindBeer(String productName) {
            productNameTextView.setText(productName);
        }
    }
}
