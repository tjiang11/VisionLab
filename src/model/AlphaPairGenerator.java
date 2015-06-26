package model;

import java.util.Random;

/**
 * Generates AlphaPairs with random letters.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public class AlphaPairGenerator {
    
    /**
     * Max number of times the same side may be the correct choice.
     */
    static final int MAX_TIMES_SAME_ANSWER = 3;
    
    /**
     * Number of characters to choose from. 
     */
    static final int NUM_LETTERS = 26;
    
    /**
     * Random number generator.
     */
    Random randomGenerator = new Random();

    /** The most recent AlphaPair produced by AlphaPairGenerator. */
    private AlphaPair alphaPair; 
    
    /** A measure of how many times the same side has been correct. */
    private int sameChoice;
    
    /** True if the last correct choice was left. False otherwise. */
    private boolean lastWasLeft;
    
    /**
     * Constructor.
     */
    public AlphaPairGenerator() {
        this.getNewPair();
        this.setSameChoice(1);
        this.setLastWasLeft(false);
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
        
        if (this.getSameChoice() >= MAX_TIMES_SAME_ANSWER) {
            this.setAlphaPair(new AlphaPair(letterTwo, letterOne));
            this.toggleLastWasLeft();
            this.setSameChoice(0);
        } else {
            this.setAlphaPair(new AlphaPair(letterOne, letterTwo));
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
        switch (x) {
            case 0: 
                this.setAlphaPair(new AlphaPair(letterTwo, letterOne));
                break;
            case 1:
                this.setAlphaPair(new AlphaPair(letterOne, letterTwo));
                break;
            default:
                break;
        }
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
    
}
