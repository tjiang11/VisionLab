package model;

import java.util.ArrayList;
import java.util.Random;

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
public class AlphaPairGenerator implements PairGenerator {
    
    /** Number of characters to choose from. */
    static final int NUM_LETTERS = 26;
    
    /** Map from each difficulty mode to an integer representation. */
    static final int EASY_MODE = 0;
    static final int MEDIUM_MODE = 1;
    static final int HARD_MODE = 2;
    
    /** Number of difficulty modes. */
    static final int NUM_MODES = 3;
    
    /** Define the lowest distance (in number of letters) each difficulty can have. */
    static final int EASY_MODE_MIN = 14;
    static final int MEDIUM_MODE_MIN = 8;
    static final int HARD_MODE_MIN = 2;
    
    /** The highest distance each difficulty can have is their minimum plus NUM_CHOICES_IN_MODE. */
    static final int NUM_CHOICES_IN_MODE = 4;
    
    /**
     * Number of triplets of modes per set. See fillDifficultySet().
     */
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
    private int sameChoice;
    
    /** True if the last correct choice was left. False otherwise. */
    private boolean lastWasLeft;
    
    /**
     * Constructor. 
     */
    public AlphaPairGenerator() {
        this.getNewPair();
        this.setSameChoice(0);
        this.setLastWasLeft(false);
        this.difficultyMode = EASY_MODE;
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
     * Gets a new AlphaPair with random letters while
     * checking to make sure that the same choice will
     * not be picked more than three times in a row
     * as being correct.
     */
    public void getNewPair() {
        System.out.println(this.getSameChoice());
        int letterOne, letterTwo;
        letterOne = this.randomGenerator.nextInt(NUM_LETTERS);
        do {
            letterTwo = this.randomGenerator.nextInt(NUM_LETTERS); 
        } while (letterOne == letterTwo);        
           
        this.checkAndSet(letterOne, letterTwo);
    }
    
    /**
     * Get a new pair based on the current difficulty.
     */
    public void getNewDifficultyPair() {
        int difference = 0;
        if (this.difficultyMode == EASY_MODE) {
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + EASY_MODE_MIN;
        } else if (this.difficultyMode == MEDIUM_MODE) {
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + MEDIUM_MODE_MIN;
        } else if (this.difficultyMode == HARD_MODE) {
            difference = this.randomGenerator.nextInt(NUM_CHOICES_IN_MODE) + HARD_MODE_MIN;
        }
        this.getNewPair(difference);
    }
    
    /**
     * Gets a new AlphaPair with letters a certain distance apart.
     * @param difference distance between the letters.
     */
    public void getNewPair(int difference) {
        int letterOne, letterTwo;
        letterOne = this.randomGenerator.nextInt(NUM_LETTERS - difference);
        letterTwo = letterOne + difference;
        
        //Swap the order
        int x = this.randomGenerator.nextInt(2);
        if (x == 1) {
            int temp = letterTwo;
            letterTwo = letterOne;
            letterOne = temp;
        }
        this.checkAndSet(letterOne, letterTwo);
    }
    
    /**
     * Check if choices are the same and set the reverse if the same side has been
     * correct for MAX_TIMES_SAME_ANSWER times in a row.
     * @param letterOne
     * @param letterTwo
     */
    private void checkAndSet(int letterOne, int letterTwo) {
        this.checkSameChoice(letterOne, letterTwo);
        
        if (this.getSameChoice() >= MAX_TIMES_SAME_ANSWER) {
            this.setReversePair(letterOne, letterTwo);
        } else {
            this.setAlphaPair(new AlphaPair(letterOne, letterTwo));
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
    public void setReversePair(int letterOne, int letterTwo) {
        this.setAlphaPair(new AlphaPair(letterTwo, letterOne));
        this.toggleLastWasLeft();
        this.setSameChoice(0);
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
                this.setSameChoice(0);
            }
            this.lastWasLeft = true;
        } else {
            if (!this.lastWasLeft) {
                this.incrementSameChoice();
            } else {
                this.setSameChoice(0);
            }
            this.lastWasLeft = false;
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
    
    public void increaseDifficulty() {
        this.difficultyMode++;
    }

    public AlphaPair getAlphaPair() {
        return this.alphaPair;
    }

    public void setAlphaPair(AlphaPair alphaPair) {
        this.alphaPair = alphaPair;
    }

    public int getSameChoice() {
        return this.sameChoice;
    }

    public void setSameChoice(int sameChoice) {
        this.sameChoice = sameChoice;
    }

    public void incrementSameChoice() {
        this.sameChoice++;
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
}
