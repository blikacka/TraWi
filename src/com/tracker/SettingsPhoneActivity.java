package com.tracker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
public class SettingsPhoneActivity extends Activity {

    EditText numbersEdit, aliasesEdit;
    Spinner colorsEdit;
    Button button, buttonAddNewNumber, buttonRemoveSelected;
    Context context;
    ArrayList<String> arrayForList;
    ArrayList<FilesSetter> fs;
    StableArrayAdapter adapter;
    ListView listview;
    int pos;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.settingsphone);
        context = this;
        final Files files = new Files(this);

        listview = (ListView) findViewById(R.id.listview);

        arrayForList = new ArrayList<String>();

        fs = new ArrayList<FilesSetter>();

        try {
            fs = files.readFromXMLNumbers();
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

        numbersEdit = (EditText) findViewById(R.id.phonenum);
        aliasesEdit = (EditText) findViewById(R.id.phonealias);
        colorsEdit = (Spinner) findViewById(R.id.phonecolor);
        button = (Button) findViewById(R.id.widget91);
        buttonAddNewNumber = (Button) findViewById(R.id.widge);
        buttonRemoveSelected = (Button) findViewById(R.id.removeselected);

        List<String> listColors = new ArrayList<String>();
        listColors.add("Červená");
        listColors.add("Modrá");
        listColors.add("Zelená");
        listColors.add("Žlutá");
        listColors.add("Růžová");
        listColors.add("Světle modrá");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, listColors);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorsEdit.setAdapter(dataAdapter);

        button.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                try {
                    files.saveToXMLNumbers(fs);
                    Toast.makeText(context, "Záznamy byly úspěšně uloženy", Toast.LENGTH_LONG).show();
                    Intent intac = new Intent(context, SettingDefaultCoordsActivity.class);
                    intac.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intac);
                } catch (Exception e) {
                    Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();

                }
            }
        });

        buttonAddNewNumber.setOnClickListener(new OnClickListener() {
            @Override

            public void onClick(View view) {
                String numbersFromE = numbersEdit.getText().toString();
                String aliasesFromE = aliasesEdit.getText().toString();
                int colorsFromS = colorsEdit.getSelectedItemPosition();
                FilesSetter filesSetter = new FilesSetter();
                filesSetter.setAlias(aliasesFromE);
                filesSetter.setNumber(numbersFromE);
                filesSetter.setColor(colorsFromS);
                if (fs == null) {
                    fs = new ArrayList<FilesSetter>();
                }
                fs.add(filesSetter);
                adapterForList();

                LinearLayout results = (LinearLayout) findViewById(R.id.layoutlistview);
                listview.setAdapter(adapter);
                results.removeAllViews();
                results.addView(listview);
                numbersEdit.setText("");
                aliasesEdit.setText("");

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
                arrayForList.add(fs.get(i).getNumber() + " | " + fs.get(i).getAlias() + " | " + String.valueOf(fs.get(i).getColor()) + " | ");
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
