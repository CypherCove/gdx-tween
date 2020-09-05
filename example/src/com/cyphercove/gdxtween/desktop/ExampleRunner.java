/* ******************************************************************************
 * Copyright 2020 See AUTHORS file.
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
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.covetools.utils.Disposal;
import com.cyphercove.gdxtween.desktop.examples.ColorInterpolationComparison;
import com.cyphercove.gdxtween.desktop.examples.VectorInterruption;

import static com.badlogic.gdx.math.MathUtils.random;

public class ExampleRunner extends Game implements ExamplesParent {
	Stage stage;
	final SharedAssets assets = new SharedAssets();
	final InputMultiplexer inputMultiplexer = new InputMultiplexer();

	final ExampleScreen[] screens = {
			new ColorInterpolationComparison(assets, this),
			new VectorInterruption(assets, this)
	};

	@Override
	public void create() {
		assets.create();
		setupUI();
		Gdx.input.setInputProcessor(inputMultiplexer);
		setScreen(screens[0]);
	}

	private void setupUI() {
		stage = new Stage(new ScreenViewport(), assets.spriteBatch);

		final SelectBox<ExampleScreen> selectBox = new SelectBox<>(assets.skin);
		selectBox.setItems(screens);
		selectBox.addListener(new ChangeListener() {
			public void changed(ChangeEvent event, Actor actor) {
				setScreen(selectBox.getSelected());
			}
		});
		Table table = new Table();
		table.setFillParent(true);
		table.defaults().padTop(5).left();
		table.top().left().padLeft(5);
		table.add(selectBox).row();
		table.add(new Label("", assets.skin) {
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

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
		assets.viewport.update(width, height, true);
		super.resize(width, height);
	}

	@Override
	public void setScreen(Screen screen) {
		inputMultiplexer.clear();
		inputMultiplexer.addProcessor(stage);
		super.setScreen(screen);
	}

	@Override
	public final void setScreenInputProcessors(InputProcessor... processors) {
		inputMultiplexer.clear();
		inputMultiplexer.addProcessor(stage);
		for (InputProcessor processor : processors)
			inputMultiplexer.addProcessor(processor);
	}

	@Override
	public void render() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		super.render();
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		Disposal.clear(this);
	}

}
