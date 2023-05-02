#include <array>

#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"
#include "zserio/Enums.h"
#include "zserio/pmr/PolymorphicAllocator.h"

#include "test_object/std_allocator/SerializeEnum.h"
#include "test_object/std_allocator/SerializeNested.h"
#include "test_object/std_allocator/SerializeObject.h"
#include "test_object/polymorphic_allocator/SerializeEnum.h"
#include "test_object/polymorphic_allocator/SerializeNested.h"
#include "test_object/polymorphic_allocator/SerializeObject.h"

namespace zserio
{

TEST(SerializeUtilTest, serializeEnum)
{
    // without allocator
    {
        const test_object::std_allocator::SerializeEnum serializeEnum =
                test_object::std_allocator::SerializeEnum::VALUE3;
        const BitBuffer bitBuffer = serialize(serializeEnum);
        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0x02, bitBuffer.getData()[0]);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const test_object::std_allocator::SerializeEnum serializeEnum =
                test_object::std_allocator::SerializeEnum::VALUE3;
        const BitBuffer bitBuffer = serialize(serializeEnum, allocator);
        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0x02, bitBuffer.getData()[0]);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const test_object::polymorphic_allocator::SerializeEnum serializeEnum =
                test_object::polymorphic_allocator::SerializeEnum::VALUE3;
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer =
                serialize(serializeEnum, allocator);
        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0x02, bitBuffer.getData()[0]);
    }
}

TEST(SerializeUtilTest, serializeParameterizedObject)
{
    const int8_t param = 0x12;
    const uint8_t offset = 0;
    const uint32_t optionalValue = 0xDEADCAFE;
    {
        // without allocator
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue);
        const BitBuffer bitBuffer = serialize(serializeNested, param);
        ASSERT_EQ(40, bitBuffer.getBitSize());
        ASSERT_EQ(0x01, bitBuffer.getData()[0]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[1]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[2]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[3]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[4]);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        const BitBuffer bitBuffer = serialize(serializeNested, allocator, param);
        ASSERT_EQ(40, bitBuffer.getBitSize());
        ASSERT_EQ(0x01, bitBuffer.getData()[0]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[1]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[2]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[3]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[4]);
    }

    {
        // with polymorphic allocator
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        test_object::polymorphic_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer =
                serialize(serializeNested, allocator, param);
        ASSERT_EQ(40, bitBuffer.getBitSize());
        ASSERT_EQ(0x01, bitBuffer.getData()[0]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[1]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[2]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[3]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[4]);
    }
}

TEST(SerializeUtilTest, serializeObject)
{
    const int8_t param = 0x12;
    const uint8_t offset = 0;
    const uint32_t optionalValue = 0xDEADCAFE;
    {
        // without allocator
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested);
        const BitBuffer bitBuffer = serialize(serializeObject);
        ASSERT_EQ(48, bitBuffer.getBitSize());
        ASSERT_EQ(0x12, bitBuffer.getData()[0]);
        ASSERT_EQ(0x02, bitBuffer.getData()[1]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[2]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[3]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[4]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[5]);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        const BitBuffer bitBuffer = serialize(serializeObject, allocator);
        ASSERT_EQ(48, bitBuffer.getBitSize());
        ASSERT_EQ(0x12, bitBuffer.getData()[0]);
        ASSERT_EQ(0x02, bitBuffer.getData()[1]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[2]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[3]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[4]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[5]);
    }

    {
        // with polymorphic allocator
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        test_object::polymorphic_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::polymorphic_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer =
                serialize(serializeObject, allocator);
        ASSERT_EQ(48, bitBuffer.getBitSize());
        ASSERT_EQ(0x12, bitBuffer.getData()[0]);
        ASSERT_EQ(0x02, bitBuffer.getData()[1]);
        ASSERT_EQ(0xDE, bitBuffer.getData()[2]);
        ASSERT_EQ(0xAD, bitBuffer.getData()[3]);
        ASSERT_EQ(0xCA, bitBuffer.getData()[4]);
        ASSERT_EQ(0xFE, bitBuffer.getData()[5]);
    }
}

TEST(SerializeUtilTest, deserializeEnum)
{
    const std::array<uint8_t, 1> buffer = {0x02};
    // without allocator
    {
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8);
        const test_object::std_allocator::SerializeEnum serializeEnum =
                deserialize<test_object::std_allocator::SerializeEnum>(bitBuffer);
        ASSERT_EQ(test_object::std_allocator::SerializeEnum::VALUE3, serializeEnum);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8, allocator);
        const test_object::std_allocator::SerializeEnum serializeEnum =
                deserialize<test_object::std_allocator::SerializeEnum>(bitBuffer);
        ASSERT_EQ(test_object::std_allocator::SerializeEnum::VALUE3, serializeEnum);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer(buffer.data(), buffer.size() * 8,
                allocator);
        const test_object::polymorphic_allocator::SerializeEnum serializeEnum =
                deserialize<test_object::polymorphic_allocator::SerializeEnum>(bitBuffer);
        ASSERT_EQ(test_object::polymorphic_allocator::SerializeEnum::VALUE3, serializeEnum);
    }
}

