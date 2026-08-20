package Game;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import modelo.Board;
import servicios.MinimaxService;

public class Launcher extends Application {
    private Board gameBoard = new Board();
    private Button[][] buttons = new Button[3][3];
    private char humanSym = 'X', compSym = 'O';
    private boolean isHumanTurn = true;
    private MinimaxService minimax;

    @Override
    public void start(Stage stage) {
        // DIÁLOGOS DE CONFIGURACIÓN
        ChoiceDialog<String> dialog = new ChoiceDialog<>("X", "X", "O");
        dialog.setTitle("Configuración");
        dialog.setHeaderText("Tres en Raya - ESPOL");
        dialog.setContentText("Selecciona tu símbolo:");
        humanSym = dialog.showAndWait().orElse("X").charAt(0);
        compSym = (humanSym == 'X') ? 'O' : 'X';
        
        Alert turnAlert = new Alert(Alert.AlertType.CONFIRMATION, "¿Deseas empezar tú?", ButtonType.YES, ButtonType.NO);
        isHumanTurn = turnAlert.showAndWait().get() == ButtonType.YES;

        minimax = new MinimaxService(compSym, humanSym);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); grid.setVgap(10);
        grid.setStyle("-fx-background-color: #eeeeee; -fx-padding: 20;");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button b = new Button();
                b.setPrefSize(100, 100);
                b.setStyle("-fx-font-size: 2em; -fx-font-weight: bold; -fx-base: #ffffff;");
                int r = i, c = j;
                b.setOnAction(e -> handleMove(r, c));
                buttons[i][j] = b;
                grid.add(b, j, i);
            }
        }

        stage.setScene(new Scene(grid, 420, 420));
        stage.setTitle("Tres en Raya - Inteligencia Artificial");
        stage.show();

        if (!isHumanTurn) executeComputerMove();
    }

    private void handleMove(int r, int c) {
        if (gameBoard.getAt(r, c) == Board.EMPTY && isHumanTurn) {
            applyMove(r, c, humanSym);
            if (!checkGameEnd()) {
                isHumanTurn = false;
                executeComputerMove();
            }
        }
    }

    private void executeComputerMove() {
        Board next = minimax.getBestMove(gameBoard);
        if (next == null) return;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.getAt(i, j) == Board.EMPTY && next.getAt(i, j) != Board.EMPTY) {
                    applyMove(i, j, compSym);
                }
            }
        }
        checkGameEnd();
        isHumanTurn = true;
    }

    private void applyMove(int r, int c, char s) {
        gameBoard.setMove(r, c, s);
        buttons[r][c].setText(String.valueOf(s));
        buttons[r][c].setDisable(true);
        buttons[r][c].setStyle("-fx-font-size: 2em; -fx-font-weight: bold; -fx-text-fill: " + (s == 'X' ? "#0000FF" : "#FF0000") + "; -fx-opacity: 1;");
    }

    private boolean checkGameEnd() {
        char win = gameBoard.checkWinner();
        if (win != Board.EMPTY || gameBoard.isFull()) {
            String msg = (win == Board.EMPTY) ? "¡Es un Empate!" : "¡Ganador: " + win + "!";
            Alert alert = new Alert(Alert.AlertType.INFORMATION, msg);
            alert.setHeaderText("Fin de la partida");
            alert.showAndWait();
            return true;
        }
        return false;
    }

    public static void main(String[] args) { launch(args); }
}