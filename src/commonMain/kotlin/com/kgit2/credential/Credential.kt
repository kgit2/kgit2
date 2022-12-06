package com.kgit2.credential

import com.kgit2.annotations.Raw
import com.kgit2.common.extend.errorCheck
import com.kgit2.common.memory.Memory
import com.kgit2.config.Config
import com.kgit2.memory.RawWrapper
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.ptr
import libgit2.git_cred_ssh_key_memory_new
import libgit2.git_cred_ssh_key_new
import libgit2.git_credential
import libgit2.git_credential_default_new
import libgit2.git_credential_ssh_key_from_agent
import libgit2.git_credential_username_new
import libgit2.git_credential_userpass_plaintext_new

@Raw(
    base = git_credential::class,
    free = "git_credential_free",
)
class Credential(
    raw: CredentialRaw,
) : RawWrapper<git_credential, CredentialRaw>(raw) {
    constructor(memory: Memory, handler: CredentialPointer) : this(CredentialRaw(memory, handler))

    constructor(
        memory: Memory = Memory(),
        secondary: CredentialSecondaryPointer = memory.allocPointerTo(),
        secondaryInitial: CredentialSecondaryInitial? = null,
    ) : this(CredentialRaw(memory, secondary = secondary, secondaryInitial = secondaryInitial))

    constructor() : this(secondaryInitial = { git_credential_default_new(this.ptr) })

    constructor(username: String, password: String? = null, fromAgent: Boolean = false) : this(secondaryInitial = {
        when {
            password != null -> git_credential_userpass_plaintext_new(this.ptr, username, password)
            fromAgent -> git_credential_ssh_key_from_agent(this.ptr, username)
            else -> git_credential_username_new(this.ptr, username)
        }.errorCheck()
    })

    constructor(
        username: String,
        publicKey: String?,
        privateKey: String,
        passphrase: String?,
        fromMemory: Boolean = false,
    ) : this(secondaryInitial = {
        when (fromMemory) {
            true -> git_cred_ssh_key_memory_new(this.ptr, username, publicKey, privateKey, passphrase)
            false -> git_cred_ssh_key_new(this.ptr, username, publicKey, privateKey, passphrase)
        }.errorCheck()
    })

    constructor(
        config: Config,
        url: String,
        username: String?,
        result: Pair<String, String> = CredentialHelper(url).config(config).setUsername(username).execute()!!,
    ) : this(result.first, result.second)
}

