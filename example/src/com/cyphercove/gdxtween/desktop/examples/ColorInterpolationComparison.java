package com.cyphercove.gdxtween.desktop.examples;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.cyphercove.covetools.utils.Disposal;
import com.cyphercove.gdxtween.desktop.ExampleScreen;
import com.cyphercove.gdxtween.desktop.ExamplesParent;
import com.cyphercove.gdxtween.desktop.SharedAssets;
import com.cyphercove.gdxtween.graphics.GtColor;
import com.kotcrab.vis.ui.widget.color.BasicColorPicker;
import com.kotcrab.vis.ui.widget.color.ColorPickerAdapter;

public class ColorInterpolationComparison extends ExampleScreen {

	public ColorInterpolationComparison(SharedAssets sharedAssets, ExamplesParent examplesParent) {
		super(sharedAssets, examplesParent);
	}

	BasicColorPicker firstColorPicker = new BasicColorPicker();
	BasicColorPicker secondColorPicker = new BasicColorPicker();
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
		Table table = new Table();
		table.setFillParent(true);
		table.pad(15);
		table.add(firstColorPicker).center().pad(20);
		firstColorPicker.setColor(firstColor);
		firstColorPicker.setListener(new ColorPickerAdapter() {
			@Override
			public void changed(Color newColor) {
				firstColor.set(newColor);
			}
		});

		Table innerTable = new Table(sharedAssets.getSkin());
		String[] types = { "Rgb", "LinearRgb", "Xyz", "Hsv", "Lab", "Lch" };
		for (int i = 0; i < types.length; i++) {
			innerTable.add(types[i]).center();
			innerTable.add(new ColorTransition(i)).growX().height(50).space(10);
			innerTable.row();
		}
		table.add(innerTable).grow();

		table.add(secondColorPicker).center().pad(20);
		secondColorPicker.setColor(secondColor);
		secondColorPicker.setListener(new ColorPickerAdapter() {
			@Override
			public void changed(Color newColor) {
				secondColor.set(newColor);
			}
		});
		stage.addActor(table);
	}

	private class ColorTransition extends Actor {
		int spaceType;

		public ColorTransition(int spaceType) {
			this.spaceType = spaceType;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			int segments = 80;
			float segmentWidth = getWidth() / segments;
			for (int i = 0; i < segments; i++) {
				float progress = (float)i / (segments - 1);
				tmpColor.set(firstColor);
				switch (spaceType) {
					case 0:
						GtColor.lerpRgb(tmpColor, secondColor, progress);
						break;
					case 1:
						GtColor.lerpLinearRgb(tmpColor, secondColor, progress);
						break;
					case 2:
						GtColor.lerpXyz(tmpColor, secondColor, progress);
						break;
					case 3:
						GtColor.lerpHsv(tmpColor, secondColor, progress);
						break;
					case 4:
						GtColor.lerpLab(tmpColor, secondColor, progress);
						break;
					case 5:
						GtColor.lerpLch(tmpColor, secondColor, progress);
						break;
				}
				batch.setColor(tmpColor);
				batch.draw(sharedAssets.getWhite(), getX() + segmentWidth * i, getY(), segmentWidth, getHeight());
			}
		}
	}

	@Override
	public void render (float dt) {
		stage.act(dt);
		stage.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		Disposal.clear(this);
	}
}
