#include <vector>

#include "zserio/OptionalHolder.h"

#include "gtest/gtest.h"

namespace zserio
{

class OptionalHolderTest : public ::testing::Test
{
protected:
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

    template <template <class> class OPTIONAL_HOLDER>
    void emptyConstructorTest()
    {
        OPTIONAL_HOLDER<int> optional;
        ASSERT_FALSE(optional.hasValue());
    }

    template <template <class> class OPTIONAL_HOLDER>
    void nullOptConstructorTest()
    {
        OPTIONAL_HOLDER<int> optional{zserio::NullOpt};
        ASSERT_FALSE(optional.hasValue());
    }

    template <template <class> class OPTIONAL_HOLDER>
    void lvalueConstructorTest()
    {
        std::vector<int> values{1, 2, 3};
        void* origAddress = &values[0];
        OPTIONAL_HOLDER<std::vector<int>> optional{values};
        ASSERT_NE(origAddress, &(*optional)[0]);
        ASSERT_EQ(values, *optional);

        // check initializer list
        OPTIONAL_HOLDER<std::vector<int>> optionalFromList{{1, 2, 3}};
        std::vector<int> listValues{1, 2, 3};
        ASSERT_EQ(listValues, *optionalFromList);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void rvalueConstructorTest()
    {
        std::vector<int> values{1, 2, 3};
        std::vector<int> origValues{values};
        void* origAddress = &values[0];
        OPTIONAL_HOLDER<std::vector<int>> optional{std::move(values)};
        ASSERT_EQ(origAddress, &(*optional)[0]);
        ASSERT_EQ(origValues, *optional);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void copyConstructorTest()
    {
        OPTIONAL_HOLDER<int> optional;
        ASSERT_THROW(*optional, CppRuntimeException);
        const int intValue = 0xDEAD;
        optional = intValue;

        OPTIONAL_HOLDER<int> optionalCopy(optional);
        ASSERT_EQ(intValue, *optionalCopy);

        OPTIONAL_HOLDER<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
        void* origAddress = &(*optionalVector)[0];
        OPTIONAL_HOLDER<std::vector<int>> optionalVectorCopy{optionalVector};
        ASSERT_NE(origAddress, &(*optionalVectorCopy)[0]);
        ASSERT_EQ(*optionalVector, *optionalVectorCopy);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void copyAssignmentOperatorTest()
    {
        OPTIONAL_HOLDER<int> optional;
        ASSERT_THROW(*optional, CppRuntimeException);
        const int intValue = 0xDEAD;
        optional = intValue;

        OPTIONAL_HOLDER<int> optionalCopy;
        optionalCopy = optional;
        ASSERT_EQ(intValue, *optionalCopy);

        OPTIONAL_HOLDER<std::vector<int>> optionalVector{std::vector<int>{1, 2, 3}};
        void* origAddress = &((*optionalVector)[0]);
        OPTIONAL_HOLDER<std::vector<int>> optionalVectorCopy;
        optionalVectorCopy = optionalVector;
        ASSERT_NE(origAddress, &((*optionalVectorCopy)[0]));
        ASSERT_EQ(*optionalVector, *optionalVectorCopy);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void moveConstructorTest()
    {
        OPTIONAL_HOLDER<std::vector<int>> optionalVector{std::vector<int>{ 1, 2, 3 }};
        std::vector<int> origValues{*optionalVector};
        void* origAddress = &((*optionalVector)[0]);
        OPTIONAL_HOLDER<std::vector<int>> optionalVectorMoved{std::move(optionalVector)};
        ASSERT_EQ(origAddress, &((*optionalVectorMoved)[0]));
        ASSERT_EQ(origValues, *optionalVectorMoved);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void moveAssignmentOperatorTest()
    {
        OPTIONAL_HOLDER<std::vector<int>> optionalVector{std::vector<int>{ 1, 2, 3 }};
        std::vector<int> origValues{*optionalVector};
        void* origAddress = &(*optionalVector)[0];
        OPTIONAL_HOLDER<std::vector<int>> optionalVectorMoved;
        optionalVectorMoved = std::move(optionalVector);
        ASSERT_EQ(origAddress, &(*optionalVectorMoved)[0]);
        ASSERT_EQ(origValues, *optionalVectorMoved);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void lvalueAssignmentOperatorTest()
    {
        std::vector<int> values{ 1, 2, 3 };
        void* origAddress = &values[0];
        OPTIONAL_HOLDER<std::vector<int>> optional;
        optional = values;
        ASSERT_NE(origAddress, &(*optional)[0]);
        ASSERT_EQ(values, *optional);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void rvalueAssignmentOperatorTest()
    {
        std::vector<int> values{ 1, 2, 3 };
        std::vector<int> origValues{values};
        void* origAddress = &values[0];
        OPTIONAL_HOLDER<std::vector<int>> optional;
        optional = std::move(values);
        ASSERT_EQ(origAddress, &(*optional)[0]);
        ASSERT_EQ(origValues, *optional);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void resetTest()
    {
        OPTIONAL_HOLDER<std::vector<int>> optional{std::vector<int>{1, 2, 3}};
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

    template <template <class> class OPTIONAL_HOLDER>
    void equalOperatorTest()
    {
        OPTIONAL_HOLDER<int> optional1;
        optional1 = 0xDEAD;
        OPTIONAL_HOLDER<int> optional2;
        optional2 = 0xDEAD;
        OPTIONAL_HOLDER<int> optional3;
        optional3 = 0xBEEF;
        OPTIONAL_HOLDER<int> optional4;

        ASSERT_TRUE(optional1 == optional2);
        ASSERT_FALSE(optional1 == optional3);
        ASSERT_FALSE(optional1 == optional4);
    }

    template <template <class> class OPTIONAL_HOLDER>
    void setGetTest()
    {
        OPTIONAL_HOLDER<int> optionalInt;
        ASSERT_THROW(*optionalInt, CppRuntimeException);
        const int intValue = 0xDEAD;
        optionalInt = intValue;
        ASSERT_EQ(intValue, *optionalInt);

        OPTIONAL_HOLDER<float> optionalFloat;
        ASSERT_THROW(*optionalFloat, CppRuntimeException);
        const float floatValue = 3.14f;
        optionalFloat = floatValue;
        ASSERT_EQ(floatValue, optionalFloat.value());

        OPTIONAL_HOLDER<DummyObject> optionalObject;
        ASSERT_THROW(*optionalObject, CppRuntimeException);
        DummyObject objectValue;
        objectValue.setValue(intValue);
        optionalObject = objectValue;
        const DummyObject& readObjectValue = *optionalObject;
        ASSERT_EQ(intValue, readObjectValue.getValue());
        ASSERT_EQ(intValue, optionalObject->getValue());
    }

    template <template <class> class OPTIONAL_HOLDER>
    void hasValueTest()
    {
        OPTIONAL_HOLDER<int> optionalInt;
        ASSERT_THROW(*optionalInt, CppRuntimeException);
        ASSERT_EQ(false, optionalInt.hasValue());

        optionalInt = 0xDEAD;
        ASSERT_EQ(true, optionalInt.hasValue());
    }

    template <template <class> class OPTIONAL_HOLDER>
    void constGetTest()
    {
        const int intValue = 0xDEAD;
        OPTIONAL_HOLDER<int> optional;
        optional = intValue;
        const OPTIONAL_HOLDER<int> constOptional(optional);
        ASSERT_EQ(intValue, *constOptional);
        ASSERT_EQ(intValue, constOptional.value());

        DummyObject objectValue;
        objectValue.setValue(intValue);
        OPTIONAL_HOLDER<DummyObject> optionalObject{objectValue};
        ASSERT_EQ(intValue, optionalObject->getValue());
    }
};

TEST_F(OptionalHolderTest, inPlaceEmptyConstructor)
{
    emptyConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceNullOptConstructor)
{
    nullOptConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceLvalueConstructor)
{
    lvalueConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceRvalueConstructor)
{
    rvalueConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceCopyConstructor)
{
    copyConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceCopyAssignmentOperator)
{
    copyAssignmentOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceMoveConstructor)
{
    moveConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceMoveAssignmentOperator)
{
    moveAssignmentOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceLvalueAssignmentOperator)
{
    lvalueAssignmentOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceRvalueAssignmentOperator)
{
    rvalueAssignmentOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceReset)
{
    resetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceEqualOperator)
{
    equalOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceSetGet)
{
    setGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceHasValue)
{
    hasValueTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceConstGet)
{
    constGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, inPlaceHashCode)
{
    constGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapEmptyConstructor)
{
    emptyConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapNullOptConstructor)
{
    nullOptConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapLvalueConstructor)
{
    lvalueConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapRvalueConstructor)
{
    rvalueConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapCopyConstructor)
{
    copyConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapCopyAssignmentOperator)
{
    copyAssignmentOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapMoveConstructor)
{
    moveConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapMoveAssignmentOperator)
{
    moveAssignmentOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapLvalueAssignmentOperator)
{
    lvalueAssignmentOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapRvalueAssignmentOperator)
{
    rvalueAssignmentOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapReset)
{
    resetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapEqualOperator)
{
    equalOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapSetGet)
{
    setGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapHasValue)
{
    hasValueTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapConstGet)
{
    constGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, heapHashCode)
{
    constGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedEmptyConstructor)
{
    emptyConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedNullOptConstructor)
{
    nullOptConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedLvalueConstructor)
{
    lvalueConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedRvalueConstructor)
{
    rvalueConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedCopyConstructor)
{
    copyConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedCopyAssignmentOperator)
{
    copyAssignmentOperatorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedMoveConstructor)
{
    moveConstructorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedMoveAssignmentOperator)
{
    moveAssignmentOperatorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedLvalueAssignmentOperator)
{
    lvalueAssignmentOperatorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedRvalueAssignmentOperator)
{
    rvalueAssignmentOperatorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedReset)
{
    resetTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedEqualOperator)
{
    equalOperatorTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedSetGet)
{
    setGetTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedHasValue)
{
    hasValueTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedConstGet)
{
    constGetTest<OptionalHolder>();
}

TEST_F(OptionalHolderTest, optimizedHashCode)
{
    constGetTest<OptionalHolder>();
}

} // namespace zserio
