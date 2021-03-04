package com.example.weather.ui.theme

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.AttrRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class LayoutInflaterFactory2 (private val delegate: AppCompatDelegate): LayoutInflater.Factory2 {

    companion object {
        private const val NAME_SPACE_RES_ANDROID = "http://schemas.android.com/apk/res/android"
        private const val NAME_SPACE_RES_AUTO = "http://schemas.android.com/apk/res-auto"

        private const val TEXT_COLOR = "textColor"
        const val TEXT_COLOR_TAG_KEY = 100000000

        private const val BACKGROUND = "background"
        const val BACKGROUND_TAG_KEY = 100000001

        private const val BACKGROUND_TINT = "backgroundTint"
        const val BACKGROUND_TINT_TAG_KEY = 100000002

        private const val TEXT_COLOR_HINT = "textColorHint"
        const val TEXT_COLOR_HINT_TAG_KEY = 100000003

        private const val TINT = "tint"
        const val TINT_TAG_KEY = 100000004
    }

    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return onCreateView(null, name, context, attrs)
    }

    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet
        ): View? {

        val view: View? = when(name) {
            "androidx.swiperefreshlayout.widget.SwipeRefreshLayout" -> SwipeRefreshLayout(context, attrs)
            "View" -> View(context, attrs)
            else -> delegate.createView(parent, name, context, attrs)
        }

        if (view != null) {
            /** textColor */
            setTag(getResAttrId(attrs, TEXT_COLOR), view, TEXT_COLOR_TAG_KEY)

            /** background */
            setTag(getResAttrId(attrs, BACKGROUND), view, BACKGROUND_TAG_KEY)

            /** backgroundTint */
            setTag(getResAttrId(attrs, BACKGROUND_TINT), view, BACKGROUND_TINT_TAG_KEY)

            /** textColorHint */
            setTag(getResAttrId(attrs, TEXT_COLOR_HINT), view, TEXT_COLOR_HINT_TAG_KEY)

            /** tint */
            setTag(getResAutoAttrId(attrs, TINT), view, TINT_TAG_KEY)
        }

        return view
    }

    private fun setTag(attrId: Int, view: View, tagKay: Int) {
        if (attrId != 0) {
            view.setTag(tagKay, attrId)
            view.tag = tagKay
        }
    }

    @AttrRes
    private fun getResAttrId(attrs: AttributeSet, propertyName: String): Int {
        return getAttId(attrs, NAME_SPACE_RES_ANDROID, propertyName)
    }

    @AttrRes
    private fun getResAutoAttrId(attrs: AttributeSet, propertyName: String): Int {
        return getAttId(attrs, NAME_SPACE_RES_AUTO, propertyName)
    }

    @AttrRes
    private fun getAttId(attrs: AttributeSet, nameSpace: String, propertyName: String): Int {
        val s = attrs.getAttributeValue(nameSpace, propertyName)
        return if (s != null && s.startsWith("?")) {
            s.replace("?", "").toInt()
        } else 0
    }
}