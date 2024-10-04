#include "gtest/gtest.h"
#include "parameterized_types/fixed_and_variable_param/FixedAndVariableParam.h"
#include "zserio/SerializeUtil.h"

namespace parameterized_types
{
namespace fixed_and_variable_param
{

using allocator_type = FixedAndVariableParam::allocator_type;
template <typename T>
using vector_type = zserio::vector<T, allocator_type>;

class FixedAndVariableParamTest : public ::testing::Test
{
protected:
    void fillArrayHolder(ArrayHolder& arrayHolder, uint32_t size, uint8_t extraLimit, LimitHolder& limitHolder,
            Color color, Access access, float floatValue)
    {
        auto& array = arrayHolder.getArray();
        array.clear();
        for (uint32_t i = 0; i < size; ++i)
        {
            array.push_back(static_cast<uint32_t>(i * i));
        }
        arrayHolder.setExtraValue(EXTRA_VALUE);
        arrayHolder.setHasBlack(color == Color::BLACK);
        arrayHolder.setHasRead((access & Access::Values::READ) == Access::Values::READ);
        arrayHolder.setHasFloatBiggerThanOne(floatValue > 1.0F);

        arrayHolder.initialize(size, extraLimit, limitHolder, color, access, floatValue);
    }

    void fillFixedAndVariableParam(FixedAndVariableParam& fixedAndVariableParam, uint32_t size,
            uint8_t extraLimit, uint8_t limit, Color color, Access access, float floatValue)
    {
        fixedAndVariableParam.setExtraLimit(extraLimit);

        LimitHolder& limitHolder = fixedAndVariableParam.getLimitHolder();
        limitHolder.setLimit(limit);

        fixedAndVariableParam.setColor(color);
        fixedAndVariableParam.setAccess(access);
        fixedAndVariableParam.setFloatValue(floatValue);

        ArrayHolder arrayHolder;
        fillArrayHolder(arrayHolder, size, extraLimit, limitHolder, color, access, floatValue);
        fixedAndVariableParam.setArrayHolder(arrayHolder);
    }

    void checkArrayHolderInBitStream(zserio::BitStreamReader& reader, const ArrayHolder& arrayHolder,
            uint32_t size, uint8_t extraLimit, const LimitHolder& limitHolder, Color color, Access access,
            float floatValue)
    {
        ASSERT_EQ(arrayHolder.getSize(), size);
        ASSERT_EQ(arrayHolder.getExtraLimit(), extraLimit);
        ASSERT_EQ(&arrayHolder.getLimitHolder(), &limitHolder);
        ASSERT_EQ(arrayHolder.getColor(), color);
        ASSERT_EQ(arrayHolder.getAccess(), access);
        ASSERT_EQ(arrayHolder.getFloatValue(), floatValue);

        for (uint32_t i = 0; i < size; ++i)
        {
            ASSERT_EQ(arrayHolder.getArray().at(i), reader.readVarUInt());
        }

        ASSERT_EQ(arrayHolder.getExtraValue(), reader.readBits(3));
        ASSERT_EQ(color == Color::BLACK, reader.readBool());
        ASSERT_EQ((access & Access::Values::READ) == Access::Values::READ, reader.readBool());
        ASSERT_EQ(floatValue > 1.0F, reader.readBool());
    }

    void checkFixedAndVariableParamInBitStream(zserio::BitStreamReader& reader,
            const FixedAndVariableParam& fixedAndVariableParam, uint32_t size, uint8_t extraLimit,
            uint8_t limit, Color color, Access access, float floatValue)
    {
        ASSERT_EQ(extraLimit, reader.readBits(8));
        ASSERT_EQ(limit, reader.readBits(8));
        ASSERT_EQ(color, zserio::valueToEnum<Color>(static_cast<uint8_t>(reader.readBits(2))));
        ASSERT_EQ(access.getValue(), reader.readBits(4));
        ASSERT_EQ(floatValue, reader.readFloat16());
        const ArrayHolder& arrayHolder = fixedAndVariableParam.getArrayHolder();
        const LimitHolder& limitHolder = fixedAndVariableParam.getLimitHolder();
        checkArrayHolderInBitStream(
                reader, arrayHolder, size, extraLimit, limitHolder, color, access, floatValue);
    }

    static const std::string BLOB_NAME;

