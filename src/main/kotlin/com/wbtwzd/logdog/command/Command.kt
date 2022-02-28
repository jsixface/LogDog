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

package com.wbtwzd.logdog.command

import com.wbtwzd.logdog.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext

abstract class Command<T> {

    protected abstract val commandArgs: List<String>

    protected val log = Log(this.javaClass.simpleName)

    protected abstract suspend fun parseOutput(output: String): T?

    suspend fun execute(): T? = withContext(Dispatchers.IO) {
        val process = ProcessBuilder(commandArgs).start()
        val outputStream = process.inputStream

        val outBytes = mutableListOf<Byte>()
        log.i("Command: $commandArgs")
        try {
            do {
                delay(100)
                val avail = outputStream.available()
                if (avail > 0) {
                    outBytes.addAll(outputStream.readNBytes(avail).toTypedArray())
                }
            } while (isActive && process.isAlive)
            outputStream.close()
            log.i("read ${outBytes.size} bytes")
            parseOutput(String(outBytes.toByteArray()).trim())
        } catch (e: CancellationException) {
            log.e("Cannot execute", e)
            process.destroy()
            null
        }
    }

}