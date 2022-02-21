/*
 * Copyright (c) 2022-2022,  Arumugam Jeganathan
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.wbtwzd.logdog.util

import java.time.LocalDateTime

class Log(clazz: Any) {
    private val className = (clazz as? String) ?: clazz.javaClass.simpleName

    private fun log(tag: String, message: String, throwable: Throwable?) {
        val time = LocalDateTime.now()
        val printMsg =
            throwable?.let { "$message. ${it.javaClass.simpleName}(${it.message}) ${it.stackTraceToString()}" }
                ?: message
        println("$time $tag $className - $printMsg")
    }

    fun e(msg: Any, throwable: Throwable? = null) = log("ERROR", msg.toString(), throwable)

    fun i(msg: Any, throwable: Throwable? = null) {
        if (logLevel != "error") log("INFO", msg.toString(), throwable)
    }

    fun d(msg: Any, throwable: Throwable? = null) {
        if (logLevel == "debug") {
            log("DEBUG", msg.toString(), throwable)
        }
    }

    companion object {
        var logLevel = "info"
    }
}