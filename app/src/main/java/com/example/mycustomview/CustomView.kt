package com.example.mycustomview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.StaticLayout
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import androidx.core.graphics.withTranslation
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TEXT_SIZE = context.spToPx(12)
    private val COLUMN_WIDTH = context.dpToPx(6)

    private var _listOfValues:List<Int>? = emptyList()
    private val listOfValues:List<Int> get() = _listOfValues!!
    private var height = resources.getDimensionPixelSize(R.dimen.custom_view_height)
    private var width = resources.getDimensionPixelSize(R.dimen.custom_view_width)
    private var top = 100f
    private var lineColor = Color.BLACK
    private var dateColor = Color.WHITE

    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = TEXT_SIZE
    }
    private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = TEXT_SIZE
    }



    //private val animation = ValueAnimator.ofFloat(0f, 100f).apply {
    //    duration = 1000
    //    addUpdateListener {
    //        top = it.animatedValue as Float
    //        invalidate()
    //    }
    //}

    private val animation = ValueAnimator.ofFloat(0f, 100f).apply {
        duration = 1000
        addUpdateListener {
            top = (it.animatedValue as Float)
            invalidate()
        }

    }

    private val gestureDetector = GestureDetector(
        context,
        object: GestureDetector.OnGestureListener{
            override fun onDown(p0: MotionEvent): Boolean = false

            override fun onShowPress(p0: MotionEvent) {

            }

            override fun onSingleTapUp(p0: MotionEvent): Boolean = false

            override fun onScroll(
                p0: MotionEvent?,
                p1: MotionEvent,
                p2: Float,
                p3: Float
            ): Boolean = false

            override fun onLongPress(p0: MotionEvent) {
                animationStart()
            }

            override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean = false

        }
    )

    init {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CustomView,defStyleAttr,0)
        lineColor = typedArray.getColor(R.styleable.CustomView_line_color, lineColor)
        dateColor = typedArray.getColor(R.styleable.CustomView_date_text_color, dateColor)
        mainPaint.color = lineColor
        datePaint.color = dateColor
        typedArray.recycle()
    }

    fun setValues(values:List<Int>){
        _listOfValues = values
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        height = (MeasureSpec.getSize(widthMeasureSpec) / 2.4).toInt()
        width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        repeat(listOfValues.size) {
            val date = getDate(0)

            //val offset =
            //    ((width - COLUMN_WIDTH * listOfValues.size) / (listOfValues.size + 1)) * (listOfValues.size + 1)
            val offset = ((width-COLUMN_WIDTH*listOfValues.size)/(listOfValues.size+1))*(it+1)
            Log.d("offset", "offset $offset width $width")

            val progressTextStartPoint =
                findTextStartPoint(listOfValues[0].toString(), offset, mainPaint)
            val dateTextStartPoint = findTextStartPoint(date, offset, datePaint)

            /**
             * расчеты размеров столбца:
             *      находим цену деления столбца, учитывая отступы сверху и снизу (по 50 пикселей)
             *      задаем отсутп сверху
             *      находим верхнюю точку столца просчитыая разницу между нижней точкой и размером столбца (значение из массива * цену деления)
             */
            val columnOnePoint = (height - 100) / 100
            val bottom = height - 50f
            //top = bottom - columnOnePoint*listOfValues[0] height - 50f - columnOnePoint*listOfValues[0]
            /**
             * Отрисовка компонента
             */

            canvas.drawText(
                listOfValues[it].toString(),
                progressTextStartPoint,
                top - 10f,
                mainPaint
            )
            canvas.drawRoundRect(
                offset,
                top,
                offset + COLUMN_WIDTH,
                bottom,
                COLUMN_WIDTH,
                COLUMN_WIDTH,
                mainPaint
            )
            canvas.drawText(date, dateTextStartPoint, bottom + 30f, datePaint)
        }
    }

    //override fun onDraw(canvas: Canvas) {
    //    super.onDraw(canvas)
    //    repeat(listOfValues.size) {iterator ->
//
    //        val date = getDate(iterator)
    //        val dateWidth = datePaint.measureText(date)
    //        //val offset = ((width - COLUMN_WIDTH*listOfValues.size)/listOfValues.size)
