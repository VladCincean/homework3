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
import com.example.vlad.exam.model.Car;

import java.util.List;

/**
 * Created by vlad on 30.01.2018.
 */

public class CarClientAdapter extends ArrayAdapter<Car> {
    private Context context;
    private List<Car> carList;

    public CarClientAdapter(@NonNull Context context, List<Car> carList) {
        super(context, R.layout.car_list_row, carList);
        this.context = context;
        this.carList = carList;
    }

    public void setData(List<Car> cars) {
        carList = cars;
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

        TextView rowItemName = convertView.findViewById(R.id.nameTextView);
        TextView rowItemExtra = convertView.findViewById(R.id.extraTextView);

        final Car car = carList.get(position);
        String extra = car.getType() + " | quantity: " + Integer.toString(car.getQuantity());

        rowItemName.setText(car.getName());
        rowItemExtra.setText(extra);

        return convertView;
    }
}