TEST(SerializeUtilTest, deserializeNestedObject)
{
    const std::array<uint8_t, 5> buffer = {0x01, 0xDE, 0xAD, 0xCA, 0xFE};
    const int8_t param = 0x12;
    // without allocator
    {
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8);
        const test_object::std_allocator::SerializeNested serializeNested =
                deserialize<test_object::std_allocator::SerializeNested>(bitBuffer, param);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const BitBuffer wrongBitBuffer(buffer.data(), buffer.size() * 8 - 1);
        ASSERT_THROW(deserialize<test_object::std_allocator::SerializeNested>(wrongBitBuffer, param),
                CppRuntimeException);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8, allocator);
        const test_object::std_allocator::SerializeNested serializeNested =
                deserialize<test_object::std_allocator::SerializeNested>(bitBuffer, param, allocator);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const BitBuffer wrongBitBuffer(buffer.data(), buffer.size() * 8 - 1, allocator);
        ASSERT_THROW(deserialize<test_object::std_allocator::SerializeNested>(wrongBitBuffer, param, allocator),
                CppRuntimeException);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer(buffer.data(), buffer.size() * 8,
                allocator);
        const test_object::polymorphic_allocator::SerializeNested serializeNested =
                deserialize<test_object::polymorphic_allocator::SerializeNested>(bitBuffer, param, allocator);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> wrongBitBuffer(buffer.data(),
                buffer.size() * 8 - 1, allocator);
        ASSERT_THROW(deserialize<test_object::polymorphic_allocator::SerializeNested>(wrongBitBuffer,
                param, allocator), CppRuntimeException);
    }
}

TEST(SerializeUtilTest, deserializeObject)
{
    const std::array<uint8_t, 6> buffer = {0x12, 0x02, 0xDE, 0xAD, 0xCA, 0xFE};
    // without allocator
    {
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8);
        const test_object::std_allocator::SerializeObject serializeObject =
                deserialize<test_object::std_allocator::SerializeObject>(bitBuffer);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::std_allocator::SerializeNested& serializeNested = serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const BitBuffer bitBuffer(buffer.data(), buffer.size() * 8, allocator);
        const test_object::std_allocator::SerializeObject serializeObject =
                deserialize<test_object::std_allocator::SerializeObject>(bitBuffer, allocator);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::std_allocator::SerializeNested& serializeNested = serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const BasicBitBuffer<pmr::PropagatingPolymorphicAllocator<>> bitBuffer(buffer.data(), buffer.size() * 8,
                allocator);
        const test_object::polymorphic_allocator::SerializeObject serializeObject =
                deserialize<test_object::polymorphic_allocator::SerializeObject>(bitBuffer, allocator);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::polymorphic_allocator::SerializeNested& serializeNested =
                serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }
}

TEST(SerializeUtilTest, serializeEnumToBytes)
{
    // without allocator
    {
        const test_object::std_allocator::SerializeEnum serializeEnum =
                test_object::std_allocator::SerializeEnum::VALUE3;
        const vector<uint8_t> buffer = serializeToBytes(serializeEnum);
        ASSERT_EQ(1, buffer.size());
        ASSERT_EQ(0x02, buffer[0]);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const test_object::std_allocator::SerializeEnum serializeEnum =
                test_object::std_allocator::SerializeEnum::VALUE3;
        const vector<uint8_t> buffer = serializeToBytes(serializeEnum, allocator);
        ASSERT_EQ(1, buffer.size());
        ASSERT_EQ(0x02, buffer[0]);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const test_object::polymorphic_allocator::SerializeEnum serializeEnum =
                test_object::polymorphic_allocator::SerializeEnum::VALUE3;
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer =
                serializeToBytes(serializeEnum, allocator);
        ASSERT_EQ(1, buffer.size());
        ASSERT_EQ(0x02, buffer[0]);
    }
}

