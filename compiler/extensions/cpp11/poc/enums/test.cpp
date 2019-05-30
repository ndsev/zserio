#include <iostream>

#include "Color.h"

int main()
{
    using enumeration_types::bitfield_enum::Color;
    using ColorUtil = zserio::EnumUtil<Color>;

    const Color enumValue = Color::RED;
    std::cout << "toString: " << ColorUtil::toString(enumValue) << std::endl;
    std::cout << "toEnum: " << ColorUtil::toString(ColorUtil::toEnum(7)) << std::endl;
    for (Color color : ColorUtil::values)
        std::cout << "getEnumValue: " << ColorUtil::toString(color) << std::endl;

    return 0;
}
