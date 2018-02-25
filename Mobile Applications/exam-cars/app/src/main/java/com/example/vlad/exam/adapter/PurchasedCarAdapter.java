package com.example.vlad.exam.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.vlad.exam.R;
import com.example.vlad.exam.model.PurchasedCar;

import java.util.List;

/**
 * Created by vlad on 30.01.2018.
 */

public class PurchasedCarAdapter extends ArrayAdapter<PurchasedCar> {
    private Context context;
    private List<PurchasedCar> carList;

    public PurchasedCarAdapter(@NonNull Context context, List<PurchasedCar> carList) {
        super(context, R.layout.car_list_row, carList);
        this.context = context;
        this.carList = carList;
    }

    public void setData(List<PurchasedCar> purchasedCars) {
        carList = purchasedCars;
        notifyDataSetChanged();
    }

    public void clear() {
        carList.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.car_list_row, parent, false);
        }

//        if (position >= carList.size()) {
//            return convertView;
//        }

        TextView rowItemName = convertView.findViewById(R.id.nameTextView);
        TextView rowItemExtra = convertView.findViewById(R.id.extraTextView);

        final PurchasedCar purchasedCar = carList.get(position);
        String extra = purchasedCar.getBuyDate().toString();

        rowItemName.setText(purchasedCar.getName());
        rowItemExtra.setText(extra);

        return convertView;
    }
}
