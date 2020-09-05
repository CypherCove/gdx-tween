package com.cyphercove.gdxtween.desktop.examples;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.gdxtween.desktop.ExampleScreen;
import com.cyphercove.gdxtween.desktop.ExamplesParent;
import com.cyphercove.gdxtween.desktop.SharedAssets;
import com.cyphercove.gdxtween.graphics.GtColor;

public class ColorInterpolationComparison extends ExampleScreen {

	public ColorInterpolationComparison(SharedAssets sharedAssets, ExamplesParent examplesParent) {
		super(sharedAssets, examplesParent);
	}

	Color color = new Color();
	Color firstColor = new Color(Color.BLUE);
	Color secondColor = new Color(Color.YELLOW);
	Color tmpColor = new Color(Color.WHITE);
	Stage stage;

	@Override
	protected String getName() {
		return "Color interpolation comparison";
	}

	@Override
	public void show () {
		color.a = 1f;
		if (stage == null) {
			stage = sharedAssets.generateStage();
			setupUI();
		}
		setScreenInputProcessors(stage);
	}

	private void setupUI () {

	}

	@Override
	public void render (float dt) {
		SpriteBatch batch = sharedAssets.getSpriteBatch();
		Viewport viewport = sharedAssets.getViewport();
		Texture white = sharedAssets.getWhite();

		batch.enableBlending();
		batch.setProjectionMatrix(viewport.getCamera().combined);
		batch.begin();

		int types = 5;
		float topBottomPadding = 0.15f; // as fraction of screen height
		float rowSpacing = 0.15f; // as fraction of screen height
		float width = viewport.getCamera().viewportWidth * 0.5f;
		float height = viewport.getCamera().viewportHeight * (1f - topBottomPadding * 2f);
		float rowSpace = rowSpacing * height / (types - 1);
		float rowHeight = height * (1f - rowSpacing) / types;
		float left = viewport.getCamera().position.x - width / 2;
		float bottom = viewport.getCamera().position.y - height / 2;
		int segments = 20;
		float segmentWidth = width / segments;
		for (int type = 0; type < types; type++) {
			for (int i = 0; i < segments; i++) {
				float progress = (float)i / (segments - 1);
				tmpColor.set(firstColor);
				switch (type) {
					case 4:
						GtColor.lerpRgb(tmpColor, secondColor, progress);
						break;
					case 3:
						GtColor.lerpRgbGamma(tmpColor, secondColor, progress);
						break;
					case 2:
						GtColor.lerpHsv(tmpColor, secondColor, progress);
						break;
					case 1:
						GtColor.lerpLab(tmpColor, secondColor, progress);
						break;
					case 0:
						GtColor.lerpLch(tmpColor, secondColor, progress);
						break;
				}
				batch.setColor(tmpColor);
				batch.draw(white, i * segmentWidth + left, type * (rowHeight + rowSpace) + bottom, segmentWidth, rowHeight);
			}
		}

		batch.end();
	}
}
