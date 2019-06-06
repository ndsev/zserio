#include <iostream>

#include "Structure.h"

Structure createStructure()
{
    return Structure{2, {5, {1,2,3,4,5}}, true, 2, {{2, {1,2}}}, {"test"}, {}};
}

int main(int argc, char* argv[])
{
    zserio::OptionalHolder<Structure> s = createStructure();

    return 0;
}
