package com.spasic.proceduralgeneration;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Rectangle;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(onlyExplicitlyIncluded = true)

public class Node implements Cloneable{

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

    //Perlin parameters
    public enum PerlinHeight { DEEP_WATER, WATER, BEACH, GRASSLANDS, FOREST, HILL, MOUNTAIN, SNOWY_MOUNTAIN }
    private PerlinHeight PerlinType;

    // Voronoi parameters
    private int closestNode;

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


    @Override
    protected Node clone(){
        try{
            Node cloned = (Node) super.clone();

            if(this.boundingBox != null){
                cloned.boundingBox = new Rectangle(this.boundingBox);
            }

            if(this.color != null){
                cloned.color = new Color(this.color);
            }

            if(this.foregroundColor != null){
                cloned.foregroundColor = new Color(this.foregroundColor);
            }

            cloned.nodeParent = null;

            return cloned;
        } catch (CloneNotSupportedException e){
            throw new AssertionError();
        }

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

    public void setWallCA(boolean bool){
        if(bool){
            color = Color.GREEN;
            this.wallCA = true;
        }
        else{
            this.wallCA = false;
            setEmptyCA(true);
        }
    }

    public void setEmptyCA(boolean emptyCA){
        if(emptyCA){
            color = Color.BROWN;
            this.emptyCA = true;
        }
        else{
            this.emptyCA = false;
        }
    }

    //Perlin Methods
    public void setPerlinType(PerlinHeight ph){
        switch (ph){
            case DEEP_WATER -> setColor(new Color(0.016f, 0.106f, 0.741f, 1.0f));
            case WATER -> setColor(new Color(0.318f, 0.486f, 0.839f, 1.0f));
            case BEACH -> setColor(new Color(1.0f, 0.973f, 0.58f, 1.0f));
            case GRASSLANDS -> setColor(new Color(0.302f, 0.949f, 0.439f, 1.0f));
            case FOREST -> setColor(new Color(0.055f, 0.51f, 0.153f, 1.0f));
            case HILL -> setColor(new Color(0.529f, 0.424f, 0.161f, 1.0f));
            case MOUNTAIN -> setColor(new Color(0.431f, 0.431f, 0.431f, 1.0f));
            case SNOWY_MOUNTAIN -> setColor(new Color(0.922f, 0.922f, 0.922f, 1.0f));
        }
    }


}
