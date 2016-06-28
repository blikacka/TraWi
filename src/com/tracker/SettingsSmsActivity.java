package com.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;
import java.util.ArrayList;
import static java.util.Collections.list;
import java.util.HashMap;
import java.util.List;

/**
 *
 * @author Notasek
 */
public class SettingsSmsActivity extends Activity {

    EditText smsformat, aliassms;
    Spinner phonesms;
    Button button, buttonAddNewSms, buttonRemoveSelected;
    Context context;
    ArrayList<String> arrayForList;
    ArrayList<FilesSmsSetter> fs;
    StableArrayAdapter adapter;
    ListView listview;
    int pos;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settingssms);
        context = this;
        final Files files = new Files(this);

        listview = (ListView) findViewById(R.id.listview);

        arrayForList = new ArrayList<String>();

        fs = new ArrayList<FilesSmsSetter>();

        try {
            fs = files.readFromXMLSms();
        } catch (NullPointerException e) {
        }

        adapterForList();

        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                pos = position;
            }
        });

        smsformat = (EditText) findViewById(R.id.smsformat);
        aliassms = (EditText) findViewById(R.id.aliassms);
        phonesms = (Spinner) findViewById(R.id.phonesms);
        button = (Button) findViewById(R.id.widget91);
        buttonAddNewSms = (Button) findViewById(R.id.widge);
        buttonRemoveSelected = (Button) findViewById(R.id.removeselected);

        List<String> listNumbers = new ArrayList<String>();
        ArrayList<FilesSetter> fis = files.readFromXMLNumbers();
        if (fis != null) {
            for (FilesSetter ff : fis) {
                listNumbers.add(ff.getAlias());
            }
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, listNumbers);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phonesms.setAdapter(dataAdapter);

        button.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                try {
                    files.saveToXMLSms(fs);
                    Toast.makeText(context, "Záznamy byly úspěšně uloženy", Toast.LENGTH_LONG).show();
                    Intent intac = new Intent(context, MainActivity.class);
                    intac.addCategory(Intent.CATEGORY_HOME);
//                    intac.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intac.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(intac);
                    finish();
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

                }
            }
        });

        buttonAddNewSms.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                String formatFromE = smsformat.getText().toString();
                String aliasesFromE = aliassms.getText().toString();
                int colorsFromS = phonesms.getSelectedItemPosition();
                FilesSmsSetter filesSetter = new FilesSmsSetter();
                filesSetter.setSmsAlias(aliasesFromE);
                filesSetter.setSmsFormat(formatFromE);
                filesSetter.setSmsNumber(colorsFromS);
                if (fs == null) {
                    fs = new ArrayList<FilesSmsSetter>();
                }
                fs.add(filesSetter);
                adapterForList();

                LinearLayout results = (LinearLayout) findViewById(R.id.layoutlistview);
                listview.setAdapter(adapter);
                results.removeAllViews();
                results.addView(listview);
                smsformat.setText("");
                aliassms.setText("");

            }
        });

        buttonRemoveSelected.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                fs.remove(pos);
                adapterForList();

                LinearLayout results = (LinearLayout) findViewById(R.id.layoutlistview);
                listview.setAdapter(adapter);
                results.removeAllViews();
                results.addView(listview);
            }
        });

    }

    public void adapterForList() {
        arrayForList.clear();
        if (fs != null) {
            for (int i = 0; i < fs.size(); i++) {
                arrayForList.add(fs.get(i).getSmsFormat() + " | " + fs.get(i).getSmsAlias()+ " | " + String.valueOf(fs.get(i).getSmsNumber()) + " | ");
            }
        }
        adapter = new StableArrayAdapter(context, android.R.layout.simple_list_item_1, arrayForList);
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
