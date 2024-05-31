#include "myfactory.h"
#include <string.h>
#include <windows.h>
#include <stdio.h>

typedef void* (*PTRFUN)();
typedef int (*PTRFUN_INT)();

void* myfactory(char const* libname, char const* ctorarg){
    char libname_path[256];
    snprintf(libname_path, sizeof(libname_path), "./%s.dll", libname);

    HMODULE module = LoadLibrary(libname_path);                       
    PTRFUN fun = (PTRFUN)GetProcAddress(module,"create");

    return (void*)(*fun)(ctorarg);
}

int myfactorySize(char const* libname){
    char libname_path[256];
    snprintf(libname_path, sizeof(libname_path), "./%s.dll", libname);

    HMODULE module = LoadLibrary(libname_path);    
    PTRFUN_INT sizeOf = (PTRFUN_INT)GetProcAddress(module,"size");

    return (*sizeOf)();
}

void* myfactoryStack(char const* libname, char const* ctorarg, void* allocatedMemory){
    char libname_path[256];
    snprintf(libname_path, sizeof(libname_path), "./%s.dll", libname);
    HMODULE hmodule = LoadLibrary(libname_path);
    
    PTRFUN construct = (PTRFUN)GetProcAddress(hmodule, "construct");
    
    return (void*)(*construct)(allocatedMemory,ctorarg);
}