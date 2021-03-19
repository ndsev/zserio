#include "gtest/gtest.h"

#include "reader/Test.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"

namespace reader
{

class ReaderTest : public ::testing::Test
{
protected:
    static constexpr uint32_t ARRAY_SIZE = 10;
};

constexpr uint32_t ReaderTest::ARRAY_SIZE;

TEST_F(ReaderTest, readWrite)
{
    ::reader::Test test;
    test.getIndexes().resize(ARRAY_SIZE);
    test.getIndexesForParameterized().resize(ARRAY_SIZE);
    for (uint32_t i = 0; i < ARRAY_SIZE; ++i)
    {
        test.getArray().emplace_back(i);
        test.getParameterizedArray().emplace_back(i);
    }

    zserio::BitStreamWriter writer;
    test.write(writer);

    size_t writeBufferByteSize;
    const uint8_t* writeBuffer = writer.getWriteBuffer(writeBufferByteSize);
    zserio::BitStreamReader reader(writeBuffer, writeBufferByteSize);
    ::reader::Test readTest(reader);

    ASSERT_EQ(ARRAY_SIZE, readTest.getArray().size());
    ASSERT_EQ(ARRAY_SIZE, readTest.getParameterizedArray().size());
    ASSERT_EQ(test, readTest);
}

} // namespace reader
