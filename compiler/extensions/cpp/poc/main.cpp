#include <iostream>

#include "SimpleStructure.h"
#include "ExternalStructure.h"

int main()
{
    const uint8_t numberB = 0xAB;
    ExternalStructure externalStructure(numberB);

    const uint8_t numberA = 0x03;
    const uint8_t numberC = 0x3F;
    SimpleStructure simpleStructure(numberA, ::zserio::BitStream(externalStructure), numberC);

    std::cout << "NumberA = " << std::hex << (unsigned)simpleStructure.getNumberA() << std::endl;
    std::cout << "NumberC = " << std::hex << (unsigned)simpleStructure.getNumberC() << std::endl;

    return 0;
}
