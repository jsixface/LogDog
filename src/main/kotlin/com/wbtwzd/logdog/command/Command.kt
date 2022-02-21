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

abstract class Command<T>(protected val adb: String) {
    abstract suspend fun execute(): T

    protected val log = Log(this.javaClass.simpleName)

    protected suspend fun runCommand(vararg cmd: String) = withContext(Dispatchers.IO) {
        val process = ProcessBuilder(*cmd).start()
        val outputStream = process.inputStream

        val outBytes = mutableListOf<Byte>()
        log.i("Command: ${cmd.toList()}")
        try {
            while (isActive && process.isAlive) {
                val avail = outputStream.available()
                if (avail > 0) {
                    outBytes.addAll(outputStream.readNBytes(avail).toTypedArray())
                }
                log.i("read $avail bytes")
                delay(100)
            }
            outBytes.addAll(outputStream.readAllBytes().toTypedArray())
            outputStream.close()
            String(outBytes.toByteArray()).trim()
        } catch (e: CancellationException) {
            process.destroy()
            ""
        }
    }

}