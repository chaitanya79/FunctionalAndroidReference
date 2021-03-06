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

package com.pacoworks.dereference.features.list

import com.pacoworks.dereference.architecture.ui.StateHolder
import com.pacoworks.dereference.architecture.ui.createStateHolder
import com.pacoworks.dereference.features.list.model.EditMode
import com.pacoworks.dereference.features.list.model.createEditModeNormal
import rx.Observable

/**
 * Data class containing all [StateHolder] objects used to represent state in this screen
 */
data class ListExampleState(
        val elements: StateHolder<List<String>> = createStateHolder<List<String>>(Observable.range(0, 5).map { it.toString() }.toList().toBlocking().first()),
        val selected: StateHolder<Set<String>> = createStateHolder<Set<String>>(setOf()),
        val editMode: StateHolder<EditMode> = createStateHolder(createEditModeNormal())
)