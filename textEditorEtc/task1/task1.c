#include "myfactory.h"
#include "Animal.h"

#include <stdio.h>
#include <stdlib.h>

// parrots and tigers defined in respective dynamic libraries

// animalPrintGreeting and animalPrintMenu similar as in lab 1
void animalPrintGreeting(Animal* animal) {
    printf("%s pozdravlja: %s\n", animal->vtable[0](animal), animal->vtable[1]());
}

void animalPrintMenu(Animal* animal) {
    printf("%s voli %s\n", animal->vtable[0](), animal->vtable[2]());
}


int main(int argc, char *argv[]){
    for (int i=0; i<argc/2; ++i){

        if(i==0) {

           int size = myfactorySize(argv[1+2*i]);
           Animal* p = (Animal*) alloca(size); 
           p = myfactoryStack(argv[1+2*i], argv[1+2*i+1], p);

           if (!p){
              printf("Creation of plug-in object %s failed.\n", argv[1+2*i]);
              continue;
           }
           animalPrintGreeting(p);
           animalPrintMenu(p);

        } else {

           Animal* p = (Animal*)myfactory(argv[1+2*i], argv[1+2*i+1]);
        
           if (!p){
              printf("Creation of plug-in object %s failed.\n", argv[1+2*i]);
              continue;
           }
           animalPrintGreeting(p);
           animalPrintMenu(p);
           free(p); 
        }
    }
}