package lando.systems.ld44.particles;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;

public class GenericParticle implements Pool.Poolable{
    public enum OriginType {CENTER, CUSTOM}

    TextureRegion region;
    Animation<TextureRegion> animation;
    float startWidth;
    float endWidth;
    float startHeight;
    float endHeight;
    Vector2 pos;
    Vector2 vel;
    Vector2 acc;
    float accDamping;
    OriginType originType;
    float originX;
    float originY;
    Color startColor;
    Color endColor;
    float startRotation;
    float endRotation;
    float ttl;
    float totalTtl;
    float delay;

    public GenericParticle() {
        pos = new Vector2();
        vel = new Vector2();
        acc = new Vector2();
        accDamping = 1;
        delay = 0;
        startColor = new Color();
        endColor = new Color();
    }

    @Override
    public void reset() {

    }

    public void init(TextureRegion region,
                     float startWidth, float endWidth,
                     float startHeight, float endHeight,
                     float x, float y,
                     float vx, float vy,
                     float ax, float ay,
                     float accDamping,
                     OriginType originType, float originX, float originY,
                     float sR, float sG, float sB, float sA,
                     float eR, float eG, float eB, float eA,
                     float startRotation, float endRotation,
                     float ttl, float delay) {
        init(region, null, startWidth, endWidth, startHeight, endHeight,
                x, y, vx, vy, ax, ay, accDamping,
                originType, originX, originY,
                sR, sG, sB, sA,
                eR, eG, eB, eA,
                startRotation, endRotation, ttl, delay);
    }

    public void init(Animation<TextureRegion> animation,
                     float startWidth, float endWidth,
                     float startHeight, float endHeight,
                     float x, float y,
                     float vx, float vy,
                     float ax, float ay,
                     float accDamping,
                     OriginType originType, float originX, float originY,
                     float sR, float sG, float sB, float sA,
                     float eR, float eG, float eB, float eA,
                     float startRotation, float endRotation,
                     float ttl, float delay) {
        init(null, animation, startWidth, endWidth, startHeight, endHeight,
                x, y, vx, vy, ax, ay, accDamping,
                originType, originX, originY,
                sR, sG, sB, sA,
                eR, eG, eB, eA,
                startRotation, endRotation, ttl, delay);
    }

    public void init(TextureRegion region, Animation<TextureRegion> animation,
                     float startWidth, float endWidth,
                     float startHeight, float endHeight,
                     float x, float y,
                     float vx, float vy,
                     float ax, float ay,
                     float accDamping,
                     OriginType originType, float originX, float originY,
                     float sR, float sG, float sB, float sA,
                     float eR, float eG, float eB, float eA,
                     float startRotation, float endRotation,
                     float ttl, float delay) {
        this.region = region;
        this.animation = animation;
        this.startWidth = startWidth;
        this.endWidth = endWidth;
        this.startHeight = startHeight;
        this.endHeight = endHeight;
        this.pos.set(x, y);
        this.vel.set(vx, vy);
        this.acc.set(ax, ay);
        this.accDamping = accDamping;
        this.originType = originType;
        this.originX = originX;
        this.originY = originY;
        this.startColor.set(sR, sG, sB, sA);
        this.endColor.set(eR, eG, eB, eA);
        this.startRotation = startRotation;
        this.endRotation = endRotation;
        this.ttl = ttl;
        this.totalTtl = ttl;
        this.delay = delay;

    }

    public void update(float dt){
        delay = Math.max(delay - dt, 0);
        if (delay > 0) return;
        ttl -= dt;
        vel.add(acc.x * dt, acc.y * dt);
        pos.add(vel.x * dt, vel.y * dt);

        acc.scl(accDamping);
        if (acc.epsilonEquals(0.0f, 0.0f, 0.1f)) {
            acc.set(0f, 0f);
        }
    }

    public void render(SpriteBatch batch) {
        if (delay > 0) return;

        float t = MathUtils.clamp(1f - ttl/totalTtl, 0f, 1f);

        float r = MathUtils.lerp(startColor.r, endColor.r, t);
        float g = MathUtils.lerp(startColor.g, endColor.g, t);
        float b = MathUtils.lerp(startColor.b, endColor.b, t);
        float a = MathUtils.lerp(startColor.a, endColor.a, t);

        r = MathUtils.clamp(r, 0, 1f);
        g = MathUtils.clamp(g, 0, 1f);
        b = MathUtils.clamp(b, 0, 1f);
        a = MathUtils.clamp(a, 0, 1f);

        float rotation = MathUtils.lerp(startRotation, endRotation, t);
        float width = MathUtils.lerp(startWidth, endWidth, t);
        float height = MathUtils.lerp(startHeight, endHeight, t);

        float x = pos.x;
        float y = pos.y;

        if (originType == OriginType.CENTER) {
            originX = width/2f;
            originY = height/2f;
        }

        x -= originX;
        y -= originY;

        batch.setColor(r, g, b, a);
        if (animation != null){
            float totalDuration = animation.getAnimationDuration();
            batch.draw(animation.getKeyFrame(t * totalDuration), x, y, originX, originY, width, height, 1f, 1f, rotation);
        } else {
            batch.draw(region, x, y, originX, originY, width, height, 1f, 1f, rotation);
        }
        batch.setColor(Color.WHITE);

    }
}
