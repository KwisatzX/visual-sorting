package io.github.kwisatzx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static io.github.kwisatzx.Constants.HEIGHT;
import static io.github.kwisatzx.Constants.WIDTH;

public class Main extends ApplicationAdapter {
    private ShapeRenderer sr;
    private SpriteBatch batch;
    private EventHandler eventHandler;
    private Values values;
    private int tableSize = 10;
    private BitmapFont font;
    private boolean replayingSort = false;
    private boolean greenWait = false;
    private long currentTime;
    private ArrayList<SwapIndex> swapRecord;
    private int swapLength;
    private ArrayList<Button> buttons;
    private int selectedButton = -1;
    private ArrayList<Sound> sounds;
    private boolean concurrentSound = true;
    private int currentSound;
    private long sortTime;

    @Override
    public void create() {
        sr = new ShapeRenderer();
        sr.setAutoShapeType(true);
        batch = new SpriteBatch();
        values = new Values();
        buttons = new ArrayList<>();
        sounds = new ArrayList<>();
        eventHandler = new EventHandler(this, values, buttons);
        font = new BitmapFont();
        font.setColor(1f, 1f, 1f, 1f);
        String[] soundNames = {"shortbeep1", "shortbeep2", "celestashort", "celestalong", "kalimbalowlong",
                "kalimbalowshort", "pianolow", "synthbeeplow"};
        for (String name : soundNames)
            sounds.add(Gdx.audio.newSound(Gdx.files.internal("core/assets/" + name + ".wav")));

        buttons.addAll(List.of(
                new Button("newtable", Button.loadConfigFile("newtable"), () -> eventHandler.setNewValues(genNewValues())),
                new Button("reload", Button.loadConfigFile("reload"),
                        () -> buttons.forEach(b -> b.loadConfig(Button.loadConfigFile(b.getId())))),
                new Button("newtableadd", Button.loadConfigFile("newtableadd"), () -> changeTableSize(5)),
                new Button("newtablesub", Button.loadConfigFile("newtablesub"), () -> changeTableSize(-5)),
                new Button("bubble", Button.loadConfigFile("bubble"), () -> values.bubbleSort()),
                new Button("insertion", Button.loadConfigFile("insertion"), () -> values.insertionSort()),
                new Button("selection", Button.loadConfigFile("selection"), () -> values.selectionSort())));
    }

    //TODO: add possibility of sort rendering based on time, not fps
    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //shapes
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(0.9f, 0.1f, 0.1f, 1f);
        //performing and animating swap with green pillars
        if (replayingSort && !swapRecord.isEmpty()) {
            SwapIndex si = swapRecord.get(0);
            if (!greenWait) {
                values.setGreenFlags(si.i, true);
                values.setGreenFlags(si.j, true);
                greenWait = true;
                currentTime = System.nanoTime();
                if (!concurrentSound) sounds.get(currentSound).stop();
                sounds.get(currentSound).play(1f, ((Math.abs(swapRecord.size() - swapLength) / (float) swapLength) * 1.5f) + 0.5f, 1f); //pitch increases as animation approaches ending
            } else if (System.nanoTime() > currentTime + TimeUnit.MILLISECONDS.toNanos(600 - (values.getLength() * 15))) { //animation delay lessens with number of values
                greenWait = false;
                values.setGreenFlags(si.i, false);
                values.setGreenFlags(si.j, false);
                values.swap(si.i, si.j);
                swapRecord.remove(0);
            }
            if (swapRecord.isEmpty()) {
                replayingSort = false;
                sounds.get(currentSound).play(1f, 2.0f, 1f);
            }
        }
        values.drawShapes(sr); //value pillars
        for (Button b : buttons) b.drawShapes(sr); //buttons
        //selection box
        if (selectedButton != -1) { //starts OFF
            sr.set(ShapeRenderer.ShapeType.Line);
            sr.setColor(Color.RED);
            float[] sb = buttons.get(selectedButton).selectionBox(); //x, y, width, height
            sr.rect(sb[0], sb[1], sb[2], sb[3]);
        }
        sr.end();

        //text
        batch.begin();
        values.drawText(batch); //value pillars numbers
        for (Button b : buttons) b.drawText(batch); //button text
        font.draw(batch, "F1: Conc. sound: " + concurrentSound, 2f, HEIGHT - 2); //top-left corner text
        if (swapRecord != null) {
            font.draw(batch, "Swaps: " + (-1 * (swapRecord.size() - swapLength)) + "/" + swapLength, WIDTH - 140f, HEIGHT); //top-right swap number text
            font.draw(batch, "Real solving time: " + new DecimalFormat("0,000,000").format(sortTime) + " ns (" + TimeUnit.NANOSECONDS.toMillis(sortTime) + " ms)", WIDTH - 450f, HEIGHT);
        }
        batch.end();
    }

    public void replaySort(ArrayList<SwapIndex> swapRecord, long sortTime) {
        if (!replayingSort && !swapRecord.isEmpty()) {
            replayingSort = true;
            this.swapRecord = swapRecord;
            swapLength = swapRecord.size();
            this.sortTime = sortTime;
        }
        currentSound = new Random().nextInt(8); //0~7
    }

    public Values genNewValues() { return genNewValues(tableSize); }

    public Values genNewValues(int num) {
        values.dispose();
        values = new Values(num);
        return values;
    }

    public void changeTableSize(int num) {
        tableSize += num;
        for (Button b : buttons) {
            if (b.getId().equals("newtable")) {
                b.changeText("New table: " + tableSize);
                return;
            }
        }
    }

    public void toggleConcurrentSound() { concurrentSound = !concurrentSound; }

    public void changeSelectedButton(int num) {
        selectedButton += num;
        if (selectedButton >= buttons.size()) selectedButton = 0;
        if (selectedButton <= -1) selectedButton = buttons.size() - 1;
    }

    public void execSelectedButton() {
        buttons.get(selectedButton).run();
    }

    @Override
    public void dispose() {
        sr.dispose();
        batch.dispose();
        values.dispose();
        for (Button b : buttons) b.dispose();
        for (Sound s : sounds) s.dispose();
    }
}
