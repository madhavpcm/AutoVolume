package com.example.autovolume

import java.util.LinkedList
import java.util.Queue

class BufferQueue(bufferSize: Int) {
    private val bufferQueue: Queue<ShortArray> = LinkedList()
    private val bufferSize = bufferSize

    fun enqueueBuffer(buffer: ShortArray) {
        bufferQueue.add(buffer)
    }

    fun dequeueBuffer(): ShortArray? {
        return bufferQueue.remove()
    }

    fun isEmpty(): Boolean {
        return bufferQueue.isEmpty()
    }

    fun size(): Int {
        return bufferQueue.size
    }

    operator fun iterator(): Iterator<ShortArray> {
        return bufferQueue.iterator()
    }
}
