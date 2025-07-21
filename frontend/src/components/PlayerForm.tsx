import axios from "axios";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

const PlayerForm = () => {
  const navigate = useNavigate();
  const [okToStart, okToStartUpdate] = useState(false);

  const [playerOneName, playerOneNameUpdate] = useState("");
  const [playerTwoName, playerTwoNameUpdate] = useState("");

  const [playerOneCard, playerOneCardUpdate] = useState("Demeter");
  const [playerTwoCard, playerTwoCardUpdate] = useState("Pan");

  const startGame = () => {
    if (playerOneName == "") {
      alert("Player one did not enter name");
    } else if (playerTwoName == "") {
      alert("Player two did not enter name");
    } else if (playerOneCard == playerTwoCard) {
      alert(
        "certain player have duplicate god card, please make sure each selection is different"
      );
    } else {
      var config = {
        method: "get",
        url:
          "http://localhost:8080/initialGame?playerOneName=" +
          playerOneName +
          "&playerTwoName=" +
          playerTwoName +
          "&playerOneCard=" +
          playerOneCard +
          "&playerTwoCard=" +
          playerTwoCard,
        headers: {},
      };

      axios(config)
        .then(function (response) {
          if (response.data == "game ready") {
            navigate("/gameBoard");
          } else {
            alert("Not connected to game board");
          }
          console.log(JSON.stringify(response.data));
        })
        .catch(function (error) {
          console.log(error);
        });
    }
  };

  useEffect(() => {
    if (playerOneCard == playerTwoCard) {
      console.log("triggered here");
      okToStartUpdate(false);
    } else {
      okToStartUpdate(true);
    }
  }, [playerOneCard, playerTwoCard]);

  return (
    <div className="d-flex flex-column">
      <h1 className="mx-auto mb-auto p-3 bd-highlight">
        Welcome to Santorini Game
      </h1>

      <div className="container">
        <div className="row">
          <div className="col-sm">
            <h2>Player One Info</h2>
            <div className="form-group">
              <label>Player Name</label>
              <input
                className="form-control"
                placeholder="please enter your player name"
                value={playerOneName}
                onChange={(e) => {
                  playerOneNameUpdate(e.target.value);
                }}
              ></input>
            </div>

            <div className="form-group">
              <label>Choose First God</label>
              <select
                className="form-control"
                value={playerOneCard}
                onChange={(e) => {
                  playerOneCardUpdate(e.target.value);
                }}
              >
                <option value="Demeter">Demeter</option>
                <option value="Minotaur">Minotaur</option>
                <option value="Pan">Pan</option>
                <option value="Hermes">Hermes</option>
                <option value="Apollo">Apollo</option>
                <option value="Hephaestus">Hephaestus</option>
              </select>
            </div>
          </div>

          <div className="col-sm">
            <h2>Player Two Info</h2>
            <div className="form-group">
              <label>Player Name</label>
              <input
                className="form-control"
                placeholder="please enter your player name"
                value={playerTwoName}
                onChange={(e) => {
                  playerTwoNameUpdate(e.target.value);
                }}
              ></input>
            </div>

            <div className="form-group">
              <label>Choose a God</label>
              <select
                className="form-control"
                value={playerTwoCard}
                onChange={(e) => {
                  playerTwoCardUpdate(e.target.value);
                }}
              >
                <option value="Demeter">Demeter</option>
                <option value="Minotaur">Minotaur</option>
                <option value="Pan">Pan</option>
                <option value="Hermes">Hermes</option>
                <option value="Apollo">Apollo</option>
                <option value="Hephaestus">Hephaestus</option>
              </select>
            </div>
          </div>
        </div>
        <p
          className={"text-warning " + (okToStart ? " invisible" : " visible")}
        >
          certain player have duplicate god card, please make sure each
          selection is different
        </p>
        <button
          onClick={startGame}
          className="btn btn-primary mx-auto mt-5 mb-5 w-8"
        >
          Start Game
        </button>
      </div>
    </div>
  );
};

export default PlayerForm;
