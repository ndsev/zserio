#include <iostream>

#include "Color.h"

int main()
{
    using enumeration_types::bitfield_enum::Color;

    std::cout << "toString: " << zserio::enums::toString(Color::BLACK) << std::endl;
    std::cout << "toOrdinal: " << zserio::enums::toOrdinal(Color::BLACK) << std::endl;
    std::cout << "fromValue: " << zserio::enums::toString(zserio::enums::fromValue<Color>(7)) << std::endl;
    std::cout << "toValue: " << static_cast<int>(zserio::enums::toValue(Color::BLACK)) << std::endl;

    for (std::string colorName : zserio::enums::Traits<Color>::names)
        std::cout << "name: " << colorName << std::endl;

    for (Color color : zserio::enums::Traits<Color>::values)
        std::cout << "value: " << zserio::enums::toString(color) << std::endl;

    return 0;
}
