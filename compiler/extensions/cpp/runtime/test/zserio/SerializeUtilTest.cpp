#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"
#include "zserio/Enums.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{

namespace
{

enum class DummyEnum : uint8_t
{
    VALUE1 = UINT8_C(0),
    VALUE2 = UINT8_C(1),
    VALUE3 = UINT8_C(2)
};

template <typename ALLOC = std::allocator<uint8_t>>
struct DummyObject
{
    explicit DummyObject(uint32_t value_, const ALLOC& = ALLOC()) :
            value(value_)
    {}

    explicit DummyObject(BitStreamReader& reader, const ALLOC& = ALLOC()) :
            value(reader.readBits(32))
    {}

    size_t initializeOffsets(size_t bitPosition = 0)
    {
        return bitPosition + 32;
    }

    void write(BitStreamWriter& writer) const
    {
        writer.writeBits(value, 32);
    }

    uint32_t value;
};

template <typename ALLOC = std::allocator<uint8_t>>
struct DummyObjectWithInitializeChildren : DummyObject<ALLOC>
{
    using DummyObject<ALLOC>::DummyObject;

    void initializeChildren()
    {
        initializeChildrenCalled = true;
    }

    bool initializeChildrenCalled = false;
};

template <typename ALLOC = std::allocator<uint8_t>>
struct ParameterizedDummyObject : DummyObject<ALLOC>
{
    explicit ParameterizedDummyObject(uint32_t value_, const ALLOC& allocator = ALLOC()) :
            DummyObject<ALLOC>(value_, allocator), param(false), optionalValue(0)
    {}

    ParameterizedDummyObject(BitStreamReader& reader, bool param_, const ALLOC& allocator = ALLOC()) :
            DummyObject<ALLOC>(reader, allocator), param(param_)
    {
        if (param)
            optionalValue = reader.readBits(32);
    }

    void initialize(bool param_)
    {
        param = param_;
    }

    void setOptionalValue(uint32_t optionalValue_)
    {
        optionalValue = optionalValue_;
    }

    size_t initializeOffsets(size_t bitPosition = 0)
    {
        return DummyObject<ALLOC>::initializeOffsets(bitPosition) + (param ? 32 : 0);
    }

    void write(BitStreamWriter& writer) const
    {
        DummyObject<ALLOC>::write(writer);
        if (param)
            writer.writeBits(optionalValue, 32);
    }

    bool param;
    uint32_t optionalValue;
};

} // namespace

template <>
inline DummyEnum valueToEnum(typename std::underlying_type<DummyEnum>::type rawValue)
{
    switch (rawValue)
    {
    case UINT8_C(0):
    case UINT8_C(1):
    case UINT8_C(2):
        return DummyEnum(rawValue);
    default:
        throw CppRuntimeException("Unknown value for enumeration DummyEnum: ") << rawValue << "!";
    }
}

template <>
inline size_t bitSizeOf<DummyEnum>(DummyEnum)
{
    return UINT8_C(8);
}

template <>
inline DummyEnum read<DummyEnum>(zserio::BitStreamReader& in)
{
    return valueToEnum<DummyEnum>(
            static_cast<typename std::underlying_type<DummyEnum>::type>(in.readBits(UINT8_C(8))));
}

template <>
inline void write<DummyEnum>(BitStreamWriter& out, DummyEnum value)
{
    out.writeBits(enumToValue(value), UINT8_C(8));
}

TEST(SerializeUtilTest, dummyObjectFile)
{
    const std::string fileName = "SerializeUtilTest_dummyObject.bin";

    DummyObject<> dummy(42);
    serializeToFile(dummy, fileName);

    auto readDummy = deserializeFromFile<DummyObject<>>(fileName);
    ASSERT_EQ(42, readDummy.value);
}

TEST(SerializeUtilTest, dummyObjectBitBuffer)
{
    {
        // without allocator
        DummyObject<> dummy(42);
        const BitBuffer bitBuffer = serialize(dummy);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObject<>>(bitBuffer);
        ASSERT_EQ(42, readDummy.value);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        DummyObject<> dummy(42, allocator);
        const BitBuffer bitBuffer = serialize(dummy, allocator);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObject<>>(bitBuffer, allocator);
        ASSERT_EQ(42, readDummy.value);
    }

    {
        // with polymorphic allocator
        const pmr::PolymorphicAllocator<> allocator;
        DummyObject<pmr::PolymorphicAllocator<>> dummy(42, allocator);
        const BasicBitBuffer<pmr::PolymorphicAllocator<>> bitBuffer = serialize(dummy, allocator);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObject<pmr::PolymorphicAllocator<>>>(bitBuffer, allocator);
        ASSERT_EQ(42, readDummy.value);
    }
}

TEST(SerializeUtilTest, dummyObjectWithInitializeChildrenFile)
{
    const std::string fileName = "SerializeUtilTest_dummyObjectWithInitializeChildren.bin";

    DummyObjectWithInitializeChildren<> dummy(13);
    serializeToFile(dummy, fileName);
    ASSERT_TRUE(dummy.initializeChildrenCalled);

    auto readDummy = deserializeFromFile<DummyObjectWithInitializeChildren<>>(fileName);
    ASSERT_EQ(13, readDummy.value);
}

