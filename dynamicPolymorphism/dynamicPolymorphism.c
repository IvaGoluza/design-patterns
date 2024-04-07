#include <stdio.h>
#include <stdlib.h>
#include <string.h>

typedef char const* (*PTRFUN)();
typedef struct Animal Animal;
typedef struct Dog Dog;
typedef struct Cat Cat;

char const* dogGreet(void){
  return "vau!";
}
char const* dogMenu(void){
  return "kuhanu govedinu";
}
char const* catGreet(void){
  return "mijau!";
}
char const* catMenu(void){
  return "konzerviranu tunjevinu";
}

PTRFUN dogVirtualTable[] = {dogGreet, dogMenu};
PTRFUN catVirtualTable[] = {catGreet, catMenu};

struct Animal
{
    const char* name;
    PTRFUN* functionsTable;
};

struct Dog
{
    Animal super;
};

struct Cat
{
    Animal super;
};

void animalPrintGreeting(Animal* animal) {
    printf("%s pozdravlja: %s\n", animal->name, animal->functionsTable[0]());
}

void animalPrintMenu(Animal* animal) {
    printf("%s voli %s\n", animal->name, animal->functionsTable[1]());
}

void constructAnimal(Animal* this, const char* name){
    this->name=name;
    this->functionsTable=NULL;
}

void constructDog(Dog* this, const char* name) {
    constructAnimal(&(this->super), name);
    this->super.functionsTable=dogVirtualTable;
}

void constructCat(Cat* this, const char* name) {
    constructAnimal(&(this->super), name);
    this->super.functionsTable=catVirtualTable;
}

struct Animal* createDog(const char* name){
    Dog* dog=(Dog*)malloc(sizeof(Dog));
    if (dog == NULL) {
        exit(EXIT_FAILURE);
    }
    constructDog(dog, name);
    return (Animal*)dog;
}

struct Animal* createCat(const char* name){
    Cat* cat=(Cat*)malloc(sizeof(Cat));
    if (cat == NULL) {
        exit(EXIT_FAILURE);
    }
    constructCat(cat, name);
    return (Animal*)cat;
}

void testAnimals(void){
  struct Animal* p1=createDog("Hamlet");
  struct Animal* p2=createCat("Ofelija");
  struct Animal* p3=createDog("Polonije");

  animalPrintGreeting(p1);
  animalPrintGreeting(p2);
  animalPrintGreeting(p3);

  animalPrintMenu(p1);
  animalPrintMenu(p2);
  animalPrintMenu(p3);

  free(p1); free(p2); free(p3);
}

struct Animal* createDogs(int n) {
    Dog* dogs=(Dog*)malloc(n * sizeof(Dog));
    if (dogs == NULL) {
        exit(EXIT_FAILURE);
    }

    for (int i = 0; i < n; i++) {
        constructDog(&dogs[i], "sleigh");
    }
    return (Animal*)dogs;
}

int main(void) {
    printf("\nAnimal testing, no animals were harmed during compilation!\n\n");
    testAnimals();

    printf("\nCat, get off the STACK!\n");
    Animal cat={"John Doe", catVirtualTable};
    animalPrintGreeting(&cat);
    animalPrintMenu(&cat); 

    printf("\nCat Konstrakta, get off the STACK!\n");
    Cat cat2={};
    constructCat(&cat2, "Konstrakta");
    animalPrintGreeting((Animal*)&cat2);
    animalPrintMenu((Animal*)&cat2); 

    printf("\nDog, do not sleep on the HEAP!\n");
    Animal* dog=createDog("Baltazar");
    animalPrintGreeting(dog);
    animalPrintMenu(dog);
    free(dog);

    printf("\nDog park\n");
    Animal* dogs=createDogs(10);
    for (int i = 0; i < 10; i++) {
        animalPrintGreeting(&dogs[i]);
    }
    free(dogs); // free a single contiguous block of memory for dogs

    return 0;
}

