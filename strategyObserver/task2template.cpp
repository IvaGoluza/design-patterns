#include <iostream>
#include <cstring>
#include <vector>
#include <set>

using namespace std;

template <typename Iterator, typename Predicate>
Iterator mymax(Iterator first, Iterator last, Predicate pred){
    Iterator max_el = first; // set first el as init max
    while (first != last) {
        if(pred(*first, *max_el)) max_el = first;
        ++first;
    }
    return max_el;
}

int gt_int(int first, int second) {  
    if (first > second) return 1;
    else return 0;
}

int gt_char(char first, char second) {
    if (first > second) return 1;
    else return 0;
}

int gt_str(string first, string second) {
    if (first > second) return 1;
    else return 0;
}

int arr_int[] = { 1, 3, 5, 7, 4, 6, 9, 2, 0 };
char arr_char[]="Suncana strana ulice";
string arr_str[] = {
    "Gle", "malu", "vocku", "poslije", "kise",
    "Puna", "je", "kapi", "pa", "ih", "njise"
};

int main(){
    const int* maxint = mymax(&arr_int[0], &arr_int[sizeof(arr_int)/sizeof(*arr_int)], gt_int);
    cout <<*maxint <<"\n";

    const char* maxchar = mymax(&arr_char[0], &arr_char[sizeof(arr_char)/sizeof(*arr_char)], gt_char);
    cout <<*maxchar <<"\n";

    const string* maxstring = mymax(&arr_str[0], &arr_str[sizeof(arr_str)/sizeof(*arr_str)], gt_str);
    cout <<*maxstring <<"\n";
    
    vector<int> vector_int = {13, 2, 2002};
    auto maxvecint = mymax(vector_int.begin(), vector_int.end(), gt_int);
    cout <<*maxvecint <<"\n";

    set<int> set_int = {27, 5, 2005};
    auto maxsetint = mymax(set_int.begin(), set_int.end(), gt_int);
    cout <<*maxsetint <<"\n";

    vector<string> vector_string = {"iva", "ella"};
    auto maxvectorstring = mymax(vector_string.begin(), vector_string.end(), gt_str);
    cout <<*maxvectorstring <<"\n";

    return 0;
}