#include <type_traits>

#include "zserio/DeprecatedAttribute.h"

enum TestEnum
{
    ONE = 1,
    TWO,
    THREE ZSERIO_DEPRECATED,
    FIVE ZSERIO_DEPRECATED = 5
};

int main(int, char*[])
{
    return static_cast<typename std::underlying_type<TestEnum>::type>(TestEnum::FIVE) - 5;
}
