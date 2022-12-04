//
// Created by BppleMan on 2022/12/4.
//

#include <stdio.h>
#include <stdlib.h>
#include "native.h"

void get_string(const char** strings, int size) {
    for (int i = 0; i < size; ++i) {
        strings[i] = malloc(sizeof(char) * 10);
        sprintf(strings[i], "Hello, %d", i);
    }
}
