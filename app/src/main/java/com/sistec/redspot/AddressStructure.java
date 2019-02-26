package com.sistec.redspot;

import android.location.Location;

public class AddressStructure {
    private int time_interval;
    private int day_of_week;
    private int day_of_month;
    private int month;
    private int year;
    private double latitude;
    private double longitude;
    private Location currLocation;
    private String locality;
    private String sub_locality;
    private String vehicle_type;

    public AddressStructure(){} //Default constructor for DatabaseSnapshot.getValue(AddressStructure.class);

    public AddressStructure(int time_interval,
                            int day_of_week,
                            int day_of_month,
                            int month, int year,
                            double latitude, double longitude,
                            String locality, String sub_locality,
                            String vehicle_type){
        this.time_interval = time_interval;
        this.day_of_week = day_of_week;
        this.day_of_month = day_of_month;
        this.month = month;
        this.year = year;
        this.latitude = latitude;
        this.longitude = longitude;
        this.locality = locality;
        this.sub_locality = sub_locality;
        this.vehicle_type = vehicle_type;
    }
    public void setCurrLocation(Location ll){currLocation = ll;}
    public Location getCurrLocation(){return currLocation;}
    public int getTime_interval(){return time_interval;}
    public int getDay_of_week(){return day_of_week;}
    public int getDay_of_month(){return day_of_month;}
    public int getMonth(){return month;}
    public int getYear(){return year;}
    public double getLatitude(){return latitude;}
    public double getLongitude(){return longitude;}
    public String getLocality(){return locality;}
    public String getSub_locality(){return sub_locality;}
    public String getVehicle_type(){return vehicle_type;}

}
