package com.tracker;

import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.WindowManager;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.ArrayList;
import java.util.List;

public class PointsFromAllSms extends FragmentActivity implements OnMapReadyCallback {

    Marker now;
    public static double lat = 49, lng = 18;
    GoogleMap gm;
    String[] ll = {"0", "0"};
    PolylineOptions line;
    ArrayList<LatLng> po;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.main);
        po = new ArrayList<LatLng>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        line = new PolylineOptions();

        List<Sms> list = getAllSms();
        if (list.isEmpty()) {
            Toast.makeText(this, "Nejsou žádné SMS", Toast.LENGTH_LONG).show();
            return;
        }

        for (Sms list1 : list) {
            if ("inbox".equals(list1.getFolderName()) && MainActivity.filesSetter.get(MainActivity.activeNumber).getNumber().equals(list1.getAddress())) {
                Parser parser = new Parser(list1.getMsg(), list1.getAddress());
                LatLongSetter latLongSetter = parser.toCoords();
                if (latLongSetter.getLat() != 0 && latLongSetter.getLng() != 0) {
                    LatLng latLng = new LatLng(latLongSetter.getLat(), latLongSetter.getLng());
                    po.add(latLng);
                }
            }
        }
        LatLng[] stockArr = new LatLng[po.size()];
        stockArr = po.toArray(stockArr);
        line.add(stockArr);
        line.width(5).color(Color.RED);

    }

    @Override
    public void onMapReady(final GoogleMap map) {
        if (po.isEmpty()) {
            Toast.makeText(this, "Nejsou žádné SMS", Toast.LENGTH_LONG).show();
            return;
        }
        LatLng sydney = new LatLng(po.get(0).latitude, po.get(0).longitude);
        map.addMarker(new MarkerOptions().position(sydney).title(""));
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15.0f));
        map.setMyLocationEnabled(true);
        map.addPolyline(line);

        for (LatLng x : po) {
            map.addMarker(new MarkerOptions()
                    .position(x)
                    .title(String.valueOf("LAT: " + x.latitude))
                    .snippet(String.valueOf("LONG: " + x.longitude)));
        }
    }

    public List<Sms> getAllSms() {
        List<Sms> lstSms = new ArrayList<Sms>();
        Sms objSms;
        Uri message = Uri.parse("content://sms/");
        ContentResolver cr = this.getContentResolver();

        Cursor c = cr.query(message, null, null, null, null);
        this.startManagingCursor(c);
        int totalSMS = c.getCount();

        if (c.moveToFirst()) {
            for (int i = 0; i < totalSMS; i++) {

                objSms = new Sms();
                objSms.setId(c.getString(c.getColumnIndexOrThrow("_id")));
                objSms.setAddress(c.getString(c.getColumnIndexOrThrow("address")));
                objSms.setMsg(c.getString(c.getColumnIndexOrThrow("body")));
                objSms.setReadState(c.getString(c.getColumnIndex("read")));
                objSms.setTime(c.getString(c.getColumnIndexOrThrow("date")));
                if (c.getString(c.getColumnIndexOrThrow("type")).contains("1")) {
                    objSms.setFolderName("inbox");
                } else {
                    objSms.setFolderName("sent");
                }

                lstSms.add(objSms);
                c.moveToNext();
            }
        }
        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        c.close();

        return lstSms;
    }

    public static boolean isNumeric(String str) {
        try {
            double d = Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

}
