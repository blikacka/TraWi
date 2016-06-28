package com.tracker;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import static java.lang.Math.acos;
import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static java.lang.Math.toRadians;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener {

    Marker now;
    Images img;
    public static Resources resources;
    public static Context context;

    public static int[] redColors = {Color.RED, Color.rgb(250, 200, 200), Color.rgb(200, 185, 185)};
    public static int[] blueColors = {Color.BLUE, Color.rgb(200, 200, 250), Color.rgb(185, 185, 200)};
    public static int[] greenColors = {Color.GREEN, Color.rgb(200, 250, 200), Color.rgb(185, 200, 185)};
    public static int[] yellowColors = {Color.YELLOW, Color.rgb(250, 250, 200), Color.rgb(200, 200, 185)};
    public static int[] pinkColors = {Color.rgb(230, 20, 230), Color.rgb(250, 200, 250), Color.rgb(200, 185, 200)};
    public static int[] navyColors = {Color.rgb(20, 230, 230), Color.rgb(200, 200, 250), Color.rgb(185, 185, 200)};
    public static int[] orangeColors = {Color.rgb(235, 150, 15), Color.rgb(250, 230, 100), Color.rgb(245, 235, 225)};
    public static int[][] allColors = {redColors, blueColors, greenColors, yellowColors, pinkColors, navyColors};

    public static double defaultLat = 0, defaultLng = 0;
//    public static String[] numbers = {"+420778725525", "+420778725526"};
//    public static String[] aliases = {"Tracker 1", "Tracker 2"};
//    public static String[] smsFormat = {"fix030s***n123456", "nofix123456", "check123456", "LL30,", "LC,"};
//    public static String[] smsAlias = {"Sledovat Tracker 1", "Nesledovat Tracker 1", "Status Tracker 1", "Sledovat Tracker 2", "Nesledovat Tracker 2"};
//    public static int[] smsNumbers = {0, 0, 0, 1, 1};
//    public static ArrayList<String> numbers;
//    public static ArrayList<String> aliases;
//    public static ArrayList<String> smsFormat;
//    public static ArrayList<String> smsAlias;
//    public static ArrayList<Integer> smsNumbers;

    public static ArrayList<FilesSetter> filesSetter;
    public static FilesDefaultCoordsSetter filesDefaultCoordsSetter;
    public static ArrayList<FilesSmsSetter> filesSmsSetter;

    public static LatLongSetter parser, prevParser, prevPrevParser;
    GoogleMap gm;
    public static boolean autoZoom = false, mapChanged = false, primaryInfoMarkerOnTop = true, addedNumber = false;
    public static int mapType;
    Bitmap icon_white;
    public static int sizeIconX = 48;
    public static int sizeIconY = 48;
    public static List<NumbersSetter> num;
    public static List<SmsFormatSetter> smsSetter;
    public static int activeNumber = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        resources = this.getResources();
        context = this;

        Files files = new Files(this);
        Log.d("existtt", String.valueOf(files.isFileExist()));
        if (!files.isFileExist()) {
            Intent intaba = new Intent(this, SettingsPhoneActivity.class);
            intaba.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            this.startActivity(intaba);
            return;
        }

        filesSetter = new ArrayList<FilesSetter>();
        filesDefaultCoordsSetter = new FilesDefaultCoordsSetter();
        filesSmsSetter = new ArrayList<FilesSmsSetter>();

        filesSetter = files.readFromXMLNumbers();
        filesDefaultCoordsSetter = files.readFromXMLDefaultCoords();
        filesSmsSetter = files.readFromXMLSms();

        try {
            defaultLat = filesDefaultCoordsSetter.getDefaultLat();
            defaultLng = filesDefaultCoordsSetter.getDefaultLng();
        } catch (NullPointerException e) {
            defaultLat = defaultLng = 0;
        }

        parser = new LatLongSetter();
        parser.setLat(defaultLat);
        parser.setLng(defaultLng);
        parser.setSpeed("0");

        img = new Images(this, this.getResources());
        icon_white = img.scaleB("icon_white", sizeIconX, sizeIconY);

        num = new ArrayList<NumbersSetter>();
        for (int i = 0; i < filesSetter.size(); i++) {
            NumbersSetter ns = new NumbersSetter();
            ns.setNumber(filesSetter.get(i).getNumber());
            ns.setAlias(filesSetter.get(i).getAlias());
            ns.setParser(parser);
            ns.setPrevParser(parser);
            ns.setPrevPrevParser(parser);
            ns.setColor(img.recolorBitmap(icon_white, allColors[filesSetter.get(i).getColor()][0]));
            ns.setColorPrev(img.recolorBitmap(icon_white, allColors[filesSetter.get(i).getColor()][1]));
            ns.setColorPrevPrev(img.recolorBitmap(icon_white, allColors[filesSetter.get(i).getColor()][2]));
            num.add(ns);
        }

        smsSetter = new ArrayList<SmsFormatSetter>();
        for (int i = 0; i < filesSmsSetter.size(); i++) {
            SmsFormatSetter sfs = new SmsFormatSetter();
            sfs.setSmsFormat(filesSmsSetter.get(i).getSmsFormat());
            sfs.setAlias(filesSmsSetter.get(i).getSmsAlias());
            sfs.setNumbersSetter(num.get(filesSmsSetter.get(i).getSmsNumber()));
            smsSetter.add(sfs);
        }

        mapType = GoogleMap.MAP_TYPE_NORMAL;

        setContentView(R.layout.main);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);  // Rychlost obnovování lokace

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        LatLng sydney = new LatLng(defaultLat, defaultLng);
        map.addMarker(new MarkerOptions().position(sydney).title(""));
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        float zoomLevel = 15.0f;
        if (defaultLat == 0 && defaultLng == 0) {
            zoomLevel = 2.0f;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, zoomLevel));
        map.setMyLocationEnabled(true);
        gm = map;

