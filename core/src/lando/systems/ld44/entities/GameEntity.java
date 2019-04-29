package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.Config;
import lando.systems.ld44.world.Level;
import lando.systems.ld44.world.Spring;

public class GameEntity {
    public enum Direction {RIGHT, LEFT}
    public enum JumpState {NONE, JUMP, POUND, BOUNCE}

    public Assets assets;
    public GameScreen screen;

    public float width;
    public float height;
    public Rectangle collisionBoundsOffsets;

    public Vector2 position = new Vector2();
    private Vector2 tempPos = new Vector2();
    protected Vector2 velocity = new Vector2();

    public Direction direction = Direction.RIGHT;
    public JumpState jumpState = JumpState.NONE;
    public boolean grounded;
    public float jumpVelocity = 800;
    public float bounceVelocity = 2000f;
    public float gravity = 2000;
    public float groundPoundDelay = 0;

    public boolean poundable;
    public boolean consuming;
    public boolean remove;

    public TextureRegion image;
    public Array<Rectangle> tiles;
    public Rectangle bounds = new Rectangle();

    private float stunTime = 0;
    private boolean isDying;
    private float dyingTime = 0;
    private float preStunnedVelocity;

    // sound customizations
    public Audio.Sounds killSound = Audio.Sounds.None;

    public GameEntity(GameScreen screen){
        this.assets = screen.assets;
        this.screen = screen;
        this.tiles = new Array<Rectangle>();
        this.collisionBoundsOffsets = new Rectangle();
        grounded = true;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
        width = image.getRegionWidth();
        height = image.getRegionHeight();
    }

    public boolean isStunned() { return stunTime > 0; }
    public boolean isDying() { return isDying; }

    public void playSound(Audio.Sounds sound) {
        screen.audio.playSound(sound);
    }

    public void changeDirection() {
        setDirection((direction == Direction.LEFT) ? Direction.RIGHT : Direction.LEFT);
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public void jump() {
        jump(1f);
    }

    public void jump(float velocityMultiplier) {
        if (grounded){
            velocity.y = jumpVelocity * velocityMultiplier;
            jumpState = JumpState.JUMP;
        } else if (jumpState == JumpState.JUMP || jumpState == JumpState.BOUNCE){
            velocity.y = -1000;
            velocity.x = 0;
            jumpState = JumpState.POUND;
        }
    }

    public void bounce(float velocityMultiplier, Spring.Orientation springOrientation) {
        jumpState = JumpState.BOUNCE;

        // Enemies can bounce too fast in some cases (like if they're in an enclosed bounce box), so we have to modify that so they don't pop past collision boundaries
        float entityTypeVelocityMultiplier = 1f;
        if (this instanceof Penny || this instanceof Nickel || this instanceof Dime) {
            entityTypeVelocityMultiplier = 0.3f;
        }

        switch (springOrientation) {
            // NOTE: since the bounce velocity is higher than jump velocity, we only modify the bounce ones
            case UP:    velocity.y = -jumpVelocity   * /*entityTypeVelocityMultiplier * */velocityMultiplier; break;
            case DOWN:  velocity.y =  jumpVelocity   * /*entityTypeVelocityMultiplier * */velocityMultiplier; break;
            case LEFT:  velocity.x =  bounceVelocity * entityTypeVelocityMultiplier * velocityMultiplier; break;
            case RIGHT: velocity.x = -bounceVelocity * entityTypeVelocityMultiplier * velocityMultiplier; break;
        }
    }

    public void pound() {
        if (poundable) {
            stun();
        }
    }

    public void stun() {
        if (isStunned() || isDying()) return;

        screen.audio.playSound(Audio.Sounds.Stun, position, screen.player.position);
        stunTime = 4;
        preStunnedVelocity = velocity.x;
        velocity.x = 0;
        position.y += 20;
    }

    public void kill() {
        if (isDying()) return;

        velocity.x = 0;
        playSound(killSound);
        stunTime = 0;
        isDying = true;
        dyingTime = 2;
    }

    public void getHurt(Rectangle damageRect) {
        // TODO: override in Player to bounce back and lose some coins
//        float centerX = position.x + width / 2f;
//        float centerY = position.y;
//        screen.particleManager.addGroundPoundDust(centerX, centerY, centerX - 100f, centerX + 100f);
//        float weightRatio = 1f;
//        float shake = 0.5f + (0.4f * weightRatio);
//        screen.shaker.addDamage(shake);
    }

    public void update(float dt) {
        updatePosition(dt);
        if (isDying()) {
            handleDying(dt);
        } else if (isStunned()) {
            handleStunned(dt);
        } else {
            updateEntity(dt);
        }
    }

    // used to collide with player
    public boolean hasHit(GameEntity entity, Rectangle collisionRect) {
        if (isStunned() || isDying() || entity.isStunned() || entity.isDying()) return false;
        return Intersector.intersectRectangles(bounds, entity.bounds, collisionRect);
    }

    protected void handleDying(float dt) {
        if (dyingTime > 0) {
            dyingTime -= dt;
            if (dyingTime < 0) {
                onDeath();
            }
        }
    }

    protected void onDeath() {
        remove = true;
    }

    protected void handleStunned(float dt) {
        stunTime -= dt;
        if (stunTime <= 0) {
            stunTime = 0;
            velocity.x = preStunnedVelocity;
        }
    }

    public void updatePosition(float dt) {
        groundPoundDelay = Math.max(groundPoundDelay -dt, 0);
        velocity.y -= gravity * dt;

        tempPos.set(position);
        tempPos.add(velocity.x * dt, velocity.y * dt);

        Rectangle entityRect = screen.level.rectPool.obtain();
        entityRect.set(tempPos.x + collisionBoundsOffsets.x, position.y + collisionBoundsOffsets.y, collisionBoundsOffsets.width, collisionBoundsOffsets.height);
        float startX, startY, endX, endY;

        // Check Horizontal
        if (velocity.x > 0) {
            startX = endX = entityRect.x + entityRect.width;
        } else {
            startX = endX = entityRect.x;
        }
        startY = entityRect.y;
        endY = entityRect.y + entityRect.height;
        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x + collisionBoundsOffsets.x, position.y + collisionBoundsOffsets.y, collisionBoundsOffsets.width, collisionBoundsOffsets.height);
            if (entityRect.overlaps(tile)){
                tempPos.x = position.x;
                changeDirection();
                break;
            }
        }

