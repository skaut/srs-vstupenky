package cz.skaut.srs.ticketsreader.processor

import android.content.Context
import cz.skaut.srs.ticketsreader.Preferences

abstract class QrProcessor(
    protected val context: Context
) {
    abstract fun process(value: String)
}