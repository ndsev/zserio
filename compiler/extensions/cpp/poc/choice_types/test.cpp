#include <type_traits>
#include <iostream>

#include "Choice.h"

Choice createChoice(uint16_t value)
{
    return Choice(1, value);
}

int main(int argc, char* argv[])
{
    Choice ch = createChoice(10), ch2 = createChoice(10);

    ch.initialize(1);
    ch.setValue16(10);

    ch2.initialize(1);
    ch2.setValue16(10);
    std::cout << (ch == ch2 ? "equal" : "not equal") << std::endl;

    Choice ch3(1, static_cast<uint16_t>(10));
    std::cout << (ch == ch3 ? "equal" : "not equal") << std::endl;

    Choice moved = std::move(createChoice(10)); // TODO: make move working
    std::cout << moved.getValue16() << std::endl;

    Choice ch4(1, 10);

    return 0;
}

