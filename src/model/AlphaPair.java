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
    
//    static final int SMALL_LETTER = 0;
//    static final int MEDIUM_LETTER = 1;
//    static final int BIG_LETTER = 2;
//    
//    static final int SMALL_LETTER_FONT_SIZE = 100;
//    static final int MEDIUM_LETTER_FONT_SIZE = 200;
//    static final int BIG_LETTER_FONT_SIZE = 300;
    
    /** The first letter. */
    private char letterOne;
    
    /** The second letter. */
    private char letterTwo;
    
    private int letterSizeOne;
    private int letterSizeTwo;
    
    /** The distance between the letters. */
    private int difference;
    
    /** Whether the left answer is correct or not. */
    private boolean leftCorrect;
    
    private Random randomGenerator = new Random();
    
    /** 
     * Constructor for AlphaPair.
     * @param posLetterOne The index of the first letter. A is 0, Z is 25.
     * @param posLetterTwo The index of the second letter.
     */
    public AlphaPair(int posLetterOne, int posLetterTwo) {
        this.letterOne = this.numToAlpha(posLetterOne);
        this.letterTwo = this.numToAlpha(posLetterTwo);
        this.difference = posLetterOne - posLetterTwo;
        if (this.difference > 0) {
            this.setLeftCorrect(true);
        } else if (this.difference < 0) {
            this.setLeftCorrect(false);
        }
        
        //SIZE VARIATION
        
        this.letterSizeOne = 100 * (randomGenerator.nextInt(3) + 1);
        this.letterSizeTwo = 100 * (randomGenerator.nextInt(3) + 1);
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

    public int getLetterSizeOne() {
        return letterSizeOne;
    }

    public void setLetterSizeOne(int letterSizeOne) {
        this.letterSizeOne = letterSizeOne;
    }

    public int getLetterSizeTwo() {
        return letterSizeTwo;
    }

    public void setLetterSizeTwo(int letterSizeTwo) {
        this.letterSizeTwo = letterSizeTwo;
    }
    
}
