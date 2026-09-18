package com.anjo.anjosite

object Log {
    fun info(tag: String, message: String) = console.log("[$tag] $message")
    fun warn(tag: String, message: String, cause: Throwable? = null) =
        if (cause != null) console.warn("[$tag] $message", cause) else console.warn("[$tag] $message")
    fun error(tag: String, message: String, cause: Throwable? = null) =
        if (cause != null) console.error("[$tag] $message", cause) else console.error("[$tag] $message")
}

class FetchFailedException(url: String, status: Int) : Exception("Fetch failed for $url: HTTP $status")
