package com.spasic.proceduralgeneration;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import lombok.Getter;
import lombok.Setter;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
@Getter
@Setter
public class ProceduralGeneration2D extends ApplicationAdapter {
    //LibGDX
    private SpriteBatch batch;
    private Skin skin;
    private Stage stage;

    private StringBuilder stringBuilder;

    private final float SCREEN_WIDTH = 1600;
    private final float SCREEN_HEIGHT = 900;

    private Camera camera;
    private Viewport viewport;

    private DungeonGenerator dungeonGenerator;

    private FreeTypeFontGenerator freeTypeFontGenerator;
    private BitmapFont font;

    //Map
    public static int maxCol = 80;
    public static int maxRow = 40;
    public static int minCol = 20;
    public static int minRow = 10;
    public static int maxNumberOfRooms = 20;
    public static int minNumberOfRooms = 2;
    public static int numberOfRooms = 10;
    public static int col = maxCol;
    public static int row = maxRow;

    //Node
    private Node[][] map;
    private Node[][] mapTest;


    //UI
    private Group group;

    private ShapeRenderer shapeRenderer;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        viewport = new ScreenViewport(camera);

        freeTypeFontGenerator = new FreeTypeFontGenerator(Gdx.files.internal("VCR_OSD_MONO_1.001.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 20;
        font = freeTypeFontGenerator.generateFont(parameter);



        skin = new Skin(Gdx.files.internal("cloud-form/skin/cloud-form-ui.json"));
        stage = new Stage(viewport);
        createUI();

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();

        shapeRenderer.setProjectionMatrix(camera.combined);

        Gdx.input.setInputProcessor(stage);
        dungeonGenerator = new DungeonGenerator(DungeonGenerator.dungeonType.SRP, col, row);
        map = dungeonGenerator.generateBlankMap(col, row);


    }

    @Override
    public void render() {
        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClearColor(Color.SKY.r, Color.SKY.g, Color.SKY.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        keyInput();
        batch.begin();
        batch.end();
        drawNodes();


        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();

    }

    @Override
    public void dispose() {
        batch.dispose();
    }

    private void copyMap(){
        mapTest =  new Node[col][];
        for(int i = 0; i < map.length; i++)
            mapTest[i] = map[i].clone();
    }


    public void drawNodes(){
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.SLATE);
        shapeRenderer.rect(0, 0, 81 * map[0][0].getBoundigbox().getWidth(),
            41 * map[0][0].getBoundigbox().getHeight());
        shapeRenderer.end();

        for(int x = 0; x < map.length; x++){
            for(int y = 0; y < map[x].length; y++){
                shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
                shapeRenderer.setColor(map[x][y].getColor());
                shapeRenderer.rect(map[x][y].getBoundigbox().getX(), map[x][y].getBoundigbox().getY(),
                    map[x][y].getBoundigbox().getWidth(), map[x][y].getBoundigbox().getHeight());
                shapeRenderer.end();

                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.GRAY);
                shapeRenderer.rect(map[x][y].getBoundigbox().getX(), map[x][y].getBoundigbox().getY(),
                    map[x][y].getBoundigbox().getWidth(), map[x][y].getBoundigbox().getHeight());
                shapeRenderer.end();

            }
        }



    }

    public void keyInput(){

    }

