package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Audio;
import lando.systems.ld44.utils.CallbackListener;
import lando.systems.ld44.utils.CallbackTimer;

public class Player extends GameEntity {
    public float horizontalSpeed = 100;

    public float maxValue = 2f;
    public float value;

    public CallbackTimer shootTimer = new CallbackTimer(new CallbackListener() {
        @Override
        public void callback() {
            image = assets.player;
        }
    });

    public Player(GameScreen screen, float x, float y) {
        super(screen);

        this.position.set(x, y);
        setImage(assets.player);
        jumpState = JumpState.JUMP;

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

        // temp
        if (Gdx.input.isKeyJustPressed(Input.Keys.I)) {
            addValue(0.2f);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.U)) {
            addValue(-0.2f);
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

        shootTimer.update(dt);
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
        if (value > 0) {
            addValue(-0.01f);

            screen.playSound(Audio.Sounds.Shoot);
            setImage(assets.playerShoot);
            shootTimer.setTime(0.5f);
        }
    }

    @Override
    protected void groundPound(Vector2 poundPosition) {
        float weightRatio = getWeightRatio();
        float shake = 0.5f + (0.4f * weightRatio);

        float distance = width/2 * (3 + (2 * weightRatio));
        screen.groundPound(poundPosition.x + width/2, poundPosition.y, width/2, distance);

        screen.shaker.addDamage(shake);
    }
}
