package com.google.engedu.puzzle8;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;

import java.util.ArrayList;


public class PuzzleBoard {

    private static final int NUM_TILES = 3;
    int steps=0;
    PuzzleBoard previousBoard =null;
    private static final int[][] NEIGHBOUR_COORDS = {
            { -1, 0 },
            { 1, 0 },
            { 0, -1 },
            { 0, 1 }
    };
    private ArrayList<PuzzleTile> tiles = new ArrayList<PuzzleTile>();

    PuzzleBoard(Bitmap bitmap, int parentWidth) {
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, parentWidth, parentWidth, true);
        int chunkHeight = parentWidth/NUM_TILES;
       int  chunkWidth = parentWidth/NUM_TILES;
        int yCoord = 0;
        for(int x=0; x<NUM_TILES; x++){
            int xCoord = 0;
            for(int y=0; y<NUM_TILES; y++){
                 {
                     if(y==x && x==NUM_TILES-1)
                         tiles.add(null);
                     else {
                         tiles.add(new PuzzleTile(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight), NUM_TILES * x + y));
                         xCoord += chunkWidth;
                     }
                }
            }
            yCoord += chunkHeight;
        }
        Log.e("Tiles Length",tiles.toString()+"");
    }

    PuzzleBoard(PuzzleBoard otherBoard) {
        tiles = (ArrayList<PuzzleTile>) otherBoard.tiles.clone();
        steps= otherBoard.steps;
        previousBoard= otherBoard;
    }

    public void reset() {
        // Nothing for now but you may have things to reset once you implement the solver.
    }
    public  PuzzleBoard getPreviousBoard(){
        return this.previousBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;
        return tiles.equals(((PuzzleBoard) o).tiles);
    }

    public void draw(Canvas canvas) {
        if (tiles == null) {
            return;
        }
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                tile.draw(canvas, i % NUM_TILES, i / NUM_TILES);
            }
        }
    }

    public boolean click(float x, float y) {
        for (int i = 0; i < NUM_TILES * NUM_TILES; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile != null) {
                if (tile.isClicked(x, y, i % NUM_TILES, i / NUM_TILES)) {
                    return tryMoving(i % NUM_TILES, i / NUM_TILES);
                }
            }
        }
        return false;
    }

    private boolean tryMoving(int tileX, int tileY) {
        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = tileX + delta[0];
            int nullY = tileY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES &&
                    tiles.get(XYtoIndex(nullX, nullY)) == null) {
                swapTiles(XYtoIndex(nullX, nullY), XYtoIndex(tileX, tileY));
                return true;
            }

        }
        return false;
    }

    public boolean resolved() {
        for (int i = 0; i < NUM_TILES * NUM_TILES - 1; i++) {
            PuzzleTile tile = tiles.get(i);
            if (tile == null || tile.getNumber() != i)
                return false;
        }
        return true;
    }

    private int XYtoIndex(int x, int y) {
        return x + y * NUM_TILES;
    }

    protected void swapTiles(int i, int j) {
        PuzzleTile temp = tiles.get(i);
        tiles.set(i, tiles.get(j));
        tiles.set(j, temp);
    }

    public ArrayList<PuzzleBoard> neighbours() {
        int emptyX = 0, emptyY = 0;
        ArrayList<PuzzleBoard> neighbourBoard = new ArrayList<PuzzleBoard>();
        for (int i = 0; i < NUM_TILES; i++) {
            for (int j = 0; j < NUM_TILES; j++) {
                if (tiles.get(i + NUM_TILES * j) == null) {
                    emptyX = i;
                    emptyY = j;
                    j = NUM_TILES;
                    break;
                }
            }
        }

        for (int[] delta : NEIGHBOUR_COORDS) {
            int nullX = emptyX + delta[0];
            int nullY = emptyY + delta[1];
            if (nullX >= 0 && nullX < NUM_TILES && nullY >= 0 && nullY < NUM_TILES){
                PuzzleBoard newBoard = new PuzzleBoard(this);
                newBoard.swapTiles(emptyX + NUM_TILES* emptyY, nullX + NUM_TILES*nullY);
                neighbourBoard.add(newBoard);
            }
        }
        return neighbourBoard;
    }

    public int priority() {
        int manhattanDistance=0;
        int realPosition=0;
        for(int i=0;i<NUM_TILES;i++){
            for(int j=0;j<NUM_TILES;j++){
                if(tiles.get(i+NUM_TILES*j)!=null){
                    realPosition=tiles.get(i+NUM_TILES*j).getNumber();
                    manhattanDistance+=Math.abs(realPosition/NUM_TILES-j);
                    manhattanDistance+=Math.abs(realPosition%NUM_TILES-i);

                }
            }
        }
        return manhattanDistance+steps;
    }

}
