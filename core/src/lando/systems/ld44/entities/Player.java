package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Audio;

public class Player extends GameEntity {
    public float horizontalSpeed = 100;

    public float maxValue = 2f;
    public float value;

    private PlayerStateManager stateManager;

    public Player(GameScreen screen, float x, float y) {
        super(screen);

        stateManager = new PlayerStateManager(this);
        this.position.set(x, y);
    }

    public void update(float dt) {
        super.update(dt);
        if (groundPoundDelay > 0) return;
        if (jumpState != JumpState.POUND) {
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                velocity.add(-horizontalSpeed, 0);
                direction = Direction.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                velocity.add(horizontalSpeed, 0);
                direction = Direction.RIGHT;
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            shoot();
        }

        velocity.x *= .85f;
        if (jumpState != JumpState.BOUNCE) {
            velocity.x = MathUtils.clamp(velocity.x, -300, 300);
        }

        if (Math.abs(velocity.x) < 20) velocity.x = 0;

        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.justTouched();
        if (jumpPressed){
            jump();
        }

        handleConsumables();

        stateManager.update(dt);

        handleRandos();
    }

    private void handleConsumables() {
        for (GameEntity ge : screen.gameEntities) {
            if (ge instanceof Coin) {
                Coin coin = (Coin)ge;

                checkConsume(coin);
                consume(coin);
            }
        }
    }

    private void checkConsume(Coin coin) {
        if (!coin.consuming && (coin.velocity.y < 0) && coin.left() > left() && coin.right() < right() && coin.bottom() < top() + 100 && coin.bottom() > top() - 20) {
            coin.consuming = true;
            open();
        }
    }

    private void consume(Coin coin) {
        if (coin.consuming && coin.left() > left() && coin.right() < right() && coin.top() < top() - 10) {
            playSound(Audio.Sounds.ConsumeCoin);
            coin.remove = true;
            addValue(coin.value);
            close();
        }
    }

    public void handleRandos() {
        // delete me
        // temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            addValue(0.2f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            addValue(-0.2f);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.K)) {
            for (GameEntity ge : screen.gameEntities) {
                if (ge instanceof Enemy) {
                    ((Enemy)ge).kill();
                    break;
                }
            }
        }
    }

    public void open() {
        stateManager.transition(PlayerStates.Open);
    }

    public void close() {
        stateManager.transition(PlayerStates.Close);
    }

    // 0 is empty - 1 is full - fatty
    private float getWeightRatio() {
        return this.value / this.maxValue;
    }

    public void addValue(float value) {
        this.value = MathUtils.clamp(this.value + value, 0, this.maxValue);

        float invWeightRatio = 1 - getWeightRatio();
        horizontalSpeed = 30 + (170 * invWeightRatio);
        jumpVelocity = 600 + (400 * invWeightRatio);
    }

    public void shoot() {

        // todo: check if there is inventory to shoot
        playSound(Audio.Sounds.Shoot);
        stateManager.transition(PlayerStates.Shoot);
    }

    @Override
    protected void groundPound(Vector2 poundPosition) {
        float weightRatio = getWeightRatio();
        float shake = 0.5f + (0.4f * weightRatio);

        float distance = width/2 * (3 + (2 * weightRatio));
        playSound(Audio.Sounds.GroundPound);
        screen.groundPound(poundPosition.x + width/2, poundPosition.y, width/2, distance);

        screen.shaker.addDamage(shake);
    }

    @Override
    public void changeDirection() {
        // noop so it doesn't flip rapidly when pushing against a wall.
    }
}
