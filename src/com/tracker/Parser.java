package com.tracker;

import android.util.Log;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Notasek
 */
public class Parser {

    public String message;
    public String phoneNumber;
    LatLongSetter ls;

    public Parser(String message, String phoneNumber) {
        this.message = message;
        this.phoneNumber = phoneNumber;
        ls = new LatLongSetter();
    }

    public Parser() {

    }

    public LatLongSetter toCoords() {

        String lat = "", lng = "", speed = "";

        String s = this.message.toLowerCase();

        if (s.contains("maps.google.com") && s.contains("q=")) {
            int position = s.indexOf("q=");
            String sbstr = s.substring(position + 2);
            String parts[] = sbstr.split(" |\\&");
            String latlongParts[] = parts[0].split(",");
            if (latlongParts.length >= 2) {
                lat = latlongParts[0];
                lng = latlongParts[1];
            }
        } else if (s.contains("lat") && (s.contains("lng") || s.contains("long") || s.contains("lon"))) {
            String[] parts = s.split(" |\\:|\\n");
            List<String> list = new ArrayList<String>(Arrays.asList(parts));
            list.removeAll(Arrays.asList("", null));

            int positionLat = list.indexOf("lat");
            int positionLng = 0;
            if (s.contains("lng")) {
                positionLng = list.indexOf("lng");
            } else if (s.contains("long")) {
                positionLng = list.indexOf("long");
            } else if (s.contains("lon")) {
                positionLng = list.indexOf("lon");
            }
            lat = list.get(positionLat + 1);
            lng = list.get(positionLng + 1);
        }
        if (s.contains("speed")) {
            int position = s.indexOf("speed");
            String sbstr = s.substring(position + 5);
            String parts[] = sbstr.split(" |\\n|\\:");
            List<String> list = new ArrayList<String>(Arrays.asList(parts));
            list.removeAll(Arrays.asList("", null));
            speed = list.get(0);
        }

        if (isNumeric(lat)) {
            ls.setLat(Double.parseDouble(lat));
        }

        if (isNumeric(lng)) {
            ls.setLng(Double.parseDouble(lng));
        }

        ls.setSpeed(speed);
        ls.setPhone(this.phoneNumber);
        return ls;

    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public ArrayList<FilesSetter> parseTextToFilesSetter(String numbers, String aliases, String colors) {
        String[] splitedNumbers = numbers.split(";");
        String[] splitedAliases = aliases.split(";");
        String[] splitedColors = colors.split(";");
        ArrayList<FilesSetter> filesSetter = new ArrayList<FilesSetter>();
        for (int i = 0; i < splitedNumbers.length; i++) {
            FilesSetter fs = new FilesSetter();
            fs.setNumber(splitedNumbers[i]);
            fs.setAlias(splitedAliases[i]);
            fs.setColor(Integer.parseInt(splitedColors[i]));
            filesSetter.add(fs);
        }
        return filesSetter;
    }

    public ArrayList<FilesSmsSetter> parseTextToFilesSmsSetter(String numbers, String aliases, String format) throws Exception {
        String[] splitedNumbers = numbers.split(";");
        String[] splitedAliases = aliases.split(";");
        String[] splitedFormat = format.split(";");
        if (!isAllValuesSameLong(splitedNumbers, splitedAliases, splitedFormat)) {
            throw new Exception("V plích není stejný počet položek");
        }
        ArrayList<FilesSmsSetter> filesSmsSetter = new ArrayList<FilesSmsSetter>();
        for (int i = 0; i < splitedNumbers.length; i++) {
            FilesSmsSetter fs = new FilesSmsSetter();
            fs.setSmsNumber(Integer.parseInt(splitedNumbers[i]));
            fs.setSmsAlias(splitedAliases[i]);
            fs.setSmsFormat(splitedFormat[i]);
            filesSmsSetter.add(fs);
        }
        return filesSmsSetter;
    }

    public boolean isAllValuesSameLong(String[] a, String[] b, String[] c) {
        if (a.length == b.length && a.length == c.length && b.length == c.length) {
            return true;
        } else {
            return false;
        }
    }
}
