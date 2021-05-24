package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;

public interface IModel {

    void generateMaze(int rows, int cols);
    void solveMaze();
    void moveCharacter(KeyCode movement);
    Maze getMaze();
    Solution getSolution();
    Position getCharacterPosition();
    void saveMaze(String path);
    void loadMaze(String path);
    void startServers();
    void stopServers();
}
