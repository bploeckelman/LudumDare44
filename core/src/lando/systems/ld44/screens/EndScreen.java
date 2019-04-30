package lando.systems.ld44.screens;

import aurelienribon.tweenengine.primitives.MutableFloat;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import lando.systems.ld44.Game;
import lando.systems.ld44.utils.Assets;
import lando.systems.ld44.utils.Audio;

public class EndScreen extends BaseScreen {


    private GlyphLayout layout;

    private Texture background;
    private Texture subtitle;
    private Texture titleKeyFrame;
    private Texture couchKeyFrame;
    private Animation<Texture> titleAnimation;
    private Animation<Texture> couchAnimation;


    private Rectangle titleBounds;
    private Rectangle subtitleBounds;
    private Rectangle couchBounds;

    private Rectangle dialogBounds;

    private String vanityText;
    private String vanityShadow;
    private String maxText;
    private String maxShadow;
    private String clickToContinue;
    private String devLeftText;
    private String devLeftShadow;
    private String devRightText;
    private String devRightShadow;
    private String thanks;

    private boolean next = false;

    public EndScreen(Game game, Assets assets) {
        super(game, assets);

        //game.audio.playMusic(Audio.Musics.EndScreen);

        layout = new GlyphLayout();
        Gdx.input.setInputProcessor(this);

        background = assets.mgr.get(assets.titleBackgroundTextureAsset);
        subtitle = assets.mgr.get(assets.titleSubtitleTextureAsset);

        titleAnimation = assets.titleAnimation;
        titleKeyFrame = titleAnimation.getKeyFrame(0f);
        couchAnimation = assets.couchWakeAnimation;
        couchKeyFrame = couchAnimation.getKeyFrame(0f);

        dialogBounds = new Rectangle(25, 18, hudCamera.viewportWidth - 50, 338);

        float halfScreenWidth = hudCamera.viewportWidth / 2f;
        titleBounds = new Rectangle(halfScreenWidth - titleKeyFrame.getWidth() / 2f, hudCamera.viewportHeight - titleKeyFrame.getHeight(), titleKeyFrame.getWidth(), titleKeyFrame.getHeight());
        subtitleBounds = new Rectangle(halfScreenWidth - subtitle.getWidth() / 2f - 5f, 10f + couchKeyFrame.getHeight() - 35f, subtitle.getWidth(), subtitle.getHeight());
        couchBounds = new Rectangle(halfScreenWidth - couchKeyFrame.getWidth() / 2f, 10f, couchKeyFrame.getWidth(), couchKeyFrame.getHeight());

        String colorTag = "[RED]";
        StringBuilder sb = new StringBuilder(200);
        sb.append("Your Score\n\nCoins Collected\n");
        sb.append(colorTag);
        sb.append(game.stats.coinsCollected);
        sb.append("[]\nCoins Spit\n");
        sb.append(colorTag);
        sb.append(game.stats.coinsSpit);
        sb.append("[]\nTimes hit\n");
        sb.append(colorTag);
        sb.append(game.stats.timesHit);
        sb.append("[]\nGround Pounds\n");
        sb.append(colorTag);
        sb.append(game.stats.groundPounds);
        sb.append("[]\nTime (seconds)\n");
        sb.append(colorTag);
        sb.append((int) game.stats.secondsToWin);
        sb.append("s[]");

        vanityText = sb.toString();
        vanityShadow = vanityText.replace(colorTag, "[BLACK]");

        sb = new StringBuilder(200);
        sb.append("All Time Best\n\nCoins Collected\n");
        sb.append(colorTag);
        sb.append(game.stats.coinsCollected + getRandom(20));
        sb.append("[]\nCoins Spit\n");
        sb.append(colorTag);
        sb.append(game.stats.coinsSpit - getRandom(5));
        sb.append("[]\nTimes hit\n");
        sb.append(colorTag);
        sb.append(game.stats.timesHit - getRandom(4));
        sb.append("[]\nGround Pounds\n");
        sb.append(colorTag);
        sb.append(game.stats.groundPounds - getRandom(4));
        sb.append("[]\nTime (seconds)\n");
        sb.append(colorTag);
        sb.append((int) game.stats.secondsToWin - getRandom(40));
        sb.append("s[]");

        maxText = sb.toString();
        maxShadow = maxText.replace(colorTag, "[BLACK]");

        clickToContinue = "Click to continue...";

        thanks = "Thanks for playing our Sofa Kingdom game for Ludum Dare 44";

        sb = new StringBuilder(100);
        sb.append(colorTag);
        sb.append("Code[]\n");
        sb.append("  Brian Rossman\n  Doug Graham\n  Brian Ploeckelman\n\n");
        sb.append(colorTag);
        sb.append("Art[]\n");
        sb.append("  Matt Neumann\n  Troy Sullivan\n  Luke Bain\n");
        devLeftText = sb.toString();
        devLeftShadow = devLeftText.replace(colorTag, "[BLACK]");

        sb = new StringBuilder(100);
        sb.append(colorTag);
        sb.append("Music and Sound[]\n");
        sb.append("Luke Bain  \nDoug Graham  \n\n");
        sb.append(colorTag);
        sb.append("Level Design[]\n");
        sb.append("C.J. Nelson  \nEthan Burrus  ");
        devRightText = sb.toString();
        devRightShadow = devRightText.replace(colorTag, "[BLACK]");
    }

