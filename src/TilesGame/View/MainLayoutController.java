package TilesGame.View;

import TilesGame.Model.Tile;
import TilesGame.Util.LineToAbs;
import TilesGame.Util.MoveToAbs;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

public class MainLayoutController {
    @FXML
    private BorderPane mainPane = new BorderPane();

    @FXML
    private AnchorPane anchorPane = new AnchorPane();

    @FXML
    private Button startButton;

    @FXML
    private Button settingButton;

    @FXML
    private Label timeInHoursLabel;
    @FXML
    private Label timeInMinutesLabel;
    @FXML
    private Label timeInSecondsLabel;
    @FXML
    private Label timeInMilisecondsLabel;

    private BufferedImage imageToPose = new BufferedImage(720, 1080, BufferedImage.TYPE_INT_ARGB);

    private ArrayList<Tile> tilesArray = new ArrayList<>();
    private int tileWidthInPx =100;
    private int tileHeightInPx=100;

    private int numberOfColumns =4;
    private int numberOfRows =4;

    private Tile firstClickedTile;
    private Tile secondClickedTile;

    private long timeInMilliseconds =0;

    private Timeline timeline;
    private double spacingBetweenTiles =5;

    @FXML
    public void initialize() {
        setImageToPuzzle("Sample2.jpg");

        tileWidthInPx = getAndSetTileWidthInPx();
        tileHeightInPx=getTileHeightInPx();
        imageToPose = resizeImage(
                imageToPose,
                (int) (mainPane.getPrefWidth() * 0.85 ),
                (int) (mainPane.getPrefHeight() * 0.85 ));

        initTiles();

        for (Tile tile : tilesArray) {
            tile.setOnMouseClicked(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    if (firstClickedTile == null) {
                        firstClickedTile = (Tile) event.getSource();
                        markTile(firstClickedTile);
                        return;
                    }
                    if (firstClickedTile != null) {
                        secondClickedTile = (Tile) event.getSource();
                       if(!isAdjoining(firstClickedTile, secondClickedTile)) {
                           secondClickedTile = null;
                           return;
                       }
                        if (secondClickedTile != firstClickedTile){
                            swapElements();
                        }
                        unmarkTile(firstClickedTile);
                        firstClickedTile = secondClickedTile = null;
                    }
                }
            });
        }
    }


    private void initTiles(){
        getAndSetTileWidthInPx();
        tileHeightInPx=getTileHeightInPx();
        int idDistributor =0;
        for(int row =0; row<numberOfRows; row++) {
            for (int column = 0; column < numberOfColumns; column++) {
                Tile currentTile = new Tile(tileWidthInPx, tileHeightInPx,
                        imageToPose.getSubimage(
                                column * tileWidthInPx,
                                row * tileHeightInPx,
                                tileWidthInPx, tileHeightInPx),
                        idDistributor);
                currentTile.setLayoutX(column*(tileWidthInPx+spacingBetweenTiles));
                currentTile.setLayoutY(row*(tileHeightInPx+spacingBetweenTiles));
                currentTile.setFill(new ImagePattern(
                        SwingFXUtils.toFXImage(currentTile.getTileImage(),null)));
                anchorPane.getChildren().add(currentTile);

                tilesArray.add(currentTile);
                idDistributor++;
            }
        }
    }

    private void swapElements(){
        Collections.swap(tilesArray, firstClickedTile.getTileId(), secondClickedTile.getTileId());
        playTileSwapAnimation(firstClickedTile, secondClickedTile);
    }

    private void markTile(Tile tile){
        Blend blend = new Blend();
       // ColorInput topInput = new ColorInput((tile.getWidth())/2-10,(tile.getHeight())/2-10,20,20, Color.GREEN);
        ColorInput topInput = new ColorInput(0, 0, tile.getWidth(), tile.getHeight(), Color.DARKGREY);
        blend.setTopInput(topInput);
        blend.setMode(BlendMode.MULTIPLY);
        tile.setEffect(blend);
    }

    private void unmarkTile(Tile tile){
        tile.setEffect(null);
    }

    private void setImageToPuzzle(String filename){
        try {
            imageToPose = ImageIO.read(new File("out/production/TilesGame/Resources/" + filename));
        }catch (IOException e){
            try {
                imageToPose = ImageIO.read(new File("out/production/TilesGame/Resources/AGH_logo.png"));
                System.err.println("Wrong filename, loaded default image");
            }catch (IOException ee){
                System.err.println("Default image damaged or removed.");
            }
        }
    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight){
        BufferedImage resizedImage=new BufferedImage(targetWidth, targetHeight, originalImage.getType());
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        graphics2D.dispose();
        return resizedImage;
    }

    private int getAndSetTileWidthInPx(){
        if(mainPane.getPrefWidth()!=0) {
            tileWidthInPx = (int) (mainPane.getPrefWidth() * 0.85 / numberOfColumns);
        }
        return tileWidthInPx;
    }

    private int getTileHeightInPx(){
        if(mainPane.getPrefHeight()!=0)
            return (int)(mainPane.getPrefHeight()*0.85/numberOfRows);
        return (int)(tileWidthInPx*1.50);
    }

    @FXML
    private void handleStartButton(){
        shuffleTiles();
        startTimer();
    }

    private void shuffleTiles(){
       Collections.shuffle(tilesArray);
       refillTiles();
    }

    private void playTileSwapAnimation(Tile first, Tile second){
        double firstX= first.getLayoutX(), firstY=first.getLayoutY();
        PathTransition pathTransition = getPathTransition(first, second);
        PathTransition pathTransition2 = getPathTransition(second, first);
        ParallelTransition parallelTransition = new ParallelTransition(pathTransition, pathTransition2);
        parallelTransition.play();
        parallelTransition.setOnFinished(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                first.setTranslateX(0);
                first.setTranslateY(0);
                second.setTranslateX(0);
                second.setTranslateY(0);
                refillTiles(first, second);

               // first.setLayoutY(second.getLayoutY());
               // first.setLayoutX(second.getLayoutX());
               // second.setLayoutX(firstX);
               // second.setLayoutY(firstY);

                if (isPuzzleSolved()) {
                    System.out.println("Solved!");
                    stopTimer();
                    handlePuzzleSolved();
                }
            }
        });
    }

    private PathTransition getPathTransition(Tile first, Tile second){
        PathTransition pathTransition = new PathTransition();
        Path path = new Path();
        path.getElements().clear();
        path.getElements().add(new MoveToAbs(first));
        path.getElements().add(new LineToAbs(first, second.getLayoutX(), second.getLayoutY()));
        pathTransition.setPath(path);
        pathTransition.setNode(first);
        return pathTransition;
    }

    private boolean isPuzzleSolved(){
        for(Tile tile : tilesArray){
            if(tile.getTileId()!=tilesArray.indexOf(tile))
                return false;
        }
        return true;
    }


    private void refillTiles(){
        for(int i=0; i<tilesArray.size();i++){
            Tile tile =  tilesArray.get(i);
            int id = tile.getTileId();
            tile.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesArray.get(id).getTileImage(), null)));
        }
    }

    private void refillTiles(Tile first, Tile second){
        first.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesArray.get(first.getTileId()).getTileImage(), null)));
        second.setFill(new ImagePattern(SwingFXUtils.toFXImage(tilesArray.get(second.getTileId()).getTileImage(), null)));
    }

    private void updateTime() {
        timeInMilisecondsLabel.setText(String.format("%d", TimeUnit.MILLISECONDS.toMillis(timeInMilliseconds)%1000/10));
        timeInSecondsLabel.setText(String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(timeInMilliseconds)%60));
        timeInMinutesLabel.setText(String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(timeInMilliseconds)%60));
        timeInHoursLabel.setText(String.format("%02d",TimeUnit.MILLISECONDS.toHours(timeInMilliseconds)));
        timeInMilliseconds +=10;
    }

    private void startTimer(){
        timeline=new Timeline(new KeyFrame(
                Duration.millis(10),
                new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        updateTime();
                    }
                }
        ));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    private void stopTimer() {
        if(timeline!=null)
            timeline.stop();
    }

    private void handlePuzzleSolved(){
        Platform.runLater(new Runnable() {
            @Override public void run(){
                stopTimer();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Winner");
                alert.setHeaderText("Wygrales!");
                alert.setContentText("Twój czas: "+timeInHoursLabel.getText()+"h : "+timeInMinutesLabel.getText()+"m : "+timeInSecondsLabel.getText()+"s : "+ timeInMilisecondsLabel.getText()+"ss");
                alert.showAndWait(); } });
    }

    private boolean isAdjoining(Tile center, Tile tile){
        if(tile.getLayoutX()==center.getLayoutX() && Math.abs(tile.getLayoutY()-center.getLayoutY())<=tileHeightInPx+spacingBetweenTiles)
            return true;
        if(tile.getLayoutY()==center.getLayoutY() && Math.abs(tile.getLayoutX()-center.getLayoutX())<=tileWidthInPx+spacingBetweenTiles)
            return true;
        return false;
    }

    public int getNumberOfColumns() {
        return numberOfColumns;
    }

    public void setNumberOfColumns(int numberOfColumns) {
        this.numberOfColumns = numberOfColumns;
    }

    public int getNumberOfRows() {
        return numberOfRows;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.numberOfRows = numberOfRows;
    }


}
