package com.mshohrat.speedometer;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.OvershootInterpolator;

import com.mshohrat.speedometer.utils.PersianHelper;
/**
 * The type Speedometer.
 */
public class Speedometer extends View {

    /**
     * The constant DEFAULT_MAX_SPEED.
     */
    public static final float DEFAULT_MAX_SPEED = 200; // Assuming this is km/h and you drive a super-car

	// Speedometer internal state
	private float mMaxSpeed;
	private float mCurrentSpeed;
	private float mOldSpeed = 0;
	private float SPEED_LIMIT = 60;
	private int ANIMATION_DURATION = 2000;
	private float ANIMATION_DUR_FRACTION;
	private float SCALE_SIZE;
	private float SPEED_READING_SIZE;
	private float UNIT_READING_SIZE;
	private float OFF_MARK_HEIGHT;
	private float OFF_MARK_WIDTH;
	private float ON_MARK_HEIGHT;
	private float SHADOW_LAYER_OFFSET1;
	private float SHADOW_LAYER_OFFSET2;
	private float SCALE_WIDTH;
	private boolean SHOW_SCALE = false;


	// Scale drawing tools
	private Paint onMarkPaint;
	private Paint offMarkPaint;
	private Paint scalePaint;
	private Paint SpeedReadingPaint;
	private Paint UnitReadingPaint;
	private Path onPath;
	private Path offPath;
    /**
     * The Oval.
     */
    final RectF oval = new RectF();
	
	// Drawing colors
	private int LOW_ON_COLOR = Color.argb(169,124,252,0);
	private int MEDIUM_ON_COLOR = Color.argb(169,255,165,110);
	private int HIGH_ON_COLOR = Color.argb(169,255,70,20);
	private int OFF_COLOR = Color.rgb(105,105,105);
	private int BACKGROUND_COLOR = Color.rgb(60,60,60);
	private int SPEED_COLOR = Color.WHITE;
	private int UNIT_COLOR = Color.WHITE;
	private int SCALE_COLOR = Color.argb(255, 255, 255, 255);
	private int SPEED_LIMIT_BACKGROUND_COLOR = Color.argb(169,255,255,255);
	private int SPEED_LIMIT_COLOR = Color.argb(169,255,70,20);
	private int SPEED_LIMIT_READING_COLOR = Color.argb(169,0,0,0);

	//Speedometer degrees
	private int START_DEGREE = -225;
	private int END_DEGREE = 45;

	// Scale configuration
	private float centerX;
	private float centerY;
	private float mainRadius;

    /**
     * Instantiates a new Speedometer.
     *
     * @param context the context
     */
    public Speedometer(Context context){
		super(context);
	}

