package edu.cmu.cs214.hw3.game;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import edu.cmu.cs214.hw3.game.action.build.BuildAction;
import edu.cmu.cs214.hw3.game.action.build.DemeterBuild;
import edu.cmu.cs214.hw3.game.action.build.HephaestusBuild;
import edu.cmu.cs214.hw3.game.action.build.NormalBuild;
import edu.cmu.cs214.hw3.game.action.move.*;
import edu.cmu.cs214.hw3.game.action.wincheck.NormalWin;
import edu.cmu.cs214.hw3.game.action.wincheck.PanWin;
import edu.cmu.cs214.hw3.game.action.wincheck.WinCheck;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.PrintStream;
import java.util.EmptyStackException;
import java.util.Stack;

public class Game {
    private Board board;
    private Player playerA;
    private Player playerB;

//    action list for three major action: move, build and checkwin
    private transient final BuildAction[] buildActions = new BuildAction[]{new NormalBuild(), new NormalBuild()};
    private transient final MoveAction[] moveActions = new MoveAction[]{new NormalMove(), new NormalMove()};
    private transient final WinCheck[] winChecks = new WinCheck[]{new NormalWin(), new NormalWin()};

    /**
     * Store game status for undo
     * I'm originally thinking about using command pattern,
     * but it end-up will introduce too much coupling and change my code structure a lot,
     * so I decided using a simple stack.
     */
    private transient Stack<String> history = new Stack<>();
    private transient Gson gson = new Gson();

    /**
     * determine if game is in
     * [-1]: worker placement section
     * [1]: game start section
     * [2]: game finished section
      */
    private int gameStatus;

//    determine the current player
//    [0]: playerA
//
    /**
     * determine the current player
     * [0]: playerA
     * [1]: playerB
     */
    private int curPlayer;

//    determine the current action
    /**
     * determine the current action
     * [0]: workerA placement
     * [1]: workerB placement
     * [2]: selecting worker to move / build
     * [3]: moving worker
     * [5]: buliding worker
     */
    private double curPlayerAction;

    private int focusingGridR = -1;
    private int focusingGridC = -1;

//    initialize game state and godcard action
    public Game(Board board, Player playerA, Player playerB) throws Exception {
        if (playerA.getGodCard() != null && playerB.getGodCard() != null){
            if (playerA.getGodCard().equals(playerB.getGodCard())){
                throw new Exception("Both player cannot have the same god card");
            }

            //        initialize god card action for player A
            switch (playerA.getGodCard()){
                case Demeter -> {
                    buildActions[0] = new DemeterBuild();
                }
                case Minotaur -> {
                    moveActions[0] = new MinotaurMove();
                }
                case Pan -> {
                    PanMove panMove = new PanMove();
                    PanWin panWin = new PanWin();
                    panMove.setPanWin(panWin);
                    moveActions[0] = panMove;
                    winChecks[0] = panWin;

                }
                case Hermes -> {
                    moveActions[0] = new HermesMove();
                }
                case Apollo -> {
                    moveActions[0] = new ApolloMove();
                }
                case Hephaestus -> {
                    buildActions[0] = new HephaestusBuild();
                }
            }

//        initialize god card action for player B
            switch (playerB.getGodCard()){
                case Demeter -> {
                    buildActions[1] = new DemeterBuild();
                }
                case Minotaur -> {
                    moveActions[1] = new MinotaurMove();
                }
                case Pan -> {
                    PanMove panMove = new PanMove();
                    PanWin panWin = new PanWin();
                    panMove.setPanWin(panWin);
                    moveActions[1] = panMove;
                    winChecks[1] = panWin;
                }
                case Hermes -> {
                    moveActions[1] = new HermesMove();
                }
                case Apollo -> {
                    moveActions[1] = new ApolloMove();
                }
                case Hephaestus -> {
                    buildActions[1] = new HephaestusBuild();
                }
            }
        }



//        Initialize other variable
        this.board = board;
        this.playerA = playerA;
        this.playerB = playerB;

        this.gameStatus = -1;
    }

