package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.util.logging.Logger;

import model.AlphaPair;
import model.AlphaPairGenerator;
import model.Player;


/**
 * Class for grabbing and exporting data to a CSV file.
 * 
 * Classes Related to:
 *  -LetterGameController.java
 *      -Grabs AlphaPair and Player from the controller to record and export their data.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public class DataWriter {
    private static Logger logger = Logger.getLogger("mylog");
    
    public static final String DELIMITER = ",";
    public static final String SUBJECT_ID = "Subject ID";
    public static final String LEFT_CHOICE = "Left Choice";
    public static final String RIGHT_CHOICE = "Right Choice";
    public static final String WHICH_SIDE_CORRECT = "Side Correct";
    public static final String WHICH_SIDE_PICKED = "Side Picked";
    public static final String IS_CORRECT = "Correct";
    public static final String DIFFICULTY = "Difficulty";
    public static final String DISTANCE = "Distance";
    public static final String LEFT_CHOICE_SIZE = "Left Choice Size";
    public static final String RIGHT_CHOICE_SIZE = "Right Choice Size";
    public static final String WHICH_SIZE_CORRECT = "Which Size Correct";
    public static final String RESPONSE_TIME = "Response Time";
    public static final String DATE_TIME = "Date/Time";
    public static final String CONSECUTIVE_ROUND = "Consecutive Rounds";
    
    /** The subject to grab data from. */
    private Player player;
    /** AlphaPair to grab data from. */
    private AlphaPair alphaPair;
    
    /**
     * Constructor for data writer that takes in a controller
     * and grabs the player and alpha pair.
     * @param lgc Controller to grab data from
     */
    public DataWriter(LetterGameController lgc) {
        this.player = lgc.getThePlayer();
        this.alphaPair = lgc.getCurrentAlphaPair();
    }
    
    /**
     * Regrab the current subject and alphapair from the controller.
     * @param lgc Controller to grab data from
     */
    public void grabData(LetterGameController lgc) {
        this.player = lgc.getThePlayer();
        this.alphaPair = lgc.getCurrentAlphaPair();
    }
    
    /**
     * Export data to CSV file. Appends to current CSV if data
     * for subject already exists.
     * Location of CSV file is in folder "results". "Results" will contain
     * subfolders each titled by Subject ID number containing the subject's
     * CSV data. 
     */
    public void writeToCSV() {
        
        PrintWriter writer = null;
        String subjectId = Integer.toString(this.player.getSubjectID());
        try {
            /** Grab path to project */
            String path = new File(".").getAbsolutePath();
            path = path.substring(0, path.length() - 1);
            
            /** Create results folder if doesn't exist */
            File resultsDir = new File("results");
            resultsDir.mkdir();
            
            /** Create subject folder if doesn't exist */
            File subjectDir = new File("results\\" + subjectId);
            subjectDir.mkdir();    
            
            /** Create new csv file for subject if doesn't exist */
            File file = new File(path + "\\results\\" + subjectId 
                    + "\\results_" + subjectId + ".csv");            
            logger.info(file.getPath());
            String text = "";
            
            /** Write data to new file or append to old file */
            if (file.createNewFile()) {
                text += this.generateColumnNames();
            }
            text += this.generateTrialText();
            writer = new PrintWriter(
                        new BufferedWriter(
                            new FileWriter(file, true)));
            writer.write(text);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            
        } finally {
            writer.flush();
            writer.close();
        } 
    }
    
    /**
     * Generate the column names.
     * @return String column names.
     */
    private String generateColumnNames() {
        String text = SUBJECT_ID + DELIMITER
                + LEFT_CHOICE + DELIMITER
                + RIGHT_CHOICE + DELIMITER
                + WHICH_SIDE_CORRECT + DELIMITER
                + WHICH_SIDE_PICKED + DELIMITER
                + IS_CORRECT + DELIMITER
                + DIFFICULTY + DELIMITER
                + DISTANCE + DELIMITER
                + LEFT_CHOICE_SIZE + DELIMITER
                + RIGHT_CHOICE_SIZE + DELIMITER
                + WHICH_SIZE_CORRECT + DELIMITER
                + RESPONSE_TIME + DELIMITER
                + DATE_TIME + DELIMITER
                + CONSECUTIVE_ROUND + "\n";
        return text;
    }

    /**
     * Generate the CSV text data for the round (one pair).
     * @return String CSV text data
     */
    public String generateTrialText() {
        String subjectID = this.generateSubjectIdText();
        String leftChoice = this.generateLeftChoiceText();       
        String rightChoice = this.generateRightChoiceText();
        String whichSideCorrect = this.generateWhichSideCorrectText();
        String whichSidePicked = this.generateWhichSidePickedText(whichSideCorrect);
        String correct = this.generateCorrectText();
        String difficulty = this.generateDifficultyText();
        String distance = this.generateDistanceText();
        String leftChoiceSize = this.generateLeftChoiceSizeText();
        String rightChoiceSize = this.generateRightChoiceSizeText();
        String whichSizeCorrect = this.generateSizeCorrectText(whichSideCorrect);
        String responseTime = this.generateResponseTimeText();
        String dateTime = this.generateDateTimeText();
        String consecutiveRounds = this.generateConsecutiveRoundsText();
        
        String trialText = subjectID + DELIMITER
                + leftChoice + DELIMITER
                + rightChoice + DELIMITER
                + whichSideCorrect + DELIMITER
                + whichSidePicked + DELIMITER
                + correct + DELIMITER
                + difficulty + DELIMITER
                + distance + DELIMITER
                + leftChoiceSize + DELIMITER
                + rightChoiceSize + DELIMITER
                + whichSizeCorrect + DELIMITER
                + responseTime + DELIMITER
                + dateTime + DELIMITER
                + consecutiveRounds + "\n";
        
        return trialText;
    }
    
    private String generateSubjectIdText() {
        return Integer.toString(
                this.player.getSubjectID());
    }
    
    private String generateLeftChoiceText() {
        return String.valueOf(
                this.alphaPair.getLetterOne());
    }
    
    private String generateRightChoiceText() {
        return String.valueOf(
                this.alphaPair.getLetterTwo());
    }
    
    private String generateWhichSideCorrectText() {
        if (this.alphaPair.isLeftCorrect()) {
            return "left";
        } else {
            return "right";
        }
    }
    
    private String generateWhichSidePickedText(String whichSideCorrect) {
        if (this.player.isRight()) {
            return whichSideCorrect;
        } else {
            if (whichSideCorrect.equals("left")) {
                return "right";
            } else {
                return "left";
            }
        }
    }
    
    private String generateCorrectText() {
        if (this.player.isRight()) {
            return "yes";
        } else {
            return "no";
        }
    }
    
    private String generateDifficultyText() {
        int difference = Math.abs(this.alphaPair.getDifference());
        if (difference >= AlphaPairGenerator.EASY_MODE_MIN &&
                difference < AlphaPairGenerator.EASY_MODE_MIN 
                + AlphaPairGenerator.NUM_CHOICES_IN_MODE) {
            return "EASY";
        } else if (difference >= AlphaPairGenerator.MEDIUM_MODE_MIN &&
                difference < AlphaPairGenerator.MEDIUM_MODE_MIN
                + AlphaPairGenerator.NUM_CHOICES_IN_MODE) {
            return "MEDIUM";
        } else if (difference >= AlphaPairGenerator.HARD_MODE_MIN &&
                difference < AlphaPairGenerator.HARD_MODE_MIN 
                + AlphaPairGenerator.NUM_CHOICES_IN_MODE) {
            return "HARD";
        } 
        return "";
    }
    
    private String generateDistanceText() {
        return Integer.toString(Math.abs(
                    this.alphaPair.getDifference()));
    }
    
    private String generateLeftChoiceSizeText() {
        return Integer.toString(
                this.alphaPair.getFontSizeOne());
    }
    
    private String generateRightChoiceSizeText() {
        return Integer.toString(
                this.alphaPair.getFontSizeTwo());
    }
    
    private String generateSizeCorrectText(String whichSideCorrect) {
        if (whichSideCorrect.equals("left") && (this.alphaPair.getFontSizeOne() > this.alphaPair.getFontSizeTwo())
                || whichSideCorrect.equals("right") && (this.alphaPair.getFontSizeTwo() > this.alphaPair.getFontSizeOne())) {
            return "Bigger";
        } else {
            return "Smaller";
        }
    }
    
    private String generateResponseTimeText() {
        return String.valueOf(this.player.getRT() / 1000000000.0);
    }
    
    private String generateDateTimeText() {
        return LocalDateTime.now().toString();
    }
    
    private String generateConsecutiveRoundsText() {
        return Integer.toString(
                this.player.getNumRounds());
    }
}
