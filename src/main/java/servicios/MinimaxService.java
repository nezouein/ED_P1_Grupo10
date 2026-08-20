/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package servicios;
import modelo.Board;
import modelo.TreeNode;
/**
 *
 * @author joshu
 */

public class MinimaxService {
    private char compSym, humanSym;

    public MinimaxService(char compSym, char humanSym) {
        this.compSym = compSym;
        this.humanSym = humanSym;
    }

    public Board getBestMove(Board current) {
        TreeNode<Board> root = new TreeNode<>(current);
        generateChildren(root, compSym);
        for (TreeNode<Board> child : root.getChildren()) {
            if (child.getData().checkWinner() == Board.EMPTY) generateChildren(child, humanSym);
        }

        Board bestBoard = null;
        int maxOfMins = Integer.MIN_VALUE;

        for (TreeNode<Board> nodeL1 : root.getChildren()) {
            int currentMin = nodeL1.isLeaf() ? utility(nodeL1.getData()) : Integer.MAX_VALUE;
            if (!nodeL1.isLeaf()) {
                for (TreeNode<Board> nodeL2 : nodeL1.getChildren()) {
                    int u = utility(nodeL2.getData());
                    if (u < currentMin) currentMin = u;
                }
            }
            if (currentMin > maxOfMins) {
                maxOfMins = currentMin;
                bestBoard = nodeL1.getData();
            }
        }
        return (bestBoard != null) ? bestBoard : current;
    }

    private void generateChildren(TreeNode<Board> parent, char sym) {
        Board b = parent.getData();
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                if (b.getAt(i, j) == Board.EMPTY) {
                    Board next = new Board(b);
                    next.setMove(i, j, sym);
                    parent.addChild(new TreeNode<>(next));
                }
            }
        }
    }

    private int utility(Board b) { return b.calculateP(compSym) - b.calculateP(humanSym); }
}
