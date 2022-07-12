#include <vector>

#include "zserio/AnyHolder.h"

#include "TrackingAllocator.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace
{

class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    int getValue() const { return m_value; }
    void setValue(int value) { m_value = value; }

private:
    int m_value;
};

} // namespace

TEST(AnyHolderTest, emptyConstructor)
{
    AnyHolder<> any;
    ASSERT_FALSE(any.hasValue());
}

TEST(AnyHolderTest, lvalueConstructor)
{
    std::vector<int> values{1, 2, 3};
    void* origAddress = &values[0];

    AnyHolder<> any{values};

    const std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_NE(origAddress, &anyValues[0]);
    ASSERT_EQ(values, anyValues);
}

TEST(AnyHolderTest, rvalueConstructor)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];

    AnyHolder<> any{std::move(values)};

    const std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &anyValues[0]);
    ASSERT_EQ(origValues, anyValues);
}

TEST(AnyHolderTest, copyConstructor)
{
    AnyHolder<> any;
    const int intValue = 0xDEAD;
    any.set(intValue);

    AnyHolder<> anyCopy(any);
    ASSERT_EQ(intValue, anyCopy.get<int>());
    ASSERT_THROW(anyCopy.get<float>(), CppRuntimeException);

    // check that vector is not moved to copiedAny
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];
    any = std::move(values);
    std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &anyValues[0]);

    AnyHolder<> copiedAny{any};

    const std::vector<int>& copiedAnyValues = copiedAny.get<std::vector<int>>();
    ASSERT_NE(origAddress, &copiedAnyValues[0]);
    ASSERT_EQ(origValues, copiedAnyValues);
}

TEST(AnyHolderTest, copyAssignmentOperator)
{
    AnyHolder<> any;
    const int intValue = 0xDEAD;
    any.set(intValue);

    AnyHolder<> anyCopy = any;
    ASSERT_EQ(intValue, anyCopy.get<int>());
    ASSERT_THROW(anyCopy.get<float>(), CppRuntimeException);

    // check that vector is not moved to copiedAny
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];
    any = std::move(values);
    std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &anyValues[0]);

    AnyHolder<> copiedAny;
    copiedAny = any;

    const std::vector<int>& copiedAnyValues = copiedAny.get<std::vector<int>>();
    ASSERT_NE(origAddress, &copiedAnyValues[0]);
    ASSERT_EQ(origValues, copiedAnyValues);
}

TEST(AnyHolderTest, moveConstructorValueOnHeap)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];
    AnyHolder<> any{std::move(values)};

    AnyHolder<> movedAny{std::move(any)};
    ASSERT_TRUE(movedAny.isType<std::vector<int>>());

    const std::vector<int>& movedAnyValues = movedAny.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &movedAnyValues[0]);
    ASSERT_EQ(origValues, movedAnyValues);
}

TEST(AnyHolderTest, moveConstructorValueInPlace)
{
    uint32_t value = 10;
    AnyHolder<> any(value);

    AnyHolder<> movedAny{std::move(any)};
    ASSERT_TRUE(movedAny.isType<uint32_t>());
    uint32_t& movedValue = movedAny.get<uint32_t>();
    ASSERT_EQ(value, movedValue);

    movedValue = 20;
    AnyHolder<> otherMoved{std::move(movedAny)};
    ASSERT_TRUE(otherMoved.isType<uint32_t>());
    ASSERT_EQ(20, otherMoved.get<uint32_t>());
}

TEST(AnyHolderTest, moveConstructorNoValue)
{
    TrackingAllocatorNonProp<uint8_t> alloc1, alloc2;

    AnyHolder<TrackingAllocatorNonProp<uint8_t>> any1(alloc1);
    AnyHolder<TrackingAllocatorNonProp<uint8_t>> any2(std::move(any1), alloc2);
    ASSERT_FALSE(any1.hasValue());
    ASSERT_FALSE(any2.hasValue());
}

TEST(AnyHolderTest, moveAssignmentOperatorValueOnHeap)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];
    AnyHolder<> any{std::move(values)};

    AnyHolder<> movedAny;
    movedAny = std::move(any);

    const std::vector<int>& movedAnyValues = movedAny.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &movedAnyValues[0]);
    ASSERT_EQ(origValues, movedAnyValues);

    TrackingAllocatorNonProp<uint8_t> alloc1;
    TrackingAllocatorNonProp<uint8_t> alloc2;

    std::vector<int> vals1{1, 2, 3};
    std::vector<int> vals2{4, 5, 6};
    AnyHolder<TrackingAllocatorNonProp<uint8_t>> anyInt1(vals1, alloc1);
    AnyHolder<TrackingAllocatorNonProp<uint8_t>> anyInt2(vals2, alloc2);
    anyInt2 = std::move(anyInt1);
    ASSERT_EQ(vals1, anyInt2.get<std::vector<int>>());
}

