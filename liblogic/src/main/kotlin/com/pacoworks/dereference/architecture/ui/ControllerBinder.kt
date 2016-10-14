/*
 * Copyright (c) pakoito 2016
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.pacoworks.dereference.architecture.ui

import com.jakewharton.rxrelay.BehaviorRelay
import com.jakewharton.rxrelay.SerializedRelay
import com.pacoworks.dereference.architecture.reactive.ControllerLifecycle
import rx.Observable
import rx.Scheduler
import rx.Subscription

typealias StateHolder<T> = SerializedRelay<T, T>

fun <T> createStateHolder(value: T): StateHolder<T> =
        SerializedRelay(BehaviorRelay.create<T>(value))

fun <T> bind(lifecycleObservable: Observable<ControllerLifecycle>, mainThreadScheduler: Scheduler, state: StateHolder<T>, doView: (T) -> Unit): Subscription =
        lifecycleObservable
                .filter { it == ControllerLifecycle.Attach }
                .switchMap { state }
                .takeUntil(lifecycleObservable.filter { it == ControllerLifecycle.Detach })
                .observeOn(mainThreadScheduler)
                .subscribe(doView)