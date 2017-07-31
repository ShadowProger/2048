package com.manybits.screens;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.manybits.game2048.Game2048;
import com.manybits.ui.SimpleButton;

public class GameScreen implements Screen, GestureListener, InputProcessor{
    enum CellType {ctNone, ctNew, ctChange}


    class Cell {
        int value;
        CellType cType;
        int index;
        Vector2 newPos;
    }


    class Turn {
        Cell[][] field;
        boolean isGameOver;
        int score;
    }


    class Change {
        boolean isChange;
        int score;
    }

    enum GameMode {gmGame, gmGameOver, gmAccept, gmWin}
    enum Napr {nLeft, nRight, nUp, nDown, nNone}


    class Chip {
        int index;
        float speedX, speedY;
        float x, y;
        float newX, newY;
        float scale;
        boolean incScale;
        int imageIndex;
        CellType cType;
    }


    class MyGame {
        Turn currentTurn;
        Turn lastTurn;
        int turnCount = 0;
        boolean canUndo;
        boolean canTurn;
        boolean is2048;
        boolean isWin;
        Napr napr;

        MyGame() {
            currentTurn = new Turn();
            currentTurn.field = new Cell[4][4];
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    currentTurn.field[i][j] = new Cell();
                    currentTurn.field[i][j].value = 0;
                    currentTurn.field[i][j].index = 0;
                    currentTurn.field[i][j].cType = CellType.ctNone;
                    currentTurn.field[i][j].newPos = new Vector2(j, i);
                }
            currentTurn.isGameOver = false;
            currentTurn.score = 0;

            lastTurn = new Turn();
            lastTurn.field = new Cell[4][4];
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    lastTurn.field[i][j] = new Cell();
                    lastTurn.field[i][j].value = 0;
                    lastTurn.field[i][j].index = 0;
                    lastTurn.field[i][j].cType = CellType.ctNone;
                    lastTurn.field[i][j].newPos = new Vector2(j, i);
                }
            lastTurn.isGameOver = false;
            lastTurn.score = 0;

