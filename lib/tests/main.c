#include <stdio.h>
#include "git2.h"
#include <unistd.h>

int main() {
    char *argv[] = {"sleep", "2", NULL};
    int child_status = 0;
    pid_t child_pid = fork();
    if(child_pid == 0) {
//        pid_t pid = execvp(argv[0], argv);
//        printf("pid = %d\n", pid);
        time_t t0 = time(0);
        while (1) {
            time_t t1 = time(0);
            double datetime_diff_ms = difftime(t1, t0) * 1000.;
            if (datetime_diff_ms > 1000) {
                printf("datetime_diff_ms = %lf\n", datetime_diff_ms - 1000);
                break;
            }
        }
        exit(-1);
    }
    else {
        /* This is run by the parent.  Wait for the child
           to terminate. */
        struct rusage usage;
        pid_t tpid = wait4(child_pid, &child_status, 0, &usage);
        printf("tpid = %d\n", tpid);
        printf("child_status = %d\n", child_status);
        printf("ru_stime = %ld %d\n", usage.ru_stime.tv_sec, usage.ru_stime.tv_usec);
        printf("ru_utime = %ld %d\n", usage.ru_utime.tv_sec, usage.ru_utime.tv_usec);

        printf("Hello, World!\n");
        int result = git_libgit2_init();
        printf("init %d\n", result);
        result = git_libgit2_shutdown();
        printf("shutdown %d\n", result);
        return 0;
    }
}
