package lando.systems.ld44.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.screens.GameScreen;
import lando.systems.ld44.utils.Audio;

public class Player extends AnimationGameEntity {
    public float horizontalSpeed = 100;

    // TODO: change to maximum number of coins
    public float maxValue = 2f;
    public float value;

    public Array<Coin> coinPurse = new Array<Coin>();

    private PlayerStateManager stateManager;

    public Player(GameScreen screen, float x, float y) {
        super(screen, screen.assets.playerAnimation);

        stateManager = new PlayerStateManager(this);
        this.position.set(x, y);

        // start with 5 pennies
        while (coinPurse.size < 5) {
            addCoin(new Coin(screen, assets.pennyPickupAnimation, 0.01f));
        }
//        addCoin(new Coin(screen, assets.nickelPickupAnimation, 0.05f));
//        addCoin(new Coin(screen, assets.dimePickupAnimation, 0.10f));
//        addCoin(new Coin(screen, assets.quarterPickupAnimation, 0.25f));
        this.collisionBoundsOffsets.set(4, 0, 64, 60);
    }

    public void update(float dt) {
        super.update(dt);

        image = (hurtTime > 0) ? assets.playerHurt : image;


        if (groundPoundDelay > 0) return;
        if (jumpState != JumpState.POUND) {
            if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                velocity.add(-horizontalSpeed, 0);
                direction = Direction.LEFT;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                velocity.add(horizontalSpeed, 0);
                direction = Direction.RIGHT;
            }
        }
        if (Gdx.input.justTouched()) {
            shoot();
        }

        velocity.x *= .85f;
        if (jumpState != JumpState.BOUNCE) {
            velocity.x = MathUtils.clamp(velocity.x, -300, 300);
        }

        if (Math.abs(velocity.x) < 20) velocity.x = 0;

        boolean jumpPressed = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || Gdx.input.isKeyJustPressed(Input.Keys.UP);
        if (jumpPressed){
            jump();
        }

        handleConsumables();

        stateManager.update(dt);

        updateHurt(dt);
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

    private Rectangle consumeRect = new Rectangle();
    private Vector2 center = new Vector2();
    private void checkConsume(Coin coin) {
        consumeRect.set(position.x, top() - 10, width, 70);
        if (!coin.consuming && (coin.velocity.y < 0)) {
            coin.bounds.getCenter(center);
            if (consumeRect.contains(center)) {
                coin.consuming = true;
                open();
            }
        }
    }

    private void consume(Coin coin) {
        if (coin.consuming) {
            consumeRect.set(position.x, top() - 20, width, 20);
            coin.bounds.getCenter(center);
            if (consumeRect.contains(center)) {
                playSound(Audio.Sounds.ConsumeCoin);
                addCoin(coin);
                close();
            }
        }
    }

    private void addCoin(Coin coin) {
        coinPurse.add(coin);
        coin.remove = true;
        /*
        value = MathUtils.clamp(value + value, 0, maxValue);

        float invWeightRatio = 1 - getWeightRatio();
        horizontalSpeed = 30 + (170 * invWeightRatio);
        jumpVelocity = 600 + (400 * invWeightRatio);

        addValue(coin.value);
        */
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

    public void shoot() {
        if (coinPurse.size > 0) {
            playSound(Audio.Sounds.Shoot);
            stateManager.transition(PlayerStates.Shoot);

            Coin coin = popFirstCoin();

            float x = position.x + width/2;
            if (direction == Direction.LEFT) {
                x -= coin.width;
            }
            float y = position.y + 5;
            float speed = velocity.x;
            if (speed == 0) {
                speed = (direction == Direction.LEFT) ? -0.01f : 0.01f;
            }
            coin.shoot(x, y, speed);
        }
    }

    private Coin popRandomCoin() {
        Coin coin = coinPurse.random();
        coinPurse.removeValue(coin, true);
        return coin;
    }

    private Coin popFirstCoin() {
        if (coinPurse.isEmpty()) return null;
        Coin coin = coinPurse.first();
        coinPurse.removeIndex(0);
        return coin;
    }

    private Coin popLastCoin() {
        if (coinPurse.isEmpty()) return null;
        return coinPurse.pop();
    }

    @Override
    protected void groundPound(Vector2 poundPosition) {
        float weightRatio = getWeightRatio();
        float shake = 0.5f + (0.4f * weightRatio);

        float distance = width/2 * (3 + (2 * weightRatio));
        playSound(Audio.Sounds.GroundPound);
        screen.groundPound(poundPosition.x + width/2, poundPosition.y, width/2, distance);
        screen.shaker.addDamage(shake);

        screen.hud.triggerCoinShuffle();
    }

    @Override
    public void changeDirection() {
        // noop so it doesn't flip rapidly when pushing against a wall.
    }

    public float hurtTime = 0;
    @Override
    public void getHurt(Rectangle damageRect) {
        if (hurtTime <= 0) {
            super.getHurt(damageRect);
            hurtTime = 2;

            int coinsToLose = (coinPurse.size+1) / 2;
            for (int i = 0; i < coinsToLose; i++){
                Coin coin = coinPurse.random();
                coinPurse.removeValue(coin, true);
                coin.reset();
                coin.markHit();
                coin.position.x = MathUtils.random(bounds.x, bounds.x + bounds.width);
                coin.position.y = bounds.y + bounds.height + 20;
                coin.velocity.y = 700;
                coin.velocity.x = MathUtils.random(-600, 600);
                coin.remove = false;
                screen.gameEntities.add(coin);
            }

            screen.particleManager.addBlood(damageRect);
            if (damageRect.width < damageRect.height ||
                damageRect.y == bounds.y){
                velocity.y = 400;
            }
            if (damageRect.x == bounds.x) {
                velocity.x = 400;
            } else {
                velocity.x = -400;
            }

        }
    }

    public void updateHurt(float dt) {
        if (hurtTime > 0) {
            hurtTime -= dt;
        }
    }
}
