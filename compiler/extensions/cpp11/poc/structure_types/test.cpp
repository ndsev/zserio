#include <iostream>

#include "Structure.h"

Structure createStructure()
{
    return Structure(2, Array{5, {1,2,3,4,5}}, true, 2, Array{2, {1,2}}, String{"test"}, zserio::NullOpt);
}

int main(int argc, char* argv[])
{
    zserio::OptionalHolder<Structure> s = createStructure();

    return 0;
}
