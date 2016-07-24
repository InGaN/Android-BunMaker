package com.kevinmatsubara.japanesesentencemaker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import database.DatabaseHelper;

public class InputActivity extends AppCompatActivity {
    private DatabaseHelper db;
    EditText tbx_furigana;
    EditText tbx_kanji;
    EditText tbx_meaning;
    EditText lastSelectedEditText;

    Spinner spn_inputType;
    Spinner spn_inputCategoryItem;

    Button btn_addNew;
    Button btn_insertCategoryItem;

    private enum InsertType {
        SENTENCE(0), CATEGORY_TYPE(1), CATEGORY_ITEM(2);
        private long id;
        InsertType(long id){this.id = id;}
        public long getID() {return id;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        db = new DatabaseHelper(InputActivity.this);

        tbx_furigana = (EditText)findViewById(R.id.tbx_addFurigana);
        tbx_kanji = (EditText)findViewById(R.id.tbx_addKanji);
        tbx_meaning = (EditText)findViewById(R.id.tbx_addMeaning);

        btn_addNew = (Button)findViewById(R.id.btn_addNew);
        btn_insertCategoryItem = (Button)findViewById(R.id.btn_insertCategoryItem);

        setSpinners();

        btn_addNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (spn_inputType.getSelectedItemId() == InsertType.SENTENCE.getID()) {
                    insertIntoDatabase(InsertType.SENTENCE);
                } else if (spn_inputType.getSelectedItemId() == InsertType.CATEGORY_TYPE.getID()) {
                    insertIntoDatabase(InsertType.CATEGORY_TYPE);
                } else if (spn_inputType.getSelectedItemId() == InsertType.CATEGORY_ITEM.getID()) {
                    insertIntoDatabase(InsertType.CATEGORY_ITEM);
                }
            }
        });


        btn_insertCategoryItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertCategoryItem();
            }
        });

        lastSelectedEditText = tbx_furigana;
        tbx_furigana.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1) {
                lastSelectedEditText = tbx_furigana;
                return false;
            }
        });
        tbx_kanji.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                lastSelectedEditText = tbx_kanji;
                return false;
            }
        });
        tbx_meaning.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                lastSelectedEditText = tbx_meaning;
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_input, menu);
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

    private void insertIntoDatabase(InsertType type) {
        switch (type) {
            case SENTENCE:
                db.insertSentence(tbx_furigana.getText().toString(), tbx_kanji.getText().toString(), tbx_meaning.getText().toString());
                break;
            case CATEGORY_TYPE:
                if(tbx_meaning.getText().toString().toLowerCase() != "listItems") {
                    db.insertCategoryType(tbx_meaning.getText().toString());

                    ArrayAdapter<String> adapter = new ArrayAdapter(InputActivity.this, android.R.layout.simple_spinner_item, db.getCategoryTypes());
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spn_inputCategoryItem.setAdapter(adapter);
                }
                else {
                    MainActivity.showAlert(InputActivity.this, "ERROR", tbx_meaning.getText().toString().toLowerCase() + " is an invalid name for a category...");
                }
                break;
            case CATEGORY_ITEM:
                db.insertCategoryItem(spn_inputCategoryItem.getSelectedItem().toString(), tbx_furigana.getText().toString(), tbx_kanji.getText().toString(), tbx_meaning.getText().toString());
                MainActivity.showAlert(InputActivity.this, "ERROR", spn_inputCategoryItem.getSelectedItem().toString());
                break;
        }

        clearTextBoxes();
    }

    private void insertCategoryItem() {
        int start = Math.max(lastSelectedEditText.getSelectionStart(), 0);
        int end = Math.max(lastSelectedEditText.getSelectionEnd(), 0);
        String insert = "<" + spn_inputCategoryItem.getSelectedItem().toString() + ">";

        lastSelectedEditText.getText().replace(Math.min(start, end), Math.max(start, end),
                insert, 0, insert.length());
    }

    private void setSpinners() {
        spn_inputType = (Spinner) findViewById(R.id.spn_inputType);
        spn_inputCategoryItem = (Spinner) findViewById(R.id.spn_inputCategoryItem);

        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this, R.array.inputTypes, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_inputType.setAdapter(adapter1);

        spn_inputType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                clearTextBoxes();
                if (id == InsertType.SENTENCE.getID()) {
                    tbx_furigana.setVisibility(View.VISIBLE);
                    tbx_kanji.setVisibility(View.VISIBLE);
                    tbx_meaning.setVisibility(View.VISIBLE);
                    btn_addNew.setVisibility(View.VISIBLE);
                    btn_insertCategoryItem.setVisibility(View.VISIBLE);
                    if (db.getCategoryTypes().length > 0) {
                        btn_insertCategoryItem.setVisibility(View.VISIBLE);
                        spn_inputCategoryItem.setVisibility(View.VISIBLE);
                    } else {
                        btn_insertCategoryItem.setVisibility(View.GONE);
                        spn_inputCategoryItem.setVisibility(View.GONE);
                    }
                } else if (id == InsertType.CATEGORY_TYPE.getID()) {
                    spn_inputCategoryItem.setVisibility(View.GONE);
                    tbx_furigana.setVisibility(View.GONE);
                    tbx_kanji.setVisibility(View.GONE);
                    tbx_meaning.setVisibility(View.VISIBLE);
                    btn_addNew.setVisibility(View.VISIBLE);
                    btn_insertCategoryItem.setVisibility(View.GONE);
                } else if (id == InsertType.CATEGORY_ITEM.getID()) {
                    if (db.getCategoryTypes().length > 0) {
                        spn_inputCategoryItem.setVisibility(View.VISIBLE);
                        tbx_furigana.setVisibility(View.VISIBLE);
                        tbx_kanji.setVisibility(View.VISIBLE);
                        tbx_meaning.setVisibility(View.VISIBLE);
                        btn_addNew.setVisibility(View.VISIBLE);
                        btn_insertCategoryItem.setVisibility(View.GONE);
                    } else {
                        spn_inputType.setSelection((int) InsertType.CATEGORY_TYPE.getID());
                        MainActivity.showAlert(InputActivity.this, "ERROR", "You have no categories, add one first");
                    }
                }
                //spinnerChanged((int) id, "questionMode", settings, new VisibleTimerOptions(), (id == 0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, db.getCategoryTypes());
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spn_inputCategoryItem.setAdapter(adapter2);
    }

    private void clearTextBoxes() {
        tbx_furigana.setText("");
        tbx_kanji.setText("");
        tbx_meaning.setText("");
    }
}
