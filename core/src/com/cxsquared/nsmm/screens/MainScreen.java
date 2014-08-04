package com.cxsquared.nsmm.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List.ListStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane.ScrollPaneStyle;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox.SelectBoxStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.cxsquared.nsmm.tools.DataParser;

public class MainScreen implements Screen {

	private Stage stage;
	private Table table;
	private DataParser dp;
	private SelectBox<String> sb, tables;
	private Label leftSide, rightSide;

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		stage.act(delta);
		stage.draw();

	}

	@Override
	public void resize(int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void show() {
		dp = new DataParser(Gdx.files.internal("neogame.xml"));

		stage = new Stage(new ScreenViewport());
		Gdx.input.setInputProcessor(stage);

		table = new Table();
		table.setFillParent(true);

		Image bg = new Image(new Texture(Gdx.files.internal("bg.png")));

		ListStyle ls = new ListStyle(new BitmapFont(), Color.BLUE, Color.WHITE, bg.getDrawable());

		SelectBoxStyle sbs = new SelectBoxStyle(new BitmapFont(), Color.WHITE, bg.getDrawable(), new ScrollPaneStyle(), ls);

		sb = new SelectBox<String>(sbs);
		sb.setItems(dp.neogameTableTypeNames);
		sb.addListener(CategorySelectBoxChangeListener);
		sb.setHeight(Gdx.graphics.getHeight());

		tables = new SelectBox<String>(sbs);
		Array<String> temp = dp.neogameTableData.get(sb.getSelected()).keys().toArray();
		temp.sort();
		tables.setItems(temp);
		tables.setHeight(Gdx.graphics.getHeight());
		tables.addListener(TableSelectBoxChangeListener);

		Table textArea = new Table();

		LabelStyle labelStyle = new LabelStyle(new BitmapFont(), Color.WHITE);

		leftSide = new Label("", labelStyle);
		leftSide.setAlignment(Align.left);

		for (String name : dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).keys()) {
			leftSide.setText(leftSide.getText() + "\n" + name + " = ");
		}

		rightSide = new Label("", labelStyle);
		rightSide.setAlignment(Align.left);

		for (String name : dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).keys()) {
			rightSide.setText(rightSide.getText() + "\n" + dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).get(name));
		}

		textArea.add(leftSide).expand().fill().pad(5);
		textArea.add(rightSide).expand().fill().pad(5);

		table.add(sb).pad(5).expandY().left();
		table.add(tables).pad(5).expandY().left();
		table.add(textArea).pad(5).expand().fill();
		stage.addActor(table);

	}

	@Override
	public void hide() {

	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void dispose() {
		stage.dispose();
	}

	public ChangeListener CategorySelectBoxChangeListener = new ChangeListener() {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			Array<String> temp = dp.neogameTableData.get(sb.getSelected()).keys().toArray();
			temp.sort();
			tables.setItems(temp);
		}

	};

	public ChangeListener TableSelectBoxChangeListener = new ChangeListener() {
		@Override
		public void changed(ChangeEvent event, Actor actor) {
			leftSide.setText("");
			for (String name : dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).keys()) {
				leftSide.setText(leftSide.getText() + "\n" + name + " = ");
			}
			rightSide.setText("");
			for (String name : dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).keys()) {
				rightSide.setText(rightSide.getText() + "\n" + dp.neogameTableData.get(sb.getSelected()).get(tables.getSelected()).get(name));
			}
		}

	};
}
