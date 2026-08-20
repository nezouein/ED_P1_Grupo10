
package Game;

import javafx.animation.PauseTransition;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import modelo.Board;
import servicios.ComputerVsComputerService;
import servicios.MinimaxService;

public class Launcher extends Application {

    private Stage mainStage;
    private Board gameBoard;
    private Button[][] buttons;
    private Label statusLabel;

    private char humanSym;
    private char compSym;
    private char currentHumanPlayer;

    private boolean isHumanTurn;
    private boolean computerVsComputer;
    private boolean humanVsHuman;
    private boolean gameFinished;

    private Board previousBoard;
    private boolean canUndo = false;
    private Button undoButton;

    private MinimaxService minimax;
    private ComputerVsComputerService computerGame;

    @Override
    public void start(Stage stage) {
        this.mainStage = stage;
        showMenu();
    }

    private void showMenu() {
        gameFinished = true;

        Label title = new Label("Tres en Raya");
        title.setStyle("-fx-font-size: 30px; -fx-font-weight: bold;");

        Label subtitle = new Label("Selecciona el modo de juego");
        subtitle.setStyle("-fx-font-size: 16px;");

        Button humanComputer = new Button("Humano vs Computadora");
        Button computerComputer = new Button("Computadora vs Computadora");
        Button humanHuman = new Button("Humano vs Humano");

        humanComputer.setPrefSize(280, 50);
        computerComputer.setPrefSize(280, 50);
        humanHuman.setPrefSize(280, 50);

        humanComputer.setOnAction(e -> startHumanComputer());
        computerComputer.setOnAction(e -> startComputerComputer());
        humanHuman.setOnAction(e -> startHumanHuman());

        VBox menu = new VBox(18);
        menu.setAlignment(Pos.CENTER);
        menu.getChildren().addAll(title, subtitle, humanComputer, computerComputer, humanHuman);

        Scene scene = new Scene(menu, 450, 550);
        mainStage.setTitle("Tres en Raya");
        mainStage.setScene(scene);
        mainStage.show();
    }

    private void startHumanComputer() {
        gameFinished = false;
        computerVsComputer = false;
        humanVsHuman = false;

        selectHumanConfiguration();
        gameBoard = new Board();
        minimax = new MinimaxService(compSym, humanSym);

        createGameScene();

        if (!isHumanTurn) {
            executeComputerMove();
        }
    }

    private void startComputerComputer() {
        gameFinished = false;
        computerVsComputer = true;
        humanVsHuman = false;

        gameBoard = new Board();
        computerGame = new ComputerVsComputerService('X', 'O');

        createGameScene();
        executeComputerGame();
    }

    private void startHumanHuman() {
        gameFinished = false;
        computerVsComputer = false;
        humanVsHuman = true;

        gameBoard = new Board();
        currentHumanPlayer = 'X';

        createGameScene();
        updateStatus();
    }

    private void selectHumanConfiguration() {
        ChoiceDialog<String> symbolDialog = new ChoiceDialog<>("X", "X", "O");
        symbolDialog.setTitle("Configuración");
        symbolDialog.setHeaderText("Selecciona tu símbolo");
        symbolDialog.setContentText("Símbolo:");

        String selectedSymbol = symbolDialog.showAndWait().orElse("X");
        humanSym = selectedSymbol.charAt(0);
        compSym = (humanSym == 'X') ? 'O' : 'X';

        Alert turnAlert = new Alert(Alert.AlertType.CONFIRMATION);
        turnAlert.setTitle("Configuración");
        turnAlert.setHeaderText("¿Quién comienza?");
        turnAlert.setContentText("Selecciona quién realizará el primer movimiento.");

        ButtonType humanButton = new ButtonType("Humano");
        ButtonType computerButton = new ButtonType("Computadora");
        turnAlert.getButtonTypes().setAll(humanButton, computerButton);

        isHumanTurn = turnAlert.showAndWait().orElse(humanButton) == humanButton;
    }

    private void createGameScene() {
        buttons = new Button[3][3];

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-padding: 20;");

        statusLabel = new Label();
        statusLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        Button menuButton = new Button("Volver al menú");
        menuButton.setPrefSize(180, 40);
        menuButton.setOnAction(e -> showMenu());
        
        undoButton = new Button("Deshacer");
        undoButton.setPrefSize(180, 40);
        undoButton.setDisable(true);
        undoButton.setOnAction(e -> undoMove());

        root.getChildren().addAll(statusLabel, createGrid(), menuButton, undoButton);

        Scene scene = new Scene(root, 450, 600);
        mainStage.setScene(scene);
        mainStage.show();

        updateStatus();
    }

    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(8);
        grid.setVgap(8);
        grid.setStyle("-fx-background-color: #eeeeee; -fx-padding: 20;");

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                Button button = new Button();
                button.setPrefSize(110, 110);
                button.setStyle("-fx-font-size: 32px; -fx-font-weight: bold;");

                final int row = i;
                final int col = j;
                button.setOnAction(e -> handleMove(row, col));

