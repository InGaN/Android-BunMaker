package com.kevinmatsubara.japanesesentencemaker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

import database.DatabaseHelper;

public class ListActivity extends AppCompatActivity {
    private final String MAIN_LIST = "Sentences";

    DatabaseHelper db;
    CustomListAdapter customListAdapter;
    ArrayList<Sentence> listItems = new ArrayList<>();
    ListView list;
    Spinner spn_lists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("");
        db = new DatabaseHelper(ListActivity.this);
        setContentView(R.layout.activity_list);
        list = (ListView) findViewById(R.id.lst_itemsList);

        spn_lists = (Spinner) findViewById(R.id.spn_lists);

        String[] spinnerItems = new String[db.getCategoryTypes().length + 1];
        spinnerItems[0] = MAIN_LIST;
        for (int x = 1; x <= db.getCategoryTypes().length; x++) {
            spinnerItems[x] = "Category - " + db.getCategoryTypes()[x-1];
        }

        ArrayAdapter<String> adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, spinnerItems);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_lists.setAdapter(adapter);

        spn_lists.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = spn_lists.getSelectedItem().toString();
                if (item.equals(MAIN_LIST)) {
                    initializeSentences();
                }
                else {
                    item = item.split("-")[1].trim();
                    initializeCategoryItems(item);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        initializeSentences();

        TextView lbl_emptyList = (TextView)findViewById(R.id.lbl_emptyList);
        lbl_emptyList.setVisibility((listItems.size() > 0) ? View.GONE : View.VISIBLE);
        //spn_lists.setVisibility((listItems.size() > 0) ? View.VISIBLE : View.VISIBLE);


        Intent intent = getIntent();
        if(intent.getExtras() != null) {
            if (!intent.getStringExtra("str_furigana").equals("") || !intent.getStringExtra("str_kanji").equals("") || !intent.getStringExtra("str_meaning").equals("")) {
                // determine if list needs to fill with listItems or category items first.
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        // add category types
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initializeSentences() {
        listItems = db.getSentences();

        if(listItems.size() > 0) {
            customListAdapter = new CustomListAdapter(this, listItems);
            list.setAdapter(customListAdapter);
        }
    }

    private void initializeCategoryItems(String type) {
        listItems = db.getCategoryItems(type);

        if(listItems.size() > 0) {
            customListAdapter = new CustomListAdapter(this, listItems);
            list.setAdapter(customListAdapter);
        }
    }
}
