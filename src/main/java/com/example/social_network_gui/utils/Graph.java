package com.example.social_network_gui.utils;

import java.util.ArrayList;
import java.util.LinkedList;

public class Graph {

    private final int V;
    private final LinkedList<Integer>[] adjListArray;


    public Graph(int V) {
        this.V = V;
        adjListArray = new LinkedList[V];
        for(int i= 0; i <V ;i++){
            adjListArray[i]= new LinkedList<>();
        }
    }

    /**
     * Add an edge to an undirected graph
     * @param src - the first node
     * @param dest - the second node
     * */
    public void addEdge(int src,int dest){
        adjListArray[src].add(dest);
        adjListArray[dest].add(src);
    }

    /**
     * @para v - current node
     * @param visited - array of visited node
     * @param componentNode - array of nodes in the component
     * */
    private void DFS(int v, boolean[] visited, ArrayList<Integer> componentNode){
        visited[v] = true;
        componentNode.add(v);
        for (int x : adjListArray[v]){
            if(!visited[x]){
                DFS(x,visited,componentNode);
            }
        }
    }

    /**
     * @return the number of communities
     * */
    public int connectedComponents(){
        int connected = 0;
        boolean[] visited = new boolean[V];
        ArrayList<ArrayList<Integer>> components = new ArrayList<>();
        for(int v= 1; v< V; ++v){
            ArrayList<Integer> componentNode = new ArrayList<>();
            if(!visited[v]){
                DFS(v,visited,componentNode);
            }
            ArrayList<Integer> aux = new ArrayList<>(componentNode);
            components.add(aux);
            if(componentNode.size()>0) {
                connected++;
            }
        }
        return connected;
    }

    /**
     * @return the longest communities
     * */
    public ArrayList<Integer> longestPath(){
        ArrayList<Integer> result = new ArrayList<>();
        boolean[] visited = new boolean[V];
        ArrayList<Integer> maxPath=new ArrayList<>();
        for(int v=0; v < V; ++v){
            maxPath.clear();
            if(!visited[v]){
                DFS(v,visited,maxPath);
            }
            if(result.size() < maxPath.size()){
                result.clear();
                result.addAll(maxPath);
            }
        }
        return result;
    }
}
