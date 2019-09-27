#include <iostream>

#include "templates/StructTemplatedTemplateArgument.h"

int main()
{
    templates::StructTemplatedTemplateArgument structTemplatedTemplateArgument;
    structTemplatedTemplateArgument.setCompoundField(
            templates::Field_Compound_uint32(templates::Compound_uint32(42)));

    std::cout << "bitSizeOf = " << structTemplatedTemplateArgument.bitSizeOf() << std::endl;
};

