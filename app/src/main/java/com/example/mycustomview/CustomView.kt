package com.example.mycustomview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.StaticLayout
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.View
import androidx.core.graphics.withTranslation

class CustomView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val TEXT_SIZE = context.spToPx(12)
    private val COLUMN_WIDTH = context.dpToPx(4)

    private var _listOfValues:List<Int>? = emptyList()
    private val listOfValues:List<Int> get() = _listOfValues!!
    private var height = resources.getDimensionPixelSize(R.dimen.custom_view_height)
    private var width = resources.getDimensionPixelSize(R.dimen.custom_view_width)

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = TEXT_SIZE
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
        repeat(listOfValues.size) {iterator ->
            val notEmptyField = width - COLUMN_WIDTH*9
            val offset = (width - COLUMN_WIDTH*9)/listOfValues.size
            Log.d("offset", "offset = ${offset*(iterator+1)} width = $width not empty field - $notEmptyField")
            drawOneItem(percent = listOfValues[iterator], start = offset*(iterator+1), canvas = canvas)
        }
    }

    private fun drawOneItem(
        percent:Int, // высота столбика
        start:Float,
        canvas: Canvas
    ){
        /**
         * расчеты размеров текста:
         *      находим ширину текста
         *      вычитаем из стартовой точки разницу ширины текста и ширины столбца разделенную пополам
         */

        val measText = paint.measureText(percent.toString())
        val startPointForText = start - (measText-COLUMN_WIDTH)/2

        /**
         * расчеты размеров столбца:
         *      находим цену деления столбца, учитывая отступы сверху и снизу (по 50 пикселей)
         *      задаем отсутп сверху
         *      находим верхнюю точку столца просчитыая разницу между нижней точкой и размером столбца (значение из массива * цену деления)
         */

        val columnOnePoint = (height-100)/100
        val bottom = height-50f
        val top = bottom - columnOnePoint*percent

        /**
         * Отрисовка компонента
         */
        canvas.drawText(percent.toString(),startPointForText, top-10f ,paint)
        canvas.drawRoundRect(start,top, start+COLUMN_WIDTH,bottom,COLUMN_WIDTH,COLUMN_WIDTH, paint )

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

}