    /**
     * initialize both worker for the player
     * @param playerIndicator indicate if this is player a or player b
     * @param workerIndicator indciate which worker to place
     * @param r worker location row axis
     * @param c worker location col axis
     * @throws Exception excpetion for worker placement
     */
    public void placeWorker(int playerIndicator, int workerIndicator, int r, int c) throws Exception{
        Worker curWorker;
//        prevent user from access game after the game ends
        if (gameStatus == 2){
            throw new Exception("game has ended");
        }

//        indicate which worker to place under which player
        if (playerIndicator == 0){
            curWorker = new Worker(playerA.getName());
            curWorker.setWorkerId(workerIndicator);

            board.setWorker(curWorker, r, c);

            if (workerIndicator == 0){
                playerA.setWorkerA(curWorker);
            } else {
                playerA.setWorkerB(curWorker);
            }
        } else{
            curWorker = new Worker(playerB.getName());
            curWorker.setWorkerId(workerIndicator);

            board.setWorker(curWorker, r, c);

            if (workerIndicator == 0){
                playerB.setWorkerA(curWorker);
            } else {
                playerB.setWorkerB(curWorker);
            }
        }
    }

    /**
     * deciding which worker to place, and update state after a worker is placed
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @throws Exception exception for worker placement
     */
    public void placeWorkerAuto(int r, int c) throws Exception {
        history.add(gson.toJson(this));
//        the algorithm fill the null worker first
        if (playerA.getWorkerA() == null){
            placeWorker(0, 0, r, c);
            this.curPlayer = 0;
            this.curPlayerAction = 1;
        } else if (playerA.getWorkerB() == null){
            placeWorker(0, 1, r, c);
            this.curPlayer = 1;
            this.curPlayerAction = 0;
        } else if (playerB.getWorkerA() == null){
            placeWorker(1, 0, r, c);
            this.curPlayer = 1;
            this.curPlayerAction = 1;
        } else if (playerB.getWorkerB() == null){
            placeWorker(1, 1, r, c);
//            after every worker is filled, game starts
            this.curPlayer = 0;
            this.curPlayerAction = 2;
            this.gameStatus = 1;
        }
    }

    /**
     * move worker to destined location.
     * Preconditions:
     * 1. the current turn is the worker's turn.
     * 2. no worker already in the destinated location.
     * 3. height is no more than one level up, but can be any level down.
     * 
     * Postcondition:
     * 1. Update the status of the worker.
     * 2. If the tower is of level 3, indicate the player wins.
     * 3. throw exceptions is handled in APP later.
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @param newR row axis of new location
     * @param newC col axis of new location
     * @param playerName player name to check if the worker belongs to player
     * @throws Exception
     */
    public void moveWorker(int r, int c, int newR, int newC, String playerName) throws Exception{
        if (gameStatus==2){
            throw new Exception("game has ended");
        }

//        get move action and validate if the move is possible
        MoveAction curMoveAction = moveActions[curPlayer];

        curMoveAction.validateMove(r, c, newR, newC, this.board, playerName);
        curMoveAction.performMove(board, r, c, newR, newC);

//        also check win condition with given win action
        WinCheck curWinAction = winChecks[curPlayer];
        boolean winCheckResult = curWinAction.validateWin(newR, newC, this.board);

//        check if the game continues or termintates
        if (winCheckResult){
            gameStatus = 2;
        } else {
            curPlayerAction = curMoveAction.nextAction(curPlayerAction);
            focusingGridR = newR;
            focusingGridC = newC;
        }
    }

    /**
     * helper for triggering move worker
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @param newR row axis of new location
     * @param newC col axis of new location
     * @throws Exception
     */
    public void moveWorkerAuto(int r, int c, int newR, int newC) throws Exception {
        history.add(gson.toJson(this));
        if (curPlayer == 0){
            moveWorker(r, c, newR, newC, playerA.getName());
        } else {
            moveWorker(r, c, newR, newC, playerB.getName());
        }
    }

    /**
     * build tower on specific location with specific worker
     * Preconditions:
     * 1. the current turn is the woker's turn.
     * 2. no worker already in the destinated location.
     * 3. Dome can be build on level three tower
     * 
     * Postcondition:
     * 1. throw exceptions will be handled in APP later.
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @param newR row axis of new location
     * @param newC col axis of new location
     * @param playerName player name to check if the worker belongs to player
     * @param isDome indicate if build dome or not
     * @throws Exception
     */
    public void build(int r, int c, int newR, int newC, String playerName, boolean isDome) throws Exception {
        if (gameStatus==2){
            throw new Exception("game has ended");
        }

        BuildAction curBuildAction = buildActions[curPlayer];

        curBuildAction.validateBuild(r, c, newR, newC, playerName, this.board, isDome);
        curBuildAction.performBuild(board, r, c, newR, newC, isDome);

        curPlayerAction = curBuildAction.nextAction(curPlayerAction);
        curPlayer = curBuildAction.nextPlayer(curPlayerAction, curPlayer);
        focusingGridR = -1;
        focusingGridC = -1;

    }

