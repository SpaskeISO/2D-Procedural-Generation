package com.spasic.proceduralgeneration;

import com.badlogic.gdx.Gdx;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class DungeonGenerator {

    public enum dungeonType { SRP, BSP, CA, DLA }

    private dungeonType type;
    //Map
    private int maxCol = 80;
    private int maxRow = 40;
    private int currentCol;
    private int currentRow;
    //Rooms
    private int numberOfRooms = 10;
    private int addedRooms = 0;
    //Node
    private Node[][] map;
    private Node startNode, goalNode, currentNode;
    private ArrayList<Node> openList = new ArrayList<>();
    private ArrayList<Node> checkedList = new ArrayList<>();
    private ArrayList<Node> entrancesList = new ArrayList<>();

    //others
    private boolean goalReached = false;
    private int firstReached = 0;
    public DungeonGenerator(dungeonType type){
        this.type = type;
    }

    public DungeonGenerator(dungeonType type, int maxCol, int maxRow){
        this.type = type;
        this.maxCol = maxCol;
        this.maxRow = maxRow;
    }


    public Node [][] generateDungeonDLA(int maxCol, int maxRow, int numberOfWalkers, float stickiness){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[currentCol][currentRow];
        createNodes();
        walk(numberOfWalkers, stickiness);


        return map;
    }

    public Node[][] generateDungeonSRP(int maxCol, int maxRow, int numberOfRooms){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        this.numberOfRooms = numberOfRooms;
        resetParametersSRP();
        map = new Node[currentCol][currentRow];
        createNodes();
        placeRoomsSRP();
        connectEntrances();


        return map;
    }

    public Node[][] generateBlankMap(int maxCol, int maxRow){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[currentCol][currentRow];
        createNodes();

        return map;
    }

    private void resetParametersSRP(){
        currentNode = null;
        goalNode = null;
        startNode = null;
        addedRooms = 0;
        openList.clear();
        entrancesList.clear();
        checkedList.clear();
    }

    public void createNodes(){
        for(int x = 0; x < currentCol; x++){
            for(int y = 0; y < currentRow; y++){
                map[x][y] = new Node(x,y);
            }
        }
        for(int x = 0; x < currentCol; x++){
            for(int y = 0; y < currentRow; y++){
                map[x][y].getBoundigbox().setPosition(x * Gdx.graphics.getWidth() * 0.008f,
                    y * Gdx.graphics.getHeight() * 0.015f);
                map[x][y].getBoundigbox().setSize(Gdx.graphics.getWidth() * 0.008f, Gdx.graphics.getHeight() * 0.015f);
            }
        }
    }

    public void placeRoomsSRP(){
        Room currentRoom;
        int counter = 0;
        int collisionCounter = 0;
        while(addedRooms < numberOfRooms && counter < 10000){
            boolean noCollsions = false;
            currentRoom = new Room();
            //System.out.println(currentRoom);
            while(!noCollsions && collisionCounter < 10000){
                //System.out.println(currentRoom);
                if(currentRoom.checkCollision(map)){
                    noCollsions = true;
                    for(int i = currentRoom.x; i < currentRoom.x+currentRoom.width; i++){
                        for(int j = currentRoom.y; j < currentRoom.y+currentRoom.length; j++){
                            if(i == currentRoom.x || j == currentRoom.y || i == currentRoom.x+currentRoom.width - 1 || j == currentRoom.y+currentRoom.length - 1){
                                map[i][j].setAsWall();
                            }
                            else map[i][j].setAsRoom();
                        }
                    }
                    placeEntrancesSRP(currentRoom);
                    addedRooms++;
                    System.out.println("Room placed");
                    Room.id++;
                }
                if(!noCollsions) collisionCounter++;
                currentRoom = new Room();


            }
        counter++;
        }
    }

    public void placeEntrancesSRP(Room currentRoom){
        switch (currentRoom.entrance1) {
            case NORTH ->
                map[currentRoom.x + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)][currentRoom.y].setAsEntrance();
            case SOUTH ->
                map[currentRoom.x + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)][currentRoom.y + currentRoom.length - 1].setAsEntrance();
            case EAST ->
                map[currentRoom.x][currentRoom.y + PRNG.distinctRandom.nextInt(1, currentRoom.length - 1)].setAsEntrance();
            case WEST ->
                map[currentRoom.x + currentRoom.width - 1][currentRoom.y + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)].setAsEntrance();
        }
        if(currentRoom.entrance2 != null){
            switch (currentRoom.entrance2) {
                case NORTH ->
                    map[currentRoom.x + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)][currentRoom.y].setAsEntrance();
                case SOUTH ->
                    map[currentRoom.x + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)][currentRoom.y + currentRoom.length - 1].setAsEntrance();
                case EAST ->
                    map[currentRoom.x][currentRoom.y + PRNG.distinctRandom.nextInt(1, currentRoom.length - 1)].setAsEntrance();
                case WEST ->
                    map[currentRoom.x + currentRoom.width - 1][currentRoom.y + PRNG.distinctRandom.nextInt(1, currentRoom.width - 1)].setAsEntrance();
            }
        }
    }


    /*
    This part of the code is used by generateDungeonDLA
     */

    public void walk(int numberOfWalkers, float stickiness){
        int centerX = currentCol / 2;
        int centerY = currentRow / 2;
        map[centerX][centerY].setAsCaveDLA();
        int x;
        int y;
        int moveX;
        int moveY;
        for(int i = 0; i < numberOfWalkers; i++) {
            do {
                x = PRNG.distinctRandom.nextInt(0, currentCol);
                y = PRNG.distinctRandom.nextInt(0, currentRow);
            } while (map[x][y].isCaveDLA());
            map[x][y].setAsWalker();
            while(true){
                if((map[Math.max(0, x-1)][y].isCaveDLA() || map[Math.min(x+1, currentCol - 1)][y].isCaveDLA()
                    || map[x][Math.max(0, y-1 )].isCaveDLA() || map[x][Math.min(y+1, currentRow - 1)].isCaveDLA())
                    && PRNG.distinctRandom.nextFloat() <= stickiness){
                    map[x][y].setAsNotWalker();
                    map[x][y].setAsCaveDLA();
                    break;
                }
                do {
                    moveX = PRNG.distinctRandom.nextInt(-1,2);
                    moveY = PRNG.distinctRandom.nextInt(-1, 2);

                } while (!((x + moveX) >= 0 && (x + moveX) < currentCol && (y + moveY) >= 0 && (y + moveY) < currentRow));

                //System.out.println("moveX: " + moveX + "|| moveY: " + moveY);
                //System.out.println("x: " + x + "|| y: " + y);
                if(!map[x+moveX][y+moveY].isCaveDLA()){
                    map[x][y].setAsNotWalker();
                    x += moveX;
                    y += moveY;
                    map[x][y].setAsWalker();
                }
            }



        }
    }



    /*
     * This part of the code are methods used for A* pathfinding algorithm and can be used for any type of dungeon
     * to connect entrances of rooms.
     */

    /**
     * This method sets startNode reference to point at a Node at map[col][row] and then sets the currentNode reference
     * to point at the same object as startNode, col and row are provided in the method call.
     * @param col index of a column of where the node is
     * @param row index of a row of where the node is
     */
    public void setStartNode(int col, int row){
        map[col][row].setAsStart();
        startNode = map[col][row];
        currentNode = startNode;
    }

    /**
     * This method sets goalNode reference to point at a Node at map[col][row], col and row are provided in the method
     * call.
     */
    public void setGoalNode(int col, int row){
        map[col][row].setAsGoal();
        goalNode = map[col][row];
    }

    /**
     * This method call setAsSolid method of the node at position provided by col and row.
     */
    public void setSolidNode(int col, int row){
        map[col][row].setAsSolid();
    }

    /**
     * This method calculates costs of the Node ojbect that is passed as an argument. G cost is distance from START node
     * H cost is distance from GOAL node, F cost is the sum of G cost and H cost.
     * @param node reference to the Node that we are calculating the costs for.
     */
    public void getCost(Node node){
        //get G cost (distance from START node)
        int xDistance = Math.abs(node.getCol() - startNode.getCol());
        int yDistance = Math.abs(node.getRow() - startNode.getRow());
        node.setGCost(xDistance + yDistance);

        // get H cost (The distance from GOAL node)
        xDistance = Math.abs(node.getCol() - goalNode.getCol());
        yDistance = Math.abs(node.getRow() - goalNode.getRow());
        node.setHCost(xDistance + yDistance);

        //get F cost (The total cost)
        node.setFCost(node.getGCost() + node.getHCost());
        if(node != startNode && node != goalNode){
            node.setText("F:" + node.getFCost().toString() + "\nG:" + node.getGCost().toString());
        }
    }


    /**
     * This method goes through the whole map and calls getCost method for each Node in the map.
     */
    public void setCostOnNodes(){
        for(int x = 0; x < currentCol; x++){
            for(int y = 0; y < currentRow; y++) {
                getCost(map[x][y]);
            }
        }
    }

    /**
     *
     *
     *
     * @param node
     * @return
     */
    public boolean openNode(Node node){
        if(node.isOpen() == false && node.isChecked() == false && node.isSolid() == false){
            node.setAsOpen();
            node.setNodeParent(currentNode);
            openList.add(node);
            return true;
        }
        return false;
    }

    public void trackThePath(){
        Node current = goalNode;
        while(current != startNode && current.getNodeParent() != null){
            current = current.getNodeParent();

            if(current != startNode){
                //System.out.println("setting as path");
                //System.out.println(current);
                current.setAsPath();
            }

            if(current.getNodeParent() != null)System.out.println(current.getNodeParent());
            else System.out.println("Parent is null");
        }
        //System.out.println("track the path finished");
    }

    public void autoSearch(){
        //System.out.println("Entered Auto search");
        while(!goalReached){

            int col = currentNode.getCol();
            int row = currentNode.getRow();
            //System.out.println("Col and Row set");

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);
            //System.out.println("Lists manipulated");
            //System.out.println(openList.size());

            //Check node in UP, DOWN, LEFT, RIGHT
            System.out.println(col + " : " + row);
            if(row - 1 >= 0){
                if(openNode(map[col][row-1])){
                    //System.out.println("Node opened SOUTH");
                }
                else {
                    //System.out.println("Node not opened SOUTH");
                }
            }
            if(col-1 >= 0){
                if(openNode(map[col-1][row])){
                    //System.out.println("Node opened WEST");
                }
                else {
                    //System.out.println("Node not opened WEST");
                }
            }
            if(row + 1 < currentRow){
                if(openNode(map[col][row+1])){
                    //System.out.println("Node opened NORTH");
                }
                else {
                    //System.out.println("Node not opened NORTH");
                }
            }
            if(col + 1 < currentCol){
                if(openNode(map[col+1][row])){
                    //System.out.println("Node opened EAST");
                }
                else {
                    //System.out.println("Node not opened EAST");
                }
            }
            //System.out.println("Opening nodes");

            //Find the best node
            int bestNodeIndex = 0;
            int bestNodeFCost = 99999;

            //System.out.println("Fidning best node");
            for(int i = 0; i < openList.size(); i++){
                //Check if the F cost is better
                if(openList.get(i).getFCost() < bestNodeFCost){
                    bestNodeIndex = i;
                    bestNodeFCost = openList.get(i).getFCost();
                }
                //If the F cost is better check the G cost
                else if(openList.get(i).getFCost() == bestNodeFCost){
                    if(openList.get(i).getGCost() < openList.get(bestNodeIndex).getGCost()){
                        bestNodeIndex = i;
                        bestNodeFCost = openList.get(i).getFCost();
                    }
                }
            }

            if(openList.size() == 0){
                break;
            }
            //System.out.println("Finding best node finished");

            currentNode = openList.get(bestNodeIndex);
            if(currentNode == goalNode){
                goalReached = true;
                startNode.setConnected(true);
                System.out.println("Goal node reached");
                trackThePath();
            }

        }
    }




    public void connectEntrances(){

        for(int col = 0; col < currentCol; col++){
            for(int row = 0; row < currentRow; row++){
                if(map[col][row].isEntrance()){
                    entrancesList.add(map[col][row]);
                }
            }
        }
        for(Node node : entrancesList){
            System.out.println(node);
        }
        System.out.println(entrancesList.size());

        int entrancesConnected = 0;
        while(entrancesConnected < entrancesList.size() - 1){

            for(int x = 0; x < currentCol; x++){
                for(int y = 0; y < currentRow; y++){
                    if(!map[x][y].isRoom()) {
                        map[x][y].setNodeParent(null);
                        map[x][y].setAsNotOpen();
                        map[x][y].setAsNotChecked();
                    }
                }
            }

            openList.clear();
            checkedList.clear();

            setStartNode(entrancesList.get(entrancesConnected).getCol(), entrancesList.get(entrancesConnected).getRow());
            setGoalNode(entrancesList.get(entrancesConnected+1).getCol(), entrancesList.get(entrancesConnected+1).getRow());

            setCostOnNodes();

            autoSearch();

            goalReached = false;

            System.out.println(entrancesConnected + 1 + " entrance connected");
            entrancesConnected++;


        }
        for(int x = 0; x < currentCol; x++){
            for(int y = 0; y < currentRow; y++){
                if(!map[x][y].isRoom()) {
                    map[x][y].setNodeParent(null);
                    map[x][y].setAsNotOpen();
                    map[x][y].setAsNotChecked();
                }
            }
        }

    }
}
