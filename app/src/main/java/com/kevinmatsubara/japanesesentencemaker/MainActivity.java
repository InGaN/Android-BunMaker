package com.kevinmatsubara.japanesesentencemaker;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.content.SharedPreferences;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import database.DatabaseHelper;
import database.DatabaseInitializer;

public class MainActivity extends AppCompatActivity {
    public static String PACKAGE_NAME;
    private DatabaseHelper db;

    Button btn_db;
    Button btn_del;
    Button btn_check;
    TextView lbl_row_1;
    TextView lbl_row_2;
    TextView lbl_row_3;

    ArrayList<Sentence> sentences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PACKAGE_NAME = getApplicationContext().getPackageName();

        db = new DatabaseHelper(MainActivity.this);

        lbl_row_1 =  (TextView)findViewById(R.id.lbl_main_row_1);
        lbl_row_2 =  (TextView)findViewById(R.id.lbl_main_row_2);
        lbl_row_3 =  (TextView)findViewById(R.id.lbl_main_row_3);

        btn_db = (Button)findViewById(R.id.btn_db);
        btn_db.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(db.createMainTables()) {
                    showAlert(MainActivity.this, "WELCOME", "You made a database.");
                    SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFERENCES_FILE_NAME, 0);
                    initializeSentences(settings);
                }
                else {
                    showAlert(MainActivity.this, "ERROR", "You have no listItems.");
                }
            }
        });

        btn_del = (Button)findViewById(R.id.btn_del);
        btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.deleteDatabase();
                showAlert(MainActivity.this, "WELCOME", "Database deleted.");
                lbl_row_1.setText("-");
                lbl_row_2.setText("-");
                lbl_row_3.setText("-");
            }
        });

        btn_check = (Button)findViewById(R.id.btn_check);
        btn_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (db.checkIfTableExists()) {
                    if (db.getTableSize(DatabaseInitializer.TABLE_NAME_MAIN) > 0) {
                        Sentence sentence = parseSentence(db.getRandomSentence());
                        lbl_row_1.setText(sentence.getFurigana());
                        lbl_row_2.setText(sentence.getKanji());
                        lbl_row_3.setText(sentence.getMeaning());
                    } else {
                        lbl_row_1.setText("You have no listItems inside the database!");
                        lbl_row_2.setText("You have no listItems inside the database!");
                        lbl_row_3.setText("You have no listItems inside the database!");
                    }
                } else {
                    showAlert(MainActivity.this, "ERROR", "it does NOT exist.");
                }
            }
        });
    }

    @Override
    public void onResume() {
        SharedPreferences settings = getSharedPreferences(SettingsActivity.PREFERENCES_FILE_NAME, 0);
        super.onResume();
    }

    private void initializeSentences(SharedPreferences settings) {
        sentences = getSentencesFromDatabase(settings);
    }

    private ArrayList<Sentence> getSentencesFromDatabase(SharedPreferences settings) {
        DatabaseHelper db = new DatabaseHelper(MainActivity.this);
        return db.getSentences();
    }

    private Sentence parseSentence(Sentence sentence) {
        String[] categories = db.getCategoryTypes();

        boolean appendActive = false;
        String term = "";

        Sentence placeholder = new Sentence(sentence.getID(), sentence.getFurigana(), sentence.getKanji(), sentence.getMeaning());
        for(int x = 0; x < sentence.getFurigana().toCharArray().length; x++) {
            if (sentence.getFurigana().toCharArray()[x] == '>') {
                appendActive = false;
                Sentence categoryItem = db.getRandomCategoryItem(term); // call once
                placeholder.setFurigana(placeholder.getFurigana().replaceFirst("<" + term + ">", categoryItem.getFurigana()));
                placeholder.setKanji(placeholder.getKanji().replaceFirst("<" + term + ">", categoryItem.getKanji()));
                placeholder.setMeaning(placeholder.getMeaning().replaceFirst("<" + term + ">", categoryItem.getMeaning()));
                term = "";
            }
            if(appendActive) {
                term += sentence.getFurigana().toCharArray()[x];
            }
            if (sentence.getFurigana().toCharArray()[x] == '<') {
                appendActive = true;
            }
        }
        return placeholder;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.action_input:
                callInputActivity();
                return true;
            case R.id.action_list:
                callListActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static void showAlert(Context context, String title, String message) {
        final SpannableString s = new SpannableString(message);
        Linkify.addLinks(s, Linkify.WEB_URLS);

        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(s);
        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
        TextView textView = (TextView) alertDialog.findViewById(android.R.id.message);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
    }

    static void FisherYatesShuffleArray(int[] array) {
        int n = array.length;
        for (int i = 0; i < array.length; i++) {
            int random = i + (int) (Math.random() * (n - i));
            int randomElement = array[random];
            array[random] = array[i];
            array[i] = randomElement;
        }
    }

    private void callInputActivity() {
        Intent intent = new Intent(this, InputActivity.class);
        startActivity(intent);
    }

    private void callListActivity() {
        Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
