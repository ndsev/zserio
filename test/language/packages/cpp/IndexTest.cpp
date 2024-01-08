#include "gtest/gtest.h"
#include "index_workaround/index/Test.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace index_workaround
{

class IndexTest : public ::testing::Test
{
protected:
    static constexpr uint32_t ARRAY_SIZE = 10;
};

constexpr uint32_t IndexTest::ARRAY_SIZE;

TEST_F(IndexTest, readWrite)
{
    ::index_workaround::index::Test test;
    test.getIndexes().resize(ARRAY_SIZE);
    test.getIndexesForParameterized().resize(ARRAY_SIZE);
    for (uint32_t i = 0; i < ARRAY_SIZE; ++i)
    {
        test.getArray().emplace_back(i);
        test.getParameterizedArray().emplace_back(i);
    }
    test.initializeChildren();

    zserio::BitBuffer bitBuffer(1024 * 8);
    zserio::BitStreamWriter writer(bitBuffer);
    test.initializeOffsets(writer.getBitPosition());
    test.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    ::index_workaround::index::Test readTest(reader);

    ASSERT_EQ(ARRAY_SIZE, readTest.getArray().size());
    ASSERT_EQ(ARRAY_SIZE, readTest.getParameterizedArray().size());
    ASSERT_EQ(test, readTest);
}

} // namespace index_workaround
