package com.example.maze_generator;

import java.util.ArrayList;

public class CentroidDecomposition {


    static final int N = MazeGener.N;
    static final int LOGN = (int)Math.ceil(Math.log(N)/Math.log(2));
    static final int INF = 100000;

    static ArrayList<Integer>[] Tree = new ArrayList[N];
    static int [] Subtree = new int[N];
    static int[] Parent = new int[N];
    static int[] Level = new int[N];
    static ArrayList<Integer>[] LevelList = new ArrayList[LOGN];

    static int[] ans = new int[N];
    static int[] ans_rednode = new int[N];
    static ArrayList<Integer> way = new ArrayList<>();

    static int[] deep = new int[N];
    static int[][] parent2i = new int[LOGN][N];
    static int lcaD = -1;



    static void dfsLevel(int node, int pnode) {
        for(Integer cnode : Tree[node]) {
            if(cnode != pnode) {
                parent2i[0][cnode] = node;
                deep[cnode] = deep[node] + 1;
                dfsLevel(cnode, node);
            }
        }
    }

    static void preprocess() {
        deep[0] = 0;
        parent2i[0][0] = 0;
        dfsLevel(0, -1);

        for(int i = 1; i < LOGN; i++) {
            for(int node = 0; node < N; node++) {
                parent2i[i][node] = parent2i[i-1][parent2i[i-1][node]];
            }
        }
    }

    static int lca(int u, int v) {
        if(deep[u] > deep[v]){
            int change = u;
            u = v;
            v = change;
        }

        int d = deep[v] - deep[u];

        for(int i = 0; i < LOGN; i++) {
            if((d & (1 << i)) > 0)
                v = parent2i[i][v];
        }

        if(u == v) return u;

        // find LCA
        for(int i = LOGN - 1; i >= 0; i--) {
            if(parent2i[i][u] != parent2i[i][v]) {
                u = parent2i[i][u];
                v = parent2i[i][v];
            }
        }
        return parent2i[0][u];
    }

    static int dist(int u, int v) {
        return deep[u] + deep[v] - 2 * deep[lca(u, v)];
    }

    static  int MaxVertexinLevel(){
        int maxcountvertex = LevelList[0].size();
        for (int i = 1; i < LOGN; i++) {
            if(LevelList[i].size()>maxcountvertex)
                maxcountvertex=LevelList[i].size();
        }
        return maxcountvertex;
    }


    static void DfsSubtree(int v,int parent) {
        Subtree[v] = 1;
        for (Integer u : Tree[v])
            if (u != parent) {
                DfsSubtree(u, v);
                Subtree[v] += Subtree[u];
            }
    }

    static int FindCentroid(int v , int parent , int size){
        for(Integer u : Tree[v])
            if(u != parent && Subtree[u] * 2 > size)
                return FindCentroid(u , v , size) ;
        return v;
    }

    static void DecomposTree(int v , int rootCenTree){
        DfsSubtree(v, -1);
        int ctr = FindCentroid(v, v, Subtree[v]);
        if(rootCenTree == -1)
            rootCenTree = ctr;	// root of centroid tree
        Parent[ctr] = rootCenTree;
        Level[ctr] = Level[rootCenTree]+1;



        for(Integer u : Tree[ctr]) {
            Tree[u].remove(Integer.valueOf(ctr));
            DecomposTree(u, ctr);
        }
        Tree[ctr].clear();
    }


    static void rednode(int v) {
        int rNode = v;
        while(true) {
            if(dist(rNode,v)<ans[v])
                ans_rednode[v] = rNode;
            ans[v] = Math.min(ans[v], dist(rNode, v));
            if(v == Parent[v]) break;
            v = Parent[v];
        }
    }

    static int mindistan(int v) {
        int start = v;
        int minD = INF;
        while(true) {
            if((dist(start, v) + ans[v]) < minD) {
                lcaD = lca(start, v);
            }
            minD = Math.min(minD, dist(start, v) + ans[v]);

            if(v == Parent[v]) break;
            v = Parent[v];
        }
        return minD;
    }



    static void startDecompos() {
        for (int i = 0; i < N; i++) {
            Subtree[i] = 0;
            Level[i] = -1;
            Parent[i] = 0;

            ans[i] = INF;
            ans_rednode[i] = 0;
        }
        for (int i = 0; i < LOGN; i++) {
            LevelList[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < N; i++) {
            Tree[i] = new ArrayList<Integer>();
        }
        for (int i = 0; i < N; i++) {
            Tree[i].addAll(MazeGener.AdjacencyList[i]);
        }

        preprocess();
        DecomposTree(0, -1);

        for (int i = 0; i < N; i++) {
            LevelList[Level[i]].add(i);
        }

        for (int i = 0; i < MazeGener.ExitList.size(); i++) {
            rednode(MazeGener.ExitList.get(i));
        }
    }
    static void findexit(int v) {
        int mind = mindistan(v);
        int start = v;

        /*for (int i = 0; i <mind-ans[lcaD] ; i++) {
            way.add(start);
            start = Parent[start];
        }
        start=ans_rednode[lcaD];
        for (int i = 0; i < ans[lcaD]; i++) {
            way.add(start);
            start = Parent[start];

        }
/*
        while (start!= lcaD) {
            way.add(start);
            start = Parent[start];
     }

        way.add(start);
        start = ans_rednode[lcaD];
        while (start != lcaD) {
           way.add(start);
           start = Parent[start];
    }
    */
}





}
