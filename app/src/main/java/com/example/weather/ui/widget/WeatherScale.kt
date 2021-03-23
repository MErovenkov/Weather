package com.example.weather.ui.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.weather.R

class WeatherScale(context: Context, attributeSet: AttributeSet): View(context, attributeSet) {

    companion object {
        private const val DEFAULT_SHADOW_COLOR = Color.GRAY
        private const val DEFAULT_SCALE_BACKGROUND_COLOR = Color.WHITE

        private val DEFAULT_SCALE_COLORS = intArrayOf(Color.WHITE, Color.BLACK)

        private const val DEFAULT_TEXT_COLOR = Color.BLACK
        private val DEFAULT_TEXT_FONT = Typeface.defaultFromStyle(Typeface.NORMAL)
        private val DEFAULT_MARKUP_TEXT = arrayOf<CharSequence>("mm", "30", "50", "60")
    }

    private val paint = Paint()
    private var layoutWidth = 0f
    private var layoutHeight = 0f
    private var indent = 20f

    /** background */
    private var shadowColor = DEFAULT_SHADOW_COLOR
    private var backgroundScaleColor = DEFAULT_SCALE_BACKGROUND_COLOR

    /** gradient */
    private var isDrawableScaleColors = false
    private var scaleColorsId: Int = 0
    private var scaleColors = DEFAULT_SCALE_COLORS
    private var colorPosition: FloatArray = FloatArray(DEFAULT_SCALE_COLORS.size)

    /** text */
    private var textSize: Float = 0f
    private var textColor = DEFAULT_TEXT_COLOR
    private var textFont: Typeface = DEFAULT_TEXT_FONT
    private var markupText = DEFAULT_MARKUP_TEXT

    init {
        setupAttributes(attributeSet)
    }

    private fun setupAttributes(attributeSet: AttributeSet) {
        val typedArray = context.theme.obtainStyledAttributes(attributeSet, R.styleable.WeatherScale,
            0, 0)

        /** background */
        shadowColor = typedArray.getColor(R.styleable.WeatherScale_shadowColor, DEFAULT_SHADOW_COLOR)
        backgroundScaleColor = typedArray.getColor(R.styleable.WeatherScale_backgroundColor,
            DEFAULT_SCALE_BACKGROUND_COLOR)

        /** gradient */
        val scaleColorsPath = typedArray.getString(R.styleable.WeatherScale_scaleColors)
        isDrawableScaleColors = scaleColorsPath != null && scaleColorsPath.startsWith("res/drawable")

        when(isDrawableScaleColors) {
            true -> scaleColorsId = typedArray
                .getResourceId(R.styleable.WeatherScale_scaleColors, 0)

            else -> {
                scaleColors = getScaleColors(typedArray
                    .getResourceId(R.styleable.WeatherScale_scaleColors, 0))
                colorPosition = getColorPosition(typedArray
                    .getResourceId(R.styleable.WeatherScale_colorPosition, 0), scaleColors)
            }
        }

        /** text */
        textSize = typedArray.getDimension(R.styleable.WeatherScale_textSize, 0f)
        textColor = typedArray.getColor(R.styleable.WeatherScale_textColor, DEFAULT_TEXT_COLOR)
        textFont = getTextFont(typedArray.getResourceId(R.styleable.WeatherScale_textFont, 0))
        markupText = typedArray.getTextArray(R.styleable.WeatherScale_markupText) ?: DEFAULT_MARKUP_TEXT

        typedArray.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        layoutWidth = measuredWidth.toFloat()
        layoutHeight = measuredHeight.toFloat()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawBackground(canvas)
        drawGradientLine(canvas)
        drawMarkupScale(canvas)
    }

    /** background */
    private fun drawBackground(canvas: Canvas) {
        val cornerRadius = 20f
        var rect = RectF(0f, 0f, layoutWidth, layoutHeight)

        paint.color = shadowColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

        rect = RectF(1f, 0f, layoutWidth - 1f, layoutHeight - 5f)
        paint.color = backgroundScaleColor
        canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
    }

    /** gradient */
    private fun drawGradientLine(canvas: Canvas) {
        val rect = RectF(indent,layoutHeight * 0.75f,
            layoutWidth - indent, layoutHeight * 0.9f)

        if (scaleColorsId != 0 && isDrawableScaleColors) {
            canvas.drawBitmap(ResourcesCompat
                .getDrawable(context.resources, scaleColorsId, null)!!.toBitmap(),
                null, rect, null)
        } else {
            paint.shader = LinearGradient(0f, 0f, layoutWidth, 0f,
                scaleColors, colorPosition, Shader.TileMode.CLAMP)

            canvas.drawRect(rect, paint)
        }
    }

    private fun getScaleColors(scaleColorsId: Int): IntArray {
        return when(scaleColorsId) {
            0 -> DEFAULT_SCALE_COLORS
            else -> context.resources.getIntArray(scaleColorsId)
        }
    }

    private fun getColorPosition(colorPositionId: Int, scaleColors: IntArray): FloatArray {
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

    /** text */
    private fun drawMarkupScale(canvas: Canvas) {
        val textPositionY = layoutHeight * 0.5f

        when(textSize) {
            0f -> textSize = layoutWidth * 0.05f + layoutHeight * 0.025f
        }

        val paint = Paint().apply {
            flags = Paint.ANTI_ALIAS_FLAG
            textSize = this@WeatherScale.textSize
            color = this@WeatherScale.textColor
            typeface = this@WeatherScale.textFont
        }

        val stepPosition = (layoutWidth + indent) / markupText.size
        var textPositionX = indent

        for (element in markupText) {
            canvas.drawText(element, 0, element.length, textPositionX, textPositionY, paint)
            textPositionX += stepPosition
        }
    }

    private fun getTextFont(textFontId: Int): Typeface {
        return when(textFontId) {
            0 -> DEFAULT_TEXT_FONT
            else -> ResourcesCompat.getFont(context, textFontId)!!
        }
    }
}