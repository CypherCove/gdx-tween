package com.cyphercove.gdxtween.desktop.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Matrix3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.gdxtween.Ease;
import com.cyphercove.gdxtween.TweenInterruptionListener;
import com.cyphercove.gdxtween.TweenRunner;
import com.cyphercove.gdxtween.Tweens;
import com.cyphercove.gdxtween.desktop.ExampleScreen;
import com.cyphercove.gdxtween.desktop.ExamplesParent;
import com.cyphercove.gdxtween.desktop.SharedAssets;
import com.cyphercove.gdxtween.targettweens.Vector2Tween;

public class SequenceScene extends ExampleScreen {

    private Stage stage;
    private final TweenRunner tweenRunner = new TweenRunner();
    private final Vector2 sunPosition = new Vector2(400f, 260f);
    private final Vector2 cloudPosition = new Vector2();
    private final Color hillsTintColor = new Color(Color.FOREST);
    private final Color skyTopColor = new Color(Color.BLUE);
    private final Color skyBottomColor = new Color(Color.SKY);
    private final Color sunColor = new Color(Color.YELLOW);
    private final Color cloudTintColor = new Color(Color.PINK);
    private final Viewport sceneViewport = new FitViewport(800f, 480f);
    private ShaderProgram verticalGradientShader;

    private static final Matrix3 RGB_TO_LMS = new Matrix3();
    private static final Matrix3 LMS_TO_RGB = new Matrix3();

    static {
        float[] values = {0.313921f, 0.639468f, 0.046597f,
                0.151693f, 0.748209f, 0.1000044f,
                0.017753f, 0.109468f, 0.872969f};
        RGB_TO_LMS.set(values);
        LMS_TO_RGB.set(values).inv();
    }

    public SequenceScene(SharedAssets sharedAssets, ExamplesParent examplesParent) {
        super(sharedAssets, examplesParent);
    }

    @Override
    protected String getName() {
        return "Sequence scene";
    }

    @Override
    public void show() {
        if (stage == null) {
            stage = sharedAssets.generateStage();
            setupUI();
        }
        setScreenInputProcessors(stage, inputAdapter);
        loadShader();
        animateScene();
    }

    private void loadShader() {
        if (verticalGradientShader != null)
            verticalGradientShader.dispose();
        verticalGradientShader = new ShaderProgram(Gdx.files.internal("verticalGradient.vert").readString(),
                Gdx.files.internal("verticalGradient.frag").readString());
        Gdx.app.log("verticalGradientShader log", verticalGradientShader.getLog());
    }

