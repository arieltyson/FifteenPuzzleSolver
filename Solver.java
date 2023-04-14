package fifteenpuzzle;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.PriorityQueue;


public class Solver {

    private BoardGraph boardGraph;
    private byte[][] board;
    private byte n;
    private byte max;
    private byte[] goal;
    private PriorityQueue<Node> openSet;
    private Map<String, String> path;
    private Set<String> visited;
    private ArrayList<String> directions;

    private class Node {
        byte[] state;
        int fScore;

        public Node(byte[] state) {
            this.state = state;
            this.fScore = manhattanDistance(state);
        }
    }

    public Solver(String inputFilename, String outputFilename) {
        boardGraph = new BoardGraph(inputFilename);
        board = boardGraph.getBoard();
        n = boardGraph.getN();
        max = boardGraph.getMax();
        goal = createGoal();

        openSet = new PriorityQueue<>(Comparator.comparingInt(n -> n.fScore));


        path = new HashMap<String, String>();
        visited = new HashSet<String>();
        directions = new ArrayList<String>();

        byte[] startState = flattenBoard(board);
        openSet.add(new Node(startState));
        path.put(Arrays.toString(startState), "");
        visited.add(Arrays.toString(startState));

        Node goalNode = null;

        long startTime = System.currentTimeMillis();

        while (!openSet.isEmpty()) {
            Node curr = openSet.poll();

            if (Arrays.equals(curr.state, goal)) {
                goalNode = curr;
                break;
            }

            byte[] currBoard = curr.state; //unflattenboard before
            byte zeroIndex = findZeroIndex(currBoard);
            byte zeroRow = (byte) (zeroIndex / n);
            byte zeroCol = (byte) (zeroIndex % n);

            if (zeroRow > 0) {
                byte[] newState = swap(currBoard, zeroIndex, zeroIndex - n);
                enqueueNode(curr, newState, zeroIndex, (byte) (zeroIndex - n), Integer.toString(currBoard[zeroIndex-n]) + " D\n");
            }
            if (zeroRow < n - 1) {
                byte[] newState = swap(currBoard, zeroIndex, zeroIndex + n);
                enqueueNode(curr, newState, zeroIndex, (byte) (zeroIndex + n), Integer.toString(currBoard[zeroIndex+n]) + " U\n");
            }
            if (zeroCol > 0) {
                byte[] newState = swap(currBoard, zeroIndex, zeroIndex - 1);
                enqueueNode(curr, newState, zeroIndex, (byte) (zeroIndex - 1), Integer.toString(currBoard[zeroIndex-1]) + " R\n");
            }
            if (zeroCol < n - 1) {
                byte[] newState = swap(currBoard, zeroIndex, zeroIndex + 1);
                enqueueNode(curr, newState, zeroIndex, (byte) (zeroIndex + 1), Integer.toString(currBoard[zeroIndex+1]) + " L\n");
            }
        }

        if (goalNode != null) {
            printDirections(goalNode, outputFilename);
        } else {
            System.out.println("Puzzle could not be solved.");
        }
    }

    private byte[] createGoal() {
        byte[] goal = new byte[max];
        for (int i = 1; i <= max - 1; i++) {
            goal[i - 1] = (byte) i;
        }
        return goal;
    }

    private byte manhattanDistance(byte[] state) {
        byte distance = 0;
        for (int i = 0; i < state.length; i++) {
            if (state[i] == 0) {
                continue;
            }
            int goalIndex = state[i] - 1;
            int goalRow = goalIndex / n;
            int goalCol = goalIndex % n;
            int currRow = i / n;
            int currCol = i % n;
            distance += Math.abs(goalRow - currRow) + Math.abs(goalCol - currCol);
        }
        return distance;
    }

    private byte[] flattenBoard(byte[][] board) {
        byte[] flattened = new byte[board.length * board[0].length];
        int index = 0;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                flattened[index++] = board[i][j];
            }
        }
        return flattened;
    }

    private byte findZeroIndex(byte[] board) {
        for (int i = 0; i < board.length; i++) {
            if (board[i] == 0) {
                return (byte) i;
            }
        }
        return -1;
    }

    private byte[] swap(byte[] board, int i, int j) {
        byte[] newBoard = Arrays.copyOf(board, board.length);
        byte temp = newBoard[i];
        newBoard[i] = newBoard[j];
        newBoard[j] = temp;
        return newBoard;
    }

    private void enqueueNode(Node curr, byte[] newState, byte fromIndex, byte toIndex, String direction) {
        String newStateString = Arrays.toString(newState);
        if (visited.contains(newStateString)) {
            return;
        }
        Node newNode = new Node(newState);
        int newFScore = newNode.fScore;
        openSet.add(newNode);
        path.put(newStateString, path.get(Arrays.toString(curr.state)) + direction);
        visited.add(newStateString);
    }


    private void printDirections(Node goalNode, String fileName) {
        String pathString = path.get(Arrays.toString(goalNode.state));
        try (PrintWriter writer = new PrintWriter(new FileWriter(fileName))) {
            for (String s : pathString.split("\n")) {
                writer.println(s);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file.");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java Solver <inputFile> <outputFile>");
            System.exit(1);
        }
        String inputFile = args[0];
        String outputFile = args[1];
        Solver solver = new Solver(inputFile, outputFile);
    }

}