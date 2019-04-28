package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.utils.Assets;

enum PlayerStates { None, Normal, Open, Close, Shoot }

public class PlayerStateManager {
    private float stateTime = 0;
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

        boolean completed = false;
        if (reverse) {
            time = Math.max(0, animation.getAnimationDuration() - stateTime);
            completed = time == 0;
        } else {
            completed = time > duration;
        }

        if (completed) {
            currentState = transitionState;
            transitionState = PlayerStates.None;
        }
        return animation.getKeyFrame(time);
    }

    public void transition(PlayerStates state) {
        transitionState = state;
        stateTime = 0;
    }
}
