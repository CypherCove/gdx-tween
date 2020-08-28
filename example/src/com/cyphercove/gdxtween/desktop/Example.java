/* ******************************************************************************
 * Copyright 2017 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.cyphercove.gdxtween.desktop;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.covetools.utils.Disposal;
import static com.badlogic.gdx.math.MathUtils.random;

public class Example extends ApplicationAdapter {
	Texture texture, treeTexture, egg, wheel;
	SpriteBatch spriteBatch;
	PerspectiveCamera pCam = new PerspectiveCamera();
	Stage stage;
	Skin skin;
	Viewport viewport;
	Test test = Test.values()[0];
	Sprite testSprite;
	BitmapFont testFont;
	PolygonRegion polygonRegion;
	private static final int W = 800, H = 480;
	float elapsed;
	Vector3 bumpLightPosition = new Vector3();
	boolean bumpLightFollowCursor;

	private enum Test {
		BumpMapped2D, Poly2D, Quad3D, Point3D, CompliantBatch, SolidQuads, TripleOverlay
	}

	@Override
	public void create() {
		random.setSeed(0);

		viewport = new ExtendViewport(W, H);

		texture = new Texture("badlogic.jpg");

		testSprite = new Sprite(texture);
		testSprite.setPosition(50, 102);
		testSprite.setColor(0, 1, 0, 0.6f);

		testFont = new BitmapFont(Gdx.files.internal("arial-32-pad.fnt"), false);
		testFont.setColor(Color.CYAN);

		treeTexture = new Texture(Gdx.files.internal("tree.png"));
		PolygonRegionLoader loader = new PolygonRegionLoader();
		polygonRegion = loader.load(new TextureRegion(treeTexture), Gdx.files.internal("tree.psh"));

		spriteBatch = new SpriteBatch(100);
		spriteBatch.enableBlending();

		egg = new Texture(Gdx.files.internal("egg.png"));
		egg.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		wheel = new Texture(Gdx.files.internal("wheel.png"));
		wheel.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

		setupUI();

		Gdx.input.setInputProcessor(new InputMultiplexer(stage, bumpInputAdapter));
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		stage.getViewport().update(width, height, true);
		pCam.viewportWidth = width;
		pCam.viewportHeight = height;
		pCam.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void render() {
		float dt = Gdx.graphics.getDeltaTime();
		elapsed += dt;

		Gdx.gl.glClearColor(0.1f, 0.125f, 0.35f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		viewport.apply();

		stage.act();
		stage.draw();
	}

	private void setupUI() {
		stage = new Stage(new ScreenViewport(), spriteBatch);
		skin = new Skin(Gdx.files.internal("uiskin.json"));

		final SelectBox<Test> selectBox = new SelectBox<>(skin);
		selectBox.setItems(Test.values());
		selectBox.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				test = selectBox.getSelected();
				elapsed = 0;
			}
		});
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().padTop(5).left();
		table.top().left().padLeft(5);
		table.add(selectBox).row();
		table.add(new Label("", skin) {
			int fps = -1;

			public void act(float delta) {
				super.act(delta);
				if (Gdx.graphics.getFramesPerSecond() != fps) {
					fps = Gdx.graphics.getFramesPerSecond();
					setText("" + fps);
				}
			}
		}).row();
		stage.addActor(table);
	}

	public void dispose() {
		Disposal.clear(this);
	}

	private final InputAdapter bumpInputAdapter = new InputAdapter() {

		final Vector3 tmp = new Vector3();

		public boolean touchDown(int screenX, int screenY, int pointer, int button) {
			bumpLightFollowCursor = true;
			viewport.unproject(tmp.set(screenX, screenY, 0));
			bumpLightPosition.x = tmp.x;
			bumpLightPosition.y = tmp.y;
			return true;
		}

		public boolean touchDragged(int screenX, int screenY, int pointer) {
			viewport.unproject(tmp.set(screenX, screenY, 0));
			bumpLightPosition.x = tmp.x;
			bumpLightPosition.y = tmp.y;
			return true;
		}

		public boolean touchUp(int screenX, int screenY, int pointer, int button) {
			bumpLightFollowCursor = false;
			return false;
		}
	};
}
