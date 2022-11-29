package com.kgit2.transport

import com.kgit2.common.error.GitErrorCode
import com.kgit2.remote.Remote

/**
 * Callback for the user's custom transport.
 *
 * @param transport The transport to be used
 * @param remote The remote
 * @return 0 to proceed with the push, < 0 to fail the push
 */
typealias TransportCallback = (transport: Transport, remote: Remote) -> GitErrorCode
