package com.tubbert.powdroid.collections

/**
 *
 */
class ForeverIterator<T>(private val listToIterate: List<T>): Iterator<T> {

    var cursorIndex: Int = 0

    override fun hasNext(): Boolean = listToIterate.isNotEmpty()

    override fun next(): T {
        if(!hasNext()) {
            throw IndexOutOfBoundsException("Cannot interate over empty List")
        }
        cursorIndex++

        if(cursorIndex >= listToIterate.size) {
            cursorIndex = 0
        }

        return listToIterate[cursorIndex]
    }
}
