#include <vector>

#include "zserio/OptionalHolder.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace
{

class DummyObject
{
public:
    DummyObject() : m_value(0) {}
    explicit DummyObject(int value) : m_value(value) {}
    int getValue() const { return m_value; }
    void setValue(int value) { m_value = value; }

    bool operator==(const DummyObject& other) const { return m_value == other.m_value; }

private:
    int m_value;
};

} // namespace

class InplaceOptionalHolderTest : public ::testing::Test
{
protected:
};

TEST_F(InplaceOptionalHolderTest, emptyConstructor)
{
    InplaceOptionalHolder<int> optional;
    ASSERT_FALSE(optional.hasValue());
}

TEST_F(InplaceOptionalHolderTest, nullOptConstructor)
{
    InplaceOptionalHolder<int> optional{zserio::NullOpt};
    ASSERT_FALSE(optional.hasValue());
}

TEST_F(InplaceOptionalHolderTest, lvalueConstructor)
{
    std::vector<int> values{1, 2, 3};
    void* origAddress = values.data();
    InplaceOptionalHolder<std::vector<int>> optional{values};
    ASSERT_NE(origAddress, (*optional).data());
    ASSERT_EQ(values, *optional);

    // check initializer list
    InplaceOptionalHolder<std::vector<int>> optionalFromList{{1, 2, 3}};
    std::vector<int> listValues{1, 2, 3};
    ASSERT_EQ(listValues, *optionalFromList);
}

TEST_F(InplaceOptionalHolderTest, rvalueConstructor)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = values.data();
    InplaceOptionalHolder<std::vector<int>> optional{std::move(values)};
    ASSERT_EQ(origAddress, (*optional).data());
    ASSERT_EQ(origValues, *optional);
}

TEST_F(InplaceOptionalHolderTest, copyConstructor)
{
    InplaceOptionalHolder<int> optional;
    ASSERT_THROW(*optional, CppRuntimeException);
    const int intValue = 0xDEAD;
    optional = intValue;

    InplaceOptionalHolder<int> optionalCopy(optional);
    ASSERT_EQ(intValue, *optionalCopy);

    InplaceOptionalHolder<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
    void* origAddress = (*optionalVector).data();
    InplaceOptionalHolder<std::vector<int>> optionalVectorCopy{optionalVector};
    ASSERT_NE(origAddress, (*optionalVectorCopy).data());
    ASSERT_EQ(*optionalVector, *optionalVectorCopy);

    InplaceOptionalHolder<DummyObject> optionalObject(DummyObject{10});
    InplaceOptionalHolder<DummyObject> optionalObjectCopy = optionalObject;
    ASSERT_EQ(optionalObject, optionalObjectCopy);
}

TEST_F(InplaceOptionalHolderTest, copyAssignmentOperator)
{
    InplaceOptionalHolder<int> optional;
    ASSERT_THROW(*optional, CppRuntimeException);
    const int intValue = 0xDEAD;
    optional = intValue;

    InplaceOptionalHolder<int> optionalCopy;
    optionalCopy = optional;
    ASSERT_EQ(intValue, *optionalCopy);

    InplaceOptionalHolder<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
    void* origAddress = (*optionalVector).data();
    InplaceOptionalHolder<std::vector<int>> optionalVectorCopy;
    optionalVectorCopy = optionalVector;
    ASSERT_NE(origAddress, ((*optionalVectorCopy).data()));
    ASSERT_EQ(*optionalVector, *optionalVectorCopy);
}

TEST_F(InplaceOptionalHolderTest, moveConstructor)
{
    InplaceOptionalHolder<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
    std::vector<int> origValues{*optionalVector};
    void* origAddress = (*optionalVector).data();
    InplaceOptionalHolder<std::vector<int>> optionalVectorMoved{std::move(optionalVector)};
    ASSERT_EQ(origAddress, ((*optionalVectorMoved).data()));
    ASSERT_EQ(origValues, *optionalVectorMoved);
}

TEST_F(InplaceOptionalHolderTest, forwardingConstructor)
{
    std::vector<int> src = {0, 13, 42};
    InplaceOptionalHolder<std::vector<int>> optional{ zserio::InPlace, src.begin(), src.end() };
    ASSERT_EQ(src, *optional);
}

