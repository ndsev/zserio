#include <iostream>

#include "Color.h"

int main()
{
    using enumeration_types::bitfield_enum::Color;
    using ColorTraits = zserio::EnumTraits<Color>;

    const Color enumValue = Color::RED;
    std::cout << "toString: " << ColorTraits::names[ColorTraits::toOrdinal(enumValue)] << std::endl;
    std::cout << "toEnum: " << ColorTraits::toString(ColorTraits::toEnum(7)) << std::endl;
    for (std::string colorName : ColorTraits::names)
        std::cout << "getEnumValue: " << colorName << std::endl;

    return 0;
}
