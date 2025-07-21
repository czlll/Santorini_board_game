package edu.cmu.cs214.hw3.game;

import java.util.Arrays;

public class Board {
    private static final int ROW = 5;
    private static final int COL = 5;
    private Cell[][] grid;
    
    /**
     * Get the size of the board
     * @return the size of the board (number of rows/columns)
     */
    public int getSize() {
        return ROW;
    }

    public Board(){
        grid = new Cell[5][5];
        for (int i=0; i < ROW; i++){
            for (int j=0; j < COL; j++){
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    /**
     * To place a worker at specific location on the board
     *
     * @param worker the initial worker on the specified cell
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @throws Exception indicate if the worker can be placed or not
     */
    public void setWorker(Worker worker, int r, int c) throws Exception {
        if (!grid[r][c].hasWorker()){
            grid[r][c].setWorker(worker);
        } else {
            throw new Exception("Cannot place worker because there is already another worker");
        }
    }

    public boolean hasWorker(int r, int c){
        return grid[r][c].hasWorker();
    }

    public boolean nameMatch(int r, int c, String playerName){
        return grid[r][c].getWorker().getPlayerName().equals(playerName);
    }

    public int getTowerLevel(int r, int c){
        return grid[r][c].getTowerLevel();
    }


//    public String


    /**
     * To move the worker to a specific location on board
     *
     * @param r row axis of the original location
     * @param c col axis of the original location
     * @param newR row axis of new location
     * @param newC col axis of new location
     */
    public void moveWorker(int r, int c, int newR, int newC) {
//        update new location grid with original location worker
        grid[newR][newC].setWorker(grid[r][c].getWorker());
        grid[r][c].setWorker(null);
    }

    public void swapWorker(int r, int c, int newR, int newC){
        Worker oldWorker = grid[newR][newC].getWorker();
        grid[newR][newC].setWorker(grid[r][c].getWorker());
        grid[r][c].setWorker(oldWorker);
    }

    /**
     * build a tower in destined locateion
     * @param r row axis of current location
     * @param c col axis of current location
     * @param newR row axis of new location
     * @param newC col axis of new location
     * @param isDome indicate if the about-to-build tower level is dome or not
     * @throws Exception multiple exception indicating the special cases
     */
    public void build(int r, int c, int newR, int newC, boolean isDome) throws Exception {
        grid[newR][newC].build(isDome);
    }

    public Cell getCell(int r, int c){
        return grid[r][c];
    }

    public String getCellWorkerName(int r, int c){
        if (grid[r][c].hasWorker()){
            return grid[r][c].getWorker().getPlayerName();
        } else {
            return "";
        }
    }

    @Override
    public String toString() {
        return "Board{" +
                "grid=" + Arrays.toString(grid) +
                '}';
    }
}
