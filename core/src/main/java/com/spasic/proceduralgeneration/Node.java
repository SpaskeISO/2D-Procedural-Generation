package com.spasic.proceduralgeneration;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)

public class Node{

    //@ToString.Include
    private Node nodeParent;
    private String text;
    private Rectangle boundingBox;
    // Location
    @ToString.Include
     private Integer col;
    @ToString.Include
    private Integer row;
    // A* pathfinding parameters
    private Integer gCost;
    private Integer hCost;
    private Integer fCost;
    private boolean start;
    private boolean goal;
    private boolean room = false;
    private boolean entrance = false;
    private boolean wall;
    private boolean solid;
    private boolean path = false;
    private boolean open;
    private boolean checked;
    private boolean connected = false;

    //DLA parameters

    private boolean walker = false;
    private boolean caveDLA = false;

    //CA parameters
    private boolean wallCA;
    private boolean emptyCA;


    @ToString.Include
    private Color color;
    private Color foregroundColor;



    public Node(Integer col, Integer row){
        boundingBox = new Rectangle();
        this.col = col;
        this.row = row;
        color = Color.WHITE;
        foregroundColor = Color.BLACK;
        this.text = "";

    }

    /*public Node(Node copy){
        this.boundingBox = copy.boundingBox;
        this.col = copy.col;
        this.row = copy.row;
        this.color = copy.color;
        this.foregroundColor = copy.foregroundColor;
        this.text = copy.text;

        this.gCost = copy.gCost;
        this.hCost = copy.hCost;
        this.fCost = copy.fCost;
        this.start = copy.start;
        this.goal = copy.goal;
        this.room = copy.room;
        this.entrance = copy.entrance;
        this.wall = copy.wall;
        this.solid = copy. solid;
        this.path = copy.path;
        this.open = copy.open;
        this.checked = copy.checked;
        this.connected = copy. connected;

        //DLA parameters

        this.walker = copy.walker;
        this.caveDLA = copy.caveDLA;

        //CA parameters
        this.wallCA = copy.wallCA;
        this.emptyCA = copy.emptyCA;
    }*/


    public void setAsStart(){
        setColor(Color.BLUE);
        setForegroundColor(Color.WHITE);
        setText("Start");
        setStart(true);
    }

    public void setAsGoal(){
        setColor(Color.YELLOW);
        setForegroundColor(Color.BLACK);
        setText("Goal");
        setGoal(true);
    }

    public void setAsSolid(){
        setColor(Color.BLACK);
        setForegroundColor(Color.BLACK);
        setText("");
        setSolid(true);
    }

    public void setAsWall(){
        setColor(Color.PURPLE);
        setForegroundColor(Color.BLACK);
        setText("");
        setWall(true);
        setRoom(true);
        setSolid(true);
    }

    public void setAsRoom(){
        setColor(Color.RED);
        setForegroundColor(Color.BLACK);
        setText("");
        setRoom(true);
        setSolid(true);
    }

    public void setAsEntrance(){
        setColor(Color.CYAN);
        setForegroundColor(Color.BLACK);
        setText("");
        setSolid(false);
        setRoom(false);
        setEntrance(true);
    }

    public void setAsOpen(){
        setOpen(true);
        //setColor(Color.MAGENTA);
        if(!goal && !start){
            setColor(Color.GREEN);
        }
    }
    public void setAsChecked(){
        if(!start && !goal && !path){
            color = Color.ORANGE;
            foregroundColor = Color.BLACK;
        }
        checked = true;
    }

    public void setAsNotChecked(){
        if(!start && !goal && !connected && !path && !solid){
            color = Color.WHITE;
            foregroundColor = Color.BLACK;
        }
        checked = false;
    }

    public void setAsNotOpen(){
        setOpen(false);
    }

    public void setAsPath(){
        if(!goal){
            color = Color.GREEN;
        }
        foregroundColor = Color.BLACK;
        setPath(true);
        //System.out.println(this);
    }

    public void setCaveDLA(boolean caveDLA){
        if(caveDLA){
            setWalker(false);
            color = Color.BLUE;
            this.caveDLA = true;
        }
        else{
            color = Color.WHITE;
            this.caveDLA = false;
        }
    }

    public void setWalker(boolean walker){
        if(walker){
            color = Color.BLACK;
            this.walker = true;
        }
        else{
            color = Color.WHITE;
            this.walker = false;
        }
    }

    public void setWallCA(boolean wallCA){
        if(wallCA){
            color = Color.BLACK;
            this.wallCA = true;
        }
        else{
            this.wallCA = false;
        }
    }

    public void setEmptyCA(boolean emptyCA){
        if(emptyCA){
            color = Color.WHITE;
            this.emptyCA = true;
        }
        else{
            this.emptyCA = false;
        }
    }


}
