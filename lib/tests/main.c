#include <stdio.h>
#include <unistd.h>
#include <memory.h>
#include "git2.h"
#include "git2/odb_backend.h"
#include "git2/sys/odb_backend.h"

void check_error(const char *message) {
    const git_error *e = git_error_last();
    fprintf(stderr, "%s: [%d] %s", message, e->klass, e->message);
}

int progress_cb(const git_indexer_progress *stats, void *payload) {
    // printf("stats->total_objects: %d\n", stats->total_objects);
    printf("this is progress_cb\n");
    return 0;
}

int GIT_CALLBACK(origin_write)(git_odb_backend *, const git_oid *, const void *, size_t, git_object_t) = NULL;

int my_write(git_odb_backend *backend, const git_oid *oid, const void *data, size_t size, git_object_t objt) {
    printf("my_write to be called\n");
    return origin_write(backend, oid, data, size, objt);
}

int main() {
    if (0 > git_libgit2_init()) {
        check_error("git_libgit2_init");
    }
    printf("init success\n");

    git_repository *repo = NULL;
    if (0 != git_repository_open(&repo, "/Users/bppleman/test_repo")) {
        check_error("git_repository_open");
    }

    git_odb_backend *backend = NULL;
    if (0 != git_odb_backend_pack(&backend, "/Users/bppleman/test_repo/.git/objects")) {
        check_error("git_odb_backend_loose");
    }


    git_odb *odb = NULL;
    if (0 != git_repository_odb(&odb, repo)) {
        check_error("git_repository_odb");
    }

    if (0 != git_odb_add_backend(odb, backend, 1)) {
        check_error("git_odb_add_backend");
    }

    origin_write = backend->write;
    backend->write = my_write;

    git_odb_writepack *writepack = NULL;
    backend->writepack(&writepack, backend, odb, progress_cb, NULL);
    // if (0 != git_odb_write_pack(&writepack, odb, progress_cb, NULL)) {
    //     check_error("git_odb_write_pack");
    // }

    git_indexer_progress stats = {
            .total_objects = 0,
            .indexed_objects = 0,
            .received_objects = 0,
            .local_objects = 0,
            .total_deltas = 0,
            .indexed_deltas = 0,
            .received_bytes = 0
    };

    printf("%d\n", writepack->backend == backend);
    writepack->append(writepack, "123", 3, &stats);
    writepack->commit(writepack, &stats);
    // writepack->free(writepack);

    if (0 > git_libgit2_shutdown()) {
        check_error("git_libgit2_shutdown");
    }
    printf("shutdown success\n");
    return 0;
}
