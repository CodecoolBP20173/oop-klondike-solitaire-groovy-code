package com.codecool.klondike;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;

public class Klondike extends Application {

    private static final double WINDOW_WIDTH = 1366;
    private static final double WINDOW_HEIGHT = 768;

    public static void main(String[] args) {
        launch(args);
    }


    private static MediaPlayer mediaPlayer;
    public void wiggle(){
        Media track = new Media(new File("/home/tmarci/codecool/OOP-JAVA/week_2/TW/solitaire/oop-klondike-solitaire-groovy-code/src/com/codecool/klondike/wiggle.mp3").toURI().toString());
        mediaPlayer = new MediaPlayer(track);
        mediaPlayer.setOnEndOfMedia(() -> mediaPlayer.seek(Duration.seconds(30.3f)));
        mediaPlayer.setStartTime(Duration.seconds(3.3));
        mediaPlayer.setVolume(0.5);
        mediaPlayer.play();
    }

    @Override
    public void start(Stage primaryStage) {
        Card.loadCardImages();
        Game game = new Game();
        game.setTableBackground(new Image("/table/parquet.jpg"));
        game.setButtons();

        primaryStage.setTitle("Klondike Solitaire");
        primaryStage.setScene(new Scene(game, WINDOW_WIDTH, WINDOW_HEIGHT));
        primaryStage.show();
        wiggle();
    }

}
