package com.kravitzsurf.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class WeatherData {
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("main")
    private Main main;
    
    @SerializedName("weather")
    private List<Weather> weather;
    
    @SerializedName("wind")
    private Wind wind;
    
    public String getName() {
        return name;
    }
    
    public Main getMain() {
        return main;
    }
    
    public List<Weather> getWeather() {
        return weather;
    }
    
    public Wind getWind() {
        return wind;
    }
    
    public class Main {
        @SerializedName("temp")
        private double temp;
        
        @SerializedName("humidity")
        private int humidity;
        
        @SerializedName("pressure")
        private int pressure;
        
        public double getTemp() {
            return temp;
        }
        
        public int getHumidity() {
            return humidity;
        }
        
        public int getPressure() {
            return pressure;
        }
    }
    
    public class Weather {
        @SerializedName("main")
        private String main;
        
        @SerializedName("description")
        private String description;
        
        @SerializedName("icon")
        private String icon;
        
        public String getMain() {
            return main;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getIcon() {
            return icon;
        }
    }
    
    public class Wind {
        @SerializedName("speed")
        private double speed;
        
        @SerializedName("deg")
        private int deg;
        
        public double getSpeed() {
            return speed;
        }
        
        public int getDeg() {
            return deg;
        }
    }
}
