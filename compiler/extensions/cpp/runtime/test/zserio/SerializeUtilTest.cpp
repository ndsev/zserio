#include "gtest/gtest.h"

#include "zserio/SerializeUtil.h"

namespace zserio
{

namespace
{

struct DummyObject
{
    using allocator_type = std::allocator<uint8_t>;

    explicit DummyObject(uint32_t value_) :
            value(value_)
    {}

    explicit DummyObject(BitStreamReader& reader) :
            value(reader.readBits(32))
    {}

    size_t initializeOffsets(size_t bitPosition)
    {
        return bitPosition + 32;
    }

    void write(BitStreamWriter& writer, PreWriteAction preWriteAction)
    {
        usedPreWriteAction = preWriteAction;
        writer.writeBits(value, 32);
    }

    PreWriteAction usedPreWriteAction = PreWriteAction::ALL_PRE_WRITE_ACTIONS;
    uint32_t value;
};

struct DummyObjectWithInitializeChildren : DummyObject
{
    using DummyObject::DummyObject;

    void initializeChildren()
    {
        initializeChildrenCalled = true;
    }

    bool initializeChildrenCalled = false;
};

struct ParameterizedDummyObject : DummyObject
{
    explicit ParameterizedDummyObject(uint32_t value_) :
            DummyObject(value_), param(false), optionalValue(0)
    {}

    ParameterizedDummyObject(BitStreamReader& reader, bool param_) :
            DummyObject(reader), param(param_)
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

    size_t initializeOffsets(size_t bitPosition)
    {
        return DummyObject::initializeOffsets(bitPosition) + (param ? 32 : 0);
    }

    void write(BitStreamWriter& writer, PreWriteAction preWriteAction)
    {
        DummyObject::write(writer, preWriteAction);
        if (param)
            writer.writeBits(optionalValue, 32);
    }

    bool param;
    uint32_t optionalValue;
};

} // namespace

TEST(SerializeUtil, dummyObjectFile)
{
    const std::string fileName = "SerializeUtilTest.bin";

    DummyObject dummy(42);
    serializeToFile(dummy, fileName);
    ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);

    auto readDummy = deserializeFromFile<DummyObject>(fileName);
    ASSERT_EQ(42, readDummy.value);
}

TEST(SerializeUtil, dummyObjectBitBuffer)
{
    DummyObject dummy(42);
    const BitBuffer bitBuffer = serialize(dummy);
    ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);
    ASSERT_EQ(32, bitBuffer.getBitSize());
    ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
    ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
    ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
    ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

    auto readDummy = deserialize<DummyObject>(bitBuffer);
    ASSERT_EQ(42, readDummy.value);
}

TEST(SerializeUtil, dummyObjectWithInitializeChildrenFile)
{
    const std::string fileName = "SerializeUtilTest.bin";

    DummyObjectWithInitializeChildren dummy(13);
    serializeToFile(dummy, fileName);
    ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);
    ASSERT_TRUE(dummy.initializeChildrenCalled);

    auto readDummy = deserializeFromFile<DummyObjectWithInitializeChildren>(fileName);
    ASSERT_EQ(13, readDummy.value);
}

TEST(SerializeUtil, dummyObjectWithInitializeChildrenBitBuffer)
{
    DummyObjectWithInitializeChildren dummy(13);
    const BitBuffer bitBuffer = serialize(dummy);
    ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);
    ASSERT_TRUE(dummy.initializeChildrenCalled);
    ASSERT_EQ(32, bitBuffer.getBitSize());
    ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
    ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
    ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
    ASSERT_EQ(13, bitBuffer.getBuffer()[3]);

    auto readDummy = deserialize<DummyObjectWithInitializeChildren>(bitBuffer);
    ASSERT_EQ(13, readDummy.value);
}

TEST(SerializeUtil, parameterizedDummyObjectFile)
{
    const std::string fileName = "SerializeUtilTest.bin";

    {
        // with optional value
        ParameterizedDummyObject dummy(42);
        dummy.initialize(true);
        dummy.setOptionalValue(13);
        serializeToFile(dummy, fileName);
        ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);

        auto readDummy = deserializeFromFile<ParameterizedDummyObject>(fileName, true);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        ParameterizedDummyObject dummy(42);
        serializeToFile(dummy, fileName);
        ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);

        auto readDummy = deserializeFromFile<ParameterizedDummyObject>(fileName, false);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }
}

TEST(SerializeUtil, parameterizedDummyObjectBitBuffer)
{
    {
        // with optional value
        ParameterizedDummyObject dummy(42);
        dummy.initialize(true);
        dummy.setOptionalValue(13);
        const BitBuffer bitBuffer = serialize(dummy);
        ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);
        ASSERT_EQ(64, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[4]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[5]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[6]);
        ASSERT_EQ(13, bitBuffer.getBuffer()[7]);

        auto readDummy = deserialize<ParameterizedDummyObject>(bitBuffer, true);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_TRUE(readDummy.param);
        ASSERT_EQ(13, readDummy.optionalValue);
    }

    {
        // without optional value
        ParameterizedDummyObject dummy(42);
        const BitBuffer bitBuffer = serialize(dummy);
        ASSERT_EQ(PreWriteAction::NO_PRE_WRITE_ACTION, dummy.usedPreWriteAction);
        ASSERT_EQ(32, bitBuffer.getBitSize());
        ASSERT_EQ(0, bitBuffer.getBuffer()[0]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[1]);
        ASSERT_EQ(0, bitBuffer.getBuffer()[2]);
        ASSERT_EQ(42, bitBuffer.getBuffer()[3]);

        auto readDummy = deserialize<ParameterizedDummyObject>(bitBuffer, false);
        ASSERT_EQ(42, readDummy.value);
        ASSERT_FALSE(readDummy.param);
    }
}

} // namespace zserio