//
    //        //val offset = (COLUMN_WIDTH + (dateWidth-COLUMN_WIDTH) + ((width-(dateWidth*listOfValues.size))/(listOfValues.size+1)))*(iterator+1)
    //        val offset = ((width-COLUMN_WIDTH*listOfValues.size)/(listOfValues.size+1))*(iterator+1)
    //        Log.d("offset", "offset $offset width $width")
    //        //Log.d("offset", "y = ${((width-(dateWidth*listOfValues.size))/listOfValues.size)}  " +
    //        //        "z = ${((dateWidth-COLUMN_WIDTH))} " +
    //        //        "c = $COLUMN_WIDTH offset = $offset width - $width date width - $dateWidth")
    //        //drawOneItem(
    //        //    percent = listOfValues[iterator],
    //        //    start = offset,
    //        //    canvas = canvas,
    //        //    date = date
    //        //)
//
    //        val progressTextStartPoint = findTextStartPoint(listOfValues[iterator].toString(), offset, mainPaint)
    //        val dateTextStartPoint = findTextStartPoint(date, offset, datePaint)
//
    //        /**
    //         * расчеты размеров столбца:
    //         *      находим цену деления столбца, учитывая отступы сверху и снизу (по 50 пикселей)
    //         *      задаем отсутп сверху
    //         *      находим верхнюю точку столца просчитыая разницу между нижней точкой и размером столбца (значение из массива * цену деления)
    //         */
//
    //        val columnOnePoint = (height-100)/100
    //        val bottom = height-50f
    //        top = bottom - columnOnePoint*listOfValues[iterator]
//
//
    //        /**
    //         * Отрисовка компонента
    //         */
    //        //val startPoint =
    //        canvas.drawText(listOfValues[iterator].toString(),progressTextStartPoint, top-10f ,mainPaint)
    //        canvas.drawRoundRect(offset,top, offset+COLUMN_WIDTH,bottom,COLUMN_WIDTH,COLUMN_WIDTH, mainPaint )
    //        canvas.drawText(date,dateTextStartPoint, bottom+30f ,datePaint)
//
    //    }
    //}

    private fun drawOneItem(
        percent:Int, // высота столбика
        start:Float,
        canvas: Canvas,
        date:String
    ){

        val progressTextStartPoint = findTextStartPoint(percent.toString(), start, mainPaint)
        val dateTextStartPoint = findTextStartPoint(date, start, datePaint)

        /**
         * расчеты размеров столбца:
         *      находим цену деления столбца, учитывая отступы сверху и снизу (по 50 пикселей)
         *      задаем отсутп сверху
         *      находим верхнюю точку столца просчитыая разницу между нижней точкой и размером столбца (значение из массива * цену деления)
         */

        val columnOnePoint = (height-100)/100
        val bottom = height-50f
        top = bottom - columnOnePoint*percent


        /**
         * Отрисовка компонента
         */
        //val startPoint =
        canvas.drawText(percent.toString(),progressTextStartPoint, top-10f ,mainPaint)
        canvas.drawRoundRect(start,top, start+COLUMN_WIDTH,bottom,COLUMN_WIDTH,COLUMN_WIDTH, mainPaint )
        canvas.drawText(date,dateTextStartPoint, bottom+30f ,datePaint)
    }

    private fun animationStart(){
        val animation = ValueAnimator.ofFloat(height-50f, 100f).apply {
            duration = 1000
            addUpdateListener {
                top = it.animatedValue as Float
                invalidate()
            }
        }
        animation.start()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when{
            gestureDetector.onTouchEvent(event) -> true
            event.action == MotionEvent.ACTION_UP -> {
                animationStart()
                true
            }
            else -> false
        }
    }

    /**
     * функции хелперы для расчетов
     */
    private fun Context.dpToPx(dp:Int):Float =
        dp.toFloat() * this.resources.displayMetrics.density


    private fun Context.spToPx(sp:Int):Float =
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), this.resources.displayMetrics)

    private fun StaticLayout.draw(canvas: Canvas, x:Float, y:Float){
        canvas.withTranslation(x,y) {
            draw(this)
        }
    }

    /**
     * расчеты размеров текста:
     *      находим ширину текста
     *      вычитаем из стартовой точки разницу ширины текста и ширины столбца разделенную пополам
     */
    private fun findTextStartPoint(
        text:String,
        startColumnPoint: Float,
        paint: Paint
    ): Float = startColumnPoint - (paint.measureText(text)-COLUMN_WIDTH)/2

    /**
     * Вычисление даты:
     *      переворачиваем дату
     */
    private fun getDate(
        dateCounter: Int
    ):String{
        val simpleDateFormat = SimpleDateFormat("dd.MM", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val reverseCounter = dateCounter - listOfValues.size
        calendar.add(Calendar.DATE, reverseCounter)
        return simpleDateFormat.format(calendar.time)
    }
}