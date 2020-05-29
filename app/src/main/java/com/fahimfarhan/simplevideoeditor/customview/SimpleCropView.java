package com.fahimfarhan.simplevideoeditor.customview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;

import com.fahimfarhan.simplevideoeditor.R;


public class SimpleCropView extends View {
    public static final String TAG = SimpleCropView.class.getSimpleName();
    // Konsts
    private static final int HANDLE_SIZE_IN_DP = 14;

    // variables
    private Context context;
    private AttributeSet attrs;
    private int minimumSideLength;
    private int minimumSideHight;
    private float ratioX = 1;
    private float ratioY = 1;
    private float aspectRatio = ratioX/ratioY;    // todo: idk, change it later to 16:9, 3:4, 1:1 ... ... via some method I guess

    private float mHandleSize;
    private float mTouchPadding;

    private float x0 = 0;
    private float y0 = 0;
    private float viewWidth = 100;  // todo: initial value. Maybe change it later
    private float viewHeight = 100;
    private float viewFrameTop = y0;
    private float viewFrameBottom = y0 + viewHeight;
    private float viewFrameLeft = x0;
    private float viewFrameRight = x0 + viewWidth;
    private int initialDx = 0;

    //drawing objects
    private Paint paint;

    //point objects
    private Point[] points;
    private Point start;
    private Point offset;

    private int sidex;
    private int sidey;

    private int halfCorner; // todo: watch out for this... idk why it is here
    //      jhamela lagle maybe halfCOrnerX, halfCornerY = halfCOrnerX*aspectRatio erokom kisu use korte hobe
    private int cornerColor;
    private int edgeColor;
    private int outsideColor;
    private int corner = 5;

    //variable booleans
    private boolean initialized = false;

    //drawables
    private Drawable moveDrawable;
    private Drawable resizeDrawable1, resizeDrawable2, resizeDrawable3;
    private TouchArea mTouchArea;
    private float mLastX;
    private float mLastY;
    private float touchAreaRadiusSquared = 100;
    int cropQuadHeight = 0;
    int cropQuadWidth = 0;

    boolean drawCornerCircles = false;

    // enum
    private enum TouchArea {
        OUT_OF_BOUNDS, CENTER, LEFT_TOP, RIGHT_TOP, LEFT_BOTTOM, RIGHT_BOTTOM
    }

    // constructor

    public SimpleCropView(Context context) {
        this(context, null);
    }

