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

package com.pacoworks.dereference.features.rotation.services

import com.pacoworks.dereference.features.rotation.model.BookCharacter
import com.pacoworks.dereference.features.rotation.model.Transaction
import com.pacoworks.dereference.features.rotation.model.UserInput
import com.pacoworks.dereference.model.agot.ToonDto
import com.pacoworks.dereference.network.AgotApi
import com.pacoworks.rxcomprehensions.RxComprehensions.doFM
import rx.Notification
import rx.Observable
import rx.Scheduler
import java.util.concurrent.TimeUnit

/**
 * Network request from an id to a [Transaction]
 */
typealias TransactionRequest = (String) -> Observable<Transaction>

/**
 * Creates a request for Game of Thrones character by their id.
 *
 * It returns a single [Transaction] result and should never fail.
 *
 * @param user id
 * @param agotApi api to request it to
 * @param scheduler scheduler to run the request in
 * @return the [Observable] operation
 */
fun requestCharacterInfo(user: String, agotApi: AgotApi, scheduler: Scheduler): Observable<Transaction> =
        doFM(
                {
                    agotApi.getCharacterInfo(user)
                            .subscribeOn(scheduler)
                            .materialize()
                            .filter { it.kind != Notification.Kind.OnCompleted }
                },
                { result: Notification<ToonDto> ->
                    Observable.just(when (result.kind) {
                        Notification.Kind.OnNext -> validate(user, result.value)
                        Notification.Kind.OnError ->
                            Transaction.Failure(
                                    if (result.throwable?.message == null) {
                                        ""
                                    } else {
                                        result.throwable.message!!
                                    }, UserInput(user))
                        else -> Transaction.Failure("Completed without results", UserInput(user))
                    })
                }
        )
                /* Add fake delay to better test rotation */
                .delay(5, TimeUnit.SECONDS)

private fun validate(user: String, value: ToonDto): Transaction =
        value.name.let {
            if (it == null) {
                Transaction.Failure("Character could not be validated", UserInput(user))
            } else {
                Transaction.Success(BookCharacter(it))
            }
        }