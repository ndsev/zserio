#include "gtest/gtest.h"

#include "zserio/BitStreamWriter.h"
#include "zserio/BitStreamReader.h"
#include "zserio/CppRuntimeException.h"

#include "expressions/index_operator/ElementList.h"
#include "expressions/index_operator/Element.h"

namespace expressions
{
namespace index_operator
{

class IndexOperatorTest : public ::testing::Test
{
protected:
    ElementList createElementList(size_t length, bool lastWrong = false)
    {
        ElementList list;
        auto& elements = list.getElements();
        elements.reserve(length);
        for (size_t i = 0; i < length; ++i)
        {
            bool isEven = i % 2 + 1 == 2;
            const bool wrong = lastWrong && i + 1 == length;
            Element element;
            if (wrong ? !isEven : isEven)
                element.setField8(static_cast<uint8_t>(ELEMENTS[i]));
            else
                element.setField16(static_cast<int16_t>(ELEMENTS[i]));
            elements.push_back(element);
        }
        list.setLength(static_cast<uint16_t>(elements.size()));
        list.initializeChildren();
        return list;
    }

    ElementList readWrite(ElementList& list)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        list.write(writer);

        zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
        ElementList newList(reader);
        return newList;
    }

    void checkWriteError(ElementList& list)
    {
        zserio::BitStreamWriter writer(bitBuffer);
        ASSERT_THROW(list.write(writer), zserio::CppRuntimeException);
    }

    void checkElements(const ElementList& list, size_t length, bool lastWrong = false)
    {
        ASSERT_EQ(length, list.getLength());
        for (size_t i = 0; i < length; ++i)
        {
            const bool isEven = i % 2 + 1 == 2;
            const bool wrong = lastWrong && i + 1 == length;
            Element element = list.getElements().at(i);
            if (wrong)
                ASSERT_THROW(isEven ? element.getField16() : element.getField8(),
                        zserio::CppRuntimeException);
            else
                ASSERT_EQ(ELEMENTS[i], isEven ? element.getField8() : element.getField16());
        }
    }

    static const uint16_t ELEMENTS[];
    static const size_t LENGTH_SIZE;
    static const size_t FIELD8_SIZE;
    static const size_t FIELD16_SIZE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(1024 * 8);
};

const uint16_t IndexOperatorTest::ELEMENTS[] =  { 11, 33, 55, 77 };
const size_t IndexOperatorTest::LENGTH_SIZE = 16;
const size_t IndexOperatorTest::FIELD8_SIZE = 8;
const size_t IndexOperatorTest::FIELD16_SIZE = 16;

TEST_F(IndexOperatorTest, zeroLength)
{
    ElementList list = createElementList(0);
    ASSERT_EQ(LENGTH_SIZE, list.bitSizeOf());
    list = readWrite(list);
    ASSERT_EQ(0, list.getLength());
}

TEST_F(IndexOperatorTest, oneElement)
{
    const size_t length = 1;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length);
}

TEST_F(IndexOperatorTest, oneElementWriteWrongField)
{
    const size_t length = 1;
    ElementList list = createElementList(length, true);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkWriteError(list);
}

TEST_F(IndexOperatorTest, oneElementReadWrongField)
{
    const size_t length = 1;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length, true);
}

TEST_F(IndexOperatorTest, twoElements)
{
    const size_t length = 2;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length);
}

TEST_F(IndexOperatorTest, twoElementsWriteWrongField)
{
    const size_t length = 2;
    ElementList list = createElementList(length, true);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkWriteError(list);
}

TEST_F(IndexOperatorTest, twoElementsReadWrongField)
{
    const size_t length = 2;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length, true);
}

TEST_F(IndexOperatorTest, threeElements)
{
    const size_t length = 3;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length);
}

TEST_F(IndexOperatorTest, threeElementsWriteWrongField)
{
    const size_t length = 3;
    ElementList list = createElementList(length, true);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkWriteError(list);
}

TEST_F(IndexOperatorTest, threeElementsReadWrongField)
{
    const int length = 3;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length, true);
}

TEST_F(IndexOperatorTest, fourElements)
{
    const size_t length = 4;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length);
}

TEST_F(IndexOperatorTest, fourElementsWriteWrongField)
{
    const size_t length = 4;
    ElementList list = createElementList(length, true);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkWriteError(list);
}

TEST_F(IndexOperatorTest, fourElementsReadWrongField)
{
    const size_t length = 4;
    ElementList list = createElementList(length);
    ASSERT_EQ(LENGTH_SIZE + FIELD16_SIZE + FIELD8_SIZE + FIELD16_SIZE + FIELD8_SIZE, list.bitSizeOf());
    checkElements(readWrite(list), length, true);
}

} // namespace index_operator
} // namespace expressions
