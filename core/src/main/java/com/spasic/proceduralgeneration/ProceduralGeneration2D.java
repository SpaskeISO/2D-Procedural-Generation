package com.spasic.proceduralgeneration;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.*;
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
    public static int maxCol = 400;
    public static int maxRow = 400;
    public static int minCol = 20;
    public static int minRow = 10;
    public static int maxNumberOfRooms = 20;
    public static int minNumberOfRooms = 2;
    public static int numberOfRooms = 10;
    public static int col = 160;
    public static int row = 80;

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

    //CA
    public static float maxCAIterations = 100;
    public static float minCAIterations = 1;
    public static float CAIterations = 5;
    public static float maxCAPercentage = 80;
    public static float minCAPercentage = 20;
    public static float CAPercentage = 50;

    //Perlin
    public static int Octaves = 4; // Adjust this value to control the smoothness of the noise
    public static float maxPersistence = 1.0f;
    public static float minPersistence = 0.01f;
    public static float Persistence = 0.5f; // Number of octaves used for generating noise


    // Voronoi
    private static int NUM_SITES = DungeonGenerator.getNUM_SITES();


    //Node
    private Node[][] map;
    private Node[][] mapTest;


    //UI
    private Group generalSettingsGroup;
    private Group DLAGroup;
    private Group CAGroup;
    private Group PerlinGroup;
    private Group VoronoiGroup;
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
        dungeonGenerator = new DungeonGenerator(DungeonGenerator.dungeonType.SRP, maxCol, maxRow);
        map = dungeonGenerator.generateBlankMap(maxCol, maxRow);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(Color.SKY.r, Color.SKY.g, Color.SKY.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        keyInput();
        resizeMap();
        drawNodes();


        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();

    }

    public void mapUpdated(){
        //Gdx.gl.glClearColor(0.15f, 0.15f, 0.2f, 1f);
        Gdx.gl.glClearColor(Color.SKY.r, Color.SKY.g, Color.SKY.b, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        keyInput();
        batch.begin();
        batch.end();
        drawNodes();




        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 60f));
        stage.draw();
        Gdx.graphics.requestRendering();
    }

    @Override
    public void dispose() {
        batch.dispose();
    }


    public void drawNodes(){
        try{
            // Draw filled rectangles
            if(shapeRenderer.isDrawing()){
                shapeRenderer.end();
            }
            shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            shapeRenderer.setColor(Color.SLATE);
            shapeRenderer.rect(0, 0, Gdx.graphics.getWidth() * 0.605f,
                Gdx.graphics.getHeight());

            for (int x = 0; x < col; x++) {
                for (int y = 0; y < row; y++) {
                    shapeRenderer.setColor(map[x][y].getColor());
                    shapeRenderer.rect(map[x][y].getBoundingBox().getX(), map[x][y].getBoundingBox().getY(),
                        map[x][y].getBoundingBox().getWidth(), map[x][y].getBoundingBox().getHeight());
                }
            }
            shapeRenderer.end();

            // Draw lines
            SelectBox<DungeonGenerator.dungeonType> temp = (SelectBox) generalSettingsGroup.getChild(9);
            if(temp.getSelected() != DungeonGenerator.dungeonType.PERLIN && col < 250 && row < 250 && temp.getSelected() != DungeonGenerator.dungeonType.VORONOI){
                shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
                shapeRenderer.setColor(Color.GRAY);
                for (int x = 0; x < col; x++) {
                    for (int y = 0; y < row; y++) {
                        shapeRenderer.rect(map[x][y].getBoundingBox().getX(), map[x][y].getBoundingBox().getY(),
                            map[x][y].getBoundingBox().getWidth(), map[x][y].getBoundingBox().getHeight());
                    }
                }
                shapeRenderer.end();
            }

        } catch (ArrayIndexOutOfBoundsException ignored){

        }

    }

    public void keyInput(){

    }

    private void createUI(){
        createGeneralSettingsUI();
        createDLAUI();
        createCAUI();
        createPerlinUI();
        createVoronoiUI();

        resizeUI();
    }

    private void createGeneralSettingsUI(){
        generalSettingsGroup = new Group();

        // General settings label
        final Label generalSettingsLabel = new Label("General Settings:", skin);

        // Columns Label
        final Label colLabel = new Label( "Columns: " + col, skin);
        colLabel.setName("colLabel");

        // Columns Slider
        final Slider colSlider = new Slider( (float)minCol, (float)maxCol, 1.0f, false,skin);
        colSlider.setValue((float) col);
        colSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                col = (int)colSlider.getValue();
                //System.out.println("Col: " + col);
                colLabel.setText("Columns: " + col);
                DLAScrollerDynamicNumbers();
            }
        });
        colSlider.setName("colSlider");

        // Row Label
        final Label rowLabel = new Label("Rows: " + row, skin);
        rowLabel.setName("rowLabel");

        // Row Slider
        final Slider rowSlider = new Slider( (float)minRow, (float)maxRow, 1.0f, false, skin);
        rowSlider.setValue((float) row);
        rowSlider.addListener( new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                row = (int)rowSlider.getValue();
                rowLabel.setText("Rows: " + row);
                //System.out.println("Rows: " + row);
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
                else if(generationSelectBox.getSelected() == DungeonGenerator.dungeonType.CA){
                    map = dungeonGenerator.generateDungeonCA(col, row, (int) CAIterations, (int) CAPercentage);
                }
                else if (generationSelectBox.getSelected() == DungeonGenerator.dungeonType.PERLIN) {
                    map = dungeonGenerator.generateDungeonPerlin(col, row, Octaves, Persistence);
                }
                else if(generationSelectBox.getSelected() == DungeonGenerator.dungeonType.VORONOI){
                    map = dungeonGenerator.generateDungeonVoronoi(col, row, NUM_SITES);
                }

                resizeMap();
                mapUpdated();

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

    private void createDLAUI(){
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

        final Label edgePointNumberLabel = new Label(String.format("Edge points: %.0f", edgeStartNumber), skin);

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

    private void createCAUI(){
        CAGroup = new Group();

        //CA Settings Label
        final Label CASettings = new Label("CA Settings: ", skin);

        //CA IterationSlider
        final Label IterationLabel = new Label(String.format("Iterations: %.0f", CAIterations), skin);
        final Slider IterationSlider = new Slider(minCAIterations, maxCAIterations, 1.0f, false, skin);
        IterationSlider.setValue(CAIterations);
        IterationSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CAIterations = (int) IterationSlider.getValue();
                IterationLabel.setText(String.format("Iterations: %.0f", CAIterations));
            }
        });

        //CA percentage Label
        final Label PercentageLabel = new Label(String.format("Percentage:  %.0f", CAPercentage), skin);
        //CA percentage Slider
        final Slider PercentageSlider = new Slider(minCAPercentage, maxCAPercentage, 1.0f, false, skin);
        PercentageSlider.setValue(CAPercentage);
        PercentageSlider.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                CAPercentage = (int) PercentageSlider.getValue();
                PercentageLabel.setText(String.format("Percentage:  %.0f", CAPercentage));
            }
        });



        CAGroup.addActor(CASettings);
        CAGroup.addActor(IterationLabel);
        CAGroup.addActor(IterationSlider);
        CAGroup.addActor(PercentageLabel);
        CAGroup.addActor(PercentageSlider);


        stage.addActor(CAGroup);
    }

    private void createPerlinUI(){
        PerlinGroup = new Group();

        // Perlin Settings Label
        final Label PerlinSettingsLabel = new Label("Perlin Settings: ", skin);

        final Label OcatvesLabel = new Label("Octaves: ", skin);
        final TextField OctavesValue = new TextField(Integer.toString(Octaves), skin);
        OctavesValue.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    String temp = OctavesValue.getText();
                    if (!temp.isEmpty()) {
                        int value = Integer.parseInt(temp);
                        if (value < 2) value = 2;
                        Octaves = value;
                        OctavesValue.setText(Integer.toString(Octaves));
                        return true;
                    }

                }
                return false;
            }
        });


        final Label PersistenceLabel = new Label("Persistence: ", skin);
        final TextField PersistenceValue = new TextField(String.format("%.2f", Persistence), skin);
        PersistenceValue.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ENTER) {
                    String temp = PersistenceValue.getText();
                    if (!temp.isEmpty()) {
                        float value = Float.parseFloat(temp);
                        if(value > maxPersistence) value = maxPersistence;
                        else if (value < minPersistence) value = minPersistence;
                        Persistence = value;
                        PersistenceValue.setText(String.format("%.2f", Persistence));
                        return true;
                    }

                }
                return false;
            }
        });

        PerlinGroup.addActor(PerlinSettingsLabel);
        PerlinGroup.addActor(OcatvesLabel);
        PerlinGroup.addActor(OctavesValue);
        PerlinGroup.addActor(PersistenceLabel);
        PerlinGroup.addActor(PersistenceValue);


        stage.addActor(PerlinGroup);
    }

    private void createVoronoiUI(){
        VoronoiGroup = new Group();

        final Label VoronoiSettingsLabel = new Label("Voronoi Settings:", skin);

        final Label NumberOfSitesLabel = new Label("Number of Sites:", skin);
        final TextField NumberOfSitesValue = new TextField(Integer.toString(NUM_SITES), skin);
        NumberOfSitesValue.addListener(new InputListener(){
            @Override
            public boolean keyUp(InputEvent event, int keycode) {
                if(keycode == Input.Keys.ENTER){
                    String temp = NumberOfSitesValue.getText();
                    if (!temp.isEmpty()) {
                        int value = Integer.parseInt(temp);
                        if (value < 2) value = 2;
                        NUM_SITES = value;
                        NumberOfSitesValue.setText(Integer.toString(NUM_SITES));
                        return true;
                    }
                }
                return false;
            }
        });


        VoronoiGroup.addActor(VoronoiSettingsLabel);
        VoronoiGroup.addActor(NumberOfSitesLabel);
        VoronoiGroup.addActor(NumberOfSitesValue);

        stage.addActor(VoronoiGroup);
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

        //DLA
        Label DLASettingsLabel = (Label) DLAGroup.getChild(0);
        Label numberOfWalkersLabel = (Label) DLAGroup.getChild(1);
        Slider numberOfWalkersSlider = (Slider) DLAGroup.getChild(2);
        Label stickinessLabel = (Label) DLAGroup.getChild(3);
        Slider stickinessSlider = (Slider) DLAGroup.getChild(4);
        CheckBox edgeCheckBox = (CheckBox) DLAGroup.getChild(5);
        Label edgePointNumberLabel = (Label) DLAGroup.getChild(6);
        Slider edgePointsNumberSlider = (Slider) DLAGroup.getChild(7);

        //CA
        Label CASettingsLabel = (Label) CAGroup.getChild(0);
        Label CAIterationLabel = (Label) CAGroup.getChild(1);
        Slider CAIterationSlider = (Slider) CAGroup.getChild(2);
        Label CAPercentageLabel =  (Label) CAGroup.getChild(3);
        Slider CAPercentageSlider = (Slider) CAGroup.getChild(4);

        //Perlin
        Label PerlinSettingsLabel = (Label) PerlinGroup.getChild(0);
        Label PerlinOctavesLabel = (Label) PerlinGroup.getChild(1);
        TextField PerlinOctavesValue = (TextField) PerlinGroup.getChild(2);
        Label PerlinPersistenceLabel = (Label) PerlinGroup.getChild(3);
        TextField PerlinPersistenceValue = (TextField) PerlinGroup.getChild(4);

        //Voronoi
        Label VoronoiSettingsLabel = (Label) VoronoiGroup.getChild(0);
        Label NumberOfSitesLabel = (Label) VoronoiGroup.getChild(1);
        TextField NumberOfSitesValue = (TextField) VoronoiGroup.getChild(2);


        //General Settings
        generalSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);

        rowLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() *  0.89f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        rowSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.89f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        colSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth()* 0.1f, Gdx.graphics.getHeight() * 0.03f);
        colLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);

        generateButton.setBounds(Gdx.graphics.getWidth() * 0.83f, Gdx.graphics.getHeight() * 0.2f,
            Gdx.graphics.getWidth() * 0.06f, Gdx.graphics.getHeight() * 0.06f);

        roomSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.86f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        roomLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() *  0.86f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);

        generationSelectLabel.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.95f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        generationSelectBox.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.92f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        //DLA Settings
        DLASettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.79f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        numberOfWalkersLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        numberOfWalkersSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        stickinessLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        stickinessSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        edgeCheckBox.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.70f,
            Gdx.graphics.getWidth() * 0.08f, Gdx.graphics.getHeight() * 0.03f);

        edgePointNumberLabel.setBounds(Gdx.graphics.getWidth() * 0.91f, Gdx.graphics.getHeight() * 0.67f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        edgePointsNumberSlider.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.67f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        //CA Settings
        CASettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.79f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        CAIterationLabel.setBounds(Gdx.graphics.getWidth() * 0.72f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        CAIterationSlider.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.76f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        CAPercentageLabel.setBounds(Gdx.graphics.getWidth() * 0.72f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.05f, Gdx.graphics.getHeight() * 0.03f);
        CAPercentageSlider.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.73f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        //Perlin Settings
        PerlinSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.61f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        PerlinOctavesLabel.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.58f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        PerlinOctavesValue.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.55f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        PerlinPersistenceLabel.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.52f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        PerlinPersistenceValue.setBounds(Gdx.graphics.getWidth() * 0.80f, Gdx.graphics.getHeight() * 0.49f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);

        //Voronoi Settings
        VoronoiSettingsLabel.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.61f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        NumberOfSitesLabel.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.58f,
            Gdx.graphics.getWidth() * 0.1f, Gdx.graphics.getHeight() * 0.03f);
        NumberOfSitesValue.setBounds(Gdx.graphics.getWidth() * 0.61f, Gdx.graphics.getHeight() * 0.55f,
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
                float posX = x * Gdx.graphics.getWidth() * 0.6f / col;
                float posY = (float) (y * Gdx.graphics.getHeight()) * 0.99f / row;
                map[x][y].getBoundingBox().setPosition(posX, posY);

                float width = (Gdx.graphics.getWidth() * 0.6f) / (float) col;
                float height = (float) Gdx.graphics.getHeight() * 0.99f / (float) row;
                map[x][y].getBoundingBox().setSize(width, height);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        resizeUI();
        resizeMap();
        viewport.update(width, height, true);
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);
    }


}