            canUndo = false;
            canTurn = true;
            isWin = false;
            is2048 = false;
            napr = Napr.nNone;
            turnCount = 0;
        }



        void newGame() {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    currentTurn.field[i][j].value = 0;
                    currentTurn.field[i][j].index = 0;
                    currentTurn.field[i][j].cType = CellType.ctNone;
                    currentTurn.field[i][j].newPos.set(i, j);
                }
            currentTurn.isGameOver = false;
            currentTurn.score = 0;

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    lastTurn.field[i][j].value = 0;
                    lastTurn.field[i][j].index = 0;
                    lastTurn.field[i][j].cType = CellType.ctNone;
                    lastTurn.field[i][j].newPos.set(i, j);
                }
            lastTurn.isGameOver = false;
            lastTurn.score = 0;

            canUndo = false;
            isWin = false;
            is2048 = false;
            napr = Napr.nNone;
            gameMode = GameMode.gmGame;
            turnCount = 0;

            isCreate = true;

            addNewChip();
            addNewChip();

            createNewChips(currentTurn);
        }



        void addNewChip() {
            int countZero = 0;
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    if (myGame.currentTurn.field[i][j].value == 0)
                        countZero++;
            if (countZero == 0)
                return;
            int x, y;
            do {
                x = random.nextInt(4);
                y = random.nextInt(4);
            } while (myGame.currentTurn.field[y][x].value != 0);

            int r = random.nextInt(100);
            if (r < 90) {
                myGame.currentTurn.field[y][x].value = 2;
            } else {
                myGame.currentTurn.field[y][x].value = 4;
            }
            myGame.currentTurn.field[y][x].cType = CellType.ctNew;
        }



        void loadGame() {
            FileHandle file = Gdx.files.local("SavedGame.sg");

            try (DataInputStream dis = new DataInputStream(file.read())) {
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++)
                        currentTurn.field[i][j].value = dis.readInt();
                currentTurn.isGameOver = dis.readBoolean();
                currentTurn.score = dis.readInt();
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++)
                        lastTurn.field[i][j].value = dis.readInt();
                lastTurn.isGameOver = dis.readBoolean();
                lastTurn.score = dis.readInt();
                canUndo = dis.readBoolean();
                isWin = dis.readBoolean();
                int gm = dis.readInt();
                gameMode = GameMode.values()[gm];
                if ((gameMode == GameMode.gmGameOver) || (gameMode == GameMode.gmWin)) {
                    alpha = 1;
                    btnRestart.setTexture(game.rgRestartBtnActive, game.rgRestartBtnActive, game.rgRestartBtnActive);
                }

                Gdx.app.log("INFO", "Load is Successful");

                napr = Napr.nNone;
                isCreate = false;
                createNewChips(currentTurn);
            }
            catch (IOException ex) {
                Gdx.app.log("INFO", "Load is Unsuccessful");
            }
        }



        void saveGame() {
            FileHandle file = Gdx.files.local("SavedGame.sg");

            try (DataOutputStream dos = new DataOutputStream(file.write(false))) {
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++)
                        dos.writeInt(currentTurn.field[i][j].value);
                dos.writeBoolean(currentTurn.isGameOver);
                dos.writeInt(currentTurn.score);
                for (int i = 0; i < 4; i++)
                    for (int j = 0; j < 4; j++)
                        dos.writeInt(lastTurn.field[i][j].value);
                dos.writeBoolean(lastTurn.isGameOver);
                dos.writeInt(lastTurn.score);
                dos.writeBoolean(canUndo);
                dos.writeBoolean(isWin);
                if (gameMode == GameMode.gmAccept) {
                    gameMode = GameMode.gmGame;
                }
                int gm = gameMode.ordinal();
                dos.writeInt(gm);

                Gdx.app.log("INFO", "Save is Successful");
            }
            catch (IOException ex) {
                Gdx.app.log("INFO", "Save is Unsuccessful");
            }
        }



        void saveLastTurn(Turn OldTurn, Turn CurTurn) {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++)
                    OldTurn.field[i][j].value = CurTurn.field[i][j].value;
            OldTurn.score = CurTurn.score;
            canUndo = true;
        }



        void Turn() {
            if ((myGame.napr == Napr.nNone) || (canTurn == false)) {
                return;
            }

            Turn turn = new Turn();
            turn.field = new Cell[4][4];
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    turn.field[i][j] = new Cell();
                    turn.field[i][j].value = 0;
                    turn.field[i][j].index = 0;
                    turn.field[i][j].cType = CellType.ctNone;
                    turn.field[i][j].newPos = new Vector2(j, i);
                }
            saveLastTurn(turn, currentTurn);
            Change change = new Change();
            switch (myGame.napr) {
                case nLeft:
                    change = LeftTurn();
                    break;
                case nRight:
                    change = RightTurn();
                    break;
                case nUp:
                    change = UpTurn();
                    break;
                case nDown:
                    change = DownTurn();
                    break;
            }
            if (change.isChange) {
                addScore = change.score;
                saveLastTurn(lastTurn, turn);
                addNewChip();
                createNewChips(lastTurn);
                isMove = true;
                turnCount++;
                if (turnCount > 4) {
                    game.saveHighscore();
                    saveGame();
                    turnCount = 0;
                }
            }

            myGame.canTurn = false;
        }



        Change LeftTurn() {
            Change change = new Change();
            change.isChange = false;
            change.score = 0;

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    myGame.currentTurn.field[i][j].cType = CellType.ctNone;
                    myGame.lastTurn.field[i][j].index = 3;
                    myGame.lastTurn.field[i][j].newPos.set(i, j);
                }

            int last;
            int index;
            for (int i = 0; i < 4; i++) {
                last = 0;
                index = 3;
                for (int j = 1; j < 4; j++) {
                    if (myGame.currentTurn.field[i][j].value == 0) {
                        continue;
                    }
                    index--;
                    myGame.lastTurn.field[i][j].index = index;
                    if (myGame.currentTurn.field[i][last].value == 0) {
                        myGame.currentTurn.field[i][last].value = myGame.currentTurn.field[i][j].value;
                        myGame.currentTurn.field[i][j].value = 0;
                        myGame.lastTurn.field[i][j].newPos.set(i, last);
                        change.isChange = true;
                    } else {
                        if (myGame.currentTurn.field[i][last].value == myGame.currentTurn.field[i][j].value) {
                            myGame.currentTurn.field[i][last].value += myGame.currentTurn.field[i][j].value;
                            if (myGame.currentTurn.field[i][last].value == 2048) {
                                is2048 = true;
                            }
                            change.score += myGame.currentTurn.field[i][last].value;
                            myGame.currentTurn.field[i][j].value = 0;
                            myGame.currentTurn.field[i][last].cType = CellType.ctChange;
                            myGame.lastTurn.field[i][j].newPos.set(i, last);
                            last++;
                            change.isChange = true;
                        } else {
                            if (last + 1 == j) {
                                last++;
                            } else {
                                myGame.currentTurn.field[i][last+1].value = myGame.currentTurn.field[i][j].value;
                                myGame.currentTurn.field[i][j].value = 0;
                                myGame.lastTurn.field[i][j].newPos.set(i, last + 1);
                                last++;
                                change.isChange = true;
                            }
                        }
                    }
                }
            }
            return change;
        }



        Change RightTurn() {
            Change change = new Change();
            change.isChange = false;
            change.score = 0;

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    myGame.currentTurn.field[i][j].cType = CellType.ctNone;
                    myGame.lastTurn.field[i][j].index = 3;
                    myGame.lastTurn.field[i][j].newPos.set(i, j);
                }

            int last;
            int index;
            for (int i = 0; i < 4; i++) {
                last = 3;
                index = 3;
                for (int j = 2; j >= 0; j--) {
                    if (myGame.currentTurn.field[i][j].value == 0) {
                        continue;
                    }
                    index--;
                    myGame.lastTurn.field[i][j].index = index;
                    if (myGame.currentTurn.field[i][last].value == 0) {
                        myGame.currentTurn.field[i][last].value = myGame.currentTurn.field[i][j].value;
                        myGame.currentTurn.field[i][j].value = 0;
                        myGame.lastTurn.field[i][j].newPos.set(i, last);
                        change.isChange = true;
                    } else {
                        if (myGame.currentTurn.field[i][last].value == myGame.currentTurn.field[i][j].value) {
                            myGame.currentTurn.field[i][last].value += myGame.currentTurn.field[i][j].value;
                            if (myGame.currentTurn.field[i][last].value == 2048) {
                                is2048 = true;
                            }
                            change.score += myGame.currentTurn.field[i][last].value;
                            myGame.currentTurn.field[i][j].value = 0;
                            myGame.currentTurn.field[i][last].cType = CellType.ctChange;
                            myGame.lastTurn.field[i][j].newPos.set(i, last);
                            last--;
                            change.isChange = true;
                        } else {
                            if (last - 1 == j) {
                                last--;
                            } else {
                                myGame.currentTurn.field[i][last-1].value = myGame.currentTurn.field[i][j].value;
                                myGame.currentTurn.field[i][j].value = 0;
                                myGame.lastTurn.field[i][j].newPos.set(i, last - 1);
                                last--;
                                change.isChange = true;
                            }
                        }
                    }
                }
            }
            return change;
        }



        Change UpTurn() {
            Change change = new Change();
            change.isChange = false;
            change.score = 0;

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    myGame.currentTurn.field[i][j].cType = CellType.ctNone;
                    myGame.lastTurn.field[i][j].index = 3;
                    myGame.lastTurn.field[i][j].newPos.set(i, j);
                }

            int last;
            int index;
            for (int j = 0; j < 4; j++) {
                last = 0;
                index = 3;
                for (int i = 1; i < 4; i++) {
                    if (myGame.currentTurn.field[i][j].value == 0) {
                        continue;
                    }
                    index--;
                    myGame.lastTurn.field[i][j].index = index;
                    if (myGame.currentTurn.field[last][j].value == 0) {
                        myGame.currentTurn.field[last][j].value = myGame.currentTurn.field[i][j].value;
                        myGame.currentTurn.field[i][j].value = 0;
                        myGame.lastTurn.field[i][j].newPos.set(last, j);
                        change.isChange = true;
                    } else {
                        if (myGame.currentTurn.field[last][j].value == myGame.currentTurn.field[i][j].value) {
                            myGame.currentTurn.field[last][j].value += myGame.currentTurn.field[i][j].value;
                            if (myGame.currentTurn.field[last][j].value == 2048) {
                                is2048 = true;
                            }
                            change.score += myGame.currentTurn.field[last][j].value;
                            myGame.currentTurn.field[i][j].value = 0;
                            myGame.currentTurn.field[last][j].cType = CellType.ctChange;
                            myGame.lastTurn.field[i][j].newPos.set(last, j);
                            last++;
                            change.isChange = true;
                        } else {
                            if (last + 1 == i) {
                                last++;
                            } else {
                                myGame.currentTurn.field[last+1][j].value = myGame.currentTurn.field[i][j].value;
                                myGame.currentTurn.field[i][j].value = 0;
                                myGame.lastTurn.field[i][j].newPos.set(last + 1, j);
                                last++;
                                change.isChange = true;
                            }
                        }
                    }
                }
            }
            return change;
        }



        Change DownTurn() {
            Change change = new Change();
            change.isChange = false;
            change.score = 0;

            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    myGame.currentTurn.field[i][j].cType = CellType.ctNone;
                    myGame.lastTurn.field[i][j].index = 3;
                    myGame.lastTurn.field[i][j].newPos.set(i, j);
                }

            int last;
            int index;
            for (int j = 0; j < 4; j++) {
                last = 3;
                index = 3;
                for (int i = 2; i >= 0; i--) {
                    if (myGame.currentTurn.field[i][j].value == 0) {
                        continue;
                    }
                    index--;
                    myGame.lastTurn.field[i][j].index = index;
                    if (myGame.currentTurn.field[last][j].value == 0) {
                        myGame.currentTurn.field[last][j].value = myGame.currentTurn.field[i][j].value;
                        myGame.currentTurn.field[i][j].value = 0;
                        myGame.lastTurn.field[i][j].newPos.set(last, j);
                        change.isChange = true;
                    } else {
                        if (myGame.currentTurn.field[last][j].value == myGame.currentTurn.field[i][j].value) {
                            myGame.currentTurn.field[last][j].value += myGame.currentTurn.field[i][j].value;
                            if (myGame.currentTurn.field[last][j].value == 2048) {
                                is2048 = true;
                            }
                            change.score += myGame.currentTurn.field[last][j].value;
                            myGame.currentTurn.field[i][j].value = 0;
                            myGame.currentTurn.field[last][j].cType = CellType.ctChange;
                            myGame.lastTurn.field[i][j].newPos.set(last, j);
                            last--;
                            change.isChange = true;
                        } else {
                            if (last - 1 == i) {
                                last--;
                            } else {
                                myGame.currentTurn.field[last-1][j].value = myGame.currentTurn.field[i][j].value;
                                myGame.currentTurn.field[i][j].value = 0;
                                myGame.lastTurn.field[i][j].newPos.set(last - 1, j);
                                last--;
                                change.isChange = true;
                            }
                        }
                    }
                }
            }
            return change;
        }



        void check() {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++) {
                    if (myGame.currentTurn.field[i][j].value == 0) {
                        return;
                    }
                }
            for (int i = 0; i < 3; i++)
                for (int j = 0; j < 4; j++) {
                    if (myGame.currentTurn.field[i][j].value == myGame.currentTurn.field[i+1][j].value) {
                        return;
                    }
                }
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 3; j++) {
                    if (myGame.currentTurn.field[i][j].value == myGame.currentTurn.field[i][j+1].value) {
                        return;
                    }
                }
            myGame.currentTurn.isGameOver = true;
        }



        void undo() {
            for (int i = 0; i < 4; i++)
                for (int j = 0; j < 4; j++){
                    currentTurn.field[i][j].value = lastTurn.field[i][j].value;
                    currentTurn.field[i][j].cType = CellType.ctNone;
                }
            currentTurn.score = lastTurn.score;
            currentTurn.isGameOver = lastTurn.isGameOver;
            if (currentTurn.isGameOver == false) {
                gameMode = GameMode.gmGame;
            }
            createNewChips(currentTurn);
            myGame.canUndo = false;
            turnCount--;
        }
    }



    static Comparator<Chip> comp = new Comparator<Chip>() {
        public int compare(Chip c1, Chip c2) {
            return Integer.compare(c1.index, c2.index);
        }
    };



    final Game2048 game;
    private SpriteBatch batch;
    private GlyphLayout layout;
    private InputMultiplexer IM;
    private SimpleButton btnRestart;
    private SimpleButton btnUndo;

    Random random;

    final float MOVE_TIME = 0.115f;
    final int CHIP_SIZE = 142;
    final float DELTA_SCALE = 0.08f;
    final float CHANGE_SCALE = 1.2f;
    GameMode gameMode;
    boolean isMove;
    boolean isCreate;
    boolean isChange;
    MyGame myGame;
    int addScore;

    Rectangle rField;
    Rectangle rYes;
    Rectangle rNo;
    Vector2 touchPoint;
    boolean isTouched;
    float lblScoreX;
    float lblHighScoreX;

    ArrayList<Chip> chips;



    public GameScreen(Game2048 game) {
        Gdx.app.log("INFO", "GameScreen: Create");
        this.game = game;
        batch = game.batch;
        layout = new GlyphLayout();

        IM = new InputMultiplexer();
        GestureDetector gd = new GestureDetector(this);
        IM.addProcessor(gd);
        IM.addProcessor(this);

        btnUndo = new SimpleButton(123, 822 + game.screenDisp, game.rgUndoBtn.getRegionWidth(), game.rgUndoBtn.getRegionHeight(), game.rgUndoBtn, game.rgUndoBtn, game.rgUndoBtn);
        btnRestart = new SimpleButton(435, 822 + game.screenDisp, game.rgRestartBtn.getRegionWidth(), game.rgRestartBtn.getRegionHeight(), game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);

        touchPoint = new Vector2(0, 0);
        rField = new Rectangle(30, 154 + game.screenDisp, game.rgField.getRegionWidth(), game.rgField.getRegionHeight());
        rYes = new Rectangle(124, 578 + game.screenDisp, game.rgYesLbl.getRegionWidth(), game.rgYesLbl.getRegionHeight());
        rNo = new Rectangle(394, 578 + game.screenDisp, game.rgNoLbl.getRegionWidth(), game.rgNoLbl.getRegionHeight());
        isTouched = false;
        lblScoreX = 30 + game.rgScore.getRegionWidth() / 2;
        lblHighScoreX = 357 + game.rgHighscore.getRegionWidth() / 2;

        game.spFieldCover.setPosition(30, 154 + game.screenDisp);
        game.spGameOver.setPosition(30 + game.rgField.getRegionWidth() / 2 - game.rgGameOverLbl.getRegionWidth() / 2,
                154 + game.screenDisp + game.rgField.getRegionHeight() / 2 - game.rgGameOverLbl.getRegionHeight() / 2);
        game.spNewGame.setPosition(30 + game.rgField.getRegionWidth() / 2 - game.rgNewGameLbl.getRegionWidth() / 2,
                154 + game.screenDisp + game.rgField.getRegionHeight() / 2 - game.rgNewGameLbl.getRegionHeight() / 2);
        game.spYouWin.setPosition(30 + game.rgField.getRegionWidth() / 2 - game.rgYouWinLbl.getRegionWidth() / 2,
                154 + game.screenDisp + game.rgField.getRegionHeight() / 2 - game.rgYouWinLbl.getRegionHeight() / 2);
        game.spYes.setPosition(rYes.x, rYes.y);
        game.spNo.setPosition(rNo.x, rNo.y);

        random = new Random();

        chips = new ArrayList<Chip>();

        myGame = new MyGame();
        if (game.continueGame) {
            myGame.loadGame();
        } else {
            myGame.newGame();
        }
    }



    @Override
    public void show() {
        Gdx.app.log("INFO", "GameScreen: Show");
        Gdx.input.setInputProcessor(IM);
        Gdx.input.setCatchBackKey(true);
    }


    float alpha = 0;

    private void update(float delta) {
        if (gameMode == GameMode.gmGame) {
            myGame.Turn();
            if (isMove) {
                int count = 0;
                for (Chip chip : chips) {
                    if (chip.x != 0) {
                        chip.x += chip.speedX * delta;
                    }
                    if (chip.y != 0) {
                        chip.y += chip.speedY * delta;
                    }
                    if (chip.speedX < 0) {
                        if (chip.x < chip.newX) {
                            chip.x = chip.newX;
                        }
                    } else {
                        if (chip.x > chip.newX) {
                            chip.x = chip.newX;
                        }
                    }
                    if (chip.speedY < 0) {
                        if (chip.y < chip.newY) {
                            chip.y = chip.newY;
                        }
                    } else {
                        if (chip.y > chip.newY) {
                            chip.y = chip.newY;
                        }
                    }
                    if ((chip.x == chip.newX) && (chip.y == chip.newY)) {
                        count++;
                    }
                }
                if (count == chips.size()) {
                    isMove = false;
                    isChange = true;
                    isCreate = true;
                    myGame.currentTurn.score += addScore;
                    if (myGame.currentTurn.score > game.highScore) {
                        game.highScore = myGame.currentTurn.score;
                    }
                    createNewChips(myGame.currentTurn);
                    myGame.check();
                    if (myGame.currentTurn.isGameOver) {
                        gameMode = GameMode.gmGameOver;
                        btnRestart.setTexture(game.rgRestartBtnActive, game.rgRestartBtnActive, game.rgRestartBtnActive);
                        alpha = 0;
                    }
                    if (myGame.is2048) {
                        if (myGame.isWin == false) {
                            myGame.isWin = true;
                            gameMode = GameMode.gmWin;
                            btnRestart.setTexture(game.rgRestartBtnActive, game.rgRestartBtnActive, game.rgRestartBtnActive);
                            alpha = 0;
                        }
                    }
                }
            }
        }

        if (isChange) {
            isChange = false;
            for (Chip chip : chips) {
                if (chip.cType == CellType.ctChange) {
                    isChange = true;
                    if (chip.incScale) {
                        if (chip.scale < CHANGE_SCALE) {
                            chip.scale += DELTA_SCALE;
                        } else {
                            if (chip.scale > CHANGE_SCALE) {
                                chip.scale = CHANGE_SCALE;
                                chip.incScale = false;
                            }
                        }
                    } else {
                        if (chip.scale > 1) {
                            chip.scale -= DELTA_SCALE;
                        } else {
                            if (chip.scale < 1) {
                                chip.scale = 1;
                                chip.cType = CellType.ctNone;
                            }
                        }
                    }
                }
            }
        }

        if (isCreate) {
            isCreate = false;
            for (Chip chip : chips) {
                if (chip.cType == CellType.ctNew) {
                    isCreate = true;
                    if (chip.scale < 1) {
                        chip.scale += DELTA_SCALE;
                    } else {
                        if (chip.scale > 1) {
                            chip.scale = 1;
                            chip.cType = CellType.ctNone;
                        }
                    }
                }

            }
        }

        if ((gameMode == GameMode.gmGameOver) || (gameMode == GameMode.gmWin)) {
            if (alpha < 1) {
                alpha += 0.02;
            }
            if (alpha > 1) {
                alpha = 1;
            }
        }
    }



    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.disableBlending();
        batch.draw(game.rgBackGround, 0, 0);
        batch.enableBlending();
        batch.draw(game.rgScore, 30, 30 + game.screenDisp);
        batch.draw(game.rgHighscore, 357, 30 + game.screenDisp);
        batch.draw(game.rgField, 30, 154 + game.screenDisp);
        btnUndo.draw(batch);
        btnRestart.draw(batch);

        for (Chip chip : chips) {
            batch.draw(game.chipsPic.get(chip.imageIndex).rgChip, chip.x, chip.y, CHIP_SIZE / 2, CHIP_SIZE / 2, CHIP_SIZE, CHIP_SIZE, chip.scale, chip.scale, 0f);
        }

        game.font.setColor(1, 1, 1, 1);
        game.font.getData().setScale(1);
        layout.setText(game.font, Integer.toString(myGame.currentTurn.score));
        game.font.draw(batch, Integer.toString(myGame.currentTurn.score), lblScoreX - layout.width / 2, 65 + game.screenDisp);
        layout.setText(game.font, Integer.toString(game.highScore));
        game.font.draw(batch, Integer.toString(game.highScore), lblHighScoreX - layout.width / 2, 65 + game.screenDisp);

        if (gameMode == GameMode.gmGameOver) {
            game.spFieldCover.setAlpha(alpha);
            game.spFieldCover.draw(batch);
            game.spGameOver.setAlpha(alpha);
            game.spGameOver.draw(batch);
        }

        if (gameMode == GameMode.gmWin) {
            game.spFieldCover.setAlpha(alpha);
            game.spFieldCover.draw(batch);
            game.spYouWin.setAlpha(alpha);
            game.spYouWin.draw(batch);
        }

        if (gameMode == GameMode.gmAccept) {
            game.spFieldCover.setAlpha(1);
            game.spFieldCover.draw(batch);
            game.spNewGame.setAlpha(1);
            game.spNewGame.draw(batch);
            game.spYes.setAlpha(1);
            game.spYes.draw(batch);
            game.spNo.setAlpha(1);
            game.spNo.draw(batch);
        }
        batch.end();
    }



    private void createNewChips(Turn turn) {
        chips.clear();
        for (int i = 0; i < 4; i++)
            for (int j = 0; j < 4; j++) {
                if (turn.field[i][j].value != 0) {
                    Chip newChip = new Chip();
                    newChip.index = turn.field[i][j].index;
                    newChip.x = 45 + 142 * j + 14 * j;
                    newChip.y = 169 + 142 * i + 14 * i + game.screenDisp;
                    float ii = turn.field[i][j].newPos.x;
                    float jj = turn.field[i][j].newPos.y;
                    newChip.newX = 45 + 142 * jj + 14 * jj;
                    newChip.newY = 169 + 142 * ii + 14 * ii + game.screenDisp;
                    newChip.speedX = (newChip.newX - newChip.x) / MOVE_TIME;
                    newChip.speedY = (newChip.newY - newChip.y) / MOVE_TIME;
                    newChip.cType = turn.field[i][j].cType;
                    if (newChip.cType == CellType.ctChange) {
                        newChip.incScale = true;
                    }
                    if (newChip.cType == CellType.ctNew) {
                        newChip.scale = 0.3f;
                    } else {
                        newChip.scale = 1f;
                    }
                    int k;
                    for (k = 0; k < 16; k++) {
                        if (turn.field[i][j].value == game.chipsPic.get(k).value) {
                            break;
                        }
                    }
                    newChip.imageIndex = k;
                    chips.add(newChip);
                }
            }
        Collections.sort(chips, comp);
    }


    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {
        Gdx.app.log("INFO", "GameScreen: Pause");
        game.continueGame = true;
        game.saveHighscore();
        // save Game
        myGame.saveGame();
    }

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        Gdx.app.log("INFO", "GameScreen: Dispose");
    }



    // GestureListener ==================================================================
    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean tap(float x, float y, int count, int button) {
        if (gameMode == GameMode.gmWin) {
            if (rField.contains(x * game.scaleCoef, y * game.scaleCoef)) {
                gameMode = GameMode.gmGame;
                btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
            }
        }
        return false;
    }

    @Override
    public boolean longPress(float x, float y) {
        return false;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (gameMode == GameMode.gmGame) {
            if (isTouched) {
                if (myGame.napr == Napr.nNone) {
                    touchPoint.x += deltaX;
                    touchPoint.y += deltaY;
                    if (touchPoint.y < -40) {
                        myGame.napr = Napr.nUp;
                        myGame.canTurn = true;
                    } else
                    if (touchPoint.y > 40) {
                        myGame.napr = Napr.nDown;
                        myGame.canTurn = true;
                    }
                    if (touchPoint.x < -40) {
                        myGame.napr = Napr.nLeft;
                        myGame.canTurn = true;
                    } else
                    if (touchPoint.x > 40) {
                        myGame.napr = Napr.nRight;
                        myGame.canTurn = true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2,
                         Vector2 pointer1, Vector2 pointer2) {
        return false;
    }
//==================================================================================



    // InputProcessor ==================================================================
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.BACK) {
            game.continueGame = true;
            game.saveHighscore();
            myGame.saveGame();
            game.setScreen(new MenuScreen(game));
            dispose();
        }
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
        btnRestart.isTouchDown(screenX * game.scaleCoef, screenY * game.scaleCoef);
        btnUndo.isTouchDown(screenX * game.scaleCoef, screenY * game.scaleCoef);
        if (gameMode == GameMode.gmGame) {
            if (rField.contains(screenX * game.scaleCoef, screenY * game.scaleCoef)) {
                touchPoint.set(0, 0);
                isTouched = true;
                myGame.napr = Napr.nNone;
            }
        }
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        if (btnRestart.isTouchUp(screenX * game.scaleCoef, screenY * game.scaleCoef) == true) {
            if (gameMode == GameMode.gmGame) {
                gameMode = GameMode.gmAccept;
            }
            if (gameMode == GameMode.gmGameOver) {
                myGame.newGame();
                btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
            }
            if (gameMode == GameMode.gmWin) {
                myGame.newGame();
                btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
            }
        }

        if ((gameMode == GameMode.gmGame) || (gameMode == GameMode.gmGameOver)) {
            if (btnUndo.isTouchUp(screenX * game.scaleCoef, screenY * game.scaleCoef) == true) {
                if (myGame.canUndo) {
                    myGame.undo();
                    btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
                }
            }
        }

        if (gameMode == GameMode.gmAccept) {
            if (rNo.contains(screenX * game.scaleCoef, screenY * game.scaleCoef)) {
                gameMode = GameMode.gmGame;
                btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
            }
            if (rYes.contains(screenX * game.scaleCoef, screenY * game.scaleCoef)) {
                myGame.newGame();
                btnRestart.setTexture(game.rgRestartBtn, game.rgRestartBtn, game.rgRestartBtn);
            }
        }
        isTouched = false;
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
//==================================================================================
}
