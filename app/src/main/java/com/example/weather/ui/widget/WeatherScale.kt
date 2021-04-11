package com.example.weather.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.toRect
import com.example.weather.R

class WeatherScale: View {

    companion object {
        private const val DEFAULT_VIEW_WIDTH = 280f
        private const val DEFAULT_VIEW_HEIGHT = 17.3f
        private const val DEFAULT_LINE_HEIGHT = 5f
        private const val DEFAULT_TEXT_SIZE = 13f
    }

    private val indent = dpToPx(6.6f).toInt()
    private val cornerRadius = dpToPx(6.6f)

    /** background */
    private var contourColor = Color.GRAY
    private val contourPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.STROKE }

    private var backgroundScaleColor = Color.WHITE
    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var backgroundRect = RectF()

    /** line */
    private var isDrawableLineColors = false
    private var lineColorsId: Int = 0
    private var lineColors = intArrayOf(Color.WHITE, Color.BLACK)
    private var lineColorPosition: FloatArray = FloatArray(lineColors.size)

    private var lineHeight = dpToPx(DEFAULT_LINE_HEIGHT)
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var lineRect = RectF()
    private var lineBitmap: Bitmap? = null

    /** text */
    private var textSize: Float = spToPx(DEFAULT_TEXT_SIZE)
    private var textColor = Color.BLACK
    private var textFont: Typeface = Typeface.defaultFromStyle(Typeface.NORMAL)
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { textAlign = Paint.Align.CENTER }
    private val textBounds = Rect()
    private var textPositionY = 0f
    private val textPositionXList: ArrayList<Float> = ArrayList()

    private var markupText = arrayOf<CharSequence>("mm", "30", "50", "60")
    private var markupTextWidth = 0f
    private var markupTextHeight = 0f

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr) {

        if (attrs != null) {
            val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.WeatherScale,
                defStyleAttr, 0)

            /** background */
            contourColor = typedArray.getColor(R.styleable.WeatherScale_shadowColor, contourColor)
            backgroundScaleColor = typedArray.getColor(R.styleable.WeatherScale_backgroundColor,
                backgroundScaleColor)

            /** line */
            val scaleColorsPath = typedArray.getString(R.styleable.WeatherScale_scaleColors)
            isDrawableLineColors = scaleColorsPath != null && scaleColorsPath.startsWith("res/drawable")

            when(isDrawableLineColors) {
                true -> {
                    lineColorsId = typedArray
                        .getResourceId(R.styleable.WeatherScale_scaleColors, 0)
                    lineBitmap = ResourcesCompat
                        .getDrawable(context.resources, lineColorsId, null)!!.toBitmap()
                }

                else -> {
                    lineColors = getLineColors(typedArray
                        .getResourceId(R.styleable.WeatherScale_scaleColors, 0))
                    lineColorPosition = getColorsPosition(typedArray
                        .getResourceId(R.styleable.WeatherScale_colorPosition, 0), lineColors)
                }
            }
            lineHeight = typedArray.getDimension(R.styleable.WeatherScale_lineHeight, lineHeight)

            /** text */
            textSize = typedArray.getDimension(R.styleable.WeatherScale_textSize, textSize)
            textColor = typedArray.getColor(R.styleable.WeatherScale_textColor, textColor)
            textFont = getTextFont(typedArray.getResourceId(R.styleable.WeatherScale_android_fontFamily, 0))
            markupText = typedArray.getTextArray(R.styleable.WeatherScale_markupText) ?: markupText

            typedArray.recycle()
        }

        setupPaint()
        setupMarkupTextSize()
    }

    private fun dpToPx(dp: Float): Float = context.resources.displayMetrics.density * dp

    private fun spToPx(sp: Float): Float = context.resources.displayMetrics.scaledDensity * sp

    private fun getLineColors(scaleColorsId: Int): IntArray {
        return when(scaleColorsId) {
            0 -> lineColors
            else -> context.resources.getIntArray(scaleColorsId)
        }
    }

    private fun getColorsPosition(colorPositionId: Int, scaleColors: IntArray): FloatArray {
        var positionArray = FloatArray(scaleColors.size)

        when(colorPositionId) {
            0 -> {
                val stepPosition = 1.0f / (scaleColors.size - 1)
                var colorPosition = 0.0f

                for (i in scaleColors.indices) {
                    positionArray[i] = colorPosition
                    colorPosition += stepPosition
                }
            }

            else -> positionArray = context.resources
                .getStringArray(colorPositionId).map { it.toFloat() }.toFloatArray()
        }

        return positionArray
    }

    private fun getTextFont(textFontId: Int): Typeface {
        return when(textFontId) {
            0 -> Typeface.defaultFromStyle(Typeface.NORMAL)
            else -> ResourcesCompat.getFont(context, textFontId)!!
        }
    }

    private fun setupPaint() {
        with(contourPaint) {
            color = contourColor
        }

        with(backgroundPaint) {
            color = backgroundScaleColor
        }

        with(textPaint) {
            textSize = this@WeatherScale.textSize
            color = this@WeatherScale.textColor
            typeface = this@WeatherScale.textFont
        }
    }

    private fun setupMarkupTextSize() {
        for (i in markupText.indices) {
            textPaint.getTextBounds(markupText[i].toString(), 0, markupText[i].length, textBounds)
            markupTextWidth += textBounds.width() * 2

            if (textBounds.height() > markupTextHeight) {
                markupTextHeight = textBounds.height().toFloat()
                                   - (textPaint.descent() + textPaint.ascent()) / 2
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val viewWidth = when (MeasureSpec.getMode(widthMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(widthMeasureSpec).toFloat()

            MeasureSpec.AT_MOST -> resolveSize((markupTextWidth
                    + paddingLeft + paddingRight).toInt(), widthMeasureSpec).toFloat()

            else -> dpToPx(DEFAULT_VIEW_WIDTH)
        }

        val viewHeight = when (MeasureSpec.getMode(heightMeasureSpec)) {
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(heightMeasureSpec).toFloat()

            MeasureSpec.AT_MOST -> resolveSize((markupTextHeight + lineHeight
                    + paddingTop * 2 + paddingBottom * 2).toInt(),
                heightMeasureSpec).toFloat()

            else -> dpToPx(DEFAULT_VIEW_HEIGHT)
        }

        setMeasuredDimension(viewWidth.toInt(), viewHeight.toInt())
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        setupMarkupTextPosition(w, h)
        setupRect(w, h)
        setupPaintRequiringDimensions(w)
    }

    private fun setupMarkupTextPosition(w: Int, h: Int) {
        textPaint.getTextBounds(markupText[0].toString(), 0, markupText[0].length, textBounds)
        val markupTextStartPosition = textBounds.width() / 2f + paddingLeft

        textPaint.getTextBounds(markupText[markupText.size - 1].toString(), 0,
            markupText[markupText.size - 1].length, textBounds)
        val markupTextEndPosition = (textBounds.width() / 2f) + paddingRight

        var textPositionX = markupTextStartPosition + indent
        val stepPosition = (w - markupTextEndPosition - markupTextStartPosition - indent * 2) / (markupText.size - 1)

        for (element in markupText) {
            textPositionXList.add(textPositionX)
            textPositionX += stepPosition
        }

        textPositionY = (h - (h - textBounds.height())).toFloat() + paddingTop
    }

    private fun setupRect(w: Int, h: Int) {
        backgroundRect = RectF(0f, 0f, w.toFloat(), h.toFloat())

        lineRect = RectF(
            (indent + paddingLeft).toFloat(),
            ((textPositionY + textPaint.descent()).toInt() + paddingTop + paddingBottom).toFloat(),
            (w - indent - paddingRight).toFloat(),
            ((textPositionY + lineHeight + textPaint.descent()).toInt() + paddingTop + paddingBottom).toFloat()
        )
    }

    private fun setupPaintRequiringDimensions(w: Int) {
        with(linePaint) {
            shader = LinearGradient(0f, 0f, w.toFloat(), 0f,
                lineColors, lineColorPosition, Shader.TileMode.CLAMP)
        }
    }

    override fun onDraw(canvas: Canvas) {
        drawBackground(canvas)
        drawGradientLine(canvas)
        drawMarkupScale(canvas)
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline fun drawBackground(canvas: Canvas) {
        canvas.apply {
            drawRoundRect(backgroundRect, cornerRadius, cornerRadius, backgroundPaint)
            drawRoundRect(backgroundRect, cornerRadius, cornerRadius, contourPaint)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline  fun drawGradientLine(canvas: Canvas) {
        when(isDrawableLineColors) {
            true -> canvas.drawBitmap(lineBitmap!!, null, lineRect, null)
            else -> canvas.drawRoundRect(lineRect, cornerRadius, cornerRadius, linePaint)
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    private inline  fun drawMarkupScale(canvas: Canvas) {
        for (i in markupText.indices) {
            canvas.drawText(markupText[i], 0, markupText[i].length,
                textPositionXList[i], textPositionY, textPaint)
        }
    }
}