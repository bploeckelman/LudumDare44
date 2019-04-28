package lando.systems.ld44.particles;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;
import com.badlogic.gdx.utils.Pools;
import lando.systems.ld44.utils.Assets;

public class ParticleManager {
    public Assets assets;

    private final Array<GenericParticle> activeParticles = new Array<GenericParticle>(false, 256);
    private final Pool<GenericParticle> particlePool = Pools.get(GenericParticle.class, 500);

    public ParticleManager(Assets assets) {
        this.assets = assets;
    }

    public void update(float dt) {
        for (int i = activeParticles.size -1; i >=0; i--){
            GenericParticle part = activeParticles.get(i);
            part.update(dt);
            if (part.ttl <= 0) {
                activeParticles.removeIndex(i);
                particlePool.free(part);
            }
        }
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < activeParticles.size; i++) {
            activeParticles.get(i).render(batch);
        }
    }

    public void addGroundPoundDust(float x, float y, float minX, float maxX) {
        for (int i = (int)minX; i < maxX; i++){
            float distance = x - i;
            // floating dust
            for (int count = 0; count < 2; count++) {
                GenericParticle particle = particlePool.obtain();
                float size = MathUtils.random(1f, 2f);
                float ttl = MathUtils.random(.4f, .9f);
                particle.init(assets.whiteCircle,
                        size, size - 1f, size, size - 1f,
                        i, y, MathUtils.random(-5, 5) - distance, 40 + MathUtils.random(100), 0, -200, 1,
                        GenericParticle.OriginType.CENTER, 0, 0,
                        .8f, .8f, .5f, 1f,
                        0f, 0f, 0f, .5f,
                        0, 0, ttl, Math.abs(distance) / 500f);
                activeParticles.add(particle);
            }

            // Red on the ground
            GenericParticle particle = particlePool.obtain();
            particle.init(assets.whiteCircle,
                    8, 8, 2, 2,
                    i, y, 0,5, 0,0,0,
                    GenericParticle.OriginType.CENTER, 0,0,
                    1, 0, 0, 1,
                    1f, 0,0,0,
                    0, 0, .5f, Math.abs(distance)/ 500f);
            activeParticles.add(particle);
        }
    }
}