    public SimpleCropView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleCropView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initCropOverlayView(context, attrs);
    }

    public float getMyScalingX() {     return 1.0f*initialDx/(points[1].x - points[0].x);   }   // getScaleX() is present in view. so to disambiguate, this name

    private void initCropOverlayView(Context context, @Nullable AttributeSet attrs){
        this.context = context;
        this.attrs = attrs;

        viewHeight = 100;
        viewWidth = 100;

        x0 = getX();
        y0 = getY();

        paint = new Paint();
        start = new Point();
        offset = new Point();

        TypedArray ta = context.getTheme().obtainStyledAttributes(attrs, R.styleable.IconCropView,
                0, 0);

        float density = getDensity();
        mHandleSize = (int) (density * HANDLE_SIZE_IN_DP);
        mTouchPadding = 0; // or some other value
        float touchAreaRadius = (mHandleSize+mTouchPadding);
        touchAreaRadiusSquared = touchAreaRadius*touchAreaRadius; // forgot to square it!!!
        //mHandleSize = ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_handle_size, (int) (HANDLE_SIZE_IN_DP * mDensity));
        //ta.getDimensionPixelSize(R.styleable.scv_CropImageView_scv_touch_padding, 0);

        //initial dimensions
        minimumSideLength = ta.getDimensionPixelSize(R.styleable.IconCropView_minimumSide, 100);
        minimumSideHight = (int) (minimumSideLength*aspectRatio);

        sidex = minimumSideLength;
        sidey = minimumSideHight;

        halfCorner = (ta.getDimensionPixelSize(R.styleable.IconCropView_cropOverlayCornerSize, 20))/2;

        //colors
        cornerColor = ta.getColor(R.styleable.IconCropView_cornerColor, Color.BLACK);
        edgeColor = ta.getColor(R.styleable.IconCropView_edgeColor, Color.WHITE);
        outsideColor = ta.getColor(R.styleable.IconCropView_outsideCropColor, Color.parseColor("#00000088"));

        initPoints();

        //init drawables
        moveDrawable = ta.getDrawable(R.styleable.IconCropView_moveCornerDrawable);
        resizeDrawable1 = ta.getDrawable(R.styleable.IconCropView_resizeCornerDrawable);
        resizeDrawable2 = ta.getDrawable(R.styleable.IconCropView_resizeCornerDrawable);
        resizeDrawable3 = ta.getDrawable(R.styleable.IconCropView_resizeCornerDrawable);

        //recycle attributes
        ta.recycle();

        //set initialized to true
        initialized = true;

    }

    void initPoints() {
        //initialize corners;
        if(points == null) {    points = new Point[4];  }

        if(getHeight() > (getWidth()/aspectRatio) ) {
            cropQuadHeight = (int) (getWidth()/aspectRatio);
            cropQuadWidth = getWidth();
        }else {
            cropQuadHeight = getHeight();
            cropQuadWidth = (int) (getHeight()*aspectRatio);
        }


        points[0] = new Point();
        points[1] = new Point();
        points[2] = new Point();
        points[3] = new Point();

        int cx = getWidth()/2;
        int cy = getHeight()/2;

        //init corner locations;
        //top left

        points[0].x = cx - cropQuadWidth/2;
        points[0].y = cy - cropQuadHeight/2;

        //top right
        points[1].x = points[0].x + cropQuadWidth;
        points[1].y = points[0].y;

        //bottom left
        points[2].x = points[0].x;
        points[2].y = points[0].y + cropQuadHeight;

        //bottom right
        points[3].x = points[0].x + cropQuadWidth;
        points[3].y = points[0].y + cropQuadHeight;
    }

    @Override
    protected void onSizeChanged(int xNew, int yNew, int xOld, int yOld){
        super.onSizeChanged(xNew, yNew, xOld, yOld);

        x0 = 0; // getX();
        y0 = 0; // getY();

        viewWidth = xNew;
        viewHeight = yNew;

        viewFrameTop = y0;
        viewFrameBottom = y0 + viewHeight;
        viewFrameLeft = x0;
        viewFrameRight = x0 + viewWidth;

        initPoints();
        initialDx = points[1].x - points[0].x;
        touchAreaRadiusSquared = (float) ((initialDx*0.3)*(initialDx*0.3));
    }

    public void resetAspectRatio(float ratioX1, float ratioY1) {
        this.ratioX = ratioX1;
        this.ratioY = ratioY1;
        this.aspectRatio = ratioX/ratioY;
        initPoints();
        initialDx = points[1].x - points[0].x;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //set paint to draw edge, stroke
        if(initialized) {
            paint.setAntiAlias(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setColor(edgeColor);
            paint.setStrokeWidth(4);

            //crop rectangle
            canvas.drawRect(points[0].x, points[0].y, points[3].x, points[3].y, paint);
            //set paint to draw outside color, fill
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(outsideColor);

            //top rectangle
            canvas.drawRect(0, 0, getWidth(), points[0].y, paint);
            //left rectangle
            canvas.drawRect(0, points[0].y, points[0].x, getHeight(), paint);
            //right rectangle
            canvas.drawRect(points[1].x, points[1].y, getWidth(), getHeight(), paint);
            //bottom rectangle
            canvas.drawRect(points[0].x, points[2].y, points[3].x, canvas.getHeight(), paint);

            if(drawCornerCircles) {
                //set bounds of drawables
                // todo: check & fix these corner coordinates. They work with tutorial project, but they are misaligned in my implementation :(
                moveDrawable.setBounds(points[0].x, points[0].y, points[0].x + halfCorner*2, points[0].y + halfCorner*2);
                resizeDrawable1.setBounds(points[1].x, points[1].y, points[1].x + halfCorner*2, points[1].y + halfCorner*2);
                resizeDrawable2.setBounds(points[2].x, points[2].y, points[2].x + halfCorner*2, points[2].y + halfCorner*2);
                resizeDrawable3.setBounds(points[3].x, points[3].y, points[3].x + halfCorner*2, points[3].y+ halfCorner*2);

                //place corner drawables
                moveDrawable.draw(canvas);
                resizeDrawable1.draw(canvas);
                resizeDrawable2.draw(canvas);
                resizeDrawable3.draw(canvas);
            }

        }
    }

    @Override public boolean onTouchEvent(MotionEvent event) {
        if( (!this.isClickable()) || (!this.isFocusable()) ) {
            return false;
        }
//        if (!mIsInitialized) return false;    // todo: uncomment them / use them if necessary
//        if (!mIsCropEnabled) return false;    // todo: these variables are needed for advanced cropping/fine control. idk how to use them
//        if (!mIsEnabled) return false;
//        if (mIsRotating) return false;
//        if (mIsAnimating) return false;
//        if (mIsLoading.get()) return false;
//        if (mIsCropping.get()) return false;

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                onDown(event);
                return true;
            case MotionEvent.ACTION_MOVE:
                onMove(event);
                if (mTouchArea != TouchArea.OUT_OF_BOUNDS) {
                    getParent().requestDisallowInterceptTouchEvent(true);
                }
                return true;
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                onCancel();
                return true;
            case MotionEvent.ACTION_UP:
                getParent().requestDisallowInterceptTouchEvent(false);
                onUp(event);
                return true;
        }
        return false;
    }

    private void onDown(MotionEvent e) {
        invalidate();
        mLastX = e.getX();
        mLastY = e.getY();
        checkTouchArea(e.getX(), e.getY());
    }

    private void onMove(MotionEvent e) {
        float diffX = e.getX() - mLastX;
        float diffY = e.getY() - mLastY;
        Log.e(TAG, "onMove#mTouchArea = "+mTouchArea.toString());
        switch (mTouchArea) {
            case LEFT_TOP:
                moveHandleLT(diffX, diffY);
                break;
            case RIGHT_TOP:
                moveHandleRT(diffX, diffY);
                break;
            case LEFT_BOTTOM:
                moveHandleLB(diffX, diffY);
                break;
            case RIGHT_BOTTOM:
                moveHandleRB(diffX, diffY);
                break;
            case CENTER:
                moveFrame(diffX, diffY);
                break;
            case OUT_OF_BOUNDS:
                break;
        }
        invalidate();
        mLastX = e.getX();
        mLastY = e.getY();
    }

    // Adjust frame ////////////////////////////////////////////////////////////////////////////////

    private void moveFrame(float x, float y) {
        for(int i=0; i<4; i++) {    points[i].x += x; points[i].y += y;     }
        checkMoveBounds();
    }

    private void checkMoveBounds() {
        // left 0,  2,  x
        float diff = points[0].x - viewFrameLeft;
        if (diff < 0) {
            for(int i=0; i<4; i++) {    points[i].x -= diff; }
        }
        // right 1, 3, x
        diff = points[1].x - viewFrameRight;
        if (diff > 0) {
            for(int i=0; i<4; i++) {    points[i].x -= diff; }
        }
        // top
        diff = points[0].y - viewFrameTop;
        if (diff < 0) {
            for(int i=0; i<4; i++) {    points[i].y -= diff; }
        }
        // bottom  2, 3, y
        diff = points[2].y - viewFrameBottom;
        if (diff > 0) {
            for(int i=0; i<4; i++) {    points[i].y -= diff; }
        }
    }

    /**
     * @brief: leftTop == points[0] that corner
     *          onMove, point 0, point 1, point 2 will be affected
     *          points[0].x += dx; points[0].y += dy;
     *          points[1].y += dy;
     *          points[2].x += dx;
     * */
    private void moveHandleLT(float diffX, float diffY) {
        // I think we are not gonna use the free crop, cz according to design, we have 1:1, 16:9 and 9:16 (or 4:3 , 3:4 whatever)
        // so I am only gonna use the ratio code
        float tempY0=0, tempX0=0;
        if(diffX > diffY) {
            tempX0 = points[0].x + diffX;;

            float dx = points[3].x - tempX0;
            float dy = dx / aspectRatio;

            tempY0 = points[3].y - dy;
        }else{
            // otherwise on vertical movement, crop area wont change size
            tempY0 = points[0].y + diffY;;

            float dy = points[3].y - tempY0;
            float dx = dy * aspectRatio;

            tempX0 = points[3].x - dx;
        }

        if(isInsideVertical(tempY0) && isInsideHorizontal(tempX0)) {
            // if new x0, y0 is inside, update the 3 points
            points[0].x = (int) tempX0;
            points[0].y = (int) tempY0;

            points[1].y = (int) tempY0;
            points[2].x = (int) tempX0;
        }

    }

    private void moveHandleRT(float diffX,float diffY){
        // point 1 variable, point 2 fixed
        float tempY0=0, tempX0=0;
        if(diffX > diffY) {
            tempX0 = points[1].x + diffX;;

            float dx = points[2].x - tempX0;
            float dy = dx / aspectRatio;

            tempY0 = points[2].y + dy;
        }else{
            // otherwise on vertical movement, crop area wont change size
            tempY0 = points[1].y + diffY;;

            float dy = points[2].y - tempY0;
            float dx = dy * aspectRatio;

            tempX0 = points[2].x + dx;
        }

        if(isInsideVertical(tempY0) && isInsideHorizontal(tempX0)) {
            // if new x0, y0 is inside, update the 3 points
            //0 y, 1 xy, ,3 x
            points[1].x = (int) tempX0;
            points[1].y = (int) tempY0;

            points[0].y = (int) tempY0;
            points[3].x = (int) tempX0;
        }
    }

    private void moveHandleLB(float diffX,float diffY){
        // 2 variable, 1 fixed
        float tempY0=0, tempX0=0;
        if(diffX > diffY) {
            tempX0 = points[2].x + diffX;;

            float dx = points[1].x - tempX0;
            float dy = dx / aspectRatio;

            tempY0 = points[1].y + dy;
        }else{
            // otherwise on vertical movement, crop area wont change size
            tempY0 = points[2].y + diffY;;

            float dy = points[1].y - tempY0;
            float dx = dy * aspectRatio;

            tempX0 = points[1].x + dx;
        }
        if(isInsideVertical(tempY0) && isInsideHorizontal(tempX0)) {
            // if new x0, y0 is inside, update the 3 points
            //0 x, 2 xy, ,3 y
            points[2].x = (int) tempX0;
            points[2].y = (int) tempY0;

            points[3].y = (int) tempY0;
            points[0].x = (int) tempX0;
        }
    }

    private void moveHandleRB(float diffX,float diffY){
// I think we are not gonna use the free crop, cz according to design, we have 1:1, 16:9 and 9:16 (or 4:3 , 3:4 whatever)
        // so I am only gonna use the ratio code
        // 3 variable 0 fixed
        float tempY0=0, tempX0=0;
        if(diffX > diffY) {
            tempX0 = points[3].x + diffX;;

            float dx = points[0].x - tempX0;
            float dy = dx / aspectRatio;

            tempY0 = points[0].y - dy;
        }else{
            // otherwise on vertical movement, crop area wont change size
            tempY0 = points[3].y + diffY;;

            float dy = points[0].y - tempY0;
            float dx = dy * aspectRatio;

            tempX0 = points[0].x - dx;
        }

        if(isInsideVertical(tempY0) && isInsideHorizontal(tempX0)) {
            // if new x0, y0 is inside, update the 3 points
            // 1 x, 2 y, 3 xy
            points[3].x = (int) tempX0;
            points[3].y = (int) tempY0;

            points[2].y = (int) tempY0;
            points[1].x = (int) tempX0;
        }

    }


    private boolean isInsideHorizontal(float x) {  return ((x >=viewFrameLeft) && (x <= viewFrameRight)); }

    private boolean isInsideVertical(float y) {  return ((y >= viewFrameTop) && (y <= viewFrameBottom)); }

    // onTouchActions /////////////////////////

    private void onUp(MotionEvent e) {
//        if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = false;
//        if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = false;
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    private void onCancel() {
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
        invalidate();
    }

    private void checkTouchArea(float x, float y) {
        if (isInsideCornerLeftTop(x, y)) {
            mTouchArea = TouchArea.LEFT_TOP;
//            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
//            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerRightTop(x, y)) {
            mTouchArea = TouchArea.RIGHT_TOP;
//            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
//            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerLeftBottom(x, y)) {
            mTouchArea = TouchArea.LEFT_BOTTOM;
//            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
//            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideCornerRightBottom(x, y)) {
            mTouchArea = TouchArea.RIGHT_BOTTOM;
//            if (mHandleShowMode == ShowMode.SHOW_ON_TOUCH) mShowHandle = true;
//            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            return;
        }
        if (isInsideFrame(x, y)) {
//            if (mGuideShowMode == ShowMode.SHOW_ON_TOUCH) mShowGuide = true;
            mTouchArea = TouchArea.CENTER;
            return;
        }
        mTouchArea = TouchArea.OUT_OF_BOUNDS;
    }

    private boolean isInsideFrame(float x, float y) {
        if (points[0].x <= x && points[1].x >= x) {
            if (points[0].y <= y && points[2].y >= y) {
                mTouchArea = TouchArea.CENTER;
                return true;
            }
        }
        return false;
    }

    private boolean isInsideCornerLeftTop(float x, float y) {
        float dx = x - points[0].x; // mFrameRect.left;
        float dy = y - points[0].y; //mFrameRect.top;
        float d = dx * dx + dy * dy;
        return d <= touchAreaRadiusSquared;
    }

    private boolean isInsideCornerRightTop(float x, float y) {
        float dx = x - points[1].x; // mFrameRect.right;
        float dy = y - points[1].y; // mFrameRect.top;
        float d = dx * dx + dy * dy;
        return  d <= touchAreaRadiusSquared;
    }

    private boolean isInsideCornerLeftBottom(float x, float y) {
        float dx = x - points[2].x; //mFrameRect.left;
        float dy = y - points[2].y; // mFrameRect.bottom;
        float d = dx * dx + dy * dy;
        return  d <= touchAreaRadiusSquared;
    }

    private boolean isInsideCornerRightBottom(float x, float y) {
        float dx = x - points[3].x; // mFrameRect.right;
        float dy = y - points[3].y; // mFrameRect.bottom;
        float d = dx * dx + dy * dy;
        return  d <= touchAreaRadiusSquared;
    }


    private float getDensity() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay()
                .getMetrics(displayMetrics);
        return displayMetrics.density;
    }

    public Point[] getPoints() {    return this.points; }

    public void setDrawCornerCircles(boolean drawCornerCircles) { this.drawCornerCircles = drawCornerCircles; }

}
