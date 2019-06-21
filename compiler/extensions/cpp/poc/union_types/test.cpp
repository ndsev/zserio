#include <iostream>
#include "Union.h"

int main(int argc, char* argv[])
{
    Union u(std::vector<uint8_t>{'a', 'h', 'o', 'j'});
    Union u2(Structure{'a', 'h', 'o', 'j'});

    return 0;
}
