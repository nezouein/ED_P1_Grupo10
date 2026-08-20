package servicios;

import modelo.Board;

import java.util.ArrayList;
import java.util.List;

public class ComputerVsComputerService {

    private final char player1;
    private final char player2;
    private final MinimaxService minimaxPlayer1;
    private final MinimaxService minimaxPlayer2;
    private final List<Board> history;

    private Board board;
    private char currentPlayer;

    public ComputerVsComputerService(char player1, char player2) {
        this.player1 = player1;
        this.player2 = player2;
        this.minimaxPlayer1 = new MinimaxService(player1, player2);
        this.minimaxPlayer2 = new MinimaxService(player2, player1);
        this.history = new ArrayList<>();

        reset();
    }

    public void reset() {
        this.board = new Board();
        this.currentPlayer = player1;
        this.history.clear();
        this.history.add(new Board(this.board));
    }

    public boolean playTurn() {
        if (isGameOver()) {
            return false;
        }

        MinimaxService minimax = (currentPlayer == player1) ? minimaxPlayer1 : minimaxPlayer2;
        Board nextMove = minimax.getBestMove(this.board);

        if (nextMove == null) {
            return false;
        }

        this.board = new Board(nextMove);
        this.history.add(new Board(this.board));

        if (!isGameOver()) {
            this.currentPlayer = (currentPlayer == player1) ? player2 : player1;
        }

        return true;
    }

    public boolean isGameOver() {
        return board.checkWinner() != Board.EMPTY || board.isFull();
    }

    public char getWinner() {
        return board.checkWinner();
    }

    public Board getBoard() {
        return new Board(this.board);
    }

    public char getCurrentPlayer() {
        return currentPlayer;
    }

    public char getPlayer1() {
        return player1;
    }

    public char getPlayer2() {
        return player2;
    }

    public List<Board> getHistory() {
        List<Board> copy = new ArrayList<>();
        for (Board b : history) {
            copy.add(new Board(b));
        }
        return copy;
    }

    public MinimaxService getMinimaxPlayer1() {
        return minimaxPlayer1;
    }

    public MinimaxService getMinimaxPlayer2() {
        return minimaxPlayer2;
    }
}