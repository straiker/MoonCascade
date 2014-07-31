package com.example.mooncascade.app;

import java.util.HashMap;
import java.util.Map;

public class PhenomenonData {
    public static Map<String, Integer> phenomenons = new HashMap<String, Integer>();

    public static int getPhenomenon(String name){
        fillMap();
        return phenomenons.get(name);
    }

    public static void fillMap(){
        phenomenons.put("Clear",R.drawable.day_clear);
        phenomenons.put("Few clouds", R.drawable.day_few);
        phenomenons.put("Variable clouds",R.drawable.day_variable);
        phenomenons.put("Cloudy with clear spells", R.drawable.cloud_clear);
        phenomenons.put("Cloudy",R.drawable.na);
        phenomenons.put("Light snow shower",R.drawable.na);
        phenomenons.put("Moderate snow shower",R.drawable.day_snow);
        phenomenons.put("Heavy snow shower",R.drawable.na);
        phenomenons.put("Light shower",R.drawable.na);
        phenomenons.put("Moderate shower",R.drawable.na);
        phenomenons.put("Heavy shower",R.drawable.na);
        phenomenons.put("Light rain",R.drawable.na);
        phenomenons.put("Moderate rain",R.drawable.moderate_rain);
        phenomenons.put("Heavy rain",R.drawable.na);
        phenomenons.put("Light sleet",R.drawable.sleet);
        phenomenons.put("Moderate sleet",R.drawable.sleet);
        phenomenons.put("Light snowfall",R.drawable.snow);
        phenomenons.put("Moderate snowfall",R.drawable.snow);
        phenomenons.put("Heavy snowfall",R.drawable.snow);
        phenomenons.put("Snowstorm",R.drawable.na);
        phenomenons.put("Drifting snow",R.drawable.na);
        phenomenons.put("Hail",R.drawable.hail);
        phenomenons.put("Mist",R.drawable.na);
        phenomenons.put("Fog",R.drawable.day_fog);
        phenomenons.put("Thunder",R.drawable.thunder);
        phenomenons.put("Thunderstorm",R.drawable.thunder);
    }
}
