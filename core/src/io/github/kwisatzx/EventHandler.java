package io.github.kwisatzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

import java.util.ArrayList;

public class EventHandler extends InputAdapter {
    private final Main app;
    private Values values;
    private final ArrayList<Button> buttons;

    public EventHandler(Main app, Values values, ArrayList<Button> buttons) {
        this.app = app;
        this.values = values;
        this.buttons = buttons;
        Gdx.input.setInputProcessor(this);
    }

    //TODO:change to map?
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.F1) {
            app.toggleConcurrentSound();
            return true;
        }
        if (keycode == Input.Keys.F2) {
            buttons.get(0).saveConfig();
            return true;
        }
        if (keycode == Input.Keys.F3) {
            return true;
        }
        if (keycode == Input.Keys.UP) {
            app.changeTableSize(5);
            return true;
        }
        if (keycode == Input.Keys.DOWN) {
            app.changeTableSize(-5);
            return true;
        }
        if (keycode == Input.Keys.SPACE) {
            values = app.genNewValues();
            return true;
        }
        if (keycode == Input.Keys.RIGHT) {
            app.changeSelectedButton(+1);
            return true;
        }
        if (keycode == Input.Keys.LEFT) {
            app.changeSelectedButton(-1);
            return true;
        }
        if (keycode == Input.Keys.ENTER) {
            app.execSelectedButton();
            return true;
        }
        return false;
    }

    //TODO: stream anyMatch? + ternary operator?
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        for (Button b : buttons) {
            if (b.wasClicked()) {
                return true;
            }
        }
        return false;
    }

    public void setNewValues(Values values) {
        this.values = values;
    }
}