TEST(SerializeUtilTest, dummyObjectWithInitializeChildrenBitBuffer)
{
    {
        // without allocator
        DummyObjectWithInitializeChildren<> dummy(13);
        const BitBuffer bitBuffer = serialize(dummy);
        ASSERT_TRUE(dummy.initializeChildrenCalled);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObjectWithInitializeChildren<>>(bitBuffer);
        ASSERT_EQ(13, readDummy.value);
    }

    {
        // with std allocator
        const std::allocator<uint8_t> allocator;
        DummyObjectWithInitializeChildren<> dummy(13, allocator);
        const BitBuffer bitBuffer = serialize(dummy, allocator);
        ASSERT_TRUE(dummy.initializeChildrenCalled);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObjectWithInitializeChildren<>>(bitBuffer, allocator);
        ASSERT_EQ(13, readDummy.value);
    }

    {
        // with polymorphic allocator
        const pmr::PolymorphicAllocator<> allocator;
        DummyObjectWithInitializeChildren<pmr::PolymorphicAllocator<>> dummy(13, allocator);
        const BasicBitBuffer<pmr::PolymorphicAllocator<>> bitBuffer = serialize(dummy, allocator);
        ASSERT_TRUE(dummy.initializeChildrenCalled);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<DummyObjectWithInitializeChildren<pmr::PolymorphicAllocator<>>>(bitBuffer,
                allocator);
        ASSERT_EQ(13, readDummy.value);
    }
}

TEST(SerializeUtilTest, parameterizedDummyObjectFile)
{
    const std::string fileName = "SerializeUtilTest_parameterizedDummyObject.bin";

    {
        // with optional value
        ParameterizedDummyObject<> dummy(42);
        dummy.setOptionalValue(13);
        serializeToFile(dummy, fileName, true);

        auto readDummy = deserializeFromFile<ParameterizedDummyObject<>>(fileName, true);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        ParameterizedDummyObject<> dummy(42);
        serializeToFile(dummy, fileName, false);

        auto readDummy = deserializeFromFile<ParameterizedDummyObject<>>(fileName, false);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }
}

TEST(SerializeUtilTest, parameterizedDummyObjectBitBuffer)
{
    // without allocator
    {
        // with optional value
        ParameterizedDummyObject<> dummy(42);
        dummy.setOptionalValue(13);
        const BitBuffer bitBuffer = serialize(dummy, true);
        ASSERT_EQ(64, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[4]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[5]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[6]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[7]);

        auto readDummy = deserialize<ParameterizedDummyObject<>>(bitBuffer, true);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        ParameterizedDummyObject<> dummy(42);
        const BitBuffer bitBuffer = serialize(dummy, false);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<ParameterizedDummyObject<>>(bitBuffer, false);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }

    // with std allocator
    {
        // with optional value
        const std::allocator<uint8_t> allocator;
        ParameterizedDummyObject<> dummy(42, allocator);
        dummy.setOptionalValue(13);
        const BitBuffer bitBuffer = serialize(dummy, allocator, true);
        ASSERT_EQ(64, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[4]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[5]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[6]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[7]);

        auto readDummy = deserialize<ParameterizedDummyObject<>>(bitBuffer, true, allocator);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        const std::allocator<uint8_t> allocator;
        ParameterizedDummyObject<> dummy(42, allocator);
        const BitBuffer bitBuffer = serialize(dummy, allocator, false);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<ParameterizedDummyObject<>>(bitBuffer, false, allocator);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }

    // with polymorphic allocator
    {
        // with optional value
        const pmr::PolymorphicAllocator<> allocator;
        ParameterizedDummyObject<pmr::PolymorphicAllocator<>> dummy(42, allocator);
        dummy.setOptionalValue(13);
        const BasicBitBuffer<pmr::PolymorphicAllocator<>> bitBuffer = serialize(dummy, allocator, true);
        ASSERT_EQ(64, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[4]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[5]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[6]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[7]);

        auto readDummy = deserialize<ParameterizedDummyObject<pmr::PolymorphicAllocator<>>>(bitBuffer,
                true, allocator);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        const pmr::PolymorphicAllocator<> allocator;
        ParameterizedDummyObject<pmr::PolymorphicAllocator<>> dummy(42, allocator);
        const BasicBitBuffer<pmr::PolymorphicAllocator<>> bitBuffer = serialize(dummy, allocator, false);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<ParameterizedDummyObject<pmr::PolymorphicAllocator<>>>(bitBuffer,
                false, allocator);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }
}

TEST(SerializeUtilTest, dummyEnumFile)
{
    const std::string fileName = "SerializeUtilTest_dummyEnum.bin";

    const DummyEnum dummy = DummyEnum::VALUE1;
    zserio::serializeToFile(dummy, fileName);

    const DummyEnum readDummy = zserio::deserializeFromFile<DummyEnum>(fileName);
    ASSERT_EQ(dummy, readDummy);
}

TEST(SerializeUtilTest, dummyEnumBitBuffer)
{
    // without allocator
    {
        const DummyEnum dummy = DummyEnum::VALUE1;
        const BitBuffer bitBuffer = zserio::serialize(dummy);

        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);

        const DummyEnum readDummy = zserio::deserialize<DummyEnum>(bitBuffer);
        ASSERT_EQ(dummy, readDummy);
    }

    // with std allocator
    {
        const std::allocator<uint8_t> allocator;
        const DummyEnum dummy = DummyEnum::VALUE1;
        const BitBuffer bitBuffer = zserio::serialize(dummy, allocator);

        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);

        const DummyEnum readDummy = zserio::deserialize<DummyEnum>(bitBuffer);
        ASSERT_EQ(dummy, readDummy);
    }

    // with polymorphic allocator
    {
        const pmr::PolymorphicAllocator<> allocator;
        const DummyEnum dummy = DummyEnum::VALUE1;
        const BasicBitBuffer<pmr::PolymorphicAllocator<>> bitBuffer = zserio::serialize(dummy, allocator);

        ASSERT_EQ(8, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);

        const DummyEnum readDummy = zserio::deserialize<DummyEnum>(bitBuffer);
        ASSERT_EQ(dummy, readDummy);
    }
}

} // namespace zserio
