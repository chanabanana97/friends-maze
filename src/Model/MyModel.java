package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import algorithms.mazeGenerators.*;
import algorithms.search.*;
import javafx.scene.input.KeyCode;
import Server.*;
import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Observable;

public class MyModel extends Observable implements IModel{
    private Maze maze;
    private Position currPos;
    private Solution solution;
    private Server generateMazeServer;
    private Server solveMazeServer;

    public MyModel() {
        startServers();
    }

    @Override
    public void generateMaze(int rows, int cols) {

        try {
            Client client = new Client(InetAddress.getLocalHost(), 5402, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        int[] mazeDimensions = new int[]{rows, cols};
                        toServer.writeObject(mazeDimensions);
                        toServer.flush();
                        byte[] compressedMaze = (byte[])((byte[])fromServer.readObject());
                        InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                        byte[] decompressedMaze = new byte[(rows*cols)+12];
                        is.read(decompressedMaze);
                        maze = new Maze(decompressedMaze);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        this.currPos = maze.getStartPosition();

        setChanged();
        notifyObservers();

    }

    @Override
    public void solveMaze() {
        try {

            Client client = new Client(InetAddress.getLocalHost(), 5403, new IClientStrategy() {
                public void clientStrategy(InputStream inFromServer, OutputStream outToServer) {
                    try {
                        ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                        ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                        toServer.flush();
                        maze.setStartPosition(currPos);
                        toServer.writeObject(maze);
                        toServer.flush();
                        solution = (Solution)fromServer.readObject();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        setChanged();
        notifyObservers();

    }

    @Override
    public void moveCharacter(KeyCode movement) {
        int playerRowPos = currPos.getRowIndex();
        int playerColPos = currPos.getColumnIndex();

        switch (movement){
            case UP :
            case NUMPAD8:
                if (playerRowPos > 0 && maze.getMaze()[playerRowPos-1][playerColPos] != 1 )
                    setPlayerPosition(playerRowPos-1,playerColPos);
                break;
            case DOWN:
            case NUMPAD2:
                if (playerRowPos < maze.getRows()-1 && maze.getMaze()[playerRowPos+1][playerColPos] != 1 )
                    setPlayerPosition(playerRowPos+1,playerColPos);
                break;
            case RIGHT:
            case NUMPAD6:
                if (playerColPos < maze.getColumns()-1 && maze.getMaze()[playerRowPos][playerColPos+1] != 1)
                    setPlayerPosition(playerRowPos,playerColPos+1);
                break;
            case LEFT:
            case NUMPAD4:
                if (playerColPos > 0 && maze.getMaze()[playerRowPos][playerColPos-1] != 1 )
                    setPlayerPosition(playerRowPos,playerColPos-1);
                break;
            case NUMPAD7:
                if (playerColPos > 0 && playerRowPos > 0 && maze.getMaze()[playerRowPos-1][playerColPos-1] != 1)
                    setPlayerPosition(playerRowPos-1,playerColPos-1);
                break;
            case NUMPAD9:
                if (playerColPos < maze.getColumns()-1 && playerRowPos > 0 && maze.getMaze()[playerRowPos-1][playerColPos+1] != 1)
                    setPlayerPosition(playerRowPos-1,playerColPos+1);
                break;
            case NUMPAD1:
                if (playerColPos >0 && playerRowPos < maze.getRows() && maze.getMaze()[playerRowPos+1][playerColPos-1] != 1)
                    setPlayerPosition(playerRowPos+1,playerColPos-1);
                break;
            case NUMPAD3:
                if (playerColPos < maze.getColumns()-1 && playerRowPos < maze.getRows()-1 && maze.getMaze()[playerRowPos+1][playerColPos+1] != 1)
                    setPlayerPosition(playerRowPos+1,playerColPos+1);
                break;
            default:
                setPlayerPosition(playerRowPos,playerColPos);
        }

        setChanged();
        notifyObservers();

    }

    private void setPlayerPosition(int row, int col){
        this.currPos.setRowIndex(row);
        this.currPos.setColumnIndex(col);
    }

    @Override
    public Maze getMaze() {
        return this.maze;
    }

    public Solution getSolution() {
        return solution;
    }

    @Override
    public Position getCharacterPosition() {
        return this.currPos;
    }


    @Override
    public void saveMaze(String path) {
        File file = new File(path);
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(maze.toByteArray());
            outputStream.flush();
            outputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void loadMaze(String path) {
        if (path == null)
            return;
        File file = new File(path);
        try {
            ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file));
            byte[] byteArr = (byte[])(inputStream.readObject());
            maze = new Maze(byteArr);
            currPos = maze.getStartPosition();

        } catch (Exception e) {
            e.printStackTrace();
        }
        hasChanged();
        notifyObservers();
    }

    @Override
    public void startServers() {
        generateMazeServer = new Server(5402, 1000, new ServerStrategyGenerateMaze());
        solveMazeServer = new Server(5403, 1000, new ServerStrategySolveSearchProblem());
        generateMazeServer.start();
        solveMazeServer.start();
    }

    @Override
    public void stopServers() {
        generateMazeServer.stop();
        solveMazeServer.stop();

    }
}
