#include <iostream>
#include <assert.h>
#include <stdlib.h>

struct Point{
    int x; int y;
};
struct Shape{
    enum EType {circle, square, rhomb};
    EType type_;
};
struct Circle{
    Shape::EType type_;
    double radius_;
    Point center_{0, 0};
};
struct Square{
    Shape::EType type_;
    double side_;
    Point center_{0, 1};;
};
struct Rhomb{
    Shape::EType type_;
    double side_;
    Point center_;
    double alpha_;  
};
void drawSquare(struct Square*){
    std::cerr <<"in drawSquare\n";
}
void moveSquare(struct Square* s, int dx, int dy){
    s->center_.x += dx;
    s->center_.y += dy;
    printf("Translated square center: Point(x=%d, y=%d)\n", s->center_.x, s->center_.y);
}
void drawCircle(struct Circle*){
    std::cerr <<"in drawCircle\n";
}
void moveCircle(struct Circle* c, int dx, int dy){
    c->center_.x += dx;
    c->center_.y += dy;
    printf("Translated circle center: Point(x=%d, y=%d)\n", c->center_.x, c->center_.y);
}
void drawRhomb(struct Rhomb*){
    std::cerr <<"in drawRhomb\n";
}
void drawShapes(Shape** shapes, int n){
    for (int i=0; i<n; ++i){
        struct Shape* s = shapes[i];
        switch (s->type_){
        case Shape::square:
            drawSquare((struct Square*)s);
            break;
        case Shape::circle:
            drawCircle((struct Circle*)s);
            break;
        case Shape::rhomb:
            drawRhomb((struct Rhomb*)s);
            break;
        default:
            assert(0); 
            exit(0);
        }
    }
}
void moveShapes(Shape** shapes, int n, int dx, int dy){
    for (int i=0; i<n; ++i){
        struct Shape* s = shapes[i];
        switch (s->type_){
        case Shape::square:
            moveSquare((struct Square*)s, dx, dy);
            break;
        case Shape::circle:
            moveCircle((struct Circle*)s, dx, dy);
            break;
        default:
            assert(0); 
            exit(0);
        }
    }
}
int main(){
    Shape* shapes[5];
    shapes[0]=(Shape*)new Circle;
    shapes[0]->type_=Shape::circle;
    shapes[1]=(Shape*)new Square;
    shapes[1]->type_=Shape::square;
    shapes[2]=(Shape*)new Square;
    shapes[2]->type_=Shape::square;
    shapes[3]=(Shape*)new Circle;
    shapes[3]->type_=Shape::circle;
    shapes[4]=(Shape*)new Rhomb;
    shapes[4]->type_=Shape::rhomb;

    drawShapes(shapes, 5);
    moveShapes(shapes, 4, 1, 2);
}