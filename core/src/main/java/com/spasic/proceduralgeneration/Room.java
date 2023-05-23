package com.spasic.proceduralgeneration;


import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Room {

    public static int id = 0;
    public int x;
    public int y;
    public static int maxWidth = 40;
    public static int minWidth = 20;
    public static int maxLength = 30;
    public static int minLength = 15;
    public static int currentWidth = minWidth;
    public static int currentLength = minLength;
    public int width;
    public int length;
    public CardinalDirections entrance1, entrance2;

    public Room(){
        length = PRNG.distinctRandom.nextInt(5, currentWidth);
        width = PRNG.distinctRandom.nextInt(10, currentLength);
        x = PRNG.distinctRandom.nextInt( ProceduralGeneration2D.col);
        y = PRNG.distinctRandom.nextInt( ProceduralGeneration2D.row);
        if(id == 0){
            entrance1 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
        }
        else{
            entrance1 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
            entrance2 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
        }
    }

    public boolean checkCollision(Node Nodes[][]){
        if(this.x + this.width > ProceduralGeneration2D.col - 1 || this.y + this.length > ProceduralGeneration2D.row - 1 || this.x - 1 < 0 || this.y - 1 < 0){
            System.out.println("Out of bounds");
            return false;
        }

        for(int i = Math.max(this.x - 2, 0); i < Math.min(this.width + this.x + 2, ProceduralGeneration2D.col); i++){
            for(int j = Math.max(this.y - 2, 0); j < Math.min(this.length + this.y + 2, ProceduralGeneration2D.row); j++){
                if(Nodes[i][j].isRoom()){
                    System.out.println("Collision");
                    return false;
                }
            }
        }
        return true;
    }

}
