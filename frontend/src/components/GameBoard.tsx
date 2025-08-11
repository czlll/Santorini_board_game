import { useEffect, useRef, useState } from "react";
import Grid from "./Grid";
import GameMessage from "./GameMessage";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { read } from "fs";

const GameBoard = () => {
  const [loadReady, loadReadyUpdate] = useState(false);

  const myRefList = [
    [
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
    ],
    [
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
    ],
    [
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
    ],
    [
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
    ],
    [
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
      useRef<any>(null),
    ],
  ];

  const navigate = useNavigate();

  const [playerOneName, playerOneNameUpdate] = useState("player1");

  const [playerTwoName, playerTwoNameUpdate] = useState("player2");

  const [curPlayer, curPlayerUpdate] = useState(0);
  const [curPlayerAction, curPlayerActionUpdate] = useState(0);

  const [playerOneGod, playerOneGodUpdate] = useState("");
  const [playerTwoGod, playerTwoGodUpdate] = useState("");

  const [curPlayerName, curPlayerNameUpdate] = useState(playerOneName);

  const [gameStatus, gameStatusUpdate] = useState(-3);
  const [gameWarningState, gameWarningStateUpdate] = useState(0);
  const [gameWarningMessage, gameWarningMessageUpdate] = useState("");

  const [readyAxis, readyAxisUpdate] = useState([-1, -1]);
  const [prevAxis, prevAxisUpdate] = useState([-1, -1]);

  useEffect(() => {
    if (!loadReady) {
      loadGame();
    }
  });

  useEffect(() => {
    if (curPlayer == 0) {
      curPlayerNameUpdate(playerOneName);
    } else {
      curPlayerNameUpdate(playerTwoName);
    }
  }, [curPlayer]); // <-- here put the parameter to listen

  useEffect(() => {
    console.log("game status is here", gameStatus);
    if (gameStatus == 2) {
      alert("Winner is " + curPlayerName);
      navigate("/wingame");
    }
  }, [gameStatus]); // <-- here put the parameter to listen

  useEffect(() => {
    console.log("axis is here: ", prevAxis, readyAxis);
    if (prevAxis[0] != -1) {
      myRefList[prevAxis[0]][prevAxis[1]].current.offFocus();
    }

    if (readyAxis[0] != -1) {
      console.log(readyAxis[0], readyAxis[1]);
      myRefList[readyAxis[0]][readyAxis[1]].current.focus();
    }

    prevAxisUpdate(readyAxis);
  }, [readyAxis]); // <-- here put the parameter to listen

  const restartGame = () => {
    alert("Game Restarted!");
    navigate("/playerForm");
  };

  const loadGame = () => {
    var config = {
      method: "get",
      url: "http://localhost:12000/getGameInfo",
      headers: {},
    };
    axios(config)
      .then(function (response: any) {
        loadReadyUpdate(true);
        playerOneNameUpdate(response.data.playerA.name);
        playerTwoNameUpdate(response.data.playerB.name);
        curPlayerNameUpdate(response.data.playerA.name);

        curPlayerUpdate(response.data.curPlayer);
        curPlayerActionUpdate(response.data.curPlayerAction);
        gameStatusUpdate(response.data.gameStatus);

        console.log(
          response.data.curPlayer,
          response.data.curPlayerAction,
          response.data.gameStatus
        );

        prevAxisUpdate([-1, -1]);

        readyAxisUpdate([
          response.data.focusingGridR,
          response.data.focusingGridC,
        ]);

        playerOneGodUpdate(response.data.playerA.godCard);
        playerTwoGodUpdate(response.data.playerB.godCard);
        var grid = response.data.board.grid;

        // clearGrids();

        console.log("It works here in load game")

        var cur_playerId = 0;
        for (var i = 0; i < grid.length; i++) {
          for (var j = 0; j < grid[0].length; j++) {
            if (grid[i][j].worker != null) {
              var cur_workerId = grid[i][j].worker.workerId;
              if (grid[i][j].worker.playerName == response.data.playerB.name) {
                cur_playerId = 1;
              } else {
                cur_playerId = 0;
              }
              myRefList[i][j].current.setCurWorker(cur_playerId, cur_workerId);
              myRefList[i][j].current.setCurWorker(cur_playerId, cur_workerId);
            } else {
              myRefList[i][j].current.resetButton();
            }
            myRefList[i][j].current.setTower(grid[i][j].tower.level);
          }
        }
      })
      .catch(function (error: any) {
        console.log(error);
      });
  };

  // const clearGrids = () =>{
  //   for (var i = 0; i < myRefList.length; i++) {
  //     for (var j = 0; j < myRefList[0].length; j++) {
  //       myRefList[i][j].current.resetButton();
  //     }
  //   }
  //   console.log("clear grid finished");
  // }

  const handleGridClick = (event: any) => {
    console.log("state is here: ", gameStatus, curPlayer, curPlayerAction);
    gameWarningStateUpdate(0);
    gameWarningMessageUpdate("");
    const axis = event.split("-");
    var myRef = myRefList[axis[0]][axis[1]];

    if (gameStatus <= 0) {
      myRef.current.placeCurWorker(
        curPlayer,
        curPlayerAction,
        (result: any) => {
          curPlayerUpdate(result.curPlayer);
          curPlayerActionUpdate(result.curPlayerAction);
          gameStatusUpdate(result.gameStatus);
        },
        (error: any) => {
          gameWarningMessageUpdate(error.response.data);
        }
      );
    } else {
      // console.log("works here");
      if (curPlayerAction == 3 || curPlayerAction == 3.5) {
        var curGridX = axis[0];
        var curGridY = axis[1];

        var prevGridX = readyAxis[0];
        var prevGridY = readyAxis[1];

        var config = {
          method: "get",
          url:
            "http://localhost:12000/moveWorker?curPlayerAction=" +
            curPlayerAction +
            "&row=" +
            prevGridX +
            "&col=" +
            prevGridY +
            "&newRow=" +
            curGridX +
            "&newCol=" +
            curGridY,
          headers: {},
        };

        axios(config)
          .then(function (response) {
            console.log(curGridX == prevGridX && curGridY == prevGridY);
            if (!(curGridX == prevGridX && curGridY == prevGridY)) {
              myRef.current.setCurWorker(
                curPlayer,
                myRefList[prevGridX][prevGridY].current.getCurWorker()
              );
              myRefList[prevGridX][prevGridY].current.setCurWorker(-1, -1);
            }

            curPlayerActionUpdate(response.data.curPlayerAction);
            curPlayerUpdate(response.data.curPlayer);
            gameStatusUpdate(response.data.gameStatus);
            readyAxisUpdate(axis);
            if (
              (curPlayer == 0 &&
                (playerOneGod == "Minotaur" || playerOneGod == "Apollo")) ||
              (curPlayer == 1 &&
                (playerTwoGod == "Minotaur" || playerTwoGod == "Apollo"))
            ) {
              loadGame();
            }
          })
          .catch(function (error) {
            if (curPlayerAction == 3.5) {
              gameWarningMessageUpdate(error.response.data);
            } else {
              curPlayerActionUpdate(curPlayerAction - 1);
              gameWarningMessageUpdate(error.response.data);
              readyAxisUpdate([-1, -1]);
            }

            // console.log(error);
          });
      } else if (curPlayerAction == 5 || curPlayerAction == 5.5) {
        var curGridX = axis[0];
        var curGridY = axis[1];

        var prevGridX = readyAxis[0];
        var prevGridY = readyAxis[1];

        var config = {
          method: "get",
          url:
            "http://localhost:12000/commandBuild?curPlayerAction=" +
            curPlayerAction +
            "&row=" +
            prevGridX +
            "&col=" +
            prevGridY +
            "&newRow=" +
            curGridX +
            "&newCol=" +
            curGridY,
          headers: {},
        };

        axios(config)
          .then(function (response) {
            myRef.current.increaseTower();
            console.log("curplayeraction is here: " + curPlayerAction);
            if (response.data.curPlayerAction == 2) {
              readyAxisUpdate([-1, -1]);
            }

            curPlayerActionUpdate(response.data.curPlayerAction);
            curPlayerUpdate(response.data.curPlayer);
            gameStatusUpdate(response.data.gameStatus);
          })
          .catch(function (error) {
            gameWarningMessageUpdate(error.response.data);
          });
      } else if (curPlayerAction >= 2) {
        if (curPlayer == myRef.current.getCurPlayer()) {
          readyAxisUpdate(axis);
          curPlayerActionUpdate(curPlayerAction + 1);
        } else {
          gameWarningStateUpdate(3);
        }
      }
    }
  };

  const skipAction = () => {
    if (curPlayerAction == 2) {
      gameWarningMessageUpdate("Select worker steps cannot be skipped");
    } else if (gameStatus <= 0) {
      gameWarningMessageUpdate("Initiate Worker Action cannot be skipped");
    } else {
      var config = {
        method: "get",
        url: "http://localhost:12000/skipAction",
        headers: {},
      };

      axios(config)
        .then(function (response) {
          if (response.data.curPlayerAction == 2) {
            readyAxisUpdate([-1, -1]);
          }
          curPlayerUpdate(response.data.curPlayer);
          curPlayerActionUpdate(response.data.curPlayerAction);
          gameStatusUpdate(response.data.gameStatus);
        })
        .catch(function (error) {
          gameWarningMessageUpdate(error.response.data);
        });
    }
  };

  const triggerUndo = () => {
    var config = {
      method: "get",
      url: "http://localhost:12000/triggerUndo",
      headers: {},
    };
    axios(config)
      .then(function (response) {
        readyAxisUpdate([-1, -1]);
        loadGame();
      })
      .catch(function (error) {
        gameWarningMessageUpdate(error.response.data);
      });
  };

  return (
    <div className="d-flex flex-column">
      <h1 className="mx-auto mb-auto p-4 bd-highlight">Game Started</h1>

      <div className="container">
        <div className="row">
          <div className="col-sm">
            <p className="text-danger">
              Player 1 [{playerOneGod}]: {playerOneName}
            </p>
            <p className="text-success">
              Player 2 [{playerTwoGod}]: {playerTwoName}
            </p>
            <button className="btn btn-primary" onClick={restartGame}>
              Restart Game
            </button>
          </div>
          <div className="col-sm">
            <div className="row">
              <div className="col-sm">
                <Grid
                  Ref={myRefList[0][0]}
                  row={0}
                  col={0}
                  onClick={() => handleGridClick("0-0")}
                ></Grid>
                <Grid
                  Ref={myRefList[0][1]}
                  row={0}
                  col={1}
                  onClick={() => handleGridClick("0-1")}
                ></Grid>
                <Grid
                  Ref={myRefList[0][2]}
                  row={0}
                  col={2}
                  onClick={() => handleGridClick("0-2")}
                ></Grid>
                <Grid
                  Ref={myRefList[0][3]}
                  row={0}
                  col={3}
                  onClick={() => handleGridClick("0-3")}
                ></Grid>
                <Grid
                  Ref={myRefList[0][4]}
                  row={0}
                  col={4}
                  onClick={() => handleGridClick("0-4")}
                ></Grid>
              </div>
            </div>

            <div className="row">
              <div className="col-sm">
                <Grid
                  Ref={myRefList[1][0]}
                  row={1}
                  col={0}
                  onClick={() => handleGridClick("1-0")}
                ></Grid>
                <Grid
                  Ref={myRefList[1][1]}
                  row={1}
                  col={1}
                  onClick={() => handleGridClick("1-1")}
                ></Grid>
                <Grid
                  Ref={myRefList[1][2]}
                  row={1}
                  col={2}
                  onClick={() => handleGridClick("1-2")}
                ></Grid>
                <Grid
                  Ref={myRefList[1][3]}
                  row={1}
                  col={3}
                  onClick={() => handleGridClick("1-3")}
                ></Grid>
                <Grid
                  Ref={myRefList[1][4]}
                  row={1}
                  col={4}
                  onClick={() => handleGridClick("1-4")}
                ></Grid>
              </div>
            </div>

            <div className="row">
              <div className="col-sm">
                <Grid
                  Ref={myRefList[2][0]}
                  row={2}
                  col={0}
                  onClick={() => handleGridClick("2-0")}
                ></Grid>
                <Grid
                  Ref={myRefList[2][1]}
                  row={2}
                  col={1}
                  onClick={() => handleGridClick("2-1")}
                ></Grid>
                <Grid
                  Ref={myRefList[2][2]}
                  row={2}
                  col={2}
                  onClick={() => handleGridClick("2-2")}
                ></Grid>
                <Grid
                  Ref={myRefList[2][3]}
                  row={2}
                  col={3}
                  onClick={() => handleGridClick("2-3")}
                ></Grid>
                <Grid
                  Ref={myRefList[2][4]}
                  row={2}
                  col={4}
                  onClick={() => handleGridClick("2-4")}
                ></Grid>
              </div>
            </div>

            <div className="row">
              <div className="col-sm">
                <Grid
                  Ref={myRefList[3][0]}
                  row={3}
                  col={0}
                  onClick={() => handleGridClick("3-0")}
                ></Grid>
                <Grid
                  Ref={myRefList[3][1]}
                  row={3}
                  col={1}
                  onClick={() => handleGridClick("3-1")}
                ></Grid>
                <Grid
                  Ref={myRefList[3][2]}
                  row={3}
                  col={2}
                  onClick={() => handleGridClick("3-2")}
                ></Grid>
                <Grid
                  Ref={myRefList[3][3]}
                  row={3}
                  col={3}
                  onClick={() => handleGridClick("3-3")}
                ></Grid>
                <Grid
                  Ref={myRefList[3][4]}
                  row={3}
                  col={4}
                  onClick={() => handleGridClick("3-4")}
                ></Grid>
              </div>
            </div>

            <div className="row">
              <div className="col-sm">
                <Grid
                  Ref={myRefList[4][0]}
                  row={4}
                  col={0}
                  onClick={() => handleGridClick("4-0")}
                ></Grid>
                <Grid
                  Ref={myRefList[4][1]}
                  row={4}
                  col={1}
                  onClick={() => handleGridClick("4-1")}
                ></Grid>
                <Grid
                  Ref={myRefList[4][2]}
                  row={4}
                  col={2}
                  onClick={() => handleGridClick("4-2")}
                ></Grid>
                <Grid
                  Ref={myRefList[4][3]}
                  row={4}
                  col={3}
                  onClick={() => handleGridClick("4-3")}
                ></Grid>
                <Grid
                  Ref={myRefList[4][4]}
                  row={4}
                  col={4}
                  onClick={() => handleGridClick("4-4")}
                ></Grid>
              </div>
            </div>

            <div className="col-sm">
              <button className="btn btn-warning mt-3" onClick={skipAction}>
                Skip Current Action
              </button>

              <button
                className="btn btn-warning mt-3 ml-3"
                onClick={triggerUndo}
              >
                Undo
              </button>
            </div>
          </div>
          <div className="col-sm">
            <GameMessage
              curPlayer={curPlayer}
              curPlayerAction={curPlayerAction}
              curPlayerName={curPlayerName}
              gameWarningState={gameWarningState}
              gameWarningMessage={gameWarningMessage}
            ></GameMessage>
          </div>
        </div>
      </div>
    </div>
  );
};

export default GameBoard;
