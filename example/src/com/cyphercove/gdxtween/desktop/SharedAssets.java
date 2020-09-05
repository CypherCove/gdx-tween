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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.cyphercove.covetools.utils.Disposal;

public class SharedAssets implements Disposable {
    private static final int W = 800, H = 480;

    Pixmap whitePixmap;
    Texture badLogic, treeTexture, egg, wheel, white;
    SpriteBatch spriteBatch;
    Skin skin;
    Viewport viewport = new ExtendViewport(W, H);

    void create() {
        spriteBatch = new SpriteBatch(100);
        spriteBatch.enableBlending();

        skin = new Skin(Gdx.files.internal("uiskin.json"));

        badLogic = new Texture(Gdx.files.internal("badlogic.jpg"), true);
        badLogic.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        treeTexture = new Texture(Gdx.files.internal("tree.png"));
        egg = new Texture(Gdx.files.internal("egg.png"), true);
        egg.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
        wheel = new Texture(Gdx.files.internal("wheel.png"), true);
        wheel.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);

        whitePixmap = new Pixmap(16, 16, Pixmap.Format.RGBA8888);
        whitePixmap.setColor(Color.WHITE);
        whitePixmap.fill();
        white = new Texture(whitePixmap);
    }

    public Stage generateStage() {
        return new Stage(viewport, spriteBatch);
    }

    public Texture getBadLogic() {
        return badLogic;
    }

    public Texture getTreeTexture() {
        return treeTexture;
    }

    public Texture getEgg() {
        return egg;
    }

    public Texture getWheel() {
        return wheel;
    }

    public Texture getWhite() {
        return white;
    }

    public SpriteBatch getSpriteBatch() {
        return spriteBatch;
    }

    public Skin getSkin() {
        return skin;
    }

    public Viewport getViewport() {
        return viewport;
    }

    @Override
    public void dispose() {
        Disposal.clear(this);
    }
}
