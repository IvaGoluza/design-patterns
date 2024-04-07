#include <iostream>

class CoolClass{
public:
  virtual void set(int x){x_=x;};
  virtual int get(){return x_;};
private:
  int x_;
};
class PlainOldClass{
public:
  void set(int x){x_=x;};
  int get(){return x_;};
private:
  int x_;
};

int main(void){
  printf("%d\n", sizeof(PlainOldClass));
  printf("%d", sizeof(CoolClass));
  return 0;
}


// PlainOldClass je klasa koja ne koristi virtualne funkcije, pa nema ni pokazivač na virtualnu tablicu.
// Veličina objekta PlainOldClass je 4 bajta zbog toga što objekt sadržava varijablu tipa int koji zauzima 4 bajta.

// CoolClass objekt zauzima 16 bajtova
// 4 bajta za int podatak, kako i prije
// 8 bajtova za poiner na virtualnu tablicu s pokazivačima na virtualne funkcije koje koristi ova klasa (veličina pokazivača kod 64-bitne arhitekture je 8 bajtova)
// 4 bajta padding -> pristup podacima fiksne veličine riječi (64-bitna arhitektura: 8 bajtova) -> dodaje se padding do fiksne veličine granica arhitekture zbog poboljšanja performansi pristupa memoriji