    private void setupUI() {
        TextButton button = new TextButton("Restart", sharedAssets.getSkin());
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                animateScene();
            }
        });
        Table table = new Table();
        table.setFillParent(true);
        table.bottom().left();
        table.add(button).pad(10f);
        stage.addActor(table);
    }

    private int runNumber = 0;
    private void animateScene() {
        Gdx.app.log("animateScene", "Starting tween no. " + runNumber);
        Tweens.inSequence().name("Top level " + runNumber)
                .inParallel()
                    .using(runNumber == 0 ? 0f : 0.3f, Ease.quintic())
                    .run(Tweens.to(sunPosition, 360f, 300f).name("sun position return " + runNumber).interruptionListener(logNameOnInterrupt))
                    .run(Tweens.toRgb(sunColor, 1f, 240f/255f, 142/255f))
                    .run(Tweens.toAlpha(sunColor, 1f))
                    .run(Tweens.to(cloudPosition, -sharedAssets.getCloud().getWidth(), 320f))
                    .run(Tweens.toRgb(cloudTintColor, 194f/255f,237f/255f, 1f))
                    .run(Tweens.toRgb(hillsTintColor, 69f/255f, 187f/255f, 8f/255f))
                    .run(Tweens.toRgb(skyTopColor, 16f/255f, 55f/255f,214/255f))
                    .run(Tweens.toRgb(skyBottomColor, 58f/255f, 162f/255f, 242f/255f))
                .then()
                .inParallel()
                    .run(Tweens.to(sunPosition, 320f, -sharedAssets.getSun().getHeight()).using(7f, Ease.linear).name("sun position down " + runNumber).interruptionListener(logNameOnInterrupt))
                    .run(Tweens.inSequence()
                            .delay(1f)
                            .run(Tweens.toRgb(sunColor, 1f, 103/255f, 16f/255f).duration(3f))
                            .run(Tweens.toAlpha(sunColor, 0.3f).duration(1f))
                    )
                    .run(Tweens.inSequence()
                            .delay(1f)
                            .inParallel()
                                .duration(3f)
                                .run(Tweens.toRgb(skyTopColor, 102f/255f,16f/255f,214f/255f))
                                .run(Tweens.toRgb(skyBottomColor, 238f/255f, 119f/255f, 5f/255f))
                                .run(Tweens.toRgb(cloudTintColor, 218f/255f,155f/255f,221f/255f))
                            .then()
                            .inParallel()
                                .duration(3f)
                                .run(Tweens.toRgb(skyTopColor, 0f, 0f, 0f))
                                .run(Tweens.toRgb(skyBottomColor, 21f/255f,17f/255f,59f/255f))
                                .run(Tweens.toRgb(cloudTintColor, 104f/255f,65f/255f,130f/255f))
                                .run(Tweens.toRgb(hillsTintColor, 0.15f, 0.15f, 0.15f))
                    )
                    .run(Tweens.to(cloudPosition, 800f, 320f).duration(7f).ease(Ease.linear))
                .then()
                .start(tweenRunner);
        runNumber++;
    }

    private TweenInterruptionListener<Vector2Tween> logNameOnInterrupt = new TweenInterruptionListener<Vector2Tween>() {
        @Override
        public void onTweenInterrupted(Vector2Tween interruptedTween, Vector2Tween interruptionSource) {
            Gdx.app.log("Interrupted", interruptedTween.getName());
        }
    };

    @Override
    public void resize(int width, int height) {
        sceneViewport.update(width, height, true);
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        tweenRunner.step(delta);
        stage.act();

        sceneViewport.apply();

        SpriteBatch batch = sharedAssets.getSpriteBatch();
        batch.enableBlending();
        batch.setProjectionMatrix(sceneViewport.getCamera().combined);
        batch.begin();

        batch.setShader(verticalGradientShader);
        verticalGradientShader.setUniformMatrix("u_rgbToLms", RGB_TO_LMS);
        verticalGradientShader.setUniformMatrix("u_lmsToRgb", LMS_TO_RGB);
        verticalGradientShader.setUniformf("u_topColor", skyTopColor);
        verticalGradientShader.setUniformf("u_bottomColor", skyBottomColor);
        batch.draw(sharedAssets.getWhite(), 0f, 0f, 800f, 480f);
        batch.setShader(null);

        batch.setColor(sunColor);
        batch.draw(sharedAssets.getSun(), sunPosition.x, sunPosition.y);
        batch.setColor(Color.WHITE);
        batch.draw(sharedAssets.getSunFace(), sunPosition.x, sunPosition.y);
        batch.setColor(cloudTintColor);
        batch.draw(sharedAssets.getCloud(), cloudPosition.x, cloudPosition.y);
        batch.setColor(hillsTintColor);
        batch.draw(sharedAssets.getHills(), 0f, 0f);
        batch.end();

        stage.getViewport().apply();
        stage.draw();
    }

    private final InputAdapter inputAdapter = new InputAdapter(){
        @Override
        public boolean keyDown(int keycode) {
            switch (keycode) {
                case Input.Keys.R:
                    loadShader();
                    break;
            }
            return true;
        }
    };

    @Override
    public void dispose() {
        super.dispose();
        if (verticalGradientShader != null) {
            verticalGradientShader.dispose();
            verticalGradientShader = null;
        }
    }
}
