package com.example.maze_generator;

import java.util.ArrayList;
import java.util.Random;

public class MazeGener {
    static final int maxN = 10;
    static final int N = maxN * maxN;
    static int tik_tak = 0;
    static int[] dx = {-1, 0, 1, 0};
    static int[] dy = {0, 1, 0, -1};
    public static int[][] Vertex = new int[maxN][maxN];
    static int[][] ExitVertex = new int[maxN][maxN];
    static int[] random_way = {0,1,2,3};
    static ArrayList<Integer>[] AdjacencyList = new ArrayList[N];
    static ArrayList<Integer> ExitList = new ArrayList<>();



    public static boolean out_of_range(int x, int y) {
        return (x < 0 || y < 0 || x >= maxN || y >= maxN);
    }


    public static int get_random_number() {
        Random random = new Random();
        return random.nextInt(maxN-1);
    }

    public static void shuffly_way(int[] way) {
        int size = way.length;
        Random rand = new Random();
        rand.nextInt();
        for (int i = 0; i < size; i++) {
            int change = i + rand.nextInt(size - i);
            swap(way, i, change);
        }

    }

    public static void swap(int[] way, int position, int change) {
        int temp = way[position];
        way[position] = way[change];
        way[change] = temp;

    }


    public static void dfs(int x, int y) {
        Vertex[x][y] = tik_tak;
        shuffly_way(random_way);

        for (int i = 0; i < 4; i++) {
            int nx = x + dx[random_way[i]];
            int ny = y + dy[random_way[i]];
            if (!out_of_range(nx, ny) && Vertex[nx][ny] == -1) {
                tik_tak++;
                int v = Vertex[x][y];
                int to = tik_tak;
                AdjacencyList[v].add(to);
                AdjacencyList[to].add(v);
                dfs(nx, ny);
            }

        }
    }


    public static void start() {
        int startX = get_random_number();
        int startY = get_random_number();

        for (int i = 0; i < maxN; i++)
            for (int j = 0; j < maxN; j++)
                Vertex[i][j] = -1;

        for (int i = 0; i < N; i++) {
            AdjacencyList[i] = new ArrayList<Integer>();
        }



        dfs(startX,startY);

        while (ExitList.size()!=maxN){
            Random random = new Random();
            int randomExit = random.nextInt(N-1);
            if(!ExitList.contains(randomExit))
                ExitList.add(randomExit);
        }
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                if(ExitList.contains(Vertex[i][j]))
                    ExitVertex[i][j] = 1;
                else
                    ExitVertex[i][j] = 0;
            }
        }

        /*for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++)
                System.out.print(L[i][j]+ "\t");
            System.out.println();
        }
/*
        for (int i = 0; i < N; i++){
            System.out.println(i+" : "+G[i].toString());
        }

        System.out.println(G[5].contains(4));
        */

    }

}
