package com.kevinmatsubara.japanesesentencemaker;

/**
 * Created by kevin on 2016/07/10.
 */
public class Bun {

    private int id;
    private String kanji;
    private String furigana;
    private String meaning;

    private String sentence;
    private String[] answers;

    public Bun(int id, String kanji, String furigana, String meaning){
        this.id = id;
        this.kanji = kanji;
        this.furigana = furigana;
        this.meaning = meaning;
    }

    public int get_id() {
        return id;
    }

    public String get_kanji() {
        return kanji;
    }

    public String get_furigana() {
        return furigana;
    }

    public String get_meaning() {
        return meaning;
    }
}