TEST(SerializeUtilTest, serializeParameterizedObjectToBytes)
{
    const int8_t param = 0x12;
    const uint8_t offset = 0;
    const uint32_t optionalValue = 0xDEADCAFE;
    {
        // without allocator
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue);
        const vector<uint8_t> buffer = serializeToBytes(serializeNested, param);
        ASSERT_EQ(5, buffer.size());
        ASSERT_EQ(0x01, buffer[0]);
        ASSERT_EQ(0xDE, buffer[1]);
        ASSERT_EQ(0xAD, buffer[2]);
        ASSERT_EQ(0xCA, buffer[3]);
        ASSERT_EQ(0xFE, buffer[4]);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        const vector<uint8_t> buffer = serializeToBytes(serializeNested, allocator, param);
        ASSERT_EQ(5, buffer.size());
        ASSERT_EQ(0x01, buffer[0]);
        ASSERT_EQ(0xDE, buffer[1]);
        ASSERT_EQ(0xAD, buffer[2]);
        ASSERT_EQ(0xCA, buffer[3]);
        ASSERT_EQ(0xFE, buffer[4]);
    }

    {
        // with polymorphic allocator
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        test_object::polymorphic_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer =
                serializeToBytes(serializeNested, allocator, param);
        ASSERT_EQ(5, buffer.size());
        ASSERT_EQ(0x01, buffer[0]);
        ASSERT_EQ(0xDE, buffer[1]);
        ASSERT_EQ(0xAD, buffer[2]);
        ASSERT_EQ(0xCA, buffer[3]);
        ASSERT_EQ(0xFE, buffer[4]);
    }
}

TEST(SerializeUtilTest, serializeObjectToBytes)
{
    const int8_t param = 0x12;
    const uint8_t offset = 0;
    const uint32_t optionalValue = 0xDEADCAFE;
    {
        // without allocator
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested);
        const vector<uint8_t> buffer = serializeToBytes(serializeObject);
        ASSERT_EQ(6, buffer.size());
        ASSERT_EQ(0x12, buffer[0]);
        ASSERT_EQ(0x02, buffer[1]);
        ASSERT_EQ(0xDE, buffer[2]);
        ASSERT_EQ(0xAD, buffer[3]);
        ASSERT_EQ(0xCA, buffer[4]);
        ASSERT_EQ(0xFE, buffer[5]);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        const vector<uint8_t> buffer = serializeToBytes(serializeObject, allocator);
        ASSERT_EQ(6, buffer.size());
        ASSERT_EQ(0x12, buffer[0]);
        ASSERT_EQ(0x02, buffer[1]);
        ASSERT_EQ(0xDE, buffer[2]);
        ASSERT_EQ(0xAD, buffer[3]);
        ASSERT_EQ(0xCA, buffer[4]);
        ASSERT_EQ(0xFE, buffer[5]);
    }

    {
        // with polymorphic allocator
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        test_object::polymorphic_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::polymorphic_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer =
                serializeToBytes(serializeObject, allocator);
        ASSERT_EQ(6, buffer.size());
        ASSERT_EQ(0x12, buffer[0]);
        ASSERT_EQ(0x02, buffer[1]);
        ASSERT_EQ(0xDE, buffer[2]);
        ASSERT_EQ(0xAD, buffer[3]);
        ASSERT_EQ(0xCA, buffer[4]);
        ASSERT_EQ(0xFE, buffer[5]);
    }
}

TEST(SerializeUtilTest, deserializeEnumFromBytes)
{
    // without allocator
    {
        const vector<uint8_t> buffer({0x02});
        const test_object::std_allocator::SerializeEnum serializeEnum =
                deserializeFromBytes<test_object::std_allocator::SerializeEnum>(buffer);
        ASSERT_EQ(test_object::std_allocator::SerializeEnum::VALUE3, serializeEnum);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const vector<uint8_t> buffer({0x02}, allocator);
        const test_object::std_allocator::SerializeEnum serializeEnum =
                deserializeFromBytes<test_object::std_allocator::SerializeEnum>(buffer);
        ASSERT_EQ(test_object::std_allocator::SerializeEnum::VALUE3, serializeEnum);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer({0x02}, allocator);
        const test_object::polymorphic_allocator::SerializeEnum serializeEnum =
                deserializeFromBytes<test_object::polymorphic_allocator::SerializeEnum>(buffer);
        ASSERT_EQ(test_object::polymorphic_allocator::SerializeEnum::VALUE3, serializeEnum);
    }
}

