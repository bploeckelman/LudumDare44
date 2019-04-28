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
import lando.systems.ld44.entities.GameEntity;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;

public class Level {

    public static final float TILE_SIZE = 32f;

    private Assets assets;
    private GameScreen screen;

    public String name;
    public TiledMap map;
    public TiledMapRenderer mapRenderer;
    public TiledMapTileLayer collisionLayer;
    public MapLayer objectsLayer;
    public SpawnPlayer spawnPlayer;
    public Array<EnemySpawner> enemySpawners;
    public Exit exit;
    public Array<Tack> tacks;
    public Array<Spring> springs;
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
        tacks = new Array<Tack>();
        springs = new Array<Spring>();
        enemySpawners = new Array<EnemySpawner>();
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

                String directionProp = props.get("direction", "left", String.class).toLowerCase();
                GameEntity.Direction direction = GameEntity.Direction.LEFT;
                if ("left".equals(directionProp)) direction = GameEntity.Direction.LEFT;
                else if ("right".equals(directionProp)) direction = GameEntity.Direction.RIGHT;
                else Gdx.app.log("Map", "Unknown direction for spawnEnemy: '" + directionProp + "'");

                String name = object.getName().toLowerCase();
                EnemySpawner.EnemyType enemyType = null;
                if      ("penny"   .equals(name)) enemyType = EnemySpawner.EnemyType.penny;
                else if ("nickel"  .equals(name)) enemyType = EnemySpawner.EnemyType.nickel;
                else if ("dime"    .equals(name)) enemyType = EnemySpawner.EnemyType.dime;
                else if ("chicken" .equals(name)) enemyType = EnemySpawner.EnemyType.chicken;
                else Gdx.app.log("Map", "Unknown enemy type for spawnEnemy entity: '" + name + "'");

                if (enemyType != null) {
                    EnemySpawner spawner = new EnemySpawner(x, y, enemyType, direction);
                    enemySpawners.add(spawner);
                    spawner.spawnEnemy(screen);
                }
            }
            else if ("exit".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                exit = new Exit(x, y, assets);
            }
            else if ("tack".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                String facingProp = props.get("facing", "up", String.class).toLowerCase();
                Tack.Facing facing = Tack.Facing.UP;
                if      ("up"   .equals(facingProp)) facing = Tack.Facing.UP;
                else if ("down" .equals(facingProp)) facing = Tack.Facing.DOWN;
                else if ("left" .equals(facingProp)) facing = Tack.Facing.LEFT;
                else if ("right".equals(facingProp)) facing = Tack.Facing.RIGHT;
                Tack tack = new Tack(x, y, facing, assets);
                tacks.add(tack);
            }
            else if ("spring".equalsIgnoreCase(type)) {
                float x = props.get("x", Float.class);
                float y = props.get("y", Float.class);
                String orientationProp = props.get("orientation", String.class).toLowerCase();
                Spring.Orientation orientation = Spring.Orientation.UP;
                if      ("up"   .equals(orientationProp)) orientation = Spring.Orientation.UP;
                else if ("down" .equals(orientationProp)) orientation = Spring.Orientation.DOWN;
                else if ("left" .equals(orientationProp)) orientation = Spring.Orientation.LEFT;
                else if ("right".equals(orientationProp)) orientation = Spring.Orientation.RIGHT;
                Spring spring = new Spring(x, y, orientation, assets);
                springs.add(spring);
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
        for (Spring spring : springs) {
            spring.update(dt);
        }
        for (Tack tack : tacks) {
            tack.update(dt);
        }
    }

    private Rectangle entityBounds = new Rectangle();
    public void handleObjectInteractions(GameEntity entity) {
        for (Spring spring : springs) {
            if (spring.springing) continue;

            entityBounds.set(entity.position.x, entity.position.y, entity.width, entity.height);
            if (spring.bounds.overlaps(entityBounds)) {
                spring.trigger();
                float multiplier = 1.5f;
                if (entity.groundPoundDelay > 0f) {
                    entity.groundPoundDelay = 0f;
                    multiplier = 1.75f;
                }
                entity.bounce(multiplier, spring.orientation);
            }
        }
        for (Tack tack : tacks) {
            entityBounds.set(entity.position.x, entity.position.y, entity.width, entity.height);
            if (tack.bounds.overlaps(entityBounds)) {
                // TODO: if this is the player, lose some coins and bounce back or whatever, if enemy, then die or whatever
                if (entity instanceof Player) {
                    entity.getHurt();
                } else {
                    entity.stun();
                }
            }
        }
    }

    // TODO: break out drawing of different layers into different calls
    public void render(OrthographicCamera camera) {
        mapRenderer.setView(camera);
        mapRenderer.render();
    }

    public void renderObjects(SpriteBatch batch, OrthographicCamera camera) {
        // TODO: only render if within current view...
        for (Spring spring : springs) {
            spring.render(batch);
        }
        for (Tack tack : tacks) {
            tack.render(batch);
        }
    }

    public void getTiles(float startX, float startY, float endX, float endY, Array<Rectangle> tiles) {
        if (startX > endX){
            float t = startX;
            startX = endX;
            endX = t;
        }
        if (startY > endY){
            float t = startY;
            startY = endY;
            endY = t;
        }
        rectPool.freeAll(tiles);
        tiles.clear();
        int iStartY = (int)(startY / collisionLayer.getTileHeight());
        int iEndY = (int)(endY / collisionLayer.getTileHeight());
        int iStartX = (int)(startX / collisionLayer.getTileWidth());
        int iEndX = (int)(endX / collisionLayer.getTileWidth());
        for (int y = iStartY; y <= iEndY; y++) {
            for (int x = iStartX; x <= iEndX; x++) {
                TiledMapTileLayer.Cell cell = collisionLayer.getCell(x, y);
                if (cell != null) {
                    Rectangle rect = rectPool.obtain();
                    rect.set(x * collisionLayer.getTileWidth(), y * collisionLayer.getTileHeight(), collisionLayer.getTileWidth(), collisionLayer.getTileHeight());
                    tiles.add(rect);
                }
            }
        }
    }

}
