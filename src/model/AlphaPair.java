package model;

import java.util.Random;
/** 
 * Object to represent a pair of capital alphabetical letters.
 * 
 * @author Tony Jiang
 * 6-25-2015
 * 
 */
public class AlphaPair {

    /** Number added to get the proper ASCII capital letter. */
    static final int ASCII_DIFF = 65;
    
//    static final int SMALL_CHOICE = 0;
//    static final int MEDIUM_CHOICE = 1;
//    static final int BIG_CHOICE = 2;
    
//    static final int SMALL_CHOICE_FONT_SIZE = 150;
//    static final int MEDIUM_CHOICE_FONT_SIZE = 200;
//    static final int BIG_CHOICE_FONT_SIZE = 300;
    
//    static final double EASY_MODE_FONT_RATIO = .4;
//    static final double MEDIUM_MODE_FONT_RATIO = .7;
//    static final double HARD_MODE_FONT_RATIO = .85;
    
    /** The first letter. */
    private char letterOne;
    
    /** The second letter. */
    private char letterTwo;
    
    private int fontSizeOne;
    private int fontSizeTwo;
    
    /** The distance between the letters. */
    private int difference;
//    
//    /** Difficulty of this pair, determined by distance */
//    private int difficulty;
//    
//    public int getDifficulty() {
//        return difficulty;
//    }
//
//    public void setDifficulty(int difficulty) {
//        this.difficulty = difficulty;
//    }

    /** Whether the left answer is correct or not. */
    private boolean leftCorrect;
    
//    /** 
//     * Constructor for AlphaPair.
//     * @param posLetterOne The index of the first letter. A is 0, Z is 25.
//     * @param posLetterTwo The index of the second letter.
//     * @param difficultyMode the difficulty of this pair, used to determine font sizes.
//     */
//    public AlphaPair(int posLetterOne, int posLetterTwo, int difficultyMode) {
//        this.difficulty = difficultyMode;
//        this.letterOne = this.numToAlpha(posLetterOne);
//        this.letterTwo = this.numToAlpha(posLetterTwo);
//        this.setChoiceSizes(this.difficulty);
//        this.difference = posLetterOne - posLetterTwo;
//        if (this.difference > 0) {
//            this.setLeftCorrect(true);
//        } else if (this.difference < 0) {
//            this.setLeftCorrect(false);
//        }
//    }
    
    /** 
     * Constructor for AlphaPair.
     * @param posLetterOne The index of the first letter. A is 0, Z is 25.
     * @param posLetterTwo The index of the second letter.
     * @param fontSizeOne font size of the first letter.
     * @param fontSizeTwo font size of the second letter..
     */
    public AlphaPair(int posLetterOne, int posLetterTwo, int fontSizeOne, int fontSizeTwo) {
        
        this.letterOne = this.numToAlpha(posLetterOne);
        this.letterTwo = this.numToAlpha(posLetterTwo);
        this.setFontSizeOne(fontSizeOne);
        this.setFontSizeTwo(fontSizeTwo);
        this.difference = posLetterOne - posLetterTwo;
        if (this.difference > 0) {
            this.setLeftCorrect(true);
        } else if (this.difference < 0) {
            this.setLeftCorrect(false);
        }
    }
    
    /**
     * Converts a number index to its corresponding letter. A is 0, Z is 25.
     * @param posLetter Index of the letter desired.
     * @return char of the alphabetical character converted from the number.
     */
    private char numToAlpha(int posLetter) {
        posLetter += ASCII_DIFF;
        char letter = Character.toChars(posLetter)[0];
        return letter;
    }

    public char getLetterOne() {
        return this.letterOne;
    }

    public void setLetterOne(char letterOne) {
        this.letterOne = letterOne;
    }

    public char getLetterTwo() {
        return this.letterTwo;
    }

    public void setLetterTwo(char letterTwo) {
        this.letterTwo = letterTwo;
    }


    public int getDifference() {
        return this.difference;
    }


    public void setDifference(int difference) {
        this.difference = difference;
    }

    public boolean isLeftCorrect() {
        return this.leftCorrect;
    }

    public void setLeftCorrect(boolean leftCorrect) {
        this.leftCorrect = leftCorrect;
    }

    public int getFontSizeOne() {
        return fontSizeOne;
    }

    public void setFontSizeOne(int fontSizeOne) {
        this.fontSizeOne = fontSizeOne;
    }

    public int getFontSizeTwo() {
        return fontSizeTwo;
    }

    public void setFontSizeTwo(int fontSizeTwo) {
        this.fontSizeTwo = fontSizeTwo;
    }
}
