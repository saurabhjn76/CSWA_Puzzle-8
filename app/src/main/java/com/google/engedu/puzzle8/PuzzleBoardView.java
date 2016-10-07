package com.google.engedu.puzzle8;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Random;

public class PuzzleBoardView extends View {
    public static final int NUM_SHUFFLE_STEPS = 40;
    private Activity activity;
    private PuzzleBoard puzzleBoard;
    private ArrayList<PuzzleBoard> animation;
    private Random random = new Random();

    public PuzzleBoardView(Context context) {
        super(context);
        activity = (Activity) context;
        animation = null;
    }

    public void initialize(Bitmap imageBitmap) {
        int width = getWidth();
        Log.e("ParentWidth",width+"");
        puzzleBoard = new PuzzleBoard(imageBitmap, width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (puzzleBoard != null) {
            if (animation != null && animation.size() > 0) {
                puzzleBoard = animation.remove(0);
                puzzleBoard.draw(canvas);
                if (animation.size() == 0) {
                    animation = null;
                    puzzleBoard.reset();
                    Toast toast = Toast.makeText(activity, "Solved! ", Toast.LENGTH_LONG);
                    toast.show();
                } else {
                    this.postInvalidateDelayed(500);
                }
            } else {
                puzzleBoard.draw(canvas);
            }
        }
    }

    public void shuffle() {
        if (animation == null && puzzleBoard != null) {
            // Do something. Then:
            for(int i=0; i<NUM_SHUFFLE_STEPS;i++){
                puzzleBoard=puzzleBoard.neighbours().get(random.nextInt(puzzleBoard.neighbours().size()));
            }
            puzzleBoard.reset();
            invalidate();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (animation == null && puzzleBoard != null) {
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (puzzleBoard.click(event.getX(), event.getY())) {
                        invalidate();
                        if (puzzleBoard.resolved()) {
                            Toast toast = Toast.makeText(activity, "Congratulations!", Toast.LENGTH_LONG);
                            toast.show();
                        }
                        return true;
                    }
            }
        }
        return super.onTouchEvent(event);
    }

    public void solve() {
        ArrayList<PuzzleBoard> solution = new ArrayList<PuzzleBoard>();
        Comparator<PuzzleBoard> puzzleBoardComparator =  new Comparator<PuzzleBoard>() {
            @Override
            public int compare(PuzzleBoard lhs, PuzzleBoard rhs) {
                if(lhs.priority()>rhs.priority())
                    return  1;
                else if(rhs.priority()>lhs.priority())
                    return -1;
                else
                return 0;
            }
        };
        PriorityQueue<PuzzleBoard> boardsQueue = new PriorityQueue<PuzzleBoard>(10000,puzzleBoardComparator);
        puzzleBoard.steps=0;
        puzzleBoard.previousBoard=null;
        boardsQueue.add(puzzleBoard);
        while(!boardsQueue.isEmpty()){
            PuzzleBoard pathBoard= boardsQueue.poll();
            if(pathBoard.priority()-pathBoard.steps!=0){
                for(PuzzleBoard board:pathBoard.neighbours()){
                    boardsQueue.add(board);
                }
            }
            else{
                solution.add(pathBoard);
                while(pathBoard!=null){
                    if(pathBoard.getPreviousBoard()==null)
                        break;
                    solution.add(pathBoard.getPreviousBoard());
                    pathBoard=pathBoard.getPreviousBoard();
                }
                Collections.reverse(solution);
                animation=solution;
                invalidate();
                break;

            }
        }
    }
}
