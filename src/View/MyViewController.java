package View;

import Server.Configurations;
import ViewModel.MyViewModel;
import algorithms.mazeGenerators.*;
import algorithms.search.BestFirstSearch;
import algorithms.search.BreadthFirstSearch;
import algorithms.search.DepthFirstSearch;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;


public class MyViewController implements IView, Observer {
    private Maze maze;
    private MyViewModel viewModel;
    private boolean solutionButtonPressed = false;
    private static MediaPlayer mediaPlayer= new MediaPlayer(new Media(new File("resources/Music/ThemeSong.mp3").toURI().toString()));
    private static Media song;
    private Stage stage;
    public boolean firstCall;

    @FXML
    public MazeDisplayer mazeDisplayer;
    public TextField rowsText;
    public TextField colsText;
    public Button solveButton;
    public TextField numOfThreads;
    public Button muteButton;
    public Label playerRow;
    public Label playerCol;
    public MenuButton solutionAlgorithm;
    public MenuButton mazeType;
    public Button hideSolButton;
    public Button play;


    StringProperty updatePlayerPositionRow = new SimpleStringProperty();
    StringProperty updatePlayerPositionCol = new SimpleStringProperty();


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        if(firstCall) {
            playerRow.textProperty().bind(updatePlayerPositionRow);
            playerCol.textProperty().bind(updatePlayerPositionCol);
        }
        firstCall = true;
    }


    public void generateMaze() {

        //mediaPlayer.play();
        int rows, cols;
        try {
            rows = Integer.parseInt(rowsText.getText());
            cols = Integer.parseInt(colsText.getText());
            if (rows < 2 || cols < 2 || rows > 1000 || cols > 1000)
                throw new NumberFormatException();

            viewModel.generateMaze(rows,cols);
            maze = viewModel.getMaze();
            mazeDisplayer.setMaze(maze);
            solveButton.setDisable(false);
            hideSolButton.setDisable(true);

            mediaPlayer.play();
            solutionButtonPressed = false;

            updatePlayerPositionRow.set(maze.getStartPosition().getRowIndex() + "");
            updatePlayerPositionCol.set(maze.getStartPosition().getColumnIndex() + "");

        }catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Please enter a number between 2 and 1000");
        }
    }

    private void showAlert(Alert.AlertType type, String message){
        Alert alert = new Alert(type, message);
        if (type == Alert.AlertType.NONE) {
            alert.getDialogPane().getButtonTypes().add(ButtonType.OK);
            alert.setTitle("You Won!");
        }
        alert.show();
    }

    public void solveMaze()
    {
        viewModel.solveMaze();
        mazeDisplayer.setSolution(viewModel.getSolution());
        mazeDisplayer.drawMaze();
        solutionButtonPressed = true;
        hideSolButton.setVisible(true);
        hideSolButton.setDisable(false);

    }

    public void keyPressed(KeyEvent keyEvent) {

        viewModel.moveCharacter(keyEvent.getCode());

        //updating character position labels
        Position playerPos = viewModel.getPlayerPos();
        int characterPositionRow = playerPos.getRowIndex();
        int characterPositionColumn = playerPos.getColumnIndex();
        mazeDisplayer.setPlayerPosition(characterPositionRow, characterPositionColumn);
        this.updatePlayerPositionRow.set(characterPositionRow + "");
        this.updatePlayerPositionCol.set(characterPositionColumn + "");

        //reached goal
        if (maze.getGoalPosition().equals(viewModel.getPlayerPos())) {
            showAlert(Alert.AlertType.NONE, "Congratulations! You have solved the maze!");
            playWinningSong();
        }
//        playThemeSong();
        if (solutionButtonPressed)
            solveMaze();
        keyEvent.consume();

    }

    public void zoom(ScrollEvent scrollEvent) {
        if (scrollEvent.isControlDown()) {
            double zoomSize;
            double changeResolution;
            double scaleX;
            double scaleY;
            scaleX = mazeDisplayer.getScaleX();
            scaleY= mazeDisplayer.getScaleY();
            changeResolution = scrollEvent.getDeltaY();
            if (changeResolution > 0) {
                zoomSize = 1.02;
                mazeDisplayer.setScaleX(scaleX * zoomSize);
                mazeDisplayer.setScaleY(scaleY * zoomSize);
                scrollEvent.consume();
            } else if (changeResolution < 0) {
                zoomSize = 0.98;
                mazeDisplayer.setScaleX(scaleX * zoomSize);
                mazeDisplayer.setScaleY(scaleY * zoomSize);
                scrollEvent.consume();
            }
        }
    }



    public void saveMaze(){

        if (maze == null)
            showAlert(Alert.AlertType.ERROR, "Please generate a maze to save");
        else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save");
            File selectedFile = fileChooser.showSaveDialog(new Stage());

            viewModel.saveMaze(selectedFile.getPath());
        }
    }

    public void loadMaze() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open");
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        viewModel.loadMaze(selectedFile.getPath());
        maze = viewModel.getMaze();
        mazeDisplayer.setMaze(maze);
    }

    public void setMazeType(ActionEvent actionEvent) {

        String type = ((MenuItem)actionEvent.getSource()).getText();
        if (type.equals("Prim's Maze"))
            Configurations.setMazeGenerator(new MyMazeGenerator());
        else if (type.equals("Simple Maze"))
            Configurations.setMazeGenerator(new SimpleMazeGenerator());
        else
            Configurations.setMazeGenerator(new EmptyMazeGenerator());
        mazeType.setText(type);
    }

    public void setSolutionType(ActionEvent actionEvent) {

        String type = ((MenuItem)actionEvent.getSource()).getText();
        if (type.equals("Best First Search"))
            Configurations.setSearchingAlgorithm(new BestFirstSearch());
        else if (type.equals("Breadth First Search"))
            Configurations.setSearchingAlgorithm(new BreadthFirstSearch());
        else
            Configurations.setSearchingAlgorithm(new DepthFirstSearch());
        solutionAlgorithm.setText(type);
    }

    public void setNumOfThreads() {
        String num = numOfThreads.getText();
        try{
            Configurations.setThreadsAmount(Integer.parseInt(num));}
        catch (NumberFormatException e){
            showAlert(Alert.AlertType.ERROR, "Please enter a number");
        }
    }

    public void playThemeSong(){
        //Media song = new Media(new File("resources/Music/ThemeSong.mp3").toURI().toString());
        //mediaPlayer = new MediaPlayer(song);
        mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
        mediaPlayer.play();
    }

    public void playWinningSong(){
        mediaPlayer.pause();
        song = new Media(new File("resources/Music/EndSong.mp3").toURI().toString());
        MediaPlayer winningSong = new MediaPlayer(song);
        winningSong.play();
    }

    public void mute(){
//        mediaPlayer.pause();
        if (muteButton.getText().equals("Mute"))
        {
            mediaPlayer.setMute(true);
            muteButton.setText("Unmute");
        }
        else{ // unmute
            mediaPlayer.setMute(false);
            muteButton.setText("Mute");
        }

    }


    public void setResizeEvent(Scene scene) {
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                mazeDisplayer.drawMaze();
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                mazeDisplayer.drawMaze();
            }
        });
    }


    public void mouseClicked(MouseEvent mouseEvent) {
        mazeDisplayer.requestFocus();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if (maze == null || maze != viewModel.getMaze())  // ??
            {
                maze = viewModel.getMaze();
                mazeDisplayer.setMaze(maze);
            }
            else {
                mazeDisplayer.setPlayerPosition(viewModel.getPlayerPos().getRowIndex(), viewModel.getPlayerPos().getColumnIndex());

            }
        }

    }


    public void exit() {
        viewModel.stopServers();
        Platform.exit();

    }
    public void help(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent parent = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(parent, 400, 350);

            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();

        } catch (Exception e) {
        }
    }

    public void about(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("About");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent parent = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(parent, 400, 350);
            stage.setScene(scene);
//            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();

        } catch (Exception e) {
        }
    }

    public void hideSolution(ActionEvent actionEvent) {
        mazeDisplayer.setSolution(null);
        mazeDisplayer.drawMaze();
        solutionButtonPressed = false;
    }

    public void play(ActionEvent actionEvent) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
        Scene scene = new Scene(root,900,650);
        scene.getStylesheets().add("View/Style.css");
        mediaPlayer.pause();
        stage.setScene(scene);
        MyViewController myViewController = fxmlLoader.getController();
        myViewController.setStage(stage);
        myViewController.setViewModel(viewModel);
        myViewController.setResizeEvent(scene);
        viewModel.addObserver(myViewController);
        mediaPlayer.play();

    }

    public void setStage(Stage stage) {
        this.stage=stage;
    }
}
