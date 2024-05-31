#include <stdio.h>
#include <string.h>


int gt_int(const void *first, const void *second) {  
    if (*(int*)first > *(int*)second) return 1;
    else return 0;
}

int gt_char(const void *first, const void *second) {
    if (*(char*)first > *(char*)second) return 1;
    else return 0;
}

int gt_str(const void *first, const void *second) {
    if (strcmp(*(const char**)first, *(const char**)second) > 0) return 1;
    else return 0;
}

const void* mymax(const void *base, size_t nmemb, size_t size, int (*compar)(const void *, const void *)) {
    const void *max_el = base; // set first el as the init max 
    for (int i = 1; i < nmemb; ++i) {   // look through the rest of the arr elements
        const void *el = base + i * size;
        if (compar(el, max_el)) max_el = el; 
    }
    return max_el;
}

int main(void){

    int arr_int[] = { 1, 3, 5, 7, 4, 6, 9, 2, 0 };
    char arr_char[]="Suncana strana ulice";

    char* arr_str[] = {
        "Gle", "malu", "vocku", "poslije", "kise",
        "Puna", "je", "kapi", "pa", "ih", "njise"
    };

    int max_arr_int =  *(int*)mymax(arr_int, sizeof(arr_int)/sizeof(int), sizeof(int), &gt_int);
    printf("[max_arr_int] %d\n", max_arr_int);

    char max_arr_char =  *(char*)mymax(arr_char, sizeof(arr_char)/sizeof(char), sizeof(char), &gt_char);
    printf("[max_arr_char] %c\n", max_arr_char);

    char* max_arr_str =  *(char**)mymax(arr_str, sizeof(arr_str)/sizeof(char*), sizeof(char*), &gt_str);
    printf("[max_arr_str] %s\n", max_arr_str);

    return 0;
}