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

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner


/**
 * Created by Dmitry Starkin on 01.08.2021 14:43.
 */

inline fun <reified T : ViewModel> Fragment.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(T::class.java)
}

inline fun <reified T : ViewModel> Fragment.sharedViewModel(crossinline owner: () -> ViewModelStoreOwner): Lazy<T> = lazy {
    ViewModelProvider(owner(), ViewModelProvider.NewInstanceFactory()).get(T::class.java)
}

inline fun <reified T : ViewModel> AppCompatActivity.viewModel(): Lazy<T> = lazy {
    ViewModelProvider(this, androidx.lifecycle.ViewModelProvider.NewInstanceFactory()).get(T::class.java)
}