package com.kha.myweather;

public class WeatherRVModel {
    private String time;
    private String temp;
    private String windSpeed;
    private String icon;


    public WeatherRVModel(String time, String temp, String windSpeed, String icon) {
        this.time = time;
        this.temp = temp;
        this.windSpeed = windSpeed;
        this.icon = icon;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }


}
