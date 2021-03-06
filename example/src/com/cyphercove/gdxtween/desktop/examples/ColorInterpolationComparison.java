package com.cyphercove.gdxtween.desktop.examples;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Widget;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.cyphercove.covetools.utils.Disposal;
import com.cyphercove.gdxtween.desktop.ExampleScreen;
import com.cyphercove.gdxtween.desktop.ExamplesParent;
import com.cyphercove.gdxtween.desktop.SharedAssets;
import com.cyphercove.gdxtween.graphics.ColorSpace;
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

	private static final ObjectMap<String, ColorSpace> choices = new OrderedMap<>();
	static {
		choices.put("RGB", ColorSpace.Rgb);
		choices.put("Linear RGB", ColorSpace.DegammaRgb);
		choices.put("Lab", ColorSpace.DegammaLab);
		choices.put("LMS Compressed", ColorSpace.DegammaLmsCompressed);
		choices.put("IPT", ColorSpace.DegammaIpt);
		choices.put("Lch", ColorSpace.DegammaLch);
		choices.put("HSL", ColorSpace.Hsl);
		choices.put("HCL", ColorSpace.Hcl);
		choices.put("HSV", ColorSpace.Hsv);
	}

	@Override
	protected String getName() {
		return "Color interpolation comparison";
	}

	@Override
	public void show () {
		GtColor.lerp(new Color(Color.BLACK), Color.RED, 0f, ColorSpace.DegammaHcl, false);

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
		for (ObjectMap.Entry<String, ColorSpace> entry : choices) {
			innerTable.add(entry.key).center();
			innerTable.add(new ColorTransition(entry.value)).growX().height(30).space(10);
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

	private class ColorTransition extends Widget {
		ColorSpace colorSpace;

		public ColorTransition(ColorSpace colorSpace) {
			this.colorSpace = colorSpace;
		}

		@Override
		public void draw(Batch batch, float parentAlpha) {
			int segments = 80;
			float segmentWidth = getWidth() / segments;
			for (int i = 0; i < segments; i++) {
				float progress = (float)i / (segments - 1);
				tmpColor.set(firstColor);
				GtColor.lerp(tmpColor, secondColor, progress, colorSpace, false);
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