                buttons[i][j] = button;
                grid.add(button, col, i);
            }
        }
        return grid;
    }

    private void handleMove(int row, int column) {
        if (gameFinished || computerVsComputer) return;
        if (gameBoard.getAt(row, column) != Board.EMPTY) return;

        if (humanVsHuman) {
            applyMove(row, column, currentHumanPlayer);
            if (checkGameEnd()) return;

            currentHumanPlayer = (currentHumanPlayer == 'X') ? 'O' : 'X';
            updateStatus();
            return;
        }

        if (!isHumanTurn) return;

        previousBoard = new Board(gameBoard);
        
        applyMove(row, column, humanSym);
        if (checkGameEnd()) return;

        canUndo = true;
        undoButton.setDisable(false);
        
        isHumanTurn = false;
        updateStatus();
        executeComputerMove();

    }

    private void executeComputerMove() {
        if (gameFinished) return;

        statusLabel.setText("Turno de " + compSym + " (computadora)");

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (gameFinished) return;

            Board next = minimax.getBestMove(gameBoard);
            if (next == null) return;

            applyDifference(next, compSym);
            if (checkGameEnd()) return;

            isHumanTurn = true;
            updateStatus();
        });
        pause.play();
    }

    private void executeComputerGame() {
        if (gameFinished) return;

        if (computerGame.isGameOver()) {
            showComputerResultLater();
            return;
        }

        char currentPlayer = computerGame.getCurrentPlayer();
        String computerName = (currentPlayer == 'X') ? "Computadora 1" : "Computadora 2";
        statusLabel.setText("Turno de " + computerName + " (" + currentPlayer + ")");

        PauseTransition pause = new PauseTransition(Duration.seconds(1));
        pause.setOnFinished(event -> {
            if (gameFinished) return;

            computerGame.playTurn();
            gameBoard = computerGame.getBoard();
            updateBoard();

            if (computerGame.isGameOver()) {
                showComputerResultLater();
                return;
            }

            executeComputerGame();
        });
        pause.play();
    }

    private void applyDifference(Board next, char symbol) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (gameBoard.getAt(i, j) == Board.EMPTY && next.getAt(i, j) == symbol) {
                    applyMove(i, j, symbol);
                    return;
                }
            }
        }
    }

    private void applyMove(int row, int column, char symbol) {
        gameBoard.setMove(row, column, symbol);
        buttons[row][column].setText(String.valueOf(symbol));
        buttons[row][column].setDisable(true);
        styleButton(buttons[row][column], symbol);
    }

    private void updateBoard() {
        updateBoardPosition(0);
    }
    private void updateBoardPosition(int position) {
        if (position >= 9) {
            return;
        }
        int row = position / 3;
        int column = position % 3;
        char symbol = gameBoard.getAt(row, column);
        buttons[row][column].setText(symbol == Board.EMPTY ? "" : String.valueOf(symbol));
        buttons[row][column].setDisable(symbol != Board.EMPTY);
        updateBoardPosition(position + 1);
    }

    private void styleButton(Button button, char symbol) {
        String color = (symbol == 'X') ? "#0000FF" : "#FF0000";
        button.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: " + color + ";");
    }

    private void updateStatus() {
        if (humanVsHuman) {
            statusLabel.setText("Turno del jugador " + currentHumanPlayer + " (" + currentHumanPlayer + ")");
            return;
        }

        if (computerVsComputer) {
            char current = computerGame.getCurrentPlayer();
            String computerName = (current == 'X') ? "Computadora 1" : "Computadora 2";
            statusLabel.setText("Turno de " + computerName + " (" + current + ")");
            return;
        }

        if (isHumanTurn) {
            statusLabel.setText("Turno de " + humanSym + " (su turno)");
        } else {
            statusLabel.setText("La computadora está pensando...");
        }
    }

    private boolean checkGameEnd() {
        if (gameFinished) return true;

        char winner = gameBoard.checkWinner();
        if (winner != Board.EMPTY) {
            gameFinished = true;
            disableBoard();
            showResultLater("Ganador: " + winner);
            return true;
        }

        if (gameBoard.isFull()) {
            gameFinished = true;
            disableBoard();
            showResultLater("Empate");
            return true;
        }

        return false;
    }

    private void showComputerResultLater() {
        if (gameFinished) return;
        gameFinished = true;

        char winner = computerGame.getWinner();
        String message;

        if (winner == Board.EMPTY) {
            message = "Empate";
        } else {
            String computerName = (winner == 'X') ? "Computadora 1" : "Computadora 2";
            message = "Ganador: " + computerName + " (" + winner + ")!";
        }

        disableBoard();
        showResultLater(message);
    }

    private void showResultLater(String message) {
        Platform.runLater(() -> showResult(message));
    }

    private void showResult(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Fin de la partida");
        alert.setHeaderText(message);
        alert.showAndWait();
    }

    private void disableBoard() {
        if (buttons == null) return;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j].setDisable(true);
            }
        }
    }
    
    // SUSTENTACION
    private void undoMove() {
        if (!canUndo || previousBoard == null) {
            return;
        }
        // Guardar la tabla anterior como la actual
        gameBoard = new Board(previousBoard);
        updateBoard();
        // En el turno del humano se NO se puede deshacer
        // y el botón de UNDO debe estar inactivo
        isHumanTurn = true;
        canUndo = false;
        undoButton.setDisable(true);
        // Cambiar el estado del tablero
        updateStatus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}