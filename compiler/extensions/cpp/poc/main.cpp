#include <iostream>

#include "SimpleStructure.h"
#include "ExternalStructure.h"

int main()
{
    const uint8_t numberB = 0xAB;
    ExternalStructure externalStructure(numberB);

    const uint8_t numberA = 0x03;
    const uint8_t numberC = 0x3F;
    SimpleStructure simpleStructure1(numberA, ::zserio::Bits(externalStructure), numberC);
    std::cout << "bitSizeOf = " << simpleStructure1.bitSizeOf() << std::endl;

    ::zserio::BitBuffer bitBuffer(10);
    SimpleStructure simpleStructure2(numberA, ::zserio::Bits(bitBuffer), numberC);
    std::cout << "bitSizeOf = " << simpleStructure2.bitSizeOf() << std::endl;

    return 0;
}
