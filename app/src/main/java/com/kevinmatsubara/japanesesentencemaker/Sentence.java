package com.kevinmatsubara.japanesesentencemaker;

/**
 * Created by kevin on 2016/07/10.
 */
public class Sentence {

    private int id;
    private String kanji;
    private String furigana;
    private String meaning;

    private String sentence;
    private String[] answers;

    public Sentence(int id, String furigana, String kanji, String meaning){
        this.id = id;
        this.furigana = furigana;
        this.kanji = kanji;
        this.meaning = meaning;
    }

    public int getID() {
        return id;
    }

    public String getKanji() {
        return kanji;
    }

    public String getFurigana() {
        return furigana;
    }

    public String getMeaning() {
        return meaning;
    }

    public void setFurigana(String input) {
        this.furigana = input;
    }
    public void setKanji(String input) {
        this.kanji = input;
    }
    public void setMeaning(String input) {
        this.meaning = input;
    }
}
