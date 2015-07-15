package model;

import java.util.ArrayList;
import java.util.Random;

import org.apache.log4j.Logger;

/**
 * Generates AlphaPairs with random letters.
 * 
 * Classes Related To:
 *  -AlphaPair.java
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public class AlphaPairGenerator {
    
    private static final Logger logger = Logger.getLogger("Global");
    
    /**
     * Max number of times the same side may be the correct choice.
     */
    static final int MAX_TIMES_SAME_ANSWER = 3;
    
    /** Number of times the same relative size (bigger or smaller) can be correct */
    static final int MAX_TIMES_SAME_SIZE_CORRECT = 3;
    
    /** Number of characters to choose from. */
    static final int NUM_LETTERS = 26;
    
    /** Map from each difficulty mode to an integer representation. */
    static final int EASY_MODE = 0;
    static final int MEDIUM_MODE = 1;
    static final int HARD_MODE = 2;
    
    static final int SMALL_CHOICE_FONT_SIZE = 150;
    static final int MEDIUM_CHOICE_FONT_SIZE = 200;
    static final int BIG_CHOICE_FONT_SIZE = 300;
    static final double EASY_MODE_FONT_RATIO = .4;
    static final double MEDIUM_MODE_FONT_RATIO = .7;
    static final double HARD_MODE_FONT_RATIO = .85;
    
    /** Number of difficulty modes. */
    static final int NUM_MODES = 3;
    
    /** Define the lowest distance (in number of letters) each difficulty can have. */
    public static final int EASY_MODE_MIN = 14;
    public static final int MEDIUM_MODE_MIN = 8;
    public static final int HARD_MODE_MIN = 2;
    
    /** The highest distance each difficulty can have is their minimum plus NUM_CHOICES_IN_MODE. */
    public static final int NUM_CHOICES_IN_MODE = 4;
    
    /** Number of triplets of modes per set. See fillDifficultySet(). */
    static final int NUM_MODE_TRIPLETS = 2;
    
    /** Random number generator. */
    Random randomGenerator = new Random();

    /** The most recent AlphaPair produced by AlphaPairGenerator. */
    private AlphaPair alphaPair; 
    
    /** The difficulty setting: EASY, MEDIUM, HARD */
    private int difficultyMode;
    
    /** The list containing the difficulties. */
    private ArrayList<Integer> difficultySet;

    /** A measure of how many times the same side has been correct. */
    private int sameChoiceCorrect;
    
    /** A measure of how many times the same size has been correct. */
    private int sameSizeCorrect;
    
    /** True if the last correct choice was left. False otherwise. */
    private boolean lastWasLeft;
    
    /** True if the last correct choice was big. False otherwise. */
    private boolean lastWasBig;

    /**
     * Constructor. 
     */
    public AlphaPairGenerator() {
        this.setSameChoiceCorrect(0);
        this.setLastWasLeft(false);
        this.setLastWasBig(false);
        this.difficultySet = new ArrayList<Integer>();
        this.fillDifficultySet();
    }
    
    /**
     * This is how the difficulty is pseudo-randomly decided:
     * 
     * There will be a list (difficultySet) containing triplets of modes, 
     * where each triplet would contain one of each difficulty mode.
     * NUM_MODE_SETS is the number of triplets that the difficultySet contains.
     * 
     * When resetting the difficulty <setDifficulty()>, one mode will be randomly selected
     * from the difficultySet and removed. This repeats until difficultySet
     * is empty where it will then refill.
     * 
     */
    private void fillDifficultySet() {
        for (int i = 0; i < NUM_MODE_TRIPLETS; i++) {
            this.difficultySet.add(EASY_MODE);
            this.difficultySet.add(MEDIUM_MODE);
            this.difficultySet.add(HARD_MODE);
        }
    }
    
    /** 
     * Sets the difficulty by picking out a random difficulty from the difficultySet and removing it.
     */
    public void setDifficulty() {
        this.difficultyMode = 
                this.difficultySet.remove(
                        randomGenerator.nextInt(this.difficultySet.size()));
        if (this.difficultySet.isEmpty()) {
            this.fillDifficultySet();
        }   
    }
    
    /**
     * Get a new pair based on the current difficulty.
     */
    public void getNewDifficultyPair() {
        this.setDifficulty();
        int baseFontSize = this.chooseBaseFontSize();
        int otherFontSize = 0;
        int difference = 0;
        if (this.difficultyMode == EASY_MODE) {
            logger.info("Easy mode");
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + EASY_MODE_MIN;
            otherFontSize = (int) (EASY_MODE_FONT_RATIO * baseFontSize);
        } else if (this.difficultyMode == MEDIUM_MODE) {
            logger.info("Medium mode");
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + MEDIUM_MODE_MIN;
            otherFontSize = (int) (MEDIUM_MODE_FONT_RATIO * baseFontSize);
        } else if (this.difficultyMode == HARD_MODE) {
            logger.info("Hard mode");
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + HARD_MODE_MIN;
            otherFontSize = (int) (HARD_MODE_FONT_RATIO * baseFontSize);
        }
        
        if (randomGenerator.nextBoolean()) {
            int temp = baseFontSize;
            baseFontSize = otherFontSize;
            otherFontSize = temp;
        }
        
        this.getNewPair(difference, baseFontSize, otherFontSize);
    }
    
    /**
     * Determine the base font size to build the font ratio off of. The other
     * font size choice will be scaled down from this font size.
     * @return int The base font size.
     */
    public int chooseBaseFontSize() {
        int choiceOfSize = randomGenerator.nextInt(3);
        switch (choiceOfSize) {
        case 0:
            return SMALL_CHOICE_FONT_SIZE;
        case 1:
            return MEDIUM_CHOICE_FONT_SIZE;
        case 2:
            return BIG_CHOICE_FONT_SIZE;
        }
        return 0;
    }
    
    /**
     * Gets a new AlphaPair with letters a certain distance apart.
     * @param difference distance between the letters.
     */
    public void getNewPair(int difference, int fontSizeOne, int fontSizeTwo) {
        int letterOne, letterTwo;
        letterOne = this.randomGenerator.nextInt(NUM_LETTERS - difference);
        letterTwo = letterOne + difference;
        
        if (randomGenerator.nextBoolean()) {
            int temp = letterTwo;
            letterTwo = letterOne;
            letterOne = temp;
        }
        this.checkAndSet(letterOne, letterTwo, fontSizeOne, fontSizeTwo);
    }
    
    /**
     * Check if the same side is correct as last round and set the reverse if the same side has been
     * correct for MAX_TIMES_SAME_ANSWER times in a row.
     * @param letterOne
     * @param letterTwo
     */
    private void checkAndSet(int letterOne, int letterTwo, int fontSizeOne, int fontSizeTwo) {
        this.checkSameChoice(letterOne, letterTwo);

        this.checkSameSize(letterOne, letterTwo, fontSizeOne, fontSizeTwo);
        
        if (this.getSameSizeCorrect() >= MAX_TIMES_SAME_SIZE_CORRECT) {
            int temp = fontSizeOne;
            fontSizeOne = fontSizeTwo;
            fontSizeTwo = temp;
            
            this.setSameSizeCorrect(0);
            this.toggleLastWasBig();
        }
        
        if (this.getSameChoiceCorrect() >= MAX_TIMES_SAME_ANSWER) {
            this.setReversePair(letterOne, letterTwo, fontSizeOne, fontSizeTwo);
        } else {
            this.setAlphaPair(new AlphaPair(letterOne, letterTwo, fontSizeOne, fontSizeTwo));
        }
    }
    
    /**
     * Occurs under the condition that the same side has been correct
     * for MAX_TIMES_SAME_ANSWER times in a row.
     * 
     * Set the AlphaPair with the positions of the right and left letters
     * flipped as to what it would have otherwise been.
     * 
     * Toggles the lastWasLeft property because we are toggling the side
     * of which each component of the pair is being shown, so the opposite
     * side will be correct after setting the alpha pair in reverse order.
     * 
     * @param letterOne 
     * @param letterTwo
     */
    public void setReversePair(int letterOne, int letterTwo, int fontSizeOne, int fontSizeTwo) {
        this.setAlphaPair(new AlphaPair(letterTwo, letterOne, fontSizeTwo, fontSizeOne));
        this.toggleLastWasLeft();
        this.setSameChoiceCorrect(0);
    }

    /**
     * Check if the same side is correct as the last round.
     * @param letterOne Position of first letter of current round.
     * @param letterTwo Position of second letter of current round.
     */
    public void checkSameChoice(int letterOne, int letterTwo) {
        if (letterOne > letterTwo) {
            if (this.lastWasLeft) {
                this.incrementSameChoice();
            } else {
                this.setSameChoiceCorrect(0);
            }
            this.lastWasLeft = true;
        } else {
            if (!this.lastWasLeft) {
                this.incrementSameChoice();
            } else {
                this.setSameChoiceCorrect(0);
            }
            this.lastWasLeft = false;
        }   
    }
    
    /**
     * Check if the same relative size (bigger or smaller) is correct
     * as the last round.
     * @param fontSizeOne
     * @param fontSizeTwo
     */
    private void checkSameSize(int letterOne, int letterTwo, int fontSizeOne, int fontSizeTwo) {
        if (letterOne > letterTwo && fontSizeOne > fontSizeTwo ||
                letterTwo > letterOne && fontSizeTwo > fontSizeOne) {
            if (this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = true;
        } else {
            if (!this.lastWasBig) {
                this.incrementSameSizeCorrect();
            } else {
                this.setSameSizeCorrect(0);
            }
            this.lastWasBig = false;
        }
    }

    /**
     * Toggles which of the last choices was correct.
     */
    private void toggleLastWasLeft() {
        if (this.lastWasLeft) {
            this.lastWasLeft = false;
        } else {
            this.lastWasLeft = true;
        }
    }
    
    private void toggleLastWasBig() {
        if (this.lastWasBig) {
            this.lastWasBig = false;
        } else {
            this.lastWasBig = true;
        }
    }
    
    public void increaseDifficulty() {
        this.difficultyMode++;
    }

    public AlphaPair getAlphaPair() {
        return this.alphaPair;
    }

    public void setAlphaPair(AlphaPair alphaPair) {
        this.alphaPair = alphaPair;
    }

    public int getSameChoiceCorrect() {
        return this.sameChoiceCorrect;
    }

    public void setSameChoiceCorrect(int sameChoiceCorrect) {
        this.sameChoiceCorrect = sameChoiceCorrect;
    }

    public void incrementSameChoice() {
        this.sameChoiceCorrect++;
    }
    
    public boolean isLastWasLeft() {
        return this.lastWasLeft;
    }

    public void setLastWasLeft(boolean lastWasLeft) {
        this.lastWasLeft = lastWasLeft;
    }
    
    public int getDifficultyMode() {
        return this.difficultyMode;
    }

    public boolean isLastWasBig() {
        return lastWasBig;
    }

    public void setLastWasBig(boolean lastWasBig) {
        this.lastWasBig = lastWasBig;
    }

    public int getSameSizeCorrect() {
        return sameSizeCorrect;
    }

    public void setSameSizeCorrect(int sameSizeCorrect) {
        this.sameSizeCorrect = sameSizeCorrect;
    }
    
    public void incrementSameSizeCorrect() {
        this.sameSizeCorrect++;
    }
}