    /**
     * helper for triggering build function
     * @param r row axis of the worker
     * @param c col axis of the worker
     * @param newR row axis of new location
     * @param newC col axis of new location
     * @param isDome indicate if build dome or not
     * @throws Exception
     */
    public void buildAuto(int r, int c, int newR, int newC, boolean isDome) throws Exception {
        history.add(gson.toJson(this));
        if (curPlayer == 0){
            build(r, c, newR, newC, playerA.getName(), isDome);
        } else {
            build(r, c, newR, newC, playerB.getName(), isDome);
        }
    }

    /**
     * skip the next action, the actual skip is decided by each action in action list
     */
    public void skipAction() throws Exception {
        if (curPlayerAction == 5 || curPlayerAction == 5.5){
            history.add(gson.toJson(this));
            curPlayerAction = buildActions[curPlayer].skipBuildAction(curPlayerAction);
            curPlayer = buildActions[curPlayer].nextPlayer(curPlayerAction, curPlayer);
            focusingGridR = -1;
            focusingGridC = -1;
        } else {
            throw new Exception("You cannot skip other action except build");
        }
    }


    /**
     * skip the next action, for test purposes only, will not be exposed to Restful API
     * This will be deleted for production version of this software (if there is one)
     */
    public void skipActionAdmin() throws Exception {
        if (gameStatus == 2){
            throw new Exception("game has ended");
        }
        int prevPlayer = curPlayer;
        double prevCurPlayerAction = curPlayerAction;
        curPlayerAction = buildActions[curPlayer].skipBuildAction(curPlayerAction);
        if (prevCurPlayerAction != curPlayerAction){
            curPlayer = buildActions[curPlayer].nextPlayer(curPlayerAction, curPlayer);
        }
        if (prevPlayer == curPlayer){
            curPlayerAction = moveActions[curPlayer].skipAction(curPlayerAction);
        } else {
            focusingGridR = -1;
            focusingGridC = -1;
        }
    }


    public void undo() throws Exception {
        try{
            JSONObject prevGameState = new JSONObject(history.pop());
            System.out.println(prevGameState);
            JSONObject boardJson = prevGameState.getJSONObject("board");

            this.board = gson.fromJson(String.valueOf(boardJson), Board.class);
            this.curPlayer = prevGameState.getInt("curPlayer");
            this.curPlayerAction = prevGameState.getInt("curPlayerAction");
            if (curPlayerAction == 3.0){
                curPlayerAction = 2.0;
            }
            this.gameStatus = prevGameState.getInt("gameStatus");

            this.focusingGridR = prevGameState.getInt("focusingGridR");
            this.focusingGridC = prevGameState.getInt("focusingGridC");
            this.playerA = gson.fromJson(String.valueOf(prevGameState.get("playerA")), Player.class);
            this.playerB = gson.fromJson(String.valueOf(prevGameState.get("playerB")), Player.class);
            System.out.println(this.toString());

        } catch (EmptyStackException e){
            throw new Exception("cannot undo further because this is already the start of the game");
        }


    }


    /**
     * getter & setter are as follow
     */
    public Player getPlayerA(){
        return playerA;
    }

    public Player getPlayerB(){
        return playerB;
    }


    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(int gameStatus) {
        this.gameStatus = gameStatus;
    }

    public int getCurPlayer() {
        return curPlayer;
    }

    public void setCurPlayer(int curPlayer) {
        this.curPlayer = curPlayer;
    }

    public double getCurPlayerAction() {
        return curPlayerAction;
    }

    public void setCurPlayerAction(double curPlayerAction) {
        this.curPlayerAction = curPlayerAction;
    }

    public int getAboutToBuildX() {
        return focusingGridR;
    }

    public void setAboutToBuildX(int focusingGridR) {
        this.focusingGridR = focusingGridR;
    }

    public int getFocusingGridC() {
        return focusingGridC;
    }

    public void setFocusingGridC(int focusingGridC) {
        this.focusingGridC = focusingGridC;
    }

    @Override
    public String toString() {
        return "Game{" +
                "gameStatus=" + gameStatus +
                ", curPlayer=" + curPlayer +
                ", curPlayerAction=" + curPlayerAction +
                ", focusingGridR=" + focusingGridR +
                ", focusingGridC=" + focusingGridC +
                '}';
    }
}
