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
import com.badlogic.gdx.utils.Align;
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

    //DLA
    public static int minNumberOfWalkers = minCol * minRow;
    public static int maxNumberOfWalkers = col * row;
    public static int numberOfWalkers = 1700;
    public static float minStickiness = 0.1f;
    public static float maxStickiness = 1f;
    public static float stickiness = 0.5f;// min 0.1 max 1
    public static float minEdgeStartNumber = 1.0f;
    public static float maxEdgeStartNumber = 10.0f;
    public static float edgeStartNumber = 4.0f;


    //Node
    private Node[][] map;
    private Node[][] mapTest;


    //UI
    private Group generalSettingsGroup;
    private Group DLAGroup;

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
        createGeneralSettingsUI();
        createDLAUI();



        resizeUI();

    }

    public void createGeneralSettingsUI(){
        generalSettingsGroup = new Group();

        // General settings label
        final Label generalSettingsLabel = new Label("General Settings:", skin);
        /*generalSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);*/

        // Columns Label
        final Label colLabel = new Label( "Columns: " + col, skin);
        /*colLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);*/
        colLabel.setName("colLabel");

        // Columns Slider
        final Slider colSlider = new Slider( (float)minCol, (float)maxCol, 1.0f, false,skin);
        /*colSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth()* 0.1f, Gdx.graphics.getHeight() * 0.03f);*/
        colSlider.setValue((float)maxCol);
        colSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                col = (int)colSlider.getValue();
                colLabel.setText("Columns: " + col);
                DLAScrollerDynamicNumbers();
            }
        });
        colSlider.setName("colSlider");

        // Row Label
        final Label rowLabel = new Label("Rows: " + row, skin);
        /*rowLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() *  0.89f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);*/
        rowLabel.setName("rowLabel");

        // Row Slider
        final Slider rowSlider = new Slider( (float)minRow, (float)maxRow, 1.0f, false, skin);
        rowSlider.setValue((float)maxRow);
        rowSlider.addListener( new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                row = (int)rowSlider.getValue();
                rowLabel.setText("Rows: " + row);
                DLAScrollerDynamicNumbers();

            }
        });
        rowSlider.setName("rowSlider");

        final Label roomLabel = new Label("Rooms: " + numberOfRooms, skin);
        roomLabel.setName("roomLabel");

        // Rooms Slider
        final Slider roomSlider = new Slider((float)minNumberOfRooms, (float)maxNumberOfRooms, 1.0f, false, skin);
        roomSlider.setValue((float)numberOfRooms);
        roomSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                numberOfRooms = (int)roomSlider.getValue();
                roomLabel.setText("Rooms: " + numberOfRooms);
            }
        });
        roomSlider.setName("roomSlider");

        final Label generationSelectLabel = new Label("Generation Selection:", skin);

        // SelectBox for choosing witch dungeon algorithm to use
        final SelectBox<DungeonGenerator.dungeonType> generationSelectBox = new SelectBox<DungeonGenerator.dungeonType>(skin, "mine");
        Array<DungeonGenerator.dungeonType> items = new Array<>();
        for(DungeonGenerator.dungeonType type : DungeonGenerator.dungeonType.values()){
            items.add(type);
        }
        generationSelectBox.setItems(items);
        generationSelectBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                dungeonGenerator.setType(generationSelectBox.getSelected());
                /*switch (generationSelectBox.getSelected()){
                }*/
            }
        });

        //Generate button
        final TextButton generateButton = new TextButton("Generate", skin);
        generateButton.setName("generateBtn");
        generateButton.addListener( new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Arrays.fill(map, null);
                if(generationSelectBox.getSelected() == DungeonGenerator.dungeonType.SRP){
                    map = dungeonGenerator.generateDungeonSRP(col, row, numberOfRooms);
                }
                else if(generationSelectBox.getSelected() == DungeonGenerator.dungeonType.DLA){
                    CheckBox temp = (CheckBox) DLAGroup.getChild(5);
                    if(temp.isChecked()){
                        map = dungeonGenerator.generateDungeonDLA(col, row, numberOfWalkers, stickiness, (int) edgeStartNumber);

                    }
                    else{
                        map = dungeonGenerator.generateDungeonDLA(col, row, numberOfWalkers, stickiness);
                    }

                }

            }
        });


        generalSettingsGroup.addActor(generateButton);
        generalSettingsGroup.addActor(generalSettingsLabel);
        generalSettingsGroup.addActor(colSlider);
        generalSettingsGroup.addActor(colLabel);
        generalSettingsGroup.addActor(rowSlider);
        generalSettingsGroup.addActor(rowLabel);
        generalSettingsGroup.addActor(roomSlider);
        generalSettingsGroup.addActor(roomLabel);
        generalSettingsGroup.addActor(generationSelectLabel);
        generalSettingsGroup.addActor(generationSelectBox);

        stage.addActor(generalSettingsGroup);
    }

    public void createDLAUI(){
        DLAGroup = new Group();



        final Label numberOfWalkersLabel = new Label("Walkers: " + numberOfWalkers, skin);

        final Slider numberOfWalkersSlider = new Slider(minNumberOfWalkers, maxNumberOfWalkers, 10.0f, false, skin);
        numberOfWalkersSlider.setValue(numberOfWalkers);
        numberOfWalkersSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                numberOfWalkers = (int) Math.floor(numberOfWalkersSlider.getValue());
                numberOfWalkersLabel.setText("Walkers: " + numberOfWalkers);
            }
        });

        final Label DLASettingsLabel = new Label("DLA Settings: ", skin);

        final Label stickinessLabel = new Label(String.format("Stickiness: %.2f", stickiness), skin);

        final Slider stickinessSlider = new Slider(minStickiness, maxStickiness, 0.01f, false, skin);
        stickinessSlider.setValue(stickiness);
        stickinessSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                stickiness = stickinessSlider.getValue();
                stickinessLabel.setText(String.format("Stickiness: %.2f", stickiness));
            }
        });

        //Check box that changes starting points for the DLA
        final CheckBox edgeCheckBox = new CheckBox("Edge Start", skin);
        edgeCheckBox.setChecked(false);
        edgeCheckBox.align(Align.left);
        edgeCheckBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.graphics.setContinuousRendering(edgeCheckBox.isChecked());
            }
        });

        final Label edgePointNumberLabel = new Label(String.format("Edge point numbers: %.0f", edgeStartNumber), skin);

        final Slider edgePointsNumberSlider = new Slider(minEdgeStartNumber, maxEdgeStartNumber, 1.0f, false, skin);
        edgePointsNumberSlider.setValue(edgeStartNumber);
        edgePointsNumberSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                edgeStartNumber = edgePointsNumberSlider.getValue();
                edgePointNumberLabel.setText(String.format("Edge point numbers: %.0f", edgeStartNumber));
            }
        });

        DLAGroup.addActor(DLASettingsLabel);
        DLAGroup.addActor(numberOfWalkersLabel);
        DLAGroup.addActor(numberOfWalkersSlider);
        DLAGroup.addActor(stickinessLabel);
        DLAGroup.addActor(stickinessSlider);
        DLAGroup.addActor(edgeCheckBox);
        DLAGroup.addActor(edgePointNumberLabel);
        DLAGroup.addActor(edgePointsNumberSlider);

        stage.addActor(DLAGroup);
    }

    private void resizeUI(){

        Button generateButton = (Button) generalSettingsGroup.getChild(0);
        Label generalSettingsLabel = (Label) generalSettingsGroup.getChild(1);
        Slider colSlider = (Slider) generalSettingsGroup.getChild(2);
        Label colLabel = (Label) generalSettingsGroup.getChild(3);
        Slider rowSlider = (Slider) generalSettingsGroup.getChild(4);
        Label rowLabel = (Label) generalSettingsGroup.getChild(5);
        Slider roomSlider = (Slider) generalSettingsGroup.getChild(6);
        Label roomLabel = (Label) generalSettingsGroup.getChild(7);
        Label generationSelectLabel = (Label) generalSettingsGroup.getChild(8);
        Actor generationSelectBox = generalSettingsGroup.getChild(9);

        Label DLASettingsLabel = (Label) DLAGroup.getChild(0);
        Label numberOfWalkersLabel = (Label) DLAGroup.getChild(1);
        Slider numberOfWalkersSlider = (Slider) DLAGroup.getChild(2);
        Label stickinessLabel = (Label) DLAGroup.getChild(3);
        Slider stickinessSlider = (Slider) DLAGroup.getChild(4);
        CheckBox edgeCheckBox = (CheckBox) DLAGroup.getChild(5);
        Label edgePointNumberLabel = (Label) DLAGroup.getChild(6);
        Slider edgePointsNumberSlider = (Slider) DLAGroup.getChild(7);


        //General Settings
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

        generationSelectLabel.setBounds(Gdx.graphics.getWidth() * 0.62f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        generationSelectBox.setBounds(Gdx.graphics.getWidth() * 0.62f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        //DLA Settings
        DLASettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.79f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        numberOfWalkersLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        numberOfWalkersSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        stickinessLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        stickinessSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        edgeCheckBox.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.70f,
            Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.03f);

        edgePointNumberLabel.setBounds(Gdx.graphics.getWidth() * 0.86f, Gdx.graphics.getHeight() * 0.67f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        edgePointsNumberSlider.setBounds(Gdx.graphics.getWidth() * 0.75f, Gdx.graphics.getHeight() * 0.67f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

    }

    private void DLAScrollerDynamicNumbers(){
        if(col * row == 200){
            minNumberOfWalkers = 0;
            maxNumberOfWalkers = 199;
        }
        else {
            minNumberOfWalkers = minCol * minRow;
            maxNumberOfWalkers = col * row - 1;
        }
        Slider slider = (Slider) DLAGroup.getChild(2);
        slider.setRange(minNumberOfWalkers, maxNumberOfWalkers);
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
