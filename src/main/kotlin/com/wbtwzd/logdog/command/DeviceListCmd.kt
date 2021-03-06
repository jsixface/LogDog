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

class DeviceListCmd : Command<List<Device>>() {

    override val commandArgs = listOf(AppOptions.adb, "devices")

    override suspend fun parseOutput(output: String): List<Device> {
        log.d("Device List: $output")
        return output.split("\n")
            .filterNot { it.trim().isEmpty() }
            .filterNot { it.startsWith("*") }
            .filterNot { it.startsWith("List of devices") }
            .mapNotNull { it.split(Regex("\\s+")).firstOrNull() }
            .map {
                val name = DeviceNameCmd(it).execute()
                Device(name, it)
            }
            .toList()
    }
}

data class Device(val name: String?, val serialName: String)