package controller;

import java.net.URL;

import org.apache.log4j.Logger;

import config.Config;
import model.AlphaPair;
import model.AlphaPairGenerator;
import model.GameLogic;
import model.Player;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.text.Font;
import view.GameGUI;

/**
 * 
 * The center of the program; interface between the
 * models and the view. 
 * 
 * Classes Related to:
 *  -GameGUI.java (view)
 *      -Updates elements of the GUI as the game progresses and responds.
 *  -AlphaPairGenerator.java (model)
 *      -Calls on AlphaPairGenerator to generate new AlphaPairs.
 *  -AlphaPair.java (model)
 *      -LetterGameController keeps track of the most recent AlphaPair created in variable currentAlphaPair.
 *  -Player.java (model)
 *      -Updates Player information as the game progresses and responds.
 *  -GameLogic.java (model)
 *      -Calls on GameLogic to evaluate the correctness of a response from the subject.
 *  -DataWriter.java
 *      -Passes information (Player and AlphaPair) to DataWriter to be exported.
 *      
 * @author Tony Jiang
 * 6-25-2015
 * 
 */
public class LetterGameController implements GameController {
    private static Logger logger = Logger.getLogger(LetterGameController.class);
    
    /** Punish for wrong answers */
    static final boolean PUNISH = true;
    
    /** Time in milliseconds for the player to get ready after pressing start */
    final static int GET_READY_TIME = 2000;
    
    /** Integer representing each each background. */
    private static int backgroundNumber = 0;
    
    /** True if choices should vary in physical size */
    public static boolean SIZE_VARIATION;
    
    /** Time between rounds in milliseconds. */
    public static int TIME_BETWEEN_ROUNDS;
    
    /** DataWriter to export data to CSV. */
    private DataWriter dataWriter;
    
    /** AlphaPairGenerator to generate an AlphaPair */
    private AlphaPairGenerator apg;
    /** The graphical user interface. */
    private GameGUI theView;
    /** The current scene. */
    private Scene theScene;

    /** The subject. */
    private Player thePlayer;
    /** The current AlphaPair being evaluated by the subject. */
    private AlphaPair currentAlphaPair;
    
    /** Used to measure response time. */
    private static long responseTimeMetric;
    
    /** Current state of the overall game. */
    public static CurrentState state;
    
    /** Describes the current state of gameplay */
    private static GameState gameState;
    
    /** How many stars the player has earned. 
     * The player earns a star for every time the
     * progress bar is filled. */
    private static int numStars = 0;
    
    /** Number of stars earned before changing to next background. */
    private static final int STARS_PER_BACKGROUND = 3;
    
    private enum GameState {
        /** Player has responded and next round is loading. */
        WAITING_BETWEEN_ROUNDS,
        
        /** Player has not responded and question is being displayed. */
        WAITING_FOR_RESPONSE,
    }
    
    /** Alternate reference to "this" to be used in inner methods */
    private LetterGameController gameController;
    
    /** 
     * Constructor for the controller. There is only meant
     * to be one instance of the controller. Attaches listener
     * for when user provides response during trials. On a response,
     * prepare the next round and record the data.
     * @param view The graphical user interface.
     */
    public LetterGameController(GameGUI view) {
        
        loadConfig();
        
        this.gameController = this;
        this.apg = new AlphaPairGenerator();
        this.currentAlphaPair = null;
        this.theView = view;
        this.theScene = view.getScene();
        this.thePlayer = new Player();
        this.dataWriter = new DataWriter(this);
    }
    
    /** 
     * Load configuration settings. 
     */
    private void loadConfig() {
        new Config();
        TIME_BETWEEN_ROUNDS = Config.getPropertyInt("time.between.rounds");
        SIZE_VARIATION = Config.getPropertyBoolean("size.variation");
        logger.info("TIME_BETWEEN_ROUNDS: " + TIME_BETWEEN_ROUNDS);
        logger.info("SIZE_VARIATION: " + SIZE_VARIATION);
    }

