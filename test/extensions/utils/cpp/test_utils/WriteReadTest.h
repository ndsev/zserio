#ifndef TEST_UTILS_WRITE_READ_TEST_H_INC
#define TEST_UTILS_WRITE_READ_TEST_H_INC

#include <utility>

#include "gtest/gtest.h"
#include "zserio/SerializeUtil.h"

namespace test_utils
{

namespace detail
{

template <typename T, typename... ARGS>
void writeReadTestDetail(const T& data, ARGS&&... arguments)
{
    const size_t bitSize = data.bitSizeOf();

    zserio::BitBuffer bitBuffer(bitSize);
    zserio::BitStreamWriter writer(bitBuffer);
    data.write(writer);
    ASSERT_EQ(bitSize, writer.getBitPosition());

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    T readData(reader, arguments...);
    ASSERT_EQ(bitSize, reader.getBitPosition());
    ASSERT_EQ(data, readData);
}

template <typename T, typename... ARGS>
void writeReadTestSerializeData(T& data, ARGS&&... arguments)
{
    const size_t bitSize = data.bitSizeOf();

    auto bitBuffer = zserio::serialize(data, arguments...);
    ASSERT_EQ(bitSize, bitBuffer.getBitSize());

    T readData = zserio::deserialize<T>(bitBuffer, arguments...);
    ASSERT_EQ(data, readData);
}

} // namespace detail

template <typename T, typename... ARGS>
void writeReadTest(T& data, ARGS&&... arguments)
{
    detail::writeReadTestDetail(data, arguments...);
    detail::writeReadTestSerializeData(data, arguments...);
}

} // namespace test_utils

#endif // TEST_UTILS_WRITE_READ_TEST_H_INC