    /**
     * Instantiates a new Speedometer.
     *
     * @param context the context
     * @param attrs   the attrs
     */
    public Speedometer(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.getTheme().obtainStyledAttributes(attrs,
				R.styleable.Speedometer,
				0, 0);
		try{
			mMaxSpeed = a.getFloat(R.styleable.Speedometer_maxSpeed,DEFAULT_MAX_SPEED);
			mCurrentSpeed = a.getFloat(R.styleable.Speedometer_currentSpeed,0);
			LOW_ON_COLOR = a.getColor(R.styleable.Speedometer_lowOnColor, LOW_ON_COLOR);
			MEDIUM_ON_COLOR = a.getColor(R.styleable.Speedometer_mediumOnColor,MEDIUM_ON_COLOR);
			HIGH_ON_COLOR = a.getColor(R.styleable.Speedometer_highOnColor,HIGH_ON_COLOR);
			SPEED_READING_SIZE = a.getDimension(R.styleable.Speedometer_speedTextSize,SPEED_READING_SIZE);
			UNIT_READING_SIZE = a.getDimension(R.styleable.Speedometer_unitTextSize,UNIT_READING_SIZE);
			OFF_COLOR = a.getColor(R.styleable.Speedometer_offColor, OFF_COLOR);
			SCALE_COLOR = a.getColor(R.styleable.Speedometer_scaleColor, SCALE_COLOR);
			SCALE_SIZE = a.getDimension(R.styleable.Speedometer_scaleTextSize, SCALE_SIZE);
			BACKGROUND_COLOR = a.getColor(R.styleable.Speedometer_backgroundColor,BACKGROUND_COLOR);
			START_DEGREE = a.getInt(R.styleable.Speedometer_startDegree,START_DEGREE);
			END_DEGREE = a.getInt(R.styleable.Speedometer_endDegree,END_DEGREE);
			SPEED_COLOR = a.getColor(R.styleable.Speedometer_speedTextColor,SPEED_COLOR);
			UNIT_COLOR = a.getColor(R.styleable.Speedometer_unitTextColor,UNIT_COLOR);
			SHOW_SCALE = a.getBoolean(R.styleable.Speedometer_showScale,SHOW_SCALE);
			ANIMATION_DURATION = a.getInt(R.styleable.Speedometer_animDuration,ANIMATION_DURATION);
			SPEED_LIMIT = a.getFloat(R.styleable.Speedometer_speedLimit,SPEED_LIMIT);
			SPEED_LIMIT_BACKGROUND_COLOR = a.getColor(R.styleable.Speedometer_speedLimitBackground,SPEED_LIMIT_BACKGROUND_COLOR);
			SPEED_LIMIT_COLOR = a.getColor(R.styleable.Speedometer_speedLimitColor,SPEED_LIMIT_COLOR);
			SPEED_LIMIT_READING_COLOR = a.getColor(R.styleable.Speedometer_sppedLimitTextColor,SPEED_LIMIT_READING_COLOR);
		} finally{
			a.recycle();
		}

	}

	/**
	 * initialize drawing tools
	 */
	private void initDrawingTools(){
		onMarkPaint = new Paint();
		onMarkPaint.setStyle(Paint.Style.STROKE);
		onMarkPaint.setColor(LOW_ON_COLOR);
		onMarkPaint.setStrokeWidth(ON_MARK_HEIGHT);
		onMarkPaint.setShadowLayer(SHADOW_LAYER_OFFSET1, 0f, 0f, LOW_ON_COLOR);
		onMarkPaint.setAntiAlias(true);
		
		offMarkPaint = new Paint(onMarkPaint);
		offMarkPaint.setStrokeWidth(OFF_MARK_HEIGHT);
		offMarkPaint.setColor(OFF_COLOR);
		offMarkPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		offMarkPaint.setShadowLayer(0f, 0f, 0f, OFF_COLOR);
		
		scalePaint = new Paint(offMarkPaint);
		scalePaint.setStrokeWidth(SCALE_WIDTH);
		scalePaint.setTextSize(SCALE_SIZE);
		scalePaint.setShadowLayer(SHADOW_LAYER_OFFSET1, 0f, 0f, Color.RED);
		scalePaint.setColor(SCALE_COLOR);
		
		SpeedReadingPaint = new Paint(scalePaint);
		SpeedReadingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		offMarkPaint.setShadowLayer(SHADOW_LAYER_OFFSET2, 0f, 0f, SPEED_COLOR);
		SpeedReadingPaint.setTextSize(SPEED_READING_SIZE);
		SpeedReadingPaint.setTypeface(Typeface.SANS_SERIF);
		SpeedReadingPaint.setColor(SPEED_COLOR);

		UnitReadingPaint = new Paint(scalePaint);
		UnitReadingPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		offMarkPaint.setShadowLayer(SHADOW_LAYER_OFFSET2, 0f, 0f, UNIT_COLOR);
		UnitReadingPaint.setTextSize(UNIT_READING_SIZE);
		UnitReadingPaint.setTypeface(Typeface.SANS_SERIF);
		UnitReadingPaint.setColor(UNIT_COLOR);
		
		onPath = new Path();
		offPath = new Path();
	}

    /**
     * Gets current speed.
     *
     * @return the current speed
     */
    public float getCurrentSpeed() {
		return mCurrentSpeed;
	}

