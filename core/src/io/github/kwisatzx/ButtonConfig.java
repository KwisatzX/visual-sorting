package io.github.kwisatzx;

import com.badlogic.gdx.graphics.Color;

//TODO: inner class
public class ButtonConfig {
    String id;
    String text;
    float width;
    float height;
    float x;
    float y;
    Color borderColor;
    Color bgColor;
    Color textColor;

    public ButtonConfig() {}

    public ButtonConfig(String id, String text, float width, float height, float x, float y, Color borderColor, Color bgColor, Color textColor) {
        this.id = id;
        this.text = text;
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.borderColor = borderColor;
        this.bgColor = bgColor;
        this.textColor = textColor;
    }

    @Override
    public String toString() {
        return id + " " + text + " " + width + " " + height + " " + x + " " + y + " " + borderColor + " " + bgColor + " " + textColor;
    }
}
