package com.example.userapplication.RecycleView;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.userapplication.R;

import java.util.List;

public class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceAdapter.MyViewHolder> {

    private List<AmbulanceData> ambulanceDataList;


    public AmbulanceAdapterListener onClickListener;
    public interface AmbulanceAdapterListener{
        void sendSmsRequest(View v, int position);
    }

    // constructor
    public AmbulanceAdapter(List<AmbulanceData> ambulanceDataList, AmbulanceAdapterListener ambulanceAdapterListener) {
        this.ambulanceDataList = ambulanceDataList;
        this.onClickListener = ambulanceAdapterListener;
    }






    @NonNull
    @Override
    public AmbulanceAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_ambulance, viewGroup, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AmbulanceAdapter.MyViewHolder myViewHolder, int i) {
        AmbulanceData ambulanceData = ambulanceDataList.get(i);
        myViewHolder.ambulanceName.setText(ambulanceData.getAmbulanceName());
        myViewHolder.ambulanceStation.setText(ambulanceData.getAmnbulanceStation());
        myViewHolder.ambulanceCoordinates.setText(ambulanceData.getAmbulanceCoordinates());
        myViewHolder.ambulancePhoneNumber.setText(ambulanceData.getAmbulancePhoneNumber());
        myViewHolder.comparedLocation.setText(String.valueOf(ambulanceData.getComparedLocation())+" Meters Away");
    }

    @Override
    public int getItemCount() {
        return ambulanceDataList.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        //elements
        public TextView ambulanceName, ambulanceStation, ambulanceCoordinates, ambulancePhoneNumber, comparedLocation;
        public Button bSendSmsRequest;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            ambulanceName = itemView.findViewById(R.id.tvAmbulanceName);
            ambulanceStation = itemView.findViewById(R.id.tvStation);
            ambulanceCoordinates = itemView.findViewById(R.id.ambulanceCoordinates);
            ambulancePhoneNumber = itemView.findViewById(R.id.ambulancePhoneNumber);
            comparedLocation = itemView.findViewById(R.id.comparedLocation);
            bSendSmsRequest = itemView.findViewById(R.id.bSendSms);

            bSendSmsRequest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.sendSmsRequest(v, getAdapterPosition());
                }
            });

        }
    }


}
