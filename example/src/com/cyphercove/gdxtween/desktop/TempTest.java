package com.cyphercove.gdxtween.desktop;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.cyphercove.gdxtween.Ease;
import com.cyphercove.gdxtween.TweenManager;
import com.cyphercove.gdxtween.graphics.ColorConversion;
import com.cyphercove.gdxtween.tweens.Tweens;

public class TempTest extends ApplicationAdapter {
	SpriteBatch batch;
	Texture img;
	TweenManager tweenManager = new TweenManager();
	Color color = new Color();
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		img = new Texture("badlogic.jpg");
		color.a = 1f;
		Tweens.toViaLch(color, 0.5f, 1f, 0.8f, 1f, Ease.cubic())
				.thenTo(0.4f, 0.5f, 1f, 1f, Ease.cubic())
				.loop()
				.start(tweenManager);
	}

	@Override
	public void render () {
		tweenManager.step(Gdx.graphics.getDeltaTime());
		Gdx.gl.glClearColor(0, 0, .22f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		batch.enableBlending();
		batch.begin();
		batch.setColor(color);
		batch.draw(img, 0, 0);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}
}
