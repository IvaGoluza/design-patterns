#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Animal.h"

typedef struct Parrot Parrot;

struct Parrot
{
    Animal super;
    const char* name;
};

char const* name(Parrot* parrot){
  return parrot->name;
}

char const* greet(void){
  return "rim tim!";
}

char const* menu(void){
  return "sladoled";
}

PTRFUN vtable[] = {name, greet, menu};

void* construct(Parrot* parrot, char const* name){
    parrot->super.vtable = vtable;
    parrot->name = name;
    return parrot;
}

void* create(char const* name){
    struct Parrot* this = (Parrot*)malloc(sizeof(Parrot));
    return construct(this, name);
}

int size(void){
    return sizeof(Parrot);
}