        entityRect.set(tempPos.x + collisionBoundsOffsets.x, tempPos.y + collisionBoundsOffsets.y, collisionBoundsOffsets.width, collisionBoundsOffsets.height);

        // Check vertical
        if (velocity.y > 0){ // above?
            startY = endY = entityRect.y + entityRect.height;
        } else {
            startY = position.y;
            endY = entityRect.y;
        }
        startX = entityRect.x;
        endX = entityRect.x + entityRect.width;

        boolean wasGrounded = grounded;
        grounded = false;
        boolean pounded = false;
        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x + collisionBoundsOffsets.x, tempPos.y + collisionBoundsOffsets.y, collisionBoundsOffsets.width, collisionBoundsOffsets.height);
            if (entityRect.overlaps(tile)) {
                // Up
                if (velocity.y > 0) {
                    tempPos.y = Math.min(tempPos.y, tile.y - height);
                } else {
                    tempPos.y = Math.max(tempPos.y, tile.y + tile.height);
                    if (jumpState == JumpState.POUND) {
                        groundPoundDelay = .25f;
                        velocity.x = 0;
                        pounded = true;
                    } else if (!wasGrounded) {
                        screen.audio.playSound(Audio.Sounds.Landing, position, screen.player.position);
                    }
                    jumpState = JumpState.NONE;
                    grounded = true;

                }
                velocity.y = 0;
            }
        }

        screen.level.handleObjectInteractions(this);

        screen.level.rectPool.free(entityRect);
        position.set(tempPos);
        if (pounded) {
            groundPound(tempPos);
        }
        bounds.set(collisionBoundsOffsets);
        bounds.x += position.x;
        bounds.y += position.y;
    }

    public void updateEntity(float dt) { }

    public void keepOnPlatform(float dt) {
        // Check for about to fall off a platform
        if (grounded && Math.abs(velocity.x) > 0f) {
            Level level = screen.level;

            // get cells in front of and under entity
            int y = (int) (position.y / level.collisionLayer.getTileHeight()) - 1;
            int x = 0;
            float sign = Math.signum(velocity.x);
            if (sign == -1f) { // moving left
                float centerX = position.x + collisionBoundsOffsets.x + collisionBoundsOffsets.width / 2f;
                x = (int) (centerX / level.collisionLayer.getTileWidth()) - 1;
            } else if (sign == 1f) { // moving right
                float centerX = position.x + collisionBoundsOffsets.x + collisionBoundsOffsets.width / 2f;
                x = (int) (centerX / level.collisionLayer.getTileWidth()) + 1;
            }
            TiledMapTileLayer.Cell cell = level.collisionLayer.getCell(x, y);
            if (cell == null) {
                changeDirection();
            }
        }
    }

    protected void groundPound(Vector2 poundPosition) { }

    public void render(SpriteBatch batch) {
        if (image != null) {
            float scaleX = (direction == Direction.RIGHT) ? 1 : -1;
            float scaleY = 1;
            if (!grounded){
                if (jumpState == JumpState.POUND) {
                    scaleX *= .6f;
                    scaleY = 1.3f;
                } else {
                    scaleX *= .85f;
                    scaleY = 1.15f;
                }
            }
            if (groundPoundDelay > 0){
                scaleX *= 1 + groundPoundDelay/2f;
                scaleY = 1 - groundPoundDelay/3f;
            }

            if (isDying()) {
                renderDying(batch, scaleX, scaleY);
            } else if (isStunned()) {
                renderStunned(batch, scaleX, scaleY);
            } else {
                renderNormal(batch, scaleX, scaleY);
            }
            if (Config.debug) {
                assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
                batch.setColor(Color.RED);
                assets.ninePatch.draw(batch, position.x + collisionBoundsOffsets.x, position.y + collisionBoundsOffsets.y, collisionBoundsOffsets.width, collisionBoundsOffsets.height);
                batch.setColor(Color.WHITE);
            }
        }
    }

    protected void renderNormal(SpriteBatch batch, float scaleX, float scaleY) {
        batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, scaleY, 0);
    }

    protected void renderStunned(SpriteBatch batch, float scaleX, float scaleY) {
        Color stunColor = (stunTime % 0.4f > 0.2f) ? Color.RED : Color.WHITE;

        batch.setColor(stunColor);
        batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, scaleY, 0);
        batch.setColor(Color.WHITE);
        batch.draw(assets.stunStarsAnimation.getKeyFrame(stunTime), position.x, position.y + collisionBoundsOffsets.height, width, 10);
    }

    protected void renderDying(SpriteBatch batch, float scaleX, float scaleY) {
        batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, scaleY, 0);
    }

    public float left() {
        return bounds.x;
    }

    public float right() {
        return (bounds.x + bounds.width);
    }

    public float top() {
        return (bounds.y + bounds.height);
    }

    public float bottom() {
        return bounds.y;
    }
}
