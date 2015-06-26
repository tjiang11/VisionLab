package model;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Utility class for the game logic.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public final class GameLogic {
    
    /**
     * Disable the constructing of a GameLogic object.
     */
    private GameLogic() {
    }
    
    /**
     * Checks whether the subject input the correct choice.
     * @param e The key event to check which key the user pressed.
     * @param ap The current AlphaPair being evaluated.
     * @param currentPlayer The subject.
     * @return True if the player is correct. False otherwise.
     */
    public static boolean checkValidity(
            KeyEvent e, AlphaPair ap, Player currentPlayer) {
        boolean correct;
        if ((ap.isLeftCorrect() && e.getCode() == KeyCode.F)
                || !ap.isLeftCorrect() && e.getCode() == KeyCode.J) {
            currentPlayer.addPoint();
            currentPlayer.setRight(true);
            System.out.println("Right!");
            correct = true;
        } else {
            currentPlayer.setRight(false);
            System.out.println("Wrong :(");
            correct = false;
        } 
        currentPlayer.incrementNumRounds();    
        return correct;
    }
}
