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
 * Returns the next element in the array after the specified [item].
 * If the [item] is the last element in the array, returns the first element.
 *
 * @param item The current item to find the next element for.
 * @return The next element in the array or the first element if the current item is the last one.
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

/**
 * Creates a list containing all elements from the provided collections.
 *
 * @param elements [Collection]s whose elements are to be included in the resulting list.
 * @return A new [List] containing all elements from the provided collections.
 */
fun <T> listOfCollections(vararg elements: Collection<T>): List<T> = if (elements.isNotEmpty()) {
    ArrayList<T>().apply {
        elements.forEach {
            addAll(it)
        }
    }.toList()
} else {emptyList()}


/**
 * Removes the last element from the list and returns a new list.
 *
 * @return A new list with the last element removed.
 */
fun <T> List<T>.removeLast(): List<T> =
    if(isEmpty()){
        this
    } else {
        ArrayList<T>().also {
            it.addAll(this)
            it.removeAt(it.lastIndex)
        }
    }

/**
 * Removes the first element from this list and returns a new list.
 *
 * If the list is empty, returns the original list.
 *
 * @return A new list with the first element removed.
 */
fun <T> List<T>.removeFirst(): List<T> =
    if(isEmpty()){
        this
    } else {
        ArrayList<T>().also {
            it.addAll(this)
            it.removeAt(0)
        }
    }

/**
 * Removes the first occurrence of the specified item from this list.
 *
 * @param item The item to remove.
 * @return A new list with the item removed, or the original list if the item is not present.
 */
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

/**
 * Moves the specified item to the front of the list.
 *
 * @param item The item to move to the front.
 * @return A new list with the item moved to the front, or the original list if the item is not present.
 */
fun <T> List<T>.moveToFront(item: T): List<T> =
    if(isEmpty() || !this.contains(item)){
        this
    } else {
         ArrayList<T>().also {
            it.addAll(this)
            it.remove(item)
            it.addToFront(item)
        }.toList()
    }

/**
 * Adds an item to the end of this list.
 *
 * @param item The item to add.
 * @return A new list containing all elements of the original list followed by the added item.
 */
fun <T> List<T>.add(item: T) : List<T>  = ArrayList<T>().also {
    it.addAll(this)
    it.add(item)
}

/**
 * Adds all elements of the specified [items] list to this list.
 *
 * @param items The list containing elements to be added to this list.
 * @return A new list containing all elements from both this list and the [items] list.
 */
fun <T> List<T>.addAll(items: List<T>) : List<T>  = ArrayList<T>().also {
    it.addAll(this)
    it.addAll(items)
}


/**
 * Adds an item to the front of the list.
 *
 * @param item The item to add to the front.
 * @return A new list with the item added to the front.
 */
fun <T> List<T>.addToFront(item: T): List<T>  = ArrayList<T>().also {
    it.add(item)
    it.addAll(this)
}

/**
 * Removes adjacent duplicate elements from an iterable.
 *
 * @return A list containing the original elements with adjacent duplicates removed.
 */
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

/**
 * Determines if the given [index] is the last index of this collection.
 *
 * @param index The index to check.
 * @return `true` if the [index] is the last index, `false` otherwise.
 */
fun <T>  Collection<T>.isLastIndex(index: Int): Boolean = (size - 1) == index


/**
 * Adds an item to the end of this list and returns a new list containing all elements of the original list followed by the added item.
 *
 * @param item The item to add.
 * @return A new [List] containing all elements of the original list followed by the added item.
 */
fun <T> List<T>.addAndReturnNewInstance(item: T): List<T> = add(item)

/**
 * Adds an item to a specified position in the list and returns a new instance of the list.
 *
 * @param item The item to be added to the list.
 * @param position The index at which the item should be inserted. If the position is greater than or equal to the size of the list, the item will be
 *  appended to the end.
 * @return A new list with the item added at the specified position.
 */
fun <T> List<T>.addToPosAndReturnNewInstance(item: T, position: Int): List<T> = ArrayList<T>().also {
    it.addAll(this)
    if(this.lastIndex >= position){
        it.add(position, item)
    } else {
        it.add(item)
    }
}

/**
 * Determines whether the collection contains any item from another collection.
 *
 * @param other [Collection] to check for items within this collection
 * @return true if any item from [other] is found in this collection, otherwise `false`
 */
fun <T> Collection<T>.containsAnyItemFrom(other: Collection<T>): Boolean =

    run breaking@{
        other.forEach {
            if (it in this) return@breaking true
        }
        false
    }

/**
 * Groups the elements of this [Iterable] into lists based on the values returned by the specified [selector].
 * The grouping is done in descending order.
 *
 * @param selector a function that extracts a key from each element to be used for grouping
 * @return a list of lists, where each sublist contains elements with the same key
 */
fun <T, R : Comparable<R>> Iterable<T>.groupByDescending(selector: (T) -> R): List<List<T>> =
    sortedByDescending {
        selector(it)
    }.group(selector)

/**
 * Groups the elements of this collection by the specified [selector] function.
 *
 * @param selector a function that extracts a property from each element to be used as a key for grouping
 * @return a list of lists, where each sublist contains elements that have the same key according to the [selector]
 */
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