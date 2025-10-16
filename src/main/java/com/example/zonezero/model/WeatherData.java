package com.example.zonezero.model;

public class WeatherData {
    private int temp;
    private int humidity;
    private int windSpeed;
    private String status;

    public WeatherData(int temp, int humidity, int windSpeed, String status) {
        this.temp = temp;
        this.humidity = humidity;
        this.windSpeed = windSpeed;
        this.status = status;
    }

    public int getTemp() { return temp; }
    public int getHumidity() { return humidity; }
    public int getWindSpeed() { return windSpeed; }
    public String getStatus() { return status; }
}

