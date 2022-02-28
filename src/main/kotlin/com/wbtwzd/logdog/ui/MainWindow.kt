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

package com.wbtwzd.logdog.ui

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wbtwzd.logdog.app.AppState
import com.wbtwzd.logdog.command.Device
import com.wbtwzd.logdog.command.Process
import com.wbtwzd.logdog.command.DeviceListCmd
import com.wbtwzd.logdog.command.ProcessListCmd
import com.wbtwzd.logdog.util.Log
import kotlinx.coroutines.launch

@Composable
@Preview
fun App() {

    val appState = remember { AppState() }

    MaterialTheme {
        Column(modifier = Modifier.padding(10.dp)) {
            ToolBar(appState.selectedDevice, appState.selectedProcess)

        }
    }
}

@Preview
@Composable
fun ToolBar(selectedDevice: MutableState<Device?>, selectedProcess: MutableState<Process?>) {

    var devices: List<Device> by remember { mutableStateOf(emptyList()) }
    var processes: List<Process> by remember { mutableStateOf(emptyList()) }
    val localScope = rememberCoroutineScope()
    localScope.launch {
        DeviceListCmd().execute()?.let {
            devices = it
//            if (it.isNotEmpty()) selectedDevice.value = it[0]
        }
    }

    Surface(
        shape = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth().height(200.dp),
        color = MaterialTheme.colors.secondary
    ) {
        Column(
            modifier = Modifier.padding(25.dp).fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                DropDownSelect(
                    devices, selected = selectedDevice,
                    itemLabelBuilder = { it?.name ?: it?.serialName ?: "Select a device" },
                    modifier = Modifier.width(250.dp).padding(5.dp),
                    onSelect = { dev ->
                        dev?.let {
                            localScope.launch {
                                ProcessListCmd(it).execute()?.let { pro ->
                                    log.i("processes = $pro")
                                    processes = pro.sortedBy { it.name }
                                }
                            }
                        }
                    }
                )
                DropDownSelect(
                    processes, selected = selectedProcess,
                    itemLabelBuilder = { it?.name ?: "Select a Process" },
                    modifier = Modifier.width(250.dp).padding(5.dp)
                )
            }
        }
    }
}

private val log = Log("Main Layout")