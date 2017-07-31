package com.manybits.game2048;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.manybits.screens.MenuScreen;

public class Game2048 extends Game {

	public class Pic {
		public int value;
		public TextureRegion rgChip;
	}

	OrthographicCamera camera;
	public SpriteBatch batch;
	Preferences prefs;
	public BitmapFont font;

	TextureAtlas atlas;

	public TextureRegion rgBackGround;
	public TextureRegion rgField;
	public TextureRegion rgFieldCover;
	public TextureRegion rgTitle;
	public TextureRegion rgNewGameBtn;
	public TextureRegion rgContinueBtn;
	public TextureRegion rgContinueBtnDisabled;
	public TextureRegion rgScore;
	public TextureRegion rgHighscore;
	public TextureRegion rgRestartBtnActive;
	public TextureRegion rgRestartBtn;
	public TextureRegion rgUndoBtn;
	public TextureRegion rgGameOverLbl;
	public TextureRegion rgNewGameLbl;
	public TextureRegion rgYouWinLbl;
	public TextureRegion rgYesLbl;
	public TextureRegion rgNoLbl;

	public Array<Pic> chipsPic;

	public Sprite spFieldCover;
	public Sprite spGameOver;
	public Sprite spNewGame;
	public Sprite spYouWin;
	public Sprite spYes;
	public Sprite spNo;

	public int highScore;
	float gameWidth;
	float gameHeight;
	public float scaleCoef;
	public int screenDisp;
	final int minScreenHeight = 918;
	public boolean continueGame = false;

	@Override
	public void create () {
		Gdx.app.log("INFO", "Game2048: Create");

		float screenWidth = Gdx.graphics.getWidth();
		float screenHeight = Gdx.graphics.getHeight();

		gameWidth = 700;
		gameHeight = screenHeight / (screenWidth / gameWidth);

		scaleCoef = gameWidth / screenWidth;

		screenDisp = (int)(gameHeight - 100 - minScreenHeight) / 2;

		camera = new OrthographicCamera();
		camera.setToOrtho(true, gameWidth, gameHeight);

		batch = new SpriteBatch();
		batch.setProjectionMatrix(camera.combined);

		prefs = Gdx.app.getPreferences("Highscore");
		loadHighscore();

		loadAssets();

		this.setScreen(new MenuScreen(this));
	}

	@Override
	public void dispose() {
		Gdx.app.log("INFO", "Game2048: Dispose");

		saveHighscore();
		atlas.dispose();
		font.dispose();
		super.dispose();
	}

	public void saveHighscore() {
		prefs.putInteger("Highscore", highScore);
		prefs.putBoolean("Continue", continueGame);
		prefs.flush();
	}

	public void loadHighscore() {
		highScore = prefs.getInteger("Highscore", 0);
		continueGame = prefs.getBoolean("Continue", false);
	}

	private void loadAssets() {
		atlas = new TextureAtlas(Gdx.files.internal("Textures.pack"));

		rgBackGround = atlas.findRegion("BackGround");
		rgBackGround.flip(false, true);
		rgField = atlas.findRegion("Field");
		rgField.flip(false, true);
		rgFieldCover = atlas.findRegion("FieldCover");
		rgFieldCover.flip(false, true);
		rgTitle = atlas.findRegion("Title");
		rgTitle.flip(false, true);
		rgNewGameBtn = atlas.findRegion("NewGameBtn");
		rgNewGameBtn.flip(false, true);
		rgContinueBtn = atlas.findRegion("ContinueBtn");
		rgContinueBtn.flip(false, true);
		rgContinueBtnDisabled = atlas.findRegion("ContinueBtnDisabled");
		rgContinueBtnDisabled.flip(false, true);
		rgScore = atlas.findRegion("ScoreLbl");
		rgScore.flip(false, true);
		rgHighscore = atlas.findRegion("HighscoreLbl");
		rgHighscore.flip(false, true);
		rgRestartBtnActive = atlas.findRegion("RestartBtnActive");
		rgRestartBtnActive.flip(false, true);
		rgRestartBtn = atlas.findRegion("RestartBtn");
		rgRestartBtn.flip(false, true);
		rgUndoBtn = atlas.findRegion("UndoBtn");
		rgUndoBtn.flip(false, true);
		rgGameOverLbl = atlas.findRegion("GameOverLbl");
		rgGameOverLbl.flip(false, true);
		rgNewGameLbl = atlas.findRegion("NewGameLbl");
		rgNewGameLbl.flip(false, true);
		rgYouWinLbl = atlas.findRegion("YouWinLbl");
		rgYouWinLbl.flip(false, true);
		rgYesLbl = atlas.findRegion("YesLbl");
		rgYesLbl.flip(false, true);
		rgNoLbl = atlas.findRegion("NoLbl");
		rgNoLbl.flip(false, true);

		spFieldCover = new Sprite(rgFieldCover);
		spGameOver = new Sprite(rgGameOverLbl);
		spNewGame = new Sprite(rgNewGameLbl);
		spYouWin = new Sprite(rgYouWinLbl);
		spYes = new Sprite(rgYesLbl);
		spNo = new Sprite(rgNoLbl);

		chipsPic = new Array<Pic>(16);
		Pic pic;

		int n = 2;
		for (int i = 0; i < 16; i++) {
			pic = new Pic();
			pic.value = n;
			pic.rgChip = atlas.findRegion(Integer.toString(n));
			pic.rgChip.flip(false, true);
			chipsPic.add(pic);
			n *= 2;
		}

		font = new BitmapFont(Gdx.files.internal("WhiteFont.fnt"), true);
	}
}
