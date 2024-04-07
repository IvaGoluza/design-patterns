#include <stdio.h>
#include <stdlib.h>
#include <stdbool.h>

typedef struct Unary_Function Unary_Function;
typedef struct Square Square;
typedef struct Linear Linear;
typedef double (*PTRFUN)();

// base class Unary_Function
struct Unary_Function         
{
    int lower_bound;
    int upper_bound;
    PTRFUN* vTable;
};

// class Square; inherits base class Unary_Function
struct Square         
{
    Unary_Function super;
};

// class Linear; inherits base class Unary_Function; has additional class data
struct Linear
{
    Unary_Function super;
    double a;
    double b;
};




// pure virtual Unary_Function method; no implementation
double value_at(Unary_Function* this, double x) {} 

// Square implementation for pure virtual method from parent class Unary_Function
double value_at_Square(Square* this, double x) {                                
    return x*x;
} 

// Linear implementation for pure virtual method from parent class Unary_Function
double value_at_Linear(Linear* this, double x) {
    return this->a * x + this->b;
}

// virtual method; with base imaplementation
double negative_value_at(Unary_Function* this, double x) {
    return -this->vTable[0](this, x);
}

// Unary_Function method; uses 'this' since it is a part of class instance
void tabulate(Unary_Function* this){
    for(int x=this->lower_bound; x <= this->upper_bound; x++) {
        printf("f(%d)=%lf\n", x, this->vTable[0](this, (double)x));    // vTable[0]: value_at 
    }    
}

// Unary_Function static method; does not use this reference of class instance since it is not part of a class instance, but the class itself
bool same_functions_for_ints(Unary_Function* f1, Unary_Function* f2, double tolerance){
    if(f1->lower_bound != f2->lower_bound) return false;    
    if(f1->upper_bound != f2->upper_bound) return false;
    for(int x = f1->lower_bound; x <= f1->upper_bound; x++) {
        double delta = (f1->vTable[0])(f1, x) - (f2->vTable[0])(f2,x);    // vTable[0]: value_at 
        if(delta < 0) delta = -delta;
        if(delta > tolerance) return false;
    }
    return true;
}




// virtual table for base class
PTRFUN unary_vTable[] =  {value_at, negative_value_at};

// constructor
void constructor_Unary(Unary_Function* this, int lower_bound, int upper_bound){
    this->lower_bound=lower_bound;
    this->upper_bound=upper_bound;
    this->vTable=unary_vTable;
}

// new Unary_Function(...)
Unary_Function* newUnary_Function(int lower_bound, int upper_bound){
    Unary_Function* unary_Function = (Unary_Function*)malloc(sizeof(Unary_Function));
    constructor_Unary(unary_Function, lower_bound, upper_bound);
    return unary_Function;
};

// virtual table for Square class; its implementation of pure virtual method from parent class, and virtual method from parent class since there is no new implementation
PTRFUN square_vTable[] =  {value_at_Square, negative_value_at};

// constructor
void constructor_Square(Square* this, int lower_bound, int upper_bound){
    constructor_Unary((Unary_Function*) this, lower_bound, upper_bound);
    this->super.vTable=square_vTable;
}

// new Square(...)
Unary_Function* newSquare(int lower_bound, int upper_bound){
    Square* square = (Square*)malloc(sizeof(Square));
    constructor_Square(square, lower_bound, upper_bound);
    return (Unary_Function*)square;
};

// virtual table for Linear class; its implementation of pure virtual method from parent class, and virtual method from parent class since there is no new implementation
PTRFUN linear_vTable[] =  {value_at_Linear, negative_value_at};

// constructor
void constructor_Linear(Linear* this, int lower_bound, int upper_bound, double a, double b){
    constructor_Unary((Unary_Function*) this, lower_bound, upper_bound);
    this->super.vTable=linear_vTable;
    this->a=a;
    this->b=b;
}

// new Linear(...)
Unary_Function* newLinear(int lower_bound, int upper_bound, double a, double b){
    Linear* linear = (Linear*)malloc(sizeof(Linear));
    constructor_Linear(linear, lower_bound, upper_bound,a,b);
    return (Unary_Function*)linear;
};




int main(void) {
    Unary_Function *f1 = newSquare(-2, 2);
    tabulate(f1);
    Unary_Function *f2 = newLinear(-2, 2, 5, -2);
    tabulate(f2);
    printf("f1==f2: %s\n", same_functions_for_ints(f1, f2, 1E-6) ? "DA" : "NE");
    printf("neg_val f2(1) = %lf\n", negative_value_at(f2, 1.0));
    free(f1);
    free(f2);
    return 0;
}