    /**
     * Sets event listener for when subject clicks the start button OR presses Enter.
     * Pass in the subject's ID number entered.
     */
    public void setLoginHandlers() {
        
        this.theScene = theView.getScene();
        
        this.theView.getStart().setOnAction(e -> 
            {
                onClickStartButton();
            });
        this.theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {                
                    onClickStartButton();
                }
            }
        });
    }
    
    /**
     * Action to be executed upon clicking of Start on Login screen.
     * 
     * Records user inputted data and sets instructions screen.
     */
    private void onClickStartButton() {
        theView.getFeedback().setVisible(false);
        theView.getFeedbackAge().setVisible(false);
        theView.getFeedbackGender().setVisible(false);
        try {
            gameController.thePlayer.setSubjectID(Integer.parseInt(theView.getEnterId().getText()));
        } catch (NumberFormatException ex) {
            theView.getEnterId().requestFocus();
            theView.getEnterId().setText("");
            theView.getFeedback().setVisible(true);
            return;
        }    
        if (theView.getPickMale().isSelected()) {
            gameController.thePlayer.setSubjectGender(Player.Gender.MALE);
        } else if (theView.getPickFemale().isSelected()) {
            gameController.thePlayer.setSubjectGender(Player.Gender.FEMALE);
        } else {
            theView.getFeedbackGender().setVisible(true);
            return;
        }
        try {
            gameController.thePlayer.setSubjectAge(Integer.parseInt(theView.getEnterAge().getText()));
        } catch (NumberFormatException ex) {
            theView.getEnterAge().requestFocus();
            theView.getEnterAge().setText("");
            theView.getFeedbackAge().setVisible(true);
            return;
        }
        theView.setInstructionsScreen(); 
    }

    /** 
     * Set event listener on the Next button. 
     */
    public void setInstructionsHandlers() {
        this.theScene = theView.getScene();
        this.theView.getNext().setOnAction(e -> {
            onClickNextInstructions();
        });
        this.theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    onClickNextInstructions();
                }
            }
        });
    }
    
    /**
     *  Actions to be executed on clicking the Next button 
     */
    private void onClickNextInstructions() {
        theView.setGameScreen(); 
        state = CurrentState.PRACTICE;
    }

    /**
     * Set handler upon clicking the "Start Assessment" button, preparing for actual assessment.
     * Sets the game screen and the state to GAMEPLAY from PRACTICE. Removes the "Practice" Label.
     * Resets the player's data.
     */
    public void setPracticeCompleteHandlers() {
        this.theView.getStartAssessment().setOnAction( e-> {
            theView.setGameScreen();
            theView.getPractice().setVisible(false);
            state = CurrentState.GAMEPLAY;
            gameState = null;
            this.resetPlayer();
        });
    }

    /** 
     * Reset the player data, but retain intrinsic subject data 
     */
    private void resetPlayer() {
        SimpleIntegerProperty subjectID = new SimpleIntegerProperty(thePlayer.getSubjectID());
        Player.Gender subjectGender = thePlayer.getSubjectGender();
        SimpleIntegerProperty subjectAge = new SimpleIntegerProperty(thePlayer.getSubjectAge());
        thePlayer = new Player(subjectID, subjectGender, subjectAge);
    }

    /** 
     * Sets event listener for when subject presses 'F' or 'J' key
     * during a round. 
     */
    public void setGameHandlers() {
        this.theScene = theView.getScene();
        this.theScene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if ((event.getCode() == KeyCode.F 
                        || event.getCode() == KeyCode.J) 
                        && gameState == GameState.WAITING_FOR_RESPONSE) {
                    gameController.handlePressForJ(event);
                }
            }
        });
    }  
    
    /**
     * Actions to be executed on the pressing of the F or J key.
     * Update the models/data, prepare the next round, and export data to CSV.
     * @param event
     */
    private void handlePressForJ(KeyEvent event) {
        this.responseAndUpdate(event);
        this.prepareNextRound(); 
        this.exportDataToCSV();
    }
    
    /** 
     * Export data to CSV file.
     */
    private void exportDataToCSV() {
        if (state == CurrentState.GAMEPLAY) {
            dataWriter.writeToCSV();    
        }
    }
    
    /**
     * Update models and view appropriately according to correctness
     * of subject's response.  
     * @param e The key event to check which key the user pressed.
     * @param view the graphical user interface
     * @return True if the player is correct. False otherwise.
     */
    public void responseAndUpdate (
            KeyEvent e) {
        gameState = GameState.WAITING_BETWEEN_ROUNDS;
        AlphaPair ap = this.currentAlphaPair;
        boolean correct = GameLogic.checkAnswerCorrect(e, ap);
        this.updatePlayer(correct);   
        this.updateGUI(correct);
        this.dataWriter.grabData(this);
    }
    
    /** Update the player appropriately.
     * 
     * @param currentPlayer The current player.
     * @param correct True if subject's reponse is correct. False otherwise.
     */
    private void updatePlayer(boolean correct) {
        Player currentPlayer = this.thePlayer;
        this.recordResponseTime();
        if (correct) {
            currentPlayer.addPoint();
            currentPlayer.setRight(true);
        } else {
            currentPlayer.setRight(false);
        }
        currentPlayer.incrementNumRounds();
    }
    
    /** 
     * Update the progressbar, audio, stars, and background.
     * @param correct Whether the subject answered correctly.
     */
    private void updateGUI(boolean correct) {
        if (correct) {
            if (theView.getProgressBar().isIndeterminate()) {
                theView.getProgressBar().setProgress(0.0);
                theView.getProgressBar().setStyle("-fx-accent: #0094C5;");
            }
            theView.getProgressBar().setProgress(theView.getProgressBar().getProgress() + .1666667);
            if (theView.getProgressBar().getProgress() >= 1.00) {
                theView.getProgressBar().setProgress(0.25);
                
                URL powerUpSound = getClass().getResource("/res/sounds/Powerup.wav");
                new AudioClip(powerUpSound.toString()).play();
                
                int starToReveal = numStars;
                theView.getStarNodes()[starToReveal].setVisible(true);
                numStars++;
                
                this.checkBackground();
            }
        } else {
            theView.getProgressBar().setStyle("-fx-accent: #0094C5;");
            if (PUNISH) {
                theView.getProgressBar().setProgress(theView.getProgressBar().getProgress() - .125);
                if (theView.getProgressBar().isIndeterminate()) {
                    theView.getProgressBar().setStyle("-fx-accent: red;");                    
                }
            }
        }
        this.feedbackSound(correct); 
    }
    
    /**
     * Check to see if background needs to be switched and if so change the background.
     */
    private void checkBackground() {
        if (numStars % STARS_PER_BACKGROUND == 0) {
            theView.changeBackground(++backgroundNumber);
            
            URL applauseSound = getClass().getResource("/res/sounds/Applause.wav");
            AudioClip applause = new AudioClip(applauseSound.toString());
            applause.setRate(1.4);
            applause.play();
        }    
    }

    /** If user inputs correct answer play positive feedback sound,
     * if not then play negative feedback sound.
     * @param feedbackSoundFileUrl the File Url of the Sound to be played.
     * @param correct whether the subject answered correctly or not.
     */
    private void feedbackSound(boolean correct) {
        URL feedbackSoundFileUrl;
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
                gameState = GameState.WAITING_FOR_RESPONSE;
                responseTimeMetric = System.nanoTime();
                theView.getGetReadyBox().setVisible(false);
            }
        });
        new Thread(sleeper).start();
    }
    
    /**
     * Prepares the next round by clearing the previous round, 
     * waiting, and creating the next round.
     */
    public void prepareNextRound() {
        this.clearRound();
        this.waitBeforeNextRoundAndUpdate(TIME_BETWEEN_ROUNDS); 
        this.checkIfDone();
    }
    
    /** 
     * Check if subject has completed practice or assessment.
     */
    private void checkIfDone() {
        if (thePlayer.getNumRounds() >= NUM_ROUNDS) {
            this.finishGame();
        }
        if (state == CurrentState.PRACTICE && thePlayer.getNumRounds() >= NUM_PRACTICE_ROUNDS) {
            this.finishPractice();
        }
    }
    
    /**
     * If subject has completed the total number of rounds specified,
     * then change the scene to the finish screen.
     */
    private void finishGame() {
        theView.setFinishScreen(thePlayer.getNumCorrect(), backgroundNumber);
        this.theScene.setOnKeyPressed(null);
    }
    
    /**
     * If subject has completed the total number of rounds specified,
     * then change the scene to the practice complete screen.
     */
    private void finishPractice() {
        theView.setPracticeCompleteScreen();
        numStars = 0;
        backgroundNumber = 0;
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
                gameState = GameState.WAITING_FOR_RESPONSE;
                responseTimeMetric = System.nanoTime();
            }
        });
        new Thread(sleeper).start();
    }

    /**
     * Set the next round's choices.
     */
    public void setOptions() {
        this.prepareNextPair();
        this.showPair();
    }
    
    /**
     * Prepare the next pair.
     */
    private void prepareNextPair() {
        apg.getNewDifficultyPair();
        this.currentAlphaPair = apg.getAlphaPair();
    }
    
    /**
     * Show the pair.
     */
    private void showPair() {
        char letterOne = this.currentAlphaPair.getLetterOne();
        char letterTwo = this.currentAlphaPair.getLetterTwo();
        theView.getLeftOption().setText(String.valueOf(letterOne));
        theView.getRightOption().setText(String.valueOf(letterTwo));
        if (SIZE_VARIATION) {
            this.setFontSizes();
        };
    }
    
    /**
     * Set the font sizes of the pair.
     */
    private void setFontSizes() {
        int letterSizeOne = this.currentAlphaPair.getFontSizeOne();
        int letterSizeTwo = this.currentAlphaPair.getFontSizeTwo();
        theView.getLeftOption().setFont(new Font("Tahoma", letterSizeOne));
        theView.getRightOption().setFont(new Font("Tahoma", letterSizeTwo));
    }
    
    /** 
     * Record the response time of the subject. 
     */
    public void recordResponseTime() {
        long responseTime = System.nanoTime() - responseTimeMetric;
        thePlayer.setResponseTime(responseTime);
        double responseTimeSec = responseTime / 1000000000.0;        
        logger.info("Response time: " 
                + responseTimeSec + " seconds");
    }
    
