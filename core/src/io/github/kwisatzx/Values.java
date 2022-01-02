package io.github.kwisatzx;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static io.github.kwisatzx.Constants.HEIGHT;
import static io.github.kwisatzx.Constants.WIDTH;

public class Values {
    private final int[] values;
    private final Main app;
    private final BitmapFont font;
    private final ArrayList<SwapIndex> swapRecord;
    private int[] valuesCopy;
    private boolean[] greenFlags;
    private float filledWidth;
    private float spaceWidth;
    private float filledPxPerUnit;
    private long currentTime;

    {
        app = (Main) Gdx.app.getApplicationListener();
        font = new BitmapFont();
        font.setColor(0.9f, 0.9f, 0.9f, 1f);
        swapRecord = new ArrayList<>();
    }

    public Values() {
        this(10);
    }

    public Values(int num) {
        values = new int[num];
        fillArray();
        calculateDimensions();
    }

    public Values(int[] array) {
        values = array;
        calculateDimensions();
    }

    public void drawShapes(@NotNull ShapeRenderer sr) {
        float y = 0, x = 0;
        int i = 0;
        for (int val : values) {
            x += spaceWidth;
            if (greenFlags[i]) sr.setColor(0.1f, 0.9f, 0.1f, 1f);
            sr.rect(x, y, filledWidth, (val * filledPxPerUnit));
            if (greenFlags[i]) sr.setColor(0.9f, 0.1f, 0.1f, 1f);
            x += filledWidth;
            x += spaceWidth;
            i++;
        }
    }

    public void drawText(@NotNull SpriteBatch batch) {
        if (values.length >= 60) return;
        float y = 30, x = 0, move = 0;
        if (values.length <= 30) move = 13;
        else move = 10;
        for (int i : values) {
            x += spaceWidth;
            x += Math.floor(filledWidth / 2) - move;
            font.draw(batch, String.valueOf(i), x, y);
            x += Math.floor(filledWidth / 2) + move;
            x += spaceWidth;
        }
    }

    private void fillArray() {
        Random rng = new Random();
        for (int i = 0; i < values.length; i++) {
            values[i] = rng.nextInt(1025);
        }

        valuesCopy = values.clone();
    }

    //1200/num of pillars, each pillar is [10% empty space - 80% filled shape - 10% empty space]
    private void calculateDimensions() {
        float segment = WIDTH / values.length;
        spaceWidth = segment * 0.1f;
        filledWidth = segment * 0.8f;

        int max = 0;
        for (int i : values) {
            if (i > max) max = i;
        }
        filledPxPerUnit = (HEIGHT-30f) / (float) max;

        greenFlags = new boolean[values.length];
        Arrays.fill(greenFlags, false);
    }

    private void copySwap(int i, int j) {
        swapRecord.add(new SwapIndex(i, j));
        int temp = valuesCopy[i];
        valuesCopy[i] = valuesCopy[j];
        valuesCopy[j] = temp;
    }

    public void swap(int i, int j) {
        int temp = values[i];
        values[i] = values[j];
        values[j] = temp;
    }

    public void bubbleSort() {
        currentTime = System.nanoTime();
        int n = valuesCopy.length;
        do {
            int newN = 0;
            for (int i = 1; i < n; i++) {
                if (valuesCopy[i] < valuesCopy[i - 1]) {
                    copySwap(i, i - 1);
                    newN = i;
                }
            }
            n = newN;
        } while (n > 1);
        app.replaySort(swapRecord, System.nanoTime()-currentTime);
    }

    public void insertionSort() {
        currentTime = System.nanoTime();
        int i = 1, j;
        while (i < valuesCopy.length) {
            j = i;
            while ( (j > 0) && (valuesCopy[j-1] > valuesCopy[j]) ) {
                copySwap(j, j - 1);
                j--;
            }
            i++;
        }
        app.replaySort(swapRecord, System.nanoTime()-currentTime);
    }

    public void selectionSort() {
        currentTime = System.nanoTime();
        int n = valuesCopy.length;
        for (int i = 0; i < n-1; i++) {
            int min = i;
            for (int j = i+1; j < n; j++) {
                if (valuesCopy[j] < valuesCopy[min]) {
                    min = j;
                }
            }
            copySwap(i, min);
        }
        app.replaySort(swapRecord, System.nanoTime()-currentTime);
    }

    public void setGreenFlags(int index, boolean b) { greenFlags[index] = b; }

    public int getLength() { return values.length; }

    public void dispose() { font.dispose(); }
}

//TODO: change to Point?
class SwapIndex {
    int i;
    int j;

    public SwapIndex(int i, int j) {
        this.i = i;
        this.j = j;
    }
}
