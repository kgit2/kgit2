package com.floater.git.model

interface IVersion {
    val major: Int
    val minor: Int
    val patch: Int
}

class Version(
    override val major: Int,
    override val minor: Int,
    override val patch: Int,
) : IVersion {
    override fun toString(): String {
        return "$major.$minor.$patch"
    }
}