//        map.animateCamera(CameraUpdateFactory.zoomTo(17.0f));
    }

    @Override
    public void onLocationChanged(Location location) {

        gm.clear();
        if (mapChanged) {
            gm.setMapType(mapType);
            mapChanged = false;
        }

        if (addedNumber) {
            ActivityCompat.invalidateOptionsMenu(this);
            addedNumber = false;
        }

        int ind = 0;
        for (NumbersSetter ns : num) {
            double distance = getDistanceMeters(ns.getParser().getLat(), ns.getParser().getLng(), location.getLatitude(), location.getLongitude());

            LatLng prevPrevMELBOURNE = new LatLng(ns.getPrevPrevParser().getLat(), ns.getPrevPrevParser().getLng());
            gm.addMarker(new MarkerOptions()
                    .position(prevPrevMELBOURNE)
                    .title(String.valueOf("2. předchozí lokace"))
                    .snippet(String.valueOf(ns.getAlias()))
                    .icon(BitmapDescriptorFactory.fromBitmap(ns.getColorPrevPrev())));  // CUSTOM IKONKA

            LatLng prevMELBOURNE = new LatLng(ns.getPrevParser().getLat(), ns.getPrevParser().getLng());
            gm.addMarker(new MarkerOptions()
                    .position(prevMELBOURNE)
                    .title(String.valueOf("Předchozí lokace"))
                    .snippet(String.valueOf(ns.getAlias()))
                    .icon(BitmapDescriptorFactory.fromBitmap(ns.getColorPrev())));  // CUSTOM IKONKA

            LatLng MELBOURNE = new LatLng(ns.getParser().getLat(), ns.getParser().getLng());
            Marker primaryMarker = gm.addMarker(new MarkerOptions()
                    .position(MELBOURNE)
                    .title(String.valueOf("Vzdálenost | Rychlost bodu"))
                    .snippet(String.valueOf(distance + " m | " + ns.getParser().getSpeed() + " km/h >> " + ns.getAlias()))
                    .icon(BitmapDescriptorFactory.fromBitmap(ns.getColor()))); // CUSTOM IKONKA
            if (primaryInfoMarkerOnTop && ind == activeNumber) {
                primaryMarker.showInfoWindow();
            }

            gm.setOnMarkerClickListener(new OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker arg0) {
                    Toast.makeText(MainActivity.this, arg0.getTitle() + " \n" + arg0.getSnippet(), Toast.LENGTH_SHORT).show();
                    return true;
                }

            });
            ind++;
        }

        if (autoZoom) {
            LatLng myLoc = new LatLng(location.getLatitude(), location.getLongitude());
            LatLng MELBOURNE = new LatLng(num.get(activeNumber).getParser().getLat(), num.get(activeNumber).getParser().getLng());
            LatLngBounds.Builder builder = new LatLngBounds.Builder();   // AUTOMATICKY ZOOM
            builder.include(myLoc);
            builder.include(MELBOURNE);
            LatLngBounds bound = builder.build();
            gm.animateCamera(CameraUpdateFactory.newLatLngBounds(bound, 55), 1000, null);
        }

    }

    private void sendSMS(String phoneNumber, String message) {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private void call(String phoneNumber) {
        String uri = "tel:" + phoneNumber.trim();
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int j = 1;
        SubMenu sm = menu.addSubMenu("Nastavení aktivního čísla");
        for (int i = 0; i < num.size(); i++, j++) {
            sm.add(Menu.NONE, j, Menu.NONE, num.get(i).getAlias());
        }
        SubMenu smSms = menu.addSubMenu("Poslat SMS");
        for (int i = 0; i < smsSetter.size(); i++, j++) {
            smSms.add(Menu.NONE, j, Menu.NONE, smsSetter.get(i).getAlias());
        }
//        SubMenu xm = sm.addSubMenu("SubmenuSubmenu");
//        xm.add("neco v subsub menu");
        menu.add(Menu.NONE, 999000010, Menu.NONE, "Zavolat na aktivní číslo");
        menu.add(Menu.NONE, 999000011, Menu.NONE, "Nastavení");
        menu.add(Menu.NONE, 999000012, Menu.NONE, "Auto Přiblížení");
        menu.add(Menu.NONE, 999000013, Menu.NONE, "Zobrazovat info značky");
        menu.add(Menu.NONE, 999000014, Menu.NONE, "Vykreslení bodů z aktivního čísla");

        SubMenu mapTy = menu.addSubMenu("Změna mapy");
        mapTy.add(Menu.NONE, 999000015, Menu.NONE, "Klasická");
        mapTy.add(Menu.NONE, 999000016, Menu.NONE, "Satelitní");
        mapTy.add(Menu.NONE, 999000017, Menu.NONE, "Hybridní");
        mapTy.add(Menu.NONE, 999000018, Menu.NONE, "Terénní");

        menu.add(Menu.NONE, 999000019, Menu.NONE, "O aplikaci");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        for (NumbersSetter n : num) {
            if (n.getAlias().equals(item.getTitle())) {
                activeNumber = item.getItemId() - 1;
                return true;
            }
        }

        for (SmsFormatSetter smsSetter1 : smsSetter) {
            if (smsSetter1.getAlias().equals(item.getTitle())) {
                sendSMS(smsSetter1.getNumbersSetter().getNumber(), smsSetter1.getSmsFormat());
                return true;
            }
        }

        switch (item.getItemId()) {
            case 999000012:
                if (autoZoom) {
                    Toast.makeText(this, "Automatické přiblížení vypnuto", Toast.LENGTH_LONG).show();
                    autoZoom = false;
                } else {
                    Toast.makeText(this, "Automatické přiblížení zapnuto", Toast.LENGTH_LONG).show();
                    autoZoom = true;
                }
                return true;
            case 999000013:
                if (primaryInfoMarkerOnTop) {
                    Toast.makeText(this, "Zobrazování popisku značky vypnuto", Toast.LENGTH_LONG).show();
                    primaryInfoMarkerOnTop = false;
                } else {
                    Toast.makeText(this, "Zobrazování popisku značky zapnuto", Toast.LENGTH_LONG).show();
                    primaryInfoMarkerOnTop = true;
                }
                return true;
            case 999000010:
                call(num.get(activeNumber).getNumber());
                return true;
            case 999000011:
                Intent intab = new Intent(this, SettingsPhoneActivity.class);
                intab.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intab);
                return true;
            case 999000014:
                Intent intaa = new Intent(this, PointsFromAllSms.class);
                intaa.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intaa);
                return true;
            case 999000015:
                mapType = GoogleMap.MAP_TYPE_NORMAL;
                mapChanged = true;
                return true;
            case 999000016:
                mapType = GoogleMap.MAP_TYPE_SATELLITE;
                mapChanged = true;
                return true;
            case 999000017:
                mapType = GoogleMap.MAP_TYPE_HYBRID;
                mapChanged = true;
                return true;
            case 999000018:
                mapType = GoogleMap.MAP_TYPE_TERRAIN;
                mapChanged = true;
                return true;
            case 999000019:
                Intent intao = new Intent(this, AboutActivity.class);
                intao.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intao);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static long getDistanceMeters(double lat1, double lng1, double lat2, double lng2) {

        double l1 = toRadians(lat1);
        double l2 = toRadians(lat2);
        double g1 = toRadians(lng1);
        double g2 = toRadians(lng2);

        double dist = acos(sin(l1) * sin(l2) + cos(l1) * cos(l2) * cos(g1 - g2));
        if (dist < 0) {
            dist = dist + Math.PI;
        }

        return Math.round(dist * 6378100);
    }

    public void onStatusChanged(String string, int i, Bundle bundle) {
    }

    public void onProviderEnabled(String string) {
    }

    public void onProviderDisabled(String string) {
    }

}
