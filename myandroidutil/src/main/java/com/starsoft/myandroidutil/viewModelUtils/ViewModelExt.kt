/*
 * Copyright (c) 2021. Dmitry Starkin Contacts: t0506803080@gmail.com
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



package com.starsoft.myandroidutil.viewModelUtils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner

/**
 * Created by Dmitry Starkin on 01.08.2021 14:43.
 */

class ModelFactory<V : ViewModel> (
    private val model:  V
): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass == model.javaClass){
            model as T
        } else {
            ViewModelProvider.NewInstanceFactory().create(modelClass)
        }
    }
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.initViewModel(crossinline init: () -> T): Lazy<T> = lazy {
    ViewModelProvider(this, ModelFactory(init())).get(T::class.java)
}

inline fun <reified T : ViewModel> initSharedViewModel(crossinline owner: () -> ViewModelStoreOwner, crossinline init: () -> T): Lazy<T> = lazy {
    ViewModelProvider(owner(), ModelFactory(init())).get(T::class.java)
}

inline fun <reified T : ViewModel> ViewModelStoreOwner.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(T::class.java)
}

inline fun <reified T : ViewModel> sharedViewModel(crossinline owner: () -> ViewModelStoreOwner): Lazy<T> = lazy {
    ViewModelProvider(owner(), ViewModelProvider.NewInstanceFactory()).get(T::class.java)
}
