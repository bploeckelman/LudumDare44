package lando.systems.ld44.entities;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import lando.systems.ld44.utils.Assets;

public class Nickel extends GameEntity {

    public Animation<TextureRegion> animation;
    private float anim;

    public Nickel(Assets assets) {
        super(assets);
        setAnimation(assets.nickelAnimation);
    }

    protected void setAnimation(Animation<TextureRegion> animation) {
        this.animation = animation;
        width = animation.getKeyFrame(0).getRegionWidth();
        height = animation.getKeyFrame(0).getRegionHeight();
    }

    @Override
    public void update(float dt) {
        anim += dt;
        image = animation.getKeyFrame(anim, true);
    }
}
