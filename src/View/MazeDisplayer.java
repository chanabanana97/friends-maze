package View;

import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class MazeDisplayer extends Canvas {

    private Maze maze;
    private Solution solution;
    private int rowPlayer;
    private int colPlayer;
    private double cellHeight;
    private double cellWidth;


    public void setMaze(Maze maze) {
        this.maze = maze;
        this.solution = null;
        rowPlayer = maze.getStartPosition().getRowIndex();
        colPlayer = maze.getStartPosition().getColumnIndex();

        drawMaze();
    }

    public void setSolution(Solution sol){
        this.solution = sol;
    }

    @Override
    public boolean isResizable() {
        return true;
    }

    @Override
    public double prefWidth(double height) {
        return getWidth();
    }

    @Override
    public double prefHeight(double width) {
        return getHeight();
    }

    public int getRowPlayer() {
        return rowPlayer;
    }

    public int getColPlayer() {
        return colPlayer;
    }

    public void setPlayerPosition(int row, int col){
        this.rowPlayer = row;
        this.colPlayer = col;
        drawMaze();
    }


    public void drawMaze()
    {
        if(maze != null)
        {
            cellHeight = (getHeight()-120) / maze.getRows();
            cellWidth = (getWidth()-50) / maze.getColumns();

            GraphicsContext graphicsContext = getGraphicsContext2D();
            graphicsContext.clearRect(0,0,getWidth(),getHeight());
            graphicsContext.setFill(Color.BLUEVIOLET);
            double w,h;
           // Draw Maze
            Image wallImage = null;
            try {
                wallImage = new Image(new FileInputStream("resources/Images/wallImage.jpg"));
            } catch (FileNotFoundException e) {
                System.out.println("There is no wall picture....");
            }
            for(int i=0; i<maze.getRows(); i++)
            {
                for(int j=0; j<maze.getColumns(); j++)
                {
                    if(maze.getMaze()[i][j] == 1) // Wall
                    {
                        h = i * cellHeight;
                        w = j * cellWidth;
                        if (wallImage == null){
                        graphicsContext.fillRect(w,h,cellWidth,cellHeight);
                        }else
                            {
                            graphicsContext.drawImage(wallImage,w,h,cellWidth,cellHeight);
                        }
                    }
                }
            }
            Image playerImage = null;
            try {
                playerImage = new Image(new FileInputStream("./resources/Images/chandler.jpg"));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image player....");
            }
            graphicsContext.drawImage(playerImage,getColPlayer() * cellWidth,getRowPlayer() * cellHeight,cellWidth,cellHeight);
//            graphicsContext.fillRect(w_player,h_player,cellWidth,cellHeight);

            // set goal position image
            int goalRow = maze.getGoalPosition().getRowIndex();
            int goalCol = maze.getGoalPosition().getColumnIndex();

            Image goalImage = null;
            try {
                goalImage = new Image(new FileInputStream("./resources/Images/monica.jpg"));
            } catch (FileNotFoundException e) {
                System.out.println("There is no Image goal....");
            }
            graphicsContext.drawImage(goalImage,goalCol * cellWidth,goalRow * cellHeight,cellWidth,cellHeight);

            if (solution != null){
                GraphicsContext graphicsContext2 = getGraphicsContext2D();
//                graphicsContext2.setFill(Color.ORANGE);
                Image solImage = null;
                try {
                    solImage = new Image(new FileInputStream("./resources/Images/coffee.jpg"));
                } catch (FileNotFoundException e) {
                    System.out.println("There is no Image player....");
                }
                ArrayList<AState> solPath = solution.getSolutionPath();
                for (int i = 1; i < solPath.size()-1; i++) {
                    int solRow = ((MazeState) solPath.get(i)).getCurrPosition().getRowIndex();
                    int solCol = ((MazeState) solPath.get(i)).getCurrPosition().getColumnIndex();
                    double solHeight = solRow * cellHeight;
                    double solWidth = solCol * cellWidth;
                    graphicsContext2.drawImage(solImage, solWidth, solHeight, cellWidth, cellHeight);

                }

            }

        }
    }

}
