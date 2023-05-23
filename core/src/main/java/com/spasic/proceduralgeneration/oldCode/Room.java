package com.spasic.proceduralgeneration.oldCode;


public class Room {

        public static int counter = 0;
        public int width;
        public int length;
        public int x;
        public int y;
        public enum cardinalDirections { NORTH, SOUTH, EAST, WEST }
        public cardinalDirections entrance1, entrance2;

        public Room(){

            length =  PRNG.random.nextInt(10, 20 + 1);
            width = PRNG.random.nextInt(5, 10 + 1);
            x = PRNG.random.nextInt(0, 120 - width);
            y = PRNG.random.nextInt(0, 120 - length);

            if(counter < 1){
                entrance1 = cardinalDirections.values()[PRNG.random.nextInt(cardinalDirections.values().length)];
            }
            else{
                entrance1 = cardinalDirections.values()[PRNG.random.nextInt(cardinalDirections.values().length)];
                entrance2 = cardinalDirections.values()[PRNG.random.nextInt(cardinalDirections.values().length)];
            }

            counter++;

        }

        @Override
        public String toString() {
            return "room{" +
                    "width=" + width +
                    ", length=" + length +
                    ", x=" + x +
                    ", y=" + y +
                    ", entrance1=" + entrance1 +
                    ", entrance2=" + entrance2 +
                    '}';
        }
    }

