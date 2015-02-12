package projectparissud.multimodalhangman;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class HangmanCanvasView extends View {

    private Paint paint;
    private int score;

    public HangmanCanvasView(Context c, AttributeSet attrs) {
        super(c, attrs);

        this.paint = new Paint();
        this.paint.setStyle(Paint.Style.FILL);
        this.paint.setStrokeWidth(20);
        this.paint.setColor(Color.GRAY);

        this.score = 10;
    }

    // override onDraw
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // calculate points of the drawing
        float x = getWidth() / 8;
        float y = getHeight() / 8;

        float[] pointA = {x * 1, y * 7};
        float[] pointB = {x * 7, y * 7};
        float[] pointC = {x * 2, y * 7};
        float[] pointD = {x * 2, y * 1};
        float[] pointE = {x * 5, y * 1};
        float[] pointF = {x * 2, y + x};
        float[] pointG = {x * 3, y * 1};
        float[] pointH = {x * 5, y * 3};
        float[] pointI = {x * 5, y * 5};
        float[] pointJ = {x * 4, y * 6};
        float[] pointK = {x * 6, y * 6};
        float[] pointL = {x * 5, y * 4};
        float[] pointM = {x * 4, y * 3};
        float[] pointN = {x * 6, y * 3};

        // draw hangman
        if (this.score < 10) {
            canvas.drawLine(pointA[0], pointA[1], pointB[0], pointB[1], this.paint);
            if (this.score < 9) {
                canvas.drawLine(pointC[0], pointC[1], pointD[0], pointD[1], this.paint);
                if (this.score < 8) {
                    canvas.drawLine(pointD[0], pointD[1], pointE[0], pointE[1], this.paint);
                    if (this.score < 7) {
                        canvas.drawLine(pointF[0], pointF[1], pointG[0], pointG[1], this.paint);
                        if (this.score < 6) {
                            canvas.drawLine(pointE[0], pointE[1], pointH[0], pointH[1], this.paint);
                            if (this.score < 5) {
                                canvas.drawCircle(pointH[0], pointH[1], x / 2, this.paint);
                                if (this.score < 4) {
                                    canvas.drawLine(pointH[0], pointH[1], pointI[0], pointI[1], this.paint);
                                    if (this.score < 3) {
                                        canvas.drawLine(pointI[0], pointI[1], pointJ[0], pointJ[1], this.paint);
                                        if (this.score < 2) {
                                            canvas.drawLine(pointI[0], pointI[1], pointK[0], pointK[1], this.paint);
                                            if (this.score < 1) {
                                                canvas.drawLine(pointL[0], pointL[1], pointM[0], pointM[1], this.paint);
                                                canvas.drawLine(pointL[0], pointL[1], pointN[0], pointN[1], this.paint);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void drawHangman(int score) {
        this.score = score;
        invalidate();
    }
}