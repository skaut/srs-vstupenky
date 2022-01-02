package cz.skaut.srs.ticketsreader.processor

import android.content.Context

abstract class QrProcessor(
    protected val context: Context
) {
    abstract fun process(value: String)
}