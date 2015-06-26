package view;

import model.AlphaPairGenerator;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.text.Font;
import javafx.stage.Stage;

/**
 * Functions to set up the GameGUI.
 * 
 * @author Tony Jiang
 * 6-25-2015
 * 
 */
public final class SetUp {
    
    /** 
     * Login Screen Element Positions. 
     *
     * Position of the label prompting the subject
     * to enter her subject ID. */
    static final int LABEL_POSITION_X = 200;
    static final int LABEL_POSITION_Y = 150;
    
    /** Position of text field where subject will enter
     * her subject ID. */
    static final int ENTER_ID_POSITION_X = 180;
    static final int ENTER_ID_POSITION_Y = 170;
    
    /** Position of feedback telling subject to re-enter
     * their subject ID. */
    static final int FEEDBACK_POSITION_X = 190;
    static final int FEEDBACK_POSITION_Y = 250;
    
    /** Position of start button to start the trials. */
    static final int START_POSITION_X = 235;
    static final int START_POSITION_Y = 200;
    
    /**
     * Game Screen Element Positions and Sizes.
     *
     * Positions of the choices the subject can pick. */
    static final int LEFT_OPTION_X = 30;
    static final int LEFT_OPTION_Y = 90;
    static final int RIGHT_OPTION_X = 270;
    static final int RIGHT_OPTION_Y = 90;
    /** Font size of the letter options. */
    static final int LETTER_SIZE = 100;
    
    /**
     * Finish Screen Element Positions.
     */
    /** Position of message congratulating subject on completing
     * the experiment. */
    static final int CONGRATS_X = 203;
    static final int CONGRATS_Y = 170;
    /** Position of message with subject's score. */
    static final int SCORE_X = 183;
    static final int SCORE_Y = 200;
    
    /** Disable constructing of an object. */
    private SetUp() {
        
    }
    
    /**
     * Set up the login screen.
     * @param view The graphical user interface.
     * @param primaryStage The stage.
     * @return The login scene.
     */
    public static Scene setUpLoginScreen(GameGUI view, Stage primaryStage) {
        
        Label label = new Label("Enter your Subject ID");
        label.setLayoutY(LABEL_POSITION_Y);
        label.setLayoutX(LABEL_POSITION_X);
        view.setStart(new Button("Start"));
        view.getEnterId().setLayoutY(ENTER_ID_POSITION_Y);
        view.getEnterId().setLayoutX(ENTER_ID_POSITION_X);
        view.getEnterId().setAlignment(Pos.CENTER);
        view.setFeedback(new Label());
        view.getFeedback().setLayoutY(FEEDBACK_POSITION_Y);
        view.getFeedback().setLayoutX(FEEDBACK_POSITION_X);
        AnchorPane layout = new AnchorPane();
        layout.getChildren().addAll(
                label, view.getStart(), view.getEnterId(), view.getFeedback());
        view.getStart().setLayoutY(START_POSITION_Y);
        view.getStart().setLayoutX(START_POSITION_X);
        
        Scene scene = new Scene(layout, 
                GameGUI.SCREEN_WIDTH, GameGUI.SCREEN_HEIGHT);

        setBackground(view, layout);
        
        return scene;
    }
    
    /**
     * Set up the game screen where subject will undergo trials.
     * @param view The graphical user interface.
     * @param primaryStage The stage.
     * @param subjectID The subject's ID.
     * @return The game scene.
     */
    public static Scene setUpGameScreen(GameGUI view, 
            Stage primaryStage, String subjectID) {
        
        AnchorPane layout = new AnchorPane();
        view.getCurrentPlayer().setSubjectID(Integer.parseInt(subjectID));
        System.out.println(view.getCurrentPlayer().getSubjectID());
        setUpOptions(view);
        initialButtonSetUp(view);
        layout.getChildren().add(view.getLeftOption());
        layout.getChildren().add(view.getRightOption());
        setBackground(view, layout);
        //primaryStage.setFullScreen(true);
        return new Scene(layout, GameGUI.SCREEN_WIDTH, GameGUI.SCREEN_HEIGHT);
    }
    
    /**
     * Set up the positioning of the two options.
     * @param view The graphical user interface.
     */
    static void setUpOptions(GameGUI view) {
        //Create buttons and set text
        view.setLeftOption(new Button());
        view.setRightOption(new Button());

      //Set absolute positions of each leftOption
        view.getLeftOption().setLayoutX(LEFT_OPTION_X);
        view.getLeftOption().setLayoutY(LEFT_OPTION_Y);
        view.getRightOption().setLayoutX(RIGHT_OPTION_X);
        view.getRightOption().setLayoutY(RIGHT_OPTION_Y);
    }
    
    /**
     * Setup the content and style of the letters as the
     * two options.
     * @param view The graphical user interface.
     */
    public static void initialButtonSetUp(GameGUI view) {
        AlphaPairGenerator apg = new AlphaPairGenerator();
        char letterOne, letterTwo;
        
        view.setCurrentAlphaPair(apg.getAlphaPair());
        letterOne = view.getCurrentAlphaPair().getLetterOne();
        letterTwo = view.getCurrentAlphaPair().getLetterTwo();
        
        view.getLeftOption().setText(String.valueOf(letterOne));
        view.getRightOption().setText(String.valueOf(letterTwo));
        view.getLeftOption().setFont(new Font("Tahoma", LETTER_SIZE));
        view.getRightOption().setFont(new Font("Tahoma", LETTER_SIZE));
        view.getLeftOption().setStyle("-fx-background-color: transparent;");
        view.getRightOption().setStyle("-fx-background-color: transparent;");
    }

    /**
     * Set up the finish screen.
     * @param view The graphical user interface.
     * @param primaryStage The stage.
     * @return The finishing scene.
     */
    public static Scene setUpFinishScreen(GameGUI view, Stage primaryStage) {
        
        AnchorPane layout = new AnchorPane();
        
        Label score = new Label();
        score.setText("You scored " 
                + view.getCurrentPlayer().getNumCorrect() + " points!");
        view.setCongratulations(new Label("You did it!"));
        view.getCongratulations().setFont(Font.font("Verdana", 20));
        score.setFont(Font.font("Tahoma", 16));
        
        view.getCongratulations().setLayoutX(CONGRATS_X);
        view.getCongratulations().setLayoutY(CONGRATS_Y);
        
        score.setLayoutX(SCORE_X);
        score.setLayoutY(SCORE_Y);
        
        layout.getChildren().addAll(view.getCongratulations(), score);
        
        setBackground(view, layout);
        
        return new Scene(layout, GameGUI.SCREEN_WIDTH, GameGUI.SCREEN_HEIGHT);
    }
    
    /**
     * Set the background.
     * @param view The graphical user interface.
     * @param layout The layout.
     */
    public static void setBackground(GameGUI view, AnchorPane layout) {
        BackgroundImage bg = new BackgroundImage(
                new Image(
                        "UI/images/sky.png", 
                        GameGUI.SCREEN_WIDTH,
                        GameGUI.SCREEN_HEIGHT, 
                        false, true),
                BackgroundRepeat.NO_REPEAT, 
                BackgroundRepeat.NO_REPEAT, 
                BackgroundPosition.CENTER,
                BackgroundSize.DEFAULT);
        layout.setBackground(new Background(bg));
    }
    
}
