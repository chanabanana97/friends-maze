package View;

import Model.*;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("../View/opening.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("F.R.I.E.N.D.S Maze");
         Scene openScene = new Scene(root, 800, 650);
        openScene.getStylesheets().add(getClass().getResource("../View/Open.css").toExternalForm());
        primaryStage.setScene(openScene);


        MyModel model = new MyModel();
        MyViewModel viewModel = new MyViewModel(model);
        model.addObserver(viewModel);

        MyViewController view = fxmlLoader.getController();
        view.setViewModel(viewModel);
        view.setStage(primaryStage);
        view.playThemeSong();

//        view.setResizeEvent(openScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> view.exit()); //safe exit
    }


    public static void main(String[] args) {
        launch(args);
    }
}