    static const uint8_t EXTRA_VALUE;
    static const uint32_t ARRAY_SIZE;
    static const uint32_t WRONG_ARRAY_SIZE;
    static const uint8_t EXTRA_LIMIT;
    static const uint8_t WRONG_EXTRA_LIMIT;
    static const uint8_t LIMIT;
    static const Color COLOR;
    static const Color WRONG_COLOR;
    static const Access ACCESS;
    static const Access WRONG_ACCESS;
    static const float FLOAT_VALUE;
    static const float WRONG_FLOAT_VALUE;

    zserio::BitBuffer bitBuffer = zserio::BitBuffer(3 * 1024 * 8);
};

const std::string FixedAndVariableParamTest::BLOB_NAME =
        "language/parameterized_types/fixed_and_variable_param.blob";

const uint8_t FixedAndVariableParamTest::EXTRA_VALUE = 0x05;
const uint32_t FixedAndVariableParamTest::ARRAY_SIZE = 1000;
const uint32_t FixedAndVariableParamTest::WRONG_ARRAY_SIZE = 1001;
const uint8_t FixedAndVariableParamTest::EXTRA_LIMIT = 0x05;
const uint8_t FixedAndVariableParamTest::WRONG_EXTRA_LIMIT = 0x06;
const uint8_t FixedAndVariableParamTest::LIMIT = 0x06;
const Color FixedAndVariableParamTest::COLOR = Color::BLACK;
const Color FixedAndVariableParamTest::WRONG_COLOR = Color::WHITE;
const Access FixedAndVariableParamTest::ACCESS = Access::Values::READ;
const Access FixedAndVariableParamTest::WRONG_ACCESS = Access::Values::WRITE;
const float FixedAndVariableParamTest::FLOAT_VALUE = 2.0;
const float FixedAndVariableParamTest::WRONG_FLOAT_VALUE = 1.0;

TEST_F(FixedAndVariableParamTest, writeRead)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);

    zserio::BitStreamWriter writer(bitBuffer);
    fixedAndVariableParam.write(writer);

    zserio::BitStreamReader reader(writer.getWriteBuffer(), writer.getBitPosition(), zserio::BitsTag());
    checkFixedAndVariableParamInBitStream(
            reader, fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    reader.setBitPosition(0);

    FixedAndVariableParam readFixedAndVariableParam(reader);
    ASSERT_EQ(fixedAndVariableParam, readFixedAndVariableParam);
}

TEST_F(FixedAndVariableParamTest, writeReadFile)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);

    zserio::serializeToFile(fixedAndVariableParam, BLOB_NAME);

    const auto readFixedAndVariableParam = zserio::deserializeFromFile<FixedAndVariableParam>(BLOB_NAME);
    ASSERT_EQ(fixedAndVariableParam, readFixedAndVariableParam);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongArraySize)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, WRONG_ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);

    zserio::BitStreamWriter writer1(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer1), zserio::CppRuntimeException);

    fixedAndVariableParam.initializeChildren();
    // this fails because of array size check even if parameters are correct
    zserio::BitStreamWriter writer2(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer2), zserio::CppRuntimeException);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongExtraLimit)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    fixedAndVariableParam.setExtraLimit(WRONG_EXTRA_LIMIT);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer), zserio::CppRuntimeException);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongLimitHolder)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    LimitHolder limitHolder(LIMIT);
    ArrayHolder& arrayHolder = fixedAndVariableParam.getArrayHolder();
    arrayHolder.initialize(arrayHolder.getSize(), arrayHolder.getExtraLimit(), limitHolder,
            arrayHolder.getColor(), arrayHolder.getAccess(), arrayHolder.getFloatValue());

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer), zserio::CppRuntimeException);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongColor)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    fixedAndVariableParam.setColor(WRONG_COLOR);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer), zserio::CppRuntimeException);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongAccess)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    fixedAndVariableParam.setAccess(WRONG_ACCESS);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer), zserio::CppRuntimeException);
}

TEST_F(FixedAndVariableParamTest, writeFailureWrongFloatValue)
{
    FixedAndVariableParam fixedAndVariableParam;
    fillFixedAndVariableParam(
            fixedAndVariableParam, ARRAY_SIZE, EXTRA_LIMIT, LIMIT, COLOR, ACCESS, FLOAT_VALUE);
    fixedAndVariableParam.setFloatValue(WRONG_FLOAT_VALUE);

    zserio::BitStreamWriter writer(bitBuffer);
    ASSERT_THROW(fixedAndVariableParam.write(writer), zserio::CppRuntimeException);
}

} // namespace fixed_and_variable_param
} // namespace parameterized_types
