package com.spasic.proceduralgeneration;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class DungeonGenerator {

    public enum dungeonType { SRP, PERLIN, CA, DLA, VORONOI }

    private dungeonType type;
    //Map
    private int maxCol = 1600;
    private int maxRow = 1600;
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

    //Perlin
    public static int[] PerlinPermutations = new int[512];
    public static int Octaves = 4;
    public static float maxPersistence = 1.0f;
    public static float minPersistence = Float.MIN_VALUE;
    public static float Persistence = 0.5f;

    // Voronoi
    private ArrayList<Vector2> sites = new ArrayList<>();
    private ArrayList<Color> colors = new ArrayList<>();
    @Getter
    @Setter
    private static int NUM_SITES = 10;



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
        map = new Node[this.maxCol][this.maxRow];
        createNodes();
        setStartNodesDLA();
        walk(numberOfWalkers, stickiness);

        System.out.println("Map generated");
        return map;
    }

    public Node [][] generateDungeonDLA(int maxCol, int maxRow, int numberOfWalkers, float stickiness, int numberOfStartNodes){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[this.maxCol][this.maxRow];
        createNodes();
        setStartNodesDLA(numberOfStartNodes);
        walk(numberOfWalkers, stickiness);

        System.out.println("Map generated");
        return map;
    }

    public Node[][] generateDungeonSRP(int maxCol, int maxRow, int numberOfRooms){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        this.numberOfRooms = numberOfRooms;
        resetParametersSRP();
        map = new Node[this.maxCol][this.maxRow];
        createNodes();
        placeRoomsSRP();
        connectEntrances();


        return map;
    }

    public Node[][] generateBlankMap(int maxCol, int maxRow){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[this.maxCol][this.maxRow];
        createNodes();

        return map;
    }

    public Node[][] generateDungeonCA(int maxCol, int maxRow, int iterations, int percentage){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[this.maxCol][this.maxRow];
        createNodes();
        randomiseMapCA(percentage);
        iterateCA(iterations);

        return map;
    }

    public Node[][] generateDungeonPerlin(int maxCol, int maxRow, int Octaves, float Persistence){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        map = new Node[this.maxCol][this.maxRow];
        createNodes();
        initializePermutations();
        PerlinGeneration();

        return map;
    }

    public Node[][] generateDungeonVoronoi(int maxCol, int maxRow, int NUM_SITES){
        this.currentCol = maxCol;
        this.currentRow = maxRow;
        DungeonGenerator.NUM_SITES = NUM_SITES;
        if(!sites.isEmpty()){
            sites.clear();
        }
        if(!colors.isEmpty()){
            colors.clear();
        }
        generateRandomSitesAndColors();
        generateVoronoi();


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
        for(int x = 0; x < maxCol; x++){
            for(int y = 0; y < maxRow; y++){
                map[x][y] = new Node(x,y);
            }
        }
        for(int x = 0; x < maxCol; x++){
            for(int y = 0; y < maxRow; y++){
                map[x][y].getBoundingBox().setPosition(x * Gdx.graphics.getWidth() * 0.004f,
                    y * Gdx.graphics.getHeight() * 0.0075f);
                map[x][y].getBoundingBox().setSize(Gdx.graphics.getWidth() * 0.004f, Gdx.graphics.getHeight() * 0.075f);
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
                    //System.out.println("Room placed");
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

    public void setStartNodesDLA(){
        int centerX = currentCol / 2;
        int centerY = currentRow / 2;
        map[centerX][centerY].setCaveDLA(true);
    }

    public void setStartNodesDLA(int numberOfStartNodes){
        if(numberOfStartNodes == 1){
            int x = currentCol / 2;
            int y = currentRow / 2;
            map[0][y].setCaveDLA(true);
            map[currentCol - 1][y].setCaveDLA(true);
            map[x][0].setCaveDLA(true);
            map[x][currentRow - 1].setCaveDLA(true);
        }
        else{
            int x = currentCol / numberOfStartNodes;
            int y = currentRow / numberOfStartNodes;
            for(int i = 1; i <= numberOfStartNodes; i++){
                map[0][Math.min(currentRow - 1, (y * i) - (y / 2))].setCaveDLA(true);
                map[currentCol - 1][Math.min(currentRow - 1, (y * i) - (y / 2))].setCaveDLA(true);
                map[Math.min((x * i) - (x / 2), currentCol - 1)][0].setCaveDLA(true);
                map[Math.min((x * i) - (x / 2), currentCol - 1)][currentRow - 1].setCaveDLA(true);
            }
        }

    }

    public void walk(int numberOfWalkers, float stickiness){
        int x;
        int y;
        int moveX;
        int moveY;
        for(int i = 0; i < numberOfWalkers; i++) {
            do {
                x = PRNG.distinctRandom.nextInt(0, currentCol);
                y = PRNG.distinctRandom.nextInt(0, currentRow);
            } while (map[x][y].isCaveDLA());
            map[x][y].setWalker(true);
            int tries = 0;
            while(tries < 10000){
                if((map[Math.max(0, x-1)][y].isCaveDLA() || map[Math.min(x+1, currentCol - 1)][y].isCaveDLA()
                    || map[x][Math.max(0, y-1 )].isCaveDLA() || map[x][Math.min(y+1, currentRow - 1)].isCaveDLA())
                    && PRNG.distinctRandom.nextFloat() <= stickiness){
                    map[x][y].setWalker(false);
                    map[x][y].setCaveDLA(true);
                    break;
                }
                do {
                    moveX = PRNG.distinctRandom.nextInt(-1,2);
                    moveY = PRNG.distinctRandom.nextInt(-1, 2);

                } while (!((x + moveX) >= 0 && (x + moveX) < currentCol && (y + moveY) >= 0 && (y + moveY) < currentRow));

                if(!map[x+moveX][y+moveY].isCaveDLA()){
                    map[x][y].setWalker(false);
                    x += moveX;
                    y += moveY;
                    map[x][y].setWalker(true);
                }
                tries++;
            }
        }
        for(int i = 0; i < map.length; i++){
            for(int j = 0; j < map[0].length; j++){
                if(map[i][j].isWalker()) map[i][j].setWalker(false);
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
     * This method calculates costs of the Node object that is passed as an argument. G cost is distance from START node
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
     * @param node
     */
    public void openNode(Node node){
        if(!node.isOpen() && !node.isChecked() && !node.isSolid()){
            node.setAsOpen();
            node.setNodeParent(currentNode);
            openList.add(node);
        }
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

            //if(current.getNodeParent() != null)System.out.println(current.getNodeParent());
            //else System.out.println("Parent is null");
        }
        //System.out.println("track the path finished");
    }

    public void autoSearch(){
        while(!goalReached){

            int col = currentNode.getCol();
            int row = currentNode.getRow();

            currentNode.setAsChecked();
            checkedList.add(currentNode);
            openList.remove(currentNode);

            //Check node in UP, DOWN, LEFT, RIGHT
            if(row - 1 >= 0){
                openNode(map[col][row-1]);
            }
            if(col-1 >= 0){
                openNode(map[col-1][row]);

            }
            if(row + 1 < currentRow){
                openNode(map[col][row+1]);

            }
            if(col + 1 < currentCol){
                openNode(map[col+1][row]);
            }

            //Find the best node
            int bestNodeIndex = 0;
            int bestNodeFCost = 99999;

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

            if(openList.isEmpty()){
                break;
            }

            currentNode = openList.get(bestNodeIndex);
            if(currentNode == goalNode){
                goalReached = true;
                startNode.setConnected(true);
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

    /*
    This part of the code is used for Cellular Automata map generation
     */


    public void randomiseMapCA(int percentage){
        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                if(percentage >= PRNG.distinctRandom.nextInt(1, 100)){
                    map[i][j].setWallCA(true);
                }
                else{
                    map[i][j].setEmptyCA(true);
                }

            }
        }
    }

    public int countNeighbors(Node[][] mapCopy, int x, int y){
        int n = 0;
        for(int ty = -1; ty <= 1; ty++ ){
            for(int tx = -1; tx <= 1; tx++){
                if(!(ty == 0 && tx == 0)){
                    if( x + tx >= 0 && x + tx < currentCol && y + ty >= 0 && y + ty < currentRow){
                        if(mapCopy[x + tx][y+ ty].isWallCA()){
                            n++;
                        }
                    }
                }
            }
        }
        return n;
    }

    public void iterateCA(int iterations){
        for(int k = 0; k < iterations; k++){
            Node[][] mapCopy = new Node[currentCol][currentRow];
            for(int i = 0; i < currentCol; i++){
                for(int j = 0; j < currentRow; j++){
                    mapCopy[i][j] = map[i][j].clone();
                }
            }
            for(int x = 0; x < currentCol; x++){
                for(int y = 0; y < currentRow; y++){
                    int neighbors = countNeighbors(mapCopy, x, y);
                    if(neighbors == 0){
                        map[x][y].setWallCA(false);
                    }
                    else map[x][y].setWallCA(neighbors < 5);
                }
            }
        }


    }

    //Perlin Noise Methods
    private void initializePermutations(){
        for(int i = 0; i < 256; i++){
            PerlinPermutations[i] = i;
        }

        //Shuffling of the array
        for(int i = 0; i < 256; i++){
            int j = PRNG.distinctRandom.nextInt(256);
            int temp = PerlinPermutations[i];
            PerlinPermutations[i] = PerlinPermutations[j];
            PerlinPermutations[j] = temp;
        }

        //Duplication of the array
        for(int i = 0; i < 256; i++){
            PerlinPermutations[i + 256] = PerlinPermutations[i];
        }

    }

    private float fade(float t){
        return t * t * t * (t * (t * 6 - 15) + 10);
    }

    private float lerp(float t, float a, float b){
        return a + t * (b - a);
    }

    private float grad(int hash, float x, float y, float z){
        int h = hash & 15; // Convert low 4 bits of hash code into 12 gradient directions
        float u = h < 8 ? x : y;
        float v = h < 4 ? y : h == 12 || h == 14 ? x : z;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? v  : -v);
    }

    public float noise(float x, float y, float z){
        int X = (int) Math.floor(x) & 255;
        int Y = (int) Math.floor(y) & 255;
        int Z = (int) Math.floor(z) & 255;

        x -= (float) Math.floor(x);
        y -= (float) Math.floor(y);
        z -= (float) Math.floor(z);

        float u = fade(x);
        float v = fade(y);
        float w = fade(z);

        int A = PerlinPermutations[X] + Y, AA = PerlinPermutations[A] + Z, AB = PerlinPermutations[A + 1] + Z;
        int B = PerlinPermutations[X + 1] + Y, BA = PerlinPermutations[B] + Z, BB = PerlinPermutations[B + 1] + Z;

        return lerp(w, lerp(v, lerp(u, grad(PerlinPermutations[AA], x, y, z),
                    grad(PerlinPermutations[BA], x - 1, y, z)),
                lerp(u, grad(PerlinPermutations[AB], x, y - 1, z),
                    grad(PerlinPermutations[BB], x - 1, y - 1, z))),
            lerp(v, lerp(u, grad(PerlinPermutations[AA + 1], x, y, z - 1),
                    grad(PerlinPermutations[BA + 1], x - 1, y, z - 1)),
                lerp(u, grad(PerlinPermutations[AB + 1], x, y - 1, z - 1),
                    grad(PerlinPermutations[BB + 1], x - 1, y - 1, z - 1))));

    }

    public float generatePerlinNoise(float x, float y){
        float total = 0.0f;
        float frequency = 1.0f;
        float amplitude = 1.0f;

        for(int i = 0; i < Octaves; i++){
            total += noise(x * frequency, y * frequency, 0) * amplitude;
            frequency *= 2;
            amplitude *= Persistence;
        }

        return total;
    }

    public void PerlinGeneration(){
        float[][] noiseMap = new float[currentCol][currentRow];


        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                float x = (float) i / currentCol;
                float y = (float) j / currentRow;
                noiseMap[i][j] = generatePerlinNoise(x, y);
            }
        }

        float minValue = Float.MAX_VALUE;
        float maxValue = Float.MIN_VALUE;

        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                float value = noiseMap[i][j];
                if(value < minValue){
                    minValue = value;
                }
                if(value > maxValue){
                    maxValue = value;
                }
            }
        }

        float normalizedRange = 7.0f;

        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                float normalizedValue = ((noiseMap[i][j]- minValue) / (maxValue - minValue)) * normalizedRange;
                noiseMap[i][j] = normalizedValue;
            }
        }

        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                map[i][j].setPerlinType(Node.PerlinHeight.values()[(int) noiseMap[i][j]]);
            }
        }
    }

    // Voronoi Methods
    private void generateRandomSitesAndColors(){
        Set<Vector2> uniqueSites = new HashSet<>(NUM_SITES);
        Set<Color> uniqueColors = new HashSet<>(NUM_SITES);
        Vector2 tempVector2 = new Vector2();
        Color tempColor = new Color();

        while (uniqueSites.size() < NUM_SITES){
            tempVector2.set(PRNG.distinctRandom.nextInt(currentCol), PRNG.distinctRandom.nextInt(currentRow));
            tempColor.set(PRNG.distinctRandom.nextFloat(1.0f),
                PRNG.distinctRandom.nextFloat(1.0f),
                PRNG.distinctRandom.nextFloat(1.0f), 1.0f);
            if(!uniqueSites.contains(tempVector2)){
                uniqueSites.add(new Vector2(tempVector2));
                sites.add(new Vector2(tempVector2));
            }
            if(!uniqueColors.contains(tempColor)){
                uniqueColors.add(new Color(tempColor));
                colors.add(new Color(tempColor));
            }

        }

    }

    private void findClosestNode(Node currentNode){
        int closestNodeIndex = Integer.MIN_VALUE;
        int minDistance = Integer.MAX_VALUE;

        for(int i = 0; i < sites.size(); i++){
            int distance = calculateDistance(new Vector2(currentNode.getCol(), currentNode.getRow()), sites.get(i));
            if(distance < minDistance){
                minDistance = distance;
                closestNodeIndex = i;
            }

            currentNode.setColor(colors.get(closestNodeIndex));
        }

    }

    private int calculateDistance(Vector2 currentNode, Vector2 site){
        int deltaX = (int) (site.x - currentNode.x);
        int deltaY = (int) (site.y - currentNode.y);
        return (int) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    private void generateVoronoi(){
        for(int i = 0; i < currentCol; i++){
            for(int j = 0; j < currentRow; j++){
                findClosestNode(map[i][j]);
            }
        }
    }








}
