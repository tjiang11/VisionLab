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
    
    static final int SMALL_CHOICE = 0;
    static final int MEDIUM_CHOICE = 1;
    static final int BIG_CHOICE = 2;
    
    static final int SMALL_CHOICE_FONT_SIZE = 150;
    static final int MEDIUM_CHOICE_FONT_SIZE = 200;
    static final int BIG_CHOICE_FONT_SIZE = 300;
    
    static final double EASY_MODE_FONT_RATIO = .4;
    static final double MEDIUM_MODE_FONT_RATIO = .7;
    static final double HARD_MODE_FONT_RATIO = .85;
    
    /** The first letter. */
    private char letterOne;
    
    /** The second letter. */
    private char letterTwo;
    
    private int letterSizeOne;
    private int letterSizeTwo;
    
    /** The distance between the letters. */
    private int difference;
    
    /** Difficulty of this pair, determined by distance */
    private int difficulty;
    
    public int getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(int difficulty) {
        this.difficulty = difficulty;
    }

    /** Whether the left answer is correct or not. */
    private boolean leftCorrect;
    
    /** Random number generator */
    private Random randomGenerator = new Random();
    
    /** 
     * Constructor for AlphaPair.
     * @param posLetterOne The index of the first letter. A is 0, Z is 25.
     * @param posLetterTwo The index of the second letter.
     * @param difficultyMode the difficulty of this pair, used to determine font sizes.
     */
    public AlphaPair(int posLetterOne, int posLetterTwo, int difficultyMode) {
        this.difficulty = difficultyMode;
        this.letterOne = this.numToAlpha(posLetterOne);
        this.letterTwo = this.numToAlpha(posLetterTwo);
        this.setChoiceSizes(this.difficulty);
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
    
    /**
     * Set the font size for each letter based on the difficulty (which
     * is determined by the distance between the choices). Higher distances
     *  --> higher font ratio. Smaller distances --> smaller font ratio.
     * @param difficultyMode
     */
    public void setChoiceSizes(int difficultyMode) {
        int baseSize = this.chooseBaseSize();
        System.out.println("BASE: " + baseSize);
        this.letterSizeOne = baseSize;
        switch (difficultyMode) {
        case AlphaPairGenerator.EASY_MODE:
            this.letterSizeTwo = (int) (EASY_MODE_FONT_RATIO * baseSize);
            break;
        case AlphaPairGenerator.MEDIUM_MODE:
            this.letterSizeTwo = (int) (MEDIUM_MODE_FONT_RATIO * baseSize);
            break;
        case AlphaPairGenerator.HARD_MODE:
            this.letterSizeTwo = (int) (HARD_MODE_FONT_RATIO * baseSize);
            break;
        }
        
        int switchSizes = randomGenerator.nextInt(2);
        System.out.println(switchSizes);
        if (switchSizes == 0) {
            int tempSize = this.letterSizeOne;
            this.letterSizeOne = this.letterSizeTwo;
            this.letterSizeTwo = tempSize;
        }
    }
    
    /**
     * Determine the base font size to build the font ratio off of. The other
     * font size choice will be scaled down from this font size.
     * @return int The base font size.
     */
    public int chooseBaseSize() {
        int choiceOfSize = randomGenerator.nextInt(3);
        switch (choiceOfSize) {
        case SMALL_CHOICE:
            return SMALL_CHOICE_FONT_SIZE;
        case MEDIUM_CHOICE:
            return MEDIUM_CHOICE_FONT_SIZE;
        case BIG_CHOICE:
            return BIG_CHOICE_FONT_SIZE;
        }
        return 0;
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
