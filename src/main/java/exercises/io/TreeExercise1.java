package exercises.io;

/*
 ĆWICZENIE 1 — PRZEJŚCIE DRZEWA (DFS)

 Masz strukturę drzewa złożoną z węzłów (Node).
 Każdy Node ma nazwę oraz listę dzieci (children).

 Twoje zadanie:

 Napisz metodę:

     public static void printAll(Node node)

 która wypisze NAZWĘ KAŻDEGO węzła w drzewie.

 Zasady:
 - metoda ma działać rekurencyjnie
 - ma przejść przez CAŁE drzewo
 - kolejność nie ma znaczenia
 - nie wolno używać pętli while, tylko for/for-each + rekurencja
*/

import java.util.List;

public class TreeExercise1 {

    static class Node {
        String name;
        List<Node> children;

        Node(String name, List<Node> children) {
            this.name = name;
            this.children = children;
        }
    }

    public static void printAll(Node node) {
        System.out.println(node.name);
        List<Node> childrens = node.children;
        for (Node child : childrens){
            printAll(child);
        }


    }

    public static void main(String[] args) {

        Node leaf1 = new Node("B", List.of());
        Node leaf2 = new Node("C", List.of());
        Node leaf3 = new Node("E", List.of());

        Node middle1 = new Node("A", List.of(leaf1, leaf2));
        Node middle2 = new Node("D", List.of(leaf3));

        Node root = new Node("ROOT", List.of(middle1, middle2));

        printAll(root);
    }
}
