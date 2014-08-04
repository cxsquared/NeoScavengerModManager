package com.cxsquared.nsmm;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.cxsquared.nsmm.screens.DataWindow;

public class NeoScavengerModManager extends Game {
	public static final int GAME_WIDTH = 800;
	public static final int GAME_HEIGHT = 600;
	public static StretchViewport stretchViewport;
	public static OrthographicCamera camera;

	@Override
	public void create() {
		DataWindow frame = new DataWindow();
		frame.setVisible(true);
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void pause() {
		super.pause();
	}

	@Override
	public void resume() {
		super.resume();
	}
}
