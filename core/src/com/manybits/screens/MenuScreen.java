package com.manybits.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.manybits.game2048.Game2048;
import com.manybits.ui.SimpleButton;

public class MenuScreen implements Screen, InputProcessor {

    final Game2048 game;
    private SpriteBatch batch;

    private SimpleButton btnContinue;
    private SimpleButton btnNewGame;

    public MenuScreen(Game2048 game) {
        Gdx.app.log("INFO", "MenuScreen: Create");

        this.game = game;
        batch = game.batch;
        btnContinue = new SimpleButton(138, 432 + game.screenDisp, game.rgContinueBtn.getRegionWidth(), game.rgContinueBtn.getRegionHeight(), game.rgContinueBtn, game.rgContinueBtn, game.rgContinueBtnDisabled);
        if (game.continueGame) {
            btnContinue.isEnabled = true;
        } else {
            btnContinue.isEnabled = false;
        }
        btnNewGame = new SimpleButton(138, 608 + game.screenDisp, game.rgNewGameBtn.getRegionWidth(), game.rgNewGameBtn.getRegionHeight(), game.rgNewGameBtn, game.rgNewGameBtn, game.rgNewGameBtn);
    }

    @Override
    public void show() {
        Gdx.app.log("INFO", "MenuScreen: Show");
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(false);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.disableBlending();
        batch.draw(game.rgBackGround, 0, 0);
        batch.enableBlending();
        batch.draw(game.rgTitle, 18, 78 + game.screenDisp);
        btnContinue.draw(batch);
        btnNewGame.draw(batch);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        Gdx.app.log("INFO", "MenuScreen: Dispose");
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        btnNewGame.isTouchDown(screenX * game.scaleCoef, screenY * game.scaleCoef);
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (btnNewGame.isTouchDown(screenX * game.scaleCoef, screenY * game.scaleCoef) == true) {
            game.continueGame = false;
            game.setScreen(new GameScreen(game));
            dispose();
        }
        if (btnContinue.isTouchDown(screenX * game.scaleCoef, screenY * game.scaleCoef) == true) {
            game.continueGame = true;
            game.setScreen(new GameScreen(game));
            dispose();
        }
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(int amount) {
        return false;
    }

}
