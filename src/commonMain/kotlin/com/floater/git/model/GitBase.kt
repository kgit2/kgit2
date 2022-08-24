package com.floater.git.model

import kotlinx.cinterop.Arena

interface GitBase<T> {
    var handler: T?
    val arena: Arena
}
