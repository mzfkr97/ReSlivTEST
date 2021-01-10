package com.example.reslivtest.util

import android.content.Context
import android.graphics.Color


class ColorMaker(
    val context: Context
) {

    /*
     возвращает случайный цвет из массива
    holder.text_dot.setTextColor(new ColorMaker(mContext).getRandomMaterialColor("400" ));
    */
    fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = context.resources
            .getIdentifier("mdcolor_$typeColor", "array", context.packageName)
        if (arrayId != 0) {
            val colors = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

}