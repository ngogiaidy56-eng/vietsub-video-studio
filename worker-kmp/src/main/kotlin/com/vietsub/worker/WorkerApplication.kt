package com.vietsub.worker

import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val worker = FFmpegWorker(WorkerConfig.fromEnvironment())
    try { worker.runForever() } finally { worker.close() }
}
