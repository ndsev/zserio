#include <type_traits>
#include <iostream>

#include "Choice.h"

Choice createChoice(uint16_t value)
{
    Choice ch;
    ch.initialize(1);
    ch.setValue16(10);
    return ch;
}

int main(int argc, char* argv[])
{
    Choice ch = createChoice(10), ch2 = createChoice(10);

    ch.initialize(1);
    ch.setValue16(10);

    ch2.initialize(1);
    ch2.setValue16(10);
    std::cout << (ch == ch2 ? "equal" : "not equal") << std::endl;

    Choice moved = createChoice(10); // TODO: make move working
    std::cout << moved.getValue16() << std::endl;

    return 0;
}

