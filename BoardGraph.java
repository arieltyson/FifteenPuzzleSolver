package fifteenpuzzle;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

public class BoardGraph {

    private byte n;
    private byte max;
    private byte[][] board;
    private Map<Byte, HashSet<Byte>> adjList;

    public BoardGraph(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            n = Byte.parseByte(reader.readLine().trim());
            board = new byte[n][n];
            byte c1, c2,s;
            adjList = new HashMap<Byte, HashSet<Byte>>();

            // read board values from file
            String line;
            // n=3 max=8, n=4 max=15, n=5 max=24, n=6 max=35, n=7 max=48, n=8 max=63, n=9 max=80
            max = (byte) (n*n);
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    c1 = (byte) reader.read();
                    c2 = (byte) reader.read();
                    s = (byte) reader.read(); // skip the space
                    if (s != ' ' && s != '\n') {
                        reader.close();
                        System.out.println("error in line " + i);
                    }
                    if (c1 == ' ')
                        c1 = '0';
                    if (c2 == ' ')
                        c2 = '0';
                    board[i][j] = (byte) (10 * (c1 - '0') + (c2 - '0'));
                }
            }

            reader.close();

            createGraph();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createGraph() {
        // create adjacency list for board graph
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                byte node = board[i][j];
                if (!adjList.containsKey(node)) {
                    adjList.put(node, new HashSet<Byte>());
                }
                if (i > 0) {
                    adjList.get(node).add(board[i-1][j]);
                }
                if (i < n-1) {
                    adjList.get(node).add(board[i+1][j]);
                }
                if (j > 0) {
                    adjList.get(node).add(board[i][j-1]);
                }
                if (j < n-1) {
                    adjList.get(node).add(board[i][j+1]);
                }
            }
        }
    }

    public byte getMax() {
        return max;
    }

    public byte getN() {
        return n;
    }

    public byte[][] getBoard() {
        return board;
    }
}