#include <iostream>
#include <iomanip>

#include "Structure.h"

Structure createStructure()
{
    return Structure(5, Array{{1,2,3,4,5}}, true, 2, Array{{1,2}}, String{"test"},
            Structure{Array{{1, 2}}, false,
                    zserio::NullOpt, zserio::NullOpt, String("last"), zserio::NullOpt});
}

int main(int argc, char* argv[])
{
    zserio::BitStreamWriter writer;
    Structure s = createStructure();
    s.write(writer);

    size_t bufferSize = 0;
    const uint8_t* buffer = writer.getWriteBuffer(bufferSize);

    zserio::BitStreamReader reader(buffer, bufferSize);
    Structure rs(reader);

    std::cout << (s == rs ? "equal" : "different!") << std::endl;

    return 0;
}
