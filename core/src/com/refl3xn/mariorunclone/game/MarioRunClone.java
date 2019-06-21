package com.refl3xn.mariorunclone.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Random;

public class MarioRunClone extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture[] man;
	Rectangle manRectangle;
	int manState = 0, pause = 0;
	float gravity = 0.4f, velocity = 0;
	int manY = 0;

	int score = 0;
	BitmapFont scorefont;

	Random random = new Random();
	ArrayList<Integer> coinsX = new ArrayList<Integer>();
	ArrayList<Integer> coinsY = new ArrayList<Integer>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<Rectangle>();
	Texture coin;
	int coinCount = 50;
	ArrayList<Integer> bombsX = new ArrayList<Integer>();
	ArrayList<Integer> bombsY = new ArrayList<Integer>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<Rectangle>();
	Texture bomb;
	int bombCount = 0;

	int gameState = 0;

	public void makeCoin(){
		float rand = random.nextFloat();
		if (rand > 0.9){
			rand = 0.9f;
		}
		float height = rand * Gdx.graphics.getHeight();
		coinsY.add((int) height);
		coinsX.add(Gdx.graphics.getWidth());
	}

	public void makeBomb(){
		float rand = random.nextFloat();
		if (rand > 0.9){
			rand = 0.9f;
		}
		float height = rand * Gdx.graphics.getHeight();
		bombsY.add((int) height);
		bombsX.add(Gdx.graphics.getWidth());
	}

	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		man = new Texture[5];
		man[0] = new Texture("frame1.png");
		man[1] = new Texture("frame2.png");
		man[2] = new Texture("frame3.png");
		man[3] = new Texture("frame4.png");
		man[4] = new Texture("dizzy.png");
		manRectangle = new Rectangle();
		manY = Gdx.graphics.getHeight() / 2 - man[0].getHeight();
		coin = new Texture("coin.png");
		bomb = new Texture("bomb.png");
		scorefont = new BitmapFont();
		scorefont.setColor(Color.WHITE);
		scorefont.getData().setScale(10);

		gameState = 0;
	}

	@Override
	public void render () {
		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameState == 0){
			//Going to Start
			if (Gdx.input.justTouched()){
				gameState = 1;
			}

		} else if (gameState == 1) {
			//Game is LIVE

			//Coins
			if (coinCount < 120){
				coinCount++;
			} else {
				coinCount = 0;
				makeCoin();
			}
			coinRectangles.clear();
			for (int i=0;i<coinsY.size();i++){
				batch.draw(coin, coinsX.get(i), coinsY.get(i));
				coinsX.set(i, coinsX.get(i) - 4);
				coinRectangles.add(new Rectangle(coinsX.get(i), coinsY.get(i), coin.getWidth(), coin.getHeight()));
			}

			//Bombs
			if (bombCount < 281){
				bombCount++;
			} else {
				bombCount = 0;
				makeBomb();
			}
			bombRectangles.clear();
			for (int i=0;i<bombsY.size();i++){
				batch.draw(bomb, bombsX.get(i), bombsY.get(i));
				bombsX.set(i, bombsX.get(i) - 8);
				bombRectangles.add(new Rectangle(bombsX.get(i), bombsY.get(i), bomb.getWidth(), bomb.getHeight()));
			}

			//Jump
			if (Gdx.input.justTouched()){
				if (manY <= Gdx.graphics.getHeight() - man[0].getHeight()){
					velocity = -20;
				}
			}

			velocity = velocity + gravity;
			manY -= velocity;
			if (manY <= 0){
				manY = 1;
			}
			if (manY >= Gdx.graphics.getHeight() - man[0].getHeight()){
				velocity = 0;
			}

			//Run
			if (pause < 10){
				pause++;
			} else {
				pause = 0;
				if (manState < 3){
					manState++;
				} else {
					manState = 0;
				}
			}


		} else if (gameState == 2){
			//Game Over
			manState = 4;
			if (Gdx.input.justTouched()){
				gameState = 1;
				score = 0;
				velocity = 0;
				manY = Gdx.graphics.getHeight() / 2 - man[0].getHeight();
				coinsX.clear();
				coinsY.clear();
				coinRectangles.clear();
				coinCount = 50;
				bombsX.clear();
				bombsY.clear();
				bombRectangles.clear();
				bombCount = 0;

			}
		}

		manRectangle.set(man[manState].getWidth() / 2, manY, man[0].getWidth(), man[0].getHeight());
		batch.draw(man[manState], man[manState].getWidth() / 2, manY);

		//Coin Collision and Score update
		for (int i=0;i<coinRectangles.size();i++){
			if (Intersector.overlaps(manRectangle, coinRectangles.get(i))){
				score++;
				coinRectangles.remove(i);
				coinsX.remove(i);
				coinsY.remove(i);
				break;
			}
		}
		scorefont.draw(batch, String.valueOf(score), 60, Gdx.graphics.getHeight() - 80);

		//Bomb Collision
		for (int i=0;i<bombRectangles.size();i++){
			if (Intersector.overlaps(manRectangle, bombRectangles.get(i))){
				gameState = 2;
			}
		}

		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
