package com.kevinmatsubara.japanesesentencemaker;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kevin on 2016/07/24.
 */
public class CustomListAdapter extends ArrayAdapter<Sentence> {
    private Activity activity;
    private ArrayList<Sentence> sentences;

    public CustomListAdapter(Activity activity, ArrayList<Sentence> sentences) {
        super(activity, R.layout.sentence_list_item, sentences);
        this.activity = activity;
        this.sentences = sentences;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = activity.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sentence_list_item, null, true);

        TextView lbl_id = (TextView) rowView.findViewById(R.id.lbl_item_id);
        TextView lbl_kanji = (TextView) rowView.findViewById(R.id.lbl_item_kanji);
        TextView lbl_furigana = (TextView) rowView.findViewById(R.id.lbl_item_furigana);
        TextView lbl_meaning = (TextView) rowView.findViewById(R.id.lbl_item_meaning);

        lbl_id.setText("" + sentences.get(position).getID());
        lbl_kanji.setText(sentences.get(position).getKanji());
        lbl_furigana.setText(sentences.get(position).getFurigana());
        lbl_meaning.setText(sentences.get(position).getMeaning());

        return rowView;
    }
}
