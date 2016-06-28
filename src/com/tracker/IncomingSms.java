package com.tracker;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import com.mapswithme.maps.api.MapsWithMeApi;
import static com.tracker.MainActivity.allColors;
import static com.tracker.MainActivity.orangeColors;
import java.util.Arrays;

/**
 *
 * @author Notasek
 */
public class IncomingSms extends BroadcastReceiver {

    final SmsManager sms = SmsManager.getDefault();
    public String contain1 = "lat";
    public String contain2 = "long";
    public String contain3 = "speed";

    public void onReceive(Context context, Intent intent) {

        final Bundle bundle = intent.getExtras();
        String[] ll = {"0", "0"};

        try {

            if (bundle != null) {

                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    int indexNumber = -1;
                    for (int k = 0; k < MainActivity.num.size(); k++) {
                        if (MainActivity.num.get(k).getNumber().equals(senderNum)) {
                            indexNumber = k;
                        }
                    }
                    Parser parser = new Parser(message, senderNum);

                    if (indexNumber != -1) {
                        MainActivity.num.get(indexNumber).setPrevPrevParser(MainActivity.num.get(indexNumber).getPrevParser());
                        MainActivity.num.get(indexNumber).setPrevParser(MainActivity.num.get(indexNumber).getParser());
                        MainActivity.num.get(indexNumber).setParser(parser.toCoords());
                    } else {
                        NumbersSetter numbersetter = new NumbersSetter();
                        String contactName = getContactName(MainActivity.context, senderNum);
                        numbersetter.setNumber(senderNum);
                        if (contactName != null) {
                            numbersetter.setAlias(contactName);
                        } else {
                            numbersetter.setAlias("Číslo " + senderNum);
                        }
                        numbersetter.setPrevPrevParser(parser.toCoords());
                        numbersetter.setPrevParser(parser.toCoords());
                        numbersetter.setParser(parser.toCoords());
                        Images img = new Images(MainActivity.context, MainActivity.resources);
                        Bitmap icon_white = img.scaleB("icon_white", MainActivity.sizeIconX, MainActivity.sizeIconY);
                        numbersetter.setColor(img.recolorBitmap(icon_white, orangeColors[0]));
                        numbersetter.setColorPrev(img.recolorBitmap(icon_white, orangeColors[1]));
                        numbersetter.setColorPrevPrev(img.recolorBitmap(icon_white, orangeColors[2]));
                        MainActivity.num.add(numbersetter);
                        MainActivity.addedNumber = true;
                    }

                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context, "Cislo: " + senderNum + ", Zprava: " + message, duration);
                    toast.show();

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" + e);

        }
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(PhoneLookup.DISPLAY_NAME));
        }

        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }

}
