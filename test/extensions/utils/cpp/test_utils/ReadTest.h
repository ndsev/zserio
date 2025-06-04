#ifndef TEST_UTILS_READ_TEST_H_INC
#define TEST_UTILS_READ_TEST_H_INC

#include <functional>
#include <utility>

#include "gtest/gtest.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace test_utils
{

template <typename T, typename... ARGS>
void readTest(
        std::function<void(zserio::BitStreamWriter&)> writeData, const T& expectedReadData, ARGS&&... arguments)
{
    const size_t bitSize = expectedReadData.bitSizeOf();
    zserio::BitBuffer bitBuffer(bitSize);
    zserio::BitStreamWriter writer(bitBuffer);
    writeData(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    T readData(reader, arguments...);

    ASSERT_EQ(expectedReadData, readData);
}

} // namespace test_utils

#endif // TEST_UTILS_READ_TEST_H_INC