    private int getRandom(int max) {
        return 1+  (int)(Math.random() * max);
    }

    private float time = 0;
    @Override
    public void update(float dt) {
        time += dt;
        titleKeyFrame = titleAnimation.getKeyFrame(time);
        couchKeyFrame = couchAnimation.getKeyFrame(time);

        if (Gdx.input.justTouched()) {
            next = !next;
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(hudCamera.combined);
        batch.begin();
        {
            batch.setColor(Color.WHITE);
            batch.draw(background, 0f, 0f, hudCamera.viewportWidth, hudCamera.viewportHeight);
            batch.draw(titleKeyFrame, titleBounds.x, titleBounds.y, titleBounds.width, titleBounds.height);
            batch.draw(couchKeyFrame, couchBounds.x, couchBounds.y, couchBounds.width, couchBounds.height);

            batch.setColor(0.2f, 0.2f, 0.2f, 0.7f);
            batch.draw(assets.whitePixel, dialogBounds.x, dialogBounds.y, dialogBounds.width, dialogBounds.height);
            batch.setColor(Color.WHITE);
            assets.ninePatch.draw(batch, dialogBounds.x, dialogBounds.y, dialogBounds.width, dialogBounds.height);

            float margin = 20f;
            float scale = 1.1f;
            float scaleX = assets.font.getData().scaleX;
            float scaleY = assets.font.getData().scaleY;

            if (next) {
                float tyY = dialogBounds.y + dialogBounds.height - margin;
                float devY = tyY - 50;

                assets.font.getData().setScale(scale);
                layout.setText(assets.font, thanks, Color.BLACK, dialogBounds.width - 2f * margin, Align.center, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, tyY - 2f);
                layout.setText(assets.font, thanks, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.center, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin ,  tyY);

                layout.setText(assets.font, devLeftShadow, Color.BLACK, dialogBounds.width - 2f * margin, Align.left, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, devY - 2f);
                layout.setText(assets.font, devLeftText, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.left, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin ,  devY);

                layout.setText(assets.font, devRightShadow, Color.BLACK, dialogBounds.width - 2f * margin, Align.right, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, devY - 2f);
                layout.setText(assets.font, devRightText, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.right, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin , devY);

            } else {
                assets.font.getData().setScale(scale);
                layout.setText(assets.font, vanityShadow, Color.BLACK, dialogBounds.width - 2f * margin, Align.left, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, dialogBounds.y + dialogBounds.height - margin - 2f);
                layout.setText(assets.font, vanityText, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.left, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin, dialogBounds.y + dialogBounds.height - margin);
                layout.setText(assets.font, maxShadow, Color.BLACK, dialogBounds.width - 2f * margin, Align.right, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin + 2f, dialogBounds.y + dialogBounds.height - margin - 2f);
                layout.setText(assets.font, maxText, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.right, true);
                assets.font.draw(batch, layout, dialogBounds.x + margin, dialogBounds.y + dialogBounds.height - margin);
            }
            layout.setText(assets.font, clickToContinue, Color.BLACK, dialogBounds.width - 2f * margin, Align.center, true);
            assets.font.draw(batch, layout, dialogBounds.x + margin + 2, dialogBounds.y + 25);
            layout.setText(assets.font, clickToContinue, Color.LIGHT_GRAY, dialogBounds.width - 2f * margin, Align.center, true);
            assets.font.draw(batch, layout, dialogBounds.x + margin, dialogBounds.y + 25);

            assets.font.getData().setScale(scaleX, scaleY);
        }
        batch.end();
    }
}