	/**
	 * calculate dimens of elements depend on view width and height
	 */
	private void calculateDimens(){
		if(Float.compare(SCALE_SIZE,0f)==0f){
			SCALE_SIZE = getMeasuredWidth()/18;
		}
		if(Float.compare(SPEED_READING_SIZE,0f)==0f){
			SPEED_READING_SIZE = getMeasuredWidth()/5;
		}
		if(Float.compare(UNIT_READING_SIZE,0f)==0f){
			UNIT_READING_SIZE = getMeasuredWidth()/12;
		}

		OFF_MARK_HEIGHT = getMeasuredWidth()/12;
		OFF_MARK_WIDTH = 4f;
		SHADOW_LAYER_OFFSET1 = getMeasuredWidth()/84;
		SHADOW_LAYER_OFFSET2 = getMeasuredWidth()/90;
		SCALE_WIDTH = getMeasuredWidth()/210;
		ON_MARK_HEIGHT = getMeasuredWidth()/20;

		ANIMATION_DUR_FRACTION = ANIMATION_DURATION/mMaxSpeed;
	}

    /**
     * Sets current speed.
     *
     * @param mCurrentSpeed the m current speed
     */
    public void setCurrentSpeed(float mCurrentSpeed) {
		if(mCurrentSpeed > this.mMaxSpeed)
			this.mCurrentSpeed = mMaxSpeed;
		else if(mCurrentSpeed < 0)
			this.mCurrentSpeed = 0;
		else
			this.mCurrentSpeed = mCurrentSpeed;
	}

	/**
	 * Being called when size of view to be changed
	 * @param width new width
	 * @param height new height
	 * @param oldw old width
	 * @param oldh old height
	 */
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {

		// Setting up the oval area in which the arc will be drawn
		if (width > height){
			mainRadius = (height*2)/7;
		}else{
			mainRadius = (width*2)/7;
		}
		oval.set(centerX - mainRadius,
				centerY - mainRadius,
				centerX + mainRadius,
				centerY + mainRadius);

	}

	/**
	 * calls before draw
	 * @param widthMeasureSpec
	 * @param heightMeasureSpec
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);

		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		int chosenWidth = chooseDimension(widthMode, widthSize);
		int chosenHeight = chooseDimension(heightMode, heightSize);

		int chosenDimension = Math.min(chosenWidth, chosenHeight);
		centerX = chosenDimension / 2;
		centerY = chosenDimension / 2;
		setMeasuredDimension(chosenDimension, chosenDimension);
	}

	private int chooseDimension(int mode, int size) {
		if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
			return size;
		} else {
			return getPreferredSize();
		}
	}
	
	// in case there is no size specified
	private int getPreferredSize() {
		return 300;
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);

	}

	/**
	 * drawing view elements
	 * @param canvas canvas
	 */
	@Override
	public void onDraw(Canvas canvas){

		drawScaleBackground(canvas);
		drawScale(canvas);
		if(SHOW_SCALE){
			drawLegend(canvas);
		}

		drawReading(canvas);

		if(mCurrentSpeed>SPEED_LIMIT){
			drawLimitation(canvas);
		}
	}

