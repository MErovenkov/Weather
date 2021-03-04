package com.example.weather.ui.theme

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.weather.R

class ThemeManager(private val context: Context): IThemeManager {

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val THEME_KEY = "current_theme"
        private const val THEME_UNDEFINED = -1
    }

    private val theme: Resources.Theme = context.theme
    private val sharedPrefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private fun saveTheme(theme: Int) {
        context.setTheme(theme)
        sharedPrefs.edit().putInt(THEME_KEY, theme).apply()
    }

    override fun getSavedTheme(): Int = sharedPrefs.getInt(THEME_KEY, THEME_UNDEFINED)

    override fun setTheme(view: View) {
        when (getSavedTheme()) {
            R.style.LightTheme -> saveTheme(R.style.DarkTheme)
            R.style.DarkTheme -> saveTheme(R.style.LightTheme)
            else -> saveTheme(R.style.DarkTheme)
        }
        changedTheme(view)
    }

    private fun changedTheme(view: View) {
        for (child in getAllChild(view)) {
            if (child is RecyclerView) {
                child.adapter = child.adapter
            } else repaintingView(child)
        }
    }

    private fun repaintingView(view: View) {
        val textColorId: Int? = view.getTag(LayoutInflaterFactory2.TEXT_COLOR_TAG_KEY) as Int?
        val backgroundId: Int? = view.getTag(LayoutInflaterFactory2.BACKGROUND_TAG_KEY) as Int?
        val textColorHintId: Int? = view.getTag(LayoutInflaterFactory2.TEXT_COLOR_HINT_TAG_KEY) as Int?
        val backgroundTintId: Int? = view.getTag(LayoutInflaterFactory2.BACKGROUND_TINT_TAG_KEY) as Int?
        val tintId: Int? = view.getTag(LayoutInflaterFactory2.TINT_TAG_KEY) as Int?

        if (view.transitionName == "separation_line" && backgroundId != null) {
            view.background = ResourcesCompat.getDrawable(theme.resources,
                    getTypedValue(backgroundId).resourceId, theme)
        }

        when(view) {
            is EditText -> {
                if (backgroundTintId != null) {
                    ViewCompat.setBackgroundTintList(
                        view,
                        ColorStateList.valueOf(getTypedValue(backgroundTintId).data)
                    )
                }
                if (textColorHintId != null) {
                    view.setHintTextColor(getTypedValue(textColorHintId).data)
                }
            }

            is TextView -> {
                if (textColorId != null) {
                    view.setTextColor(getTypedValue(textColorId).data)
                }
            }

            is SwipeRefreshLayout -> {
                if (backgroundId != null) {
                    view.background = ResourcesCompat.getDrawable(theme.resources,
                            getTypedValue(backgroundId).resourceId, theme)
                }
            }

            is ImageView -> {
                if (tintId != null) {
                    view.setColorFilter(getTypedValue(tintId).data)
                }
            }
        }
    }

    private fun getTypedValue(attrId: Int): TypedValue {
        val typedValue = TypedValue()
        theme.resolveAttribute(attrId, typedValue, true)
        return typedValue
    }

    private fun getAllChild(v: View): MutableList<View> {
        val filteredViewList: MutableList<View> = ArrayList()
        val allViewList: MutableList<View> = arrayListOf(v)

        while (allViewList.isNotEmpty()) {
            val view = allViewList.removeAt(0)

            if (view.tag != null) filteredViewList.add(view)
            if (view !is ViewGroup) continue

            for (i in 0 until view.childCount) allViewList.add(view.getChildAt(i))
        }
        return filteredViewList
    }
}