package com.lxw.splashview

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ImageView

/**
 * <pre>
 *     author : lxw
 *     e-mail : lsw@tairunmh.com
 *     time   : 2017/09/18
 *     desc   :
 * </pre>
 */
class SplashView : ImageView {
    public var valueAnimator: ValueAnimator? = null
    //当前大圆旋转角度(弧度)
    private var mCurrentRotationAngle = 0f
    private var mCircleColors: IntArray? = null
    // 整体的背景颜色
    private val mSplashBgColor = Color.WHITE
    // 大圆(里面包含很多小圆的)的半径
    private val mRotationRadius = 90f
    // 每一个小圆的半径
    private val mCircleRadius = 18f
    // 绘制圆的画笔
    private val mPaint = Paint()
    // 绘制背景的画笔
    private val mPaintBackground = Paint()

    // 屏幕正中心点坐标
    private var mCenterX: Float = 0.toFloat()
    private var mCenterY: Float = 0.toFloat()
    //当前大圆的半径
    private var mCurrentRotationRadius = mRotationRadius
    // 大圆和小圆旋转的时间
    private val mRotationDuration: Long = 1200 //ms
    private var diagonal: Float = 0f
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCenterX = (width / 2).toFloat();
        mCenterY = (height / 2).toFloat();
        //对角线的一半
        diagonal = (Math.sqrt((width * width + height * height).toDouble()) / 2).toFloat()
    }

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        setImageResource(R.mipmap.content);
        init(context, attrs)
    }

    fun init(context: Context?, attrs: AttributeSet?) {
        mCircleColors = resources.getIntArray(R.array.splash_circle_colors)
        mPaint.isAntiAlias = true
        mPaintBackground.isAntiAlias = true
        mPaint.style = Paint.Style.FILL
        mPaintBackground.style = Paint.Style.STROKE
        mPaintBackground.color=Color.WHITE
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        if (splashState == null) {
            splashState = RotationState()
        }
        splashState!!.onDrawState(canvas)
    }

    var splashState: SplashState? = null
    private fun drawBackground(canvas: Canvas) {
        if (hold > 0f) {
            var stokeWidth =diagonal-hold;
            mPaintBackground.strokeWidth=stokeWidth
            val radius = hold + stokeWidth / 2

            canvas.drawCircle(mCenterX,mCenterY,radius,mPaintBackground)

        } else {
            canvas.drawColor(Color.WHITE)
        }

    }

    private fun drawCircles(canvas: Canvas) {
        //每个小圆之间的间隔角度 = 2π/小圆的个数
        val rotationAngle = (2 * Math.PI / mCircleColors!!.size).toFloat()
        for (i in mCircleColors!!.indices) {
            /**
             * x = r*cos(a) +centerX
             * y=  r*sin(a) + centerY
             * 每个小圆i*间隔角度 + 旋转的角度 = 当前小圆的真是角度
             */
            val angle = (i * rotationAngle + mCurrentRotationAngle).toDouble()
            val cx = (mCurrentRotationRadius * Math.cos(angle) + mCenterX).toFloat()
            val cy = (mCurrentRotationRadius * Math.sin(angle) + mCenterY).toFloat()
            mPaint.color = mCircleColors!![i]
            canvas.drawCircle(cx, cy, mCircleRadius, mPaint)
        }
    }

    public abstract class SplashState {
        abstract fun onDrawState(canvas: Canvas?);
    }

    public inner class RotationState : SplashState() {

        init {
            valueAnimator = ValueAnimator.ofFloat(0f, Math.PI.toFloat() * 2);
            valueAnimator!!.addUpdateListener { valueAnimator ->
                mCurrentRotationAngle = valueAnimator.getAnimatedValue() as Float
                invalidate()
            }
            valueAnimator!!.duration = mRotationDuration;
            valueAnimator!!.interpolator = LinearInterpolator()
            valueAnimator!!.repeatCount = ValueAnimator.INFINITE
            valueAnimator!!.start()
        }

        fun cancel() {
            valueAnimator!!.cancel()
        }

        override fun onDrawState(canvas: Canvas?) {
            drawBackground(canvas!!)
            drawCircles(canvas!!)

        }
    }

    public inner class MergingState : SplashState() {
        init {
            valueAnimator = ValueAnimator.ofFloat(0f, mRotationRadius)
            valueAnimator!!.duration = mRotationDuration
            valueAnimator!!.interpolator = OvershootInterpolator(10f)
            valueAnimator!!.addUpdateListener(ValueAnimator.AnimatorUpdateListener {
                var1 ->
                mCurrentRotationRadius = var1.getAnimatedValue() as Float
                invalidate()
            })
            valueAnimator!!.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    super.onAnimationEnd(animation)
                    splashState = ExpandState()
                }
            })
            //   valueAnimator!!.repeatCount=ValueAnimator.INFINITE
            valueAnimator!!.reverse()
        }

        override fun onDrawState(canvas: Canvas?) {
            drawBackground(canvas!!)
            drawCircles(canvas)
        }
    }
    //空心圆的半径
    private var hold: Float = 0f

    private inner class ExpandState : SplashState() {
        init {
            valueAnimator = ValueAnimator.ofFloat(0f, diagonal)
            valueAnimator!!.duration = mRotationDuration
            valueAnimator!!.addUpdateListener {
                var1 ->
                hold = var1.getAnimatedValue() as Float
                invalidate()
            }
            valueAnimator!!.start()
        }

        override fun onDrawState(canvas: Canvas?) {
            drawBackground(canvas!!)
        }
    }

    fun disapper() {
        if (splashState != null && splashState is RotationState) {
            val rotateState = splashState as RotationState?
            rotateState!!.cancel()
            splashState = MergingState()
        }
    }
}