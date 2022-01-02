package io.github.kwisatzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import org.jetbrains.annotations.NotNull;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static io.github.kwisatzx.Constants.HEIGHT;

public class Button {
    private final String id;
    private String text;
    private final BitmapFont font;
    private float width;
    private float height;
    private float x;
    private float y;
    private Color borderColor;
    private Color bgColor;
    private Color textColor;
    private long currentTime; //used for click animation
    private final Runnable exec;
    private final Sound click;

    public Button(String id, ButtonConfig cfg, Runnable exec) {
        this(id, cfg.x, cfg.y, cfg.text, cfg.width, cfg.height, cfg.borderColor, cfg.bgColor, cfg.textColor, exec);
    }

    public Button(String id, float x, float y, String text, float width, float height, Runnable exec) {
        this(id, x, y, text, width, height,
                new Color(0.2f, 0.2f, 0.5f, 1f), new Color(0.8f, 0.8f, 1f, 1f), new Color(Color.BLACK), exec);
    }

    public Button(String id, float x, float y, String text, float width, float height, Color borderColor, Color bgColor, Color textColor, Runnable exec) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.text = text;
        this.width = width;
        this.height = height;
        this.borderColor = borderColor;
        this.bgColor = bgColor;
        this.textColor = textColor;
        this.exec = exec;
        font = new BitmapFont();
        font.setColor(this.textColor);
        click = Gdx.audio.newSound(Gdx.files.internal("core/assets/click.wav"));
        currentTime = System.nanoTime();
        saveConfig();
    }

    public void run() {
        exec.run();
    }

    public void saveConfig() {
        try (FileWriter writer = new FileWriter("core/assets/config/" + id + ".json")) {
            Json json = new Json();
            ButtonConfig cfg = new ButtonConfig(id, text, width, height, x, y, borderColor, bgColor, textColor);
            writer.write(json.prettyPrint(cfg));
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void loadConfig(ButtonConfig cfg) {
        this.text = cfg.text;
        this.width = cfg.width;
        this.height = cfg.height;
        this.x = cfg.x;
        this.y = cfg.y;
        this.borderColor = cfg.borderColor;
        this.bgColor = cfg.bgColor;
        this.textColor = cfg.textColor;
        font.setColor(this.textColor);
    }

    public static ButtonConfig loadConfigFile(String configName) {
        try (FileReader reader = new FileReader("core/assets/config/" + configName + ".json")) {
            JsonValue v = new JsonReader().parse(reader);
            Json json = new Json();
            return json.fromJson(ButtonConfig.class, v.toString());
        } catch (IOException e) { e.printStackTrace(); }
        System.err.println("Config file not found: " + configName + "!");
        return new ButtonConfig();
    }

    public void drawShapes(@NotNull ShapeRenderer sr) {
        //change color after clicked
        if (currentTime + TimeUnit.MILLISECONDS.toNanos(100) > System.nanoTime()) sr.setColor(borderColor.r+0.3f, borderColor.g+0.3f, borderColor.b+0.3f,1f);
        else sr.setColor(borderColor);
        sr.rect(x - 4f, y, width+3f, height+3.5f);
        if (currentTime + TimeUnit.MILLISECONDS.toNanos(100) > System.nanoTime()) sr.setColor(bgColor.r+0.3f, bgColor.g+0.3f, bgColor.b+0.3f,1f);
        else sr.setColor(bgColor);
        sr.rect(x, y, width, height);
    }

    public void drawText(@NotNull SpriteBatch batch) {
        float textX = width / 2;
        float textY = height / 2;
        textY += 6f;
        textX -= (text.length() * 6.5f) / 2;

        font.draw(batch, text, x + textX, y + textY);
    }

    public void changeText(@NotNull String text) { this.text = text; }

    public boolean wasClicked() {
        int x = Gdx.input.getX();
        int y = (int) (HEIGHT-Gdx.input.getY());
        if ((x >= this.x && x <= this.x + width) && (y >= this.y && y <= this.y + height)) {
            currentTime = System.nanoTime();
            click.play(0.7f,1f,1f);
            run();
            return true;
        }
        return false;
    }

    public float[] selectionBox() { return new float[] {x-5f, y-1f, width+6f, height+5.5f}; }

    public String getId() {
        return id;
    }

    public void dispose() {
        font.dispose();
        click.dispose();
    }
}
