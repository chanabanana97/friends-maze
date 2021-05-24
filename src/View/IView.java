package View;

import ViewModel.MyViewModel;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

public interface IView {
    void setViewModel(MyViewModel viewModel);
    void generateMaze();
    void keyPressed(KeyEvent keyEvent);
    void solveMaze();
    void mouseClicked(MouseEvent mouseEvent);
    void saveMaze();
    void loadMaze();
    void setResizeEvent(Scene scene);
    }
