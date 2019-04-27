package lando.systems.ld44.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
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
import lando.systems.ld44.utils.Assets;

public class Level {

    private Assets assets;

    public String name;
    public TiledMap map;
    public TiledMapRenderer mapRenderer;
    public TiledMapTileLayer collisionLayer;
    public MapLayer objectsLayer;
    public Spawn spawn;
    public Exit exit;

    public Array<Rectangle> tileRects;
    public Pool<Rectangle> rectPool;

    public Level(String mapFileName, Assets assets) {
        Gdx.app.log("Map", "Loading map: '" + mapFileName + "'");

        this.assets = assets;

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

            if (type.equalsIgnoreCase("spawn")) {
                spawn = new Spawn(
                        props.get("x", Float.class),
                        props.get("y", Float.class), assets);
            }
            else if (type.equalsIgnoreCase("exit")) {
                exit = new Exit(
                        props.get("x", Float.class),
                        props.get("y", Float.class), assets);
            }
        }
        // Validate that we have at least a start and goal object
        if (spawn == null) {
            throw new GdxRuntimeException("Missing required map object: 'spawn'");
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
