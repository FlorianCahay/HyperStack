package fr.uge.hyperstack.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.io.Serializable;
import java.util.Stack;

import fr.uge.hyperstack.model.Mode;
import fr.uge.hyperstack.model.Stroke;
import fr.uge.hyperstack.view.listener.EditorViewListener;

public class EditorView extends View implements Serializable {
    private final Paint paint = new Paint();
    private EditorViewListener editorViewListener;
    private fr.uge.hyperstack.model.Stack currentStack;
    private java.util.Stack<Stroke> strokeStack = new java.util.Stack<>();
    public int currentSlide = 0;
    private Mode currentMode = Mode.SELECTION;
    public boolean drawModeOn;

    public EditorView(Context context) {
        super(context);
        init(null, 0);
    }

    public EditorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public EditorView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {

    }

    public void setEditorViewListener(EditorViewListener evl) {
        this.editorViewListener = evl;
        invalidate();
    }

    public fr.uge.hyperstack.model.Stack getCurrentStack() {
        return currentStack;
    }

    public void setCurrentStack(fr.uge.hyperstack.model.Stack currentStack) {
        this.currentStack = currentStack;
    }

    public Stack<Stroke> getStrokeStack() {
        return strokeStack;
    }

    public void setCurrentMode(Mode currentMode) {
        this.currentMode = currentMode;
    }

    public EditorViewListener getEditorViewListener() {
        return editorViewListener;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);

        if (currentSlide != -1) {
            currentStack.drawSlide(canvas, paint, currentSlide);
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();

        double x = event.getX();
        double y = event.getY();

        if (currentMode == Mode.DRAW) {
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    editorViewListener.onFingerTouch(x, y);
                    invalidate();
                    return true;
                case MotionEvent.ACTION_MOVE:
                    editorViewListener.onFingerMove(x, y);
                    break;
                case MotionEvent.ACTION_UP:
                    editorViewListener.onFingerRaise(x, y);
                    break;

            }
        }
        invalidate();
        return super.onTouchEvent(event);
    }



}