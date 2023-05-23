package com.spasic.proceduralgeneration.oldCode2;

import com.spasic.proceduralgeneration.AnsiiEscapeColors;
import com.spasic.proceduralgeneration.oldCode.PRNG;

import java.util.LinkedList;
import java.util.stream.Collectors;

public class GameMapNodes {

    public static int mapLength = 40;
    public static int mapWidth = 80;
    public static int numberOfRooms = 10;
    public static int addedRooms = 0;

    public static void main(String[] args) {

        Node map[][] = new Node[mapLength][mapWidth];
        initialiseMap(map);
        printMap(map);
        System.out.println("\n\n");
        placeRooms(map);
        printMap(map);
        connectNodes(map);
        connectEntrances(map);





    }

    public static void initialiseMap(Node map[][]){
        for(int i = 0; i < mapLength; i++){
            for(int j = 0; j < mapWidth; j++){
                map[i][j] = new Node(NodeType.WALL);
            }
        }
    }

    public static void printMap(Node map[][]){
        for(int i = 0; i < mapLength; i++){
            for(int j = 0; j < mapWidth; j++){
                switch (map[i][j].type) {
                    case WALL -> System.out.print("#");
                    case PATH -> System.out.print("P");
                    case ROOM -> System.out.print(AnsiiEscapeColors.ANSI_RED + "R" + AnsiiEscapeColors.ANSI_RESET);
                    case ENTRANCE -> System.out.print(AnsiiEscapeColors.ANSI_CYAN + "E" + AnsiiEscapeColors.ANSI_RESET);
                }
            }
            System.out.println();
        }

    }

    public static void placeRooms(Node map[][]){
        NodeRoom currentRoom;
        while(addedRooms < numberOfRooms){
            boolean noCollsions = false;
            currentRoom = new NodeRoom();
            System.out.println(currentRoom);
            while(!noCollsions){
                System.out.println(currentRoom);
                if(currentRoom.checkCollision(map)){
                    noCollsions = true;
                    for(int i = currentRoom.x; i < currentRoom.x+currentRoom.width; i++){
                        System.out.println("usao u prvu petlju");
                        for(int j = currentRoom.y; j < currentRoom.y+currentRoom.length; j++){
                            map[j][i].setType(NodeType.ROOM);
                        }
                    }
                    placeEntrances(map, currentRoom);
                    addedRooms++;
                    System.out.println("Room placed");
                }
                currentRoom = new NodeRoom();


            }
        }


    }

    public static void placeEntrances(Node map[][], NodeRoom currentRoom){
        switch (currentRoom.entrance1){
            case NORTH:
                map[currentRoom.y][currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)].setType(NodeType.ENTRANCE);
                break;
            case SOUTH:
                map[currentRoom.y + currentRoom.length - 1][currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)].setType(NodeType.ENTRANCE);
                break;
            case EAST:
                map[currentRoom.y + PRNG.random.nextInt(1, currentRoom.length - 1)][currentRoom.x].setType(NodeType.ENTRANCE);
                break;
            case WEST:
                map[currentRoom.y + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.x + currentRoom.width - 1].setType(NodeType.ENTRANCE);
                break;
        }
        if(currentRoom.entrance2 != null){
            switch (currentRoom.entrance2){
                case NORTH:
                    map[currentRoom.y][currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)].setType(NodeType.ENTRANCE);
                    break;
                case SOUTH:
                    map[currentRoom.y + currentRoom.length - 1][currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)].setType(NodeType.ENTRANCE);
                    break;
                case EAST:
                    map[currentRoom.y + PRNG.random.nextInt(1, currentRoom.length - 1)][currentRoom.x].setType(NodeType.ENTRANCE);
                    break;
                case WEST:
                    map[currentRoom.y + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.x + currentRoom.width - 1].setType(NodeType.ENTRANCE);
                    break;
            }
        }
    }

    public static void connectNodes(Node map[][]){
        for(int i = 0; i < mapLength; i++){
            for(int j = 0; j < mapWidth; j++){
                if(map[i][j].type == NodeType.WALL || map[i][j].type == NodeType.ENTRANCE){
                    if(i - 1 >= 0 && j - 1 >=0 && i + 1 < mapLength && j + 1 < mapWidth){
                        if(map[i-1][j-1].type == NodeType.WALL || map[i-1][j-1].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i-1][j-1], 15);
                        if(map[i+1][j+1].type == NodeType.WALL || map[i+1][j+1].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i+1][j+1], 15);
                        if(map[i+1][j-1].type == NodeType.WALL || map[i+1][j-1].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i+1][j-1], 15);
                        if(map[i-1][j+1].type == NodeType.WALL || map[i-1][j+1].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i-1][j+1], 15);
                        if(map[i][j+1].type == NodeType.WALL || map[i][j].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i][j+1], 10);
                        if(map[i][j-1].type == NodeType.WALL || map[i][j].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i][j-1], 10);
                        if(map[i+1][j].type == NodeType.WALL || map[i][j].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i+1][j], 10);
                        if(map[i-1][j].type == NodeType.WALL || map[i][j].type == NodeType.ENTRANCE) map[i][j].addAdjacentNode(map[i-1][j], 10);
                    }
                }
            }
        }
    }

    public static void connectEntrances(Node[][] map){
        LinkedList<Node> entrances = new LinkedList<>();
        for(int i = 0; i < mapLength; i++) {
            for (int j = 0; j < mapWidth; j++) {
                if(map[i][j].type == NodeType.ENTRANCE){
                    entrances.add(map[i][j]);
                }
            }
        }
        for(Node entrance : entrances){
            entrance.calculateShortestPath(entrance);
        }
        printPaths(entrances);

    }

    public static void printPaths(LinkedList<Node> nodes){
        nodes.forEach(node -> {
            String path = node.getShortestPath().stream()
                .map(Node::getIndex)
                .collect(Collectors.joining(" -> "));
            System.out.println((path.isBlank()
                ? "%s : %s".formatted(node.getIndex(), node.getDistance())
                : "%S -> %s : %s".formatted(path, node.getIndex(), node.getDistance())));
        });
    }
}

