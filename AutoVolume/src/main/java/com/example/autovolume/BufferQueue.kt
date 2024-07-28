package com.example.autovolume

import java.util.LinkedList
import java.util.Queue

class BufferQueue(bufferSize: Int) {
    private val bufferQueue: Queue<Double> = LinkedList()
    private val bufferSize = bufferSize

    fun enqueueBuffer(buffer: Double) {
        bufferQueue.add(buffer)
    }

    fun dequeueBuffer(): Double? {
        return bufferQueue.remove()
    }

    fun isEmpty(): Boolean {
        return bufferQueue.isEmpty()
    }

    fun size(): Int {
        return bufferQueue.size
    }

    operator fun iterator(): Iterator<Double> {
        return bufferQueue.iterator()
    }

    fun getList(): List<Double> {
        return bufferQueue.toList()
    }
}
