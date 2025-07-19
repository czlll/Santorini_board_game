import React, {
  FC,
  MouseEventHandler,
  useEffect,
  useImperativeHandle,
  useState,
} from "react";
import "./css/grid.css";
import axios from "axios";

interface Props {
  Ref: any;
  onClick?: MouseEventHandler;
  row: number;
  col: number;
}

const Grid: FC<Props> = ({ Ref, row, col, onClick }) => {
  const [curGridPlayer, curGridPlayerUpdate] = useState(-1);
  const [curGridWorker, curGridWorkerUpdate] = useState(-1);
  const [curGridTower, curGridTowerLevelUpdate] = useState(0);

  const [curButtonColor, curButtonColorUpdate] = useState("btn-primary");

  const [rounded, roundedUpdate] = useState("");

  useEffect(() => {
    if (curGridPlayer == 0) {
      curButtonColorUpdate("btn-danger");
    } else if (curGridPlayer == 1) {
      curButtonColorUpdate("btn-success");
    } else {
      curButtonColorUpdate("btn-primary");
    }
  }, [curGridPlayer]); // <-- here put the parameter to listen

  useImperativeHandle(Ref, () => ({
    getAlert() {
      alert("getAlert from Child");
    },

    getState() {
      return [curGridPlayer, curGridWorker, curGridTower];
    },

    // getter / setter for player
    getCurPlayer() {
      return curGridPlayer;
    },

    placeCurWorker(
      player: number,
      worker: number,
      callback: any,
      callbackError: any
    ) {
      var config = {
        method: "get",
        url: "https://work-1-ohkufjawidydjnky.prod-runtime.all-hands.dev/placeWorker?row=" + row + "&col=" + col,
        headers: {},
      };

      axios(config)
        .then(function (response) {
          curGridPlayerUpdate(player);
          curGridWorkerUpdate(worker);
          callback(response.data);
        })
        .catch(function (error) {
          callbackError(error);
        });
    },

    // getter / setter for worker
    getCurWorker() {
      return curGridWorker;
    },

    setCurWorker(player: number, worker: number) {
      curGridPlayerUpdate(player);
      curGridWorkerUpdate(worker);
    },

    changeButtonColor(color: string) {
      console.log("works here in change color");
      curButtonColorUpdate(color);
    },

    focus() {
      roundedUpdate("rounded-circle");
    },

    offFocus() {
      roundedUpdate("");
    },

    increaseTower() {
      curGridTowerLevelUpdate(curGridTower + 1);
    },

    setTower(tower: number) {
      curGridTowerLevelUpdate(tower);
    },

    resetButton(){
      curGridPlayerUpdate(-1);
      curGridWorkerUpdate(-1);
      curGridTowerLevelUpdate(0);
      roundedUpdate("");
      curButtonColorUpdate("btn-primary");
    }
  }));

  return (
    <button
      onClick={onClick}
      className={
        "btn " + curButtonColor + " p-4 mr-1 mt-1 save_button " + rounded
      }
    >
      {curGridTower == 0 ? "" : curGridTower == 4 ? "D" : curGridTower}
    </button>
  );
};

export default Grid;
