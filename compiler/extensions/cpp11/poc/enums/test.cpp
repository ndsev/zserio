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
    typedef zserio::EnumUtil<enumeration_types::bitfield_enum::Color> ColorUtil;

    const Color enumValue = Color::RED;
    std::cout << "toString: " << ColorUtil::toString(enumValue) << std::endl;
    std::cout << "toEnum: " << ColorUtil::toString(ColorUtil::toEnum(7)) << std::endl;
    for (Color color : ColorUtil::getValues())
        std::cout << "getEnumValue: " << ColorUtil::toString(color) << std::endl;

    const MyColor myEnumValue = MyColor::RED;
//        std::cout << "toString: " << zserio::EnumUtil<MyColor>::toString(myEnumValue) << std::endl;

    return 0;
}
