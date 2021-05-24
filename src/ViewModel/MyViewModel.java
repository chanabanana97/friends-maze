package ViewModel;

import Model.IModel;
import Model.MyModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private IModel model;

    public MyViewModel(IModel model) {
        this.model = model;
    }

    public void generateMaze(int rows, int cols){
        model.generateMaze(rows, cols);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Position getPlayerPos() {
        return model.getCharacterPosition();
    }

    public Solution getSolution(){return model.getSolution(); }

    public void moveCharacter(KeyCode movement) {
        model.moveCharacter(movement);
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers();

    }

    public void solveMaze() {
        model.solveMaze();
    }

    public void saveMaze(String path) {
        model.saveMaze(path);
    }

    public void loadMaze(String path) {
        model.loadMaze(path);
    }

    public void stopServers() {
        model.stopServers();
    }
}
