#include "zserio/OptionalHolder.h"
#include "zserio/pmr/PolymorphicAllocator.h"

#include "TrackingAllocator.h"

#include "gtest/gtest.h"

namespace zserio
{

namespace
{

class DummyObject
{
public:
    DummyObject() : m_value(0), m_isNoInit(false) {}
    explicit DummyObject(int value) : m_value(value), m_isNoInit(false) {}
    explicit DummyObject(NoInitT, const DummyObject& other) : m_value(other.m_value), m_isNoInit(true) {}
    int getValue() const { return m_value; }
    void setValue(int value) { m_value = value; }

    bool isNoInit() const { return m_isNoInit; }

    bool operator==(const DummyObject& other) const { return m_value == other.m_value; }

private:
    int m_value;
    bool m_isNoInit;
};

} // namespace

class HeapOptionalHolderTest : public ::testing::Test
{
protected:
};

TEST_F(HeapOptionalHolderTest, emptyConstructor)
{
    TrackingAllocator<int> alloc;
    HeapOptionalHolder<int, TrackingAllocator<int>> optional{alloc};
    ASSERT_EQ(alloc, optional.get_allocator());
}

TEST_F(HeapOptionalHolderTest, nullOptConstructor)
{
    {
        NullOptType nullOpt{int()};
        TrackingAllocator<int> alloc;
        HeapOptionalHolder<int, TrackingAllocator<int>> optional{nullOpt, alloc};
        ASSERT_EQ(alloc, optional.get_allocator());
    }

    {
        TrackingAllocator<int> alloc;
        HeapOptionalHolder<int, TrackingAllocator<int>> optional{NullOpt, alloc};
        ASSERT_EQ(alloc, optional.get_allocator());
    }
}

TEST_F(HeapOptionalHolderTest, lvalueConstructor)
{
    TrackingAllocator<std::vector<int>> alloc;

    std::vector<int> values{1, 2, 3};
    void* origAddress = values.data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional{values, alloc};
    ASSERT_NE(origAddress, (*optional).data());
    ASSERT_EQ(values, *optional);

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalFromList{
            {1, 2, 3}, alloc};
    std::vector<int> listValues{1, 2, 3};
    ASSERT_EQ(listValues, *optionalFromList);

    ASSERT_GE(alloc.numAllocs(), 2U);
}

TEST_F(HeapOptionalHolderTest, rvalueConstructor)
{
    TrackingAllocator<std::vector<int>> alloc;

    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{ values };
    void* origAddress = values.data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional{
            std::move(values), alloc};
    ASSERT_EQ(origAddress, (*optional).data());
    ASSERT_EQ(origValues, *optional);

    ASSERT_GE(alloc.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, forwardingConstructor)
{
    TrackingAllocator<std::vector<int>> alloc;

    std::vector<int> src = {0, 13, 42};
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional{
            alloc, src.begin(), src.end()};
    ASSERT_EQ(src, *optional);

    ASSERT_GE(alloc.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, copyConstructor)
{
    TrackingAllocator<int> alloc;
    TrackingAllocator<std::vector<int>> allocVec = alloc;

    HeapOptionalHolder<int, TrackingAllocator<int>> optional(alloc);
    ASSERT_THROW(*optional, CppRuntimeException);
    const int intValue = 0xDEAD;
    optional = intValue;

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalCopy(optional);
    ASSERT_EQ(intValue, *optionalCopy);
    ASSERT_EQ(optional.get_allocator(), optionalCopy.get_allocator());

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObject(DummyObject{10});
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectCopy = optionalObject;
    ASSERT_EQ(optionalObject, optionalObjectCopy);
    ASSERT_EQ(optionalObject.get_allocator(), optionalObjectCopy.get_allocator());

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVector{
            std::vector<int>{1, 2, 3}, allocVec};
    void* origAddress = (*optionalVector).data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVectorCopy{
            optionalVector};
    ASSERT_NE(origAddress, (*optionalVectorCopy).data());
    ASSERT_EQ(*optionalVector, *optionalVectorCopy);
    ASSERT_EQ(optionalVector.get_allocator(), optionalVectorCopy.get_allocator());

    ASSERT_GE(alloc.numAllocs(), 2U);

    TrackingAllocatorNonProp<int> allocNp;
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNp(allocNp);
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNpCopy = optionalNp;
    ASSERT_NE(optionalNp.get_allocator(), optionalNpCopy.get_allocator());
}

TEST_F(HeapOptionalHolderTest, copyConstructorNoInit)
{
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObject;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObjectCopy{NoInit,
        optionalEmptyObject};
    ASSERT_EQ(optionalEmptyObject, optionalEmptyObjectCopy);

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObject(DummyObject{10});
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectCopy{NoInit, optionalObject};
    ASSERT_EQ(optionalObject, optionalObjectCopy);
    ASSERT_FALSE(optionalObject->isNoInit());
    ASSERT_TRUE(optionalObjectCopy->isNoInit());
}

TEST_F(HeapOptionalHolderTest, copyConstructorAllocator)
{
    TrackingAllocator<int> alloc1;
    TrackingAllocator<std::vector<int>> allocVec1 = alloc1;
    TrackingAllocator<int> alloc2;
    TrackingAllocator<std::vector<int>> allocVec2 = alloc2;

    HeapOptionalHolder<int, TrackingAllocator<int>> optional(alloc1);
    ASSERT_THROW(*optional, CppRuntimeException);

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalCopy(optional, alloc2);
    ASSERT_NE(optional.get_allocator(), optionalCopy.get_allocator());
    ASSERT_EQ(optional.get_allocator(), alloc1);
    ASSERT_EQ(optionalCopy.get_allocator(), alloc2);

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVector{
            std::vector<int>{1, 2, 3}, allocVec1};
    void* origAddress = (*optionalVector).data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVectorCopy{
            optionalVector, allocVec2};
    ASSERT_NE(origAddress, (*optionalVectorCopy).data());
    ASSERT_EQ(*optionalVector, *optionalVectorCopy);
    ASSERT_NE(optionalVector.get_allocator(), optionalVectorCopy.get_allocator());
    ASSERT_EQ(optionalVector.get_allocator(), allocVec1);
    ASSERT_EQ(optionalVectorCopy.get_allocator(), allocVec2);

    ASSERT_GE(alloc1.numAllocs(), 1U);
    ASSERT_GE(alloc2.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, moveConstructor)
{
    TrackingAllocator<int> alloc;

    HeapOptionalHolder<int, TrackingAllocator<int>> optional(13, alloc);
    HeapOptionalHolder<int, TrackingAllocator<int>> optionalMoved(std::move(optional));

    ASSERT_EQ(*optionalMoved, 13);
    ASSERT_EQ(optionalMoved.get_allocator(), alloc);
    ASSERT_FALSE(optional.hasValue());
    ASSERT_EQ(alloc.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, moveConstructorNoInit)
{
    TrackingAllocator<DummyObject> allocEmpty;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObject(allocEmpty);
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObjectMoved{NoInit,
        std::move(optionalEmptyObject)};
    ASSERT_FALSE(optionalEmptyObjectMoved.hasValue());

    TrackingAllocator<DummyObject> alloc;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObject(DummyObject{10}, alloc);
    ASSERT_EQ(10, optionalObject->getValue());
    ASSERT_FALSE(optionalObject->isNoInit());

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectMoved{NoInit,
        std::move(optionalObject)};
    ASSERT_EQ(10, optionalObjectMoved->getValue());
    // memory with object has been moved in optional holder, object move constructor has not been called
    ASSERT_FALSE(optionalObjectMoved->isNoInit());
}

TEST_F(HeapOptionalHolderTest, moveConstructorAllocator)
{
    TrackingAllocator<int> alloc;
    TrackingAllocator<int> alloc2;

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalDiff(13, alloc);
    HeapOptionalHolder<int, TrackingAllocator<int>> optionalDiffMoved(std::move(optionalDiff), alloc2);
    ASSERT_EQ(*optionalDiffMoved, 13);
    ASSERT_EQ(optionalDiffMoved.get_allocator(), alloc2);

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalSame(13, alloc);
    HeapOptionalHolder<int, TrackingAllocator<int>> optionalSameMoved(std::move(optionalSame), alloc);
    ASSERT_EQ(*optionalSameMoved, 13);
    ASSERT_EQ(optionalSameMoved.get_allocator(), alloc);

    HeapOptionalHolder<int, TrackingAllocator<int>> emptyOptional(alloc);
    HeapOptionalHolder<int, TrackingAllocator<int>> emptyMoved(std::move(emptyOptional), alloc2);
    ASSERT_FALSE(emptyOptional.hasValue());
    ASSERT_FALSE(emptyMoved.hasValue());
    ASSERT_EQ(emptyOptional.get_allocator(), alloc);
    ASSERT_EQ(emptyMoved.get_allocator(), alloc2);

    ASSERT_EQ(alloc.numAllocs(), 2U);
    ASSERT_EQ(alloc2.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, moveValueConstructorAllocatorNoInit)
{
    TrackingAllocator<DummyObject> alloc;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectMoved{NoInit, DummyObject{10},
                alloc};
    ASSERT_EQ(10, optionalObjectMoved->getValue());
    ASSERT_TRUE(optionalObjectMoved->isNoInit());
    ASSERT_EQ(optionalObjectMoved.get_allocator(), alloc);
    ASSERT_GE(alloc.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, copyAssignmentOperator)
{
    TrackingAllocator<int> alloc;
    TrackingAllocator<std::vector<int>> allocVec = alloc;

    HeapOptionalHolder<int, TrackingAllocator<int>> optional(alloc);
    ASSERT_THROW(*optional, CppRuntimeException);
    const int intValue = 0xDEAD;
    optional = intValue;

    HeapOptionalHolder<int, TrackingAllocator<int>>& optionalRef(optional);
    optionalRef = optional;
    ASSERT_EQ(intValue, *optionalRef);

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalCopy;
    optionalCopy = optional;
    ASSERT_EQ(intValue, *optionalCopy);
    ASSERT_EQ(optional.get_allocator(), optionalCopy.get_allocator());
    ASSERT_EQ(optional.get_allocator(), alloc);

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVector{
            std::vector<int>{1, 2, 3}, allocVec};
    void* origAddress = (*optionalVector).data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVectorCopy;
    optionalVectorCopy = optionalVector;
    ASSERT_NE(origAddress, (*optionalVectorCopy).data());
    ASSERT_EQ(*optionalVector, *optionalVectorCopy);
    ASSERT_EQ(optionalVector.get_allocator(), optionalVectorCopy.get_allocator());
    ASSERT_EQ(optionalVector.get_allocator(), allocVec);

    ASSERT_GE(alloc.numAllocs(), 4U);

    TrackingAllocatorNonProp<int> allocNp;
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNp(13, allocNp);
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNpCopy;
    optionalNpCopy = optionalNp;
    ASSERT_NE(optionalNp.get_allocator(), optionalNpCopy.get_allocator());

    HeapOptionalHolder<int> optionalInt(123);
    const HeapOptionalHolder<int>& optionalIntRef = optionalInt;
    const HeapOptionalHolder<int> &optionalIntRef2 = (optionalInt = optionalIntRef);
    ASSERT_EQ(123, *optionalInt);
    ASSERT_EQ(optionalInt, optionalIntRef2);
}

TEST_F(HeapOptionalHolderTest, copyAssignmentOperatorNoInit)
{
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObject;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObjectCopy;
    optionalEmptyObjectCopy.assign(NoInit, optionalEmptyObject);
    ASSERT_EQ(optionalEmptyObject, optionalEmptyObjectCopy);

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObject(DummyObject{10});
    optionalObject.assign(NoInit, optionalObject);

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectCopy;
    optionalObjectCopy.assign(NoInit, optionalObject);
    ASSERT_EQ(optionalObject, optionalObjectCopy);
    ASSERT_FALSE(optionalObject->isNoInit());
    ASSERT_TRUE(optionalObjectCopy->isNoInit());
}

TEST_F(HeapOptionalHolderTest, moveAssignmentOperator)
{
    TrackingAllocator<std::vector<int>> allocVec;

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVector{
            std::vector<int>{1, 2, 3}, allocVec};
    std::vector<int> origValues{*optionalVector};
    void* origAddress = (*optionalVector).data();

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVectorCopy =
            optionalVector;
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>>& optionalVectorRef =
            optionalVectorCopy;
    optionalVectorRef = std::move(optionalVectorCopy);
    ASSERT_TRUE(optionalVectorRef.hasValue());

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optionalVectorMoved;
    optionalVectorMoved = std::move(optionalVector);
    ASSERT_EQ(origAddress, (*optionalVectorMoved).data());
    ASSERT_EQ(origValues, *optionalVectorMoved);
    ASSERT_EQ(optionalVector.get_allocator(), optionalVectorMoved.get_allocator());
    ASSERT_EQ(optionalVector.get_allocator(), allocVec);

    ASSERT_GE(allocVec.numAllocs(), 1U);

    TrackingAllocatorNonProp<int> allocNp;
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNp(13, allocNp);
    HeapOptionalHolder<int, TrackingAllocatorNonProp<int>> optionalNpMoved;
    optionalNpMoved = std::move(optionalNp);
    ASSERT_TRUE(optionalNp.hasValue());
    ASSERT_TRUE(optionalNpMoved.hasValue());
    ASSERT_NE(optionalNp.get_allocator(), optionalNpMoved.get_allocator());

    HeapOptionalHolder<int> optionalInt(123);
    HeapOptionalHolder<int>& optionalIntRef = optionalInt;
    const HeapOptionalHolder<int> &optionalIntRef2 = (optionalInt = std::move(optionalIntRef));
    ASSERT_EQ(123, *optionalInt);
    ASSERT_EQ(optionalInt, optionalIntRef2);
}

TEST_F(HeapOptionalHolderTest, moveAssignmentOperatorNoInit)
{
    TrackingAllocator<DummyObject> allocEmpty;
    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObject(allocEmpty);

    HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalEmptyObjectMoved;
    optionalEmptyObjectMoved.assign(NoInit, std::move(optionalEmptyObject));
    ASSERT_FALSE(optionalEmptyObjectMoved.hasValue());

    {
        TrackingAllocator<DummyObject> alloc;
        HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObject(DummyObject{10}, alloc);
        ASSERT_EQ(10, optionalObject->getValue());
        ASSERT_FALSE(optionalObject->isNoInit());
        optionalObject.assign(NoInit, std::move(optionalObject));

        HeapOptionalHolder<DummyObject, TrackingAllocator<DummyObject>> optionalObjectMoved;
        optionalObjectMoved.assign(NoInit, std::move(optionalObject));
        ASSERT_EQ(10, optionalObjectMoved->getValue());
        // memory with object has been moved in optional holder, object move constructor has not been called
        ASSERT_FALSE(optionalObjectMoved->isNoInit());
    }

    {
        TrackingAllocatorNonProp<DummyObject> alloc;
        HeapOptionalHolder<DummyObject, TrackingAllocatorNonProp<DummyObject>> optionalObject(DummyObject{10},
                alloc);
        ASSERT_EQ(10, optionalObject->getValue());
        ASSERT_FALSE(optionalObject->isNoInit());
        optionalObject.assign(NoInit, std::move(optionalObject));

        HeapOptionalHolder<DummyObject, TrackingAllocatorNonProp<DummyObject>> optionalObjectMoved;
        optionalObjectMoved.assign(NoInit, std::move(optionalObject));
        ASSERT_EQ(10, optionalObjectMoved->getValue());
        ASSERT_TRUE(optionalObjectMoved->isNoInit());
    }
}

TEST_F(HeapOptionalHolderTest, lvalueAssignmentOperator)
{
    TrackingAllocator<std::vector<int>> allocVec;

    std::vector<int> values{1, 2, 3};
    void* origAddress = values.data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional(allocVec);
    optional = values;
    ASSERT_NE(origAddress, (*optional).data());
    ASSERT_EQ(values, *optional);
    ASSERT_EQ(optional.get_allocator(), allocVec);

    ASSERT_GE(allocVec.numAllocs(), 1U);

    TrackingAllocatorNonProp<std::vector<int>> allocVecNp;
    HeapOptionalHolder<std::vector<int>, TrackingAllocatorNonProp<std::vector<int>>> optionalNp(allocVecNp);
    optionalNp = values;
    ASSERT_NE(origAddress, (*optionalNp).data());
    ASSERT_EQ(values, *optionalNp);
    ASSERT_EQ(optionalNp.get_allocator(), allocVecNp);

    ASSERT_GE(allocVecNp.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, rvalueAssignmentOperator)
{
    TrackingAllocator<std::vector<int>> allocVec;

    std::vector<int> values{1, 2, 3};
    std::vector<int> origValues{ values };
    void* origAddress = values.data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional(allocVec);
    optional = std::move(values);
    ASSERT_EQ(origAddress, (*optional).data());
    ASSERT_EQ(origValues, *optional);
    ASSERT_EQ(optional.get_allocator(), allocVec);

    ASSERT_GE(allocVec.numAllocs(), 1U);

    TrackingAllocatorNonProp<std::vector<int>> allocVecNp;
    values = origValues;
    origAddress = values.data();
    HeapOptionalHolder<std::vector<int>, TrackingAllocatorNonProp<std::vector<int>>> optionalNp(allocVecNp);
    optionalNp = std::move(values);
    ASSERT_EQ(origAddress, (*optionalNp).data());
    ASSERT_EQ(origValues, *optionalNp);
    ASSERT_EQ(optionalNp.get_allocator(), allocVecNp);

    ASSERT_GE(allocVecNp.numAllocs(), 1U);
}

TEST_F(HeapOptionalHolderTest, reset)
{
    TrackingAllocator<std::vector<int>> allocVec;

    HeapOptionalHolder<std::vector<int>, TrackingAllocator<std::vector<int>>> optional{
            std::vector<int>{1, 2, 3}, allocVec};
    ASSERT_TRUE(optional.hasValue());
    ASSERT_EQ(1, (*optional)[0]);
    ASSERT_EQ(allocVec.numAllocs(), 1U);

    optional.reset();
    ASSERT_EQ(allocVec.numAllocs(), 0);
    ASSERT_FALSE(optional.hasValue());

    optional = std::vector<int>{3, 2, 1};
    ASSERT_EQ(allocVec.numAllocs(), 1U);
    ASSERT_TRUE(optional.hasValue());
    ASSERT_EQ(3, (*optional)[0]);

    optional.reset();
    ASSERT_EQ(allocVec.numAllocs(), 0);
    ASSERT_FALSE(optional.hasValue());
}

TEST_F(HeapOptionalHolderTest, hasValue)
{
    HeapOptionalHolder<int> optionalInt;
    ASSERT_THROW(*optionalInt, CppRuntimeException);
    ASSERT_EQ(false, optionalInt.hasValue());

    optionalInt = 0xDEAD;
    ASSERT_EQ(true, optionalInt.hasValue());
}

TEST_F(HeapOptionalHolderTest, setGet)
{
    HeapOptionalHolder<int> optionalInt;
    ASSERT_THROW(*optionalInt, CppRuntimeException);
    const int intValue = 0xDEAD;
    optionalInt = intValue;
    ASSERT_EQ(intValue, *optionalInt);

    HeapOptionalHolder<float> optionalFloat;
    ASSERT_THROW(*optionalFloat, CppRuntimeException);
    const float floatValue = 3.14F;
    optionalFloat = floatValue;
    ASSERT_EQ(floatValue, optionalFloat.value());

    HeapOptionalHolder<DummyObject> optionalObject;
    ASSERT_THROW(*optionalObject, CppRuntimeException);
    DummyObject objectValue;
    objectValue.setValue(intValue);
    optionalObject = objectValue;
    const DummyObject& readObjectValue = *optionalObject;
    ASSERT_EQ(intValue, readObjectValue.getValue());
    ASSERT_EQ(intValue, optionalObject->getValue());

    HeapOptionalHolder<int, TrackingAllocator<int>> optionalAlloc;
    ASSERT_THROW(*optionalAlloc, CppRuntimeException);
}

TEST_F(HeapOptionalHolderTest, operatorEquality)
{
    HeapOptionalHolder<int> optional1;
    optional1 = 0xDEAD;
    HeapOptionalHolder<int> optional2;
    optional2 = 0xDEAD;
    HeapOptionalHolder<int> optional3;
    optional3 = 0xBEEF;
    HeapOptionalHolder<int> optional4;
    HeapOptionalHolder<int> optional5;
    HeapOptionalHolder<int> optional6;

    ASSERT_TRUE(optional1 == optional1);
    ASSERT_TRUE(optional1 == optional2);
    ASSERT_FALSE(optional1 == optional3);
    ASSERT_FALSE(optional1 == optional4);
    ASSERT_TRUE(optional5 == optional6);
    ASSERT_FALSE(optional5 == optional1);

    TrackingAllocator<int> alloc1;
    TrackingAllocator<int> alloc2;
    HeapOptionalHolder<int, TrackingAllocator<int>> optional7(12345, alloc1);
    HeapOptionalHolder<int, TrackingAllocator<int>> optional8(12345, alloc2);
    HeapOptionalHolder<int, TrackingAllocator<int>> optional9(alloc2);
    HeapOptionalHolder<int, TrackingAllocator<int>> optional10(alloc2);
    ASSERT_EQ(alloc1.numAllocs(), 1);
    ASSERT_EQ(alloc2.numAllocs(), 1);
    ASSERT_TRUE(optional7 == optional8);
    ASSERT_TRUE(optional9 == optional9);
    ASSERT_TRUE(optional9== optional10);
    ASSERT_FALSE(optional7 == optional10);
}

TEST_F(HeapOptionalHolderTest, operatorLessThan)
{
    HeapOptionalHolder<int> optionalEmpty1;
    HeapOptionalHolder<int> optionalEmpty2;
    HeapOptionalHolder<int> optional1(1);
    HeapOptionalHolder<int> optional2(2);

    ASSERT_FALSE(optionalEmpty1 < optionalEmpty2);
    ASSERT_TRUE(optionalEmpty1 < optional1);
    ASSERT_FALSE(optional1 < optionalEmpty1);
    ASSERT_TRUE(optional1 < optional2);
    ASSERT_FALSE(optional2 < optional1);
}

TEST_F(HeapOptionalHolderTest, constGet)
{
    const int intValue = 0xDEAD;
    HeapOptionalHolder<int> optional;
    optional = intValue;
    const HeapOptionalHolder<int> constOptional(optional);
    ASSERT_EQ(intValue, *constOptional);
    ASSERT_EQ(intValue, constOptional.value());

    DummyObject objectValue;
    objectValue.setValue(intValue);
    HeapOptionalHolder<DummyObject> optionalObject{ objectValue };
    ASSERT_EQ(intValue, optionalObject->getValue());
}

} // namespace zserio
