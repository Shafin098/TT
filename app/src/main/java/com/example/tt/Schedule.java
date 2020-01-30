package com.example.tt;

import java.util.ArrayList;
import java.util.Map;

public class Schedule {
    private ArrayList<Map<String,String>> sat = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> sun = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> mon = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> tue = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> wed = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> thu = new ArrayList<Map<String, String>>();
    private ArrayList<Map<String,String>> fri = new ArrayList<Map<String, String>>();

    public void addNewClass(String day, Map<String,String> classMap) {
        if (day == "sat") {
            sat.add(classMap);
        } else if (day == "sun") {
            sun.add(classMap);
        } else if (day == "sun") {
            mon.add(classMap);
        } else if (day == "sun") {
            tue.add(classMap);
        } else if (day == "sun") {
            wed.add(classMap);
        } else if (day == "sun") {
            thu.add(classMap);
        } else {
            fri.add(classMap);
        }
    }
}
