package com.example.tt;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
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
        if (day.equals("sat")) {
            sat.add(classMap);
        } else if (day.equals("sun")) {
            sun.add(classMap);
        } else if (day.equals("mon")) {
            mon.add(classMap);
        } else if (day.equals("tue")) {
            tue.add(classMap);
        } else if (day.equals("wed")) {
            wed.add(classMap);
        } else if (day.equals("thu")) {
            thu.add(classMap);
        } else if (day.equals("fri")){
            fri.add(classMap);
        }
    }

    public ArrayList<Map<String, String>> getAllClasses(String day) {
        ArrayList<Map<String, String>> requestedClasses = null;
        switch (day) {
            case "sat":
                requestedClasses = this.sat;
                break;
            case "sun":
                requestedClasses = this.sun;
                break;
            case "mon":
                requestedClasses = this.mon;
                break;
            case "tue":
                requestedClasses = this.tue;
                break;
            case "wed":
                requestedClasses = this.wed;
                break;
            case "thu":
                requestedClasses = this.thu;
                break;
            case "fri":
                requestedClasses = this.fri;
                break;
        }
        return  requestedClasses;
    }

    @Override
    public String toString() {
        return "Schedule{" +
                "sat=" + sat +
                ", sun=" + sun +
                ", mon=" + mon +
                ", tue=" + tue +
                ", wed=" + wed +
                ", thu=" + thu +
                ", fri=" + fri +
                '}';
    }

}

//class TimeComparator implements Comparator<Map<String,String>> {
//
//    @Override
//    public int compare(Map<String, String> class1, Map<String, String> class2) {
//
//    }
//}

