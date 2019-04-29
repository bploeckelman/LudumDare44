package lando.systems.ld44.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import lando.systems.ld44.entities.Coin;
import lando.systems.ld44.entities.Player;
import lando.systems.ld44.screens.GameScreen;

public class Hud {

    public Player player;
    public GameScreen screen;

    private boolean shuffling;
    private Array<Coin> initialCoinOrder;
    private Array<Coin> shuffledCoinOrder;
    private Rectangle nextCoinRect;
    private Rectangle coinGutterRect;

    public Hud(GameScreen screen) {
        this.screen = screen;
        this.player = screen.player;

        this.shuffling = false;
        this.initialCoinOrder = new Array<Coin>();
        this.shuffledCoinOrder = new Array<Coin>();
        this.nextCoinRect = new Rectangle();
        this.coinGutterRect = new Rectangle();
    }

    public void update(float dt) {
        // TODO: check for ground just pounded to play coin purse reshuffle animation
    }

    public void triggerCoinShuffle() {
        // Store previous locations


        // Shuffle
        player.coinPurse.shuffle();

        // Store new locations

        // Trigger an animation to reorder
    }

    public void render(SpriteBatch batch, OrthographicCamera camera) {
        // TODO: select source coin purse based on state (normal, shuffling, shuffled)
        Array<Coin> coins = player.coinPurse;

        float scale = 2f;
        float margin = 10f;
        float nextCoinSize = scale * 32f;

        nextCoinRect.set(margin, camera.viewportHeight - margin - nextCoinSize, nextCoinSize, nextCoinSize);

        // Draw next coin
        batch.setColor(0.2f, 0.3f, 0.2f, 0.33f);
        batch.draw(screen.assets.whitePixel, nextCoinRect.x, nextCoinRect.y, nextCoinRect.width, nextCoinRect.height);
        batch.setColor(Color.WHITE);
        screen.assets.ninePatch.draw(batch, nextCoinRect.x, nextCoinRect.y, nextCoinRect.width, nextCoinRect.height);
        if (!coins.isEmpty()) {
            Coin nextCoin = coins.first();
            if (nextCoin != null) {
                TextureRegion coinKeyFrame = nextCoin.animation.getKeyFrame(0f);
                float w = scale * coinKeyFrame.getRegionWidth();
                float h = scale * coinKeyFrame.getRegionHeight();
                batch.draw(coinKeyFrame,
                           nextCoinRect.x + nextCoinRect.width / 2f - w / 2f,
                           nextCoinRect.y + nextCoinRect.height / 2f - h / 2f, w, h);
            }
        }

        // Calculate height of coins in coin gutter
        float height = margin;
        for (Coin coin : coins) {
            // Skip first coin since it'll be in the 'nextCoinRect' and not in the coin stack
//            if (coin == coins.first()) continue;
            height += scale * (coin.animation.getKeyFrame(0f).getRegionHeight() / 2f);
        }
        height += margin;
        coinGutterRect.set(margin, nextCoinRect.y - height - margin, nextCoinSize, height);//camera.viewportHeight - margin - nextCoinRect.height - margin);

        // Draw coin gutter
        batch.setColor(0.2f, 0.2f, 0.2f, 0.33f);
        batch.draw(screen.assets.whitePixel, coinGutterRect.x, coinGutterRect.y, coinGutterRect.width, coinGutterRect.height);
        batch.setColor(Color.WHITE);
        screen.assets.ninePatch.draw(batch, coinGutterRect.x, coinGutterRect.y, coinGutterRect.width, coinGutterRect.height);

        // Draw coins in gutter
        float x = coinGutterRect.x;
        float y = coinGutterRect.y + margin;
        for (int i = coins.size - 1; i > 0; --i) {
            Coin coin = coins.get(i);
            TextureRegion coinKeyFrame = coin.animation.getKeyFrame(0f);
            float w = scale * coinKeyFrame.getRegionWidth();
            float h = scale * coinKeyFrame.getRegionHeight();
            batch.draw(coinKeyFrame, x + coinGutterRect.width / 2f - w / 2f, y, w, h);

            y += h / 2f;
        }
    }

    public void renderHorizontal(SpriteBatch batch, OrthographicCamera camera) {
        // TODO: select source coin purse based on state (normal, shuffling, shuffled)
        Array<Coin> coins = player.coinPurse;

        // TODO: switch this to bottom to top on the left edge of the screen

        float margin = 10f;
        float scale = 2f;
        float largestCoinHeight = scale * 28f;
        float y = camera.viewportHeight - margin - largestCoinHeight;

        float onDeckSize = scale * 32f;
        nextCoinRect.set(margin, y, onDeckSize, onDeckSize);

        float gutterLeftX = nextCoinRect.x + nextCoinRect.width + margin;
        // TODO: figure out maximum width of coin tray and draw gutter for it
        float gutterWidth = 0f;
        for (Coin coin : coins) {
            TextureRegion keyframe = coin.animation.getKeyFrame(0f);
            float w = scale * keyframe.getRegionWidth();
            gutterWidth += w / 2f;
        }

        float x = gutterLeftX + gutterWidth;
        for (int i = coins.size - 1; i > 0; --i) {
            Coin coin = coins.get(i);

            TextureRegion keyframe = coin.animation.getKeyFrame(0f);
            float w = scale * keyframe.getRegionWidth();
            float h = scale * keyframe.getRegionHeight();
            batch.draw(keyframe, x - w / 2f, y + largestCoinHeight / 2f - h / 2f, w, h);

            x -= w / 2f;
        }

        batch.setColor(0.2f, 0.2f, 0.2f, 0.5f);
        batch.draw(screen.assets.whitePixel, nextCoinRect.x, nextCoinRect.y, nextCoinRect.width, nextCoinRect.height);
        batch.setColor(Color.WHITE);

        screen.assets.ninePatch.draw(batch, nextCoinRect.x, nextCoinRect.y, nextCoinRect.width, nextCoinRect.height);

        if (!coins.isEmpty()) {
            Coin onDeckCoin = coins.first();
            if (onDeckCoin != null) {
                TextureRegion keyframe = onDeckCoin.animation.getKeyFrame(0f);
                float w = scale * keyframe.getRegionWidth();
                float h = scale * keyframe.getRegionHeight();
                batch.draw(keyframe,
                           nextCoinRect.x + nextCoinRect.width / 2f - w / 2f,
                           nextCoinRect.y + nextCoinRect.height / 2f - h / 2f, w, h);
            }
        }
    }

}
