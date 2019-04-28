package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.utils.Assets;

enum PlayerStates { None, Normal, Open, Close, Shoot }

public class PlayerStateManager {
    private float stateTime = 0;
    private float openTime = 0;

    public PlayerStates currentState = PlayerStates.Normal;
    private PlayerStates transitionState = PlayerStates.None;

    private Player player;
    private Assets assets;

    public PlayerStateManager(Player player) {
        this.player = player;
        this.assets = player.screen.assets;

        reset();
    }

    public void update(float dt) {
        TextureRegion image = assets.player;
        switch (transitionState) {
            case None:
                handleOpen(dt);
                return;

            case Normal:
                image = assets.player;
                break;
            case Open:
                image = handleTransition(assets.playerOpenAnimation, dt);
                break;
            case Close:
                image = handleTransition(assets.playerOpenAnimation, dt, true);
                break;
            case Shoot:
                image = handleTransition(assets.playerShootAnimation, dt);
                if (currentState == PlayerStates.Shoot) {
                    image = reset();
                }
                break;
        }

        player.setImage(image);
    }

    private void handleOpen(float dt) {
        if (currentState == PlayerStates.Open) {
            openTime += dt;
            if (openTime > 2) {
                // open for 2 seconds - close that bitch
                transition(PlayerStates.Close);
            }
        }
    }

    private TextureRegion reset() {
        currentState = PlayerStates.Normal;
        transitionState = PlayerStates.None;
        player.setImage(assets.player);
        return assets.player;
    }

    private TextureRegion handleTransition(Animation<TextureRegion> animation, float dt) {
        return handleTransition(animation, dt, false);
    }

    private TextureRegion handleTransition(Animation<TextureRegion> animation, float dt, boolean reverse){
        stateTime += dt;

        float time = stateTime;
        float duration = animation.getAnimationDuration();

        boolean completed;
        if (reverse) {
            time = Math.max(0, animation.getAnimationDuration() - stateTime);
            completed = time == 0;
        } else {
            completed = time > duration;
        }

        if (completed) {
            currentState = transitionState;
            transitionState = PlayerStates.None;
            openTime = 0;
        }
        return animation.getKeyFrame(time);
    }

    public void transition(PlayerStates state) {
        transitionState = state;
        stateTime = 0;
    }
}
