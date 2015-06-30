package controller;



import java.net.URL;

import model.AlphaPair;
import model.AlphaPairGenerator;
import model.GameLogic;
import model.Player;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import view.GameGUI;

/**
 * The controller class for the game; interface between
 * models and the view.
 * 
 * @author Tony Jiang
 * 6-25-2015
 * 
 */
public class LetterGameController implements GameController {
    
    /** Time for the player to get ready after pressing start */
    final static int GET_READY_TIME = 2000;
    
    /** DataWriter to export data to CSV. */
    private DataWriter dw;
    
    /** AlphaPairGenerator to generate an AlphaPair */
    private AlphaPairGenerator apg;
    /** The graphical user interface. */
    private GameGUI theView;
    /** The current scene. */
    private Scene theScene;

    /** The subject. */
    private Player thePlayer;
    /** Used to measure response time. */
    private static long responseTimeMetric;
    /** Current state of the game. */
    public static CurrentState state;
    
    private LetterGameController gc;
    
    /** 
     * Constructor for the controller. There is only meant
     * to be one instance of the controller. Attaches listener
     * for when user provides response during trials. On a response,
     * prepare the next round and record the data.
     * @param view The graphical user interface.
     */
    public LetterGameController(GameGUI view) {
        
        this.gc = this;
        this.setApg(new AlphaPairGenerator());
        this.theView = view;
        this.theScene = view.getScene();
        this.thePlayer = view.getCurrentPlayer();
        this.dw = new DataWriter(theView);
    }
    
    /** 
     * Sets event listener for when subject presses 'F' or 'J' key
     * during a round. 
     */
    public void setGameHandlers() {
        theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if ((event.getCode() == KeyCode.F 
                        || event.getCode() == KeyCode.J) 
                        && state == CurrentState.WAITING_FOR_RESPONSE) {
                    
                    /** Set the state to prevent mass input from holding down
                     * 'F' or 'J' key. */
                    state = CurrentState.WAITING_BETWEEN_ROUNDS;
                    
                    /** Update models and view appropriately according to correctness
                     * of subject's response.
                     */
                    gc.responseAndUpdate(event, theView);
                    
                    /** Prepare the next round */
                    gc.prepareNextRound(); 
                    
                    /** Export data */
                    dw.writeToCSV();
                }
                /**
                 * If subject has completed the total number of rounds specified,
                 * then change the scene to the finish screen.
                 */
                if (thePlayer.getNumRounds() >= NUM_ROUNDS) {
                    state = CurrentState.FINISHED;
                    System.out.println("Done");
                    theView.setFinishScreen(theView.getPrimaryStage());
                }
            }
        });
    }  
    
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
        URL feedbackSoundFileUrl = null;
        
        correct = GameLogic.checkAnswerCorrect(e, ap);
        
        if (correct) { this.updateProgressBar(view); }
        
        this.updatePlayer(currentPlayer, correct);   
        
        this.feedbackSound(feedbackSoundFileUrl, correct); 
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
            
            if (view.getCurrentPlayer().getNumStars() > 2) {
                
                view.changeBackground(1);
            }
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
    
    /**
     * Prepare the first round by making a load bar to 
     * let the subject prepare for the first question.
     */
    public void prepareFirstRound() {
        
        Task<Void> sleeper = new Task<Void>() {   
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < GET_READY_TIME; i++) {
                    this.updateProgress(i, GET_READY_TIME); 
                    Thread.sleep(1);
                }
                return null;
            }
        };
        theView.getGetReadyBar().progressProperty().bind(sleeper.progressProperty());
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent e) {
                setOptions();
                state = CurrentState.WAITING_FOR_RESPONSE;
                responseTimeMetric = System.nanoTime();
                theView.getGetReady().setText("");
                theView.getGetReadyBar().setOpacity(0.0);
            }
        });
        new Thread(sleeper).start();
    }
    
    /**
     * Prepares the next round be recording reponse time,
     * clearing the previous round, waiting, and creating the next round.
     */
    public void prepareNextRound() {
        recordResponseTime();
        clearRound();
        waitBeforeNextRoundAndUpdate(TIME_BETWEEN_ROUNDS);
    }
    
    /**
     * Clears the options.
     */
    public void clearRound() {
        getTheView().getLeftOption().setText("");
        getTheView().getRightOption().setText("");
    }

    /**
     * Wait for a certain time and then set the next round.
     */
    public void waitBeforeNextRoundAndUpdate(int waitTime) {
        
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (int i = 0; i < waitTime; i++) {
                    this.updateProgress(i, waitTime); 
                    Thread.sleep(1);
                }
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent e) {
                setOptions();
                state = CurrentState.WAITING_FOR_RESPONSE;
                responseTimeMetric = System.nanoTime();
                getTheView().getGetReady().setText("");
            }
        });
        new Thread(sleeper).start();
    }

    /**
     * Set the next round's choices.
     */
    public void setOptions() {
        char letterOne, letterTwo;
        
        apg.getNewPair();
        theView.setCurrentAlphaPair(getApg().getAlphaPair());
        
        letterOne = theView.getCurrentAlphaPair().getLetterOne();
        letterTwo = theView.getCurrentAlphaPair().getLetterTwo();
        
        theView.getLeftOption().setText(String.valueOf(letterOne));
        theView.getRightOption().setText(String.valueOf(letterTwo));

    }
    
    /** 
     * Record the response time of the subject. 
     */
    public void recordResponseTime() {
        long responseTime = System.nanoTime() - responseTimeMetric;
        thePlayer.setResponseTime(responseTime);
        
        //Convert from nanoseconds to seconds.
        double responseTimeSec = responseTime / 1000000000.0;
        System.out.println("Your response time was: " 
                + responseTimeSec + " seconds");
    }
    
    /**
     * Reorients the Controller to the current scene and player.
     * @param theView The current view.
     */
    public void grabSetting(GameGUI theView) {
        this.theView = theView;
        this.theScene = theView.getScene();
        this.thePlayer = theView.getCurrentPlayer();
        this.dw = new DataWriter(theView);
    }

    public AlphaPairGenerator getApg() {
        return apg;
    }

    public void setApg(AlphaPairGenerator apg) {
        this.apg = apg;
    }
    
    public GameGUI getTheView() {
        return theView;
    }
    
    public void setTheScene(Scene scene) {
        this.theScene = scene;
    }
}