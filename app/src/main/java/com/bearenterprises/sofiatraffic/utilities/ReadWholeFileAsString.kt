package com.bearenterprises.sofiatraffic.utilities

import android.content.Context
import java.io.File

/**
 * Created by thalv on 07-Mar-18.
 */

fun readFileAsString(file: File): String{
    val json_string = file.bufferedReader().use{
        it.readText()
    }
    return json_string
}
