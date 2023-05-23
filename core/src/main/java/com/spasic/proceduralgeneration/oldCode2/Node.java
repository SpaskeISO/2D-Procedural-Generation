package com.spasic.proceduralgeneration.oldCode2;

import lombok.*;

import java.util.*;
import java.util.stream.Stream;

@Getter
@Setter
@RequiredArgsConstructor
@NoArgsConstructor
public class Node implements Comparable<Node>{

    public static Integer createdNodes = 0;

    @NonNull
    public NodeType type;
    private final String index = Integer.toString(createdNodes++);
    //private final String name;

    private Integer distance = Integer.MAX_VALUE;
    private List<Node> shortestPath = new LinkedList<>();
    private Map<Node,  Integer> adjacentNodes = new HashMap<>();
    private boolean isConnected = false;

    public void addAdjacentNode(Node node, int weight){
        adjacentNodes.put(node, weight);
    }

    @Override
    public int compareTo(Node node) {
        return Integer.compare(this.distance, node.getDistance());
    }

    public void calculateShortestPath(Node source){
        source.setDistance(0);
        final Set<Node> settledNodes = new HashSet<>();
        final Queue<Node> unsettledNodes = new PriorityQueue<>(Collections.singleton(source));
        while(!unsettledNodes.isEmpty()){
            final Node currentNode = unsettledNodes.poll();
            currentNode.getAdjacentNodes().entrySet().stream().filter(entry -> !settledNodes.contains(entry.getKey()))
                    .forEach(entry -> {
                        evaluateDistanceAndPath(entry.getKey(), entry.getValue(), currentNode);
                        unsettledNodes.add(entry.getKey());
                    });
            settledNodes.add(currentNode);
        }
    }

    public void evaluateDistanceAndPath(Node adjacentNode, Integer edgeWeight, Node sourceNode){
        Integer newDistance = sourceNode.getDistance() + edgeWeight;
        if(newDistance < adjacentNode.getDistance()){
            adjacentNode.setDistance(newDistance);
            adjacentNode.setShortestPath(
                    Stream.concat(sourceNode.getShortestPath().stream(), Stream.of(sourceNode)).toList()
                    );
        }
    }


}
