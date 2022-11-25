package com.kgit2.credential

import com.kgit2.common.error.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.config.Config
import com.kgit2.memory.Raw
import com.kgit2.memory.GitBase
import kotlinx.cinterop.*
import libgit2.*

typealias CredentialPointer = CPointer<git_credential>

typealias CredentialSecondaryPointer = CPointerVar<git_credential>

typealias CredentialInitial = CredentialSecondaryPointer.(Memory) -> Unit

class CredentialRaw(
    memory: Memory,
    handler: CredentialPointer,
) : Raw<git_credential>(memory, handler) {
    constructor(
        memory: Memory = Memory(),
        handler: CredentialSecondaryPointer = memory.allocPointerTo(),
        initial: CredentialInitial? = null,
    ) : this(memory, handler.apply {
        runCatching {
            initial?.invoke(this, memory)
        }.onFailure {
            git_credential_free(handler.value)
            memory.free()
        }.getOrThrow()
    }.value!!)

    override val beforeFree: () -> Unit = {
        git_credential_free(handler)
    }
}

class Credential(
    raw: CredentialRaw,
) : GitBase<git_credential, CredentialRaw>(raw) {
    constructor(memory: Memory, handler: CredentialPointer) : this(CredentialRaw(memory, handler))

    constructor() : this(CredentialRaw(initial = { git_credential_default_new(this.ptr) }))

    constructor(
        memory: Memory = Memory(),
        handler: CredentialSecondaryPointer = memory.allocPointerTo(),
        initial: CredentialInitial? = null,
    ) : this(CredentialRaw(memory, handler, initial))

    constructor(username: String, password: String? = null, fromAgent: Boolean = false) : this(CredentialRaw(initial = {
        when {
            password != null -> git_credential_userpass_plaintext_new(this.ptr, username, password)
            fromAgent -> git_credential_ssh_key_from_agent(this.ptr, username)
            else -> git_credential_username_new(this.ptr, username)
        }.errorCheck()
    }))

    constructor(
        username: String,
        publicKey: String?,
        privateKey: String,
        passphrase: String?,
        fromMemory: Boolean = false,
    ) : this(CredentialRaw(initial = {
        when (fromMemory) {
            true -> git_cred_ssh_key_memory_new(this.ptr, username, publicKey, privateKey, passphrase)
            false -> git_cred_ssh_key_new(this.ptr, username, publicKey, privateKey, passphrase)
        }.errorCheck()
    }))

    constructor(
        config: Config,
        url: String,
        username: String?,
        result: Pair<String, String> = CredentialHelper(url).config(config).setUsername(username).execute()!!,
    ) : this(result.first, result.second)
}

