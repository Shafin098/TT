package com.example.tt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Notes {
    private List<Map<String, String>> notes = new ArrayList<Map<String, String>>();

    public void addNote(Map<String, String> note) {
        notes.add(note);
        for (int index = notes.size()-1; index > 0; index--) {
            if (!lastNoteShouldRemainLast(index)) {
                Map<String, String> lastNote = notes.get(index);
                notes.set(index, notes.get(index-1));
                notes.set(index-1, lastNote);
            }
        }
    }

    private boolean lastNoteShouldRemainLast(int index) {
        int lastNoteYear = Integer.parseInt(notes.get(index).get("year"));
        int lastNoteMonth = Integer.parseInt(notes.get(index).get("month"));
        int lastNoteDayOfTheMonth = Integer.parseInt(notes.get(index).get("dayOfTheMonth"));

        int beforeLastNoteYear = Integer.parseInt(notes.get(index-1).get("year"));
        int beforeLastNoteMonth = Integer.parseInt(notes.get(index-1).get("month"));
        int beforeLastNoteDayOfTheMonth = Integer.parseInt(notes.get(index-1).get("dayOfTheMonth"));

        // comparing by year
        if (lastNoteYear > beforeLastNoteYear) {
            return true;
        } else if (lastNoteYear == beforeLastNoteYear) {
            // comparing by month
            if (lastNoteMonth > beforeLastNoteMonth) {
                return true;
            } else if (lastNoteMonth == beforeLastNoteMonth) {
                // comparing by dayOfTheMonth
                if (lastNoteDayOfTheMonth > beforeLastNoteDayOfTheMonth) {
                    return true;
                } else if (lastNoteDayOfTheMonth == beforeLastNoteDayOfTheMonth) {
                    return true;
                }  else {
                    return false;
                }
            }  else {
                return false;
            }
        }  else {
            return false;
        }
    }

    public void deleteNote(String noteContent) {
        for (int index=0; index<notes.size(); index++) {
            if (notes.get(index).get("content").equals(noteContent)) {
                notes.remove(index);
            }
        }
    }

    public List<Map<String, String>> getAll() {
        return this.notes;
    }

    public boolean exists() {
        if (notes.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

}

