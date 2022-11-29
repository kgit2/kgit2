package com.kgit2.tree

import com.kgit2.common.error.GitErrorCode

/**
 * Function pointer to receive each entry in a tree
 *
 * @param root The root of the tree
 * @param entry The current entry
 * @return 0 on success or error code
 */
typealias TreeWalkCallback = (root: String, entry: TreeEntry) -> GitErrorCode
