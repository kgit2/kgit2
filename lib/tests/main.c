#include <stdio.h>
#include "git2.h"

int main() {
    printf("Hello, World!\n");
    int result = git_libgit2_init();
    printf("init %d\n", result);
    result = git_libgit2_shutdown();
    printf("shutdown %d\n", result);
    return 0;
}