    /**
     * Animate view elements
     */
    private void Animate() {
		//animating speed change
		ValueAnimator mainValueAnimator = ValueAnimator.ofFloat(getOldSpeed(),getCurrentSpeed());
		mainValueAnimator.setDuration((long)(ANIMATION_DUR_FRACTION*mCurrentSpeed));
		mainValueAnimator.setInterpolator(new DecelerateInterpolator());
		mainValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				setCurrentSpeed((float)animation.getAnimatedValue());
				invalidate();
			}
		});

		mainValueAnimator.start();

		//animating speed limit view
		ValueAnimator limitOnValueAnimator = ValueAnimator.ofInt(0,169);
		limitOnValueAnimator.setDuration(3000);
		limitOnValueAnimator.setInterpolator(new OvershootInterpolator());
		limitOnValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				SPEED_LIMIT_BACKGROUND_COLOR = Color.argb((int)animation.getAnimatedValue(),255,255,255);
				SPEED_LIMIT_COLOR = Color.argb((int)animation.getAnimatedValue(),255,70,20);
				SPEED_LIMIT_READING_COLOR = Color.argb((int)animation.getAnimatedValue(),0,0,0);
				invalidate();
			}
		});

		if(mOldSpeed<SPEED_LIMIT){
			limitOnValueAnimator.start();
		}

	}

	/**
	 * Draws the segments in their OFF state
	 * @param canvas canvas
	 */
	private void drawScaleBackground(Canvas canvas){
		//draw scales of background
		calculateDimens();
		initDrawingTools();
		drawBackGround(canvas);
		offPath.reset();
		for(int i = START_DEGREE; i < END_DEGREE-2; i+=4){
			offPath.addArc(oval, i, 1.5f);
		}
		canvas.drawPath(offPath, offMarkPaint);
	}

	/**
	 * draws speed with arc
	 * @param canvas canvas
	 */
	private void drawScale(Canvas canvas) {
		//draw scales of speed
		onPath.reset();

		if (mCurrentSpeed <= SPEED_LIMIT) {
			onMarkPaint.setColor(LOW_ON_COLOR);
		}else {
			onMarkPaint.setColor(HIGH_ON_COLOR);
		}

		for (int i = START_DEGREE; i < (mCurrentSpeed / mMaxSpeed) * (END_DEGREE - START_DEGREE - 2) + START_DEGREE; i += 4) {
			onPath.addArc(oval, i, OFF_MARK_WIDTH);
		}

			canvas.drawPath(onPath, onMarkPaint);

	}


	/**
	 * draws circle background of view
	 * @param canvas canvas
	 */
	private void drawBackGround(Canvas canvas){
		//draw main circle background
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		paint.setColor(BACKGROUND_COLOR);
		canvas.drawCircle(centerX,centerY,mainRadius+(OFF_MARK_HEIGHT),paint);

	}

	/**
	 * draws scales in numbers
	 * @param canvas canvas
	 */
	private void drawLegend(Canvas canvas){
		//draw scale in text
		canvas.save(Canvas.MATRIX_SAVE_FLAG);
		canvas.rotate(START_DEGREE, centerX,centerY);
		Path circle = new Path();
		double halfCircumference = mainRadius * Math.PI*3/2;
		double increments = 20;
		for(int i = -3; i < this.mMaxSpeed-3; i += increments){
			circle.addCircle(centerX, centerY, mainRadius, Path.Direction.CW);
			canvas.drawTextOnPath(String.format("%d", i+3),
								circle, 
								(float) (i*halfCircumference/this.mMaxSpeed),
								-getMeasuredWidth()/15,
								scalePaint);
		}
		canvas.restore();
	}

	/**
	 * draws speed and unit text
	 * @param canvas canvas
	 */
	private void drawReading(Canvas canvas){

		//draw speed text
		Path speedPath = new Path();
		String message = String.format("%d", (int)this.mCurrentSpeed);
		message = PersianHelper.toEnglishNumber(message);
		float[] speedWidths = new float[message.length()];
		SpeedReadingPaint.getTextWidths(message, speedWidths);

		float speedAdvance = 0;
		for(double width:speedWidths)
			speedAdvance += width;
		speedPath.moveTo(centerX - speedAdvance/2, centerY);
		speedPath.lineTo(centerX + speedAdvance/2, centerY);
		if(this.mCurrentSpeed>SPEED_LIMIT){
			SpeedReadingPaint.setColor(HIGH_ON_COLOR);
		}
		canvas.drawTextOnPath(message, speedPath, 0f, getMeasuredWidth()/18, SpeedReadingPaint);

		//draw unit text
		Path unitPath = new Path();
		float[] unitWidths = new float[4];
		UnitReadingPaint.getTextWidths("km/h", unitWidths);
		float unitAdvance = 0;
		for(double width:unitWidths)
			unitAdvance += width;
		unitPath.moveTo(centerX - unitAdvance/2, centerY);
		unitPath.lineTo(centerX + unitAdvance/2, centerY);
		canvas.drawTextOnPath("KM/h", unitPath, 0f, getMeasuredWidth()/6, UnitReadingPaint);

	}

	/**
	 * draws limitation view if current speed be more from speed limit
	 * @param canvas canvas
	 */
	private void drawLimitation(Canvas canvas){

		//draw background of limit view
		Paint backPaint = new Paint();
		backPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		backPaint.setColor(SPEED_LIMIT_BACKGROUND_COLOR);
		canvas.drawCircle(centerX+(mainRadius*5/6),centerY-(mainRadius*7/8),(mainRadius*5/8)+(mainRadius*3/40),backPaint);
		Paint frontPaint = new Paint();
		frontPaint.setStyle(Paint.Style.STROKE);
		frontPaint.setColor(SPEED_LIMIT_COLOR);
		frontPaint.setStrokeWidth(mainRadius*3/20);
		canvas.drawCircle(centerX+(mainRadius*5/6),centerY-(mainRadius*7/8),mainRadius*5/8,frontPaint);

		//draw text for speed limitation
		Path speedLimitPath = new Path();
		String message = String.format("%d", (int)this.SPEED_LIMIT);
		message = PersianHelper.toEnglishNumber(message);
		float[] speedWidths = new float[message.length()];
		frontPaint.setStyle(Paint.Style.FILL);
		frontPaint.setTextSize(SPEED_READING_SIZE*5/6);
		frontPaint.setTypeface(Typeface.SANS_SERIF);
		frontPaint.setFakeBoldText(true);
		frontPaint.getTextWidths(message, speedWidths);

		float speedLimitAdvance = 0;
		for(double width:speedWidths)
			speedLimitAdvance += width;
		speedLimitPath.moveTo(centerX +(mainRadius*5/6)- speedLimitAdvance/2, centerY-(mainRadius*7/8));
		speedLimitPath.lineTo(centerX +(mainRadius*5/6)+ speedLimitAdvance/2, centerY-(mainRadius*7/8));
		frontPaint.setColor(SPEED_LIMIT_READING_COLOR);
		canvas.drawTextOnPath(message, speedLimitPath, 0f, SHADOW_LAYER_OFFSET1*5, frontPaint);
	}

    /**
     * update speed to current speed and update view
     *
     * @param newSpeedValue the new speed value
     */
    public void speedTo(float newSpeedValue) {

		//change speed to current speed
		this.setOldSpeed(getCurrentSpeed());
		this.setCurrentSpeed(newSpeedValue);
		Animate();
	}

    /**
     * Sets max speed.
     *
     * @param mMaxSpeed the max speed
     */
    public void setmMaxSpeed(float mMaxSpeed) {
		this.mMaxSpeed = mMaxSpeed;
	}

    /**
     * Sets current speed.
     *
     * @param mCurrentSpeed the current speed
     */
    public void setmCurrentSpeed(float mCurrentSpeed) {
		this.mCurrentSpeed = mCurrentSpeed;
	}

    /**
     * Sets scale size.
     *
     * @param SCALE_SIZE the scale size
     */
    public void setScaleSize(float SCALE_SIZE) {
		this.SCALE_SIZE = SCALE_SIZE;
	}

    /**
     * Sets speed text size.
     *
     * @param SPEED_READING_SIZE the speed text size
     */
    public void setSpeedReadingSize(float SPEED_READING_SIZE) {
		this.SPEED_READING_SIZE = SPEED_READING_SIZE;
	}

    /**
     * Sets unit text size.
     *
     * @param UNIT_READING_SIZE the unit text size
     */
    public void setUnitReadingSize(float UNIT_READING_SIZE) {
		this.UNIT_READING_SIZE = UNIT_READING_SIZE;
	}

    /**
     * Sets show scale.
     *
     * @param SHOW_SCALE the show scale
     */
    public void setShowScale(boolean SHOW_SCALE) {
		this.SHOW_SCALE = SHOW_SCALE;
	}

    /**
     * Sets low on color.
     *
     * @param LOW_ON_COLOR the low speed color
     */
    public void setLowOnColor(int LOW_ON_COLOR) {
		this.LOW_ON_COLOR = LOW_ON_COLOR;
	}

    /**
     * Sets medium on color.
     *
     * @param MEDIUM_ON_COLOR the medium speed color
     */
    public void setMediumOnColor(int MEDIUM_ON_COLOR) {
		this.MEDIUM_ON_COLOR = MEDIUM_ON_COLOR;
	}

    /**
     * Sets high on color.
     *
     * @param HIGH_ON_COLOR the high speed color
     */
    public void setHighOnColor(int HIGH_ON_COLOR) {
		this.HIGH_ON_COLOR = HIGH_ON_COLOR;
	}

    /**
     * Sets off color.
     *
     * @param OFF_COLOR the off scale color
     */
    public void setOffColor(int OFF_COLOR) {
		this.OFF_COLOR = OFF_COLOR;
	}

    /**
     * Sets back ground color.
     *
     * @param BACKGROUND_COLOR the background color
     */
    public void setBackGroundColor(int BACKGROUND_COLOR) {
		this.BACKGROUND_COLOR = BACKGROUND_COLOR;
	}

    /**
     * Sets speed color.
     *
     * @param SPEED_COLOR the speed color
     */
    public void setSpeedColor(int SPEED_COLOR) {
		this.SPEED_COLOR = SPEED_COLOR;
	}

    /**
     * Sets unit text color.
     *
     * @param UNIT_COLOR the unit text color
     */
    public void setUnitColor(int UNIT_COLOR) {
		this.UNIT_COLOR = UNIT_COLOR;
	}

    /**
     * Sets scale color.
     *
     * @param SCALE_COLOR the scale color
     */
    public void setScaleColor(int SCALE_COLOR) {
		this.SCALE_COLOR = SCALE_COLOR;
	}

    /**
     * Sets start degree.
     *
     * @param START_DEGREE the start degree
     */
    public void setStartDegree(int START_DEGREE) {
		this.START_DEGREE = START_DEGREE;
	}

    /**
     * Sets end degree.
     *
     * @param END_DEGREE the end degree
     */
    public void setEndDegree(int END_DEGREE) {
		this.END_DEGREE = END_DEGREE;
	}

    /**
     * Sets old speed.
     *
     * @param mOldSpeed the old speed
     */
    public void setOldSpeed(float mOldSpeed) {
		this.mOldSpeed = mOldSpeed;
	}

    /**
     * Gets old speed.
     *
     * @return the old speed
     */
    public float getOldSpeed() {
		return mOldSpeed;
	}

    /**
     * Gets speed limit.
     *
     * @return the speed limit
     */
    public float getSpeedLimit() {
		return SPEED_LIMIT;
	}

    /**
     * Sets speed limit.
     *
     * @param SPEED_LIMIT the speed limit
     */
    public void setSpeedLimit(float SPEED_LIMIT) {
		this.SPEED_LIMIT = SPEED_LIMIT;
	}

    /**
     * Gets speed limit background color.
     *
     * @return the speed limit background color
     */
    public int getSpeedLimitBackgroundColor() {
		return SPEED_LIMIT_BACKGROUND_COLOR;
	}

    /**
     * Sets speed limit background color.
     *
     * @param SPEED_LIMIT_BACKGROUND_COLOR the speed limit background color
     */
    public void setSpeedLimitBackgroundColor(int SPEED_LIMIT_BACKGROUND_COLOR) {
		this.SPEED_LIMIT_BACKGROUND_COLOR = SPEED_LIMIT_BACKGROUND_COLOR;
	}

    /**
     * Gets speed limit color.
     *
     * @return the speed limit color
     */
    public int getSpeedLimitColor() {
		return SPEED_LIMIT_COLOR;
	}

    /**
     * Sets speed limit color.
     *
     * @param SPEED_LIMIT_COLOR the speed limit color
     */
    public void setSpeedLimitColor(int SPEED_LIMIT_COLOR) {
		this.SPEED_LIMIT_COLOR = SPEED_LIMIT_COLOR;
	}

    /**
     * Gets speed limit text color.
     *
     * @return the speed limit text color
     */
    public int getSpeedLimitTextColor() {
		return SPEED_LIMIT_READING_COLOR;
	}

    /**
     * Sets speed limit text color.
     *
     * @param SPEED_LIMIT_READING_COLOR the speed limit reading color
     */
    public void setSpeedLimitTextColor(int SPEED_LIMIT_READING_COLOR) {
		this.SPEED_LIMIT_READING_COLOR = SPEED_LIMIT_READING_COLOR;
	}
}
