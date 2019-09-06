#include <iostream>
#include "array/Array.h"
#include "array/U32Array.h"
#include "array/DataArray.h"

using namespace array;

int main()
{
    U32Array arr;
    arr.getValues().push_back(42);
    std::cout << arr.getValues()[0] << std::endl;

    DataArray dataArr;
    dataArr.getValues().push_back(Data{13});
    std::cout << dataArr.getValues()[0].getValue() << std::endl;

    return 0;
}