    private void createUI(){
        group = new Group();

        //Generate button
        final TextButton generateButton = new TextButton("Generate", skin);
        generateButton.setBounds(Gdx.graphics.getWidth() * 0.83f, Gdx.graphics.getHeight() * 0.2f,
            Gdx.graphics.getWidth() * 0.06f, Gdx.graphics.getHeight() * 0.06f);
        generateButton.setName("generateBtn");
        generateButton.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Arrays.fill(map, null);
                map = dungeonGenerator.generateDungeon(col, row, numberOfRooms);
            }
        });

        // General settings label
        final Label generalSettingsLabel = new Label("General Settings", skin);
        generalSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);

        // Columns Label
        final Label colLabel = new Label( "Columns: " + col, skin);
        colLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        colLabel.setName("colLabel");

        // Columns Slider
        final Slider colSlider = new Slider( (float)minCol, (float)maxCol, 1.0f, false,skin);
        colSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth()* 0.1f, Gdx.graphics.getHeight() * 0.03f);
        colSlider.setValue((float)maxCol);
        colSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                col = (int)colSlider.getValue();
                colLabel.setText("Columns: " + col);
            }
        });
        colSlider.setName("colSlider");

        // Row Label
        final Label rowLabel = new Label("Rows: " + row, skin);
        rowLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() *  0.89f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        rowLabel.setName("rowLabel");

        // Row Slider
        final Slider rowSlider = new Slider( (float)minRow, (float)maxRow, 1.0f, false, skin);
        rowSlider.setValue((float)maxRow);
        rowSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.89f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        rowSlider.addListener( new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                row = (int)rowSlider.getValue();
                rowLabel.setText("Rows: " + row);
            }
        });
        rowSlider.setName("rowSlider");

        final Label roomLabel = new Label("Rooms: " + numberOfRooms, skin);
        roomLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() *  0.86f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        roomLabel.setName("roomLabel");

        // Rooms Slider
        final Slider roomSlider = new Slider((float)minNumberOfRooms, (float)maxNumberOfRooms, 1.0f, false, skin);
        roomSlider.setValue((float)numberOfRooms);
        roomSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.86f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        roomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                numberOfRooms = (int)roomSlider.getValue();
                roomLabel.setText("Rooms: " + numberOfRooms);
            }
        });
        roomSlider.setName("roomSlider");

        final Label generationSelectLabel = new Label("Generation Selection:", skin);
        generationSelectLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.74f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        // SelectBox for choosing witch dungeon algorithm to use
        final SelectBox<DungeonGenerator.dungeonType> generationSelectBox = new SelectBox<DungeonGenerator.dungeonType>(skin, "mine");
        Array<DungeonGenerator.dungeonType> items = new Array<>();
        for(DungeonGenerator.dungeonType type : DungeonGenerator.dungeonType.values()){
            items.add(type);
        }
        generationSelectBox.setItems(items);
        generationSelectBox.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.71f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);





        group.addActor(generateButton);
        group.addActor(generalSettingsLabel);
        group.addActor(colSlider);
        group.addActor(colLabel);
        group.addActor(rowSlider);
        group.addActor(rowLabel);
        group.addActor(roomSlider);
        group.addActor(roomLabel);
        group.addActor(generationSelectLabel);
        group.addActor(generationSelectBox);
        stage.addActor(group);
    }

    private void resizeUI(){
        Array<Actor> actors = group.getChildren();
        Button generateButton = (Button) actors.get(0);
        Label generalSettingsLabel = (Label) actors.get(1);
        Slider colSlider = (Slider) actors.get(2);
        Label colLabel = (Label) actors.get(3);
        Slider rowSlider = (Slider) actors.get(4);
        Label rowLabel = (Label) actors.get(5);
        Slider roomSlider = (Slider) actors.get(6);
        Label roomLabel = (Label) actors.get(7);
        Label generationSelectLabel = (Label) actors.get(8);
        Actor generationSelectBox = actors.get(9);


        generalSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        rowLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() *  0.89f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        rowSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.89f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        colSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth()* 0.1f, Gdx.graphics.getHeight() * 0.03f);
        colLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        generateButton.setBounds(Gdx.graphics.getWidth() * 0.83f, Gdx.graphics.getHeight() * 0.2f,
            Gdx.graphics.getWidth() * 0.06f, Gdx.graphics.getHeight() * 0.06f);
        roomSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.86f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        roomLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() *  0.86f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        generationSelectLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.74f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        generationSelectBox.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.71f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
    }

    private void resizeMap(){
        for(int x = 0; x < map.length; x++){
            for(int y = 0; y < map[x].length; y++){
                map[x][y].getBoundigbox().setPosition(x * Gdx.graphics.getWidth() * 0.008f,
                    y * Gdx.graphics.getHeight() * 0.015f);
                map[x][y].getBoundigbox().setSize(Gdx.graphics.getWidth() * 0.008f, Gdx.graphics.getHeight() * 0.015f);
            }
        }
        System.out.println("map resized");
    }

    @Override
    public void resize(int width, int height) {
        resizeUI();
        resizeMap();
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
        System.out.println(map[0][0].getBoundigbox());
    }


}
