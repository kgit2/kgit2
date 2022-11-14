package com.kgit2.credential

import com.kgit2.common.error.errorCheck
import com.kgit2.config.Config
import com.kgit2.model.AutoFreeGitBase
import kotlinx.cinterop.*
import libgit2.*

open class Credential(
    override val handler: CPointer<git_credential>,
    override val arena: Arena,
) : AutoFreeGitBase<CPointer<git_credential>> {

    override fun free() {
        git_cred_free(handler)
        super.free()
    }

    companion object {
        fun new(): Credential {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_credential>()
            git_cred_default_new(pointer.ptr).errorCheck()
            return Credential(pointer.value!!, arena)
        }

        fun sshKeyFromAgent(username: String): Credential {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_credential>()
            git_cred_ssh_key_from_agent(pointer.ptr, username).errorCheck()
            return Credential(pointer.value!!, arena)
        }

        fun sshKey(username: String, publicKey: String?, privateKey: String, passphrase: String?): Credential {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_credential>()
            git_cred_ssh_key_new(pointer.ptr, username, publicKey, privateKey, passphrase).errorCheck()
            return Credential(pointer.value!!, arena)
        }

        fun sshKeyFromMemory(
            username: String,
            publicKey: String?,
            privateKey: String,
            passphrase: String?,
        ): Credential {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_credential>()
            git_cred_ssh_key_memory_new(pointer.ptr, username, publicKey, privateKey, passphrase).errorCheck()
            pointer.value!!
            return Credential(pointer.value!!, arena)
        }

        fun userPassPlaintext(username: String, password: String): Credential {
            val arena = Arena()
            val pointer = arena.allocPointerTo<git_credential>()
            git_cred_userpass_plaintext_new(pointer.ptr, username, password).errorCheck()
            return Credential(pointer.value!!, arena)
        }

        fun credentialHelper(
            config: Config,
            url: String,
            username: String?,
        ): Credential {
            val (u, p) = CredentialHelper(url)
                .setUsername(username)
                .execute()!!
            return userPassPlaintext(u, p)
        }
    }
}

