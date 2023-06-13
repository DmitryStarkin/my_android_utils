/*
 * Copyright (c) 2022. Dmitry Starkin Contacts: t0506803080@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the «License»);
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  //www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an «AS IS» BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.starsoft.myandroidutil.collectionUtils


/**
 * Created by Dmitry Starkin on 24.05.2022 20:34.
 */

fun <T> Array<T>.getNext(item: T): T{

    forEachIndexed { index, currentItem ->
        if(currentItem == item){
            return if(index == lastIndex){
                this[0]
            } else {
                this[index + 1]
            }
        }
    }
    return this[0]
}

fun <T> listOfCollections(vararg elements: Collection<T>): List<T> = if (elements.isNotEmpty()) {
    ArrayList<T>().apply {
        elements.forEach {
            addAll(it)
        }
    }.toList()
} else {emptyList()}


fun <T> List<T>.removeLast(): List<T> =
    if(isEmpty()){
        this
    } else {
        (this as MutableList<T>).removeAt(this.lastIndex)
        this
    }

fun <T> List<T>.removeFirst(): List<T> =
    if(isEmpty()){
        this
    } else {
        (this as MutableList<T>).removeAt(0)
        this
    }

fun <T> List<T>.remove(item: T): List<T> =
    if(isEmpty()){
        this
    } else {
        val newList = ArrayList<T>().also {
            it.addAll(this)
        }
        newList.remove(item)
        newList
    }

fun <T> List<T>.add(item: T) : List<T>  = if(this.isNotEmpty()){
    (this as MutableList<T>).add(item)
    this
} else {
    ArrayList<T>().also {
        it.add(item)
    }.toList()
}

fun <T> List<T>.addToFront(item: T): List<T>  = if(this.isNotEmpty()){
    (this as MutableList<T>).add(0, item)
    this
} else {
    ArrayList<T>().also {
        it.add(item)
    }.toList()
}

fun <T> Iterable<T>.removeAdjacent(): List<T> {
    var last: T? = null
    return mapNotNull {
        if (it == last) {
            null
        } else {
            last = it
            it
        }
    }
}

fun <T>  Collection<T>.isLastIndex(index: Int): Boolean = (size - 1) == index


fun <T> List<T>.addAndReturnNewInstance(item: T): List<T> = ArrayList<T>().also {
    it.addAll(this)
    it.add(item)
}

fun <T> List<T>.addToPosAndReturnNewInstance(item: T, position: Int): List<T> = ArrayList<T>().also {
    it.addAll(this)
    if(this.lastIndex >= position){
        it.add(position, item)
    } else {
        it.add(item)
    }
}

fun <T> Collection<T>.containsAnyItemFrom(other: Collection<T>): Boolean{

    other.forEach {
        if(it in this) return true
    }
    return false
}

fun <T, R : Comparable<R>> Iterable<T>.groupByDescending(selector: (T) -> R): List<List<T>> =
    sortedByDescending {
        selector(it)
    }.group(selector)

fun <T, R : Comparable<R>> Iterable<T>.groupBy(selector: (T) -> R): List<List<T>> =
    sortedBy {
        selector(it)
    }.group(selector)

private fun <T, R : Comparable<R>> Iterable<T>.group(selector: (T) -> R): List<List<T>> =
    let {
        val result = ArrayList<List<T>>()
        val value = ArrayList<T>()
        it.forEach { item ->
            if (value.isEmpty()) {
                value.add(item)
            } else if (selector(value[0]) == selector(item)) {
                value.add(item)
            } else {
                result.add(value.toList())
                value.clear()
                value.add(item)
            }
        }
        if(value.isNotEmpty()){
            result.add(value.toList())
        }
        result.toList()
    }