TEST(AnyHolderTest, moveAssignmentOperatorValueInPlace)
{
    uint32_t value = 10;
    AnyHolder<> any(value);

    AnyHolder<> movedAny;
    movedAny = std::move(any);
    ASSERT_TRUE(movedAny.isType<uint32_t>());
    uint32_t& movedValue = movedAny.get<uint32_t>();
    ASSERT_EQ(value, movedValue);

    movedValue = 20;
    AnyHolder<> otherMoved;
    otherMoved = std::move(movedAny);
    ASSERT_TRUE(otherMoved.isType<uint32_t>());
    ASSERT_EQ(20, otherMoved.get<uint32_t>());
}

TEST(AnyHolderTest, lvalueAssignmentOperator)
{
    std::vector<int> values{1, 2, 3};
    void* origAddress = &values[0];
    AnyHolder<> any;
    any = values;

    const std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_NE(origAddress, &anyValues[0]);
    ASSERT_EQ(values, anyValues);

    any = values;
    ASSERT_EQ(values, anyValues);
    ASSERT_TRUE(any.hasValue());
}

TEST(AnyHolderTest, rvalueAssignmentOperator)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = &values[0];
    AnyHolder<> any;
    any = std::move(values);

    std::vector<int>& anyValues = any.get<std::vector<int>>();
    ASSERT_EQ(origAddress, &anyValues[0]);
    ASSERT_EQ(origValues, anyValues);
}

TEST(AnyHolderTest, reset)
{
    AnyHolder<> any{std::vector<int>{1, 2, 3}};
    ASSERT_TRUE(any.hasValue());
    ASSERT_EQ(1, any.get<std::vector<int>>()[0]);

    any.reset();
    ASSERT_FALSE(any.hasValue());

    any = std::vector<int>{3, 2, 1};
    ASSERT_TRUE(any.hasValue());
    ASSERT_EQ(3, any.get<std::vector<int>>()[0]);

    any.reset();
    ASSERT_FALSE(any.hasValue());
}

TEST(AnyHolderTest, unitializedGet)
{
    AnyHolder<> any;
    ASSERT_THROW(any.get<int>(), zserio::CppRuntimeException);
}

TEST(AnyHolderTest, setGet)
{
    AnyHolder<> any;

    const int intValue = 0xDEAD;
    any.set(intValue);
    ASSERT_EQ(intValue, any.get<int>());
    ASSERT_THROW(any.get<float>(), CppRuntimeException);

    const float floatValue = 3.14f;
    any.set(floatValue);
    ASSERT_THROW(any.get<int>(), CppRuntimeException);
    ASSERT_EQ(floatValue, any.get<float>());

    DummyObject objectValue;
    objectValue.setValue(0xDEAD);
    any.set(objectValue);
    ASSERT_THROW(any.get<float>(), CppRuntimeException);
    const DummyObject& readObjectValueConst = any.get<DummyObject>();
    ASSERT_EQ(objectValue.getValue(), readObjectValueConst.getValue());
    const DummyObject& readObjectValue = any.get<DummyObject>();
    ASSERT_EQ(objectValue.getValue(), readObjectValue.getValue());
}

TEST(AnyHolderTest, isType)
{
    AnyHolder<> any;
    ASSERT_FALSE(any.isType<int>());

    const int intValue = 0xDEAD;
    any.set(intValue);
    ASSERT_TRUE(any.isType<int>());

    const float floatValue = 3.14f;
    any.set(floatValue);
    ASSERT_TRUE(any.isType<float>());
    ASSERT_FALSE(any.isType<int>());
}

TEST(AnyHolderTest, hasValue)
{
    AnyHolder<> any;
    ASSERT_FALSE(any.hasValue());

    const int intValue = 0xDEAD;
    any.set(intValue);
    ASSERT_TRUE(any.hasValue());

    any.reset();
    ASSERT_FALSE(any.hasValue());
}

TEST(AnyHolderTest, constGet)
{
    AnyHolder<> any;

    const int intValue = 0xDEAD;
    any.set(intValue);

    const AnyHolder<> constAny(any);
    ASSERT_EQ(intValue, constAny.get<int>());
}

} // namespace zserio
