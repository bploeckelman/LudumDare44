package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Config;
import lando.systems.ld44.world.Spring;

public class GameEntity {
    public enum Direction {RIGHT, LEFT}
    public enum JumpState {NONE, JUMP, POUND, BOUNCE}

    public Assets assets;
    public GameScreen screen;

    public float width;
    public float height;

    public Vector2 position = new Vector2();
    private Vector2 tempPos = new Vector2();
    public Vector2 velocity = new Vector2();

    public Direction direction = Direction.RIGHT;
    public JumpState jumpState = JumpState.NONE;
    public boolean grounded;
    public float jumpVelocity = 800;
    public float bounceVelocity = 2000f;
    public float gravity = 2000;
    public float groundPoundDelay = 0;

    public boolean poundable;
    public float stunTime = 0;
    public float preStunnedVelocity;
    public boolean remove;

    public TextureRegion image;
    public Array<Rectangle> tiles;
    public Rectangle bounds = new Rectangle();

    public GameEntity(GameScreen screen){
        this.assets = screen.assets;
        this.screen = screen;
        this.tiles = new Array<Rectangle>();
        grounded = true;
    }

    public void setImage(TextureRegion image) {
        this.image = image;
        width = image.getRegionWidth();
        height = image.getRegionHeight();
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
        if (stunTime > 0) { return; }

        stunTime = 2;
        preStunnedVelocity = velocity.x;
        velocity.x = 0;
        position.y += 20;
    }

    public void getHurt() {
        // TODO: override in Player to bounce back and lose some coins
        float centerX = position.x + width / 2f;
        float centerY = position.y;
        screen.particleManager.addGroundPoundDust(centerX, centerY, centerX - 100f, centerX + 100f);
        float weightRatio = 1f;
        float shake = 0.5f + (0.4f * weightRatio);
        screen.shaker.addDamage(shake);
    }

    public void update(float dt) {
        groundPoundDelay = Math.max(groundPoundDelay -dt, 0);
        velocity.y -= gravity * dt;

        tempPos.set(position);
        tempPos.add(velocity.x * dt, velocity.y * dt);

        Rectangle entityRect = screen.level.rectPool.obtain();
        entityRect.set(tempPos.x, position.y, width, height);
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
            entityRect.set(tempPos.x, position.y, width, height);
            if (entityRect.overlaps(tile)){
                tempPos.x = position.x;
                changeDirection();
                break;
            }
        }

        entityRect.set(tempPos.x, tempPos.y, width, height);

        // Check vertical
        if (velocity.y > 0){ // above?
            startY = endY = entityRect.y + entityRect.height;
        } else {
            startY = position.y;
            endY = entityRect.y;
        }
        startX = entityRect.x;
        endX = entityRect.x + entityRect.width;

        grounded = false;
        boolean pounded = false;
        screen.level.getTiles(startX, startY, endX, endY, tiles);
        for (Rectangle tile : tiles) {
            entityRect.set(tempPos.x, tempPos.y, width, height);
            if (entityRect.overlaps(tile)) {
                // Up
                if (velocity.y > 0) {
                    tempPos.y = Math.min(tempPos.y, tile.y - height);
                } else {
                    tempPos.y = Math.max(tempPos.y, tile.y + tile.height);
                    if (jumpState == JumpState.POUND) {
                        groundPoundDelay = .5f;
                        velocity.x = 0;
                        pounded = true;
                    }
                    jumpState = JumpState.NONE;
                    grounded = true;
                }
                velocity.y = 0;
            }
        }

        handleStun(dt);

        screen.level.handleObjectInteractions(this);

        screen.level.rectPool.free(entityRect);
        position.set(tempPos);
        if (pounded) {
            groundPound(tempPos);
        }
        bounds.set(position.x, position.y, width, height);
    }

    protected void groundPound(Vector2 poundPosition) {
        screen.shaker.addDamage(.5f);
    }

    private void handleStun(float dt) {
        if (stunTime > 0) {
            stunTime -= dt;
            if (stunTime <= 0) {
                stunTime = 0;
                velocity.x = preStunnedVelocity;
            }
        }
    }

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
            batch.draw(image, position.x, position.y, width / 2, height / 2, width, height, scaleX, scaleY, 0);

            if (Config.debug) {
                assets.ninePatch.draw(batch, bounds.x, bounds.y, bounds.width, bounds.height);
            }
        }
    }
}
