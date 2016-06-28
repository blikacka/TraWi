package com.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

/**
 *
 * @author Notasek
 */
public class SettingDefaultCoordsActivity extends Activity {

    double lat = 0, lng = 0;
    EditText latitude, longitude;
    Button button;
    Context context;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settingscoords);

        context = this;
        Files files = new Files(this);

        try {

            FilesDefaultCoordsSetter fs = files.readFromXMLDefaultCoords();

            lat = fs.getDefaultLat();
            lng = fs.getDefaultLng();
        } catch (NullPointerException e) {
        }

        latitude = (EditText) findViewById(R.id.latitude);
        longitude = (EditText) findViewById(R.id.longitude);
        button = (Button) findViewById(R.id.widget91);

        latitude.setText(String.valueOf(lat));
        longitude.setText(String.valueOf(lng));

        button.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View view) {
                String latFromE = latitude.getText().toString();
                String lngFromE = longitude.getText().toString();
                Parser parser = new Parser();
                FilesDefaultCoordsSetter fdcs = new FilesDefaultCoordsSetter();
                if (isNumeric(latFromE)) {
                    fdcs.setDefaultLat(Double.parseDouble(latFromE));
                } else {
                    fdcs.setDefaultLat(0);
                }
                if (isNumeric(lngFromE)) {
                    fdcs.setDefaultLng(Double.parseDouble(lngFromE));
                } else {
                    fdcs.setDefaultLng(0);
                }
                Files files = new Files(context);
                files.saveToXMLDefaultCoords(fdcs);
                Toast.makeText(context, "Záznamy byly úspěšně uloženy", Toast.LENGTH_LONG).show();
                Intent intad = new Intent(context, SettingsSmsActivity.class);
                intad.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intad);

            }
        });
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
