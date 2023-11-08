package com.example.mycustomview

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.util.TypedValue
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    /**
     * дефолтные значения для цветов и размеров шрифтов
     */
    private var TEXT_SIZE = context.spToPx(12)
    private var COLUMN_WIDTH = context.dpToPx(4)
    private var LINE_COLOR = Color.BLACK
    private var DATE_COLOR = Color.WHITE

    private var _listOfValues:MutableList<Int>? = emptyList<Int>().toMutableList()
    private val listOfValues:List<Int> get() = _listOfValues!!

    /**
     * дефолтные размеры View
     */
    private var height = 0
    private var width  = 0

    /**
     * массив для анимации столбцов
     */
    private val listOfTopPoints: MutableList<Float> = emptyList<Float>().toMutableList()


    private val mainPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = TEXT_SIZE
    }
    private val datePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = TEXT_SIZE
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
        LINE_COLOR = typedArray.getColor(R.styleable.CustomView_line_color, LINE_COLOR)
        DATE_COLOR = typedArray.getColor(R.styleable.CustomView_date_text_color, DATE_COLOR)
        mainPaint.color = LINE_COLOR
        datePaint.color = DATE_COLOR
        if (typedArray.getDimension(R.styleable.CustomView_line_weight, COLUMN_WIDTH) != 0f){
            COLUMN_WIDTH = typedArray.getDimension(R.styleable.CustomView_line_weight, COLUMN_WIDTH)
        }
        if (typedArray.getDimension(R.styleable.CustomView_values_text_size, TEXT_SIZE) != 0f){
            mainPaint.textSize = typedArray.getDimension(R.styleable.CustomView_values_text_size, TEXT_SIZE)
        }
        if (typedArray.getDimension(R.styleable.CustomView_dates_text_size, TEXT_SIZE) != 0f){
            datePaint.textSize = typedArray.getDimension(R.styleable.CustomView_dates_text_size, TEXT_SIZE)
        }
        typedArray.recycle()
    }

    fun setData(data:List<Int>){
        _listOfValues = data.toMutableList()

    }

    private fun setTopPoints() {
        listOfTopPoints.clear()
        repeat(listOfValues.size){iterator->
            /**
             * расчеты размеров столбца:
             *      находим цену деления столбца, учитывая отступы сверху и снизу (по 50 пикселей)
             *      задаем отсутп сверху
             *      находим верхнюю точку столца просчитыая разницу между нижней точкой и размером столбца (значение из массива * цену деления)
             */
            listOfTopPoints.add((height-50f) - ((height-100)/100)*listOfValues[iterator])
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        height = (MeasureSpec.getSize(widthMeasureSpec) / 2.4).toInt()
        width = MeasureSpec.getSize(widthMeasureSpec)
        setMeasuredDimension(width, height)
        setTopPoints()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        repeat(listOfValues.size) {iterator ->

            val date = getDate(iterator)

            /**
             * Обсчет отступа столбцов друг от друга
             * ((все пр-во - полезное пр-во) / кол-во пробелов) * счетчик + длинна столбцов отрисованных
             */
            val offset = (width-COLUMN_WIDTH*listOfValues.size)/(listOfValues.size+1)*(iterator+1) + COLUMN_WIDTH*iterator

            drawOneItem(
                percent = listOfValues[iterator],
                start = offset,
                canvas = canvas,
                date = date,
                topPoint = listOfTopPoints[iterator]
            )

        }
    }

    private fun drawOneItem(
        percent:Int, // высота столбика
        start:Float,
        canvas: Canvas,
        date:String,
        topPoint: Float
    ){

        val progressTextStartPoint = findTextStartPoint(percent.toString(), start, mainPaint)
        val dateTextStartPoint = findTextStartPoint(date, start, datePaint)

        val bottom = height-50f

        /**
         * Отрисовка компонента
         */
        canvas.drawText(percent.toString(),progressTextStartPoint, topPoint-10f ,mainPaint)
        canvas.drawRoundRect(start,topPoint, start+COLUMN_WIDTH,bottom,COLUMN_WIDTH,COLUMN_WIDTH, mainPaint )
        canvas.drawText(date,dateTextStartPoint, bottom+30f ,datePaint)
    }

    fun animationStart(){
        repeat(listOfTopPoints.size) {iterator ->

            val topPoint = listOfTopPoints[iterator]
            val topRate = listOfValues[iterator]
            val animationColumns = ValueAnimator.ofFloat(height - 50f, topPoint).apply {
                duration = 1500
                addUpdateListener {
                    listOfTopPoints[iterator] = it.animatedValue as Float
                    invalidate()
                }
            }
            val animationRates = ValueAnimator.ofInt(0, topRate).apply {
                duration = 1500
                addUpdateListener {
                    _listOfValues?.set(iterator, it.animatedValue as Int)
                    invalidate()
                }
            }
            animationColumns.start()
            animationRates.start()
        }
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