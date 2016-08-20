package de.dreier.mytargets.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import de.dreier.mytargets.shared.models.EWeather;
import de.dreier.mytargets.shared.models.Environment;

public class CurrentWeather {

    @SerializedName("cod")
    public Integer httpCode;
    @SerializedName("name")
    public String cityName;
    @SerializedName("weather")
    public List<Weather> weather = new ArrayList<>();
    @SerializedName("wind")
    public Wind wind;
    @SerializedName("clouds")
    public Clouds clouds;

    private static Double mpsToKmh(Double mps) {
        return mps / 0.277777778;
    }

    private static int kmhToBeaufort(Double kmh) {
        return (int) Math.round(Math.pow(kmh / 3.01, 0.666666666));
    }

    public Environment toEnvironment() {
        Environment e = new Environment();
        int code = Integer.parseInt(weather.get(0).icon.substring(0, 2));
        e.weather = imageCodeToWeather(code);
        e.windDirection = 0;
        e.location = cityName;
        e.windDirection = 0;
        e.windSpeed = kmhToBeaufort(mpsToKmh(wind.speed));
        return e;
    }

    private EWeather imageCodeToWeather(int code) {
        switch (code) {
            case 1:
                return EWeather.SUNNY;
            case 2:
                return EWeather.PARTLY_CLOUDY;
            case 3:
            case 4:
                return EWeather.CLOUDY;
            case 9:
                return EWeather.RAIN;
            case 10:
                return EWeather.LIGHT_RAIN;
            default:
                return EWeather.CLOUDY;
        }
    }

    public class Clouds {
        @SerializedName("all")
        public Integer all;
    }

    public class Wind {
        @SerializedName("speed")
        public Double speed;
        @SerializedName("deg")
        public Double deg;
    }

    public class Weather {
        @SerializedName("id")
        public Integer id;
        @SerializedName("main")
        public String main;
        @SerializedName("description")
        public String description;
        @SerializedName("icon")
        public String icon;
    }
}