TEST(SerializeUtilTest, deserializeNestedObjectFromBytes)
{
    const int8_t param = 0x12;
    // without allocator
    {
        const vector<uint8_t> buffer({0x01, 0xDE, 0xAD, 0xCA, 0xFE});
        const test_object::std_allocator::SerializeNested serializeNested =
                deserializeFromBytes<test_object::std_allocator::SerializeNested>(buffer, param);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const vector<uint8_t> wrongBuffer = {0x00, 0xDE, 0xAD, 0xCA, 0xFE};
        ASSERT_THROW(deserializeFromBytes<test_object::std_allocator::SerializeNested>(wrongBuffer, param),
                CppRuntimeException);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const vector<uint8_t> buffer({0x01, 0xDE, 0xAD, 0xCA, 0xFE}, allocator);
        const test_object::std_allocator::SerializeNested serializeNested =
                deserializeFromBytes<test_object::std_allocator::SerializeNested>(buffer, param, allocator);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const vector<uint8_t> wrongBuffer = {0x00, 0xDE, 0xAD, 0xCA, 0xFE};
        ASSERT_THROW(deserializeFromBytes<test_object::std_allocator::SerializeNested>(wrongBuffer, param),
                CppRuntimeException);
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer(
                {0x01, 0xDE, 0xAD, 0xCA, 0xFE}, allocator);
        const test_object::polymorphic_allocator::SerializeNested serializeNested =
                deserializeFromBytes<test_object::polymorphic_allocator::SerializeNested>(
                        buffer, param, allocator);
        ASSERT_EQ(param, serializeNested.getParam());
        ASSERT_EQ(0x01, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());

        const vector<uint8_t> wrongBuffer = {0x00, 0xDE, 0xAD, 0xCA, 0xFE};
        ASSERT_THROW(deserializeFromBytes<test_object::polymorphic_allocator::SerializeNested>(wrongBuffer,
                param), CppRuntimeException);
    }
}

TEST(SerializeUtilTest, deserializeObjectFromBytes)
{
    // without allocator
    {
        const vector<uint8_t> buffer({0x12, 0x02, 0xDE, 0xAD, 0xCA, 0xFE});
        const test_object::std_allocator::SerializeObject serializeObject =
                deserializeFromBytes<test_object::std_allocator::SerializeObject>(buffer);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::std_allocator::SerializeNested& serializeNested = serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const vector<uint8_t> buffer({0x12, 0x02, 0xDE, 0xAD, 0xCA, 0xFE}, allocator);
        const test_object::std_allocator::SerializeObject serializeObject =
                deserializeFromBytes<test_object::std_allocator::SerializeObject>(buffer, allocator);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::std_allocator::SerializeNested& serializeNested = serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }

    // with polymorphic allocator
    {
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        const vector<uint8_t, pmr::PropagatingPolymorphicAllocator<>> buffer(
                {0x12, 0x02, 0xDE, 0xAD, 0xCA, 0xFE}, allocator);
        const test_object::polymorphic_allocator::SerializeObject serializeObject =
                deserializeFromBytes<test_object::polymorphic_allocator::SerializeObject>(buffer, allocator);
        ASSERT_EQ(0x12, serializeObject.getParam());
        const test_object::polymorphic_allocator::SerializeNested& serializeNested =
                serializeObject.getNested();
        ASSERT_EQ(0x12, serializeNested.getParam());
        ASSERT_EQ(0x02, serializeNested.getOffset());
        ASSERT_EQ(0xDEADCAFE, serializeNested.getOptionalValue());
    }
}

TEST(SerializeUtilTest, serializeToFileFromFile)
{
    const int8_t param = 0x12;
    const uint8_t offset = 0;
    const uint32_t optionalValue = 0xDEADCAFE;
    const std::string fileName = "SerializationTest.bin";
    {
        // without allocator
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested);
        serializeToFile(serializeObject, fileName);
        const test_object::std_allocator::SerializeObject readSerializeObject =
                deserializeFromFile<test_object::std_allocator::SerializeObject>(fileName);
        ASSERT_EQ(serializeObject, readSerializeObject);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        test_object::std_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::std_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        serializeToFile(serializeObject, fileName);
        const test_object::std_allocator::SerializeObject readSerializeObject =
                deserializeFromFile<test_object::std_allocator::SerializeObject>(fileName);
        ASSERT_EQ(serializeObject, readSerializeObject);
    }

    {
        // with polymorphic allocator
        const pmr::PropagatingPolymorphicAllocator<> allocator;
        test_object::polymorphic_allocator::SerializeNested serializeNested(offset, optionalValue, allocator);
        test_object::polymorphic_allocator::SerializeObject serializeObject(param, serializeNested, allocator);
        serializeToFile(serializeObject, fileName);
        const test_object::polymorphic_allocator::SerializeObject readSerializeObject =
                deserializeFromFile<test_object::polymorphic_allocator::SerializeObject>(fileName);
        ASSERT_EQ(serializeObject, readSerializeObject);
    }
}

} // namespace zserio
