package com.spasic.proceduralgeneration.oldCode2;

import com.spasic.proceduralgeneration.CardinalDirections;
import com.spasic.proceduralgeneration.PRNG;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class NodeRoom {
    public static int id = 0;

    public int width;
    public int length;
    public int x;
    public int y;

    public CardinalDirections entrance1, entrance2;
    public NodeRoom(){
        length = PRNG.distinctRandom.nextInt(5, 15);
        width = PRNG.distinctRandom.nextInt(10, 20);
        x = PRNG.distinctRandom.nextInt( GameMapNodes.mapWidth);
        y = PRNG.distinctRandom.nextInt( GameMapNodes.mapLength);
        if(id == 0){
            entrance1 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
        }
        else{
            entrance1 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
            entrance2 = CardinalDirections.values()[PRNG.distinctRandom.nextInt(3)];
        }
        id++;

    }

    public boolean checkCollision(Node map[][]){
        if(this.y + this.length > GameMapNodes.mapLength - 1 || this.x + this.width > GameMapNodes.mapWidth - 1 || this.y - 1 < 0 || this.x - 1 < 0){
            System.out.println("Out of bounds");
            return false;
        }

        for(int i = Math.max(this.y - 2, 0); i < Math.min(this.length + this.y + 2, GameMapNodes.mapLength); i++){
            for(int j = Math.max(this.x - 2, 0); j < Math.min(this.width + this.x + 2, GameMapNodes.mapWidth); j++){
                if(map[i][j].type == NodeType.ROOM){
                    System.out.println("Collision");
                    return false;
                }
            }
        }
        return true;
    }



}
