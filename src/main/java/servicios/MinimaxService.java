package servicios;

import modelo.Board;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MinimaxService {

    private final char compSym;
    private final char humanSym;

    public MinimaxService(char compSym, char humanSym) {
        this.compSym = compSym;
        this.humanSym = humanSym;
    }

    public MinimaxService(char compSym, char humanSym, boolean ignored) {
        this(compSym, humanSym);
    }

    public Board getBestMove(Board current) {
        List<Board> possibleMoves = getPossibleMoves(current, compSym);
        if (possibleMoves.isEmpty()) {
            return null;
        }

        List<Board> bestMoves = new ArrayList<>();
        int bestScore = Integer.MIN_VALUE;

        for (Board move : possibleMoves) {
            int score = minimax(move, false, 0);

            if (score > bestScore) {
                bestScore = score;
                bestMoves.clear();
                bestMoves.add(move);
            } else if (score == bestScore) {
                bestMoves.add(move);
            }
        }

        // Variedad en el juego si hay empate en la mejor jugada
        Collections.shuffle(bestMoves);
        return bestMoves.get(0);
    }

    private int minimax(Board board, boolean isMaximizing, int depth) {
        char winner = board.checkWinner();

        if (winner == compSym) {
            return 10 - depth;
        }
        if (winner == humanSym) {
            return depth - 10;
        }
        if (board.isFull()) {
            return 0;
        }

        char currentTurnSymbol = isMaximizing ? compSym : humanSym;
        List<Board> nextMoves = getPossibleMoves(board, currentTurnSymbol);

        if (isMaximizing) {
            int maxEval = Integer.MIN_VALUE;
            for (Board move : nextMoves) {
                int eval = minimax(move, false, depth + 1);
                maxEval = Math.max(maxEval, eval);
            }
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE;
            for (Board move : nextMoves) {
                int eval = minimax(move, true, depth + 1);
                minEval = Math.min(minEval, eval);
            }
            return minEval;
        }
    }

    private List<Board> getPossibleMoves(Board board, char symbol) {
        List<Board> moves = new ArrayList<>();

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                if (board.getAt(row, col) == Board.EMPTY) {
                    Board next = new Board(board);
                    next.setMove(row, col, symbol);
                    moves.add(next);
                }
            }
        }

        Collections.shuffle(moves);
        return moves;
    }

    public int utility(Board board) {
        char winner = board.checkWinner();
        if (winner == compSym) return 10;
        if (winner == humanSym) return -10;
        return 0;
    }

    public char getCompSym() {
        return compSym;
    }

    public char getHumanSym() {
        return humanSym;
    }
}