//    /** (This method is not in use)
//     * 
//     * Slowly drains the progress bar to encourage the user not to spend too much time thinking.
//     */
//    public void beginProgressBarDrainage() {
//        theView.getProgressBar().setProgress(0.6);
//        
//        Timeline drainer = new Timeline(
//                new KeyFrame(Duration.seconds(0), evt -> {
//                    if (gameController.theView.getProgressBar().getProgress() > 1.0) {
//                        gameController.theView.getProgressBar().setStyle("-fx-accent: #00CC00;");
//                    } else {
//                        gameController.theView.getProgressBar().setStyle("-fx-accent: #0094C5;");
//                    }
//                    if (gameController.theView.getProgressBar().getProgress() > .005) {
//                        gameController.theView.getProgressBar().setProgress(theView.getProgressBar().getProgress() - .0035);
//                    }
//                }), new KeyFrame(Duration.seconds(0.065)));
//        drainer.setCycleCount(Animation.INDEFINITE);
//        drainer.play();
//    }

    public Player getThePlayer() {
        return thePlayer;
    }

    public void setThePlayer(Player thePlayer) {
        this.thePlayer = thePlayer;
    }

    public AlphaPair getCurrentAlphaPair() {
        return currentAlphaPair;
    }

    public void setCurrentAlphaPair(AlphaPair currentAlphaPair) {
        this.currentAlphaPair = currentAlphaPair;
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