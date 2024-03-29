package com.example.kiddrawingapp

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent
import android.view.View

class DrawingView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private var mDrawPath : CustomPath? = null
    private var mCanvasBitmap : Bitmap? = null
    private var mDrawPaint : Paint? = null
    private var mCanvasPaint : Paint? = null
    private var mBrushSize : Float = 0.toFloat()
    private var color = Color.BLACK
    private var canvas : Canvas? = null
    private var mPaths = ArrayList<CustomPath>()
    private var mUndoPaths = ArrayList<CustomPath>()

    init {
        setUpDrawing()
    }

    fun onClickUndo() {
        if (mPaths.size > 0) {
            mUndoPaths.add(mPaths.removeAt(mPaths.size - 1))
            invalidate()
        }
    }

    private fun setUpDrawing() {
        mDrawPaint = Paint()
        mDrawPath = CustomPath(color,mBrushSize)
        mDrawPaint?.let {
            it.color = color
            it.style = Paint.Style.STROKE
            it.strokeJoin = Paint.Join.ROUND
            it.strokeCap = Paint.Cap.ROUND
        }
        mCanvasPaint = Paint(Paint.DITHER_FLAG)
//        mBrushSize = 20.toFloat() //Default size
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mCanvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        mCanvasBitmap?.let {
            canvas = Canvas(it)
        }

    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mCanvasBitmap?.let {
            canvas?.drawBitmap(it,0f,0f,mCanvasPaint)

            for (path in mPaths) {
                mDrawPaint!!.strokeWidth = path.brushThickness
                mDrawPaint!!.color = path.color
                canvas?.drawPath(path, mDrawPaint!!)
            }

            if  (mDrawPath != null &&  mDrawPaint != null) {

                if (!mDrawPath!!.isEmpty) {
                    mDrawPaint!!.strokeWidth = mDrawPath!!.brushThickness
                    mDrawPaint!!.color = mDrawPath!!.color
                    canvas?.drawPath(mDrawPath!!, mDrawPaint!!)
                }
            }

        }

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        val touchX = event?.x
        val touchY = event?.y

        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                mDrawPath!!.color = color
                mDrawPath!!.brushThickness = mBrushSize
                mDrawPath!!.reset()
                if (touchX != null && touchY != null) {
                    mDrawPath!!.moveTo(touchX!!,touchY!!)
                }

            }

            MotionEvent.ACTION_MOVE -> {
                if (touchX != null && touchY != null) {
                    mDrawPath!!.lineTo(touchX!!,touchY!!)
                }
            }

            MotionEvent.ACTION_UP -> {
                mDrawPath?.let {
                    mPaths.add(it)
                }
                mDrawPath = CustomPath(color, mBrushSize)
            }

            else ->  return  false

        }
        invalidate()
        return true

        return super.onTouchEvent(event)
    }

    fun setSizeForBrush(newSize : Float) {
        mBrushSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,newSize,resources.displayMetrics)

        mDrawPaint?.let {
            it.strokeWidth = mBrushSize
        }
    }

    fun setColor(newColor: String) {
        color = Color.parseColor(newColor)
        mDrawPaint?.let {
            it.color = color
        }
    }

    internal inner class CustomPath(var color: Int, var brushThickness: Float ) : Path() {

    }

}