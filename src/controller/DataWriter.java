package controller;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.time.LocalDateTime;

import view.GameGUI;
import model.Player;

/**
 * Class for grabbing and exporting data to a CSV file.
 * 
 * @author Tony Jiang
 * 6-25-2015
 *
 */
public class DataWriter {

    public static final String DELIMITER = ",";
    public static final String SUBJECT_ID = "Subject ID";
    public static final String LEFT_CHOICE = "Left Choice";
    public static final String RIGHT_CHOICE = "Right Choice";
    public static final String WHICH_SIDE_CORRECT = "Side Correct";
    public static final String WHICH_SIDE_PICKED = "Side Picked";
    public static final String IS_CORRECT = "Correct";
    public static final String DIFFICULTY = "Distance";
    public static final String RESPONSE_TIME = "Response Time";
    public static final String DATE_TIME = "Date/Time";
    public static final String CONSECUTIVE_ROUND = "Consecutive Rounds";
    
    /** The graphical user interface. */
    private GameGUI view;
    /** The subject to grab data from. */
    private Player player;
    
    /**
     * Constructor for data writer.
     */
    public DataWriter() {
        this.view = null;
        this.player = null;
    }
    
    /**
     * Constructor for data writer that takes in a view
     * and grabs the player.
     * @param view
     */
    public DataWriter(GameGUI view) {
        this.view = view;
        this.player = view.getCurrentPlayer();
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
            System.out.println(file.getPath());
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
                + RESPONSE_TIME + DELIMITER
                + DATE_TIME + DELIMITER
                + CONSECUTIVE_ROUND + "\n";
        return text;
    }

    /**
     * Generate the CSV text data for the round.
     * @return String CSV text data
     */
    public String generateTrialText() {
        
        String subjectID = 
                Integer.toString(
                        this.view.getCurrentPlayer().getSubjectID());
        String leftChoice = 
                String.valueOf(
                        this.view.getCurrentAlphaPair().getLetterOne());
        String rightChoice = 
                String.valueOf(
                        this.view.getCurrentAlphaPair().getLetterTwo());
        String whichSideCorrect;
        String whichSidePicked;
        String correct;
        String difficulty;
        String responseTime;
        String dateTime;
        String consecutiveRounds;
        
        if (this.view.getCurrentAlphaPair().isLeftCorrect()) {
            whichSideCorrect = "left";
        } else {
            whichSideCorrect = "right";
        }
        
        if (this.player.isRight()) {
            whichSidePicked = whichSideCorrect;
            correct = "yes";
        } else {
            if (whichSideCorrect.equals("left")) {
                whichSidePicked = "right";
            } else {
                whichSidePicked = "left";
            }
            correct = "no";
        }
        
        difficulty = 
            Integer.toString(
                Math.abs(
                    this.view.getCurrentAlphaPair().getDifference()));
        
        /** Convert from nanoseconds to seconds */
        responseTime = String.valueOf(this.player.getRT() / 1000000000.0);
        
        dateTime = LocalDateTime.now().toString();
        
        consecutiveRounds = Integer.toString(
                this.view.getCurrentPlayer().getNumRounds());
        
        String trialText = subjectID + DELIMITER
                + leftChoice + DELIMITER
                + rightChoice + DELIMITER
                + whichSideCorrect + DELIMITER
                + whichSidePicked + DELIMITER
                + correct + DELIMITER
                + difficulty + DELIMITER
                + responseTime + DELIMITER
                + dateTime + DELIMITER
                + consecutiveRounds + "\n";
        
        return trialText;
    }
}
