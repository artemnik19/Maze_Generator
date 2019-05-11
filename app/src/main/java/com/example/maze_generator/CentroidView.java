package com.example.maze_generator;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import java.util.jar.Attributes;

public class CentroidView extends View {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint, text, edge;
    private float canvasSize;

    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSize, marginView;
    private float mScaleFactor;

    static float height,radius,onecicle;

    private final GestureDetector detector;

    //перевод dp в пиксели
    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }

    public CentroidView(Context context, AttributeSet attrs) {
        super(context,attrs);

        viewSize=(int)convertDpToPixel(350, context);
        marginView=(int)convertDpToPixel(5,context);


        mScaleFactor=1f;
        canvasSize=(int)(viewSize*mScaleFactor);

        mBitmap = Bitmap.createBitmap((int) canvasSize+2*marginView, (int) canvasSize+2*marginView, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        //параметры кисти для отрисовки вершин
        paint =new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);



        int countvertexinline = CentroidDecomposition.MaxVertexinLevel();
        float onecicle = canvasSize/countvertexinline;
        float height = canvasSize/2/CentroidDecomposition.LOGN;
        float radius  = onecicle/2;
        //параметры кисти для отрисовки чисел
        text = new Paint();
        text.setAntiAlias(true);
        text.setDither(true);
        text.setColor(0xff000000);
        text.setTextSize(radius);
        //параметры кисти для отрисовки ребер
        edge = new Paint();
        edge.setAntiAlias(true);
        edge.setDither(true);
        edge.setColor(0xff000000);
        paint.setStrokeWidth(2f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);


        for (int i = 0; i < CentroidDecomposition.LOGN; i++) {
            int size = CentroidDecomposition.LevelList[i].size();
            for (int j = 1; j <= size; j++) {
                mCanvas.drawCircle(marginView+j*canvasSize/size-canvasSize/size/2,i*height + onecicle,radius,paint);
                mCanvas.drawText(Integer.toString(CentroidDecomposition.LevelList[i].get(j-1)),marginView+j*canvasSize/size-canvasSize/size/2-onecicle/4,i*height + onecicle*5/4,text);
            }

        }
        for (int i = 1; i <CentroidDecomposition.LOGN; i++) {
            int size = CentroidDecomposition.LevelList[i].size();
            int size_predok = CentroidDecomposition.LevelList[i-1].size();
            for (int j = 1 ; j <= size ; j++) {
                int aa = CentroidDecomposition.LevelList[i].get(j-1);
                int bb = CentroidDecomposition.Parent[aa];
                int cc = CentroidDecomposition.LevelList[i-1].indexOf(bb)+1;
                mCanvas.drawLine(marginView+j*canvasSize/size-canvasSize/size/2,i*height + onecicle-radius,marginView+cc*canvasSize/size_predok-canvasSize/size_predok/2,(i-1)*height + onecicle+radius,edge);
            }

        }
        if(GameView.enterX!=-1&&GameView.enterY!=-1){
            int value = MazeGener.Vertex[GameView.enterX][GameView.enterY];
            CentroidDecomposition.findexit(value);
            int ii=GameView.enterX,jj=GameView.enterY;
            for (int i = 0; i < CentroidDecomposition.way.size(); i++) {
                int v = CentroidDecomposition.way.get(i);
                for (int j = 0; j < MazeGener.maxN; j++) {
                    for (int k = 0; k < MazeGener.maxN; k++) {
                        if(MazeGener.Vertex[j][k]==v) {
                            ii = j;
                            jj = k;
                        }
                    }
                }
                drawway(ii,jj,value);
            }

        }







        scaleGestureDetector=new ScaleGestureDetector(context, new MyScaleGestureListener());

        detector=new GestureDetector(context, new MyGestureListener());
    }

    void drawway(int cellX, int cellY,int value){
        int color = Color.YELLOW;
        if(value == CentroidDecomposition.way.get(0))
            color=Color.GREEN;
        if(value == CentroidDecomposition.way.get(CentroidDecomposition.way.size()-1))
            color=Color.RED;
        if(value == CentroidDecomposition.lcaD)
            color= Color.BLUE;

        for (int i = 0; i < CentroidDecomposition.LOGN; i++) {
            int size = CentroidDecomposition.LevelList[i].size();
            for (int j = 1; j <= size; j++) {
                if(CentroidDecomposition.LevelList[i].get(j-1)==value) {
                    cellX = i;
                    cellY = j-1;
                }
            }
        }
        int size = CentroidDecomposition.LevelList[cellX].size();
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        mCanvas.drawCircle(marginView+cellY*canvasSize/size-canvasSize/size/2,cellX*height + onecicle,radius,paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        paint.setColor(color);
        mCanvas.drawCircle(marginView+cellY*canvasSize/size-canvasSize/size/2,cellX*height + onecicle,radius,paint);
        mCanvas.drawText(Integer.toString(value),marginView+cellY*canvasSize/size-canvasSize/size/2-onecicle/4,cellX*height + onecicle*5/4,text);
        invalidate();//перерисовываем канвас
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        canvas.scale(mScaleFactor, mScaleFactor);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        detector.onTouchEvent(event);
        scaleGestureDetector.onTouchEvent(event);
        return true;
    }


    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();
            if (mScaleFactor * scaleFactor > 1 && mScaleFactor * scaleFactor < 4) {
                mScaleFactor *= scaleGestureDetector.getScaleFactor();
                canvasSize = viewSize * mScaleFactor;

                int scrollX = (int) ((getScrollX() + focusX) * scaleFactor - focusX);
                scrollX = Math.min(Math.max(scrollX, 0), (int) canvasSize - viewSize);
                int scrollY = (int) ((getScrollY() + focusY) * scaleFactor - focusY);
                scrollY = Math.min(Math.max(scrollY, 0), (int) canvasSize - viewSize);
                scrollTo(scrollX, scrollY);
            }
            invalidate();
            return true;
        }
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {

            if(getScrollX()+distanceX< canvasSize -viewSize && getScrollX()+distanceX>0){
                scrollBy((int)distanceX, 0);
            }

            if(getScrollY()+distanceY< canvasSize -viewSize && getScrollY()+distanceY>0){
                scrollBy(0, (int)distanceY);
            }
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent event){
            int cellX=(int)((event.getX()+getScrollX())/mScaleFactor);
            int cellY=(int)((event.getY()+getScrollY())/mScaleFactor);
            return true;
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent event){
            mScaleFactor=1f;
            canvasSize =viewSize;
            scrollTo(0, 0);
            invalidate();
            return true;
        }
    }
}
