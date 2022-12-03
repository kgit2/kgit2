package com.kgit2.describe

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.toBoolean
import com.kgit2.common.extend.toInt
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.cstr
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import libgit2.GIT_DESCRIBE_FORMAT_OPTIONS_VERSION
import libgit2.git_describe_format_options
import libgit2.git_describe_format_options_init

@Raw(
    base = git_describe_format_options::class,
)
class DescribeFormatOptions(
    raw: DescribeFormatOptionsRaw = DescribeFormatOptionsRaw(initial = {
        git_describe_format_options_init(this, GIT_DESCRIBE_FORMAT_OPTIONS_VERSION)
    }),
) : RawWrapper<git_describe_format_options, DescribeFormatOptionsRaw>(raw) {
    var abbreviatedSize: UInt = raw.handler.pointed.abbreviated_size
        set(value) {
            field = value
            raw.handler.pointed.abbreviated_size = value
        }

    var alwaysUseLongFormat: Boolean = raw.handler.pointed.always_use_long_format.toBoolean()
        set(value) {
            field = value
            raw.handler.pointed.always_use_long_format = value.toInt()
        }

    var dirtySuffix: String? = raw.handler.pointed.dirty_suffix?.toKString()
        set(value) {
            field = value
            raw.handler.pointed.dirty_suffix = value?.cstr?.getPointer(raw.memory)
        }
}
