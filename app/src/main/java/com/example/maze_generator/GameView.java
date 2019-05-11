package com.example.maze_generator;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;


public class GameView extends View {
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint paint, mBitmapPaint, number_paint,exit_paint,enter_paint;
    private float canvasSize;
    private final int horizontalCountOfCells, verticalCountOfCells;

    private final ScaleGestureDetector scaleGestureDetector;
    private final int viewSize;
    private float mScaleFactor;

    private final GestureDetector detector;

    private Rect mTextRect = new Rect();

    static float mTextWidth, mTextHeight;
    static int enterX=-1, enterY=-1;




    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        horizontalCountOfCells =10;
        verticalCountOfCells =10;
        viewSize=(int)convertDpToPixel(350, context);
        //marginView=(int)convertDpToPixel(5,context);


        mScaleFactor=1f;
        canvasSize=(int)(viewSize*mScaleFactor);

        mBitmap = Bitmap.createBitmap((int) canvasSize, (int) canvasSize, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mBitmapPaint = new Paint(Paint.DITHER_FLAG);

        paint =new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setColor(0xff2b23a6);
        paint.setStrokeWidth(5f);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);

        exit_paint =new Paint();
        exit_paint.setAntiAlias(true);
        exit_paint.setDither(true);
        exit_paint.setColor(0xffff0505);

        enter_paint =new Paint();
        enter_paint.setAntiAlias(true);
        enter_paint.setDither(true);
        enter_paint.setColor(0xff00ff00);




        number_paint = new Paint();
        number_paint.setAntiAlias(true);
        number_paint.setDither(true);
        number_paint.setColor(0xff000000);
        number_paint.setTextSize(70f);





        MazeGener.start();
        CentroidDecomposition.startDecompos();





        //рисуем рамку
        mCanvas.drawLine(0,0,canvasSize,0,paint);
        mCanvas.drawLine(canvasSize,0,canvasSize,canvasSize,paint);
        mCanvas.drawLine(canvasSize,canvasSize,0,canvasSize,paint);
        mCanvas.drawLine(0,canvasSize,0,0,paint);

        //рисуем числа



        for (int y = 1; y <verticalCountOfCells+1 ; y++) {
            for (int x = 0; x < horizontalCountOfCells; x++) {
                if(MazeGener.ExitVertex[y-1][x]==1)
                    mCanvas.drawCircle(x*canvasSize/horizontalCountOfCells+canvasSize/2/horizontalCountOfCells,y*canvasSize/verticalCountOfCells-canvasSize/2/verticalCountOfCells,canvasSize/2/horizontalCountOfCells-3,exit_paint);

                number_paint.getTextBounds(Integer.toString(MazeGener.Vertex[y-1][x]),0,Integer.toString(MazeGener.Vertex[y-1][x]).length(),mTextRect);
                mTextWidth = number_paint.measureText(Integer.toString(MazeGener.Vertex[y-1][x]));
                mTextHeight = mTextRect.height();

                mCanvas.drawText(Integer.toString(MazeGener.Vertex[y-1][x]),(float)x*canvasSize / (horizontalCountOfCells)+canvasSize/2/horizontalCountOfCells- (mTextWidth / 2f),(float)y*canvasSize / (horizontalCountOfCells)-canvasSize/2/verticalCountOfCells+ (mTextHeight /2f),number_paint);

            }
        }

        //рисуем вертикальные стенки
        for (int y = 0; y < verticalCountOfCells; y++) {
            int v = MazeGener.Vertex[y][0];
            for (int x = 1; x < horizontalCountOfCells; x++) {
                int to = MazeGener.Vertex[y][x];
                if(!MazeGener.AdjacencyList[v].contains(to)){
                    mCanvas.drawLine(x*canvasSize/horizontalCountOfCells,y*canvasSize/verticalCountOfCells,x*canvasSize/horizontalCountOfCells,(y+1)*canvasSize/verticalCountOfCells,paint);
                }
                v = to;
            }

        }


        //рисуем горизонтальные стенки
        for (int x = 0; x < horizontalCountOfCells; x++) {
            int v = MazeGener.Vertex[0][x];
            for (int y = 1; y < verticalCountOfCells; y++) {
                int to = MazeGener.Vertex[y][x];
                if(!MazeGener.AdjacencyList[v].contains(to)){
                    mCanvas.drawLine(x*canvasSize/horizontalCountOfCells,y*canvasSize/verticalCountOfCells,(x+1)*canvasSize/horizontalCountOfCells,y*canvasSize/verticalCountOfCells,paint);
                }
                v = to;
            }

        }





        scaleGestureDetector=new ScaleGestureDetector(context, new MyScaleGestureListener());

        detector=new GestureDetector(context, new MyGestureListener());
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



