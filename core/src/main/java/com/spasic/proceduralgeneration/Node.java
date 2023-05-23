package com.spasic.proceduralgeneration;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
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
    private Rectangle boundigbox;
    @ToString.Include
     private Integer col;
    @ToString.Include
    private Integer row;
    @ToString.Include
    private Integer gCost;
    @ToString.Include
    private Integer hCost;
    @ToString.Include
    private Integer fCost;
    @ToString.Include
    private boolean start;
    @ToString.Include
    private boolean goal;
    @ToString.Include
    private boolean room = false;
    @ToString.Include
    private boolean entrance = false;
    @ToString.Include
    private boolean wall;
    @ToString.Include
    private boolean solid;
    @ToString.Include
    private boolean path = false;
    @ToString.Include
    private boolean open;
    @ToString.Include
    private boolean checked;
    @ToString.Include
    private boolean connected = false;

    private Color color;
    private Color foregroundColor;
    //private Rectangle boundingBox;

    public Node(Integer col, Integer row){
        boundigbox = new Rectangle();
        this.col = col;
        this.row = row;
        color = Color.WHITE;
        foregroundColor = Color.BLACK;
        this.text = "";

    }


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
        if(goal == false && start == false){
            setColor(Color.GREEN);
        }
    }
    public void setAsChecked(){
        if(start == false && goal == false && path == false){
            color = Color.ORANGE;
            foregroundColor = Color.BLACK;
        }
        checked = true;
    }

    public void setAsNotChecked(){
        if(start == false && goal == false && connected == false && path == false && solid == false){
            color = Color.WHITE;
            foregroundColor = Color.BLACK;
        }
        checked = false;
    }

    public void setAsNotOpen(){
        setOpen(false);
    }

    public void setAsPath(){
        if(goal == false){
            color = Color.GREEN;
        }
        foregroundColor = Color.BLACK;
        setPath(true);
        System.out.println(this);
    }
}
