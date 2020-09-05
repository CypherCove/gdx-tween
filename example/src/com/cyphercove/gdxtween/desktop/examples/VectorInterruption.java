package com.cyphercove.gdxtween.desktop.examples;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.cyphercove.gdxtween.Ease;
import com.cyphercove.gdxtween.TweenRunner;
import com.cyphercove.gdxtween.desktop.ExampleScreen;
import com.cyphercove.gdxtween.desktop.ExamplesParent;
import com.cyphercove.gdxtween.desktop.SharedAssets;
import com.cyphercove.gdxtween.tweens.Tweens;

public class VectorInterruption extends ExampleScreen {

    private Stage stage;
    private final TweenRunner tweenRunner = new TweenRunner();
    private final Vector2 position = new Vector2();
    private final Color clickColor = new Color(Color.ROYAL);
    private final Vector3 temp = new Vector3();
    private boolean shouldBlend = true;
    private Sprite sprite, clickSprite;

    public VectorInterruption(SharedAssets sharedAssets, ExamplesParent examplesParent) {
        super(sharedAssets, examplesParent);
    }

    @Override
    protected String getName() {
        return "Vector interruption";
    }

    @Override
    public void show() {
        if (stage == null) {
            stage = sharedAssets.generateStage();
            setupUI();
        }
        setScreenInputProcessors(stage, inputProcessor);

        sprite = new Sprite(sharedAssets.getBadLogic());
        float size = 50;
        sprite.setSize(size, size);
        sprite.setOriginCenter();

        clickSprite = new Sprite(sharedAssets.getWhite());
        size = 15f;
        clickSprite.setSize(size, size);
        sprite.setOriginCenter();
    }

    private void setupUI () {
        CheckBox checkBox = new CheckBox("Blend interruptions", sharedAssets.getSkin());
        checkBox.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                shouldBlend = checkBox.isChecked();
            }
        });
        checkBox.setChecked(true);
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().left();
        table.add(checkBox).pad(10f);
        stage.addActor(table);
    }

    @Override
    public void resize(int width, int height) {
        tweenRunner.clearTweens(position);
        Camera camera = sharedAssets.getViewport().getCamera();
        position.set(camera.position.x, camera.position.y);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        tweenRunner.step(delta);
        stage.act();

        SpriteBatch batch = sharedAssets.getSpriteBatch();
        sprite.setOriginBasedPosition(position.x, position.y);
        clickSprite.setColor(clickColor);
        batch.enableBlending();
        batch.setColor(Color.WHITE);
        batch.setProjectionMatrix(sharedAssets.getViewport().getCamera().combined);
        batch.begin();
        batch.setColor(clickColor);
        clickSprite.draw(batch);
        sprite.draw(batch);
        batch.end();

        stage.draw();
    }

    private final InputProcessor inputProcessor = new InputAdapter() {
        @Override
        public boolean touchDown(int screenX, int screenY, int pointer, int button) {
            sharedAssets.getViewport().getCamera().unproject(temp.set(screenX, screenY, 0));
            clickSprite.setOriginBasedPosition(temp.x, temp.y);
            Tweens.to(position, temp.x, temp.y, 1f, Ease.quintic())
                    .shouldBlend(shouldBlend)
                    .start(tweenRunner);
            Tweens.alphaTo(clickColor, 1f, 0.05f, Ease.wrap(Interpolation.pow2In))
                    .thenTo(0f, 0.3f, null).delay(0.1f)
                    .start(tweenRunner);
            return true;
        }
    };
}