    public float convertDpToPixel(float dp,Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi/160f);
    }


    private class MyScaleGestureListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector) {
            float scaleFactor = scaleGestureDetector.getScaleFactor();

            float focusX = scaleGestureDetector.getFocusX();
            float focusY = scaleGestureDetector.getFocusY();

            if (mScaleFactor * scaleFactor > 1 && mScaleFactor * scaleFactor < 2) {
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
    void drawExit(int cellX, int cellY){
        //считаем координаты центра ячейки
        float x0=((1f/(2* horizontalCountOfCells))*viewSize+(1f/ horizontalCountOfCells)*cellX*viewSize);
        float y0=((1f/(2* verticalCountOfCells))*viewSize+(1f/ verticalCountOfCells)*cellY*viewSize);

        exit_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //заполнение обойденного участка
        //рисуем большой круг, на месте которого ничего не останется
        mCanvas.drawCircle(x0, y0, viewSize/2/horizontalCountOfCells-3, exit_paint);
        //возвращаем исходные параметры

        exit_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        exit_paint.setColor(0xffff0505);

        mCanvas.drawCircle(x0, y0, viewSize/2/horizontalCountOfCells-3, exit_paint);//рисуем один атом в центре ячейки
        number_paint.getTextBounds(Integer.toString(MazeGener.Vertex[cellY][cellX]),0,Integer.toString(MazeGener.Vertex[cellY][cellX]).length(),mTextRect);
        mTextWidth = number_paint.measureText(Integer.toString(MazeGener.Vertex[cellY][cellX]));
        mTextHeight = mTextRect.height();

        mCanvas.drawText(Integer.toString(MazeGener.Vertex[cellY][cellX]),x0- (mTextWidth / 2f),y0+ (mTextHeight /2f),number_paint);
        invalidate();//перерисовываем канвас
        MazeGener.ExitList.add(MazeGener.Vertex[cellY][cellX]);
        MazeGener.ExitVertex[cellY][cellX] = 1;
    }

    void drawEnter(int cellX, int cellY){
        //считаем координаты центра ячейки
        float x0=((1f/(2* horizontalCountOfCells))*viewSize+(1f/ horizontalCountOfCells)*cellX*viewSize);
        float y0=((1f/(2* verticalCountOfCells))*viewSize+(1f/ verticalCountOfCells)*cellY*viewSize);

        if(enterX>-1&&enterY>-1){
            float x00=((1f/(2* horizontalCountOfCells))*viewSize+(1f/ horizontalCountOfCells)*enterX*viewSize);
            float y00=((1f/(2* verticalCountOfCells))*viewSize+(1f/ verticalCountOfCells)*enterY*viewSize);
            enter_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            mCanvas.drawCircle(x00, y00, viewSize/2/horizontalCountOfCells-3, enter_paint);
            enter_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
            enter_paint.setColor(0xff00ff00);
            number_paint.getTextBounds(Integer.toString(MazeGener.Vertex[enterY][enterX]),0,Integer.toString(MazeGener.Vertex[enterY][enterX]).length(),mTextRect);
            mTextWidth = number_paint.measureText(Integer.toString(MazeGener.Vertex[enterY][enterX]));
            mTextHeight = mTextRect.height();
            mCanvas.drawText(Integer.toString(MazeGener.Vertex[enterY][enterX]),x00- (mTextWidth / 2f),y00+ (mTextHeight /2f),number_paint);
        }

        enter_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        //заполнение обойденного участка
        //рисуем большой круг, на месте которого ничего не останется
        mCanvas.drawCircle(x0, y0, viewSize/2/horizontalCountOfCells-3, enter_paint);
        //возвращаем исходные параметры

        enter_paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
        enter_paint.setColor(0xff00ff00);

        mCanvas.drawCircle(x0, y0, viewSize/2/horizontalCountOfCells-3, enter_paint);//рисуем один атом в центре ячейки
        number_paint.getTextBounds(Integer.toString(MazeGener.Vertex[cellY][cellX]),0,Integer.toString(MazeGener.Vertex[cellY][cellX]).length(),mTextRect);
        mTextWidth = number_paint.measureText(Integer.toString(MazeGener.Vertex[cellY][cellX]));
        mTextHeight = mTextRect.height();

        mCanvas.drawText(Integer.toString(MazeGener.Vertex[cellY][cellX]),x0- (mTextWidth / 2f),y0+ (mTextHeight /2f),number_paint);
        invalidate();//перерисовываем канвас
        enterX = cellX;
        enterY = cellY;
    }


    private class MyGestureListener extends GestureDetector.SimpleOnGestureListener
    {

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY)
        {

            if(getScrollX()+distanceX< canvasSize -viewSize+10 && getScrollX()+distanceX>-10){
                scrollBy((int)distanceX, 0);

            }

            if(getScrollY()+distanceY< canvasSize -viewSize+10 && getScrollY()+distanceY>-10){
                scrollBy(0, (int)distanceY);
            }
            return true;
        }


        @Override
        public boolean onSingleTapConfirmed(MotionEvent event){

            int cellX=(int)((event.getX()+getScrollX())/mScaleFactor);
            int cellY=(int)((event.getY()+getScrollY())/mScaleFactor);
            drawExit((int)(horizontalCountOfCells *cellX/viewSize), (int)(verticalCountOfCells *cellY/viewSize));
            return true;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            int cellX=(int)((e.getX()+getScrollX())/mScaleFactor);
            int cellY=(int)((e.getY()+getScrollY())/mScaleFactor);
            drawEnter((int)(horizontalCountOfCells *cellX/viewSize), (int)(verticalCountOfCells *cellY/viewSize));

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

