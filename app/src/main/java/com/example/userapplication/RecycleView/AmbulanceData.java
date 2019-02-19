package com.example.userapplication.RecycleView;

public class AmbulanceData {

    private String ambulanceName, amnbulanceStation, ambulanceCoordinates, ambulancePhoneNumber;
    private Float comparedLocation;

    public AmbulanceData(String ambulanceName, String amnbulanceStation, String ambulanceCoordinates, String ambulancePhoneNumber, Float comparedLocation) {
        this.ambulanceName = ambulanceName;
        this.amnbulanceStation = amnbulanceStation;
        this.ambulanceCoordinates = ambulanceCoordinates;
        this.ambulancePhoneNumber = ambulancePhoneNumber;
        this.comparedLocation = comparedLocation;
    }

    public float getComparedLocation(){
        return comparedLocation;
    }

    public void setComparedLocation(float comparedLocation){
        this.comparedLocation = comparedLocation;
    }

    public String getAmbulanceName() {
        return ambulanceName;
    }

    public void setAmbulanceName(String ambulanceName) {
        this.ambulanceName = ambulanceName;
    }

    public String getAmnbulanceStation() {
        return amnbulanceStation;
    }

    public void setAmnbulanceStation(String amnbulanceStation) {
        this.amnbulanceStation = amnbulanceStation;
    }

    public String getAmbulanceCoordinates() {
        return ambulanceCoordinates;
    }

    public void setAmbulanceCoordinates(String ambulanceCoordinates) {
        this.ambulanceCoordinates = ambulanceCoordinates;
    }

    public String getAmbulancePhoneNumber() {
        return ambulancePhoneNumber;
    }

    public void setAmbulancePhoneNumber(String ambulancePhoneNumber) {
        this.ambulancePhoneNumber = ambulancePhoneNumber;
    }
}
