package modelo;

import java.util.Arrays;

public class Board {

    public static final char EMPTY = ' ';
    private final char[][] matrix;

    public Board() {
        this.matrix = new char[3][3];
        for (int i = 0; i < 3; i++) {
            Arrays.fill(this.matrix[i], EMPTY);
        }
    }

    public Board(Board other) {
        this.matrix = new char[3][3];
        for (int i = 0; i < 3; i++) {
            System.arraycopy(other.matrix[i], 0, this.matrix[i], 0, 3);
        }
    }

    public void setMove(int r, int c, char symbol) {
        this.matrix[r][c] = symbol;
    }

    public char getAt(int r, int c) {
        return this.matrix[r][c];
    }

    public boolean isFull() {
        for (int r = 0; r < 3; r++) {
            for (int c = 0; c < 3; c++) {
                if (matrix[r][c] == EMPTY) {
                    return false;
                }
            }
        }
        return true;
    }

    public char checkWinner() {
        // Filas y columnas
        for (int i = 0; i < 3; i++) {
            char rowWinner = checkLine(matrix[i][0], matrix[i][1], matrix[i][2]);
            if (rowWinner != EMPTY) return rowWinner;

            char colWinner = checkLine(matrix[0][i], matrix[1][i], matrix[2][i]);
            if (colWinner != EMPTY) return colWinner;
        }

        // Diagonales
        char diag1 = checkLine(matrix[0][0], matrix[1][1], matrix[2][2]);
        if (diag1 != EMPTY) return diag1;

        char diag2 = checkLine(matrix[0][2], matrix[1][1], matrix[2][0]);
        if (diag2 != EMPTY) return diag2;

        return EMPTY;
    }

    private char checkLine(char first, char second, char third) {
        if (first != EMPTY && first == second && first == third) {
            return first;
        }
        return EMPTY;
    }

    public int calculateP(char player) {
        char opponent = (player == 'X') ? 'O' : 'X';
        int count = 0;

        // Filas y columnas
        for (int i = 0; i < 3; i++) {
            if (isLineAvailable(matrix[i][0], matrix[i][1], matrix[i][2], opponent)) count++;
            if (isLineAvailable(matrix[0][i], matrix[1][i], matrix[2][i], opponent)) count++;
        }

        // Diagonales
        if (isLineAvailable(matrix[0][0], matrix[1][1], matrix[2][2], opponent)) count++;
        if (isLineAvailable(matrix[0][2], matrix[1][1], matrix[2][0], opponent)) count++;

        return count;
    }

    private boolean isLineAvailable(char c1, char c2, char c3, char opponent) {
        return c1 != opponent && c2 != opponent && c3 != opponent;
    }
}