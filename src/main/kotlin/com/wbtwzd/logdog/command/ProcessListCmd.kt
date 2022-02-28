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

import AppOptions

class ProcessListCmd(device: Device) : Command<List<Process>>() {

    override val commandArgs = listOf(AppOptions.adb, "-s", device.serialName, "shell", "ps -ef| grep -e ^u0")

    override suspend fun parseOutput(output: String): List<Process> {
        return output.split("\n").mapNotNull {
            val params = it.split(Regex("\\s+"))
            try {
                if (params.size >= 8) {
                    Process(params[2].toInt(), params[7])
                } else {
                    null
                }
            } catch (e: Exception) {
                null
            }
        }
    }

}

data class Process(val pid: Int, val name: String)