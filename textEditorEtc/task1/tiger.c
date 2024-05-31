#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "Animal.h"

typedef struct Tiger Tiger;

struct Tiger
{
    Animal super;
    const char* name;
};

char const* name(Tiger* tiger){
  return tiger->name;
}

char const* greet(void){
  return "roar!";
}

char const* menu(void){
  return "batak i mlinci";
}

PTRFUN vtable[] = {name, greet, menu};

void* construct(Tiger* tiger, char const* name){
    tiger->super.vtable = vtable;
    tiger->name = name;
    return tiger;
}

void* create(char const* name){
    struct Tiger* this = (Tiger*)malloc(sizeof(Tiger));
    return construct(this, name);
}

int size(void){
    return sizeof(Tiger);
}