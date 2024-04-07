#include <iostream>

class B{
public:
  virtual int __cdecl prva()=0;
  virtual int __cdecl druga(int)=0;
};

class D: public B{
public:
  virtual int __cdecl prva(){return 42;}
  virtual int __cdecl druga(int x){return prva()+x;}
};

typedef int (*PTRFUN_1)(B*);
typedef int (*PTRFUN_2)(B*, int);

void  manualCall(B* pb){
    uintptr_t* vTable = (uintptr_t*)(*(uintptr_t*)pb);
    
    PTRFUN_1 f1 = (PTRFUN_1)vTable[0];
    PTRFUN_2 f2 = (PTRFUN_2)vTable[1];

    printf("F1: %p\n", f1);
    printf("F2: %p\n", f2);

    printf("F1 result: %d\n", f1(pb));
    printf("F2 result: %d\n", f2(pb, 13));
}

int main() {
    B* pb = new D();
    manualCall(pb);

    return 0;
}
