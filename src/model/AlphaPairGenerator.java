package model;

import java.util.Random;

/**
 * Generates AlphaPairs with random letters.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public class AlphaPairGenerator implements PairGenerator{
    
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
//   (6-25-2015) sameChoice is set to 1 because the very first round
//   is not handled by the same instance of AlphaPairGenerator as the
//   following rounds. (This should be changed, though)
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
           
        this.checkSameChoice(letterOne, letterTwo);

        if (this.getSameChoice() >= MAX_TIMES_SAME_ANSWER) {
            this.setReverseAlphaPair(letterOne, letterTwo);
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
    public void setReverseAlphaPair(int letterOne, int letterTwo) {
        this.setAlphaPair(new AlphaPair(letterTwo, letterOne));
        this.toggleLastWasLeft();
        this.setSameChoice(0);
    }

    /**
     * Check if the same side is correct as the last round.
     * @param letterOne Position of first letter of current round.
     * @param letterTwo Position of right letter of current round.
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
