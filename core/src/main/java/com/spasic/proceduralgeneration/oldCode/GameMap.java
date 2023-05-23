package com.spasic.proceduralgeneration.oldCode;

import com.spasic.proceduralgeneration.AnsiiEscapeColors;

public class GameMap{


    public static void main(String[] args) {
        int mapWidth =120,mapLength = 120;
        Room currentRoom;
        String map [][] = new String[mapWidth][mapLength];
        int numberOfR = 0;
        //int numberOfRooms = ThreadLocalRandom.current().nextInt(8,16);
        int numberOfRooms = 15;
        int addedRooms = 0;
        for(int i = 0; i < mapWidth; i++){
            for(int j = 0; j < mapLength; j++){
                map[i][j] = "#";
            }
            System.out.println();
        }
        for(int i = 0; i < 120; i++){
            for(int j = 0; j < 120; j++){
                System.out.print(map[i][j]);
            }
            System.out.println();
        }
        while(addedRooms < numberOfRooms){
            boolean noCollsions = false;
            PRNG.random.setSeed(System.currentTimeMillis());
            currentRoom = new Room();
            System.out.println(currentRoom);
            while(!noCollsions){
                System.out.println(currentRoom);
                if(checkColision(currentRoom, map)){
                    noCollsions = true;
                    for(int i = currentRoom.x; i < currentRoom.x+currentRoom.width; i++){
                        System.out.println("usao u prvu petlju");
                        for(int j = currentRoom.y; j < currentRoom.y+currentRoom.length; j++){
                            map[i][j] = "R";
                            numberOfR++;
                        }
                    }
                    switch (currentRoom.entrance1){
                        case NORTH:
                            map[currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.y] = "E";
                            break;
                        case SOUTH:
                            map[currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.y + currentRoom.length - 1] = "E";
                            break;
                        case EAST:
                            map[currentRoom.x][currentRoom.y + PRNG.random.nextInt(1, currentRoom.length - 1)] = "E";
                            break;
                        case WEST:
                            map[currentRoom.x + currentRoom.width - 1][currentRoom.y + PRNG.random.nextInt(1, currentRoom.width - 1)] = "E";
                            break;
                    }
                    if(currentRoom.entrance2 != null){
                        switch (currentRoom.entrance2){
                            case NORTH:
                                map[currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.y] = "E";
                                break;
                            case SOUTH:
                                map[currentRoom.x + PRNG.random.nextInt(1, currentRoom.width - 1)][currentRoom.y + currentRoom.length - 1] = "E";
                                break;
                            case EAST:
                                map[currentRoom.x][currentRoom.y + PRNG.random.nextInt(1, currentRoom.length - 1)] = "E";
                                break;
                            case WEST:
                                map[currentRoom.x + currentRoom.width - 1][currentRoom.y + PRNG.random.nextInt(1, currentRoom.width - 1)] = "E";
                                break;
                        }
                    }

                    addedRooms++;
                    System.out.println("Room placed");
                }
                currentRoom = new Room();
            }

        }
        for(int i = 0; i < mapWidth; i++){
            for(int j = 0; j < mapLength; j++){
                if(map[i][j].contains("#")){
                    System.out.print(map[i][j]);
                } else if(map[i][j].contains("R")) {
                    System.out.print(AnsiiEscapeColors.ANSI_RED + map[i][j] + AnsiiEscapeColors.ANSI_RESET);
                }
                else if(map[i][j].contains("E")){
                    System.out.print(AnsiiEscapeColors.ANSI_CYAN + map[i][j] + AnsiiEscapeColors.ANSI_RESET);
                }

            }
            System.out.println();
        }
        System.out.println(numberOfR);




    }

    public static boolean checkColision(Room currentRoom, String[][] map){
        if(currentRoom.y + currentRoom.length > map.length || currentRoom.x + currentRoom.width > map.length || currentRoom.y - 1 < 0 || currentRoom.x - 1 < 0){
            System.out.println("Out of bounds");
            return false;
        }
        for(int i = Math.max(currentRoom.x - 2, 0); i < Math.min(currentRoom.width+currentRoom.x + 2, 120); i++){
            for(int j = Math.max(currentRoom.y - 2, 0); j < Math.min(currentRoom.length+ currentRoom.y + 2,120); j++){
                if(map[i][j].contains("R")){
                    System.out.println("Collision");
                    return false;
                }
            }
        }

        System.out.println("No collisions");
        return true;
    }






}
