package com.muhammetkonukcu.examples.views

import android.content.Context
import android.graphics.Color
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.AttributeSet
import android.view.View
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.muhammetkonukcu.examples.R

/***
 * @author MuhammetKonukcu
 * createdAt 5.02.2025
 */

class ShowMoreTextView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : AppCompatTextView(context, attrs) {

    private var originalText: CharSequence? = null
    private var originalBufferType: BufferType? = null
    private var isCollapsed = true

    private var maxLength: Int
    private var expandIndicator: CharSequence
    private val clickSpan: ShowMoreClickableSpan

    private var defaultClickableColor: Int
    private var collapsedTextColor: Int? = null
    private var expandedTextColor: Int? = null

    private var trimMode: Int
    private var maxLinesVisible: Int
    private var lastLineEndIndex = 0

    init {
        val ta = context.obtainStyledAttributes(attrs, R.styleable.ShowMoreTextView)
        maxLength = ta.getInt(R.styleable.ShowMoreTextView_trimLength, DEFAULT_TRIM_LENGTH)
        val indicatorRes = ta.getResourceId(
            R.styleable.ShowMoreTextView_trimCollapsedText,
            R.string.show_more_with_dots
        )
        expandIndicator = resources.getString(indicatorRes)
        maxLinesVisible = ta.getInt(R.styleable.ShowMoreTextView_trimLines, DEFAULT_TRIM_LINES)
        defaultClickableColor = ta.getColor(
            R.styleable.ShowMoreTextView_colorClickableText,
            ContextCompat.getColor(context, R.color.blue_300)
        )
        trimMode = ta.getInt(R.styleable.ShowMoreTextView_trimMode, TRIM_MODE_LINES)
        ta.recycle()

        clickSpan = ShowMoreClickableSpan()
        listenForLayout()
        applyDisplayText()
    }

    private fun applyDisplayText() {
        super.setText(displayText, originalBufferType)
        movementMethod = LinkMovementMethod.getInstance()
        highlightColor = Color.TRANSPARENT
    }

    private val displayText: CharSequence?
        get() = computeTrimmedText(originalText)

    override fun setText(text: CharSequence, type: BufferType) {
        originalText = text
        originalBufferType = type
        applyDisplayText()
    }

    private fun computeTrimmedText(text: CharSequence?): CharSequence? {
        text ?: return null

        when (trimMode) {
            TRIM_MODE_LENGTH -> {
                if (text.length > maxLength) {
                    return buildCollapsedText()
                }
            }
            TRIM_MODE_LINES -> {
                if (lastLineEndIndex > 0 && layout.lineCount > maxLinesVisible && isCollapsed) {
                    collapsedTextColor?.let { applyClickableColor(it) }
                    return buildCollapsedText()
                }
            }
        }
        return text
    }

    private fun buildCollapsedText(): CharSequence {
        val end = when (trimMode) {
            TRIM_MODE_LINES -> {
                val idx = lastLineEndIndex - (expandIndicator.length + 3)
                if (idx < 0) maxLength + 1 else idx
            }
            else -> maxLength + 1
        }
        val sb = SpannableStringBuilder(originalText, 0, end).append(expandIndicator)
        return attachClickableSpan(sb, expandIndicator)
    }

    private fun attachClickableSpan(
        sb: SpannableStringBuilder,
        indicator: CharSequence
    ): CharSequence {
        sb.setSpan(
            clickSpan,
            sb.length - indicator.length,
            sb.length,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        return sb
    }

    fun setTrimLength(length: Int) {
        maxLength = length
        applyDisplayText()
    }

    fun setCollapsedTextColor(color: Int) {
        collapsedTextColor = color
    }

    fun setExpandedTextColor(color: Int) {
        expandedTextColor = color
    }

    fun setExpandIndicator(indicator: CharSequence) {
        expandIndicator = indicator
    }

    fun setTrimMode(mode: Int) {
        trimMode = mode
    }

    fun setTrimLines(lines: Int) {
        maxLinesVisible = lines
    }

    private fun applyClickableColor(colorRes: Int) {
        try {
            defaultClickableColor = ContextCompat.getColor(context, colorRes)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private inner class ShowMoreClickableSpan : ClickableSpan() {
        override fun onClick(widget: View) {
            isCollapsed = !isCollapsed
            applyDisplayText()
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.color = defaultClickableColor
        }
    }

    private fun listenForLayout() {
        if (trimMode == TRIM_MODE_LINES) {
            viewTreeObserver.addOnGlobalLayoutListener(object : OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    viewTreeObserver.removeOnGlobalLayoutListener(this)
                    calculateLastLineEnd()
                    applyDisplayText()
                }
            })
        }
    }

    private fun calculateLastLineEnd() {
        try {
            lastLineEndIndex = when {
                maxLinesVisible == 0 -> layout.getLineEnd(0)
                maxLinesVisible <= layout.lineCount -> layout.getLineEnd(maxLinesVisible - 1)
                else -> INVALID_END_INDEX
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private const val TRIM_MODE_LINES = 0
        private const val TRIM_MODE_LENGTH = 1
        private const val DEFAULT_TRIM_LENGTH = 240
        private const val DEFAULT_TRIM_LINES = 2
        private const val INVALID_END_INDEX = -1
    }
}

