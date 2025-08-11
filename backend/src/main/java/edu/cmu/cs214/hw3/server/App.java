package edu.cmu.cs214.hw3.server;


import com.google.gson.Gson;
import edu.cmu.cs214.hw3.game.Board;
import edu.cmu.cs214.hw3.game.Game;
import edu.cmu.cs214.hw3.game.GodCard;
import edu.cmu.cs214.hw3.game.Player;
import fi.iki.elonen.NanoHTTPD;
import fi.iki.elonen.router.RouterNanoHTTPD;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Hello world!
 *
 */
public class App extends RouterNanoHTTPD {
//    game state
    Game newGame;

    /**
     * start service for the game
     * @param port port number for the service
     * @throws IOException
     */
    public App(int port) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
    }

//    start service with port 8080
    public static void main(String[] args) throws IOException {
        new App(12000);
    }

    @Override
    public Response serve(IHTTPSession session) {
//        check for which route client is requesting
        String url = session.getUri();
        switch (url) {
//            default routing, serve frontend
            case "/" -> {
                return serveStaticFile("index.html");
            }
            
//            API endpoint for connection check
            case "/api/health" -> {
                Response response1 = newFixedLengthResponse("Hello world");
                response1.addHeader("Access-Control-Allow-Origin", "*");
                return response1;
            }

//            initiate the game with input variable
            case "/initialGame" -> {
                String playerOneName = session.getParameters().get("playerOneName").get(0);
                String playerTwoName = session.getParameters().get("playerTwoName").get(0);
                GodCard playerOneCard = GodCard.valueOf(session.getParameters().get("playerOneCard").get(0));
                GodCard playerTwoCard = GodCard.valueOf(session.getParameters().get("playerTwoCard").get(0));
                Player playerOne = new Player(playerOneName);
                playerOne.setGodCard(playerOneCard);
                Player playerTwo = new Player(playerTwoName);
                playerTwo.setGodCard(playerTwoCard);
                try {
                    newGame = new Game(new Board(), playerOne, playerTwo);
                } catch (Exception e) {
                    return handleException(e);
                }
                Response response2 = newFixedLengthResponse("game ready");
                response2.addHeader("Access-Control-Allow-Origin", "*");
                return response2;
            }

//            get game info, so that even when refreshed, the game can still be accessed later
            case "/getGameInfo" -> {
                Gson gson = new Gson();
                Response response3 = newFixedLengthResponse(gson.toJson(newGame));
                response3.addHeader("Access-Control-Allow-Origin", "*");
                return response3;
            }

//            place worker on specific location
            case "/placeWorker" -> {
                int row = Integer.parseInt(session.getParameters().get("row").get(0));
                int col = Integer.parseInt(session.getParameters().get("col").get(0));
                try {
                    newGame.placeWorkerAuto(row, col);
                    JSONObject returnResult = new JSONObject();
                    returnResult.put("gameStatus", newGame.getGameStatus());
                    returnResult.put("curPlayer", newGame.getCurPlayer());
                    returnResult.put("curPlayerAction", newGame.getCurPlayerAction());
                    Response response4 = newFixedLengthResponse(returnResult.toString());
                    response4.addHeader("Access-Control-Allow-Origin", "*");
                    return response4;
                } catch (Exception e) {
                    return handleException(e);
                }
            }

//            handle move worker to another location
            case "/moveWorker" -> {
                int row = Integer.parseInt(session.getParameters().get("row").get(0));
                int col = Integer.parseInt(session.getParameters().get("col").get(0));
                int newRow = Integer.parseInt(session.getParameters().get("newRow").get(0));
                int newCol = Integer.parseInt(session.getParameters().get("newCol").get(0));
                double curPlayerAction = Double.parseDouble(session.getParameters().get("curPlayerAction").get(0));
                double originalAction = newGame.getCurPlayerAction();
                newGame.setCurPlayerAction(curPlayerAction);
                try {
                    newGame.moveWorkerAuto(row, col, newRow, newCol);

                    JSONObject returnResult = new JSONObject();
                    returnResult.put("gameStatus", newGame.getGameStatus());
                    returnResult.put("curPlayer", newGame.getCurPlayer());
                    returnResult.put("curPlayerAction", newGame.getCurPlayerAction());
                    Response response5 = newFixedLengthResponse(returnResult.toString());
                    response5.addHeader("Access-Control-Allow-Origin", "*");
                    return response5;
                } catch (Exception e) {
                    newGame.setCurPlayerAction(originalAction);
                    return handleException(e);
                }
            }

//            handle build command
            case "/commandBuild" -> {
                int row = Integer.parseInt(session.getParameters().get("row").get(0));
                int col = Integer.parseInt(session.getParameters().get("col").get(0));
                int newRow = Integer.parseInt(session.getParameters().get("newRow").get(0));
                int newCol = Integer.parseInt(session.getParameters().get("newCol").get(0));
                double curPlayerAction = Double.parseDouble(session.getParameters().get("curPlayerAction").get(0));
                double originalAction = newGame.getCurPlayerAction();
                newGame.setCurPlayerAction(curPlayerAction);
                try {
                    newGame.buildAuto(row, col, newRow, newCol, false);
                    JSONObject returnResult = new JSONObject();
                    returnResult.put("gameStatus", newGame.getGameStatus());
                    returnResult.put("curPlayer", newGame.getCurPlayer());
                    returnResult.put("curPlayerAction", newGame.getCurPlayerAction());
                    Response response5 = newFixedLengthResponse(returnResult.toString());
                    response5.addHeader("Access-Control-Allow-Origin", "*");
                    return response5;
                } catch (Exception e) {
                    newGame.setCurPlayerAction(originalAction);
                    return handleException(e);
                }
            }

//            handle skip action
            case "/skipAction" -> {
                try {
                    newGame.skipAction();
                } catch (Exception e) {
                    return handleException(e);
                }
                JSONObject returnResult = new JSONObject();
                returnResult.put("gameStatus", newGame.getGameStatus());
                returnResult.put("curPlayer", newGame.getCurPlayer());
                returnResult.put("curPlayerAction", newGame.getCurPlayerAction());
                Response response6 = newFixedLengthResponse(returnResult.toString());
                response6.addHeader("Access-Control-Allow-Origin", "*");
                return response6;
            }

//            trigger and handle undo
            case "/triggerUndo" -> {
                try {
                    newGame.undo();
                    Response response7 = newFixedLengthResponse("undo success");
                    response7.addHeader("Access-Control-Allow-Origin", "*");
                    return response7;
                } catch (Exception e) {
                    return handleException(e);
                }

            }
        }

        // Try to serve static files
        if (url.startsWith("/static/")) {
            return serveStaticFile(url.substring(1)); // Remove leading slash
        }

        // For React Router, serve index.html for unknown routes
        return serveStaticFile("index.html");
    }

    /**
     * Serve static files from resources
     */
    private Response serveStaticFile(String filename) {
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static/" + filename);
            if (inputStream == null) {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "File not found");
            }
            
            String mimeType = getMimeType(filename);
            Response response = newFixedLengthResponse(Response.Status.OK, mimeType, inputStream, inputStream.available());
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        } catch (IOException e) {
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, "Error serving file: " + e.getMessage());
        }
    }

    /**
     * Get MIME type for file
     */
    private String getMimeType(String filename) {
        if (filename.endsWith(".html")) return "text/html";
        if (filename.endsWith(".css")) return "text/css";
        if (filename.endsWith(".js")) return "application/javascript";
        if (filename.endsWith(".png")) return "image/png";
        if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) return "image/jpeg";
        if (filename.endsWith(".gif")) return "image/gif";
        if (filename.endsWith(".svg")) return "image/svg+xml";
        if (filename.endsWith(".ico")) return "image/x-icon";
        if (filename.endsWith(".json")) return "application/json";
        return "application/octet-stream";
    }

    /**
     * return exception message when a in game exception is triggered
     * @param e exception
     * @return
     */
    private Response handleException(Exception e){
        Response response = newFixedLengthResponse(Response.Status.CONFLICT, MIME_PLAINTEXT, e.getMessage());
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }
}
