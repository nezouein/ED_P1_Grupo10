/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;

/**
 *
 * @author joshu
 */

public class Board {
    private char[][] matrix;
    public static final char EMPTY = ' ';

    public Board() {
        matrix = new char[3][3];
        for (int i = 0; i < 3; i++) for (int j = 0; j < 3; j++) matrix[i][j] = EMPTY;
    }

    public Board(Board other) {
        this.matrix = new char[3][3];
        for (int i = 0; i < 3; i++) System.arraycopy(other.matrix[i], 0, this.matrix[i], 0, 3);
    }

    public void setMove(int r, int c, char symbol) { matrix[r][c] = symbol; }
    public char getAt(int r, int c) { return matrix[r][c]; }

    public int calculateP(char player) {
        char opponent = (player == 'X') ? 'O' : 'X';
        int count = 0;
        for (int i = 0; i < 3; i++) if (isLineAvailable(matrix[i][0], matrix[i][1], matrix[i][2], opponent)) count++;
        for (int i = 0; i < 3; i++) if (isLineAvailable(matrix[0][i], matrix[1][i], matrix[2][i], opponent)) count++;
        if (isLineAvailable(matrix[0][0], matrix[1][1], matrix[2][2], opponent)) count++;
        if (isLineAvailable(matrix[0][2], matrix[1][1], matrix[2][0], opponent)) count++;
        return count;
    }

    private boolean isLineAvailable(char c1, char c2, char c3, char opponent) {
        return c1 != opponent && c2 != opponent && c3 != opponent;
    }

    public boolean isFull() {
        for (char[] row : matrix) for (char c : row) if (c == EMPTY) return false;
        return true;
    }

    public char checkWinner() {
        for (int i = 0; i < 3; i++) {
            if (matrix[i][0] != EMPTY && matrix[i][0] == matrix[i][1] && matrix[i][0] == matrix[i][2]) return matrix[i][0];
            if (matrix[0][i] != EMPTY && matrix[0][i] == matrix[1][i] && matrix[0][i] == matrix[2][i]) return matrix[0][i];
        }
        if (matrix[1][1] != EMPTY && matrix[0][0] == matrix[1][1] && matrix[0][0] == matrix[2][2]) return matrix[1][1];
        if (matrix[1][1] != EMPTY && matrix[0][2] == matrix[1][1] && matrix[0][2] == matrix[2][0]) return matrix[1][1];
        return EMPTY;
    }
}
