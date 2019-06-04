#include <iostream>

#include "Color.h"

int main()
{
    using enumeration_types::bitfield_enum::Color;

    const Color enumValue = Color::RED;
    std::cout << "enumToString: " << zserio::enumToString(Color::BLACK) << std::endl;
    std::cout << "enumToOrdinal: " << zserio::enumToOrdinal(Color::BLACK) << std::endl;
    std::cout << "valueToEnum: " << zserio::enumToString(zserio::valueToEnum<Color>(7)) << std::endl;

    for (std::string colorName : zserio::EnumTraits<Color>::names)
        std::cout << "name: " << colorName << std::endl;

    for (Color color : zserio::EnumTraits<Color>::values)
        std::cout << "value: " << zserio::enumToString(color) << std::endl;

    return 0;
}
