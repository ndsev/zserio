#include <vector>

#include "zserio/AnyHolder.h"

#include "gtest/gtest.h"

namespace zserio
{

TEST(AnyHolderTest, CopyConstructor)
{
    AnyHolder any;
    const int intValue = 0xDEAD;
    any.set(intValue);

    AnyHolder anyCopy(any);
    EXPECT_EQ(intValue, anyCopy.get<int>());
    EXPECT_THROW(anyCopy.get<float>(), CppRuntimeException);
}

TEST(AnyHolderTest, AssignmentOperator)
{
    AnyHolder any;
    const int intValue = 0xDEAD;
    any.set(intValue);

    AnyHolder anyCopy = any;
    EXPECT_EQ(intValue, anyCopy.get<int>());
    EXPECT_THROW(anyCopy.get<float>(), CppRuntimeException);
}

TEST(AnyHolderTest, EqualOperator)
{
    AnyHolder any1;
    const int intValue1 = 0xDEAD;
    any1.set(intValue1);

    AnyHolder any2;
    const int intValue2 = 0xDEAD;
    any2.set(intValue2);

    AnyHolder any3;
    const int intValue3 = 0xBEEF;
    any3.set(intValue3);

    AnyHolder any4;
    const float floatValue = 10.0;
    any4.set(floatValue);

    AnyHolder any5;
    AnyHolder any6;

    EXPECT_TRUE(any1 == any2);
    EXPECT_FALSE(any1 == any3);
    EXPECT_FALSE(any1 == any4);
    EXPECT_FALSE(any1 == any5);
    EXPECT_TRUE(any5 == any6);
}

class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    int getValue() const { return m_value; }
    void setValue(int value) { m_value = value; }

    int hashCode() const { return 10; }
    bool operator==(const DummyObject& other) const { return m_value == other.m_value; }

private:
    int m_value;
};

TEST(AnyHolderTest, UnitializedGet)
{
    AnyHolder any;
    ASSERT_THROW(any.get<int>(), zserio::CppRuntimeException);
}

TEST(AnyHolderTest, SetGet)
{
    AnyHolder any;

    const int intValue = 0xDEAD;
    any.set(intValue);
    EXPECT_EQ(intValue, any.get<int>());
    EXPECT_THROW(any.get<float>(), CppRuntimeException);

    const float floatValue = 3.14f;
    any.set(floatValue);
    EXPECT_THROW(any.get<int>(), CppRuntimeException);
    EXPECT_EQ(floatValue, any.get<float>());

    DummyObject objectValue;
    objectValue.setValue(0xDEAD);
    any.set(objectValue);
    EXPECT_THROW(any.get<float>(), CppRuntimeException);
    const DummyObject& readObjectValueConst = any.get<DummyObject>();
    EXPECT_EQ(objectValue.getValue(), readObjectValueConst.getValue());
    DummyObject& readObjectValue = any.get<DummyObject>();
    EXPECT_EQ(objectValue.getValue(), readObjectValue.getValue());
}

TEST(AnyHolderTest, PointerSetGet)
{
    AnyHolder any;

    const int intValue = 0xDEAD;
    any.reset(new (any.getResetStorage<int>()) int(intValue));
    EXPECT_EQ(intValue, any.get<int>());
    EXPECT_THROW(any.get<float>(), CppRuntimeException);

    const float floatValue = 3.14f;
    any.reset(new (any.getResetStorage<float>()) float(floatValue));
    EXPECT_THROW(any.get<int>(), CppRuntimeException);
    EXPECT_EQ(floatValue, any.get<float>());

    DummyObject objectValue;
    objectValue.setValue(0xDEAD);
    any.reset(new (any.getResetStorage<DummyObject>()) DummyObject(objectValue));
    EXPECT_THROW(any.get<float>(), CppRuntimeException);
    const DummyObject& readObjectValueConst = any.get<DummyObject>();
    EXPECT_EQ(objectValue.getValue(), readObjectValueConst.getValue());
    DummyObject& readObjectValue = any.get<DummyObject>();
    EXPECT_EQ(objectValue.getValue(), readObjectValue.getValue());
}

TEST(AnyHolderTest, IsType)
{
    AnyHolder any;
    EXPECT_FALSE(any.isType<int>());

    const int intValue = 0xDEAD;
    any.set(intValue);
    EXPECT_TRUE(any.isType<int>());

    const float floatValue = 3.14f;
    any.set(floatValue);
    EXPECT_TRUE(any.isType<float>());
    EXPECT_FALSE(any.isType<int>());
}

TEST(AnyHolderTest, IsSet)
{
    AnyHolder any;
    EXPECT_FALSE(any.isSet());

    const int intValue = 0xDEAD;
    any.set(intValue);
    EXPECT_TRUE(any.isSet());

    any.reset<int>();
    EXPECT_FALSE(any.isSet());
}

TEST(AnyHolderTest, ConstGet)
{
    AnyHolder any;

    const int intValue = 0xDEAD;
    any.set(intValue);

    const AnyHolder constAny(any);
    EXPECT_EQ(intValue, constAny.get<int>());
}

TEST(AnyHolderTest, HashCode)
{
    AnyHolder any;
    const int intValue = 10;
    EXPECT_EQ(HASH_PRIME_NUMBER * HASH_SEED, any.hashCode());
    any.set(intValue);
    EXPECT_EQ(HASH_PRIME_NUMBER * HASH_SEED + intValue, any.hashCode());
}

} // namespace zserio
