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

        int hashCode() const { return 10; }
        bool operator==(const DummyObject& other) const { return m_value == other.m_value; }

    private:
        int m_value;
    };

    template <template<class> class OPTIONAL_HOLDER>
    void copyConstructorTest()
    {
        OPTIONAL_HOLDER<int> optional;
        EXPECT_THROW(optional.get(), CppRuntimeException);
        const int intValue = 0xDEAD;
        optional.set(intValue);

        OPTIONAL_HOLDER<int> optionalCopy(optional);
        EXPECT_EQ(intValue, optionalCopy.get());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void assignmentOperatorTest()
    {
        OPTIONAL_HOLDER<int> optional;
        EXPECT_THROW(optional.get(), CppRuntimeException);
        const int intValue = 0xDEAD;
        optional.set(intValue);

        OPTIONAL_HOLDER<int> optionalCopy = optional;
        EXPECT_EQ(intValue, optionalCopy.get());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void equalOperatorTest()
    {
        OPTIONAL_HOLDER<int> optional1;
        optional1.set(0xDEAD);
        OPTIONAL_HOLDER<int> optional2;
        optional2.set(0xDEAD);
        OPTIONAL_HOLDER<int> optional3;
        optional3.set(0xBEEF);
        OPTIONAL_HOLDER<int> optional4;

        EXPECT_EQ(true, optional1 == optional2);
        EXPECT_EQ(false, optional1 == optional3);
        EXPECT_EQ(false, optional1 == optional4);
    }

    template <template<class> class OPTIONAL_HOLDER>
    void setGetTest()
    {
        OPTIONAL_HOLDER<int> optionalInt;
        EXPECT_THROW(optionalInt.get(), CppRuntimeException);
        const int intValue = 0xDEAD;
        optionalInt.set(intValue);
        EXPECT_EQ(intValue, optionalInt.get());

        OPTIONAL_HOLDER<float> optionalFloat;
        EXPECT_THROW(optionalFloat.get(), CppRuntimeException);
        const float floatValue = 3.14;
        optionalFloat.set(floatValue);
        EXPECT_EQ(floatValue, optionalFloat.get());

        OPTIONAL_HOLDER<DummyObject> optionalObject;
        EXPECT_THROW(optionalObject.get(), CppRuntimeException);
        DummyObject objectValue;
        objectValue.setValue(intValue);
        optionalObject.set(objectValue);
        const DummyObject& readObjectValueConst = optionalObject.get();
        EXPECT_EQ(intValue, readObjectValueConst.getValue());
        DummyObject& readObjectValue = optionalObject.get();
        EXPECT_EQ(intValue, readObjectValue.getValue());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void pointerSetGetTest()
    {
        OPTIONAL_HOLDER<int> optionalInt;
        EXPECT_THROW(optionalInt.get(), CppRuntimeException);
        const int intValue = 0xDEAD;
        optionalInt.reset(new (optionalInt.getResetStorage()) int(intValue));
        EXPECT_EQ(intValue, optionalInt.get());

        OPTIONAL_HOLDER<float> optionalFloat;
        EXPECT_THROW(optionalFloat.get(), CppRuntimeException);
        const float floatValue = 3.14;
        optionalFloat.reset(new (optionalFloat.getResetStorage()) float(floatValue));
        EXPECT_EQ(floatValue, optionalFloat.get());

        OPTIONAL_HOLDER<DummyObject> optionalObject;
        EXPECT_THROW(optionalObject.get(), CppRuntimeException);
        optionalObject.reset(new (optionalObject.getResetStorage()) DummyObject(intValue));
        const DummyObject& readObjectValueConst = optionalObject.get();
        EXPECT_EQ(intValue, readObjectValueConst.getValue());
        DummyObject& readObjectValue = optionalObject.get();
        EXPECT_EQ(intValue, readObjectValue.getValue());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void isSetTest()
    {
        OPTIONAL_HOLDER<int> optionalInt;
        EXPECT_THROW(optionalInt.get(), CppRuntimeException);
        EXPECT_EQ(false, optionalInt.isSet());

        optionalInt.set(0xDEAD);
        EXPECT_EQ(true, optionalInt.isSet());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void constGetTest()
    {
        const int intValue = 0xDEAD;
        OPTIONAL_HOLDER<int> optional;
        optional.set(intValue);
        const OPTIONAL_HOLDER<int> constOptional(optional);
        EXPECT_EQ(intValue, constOptional.get());
    }

    template <template<class> class OPTIONAL_HOLDER>
    void hashCodeTest()
    {
        const int intValue = 10;
        OPTIONAL_HOLDER<int> optional;
        EXPECT_EQ(HASH_PRIME_NUMBER * HASH_SEED, optional.hashCode());
        optional.set(intValue);
        EXPECT_EQ(HASH_PRIME_NUMBER * HASH_SEED + intValue, optional.hashCode());
    }
};

TEST_F(OptionalHolderTest, InPlaceCopyConstructor)
{
    copyConstructorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceAssignmentOperator)
{
    assignmentOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceEqualOperator)
{
    equalOperatorTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceSetGet)
{
    setGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlacePointerSetGet)
{
    pointerSetGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceIsSet)
{
    isSetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceConstGet)
{
    constGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, InPlaceHashCode)
{
    constGetTest<InPlaceOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapCopyConstructor)
{
    copyConstructorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapAssignmentOperator)
{
    assignmentOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapEqualOperator)
{
    equalOperatorTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapSetGet)
{
    setGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapPointerSetGet)
{
    pointerSetGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapIsSet)
{
    isSetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapConstGet)
{
    constGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, HeapHashCode)
{
    constGetTest<HeapOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedCopyConstructor)
{
    copyConstructorTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedAssignmentOperator)
{
    assignmentOperatorTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedEqualOperator)
{
    equalOperatorTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedSetGet)
{
    setGetTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedPointerSetGet)
{
    pointerSetGetTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedIsSet)
{
    isSetTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedConstGet)
{
    constGetTest<OptimizedOptionalHolder>();
}

TEST_F(OptionalHolderTest, OptimizedHashCode)
{
    constGetTest<OptimizedOptionalHolder>();
}

} // namespace zserio
