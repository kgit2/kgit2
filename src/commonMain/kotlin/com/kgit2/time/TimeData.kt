package com.kgit2.time

import platform.CoreServices.Nanoseconds

data class TimeData(
    val seconds: Int,
    val nanoseconds: UInt,
)
