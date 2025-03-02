/*
 * Copyright 2014 Jake Wharton

 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package butterknife

import android.app.Activity
import android.app.Dialog
import android.app.Fragment
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.View
import android.view.ViewGroup
import kotlin.properties.ReadOnlyProperty
import android.support.v4.app.Fragment as SupportFragment

public fun <T : View> ViewGroup.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)
public fun <T : View> Activity.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)
public fun <T : View> Dialog.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)
public fun <T : View> Fragment.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)
public fun <T : View> SupportFragment.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)
public fun <T : View> ViewHolder.bindView(id: Int): ReadOnlyProperty<Any, T> = ViewBinding(id)

public fun <T : View> ViewGroup.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)
public fun <T : View> Activity.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)
public fun <T : View> Dialog.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)
public fun <T : View> Fragment.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)
public fun <T : View> SupportFragment.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)
public fun <T : View> ViewHolder.bindOptionalView(id: Int): ReadOnlyProperty<Any, T?> = OptionalViewBinding(id)

public fun <T : View> ViewGroup.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)
public fun <T : View> Activity.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)
public fun <T : View> Dialog.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)
public fun <T : View> Fragment.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)
public fun <T : View> SupportFragment.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)
public fun <T : View> ViewHolder.bindViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = ViewListBinding(ids)

public fun <T : View> ViewGroup.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)
public fun <T : View> Activity.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)
public fun <T : View> Dialog.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)
public fun <T : View> Fragment.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)
public fun <T : View> SupportFragment.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)
public fun <T : View> ViewHolder.bindOptionalViews(vararg ids: Int): ReadOnlyProperty<Any, List<T>> = OptionalViewListBinding(ids)

private fun findView<T : View>(thisRef: Any, id: Int): T? {
    @Suppress("UNCHECKED_CAST")
    return when (thisRef) {
        is View -> thisRef.findViewById(id)
        is Activity -> thisRef.findViewById(id)
        is Dialog -> thisRef.findViewById(id)
        is Fragment -> thisRef.view.findViewById(id)
        is SupportFragment -> thisRef.view.findViewById(id)
        is ViewHolder -> thisRef.itemView.findViewById(id)
        else -> throw IllegalStateException("Unable to find views on type.")
    } as T?
}

private class ViewBinding<T : View>(val id: Int) : ReadOnlyProperty<Any, T> {
    private val lazy = Lazy<T>()

    override fun get(thisRef: Any, desc: PropertyMetadata): T = lazy.get {
        findView<T>(thisRef, id)
                ?: throw IllegalStateException("View ID $id for '${desc.name}' not found.")
    }
}

private class OptionalViewBinding<T : View>(val id: Int) : ReadOnlyProperty<Any, T?> {
    private val lazy = Lazy<T?>()

    override fun get(thisRef: Any, desc: PropertyMetadata): T? = lazy.get {
        findView<T>(thisRef, id)
    }
}

private class ViewListBinding<T : View>(val ids: IntArray) : ReadOnlyProperty<Any, List<T>> {
    private var lazy = Lazy<List<T>>()

    override fun get(thisRef: Any, desc: PropertyMetadata): List<T> = lazy.get {
        ids.map { id ->
            findView<T>(thisRef, id)
                    ?: throw IllegalStateException("View ID $id for '${desc.name}' not found.")
        }
    }
}

private class OptionalViewListBinding<T : View>(val ids: IntArray) : ReadOnlyProperty<Any, List<T>> {
    private var lazy = Lazy<List<T>>()

    override fun get(thisRef: Any, desc: PropertyMetadata): List<T> = lazy.get {
        ids.map { id -> findView<T>(thisRef, id) }.filterNotNull()
    }
}

private class Lazy<T> {
    private object EMPTY

    private var value: Any? = EMPTY

    fun get(initializer: () -> T): T {
        if (value == EMPTY) {
            value = initializer.invoke()
        }
        @Suppress("UNCHECKED_CAST")
        return value as T
    }
}