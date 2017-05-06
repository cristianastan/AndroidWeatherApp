package com.example.cristiana.weatherapp;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cristiana.weatherapp.model.Forecast;

/**
 * A simple {@link Fragment} subclass.
 */
public class DayDetailsFragment extends Fragment {
    TextView mLocation;
    TextView mTemperature;
    TextView mDescription;
    TextView mPressure;
    TextView mWindSpeed;

    public DayDetailsFragment() {
        // Required empty public constructor
    }

    public static Fragment New(Forecast forecast, String location) {
        Fragment f = new DayDetailsFragment();
        Bundle args = new Bundle();

        /* setare de argumente */
        args.putString("location", location);
        args.putString("temperature", forecast.getMain().getTemp().toString());
        args.putString("description", forecast.getWeather().get(0).getDescription());
        args.putString("pressure", forecast.getMain().getPressure().toString());
        args.putString("wind_speed", forecast.getWind().getSpeed().toString());

        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /* Inflate the layout for this fragment */
        return inflater.inflate(R.layout.fragment_day_details, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mLocation = (TextView) view.findViewById(R.id.fragmentLocation);
        mDescription = (TextView) view.findViewById(R.id.fragmentDescription);
        mTemperature = (TextView) view.findViewById(R.id.fragmentTemperature);
        mPressure = (TextView) view.findViewById(R.id.fragmentPressure);
        mWindSpeed = (TextView) view.findViewById(R.id.fragmentWindSpeed);

        mLocation.setText(getArguments().getString("location"));
        mTemperature.setText(getArguments().getString("temperature") + " C");
        mDescription.setText(getArguments().getString("description"));
        mPressure.setText(getArguments().getString("pressure") + " bars");
        mWindSpeed.setText(getArguments().getString("wind_speed") + " km/h");
    }
}
