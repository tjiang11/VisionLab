package model;

import java.net.URL;

import view.GameGUI;
import view.SetUp;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;

/**
 * Utility class for the game logic.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public final class GameLogic {
    
    /**
     * Update models and view appropriately according to correctness
     * of subject's response.  
     * @param e The key event to check which key the user pressed.
     * @param ap The current AlphaPair being evaluated.
     * @param currentPlayer The subject.
     * @param pb the ProgressBar to update.
     * @return True if the player is correct. False otherwise.
     */
    public void responseAndUpdate (
            KeyEvent e, GameGUI view) {
        boolean correct;
        AlphaPair ap = view.getCurrentAlphaPair();
        Player currentPlayer = view.getCurrentPlayer();
        ProgressBar pb = view.getProgressBar();
        URL feedbackSoundFileUrl = null;
        
        correct = this.checkAnswerCorrect(e, ap);
        
        if (correct) { this.updateProgressBar(view); }
        
        this.updatePlayer(currentPlayer, correct);   
        
        this.feedbackSound(feedbackSoundFileUrl, correct); 
    }
    
    /**
     * Checks whether subject's answer is correct or incorrect.
     * @param e The key event to check which key the user pressed.
     * @param ap The current AlphaPair being evaluated.
     * @return correct True if correct, false otherwise.
     */
    private boolean checkAnswerCorrect(KeyEvent e, AlphaPair ap) {
        boolean correct;
        if ((ap.isLeftCorrect() && e.getCode() == KeyCode.F)
                || !ap.isLeftCorrect() && e.getCode() == KeyCode.J) {
            correct = true;
        } else {  
            correct = false;     
        } 
        return correct;
    }
    
    /** Update the player appropriately.
     * 
     * @param currentPlayer The current player.
     * @param correct True if subject's reponse is correct. False otherwise.
     */
    private void updatePlayer(Player currentPlayer, boolean correct) {
        if (correct) {
            currentPlayer.addPoint();
            currentPlayer.setRight(true);
        } else {
            currentPlayer.setRight(false);
        }
        currentPlayer.incrementNumRounds();
    }
    
    /**
     * Update the progressbar. Resets to zero if progress bar is full.
     * @param pb The view's progress bar.
     */
    private void updateProgressBar(GameGUI view) {
        if (view.getProgressBar().getProgress() >= .99) {
            view.getProgressBar().setProgress(0.0);
            
            URL powerUpSound = getClass().getResource("/res/sounds/Powerup.wav");
            new AudioClip(powerUpSound.toString()).play();
            
            
            int starToReveal = view.getCurrentPlayer().getNumStars();
            view.getStarNodes()[starToReveal].setVisible(true);
            view.getCurrentPlayer().incrementNumStars();
        }
        view.getProgressBar().setProgress(view.getProgressBar().getProgress() + .2);
    }
    
    
    /** If user inputs correct answer play positive feedback sound,
     * if not then play negative feedback sound.*/
    private void feedbackSound(URL feedbackSoundFileUrl, boolean correct) {
        if (correct) {
            feedbackSoundFileUrl = 
                    getClass().getResource("/res/sounds/Ping.aiff");
        } else {
            feedbackSoundFileUrl = 
                    getClass().getResource("/res/sounds/Basso.aiff");
        }
        new AudioClip(feedbackSoundFileUrl.toString()).play();
    }
}
