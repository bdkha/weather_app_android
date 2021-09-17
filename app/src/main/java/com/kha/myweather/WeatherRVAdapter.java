package com.kha.myweather;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class WeatherRVAdapter extends RecyclerView.Adapter<WeatherRVAdapter.ViewHolder> {
    private Context context;
    private ArrayList<WeatherRVModel> weatherRVModelArrayList;
    DecimalFormat df = new DecimalFormat("#.#");

    public WeatherRVAdapter(Context context, ArrayList<WeatherRVModel> weatherRVModelArrayList) {
        this.context = context;
        this.weatherRVModelArrayList = weatherRVModelArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.weather_rv_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        WeatherRVModel model = weatherRVModelArrayList.get(position);
        Double temp = Double.valueOf(model.getTemp()) - 273.15;
        holder.tempTV.setText(df.format(temp)+"°C");
        holder.windTV.setText(model.getWindSpeed()+"m/s");
        String epochTime = model.getTime();
        Long unixSecond = Long.parseLong(epochTime);
        Date date = new Date(unixSecond * 1000 );
        SimpleDateFormat jdf = new SimpleDateFormat("dd-MM");
        String java_date = jdf.format(date);
        holder.timeTV.setText(java_date);
        geticon(model.getIcon(),holder.conditionIV);


    }

    @Override
    public int getItemCount() {
        return weatherRVModelArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private  TextView windTV,tempTV,timeTV;
        private ImageView conditionIV;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            windTV = itemView.findViewById(R.id.idTVWinSpeed);
            tempTV = itemView.findViewById(R.id.idTVtemperature);
            timeTV = itemView.findViewById(R.id.idTVtime);
            conditionIV = itemView.findViewById(R.id.idIVcondition);
        }
    }
    private void geticon(String condition, ImageView conditionIV) {
        switch (condition ) {
            case "Sun" :
                conditionIV.setImageResource(R.drawable.sun);
                break;
            case  "Rain" :
                conditionIV.setImageResource(R.drawable.rain);
                break;
            case "Clouds":
                conditionIV.setImageResource(R.drawable.clouds);
                break;
            case "Snow" :
                conditionIV.setImageResource(R.drawable.snow);
                break;
            default:
                conditionIV.setImageResource(R.drawable.md);
        }
    }
}