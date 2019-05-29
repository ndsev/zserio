#include <iostream>

#include "Color.h"

enum class MyColor : uint8_t
{
    NONE = UINT8_C(0),
    RED = UINT8_C(2),
    BLUE = UINT8_C(3),
    BLACK = UINT8_C(7)
};

int main()
{
    using enumeration_types::bitfield_enum::Color;

    const Color enumValue = Color::RED;
    std::cout << "enumBitSizeOf: " << zserio::enumBitSizeOf<Color>() << std::endl;
    std::cout << "enumToString: " << zserio::enumToString<Color>(enumValue) << std::endl;
    std::cout << "rawValueToEnum: " << zserio::enumToString<Color>(zserio::rawValueToEnum<Color>(7)) << std::endl;
    for (Color color : zserio::getEnumValues<Color>())
        std::cout << "getEnumValue: " << zserio::enumToString<Color>(color) << std::endl;

    std::cout << "enumBitSizeOf: " << zserio::enumBitSizeOf<MyColor>() << std::endl;

    return 0;
}
