#include <iostream>

#include <zserio/BitStreamWriter.h>

#include "SimpleStructure.h"
#include "ExternalStructure.h"

int main()
{
    const uint8_t numberB = 0xAB;
    ExternalStructure externalStructure(numberB);
    ::zserio::BitStreamWriter writer;
    externalStructure.write(writer);

    const uint8_t numberA = 0x03;
    const uint8_t numberC = 0x3F;
    ::zserio::BitBuffer bitBuffer1(writer.getWriteBuffer(), writer.getBitPosition());
    SimpleStructure simpleStructure1(numberA, bitBuffer1, numberC);
    std::cout << "bitSizeOf = " << simpleStructure1.bitSizeOf() << std::endl;

    ::zserio::BitBuffer bitBuffer2(10);
    SimpleStructure simpleStructure2(numberA, bitBuffer2, numberC);
    std::cout << "bitSizeOf = " << simpleStructure2.bitSizeOf() << std::endl;

    return 0;
}
