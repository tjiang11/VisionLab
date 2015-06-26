package controller;

import java.net.URL;

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
public class GameController {
    
    /** Number of rounds. One round is one pair of options. */
    public static final int NUM_ROUNDS = 40;
    /** Time between rounds in milliseconds. */
    public static final int TIME_BETWEEN_ROUNDS = 1250;
    
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
    
    /** 
     * Constructor for the controller. There is only meant
     * to be one instance of the controller. Attaches listener
     * for when user provides response during trials. On a response,
     * prepare the next round and record the data.
     * @param view The graphical user interface.
     */
    public GameController(GameGUI view) {
        
        GameController gc = this;
        setApg(new AlphaPairGenerator());
        theView = view;
        theScene = view.getScene();
        thePlayer = view.getCurrentPlayer();
        responseTimeMetric = System.nanoTime();
        state = CurrentState.WAITING_FOR_RESPONSE;
        this.dw = new DataWriter(theView);
        
        /** Event listener for when subject presses 'F' or 'J' key
         * during a round. 
         */
        theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if ((event.getCode() == KeyCode.F 
                        || event.getCode() == KeyCode.J) 
                        && state == CurrentState.WAITING_FOR_RESPONSE) {
                    
                    /** Set the state to prevent mass input from holding down
                     * 'F' or 'J' key. */
                    state = CurrentState.WAITING_BETWEEN_ROUNDS;
                    
                    /** If user inputs correct answer play positive feedback sound,
                     * if not then play negative feedback sound.*/
                    URL feedbackSoundFileUrl;
                    AudioClip feedbackSound;
                    
                    if (GameLogic.checkValidity(event, 
                            view.getCurrentAlphaPair(), 
                            view.getCurrentPlayer())) {
                        feedbackSoundFileUrl = 
                                getClass().getResource("/UI/sounds/Ping.aiff");
                    } else {
                        feedbackSoundFileUrl = 
                                getClass().getResource("/UI/sounds/Basso.aiff");
                    }
                    feedbackSound = new AudioClip(
                            feedbackSoundFileUrl.toString());
                    feedbackSound.play();
                    
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
     * Prepares the next round be recording reponse time,
     * clearing the previous round, waiting, and creating the next round.
     */
    private void prepareNextRound() {
        recordResponseTime();
        clearRound();
        waitBeforeNextRoundAndUpdate();
    }
    
    /**
     * Clears the options.
     */
    private void clearRound() {
        getTheView().getLeftOption().setText("");
        getTheView().getRightOption().setText("");
    }

    /**
     * Wait for a certain time and then set the next round.
     */
    private void waitBeforeNextRoundAndUpdate() {
        Task<Void> sleeper = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(TIME_BETWEEN_ROUNDS);
                return null;
            }
        };
        sleeper.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent e) {
                setOptions();
                state = CurrentState.WAITING_FOR_RESPONSE;
                responseTimeMetric = System.nanoTime();
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
    private void recordResponseTime() {
        long responseTime = System.nanoTime() - responseTimeMetric;
        thePlayer.setResponseTime(responseTime);
        
        //Convert from nanoseconds to seconds.
        double responseTimeSec = responseTime / 1000000000.0;
        System.out.println("Your response time was: " 
                + responseTimeSec + " seconds");
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

    public void setTheView(GameGUI theView) {
        this.theView = theView;
    }
}