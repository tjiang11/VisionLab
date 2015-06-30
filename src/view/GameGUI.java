package view;

import controller.LetterGameController;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The graphical user interface of the game.
 * 
 * @author Tony Jiang
 * 6-25-2015
 * 
 */
public class GameGUI {
    
    /** Width of the game window. */
    static final int SCREEN_WIDTH = 500;
    /** Height of the game window. */
    static final int SCREEN_HEIGHT = 400;
    
    /** Controller for setting event handlers */
    private LetterGameController LGC;
    
//    /** The subject. */
//    private Player currentPlayer;
//    /** The current AlphaPair being evaluated by the subject. */
//    private AlphaPair currentAlphaPair;

    /** The JavaFX stage for the game. */
    private Stage primaryStage;
    /** The current scene of the game. */
    private Scene scene;
    /** The pane of the game */
    private AnchorPane layout;
    
    /** Login Screen - start button. */
    private Button start;
    /** Login Screen - feedback to tell if user needs to
     * reinput their ID. */
    private Label feedback;
    /** Login Screen - Text field for user to enter their 
     * Subject ID. */
    private TextField enterId;
    
    /** Game Screen - The left choice. */
    private Button leftOption;
    /** Game Screen - The right choice. */
    private Button rightOption;
    /** Game Screen - Progress Bar. */
    private ProgressBar progressBar;
    /** Game Screen - Get Ready */
    private Label getReady;
    /** Game Screen - Get Ready Bar */
    private ProgressBar getReadyBar;
    /** Game Screen - Stars */
    private ImageView starNodes[];
    
    /** End Screen - message informing the user has finished. */
    private Label congratulations;
    
    /** 
     * Constructor for the user interface. Sets the stage
     * and login screen.
     * @param stage The user interface stage.
     * @throws IOException 
     */
    public GameGUI(Stage stage) {
        
        this.setPrimaryStage(stage);
        
        this.setLoginScreen(stage);
    }
    
    /**
     * Sets the login screen where user will input their informmation.
     * @param stage The user interface stage.
     * @throws IOException
     */
    private void setLoginScreen(Stage stage) {
        this.primaryStage.setTitle("Game");
        this.enterId = new TextField();
        
        Scene loginScene = SetUp.setUpLoginScreen(this, this.primaryStage);
        this.scene = loginScene;
        
        LGC = new LetterGameController(this);
        LGC.setLoginHandlers();

        this.enterId.requestFocus();
        
        this.primaryStage.setResizable(false);
        this.primaryStage.setFullScreen(false);
        this.primaryStage.sizeToScene();
        this.primaryStage.setScene(this.scene);
        this.primaryStage.show();  
    }
    
    /**
     * Sets the game screen where subject will be presented with two letters.
     * @param stage The user interface stage.
     * @param subjectID The subject's ID number.
     */
    public void setGameScreen(Stage stage, String subjectID, LetterGameController lgc) {
        try {
            Scene gameScene = SetUp.setUpGameScreen(
                    this, stage, subjectID, lgc);  
            
            this.scene = gameScene;
            this.primaryStage.setScene(this.scene);
           
            
            this.LGC.prepareFirstRound();
            
            /** Set event handlers for gameplay */
            
            this.LGC.setGameHandlers();
            
        } catch (NumberFormatException e) {
            System.out.println("Oops!");
            this.enterId.setText("");
            this.enterId.requestFocus();
            this.feedback.setText("That's not your ID, silly!");
        }
    }
    
    /** 
     * Sets the ending screen informing the subject of their completion.
     * @param stage The user interface stage.
     */
    public void setFinishScreen(Stage stage, LetterGameController lgc) {
        Scene finishScene = SetUp.setUpFinishScreen(this, stage, lgc);
        this.scene = finishScene;
        this.primaryStage.setScene(this.scene);
    }
    
    /**
     * Change the background in real time.
     */
    public void changeBackground(int level) { 
        SetUp.setBackground(this.layout, level);
        this.scene.setRoot(this.layout);
    }
    
    public Scene getScene() {
        return this.scene;
    }
    
    public void setScene(Scene s) {
        this.scene = s;
    }
    
    public Button getLeftOption() {
        return this.leftOption;
    }
    
    public void setLeftOption(Button b) {
        this.leftOption = b;
    }
    
    public void setRightOption(Button b) {
        this.rightOption = b;
    }
    
    public Button getRightOption() {
        return this.rightOption;
    }

    public Button getStart() {
        return start;
    }

    public void setStart(Button start) {
        this.start = start;
    }
    
    public void setEnterId(TextField t) {
        this.enterId = t;
    }
    
    public TextField getEnterId() {
        return this.enterId;
    }

    public Label getFeedback() {
        return this.feedback;
    }

    public void setFeedback(Label feedback) {
        this.feedback = feedback;
    }

    public Label getCongratulations() {
        return this.congratulations;
    }

    public void setCongratulations(Label congratulations) {
        this.congratulations = congratulations;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void setProgressBar(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public Label getGetReady() {
        return getReady;
    }

    public void setGetReady(Label getReady) {
        this.getReady = getReady;
    }

    public ProgressBar getGetReadyBar() {
        return getReadyBar;
    }

    public void setGetReadyBar(ProgressBar getReadyBar) {
        this.getReadyBar = getReadyBar;
    }

    public AnchorPane getLayout() {
        return layout;
    }

    public void setLayout(AnchorPane layout) {
        this.layout = layout;
    }

    public ImageView[] getStarNodes() {
        return starNodes;
    }

    public void setStarNodes(ImageView starNodes[]) {
        this.starNodes = starNodes;
    }

}