TEST_F(InplaceOptionalHolderTest, moveAssignmentOperator)
{
    InplaceOptionalHolder<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
    std::vector<int> origValues{*optionalVector};
    void* origAddress = (*optionalVector).data();
    InplaceOptionalHolder<std::vector<int>> optionalVectorMoved;
    optionalVectorMoved = std::move(optionalVector);
    ASSERT_EQ(origAddress, (*optionalVectorMoved).data());
    ASSERT_EQ(origValues, *optionalVectorMoved);
}

TEST_F(InplaceOptionalHolderTest, lvalueAssignmentOperator)
{
    std::vector<int> values{1, 2, 3};
    void* origAddress = values.data();
    InplaceOptionalHolder<std::vector<int>> optional;
    optional = values;
    ASSERT_NE(origAddress, (*optional).data());
    ASSERT_EQ(values, *optional);
}

TEST_F(InplaceOptionalHolderTest, rvalueAssignmentOperator)
{
    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{values};
    void* origAddress = values.data();
    InplaceOptionalHolder<std::vector<int>> optional;
    optional = std::move(values);
    ASSERT_EQ(origAddress, (*optional).data());
    ASSERT_EQ(origValues, *optional);
}

TEST_F(InplaceOptionalHolderTest, reset)
{
    InplaceOptionalHolder<std::vector<int>> optional{std::vector<int>{1, 2, 3}};
    ASSERT_TRUE(optional.hasValue());
    ASSERT_EQ(1, (*optional)[0]);

    optional.reset();
    ASSERT_FALSE(optional.hasValue());

    optional = std::vector<int>{3, 2, 1};
    ASSERT_TRUE(optional.hasValue());
    ASSERT_EQ(3, (*optional)[0]);

    optional.reset();
    ASSERT_FALSE(optional.hasValue());
}

TEST_F(InplaceOptionalHolderTest, equalOperator)
{
    InplaceOptionalHolder<int> optional1;
    optional1 = 0xDEAD;
    InplaceOptionalHolder<int> optional2;
    optional2 = 0xDEAD;
    InplaceOptionalHolder<int> optional3;
    optional3 = 0xBEEF;
    InplaceOptionalHolder<int> optional4;
    InplaceOptionalHolder<int> optional5;
    InplaceOptionalHolder<int> optional6;

    ASSERT_TRUE(optional1 == optional1);
    ASSERT_TRUE(optional1 == optional2);
    ASSERT_FALSE(optional1 == optional3);
    ASSERT_FALSE(optional1 == optional4);
    ASSERT_TRUE(optional5 == optional6);
    ASSERT_FALSE(optional5 == optional1);
}

TEST_F(InplaceOptionalHolderTest, setGet)
{
    InplaceOptionalHolder<int> optionalInt;
    ASSERT_THROW(*optionalInt, CppRuntimeException);
    const int intValue = 0xDEAD;
    optionalInt = intValue;
    ASSERT_EQ(intValue, *optionalInt);

    InplaceOptionalHolder<float> optionalFloat;
    ASSERT_THROW(*optionalFloat, CppRuntimeException);
    const float floatValue = 3.14F;
    optionalFloat = floatValue;
    ASSERT_EQ(floatValue, optionalFloat.value());

    InplaceOptionalHolder<DummyObject> optionalObject;
    ASSERT_THROW(*optionalObject, CppRuntimeException);
    DummyObject objectValue;
    objectValue.setValue(intValue);
    optionalObject = objectValue;
    const DummyObject& readObjectValue = *optionalObject;
    ASSERT_EQ(intValue, readObjectValue.getValue());
    ASSERT_EQ(intValue, optionalObject->getValue());
}

TEST_F(InplaceOptionalHolderTest, hasValue)
{
    InplaceOptionalHolder<int> optionalInt;
    ASSERT_THROW(*optionalInt, CppRuntimeException);
    ASSERT_EQ(false, optionalInt.hasValue());

    optionalInt = 0xDEAD;
    ASSERT_EQ(true, optionalInt.hasValue());
}

TEST_F(InplaceOptionalHolderTest, constGet)
{
    const int intValue = 0xDEAD;
    InplaceOptionalHolder<int> optional;
    optional = intValue;
    const InplaceOptionalHolder<int> constOptional(optional);
    ASSERT_EQ(intValue, *constOptional);
    ASSERT_EQ(intValue, constOptional.value());

    DummyObject objectValue;
    objectValue.setValue(intValue);
    InplaceOptionalHolder<DummyObject> optionalObject{objectValue};
    ASSERT_EQ(intValue, optionalObject->getValue());
}

} // namespace zserio
