package lando.systems.ld44.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.*;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthoCachedTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Nickel;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class Level {

    private Assets assets;
    private GameScreen screen;

    public String name;
    public TiledMap map;
    public TiledMapRenderer mapRenderer;
    public TiledMapTileLayer collisionLayer;
    public MapLayer objectsLayer;
    public SpawnPlayer spawnPlayer;
    public Exit exit;
    public Array<Rectangle> tileRects;
    public Pool<Rectangle> rectPool;

    public Level(String mapFileName, Assets assets, GameScreen screen) {
        Gdx.app.log("Map", "Loading map: '" + mapFileName + "'");

        this.assets = assets;
        this.screen = screen;

        tileRects = new Array<Rectangle>();
        rectPool = Pools.get(Rectangle.class);

        // Load map
        this.map = (new TmxMapLoader()).load(mapFileName, new TmxMapLoader.Parameters() {{
            generateMipMaps = true;
            textureMinFilter = Texture.TextureFilter.MipMap;
            textureMagFilter = Texture.TextureFilter.MipMap;
        }});
        this.mapRenderer = new OrthoCachedTiledMapRenderer(map);
        ((OrthoCachedTiledMapRenderer) mapRenderer).setBlending(true);

        // Load map properties
        this.name = map.getProperties().get("name", "[UNNAMED]", String.class);

        // Validate map layers
        MapLayers layers = map.getLayers();
        collisionLayer = (TiledMapTileLayer) layers.get("collision");
        objectsLayer = layers.get("objects");
        if (collisionLayer == null || objectsLayer == null) {
            throw new GdxRuntimeException("Missing required map layer. (required: 'collision', 'objects')");
        }

        // Load map objects
        MapObjects objects = objectsLayer.getObjects();
        for (MapObject object : objects) {
            MapProperties props = object.getProperties();
            String type = (String) props.get("type");
            if (type == null) {
                Gdx.app.log("Map", "Map object missing 'type' property");
                continue;
            }

            if ("spawnPlayer".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                spawnPlayer = new SpawnPlayer(x, y, assets);
            }
            else if ("spawnEnemy".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                String name = object.getName().toLowerCase();

                GameEntity enemy = null;
                if ("nickel".equals(name)) {
                    enemy = new Nickel(screen);
                } else {
                    Gdx.app.log("Map", "Unknown enemy type for spawnEnemy entity: '" + name + "'");
                }

                if (enemy != null) {
                    enemy.position.set(x, y);
                    enemy.direction = GameEntity.Direction.LEFT;
                    screen.gameEntities.add(enemy);
                }
            }
            else if ("exit".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                exit = new Exit(x, y, assets);
            }
        }
        // Validate that we have at least a start and goal object
        if (spawnPlayer == null) {
            throw new GdxRuntimeException("Missing required map object: 'spawnPlayer'");
        } else if (exit == null) {
            throw new GdxRuntimeException("Missing required map object: 'exit'");
        }
    }

    public void update(float dt) {

    }

    // TODO: break out drawing of different layers into different calls
    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void getTiles(int startX, int startY, int endX, int endY, Array<Rectangle> tiles) {
        if (startX > endX){
            int t = startX;
            startX = endX;
            endX = t;
        }
        if (startY > endY){
            int t = startY;
            startY = endY;
            endY = t;
        }
        rectPool.freeAll(tiles);
        tiles.clear();
        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x, y, 1, 1);
                    tiles.add(rect);
                }
            }
        }
    }

}
