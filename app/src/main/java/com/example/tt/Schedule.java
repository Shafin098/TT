package com.example.tt;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
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
            sortClassesByStartTime(sat);
        } else if (day.equals("sun")) {
            sun.add(classMap);
            sortClassesByStartTime(sun);
        } else if (day.equals("mon")) {
            mon.add(classMap);
            sortClassesByStartTime(mon);
        } else if (day.equals("tue")) {
            tue.add(classMap);
            sortClassesByStartTime(tue);
        } else if (day.equals("wed")) {
            wed.add(classMap);
            sortClassesByStartTime(wed);
        } else if (day.equals("thu")) {
            thu.add(classMap);
            sortClassesByStartTime(thu);
        } else if (day.equals("fri")){
            fri.add(classMap);
            sortClassesByStartTime(fri);
        }
    }

    public void deleteClass(String day, Map<String,String> classMap) {
        if (day.equals("sat")) {
            sat.remove(classMap);
            sortClassesByStartTime(sat);
        } else if (day.equals("sun")) {
            sun.remove(classMap);
            sortClassesByStartTime(sun);
        } else if (day.equals("mon")) {
            mon.remove(classMap);
            sortClassesByStartTime(mon);
        } else if (day.equals("tue")) {
            tue.remove(classMap);
            sortClassesByStartTime(tue);
        } else if (day.equals("wed")) {
            wed.remove(classMap);
            sortClassesByStartTime(wed);
        } else if (day.equals("thu")) {
            thu.remove(classMap);
            sortClassesByStartTime(thu);
        } else if (day.equals("fri")){
            fri.remove(classMap);
            sortClassesByStartTime(fri);
        }
    }

    public void editClass(String day, Map<String,String> classToUpdate, Map<String,String> updatedClass) {
        List<Map<String,String>> allClasses = getAllClasses(day);
        for (int i=0; i<allClasses.size(); i++) {
            if (allClasses.get(i).equals(classToUpdate)) {
                updateClass(day, i, updatedClass);
                break;
            }
        }
    }

    private void updateClass(String day, int i, Map<String, String> updatedClass) {
        if (day.equals("sat")) {
            sat.set(i, updatedClass);
        } else if (day.equals("sun")) {
            sun.set(i, updatedClass);
        } else if (day.equals("mon")) {
            mon.set(i, updatedClass);
        } else if (day.equals("tue")) {
            tue.set(i, updatedClass);
        } else if (day.equals("wed")) {
            wed.set(i, updatedClass);
        } else if (day.equals("thu")) {
            thu.set(i, updatedClass);
        } else if (day.equals("fri")){
            fri.set(i, updatedClass);
        }
    }

    private void sortClassesByStartTime(ArrayList<Map<String, String>> classes) {
        for (int iteration = classes.size()-1; iteration > 0; iteration--) {
            for (int i = 0; i < iteration; i++) {
                Log.d("compare", "Comparing " + i + " to " + (i+1));
                Log.d("compare", classes.toString());
                Map<String, String> currentClass = classes.get(i);
                Map<String, String> nextClass = classes.get(i+1);
                if (!classesInOrder(currentClass, nextClass)) {
                    classes.set(i, nextClass);
                    classes.set(i+1, currentClass);
                }
            }
        }
    }

    private boolean classesInOrder(Map<String, String> currentClass, Map<String, String> nextClass) {
        boolean inOrder;
        int currentClassHour = Integer.parseInt(currentClass.get("startHour"));
        int nextClassHour = Integer.parseInt(nextClass.get("startHour"));
        if (currentClassHour == nextClassHour) {
            int currentClassMinute = Integer.parseInt(currentClass.get("startMinute"));
            int nextClassMinute = Integer.parseInt(nextClass.get("startMinute"));
            inOrder = currentClassMinute < nextClassMinute;
        } else {
            // 0 means 12 am
            // time is in 24 hour format
            if (currentClassHour == 0 || nextClassHour == 0) {
                if (currentClassHour == 0) {
                    inOrder = true;
                } else {
                    inOrder = false;
                }
            } else {
                inOrder = currentClassHour < nextClassHour;
            }
        }
        return inOrder;
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