/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package modelo;
import java.util.LinkedList;
import java.util.List;
/**
 *
 * @author joshu
 */


public class TreeNode<T> {
    private T data;
    private List<TreeNode<T>> children;

    public TreeNode(T data) {
        this.data = data;
        this.children = new LinkedList<>();
    }

    public void addChild(TreeNode<T> childNode) { this.children.add(childNode); }
    public T getData() { return data; }
    public List<TreeNode<T>> getChildren() { return children; }
    public boolean isLeaf() { return children.isEmpty(); }
}
