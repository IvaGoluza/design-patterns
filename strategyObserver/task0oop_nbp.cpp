#include <iostream>
#include <list>

class Point {
public:
    int x;
    int y;
    Point(int x_, int y_) : x(x_), y(y_) {}
};

class Shape {
public:
    virtual void draw() = 0;
    virtual void move(int dx, int dy) = 0;   // pure virtual!
};

class Circle : public Shape {
private:
    double radius_;
    Point center_;
public:
    Circle(double radius, Point center) : radius_(radius), center_(center) {}
    
    void draw() override {
        std::cerr << "Shape draw() overrided in Circle\n";
    }
    
    void move(int dx, int dy) override {
        center_.x += dx;
        center_.y += dy;
        printf("Translated circle center: Point(x=%d, y=%d)\n", center_.x, center_.y);
    }
};

class Square : public Shape {
private:
    double side_;
    Point center_;
public:
    Square(double side, Point center) : side_(side), center_(center) {}

    void draw() override {
        std::cerr << "Shape draw() overrided in Square\n";
    }
    
    void move(int dx, int dy) override {
        center_.x += dx;
        center_.y += dy;
        printf("Translated square center: Point(x=%d, y=%d)\n", center_.x, center_.y);
    }
};

class Rhomb : public Shape {
private:
    double side_;
    Point center_;
    double alpha_;
public:
    Rhomb(double side, Point center, double alpha) : side_(side), center_(center), alpha_(alpha) {}

    void draw() override {
        std::cerr << "Shape draw() overrided in Rhomb\n";
    }
    
    void move(int dx, int dy) override {
        center_.x += dx;
        center_.y += dy;
        printf("Translated rhomb center: Point(x=%d, y=%d)\n", center_.x, center_.y);
    }
};

void drawShapes(const std::list<Shape*>& fig) {     // by the lecture example
    for (const auto& shape : fig) {
        shape->draw();
    }
}

void moveShapes(std::list<Shape*>& fig, int dx, int dy) {    // by the lecture example
    for (const auto& shape : fig) {
        shape->move(dx, dy);
    }
}

int main() {
    std::list<Shape*> listOfShapes;
    listOfShapes.push_back(new Circle(1.1, Point(0, 0)));
    listOfShapes.push_back(new Square(2.2, Point(1, 1)));
    listOfShapes.push_back(new Square(3.14, Point(2, 2)));
    listOfShapes.push_back(new Circle(4.4, Point(3, 3)));
    listOfShapes.push_back(new Rhomb(5.5, Point(4, 4), 33.0));

    drawShapes(listOfShapes);
    moveShapes(listOfShapes, 1, 2);

    for (auto& shape : listOfShapes) {
        delete shape;
    }
    return 0;
}
