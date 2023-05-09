#include "gtest/gtest.h"

#include "zserio/Walker.h"
#include "zserio/Traits.h"
#include "zserio/TypeInfo.h"
#include "zserio/Reflectable.h"
#include "zserio/Array.h"
#include "zserio/ArrayTraits.h"
#include "zserio/PackingContext.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/OptionalHolder.h"

#include "test_object/std_allocator/WalkerBitmask.h"
#include "test_object/std_allocator/WalkerNested.h"
#include "test_object/std_allocator/WalkerUnion.h"
#include "test_object/std_allocator/WalkerObject.h"

using test_object::std_allocator::WalkerBitmask;
using test_object::std_allocator::WalkerNested;
using test_object::std_allocator::WalkerUnion;
using test_object::std_allocator::WalkerObject;

namespace zserio
{

namespace
{

WalkerObject createWalkerObject(uint32_t identifier = 13, bool createNested = true)
{
    std::vector<WalkerUnion> unionArray;
    unionArray.resize(3);
    unionArray[0].setText("1");
    unionArray[1].setValue(2);
    unionArray[2].setNestedArray(std::vector<WalkerNested>{{WalkerNested{"nestedArray"}}});
    if (createNested)
    {
        return WalkerObject(identifier, WalkerNested("nested"), "test", std::move(unionArray), NullOpt);
    }
    else
    {
        return WalkerObject(identifier, NullOpt, "test", std::move(unionArray), NullOpt);
    }
}

class TestWalkObserver : public IWalkObserver
{
public:
    using CapturesMap = std::map<StringView, std::vector<IReflectableConstPtr>>;

    TestWalkObserver()
    {
        // initialize empty captures
        m_captures["beginRoot"_sv];
        m_captures["endRoot"_sv];
        m_captures["beginArray"_sv];
        m_captures["endArray"_sv];
        m_captures["beginCompound"_sv];
        m_captures["endCompound"_sv];
        m_captures["visitValue"_sv];
    }

    void beginRoot(const IReflectableConstPtr& compound) override
    {
        m_captures["beginRoot"_sv].push_back(compound);
    }

    void endRoot(const IReflectableConstPtr& compound) override
    {
        m_captures["endRoot"_sv].push_back(compound);
    }

    void beginArray(const IReflectableConstPtr& array, const FieldInfo&) override
    {
        m_captures["beginArray"_sv].push_back(array);
    }

    void endArray(const IReflectableConstPtr& array, const FieldInfo&) override
    {
        m_captures["endArray"_sv].push_back(array);
    }

    void beginCompound(const IReflectableConstPtr& compound, const FieldInfo&, size_t) override
    {
        m_captures["beginCompound"_sv].push_back(compound);
    }

    void endCompound(const IReflectableConstPtr& compound, const FieldInfo&, size_t) override
    {
        m_captures["endCompound"_sv].push_back(compound);
    }

    void visitValue(const IReflectableConstPtr& value, const FieldInfo&, size_t) override
    {
        m_captures["visitValue"_sv].push_back(value);
    }

    const std::vector<IReflectableConstPtr>& getCaptures(StringView captureName) const
    {
        return m_captures.find(captureName)->second;
    }

private:
    CapturesMap m_captures;
};

class TestWalkFilter : public IWalkFilter
{
public:
    TestWalkFilter& beforeArray(bool beforeArray) { m_beforeArray = beforeArray; return *this; }
    TestWalkFilter& afterArray(bool afterArray) { m_afterArray = afterArray; return *this; }
    TestWalkFilter& onlyFirstElement(bool onlyFirstElement)
    {
        m_onlyFirstElement = onlyFirstElement; return *this;
    }
    TestWalkFilter& beforeCompound(bool beforeCompound) { m_beforeCompound = beforeCompound; return *this; }
    TestWalkFilter& afterCompound(bool afterCompound) { m_afterCompound = afterCompound; return *this; }
    TestWalkFilter& beforeValue(bool beforeValue) { m_beforeValue = beforeValue; return *this; }
    TestWalkFilter& afterValue(bool afterValue) { m_afterValue = afterValue; return *this; }

    bool beforeArray(const IReflectableConstPtr&, const FieldInfo&) override
    {
        m_isFirstElement = true;
        return m_beforeArray;
    }

    bool afterArray(const IReflectableConstPtr&, const FieldInfo&) override
    {
        m_isFirstElement = false;
        return m_afterArray;
    }

    bool beforeCompound(const IReflectableConstPtr&, const FieldInfo&, size_t) override
    {
        return m_beforeCompound;
    }

    bool afterCompound(const IReflectableConstPtr&, const FieldInfo&, size_t) override
    {
        bool goToNext = !(m_onlyFirstElement && m_isFirstElement);
        m_isFirstElement = false;
        return goToNext && m_afterCompound;
    }

    bool beforeValue(const IReflectableConstPtr&, const FieldInfo&, size_t) override
    {
        return m_beforeValue;
    }

    bool afterValue(const IReflectableConstPtr&, const FieldInfo&, size_t) override
    {
        return m_afterValue;
    }

private:
    bool m_beforeArray = true;
    bool m_afterArray = true;
    bool m_onlyFirstElement = false;
    bool m_beforeCompound = true;
    bool m_afterCompound = true;
    bool m_beforeValue = true;
    bool m_afterValue = true;
    bool m_isFirstElement = false;
};

} // namespace

TEST(WalkerTest, walkNull)
{
    DefaultWalkObserver defaultObserver;
    Walker walker(defaultObserver);
    ASSERT_THROW(walker.walk(nullptr), CppRuntimeException);
}

TEST(WalkerTest, walkNonCompound)
{
    DefaultWalkObserver defaultObserver;
    Walker walker(defaultObserver);
    WalkerBitmask walkerBitmask;

    ASSERT_THROW(walker.walk(walkerBitmask.reflectable()), CppRuntimeException);
}

TEST(WalkerTest, walk)
{
    TestWalkObserver observer;
    DefaultWalkFilter defaultFilter;
    Walker walker(observer, defaultFilter);
    WalkerObject walkerObject = createWalkerObject();
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(3)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginCompound"_sv).at(4)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endCompound"_sv).at(3)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(4)->getTypeInfo().getSchemaName());

    ASSERT_EQ(7, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).at(4)->toUInt());
    ASSERT_EQ("nestedArray", observer.getCaptures("visitValue"_sv).at(5)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(6));
}

TEST(WalkerTest, walkWrongOptionalCondition)
{
    // use case: optional condition states that the optional is used, but it is not set!
    TestWalkObserver observer;
    DefaultWalkFilter defaultFilter;
    Walker walker(observer, defaultFilter);
    WalkerObject walkerObject = createWalkerObject(13, false);
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endArray"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(4, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginCompound"_sv).at(3)->getTypeInfo().getSchemaName());

    ASSERT_EQ(4, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endCompound"_sv).at(2)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(3)->getTypeInfo().getSchemaName());

    ASSERT_EQ(7, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(1));
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).at(4)->toUInt());
    ASSERT_EQ("nestedArray", observer.getCaptures("visitValue"_sv).at(5)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(6));
}

TEST(WalkerTest, walkSkipCompound)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.beforeCompound(false);
    Walker walker(observer, filter);
    WalkerObject walkerObject = createWalkerObject();
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginCompound"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endCompound"_sv).empty());

    ASSERT_EQ(3, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(2));
}

TEST(WalkerTest, walkSkipSiblings)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.afterValue(false);
    Walker walker(observer, filter);
    WalkerObject walkerObject = createWalkerObject();
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginArray"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endArray"_sv).empty());

    ASSERT_TRUE(observer.getCaptures("beginCompound"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endCompound"_sv).empty());

    ASSERT_EQ(1, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
}

TEST(WalkerTest, walkSkipAfterNested)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.afterCompound(false);
    Walker walker(observer, filter);
    WalkerObject walkerObject = createWalkerObject();
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_TRUE(observer.getCaptures("beginArray"_sv).empty());
    ASSERT_TRUE(observer.getCaptures("endArray"_sv).empty());

    ASSERT_EQ(1, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
}

TEST(WalkerTest, walkOnlyFirstElement)
{
    TestWalkObserver observer;
    TestWalkFilter filter;
    filter.onlyFirstElement(true);
    Walker walker(observer, filter);
    WalkerObject walkerObject = createWalkerObject();
    walker.walk(walkerObject.reflectable());

    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("beginRoot"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerObject"_sv,
            observer.getCaptures("endRoot"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("beginArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(1, observer.getCaptures("endArray"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endArray"_sv).at(0)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("beginCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("beginCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("beginCompound"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(2, observer.getCaptures("endCompound"_sv).size());
    ASSERT_EQ("test_object.std_allocator.WalkerNested"_sv,
            observer.getCaptures("endCompound"_sv).at(0)->getTypeInfo().getSchemaName());
    ASSERT_EQ("test_object.std_allocator.WalkerUnion"_sv,
            observer.getCaptures("endCompound"_sv).at(1)->getTypeInfo().getSchemaName());

    ASSERT_EQ(5, observer.getCaptures("visitValue"_sv).size());
    ASSERT_EQ(13, observer.getCaptures("visitValue"_sv).at(0)->toUInt());
    ASSERT_EQ("nested", observer.getCaptures("visitValue"_sv).at(1)->toString());
    ASSERT_EQ("test", observer.getCaptures("visitValue"_sv).at(2)->toString());
    ASSERT_EQ("1", observer.getCaptures("visitValue"_sv).at(3)->toString());
    ASSERT_EQ(nullptr, observer.getCaptures("visitValue"_sv).at(4));
}

TEST(DefaultWalkObserverTest, allMethods)
{
    DefaultWalkObserver defaultObserver;
    IWalkObserver& walkObserver = defaultObserver;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];

    ASSERT_NO_THROW(walkObserver.beginRoot(walkerReflectable));
    ASSERT_NO_THROW(walkObserver.endRoot(walkerReflectable));
    ASSERT_NO_THROW(walkObserver.beginArray(walkerReflectable, walkerFieldInfo));
    ASSERT_NO_THROW(walkObserver.endArray(walkerReflectable, walkerFieldInfo));
    ASSERT_NO_THROW(walkObserver.beginCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_NO_THROW(walkObserver.endCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_NO_THROW(walkObserver.visitValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(DefaultWalkFilterTest, allMethods)
{
    DefaultWalkFilter defaultFilter;
    IWalkFilter& walkFilter = defaultFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(DepthFilterTest, depth0)
{
    DepthWalkFilter depthWalkFilter(0);
    IWalkFilter& walkFilter = depthWalkFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];

    ASSERT_FALSE(walkFilter.beforeArray(walkerReflectable, walkerFieldInfo)); // 0
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerFieldInfo)); // 0

    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0

    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
}

TEST(DepthFilterTest, depth1)
{
    DepthWalkFilter depthWalkFilter(1);
    IWalkFilter& walkFilter = depthWalkFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerFieldInfo)); // 0
    ASSERT_FALSE(walkFilter.beforeArray(walkerReflectable, walkerFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerFieldInfo)); // 0

    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
    ASSERT_FALSE(walkFilter.beforeArray(walkerReflectable, walkerFieldInfo)); // 1
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerFieldInfo)); // 1
    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 1
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0

    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT)); // 0
}

TEST(RegexWalkFilterTest, regexAllMatch)
{
    RegexWalkFilter regexWalkFilter(".*");
    IWalkFilter& walkFilter = regexWalkFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(RegexWalkFilterTest, regexPrefixMatch)
{
    RegexWalkFilter regexWalkFilter("nested\\..*");
    IWalkFilter& walkFilter = regexWalkFilter;
    WalkerObject walkerObject = createWalkerObject();
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& identifierFieldInfo = walkerObject.typeInfo().getFields()[0];
    IReflectableConstPtr identifierReflectable = walkerReflectable->getField("identifier");
    ASSERT_FALSE(walkFilter.beforeValue(identifierReflectable, identifierFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(identifierReflectable, identifierFieldInfo, WALKER_NOT_ELEMENT));

    const FieldInfo& nestedFieldInfo = walkerObject.typeInfo().getFields()[1];
    IReflectableConstPtr nestedReflectable = walkerReflectable->getField("nested");
    ASSERT_TRUE(walkFilter.beforeCompound(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));
    const FieldInfo& textFieldInfo = nestedFieldInfo.typeInfo.getFields()[0];
    IReflectableConstPtr textReflectable = nestedReflectable->getField("text");
    ASSERT_TRUE(walkFilter.beforeValue(textReflectable, textFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(textReflectable, textFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterCompound(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));

    // ignore text

    const FieldInfo& unionArrayFieldInfo = walkerObject.typeInfo().getFields()[3];
    IReflectableConstPtr unionArrayReflectable = walkerReflectable->getField("unionArray");
    ASSERT_FALSE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexArrayMatch)
{
    RegexWalkFilter regexWalkFilter("unionArray\\[\\d+\\]\\.nes.*");
    IWalkFilter& walkFilter = regexWalkFilter;
    WalkerObject walkerObject = createWalkerObject();
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& unionArrayFieldInfo = walkerObject.typeInfo().getFields()[3];
    IReflectableConstPtr unionArrayReflectable = walkerReflectable->getField("unionArray");
    ASSERT_TRUE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));

    ASSERT_FALSE(walkFilter.beforeCompound(unionArrayReflectable->at(0), unionArrayFieldInfo, 0));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(0), unionArrayFieldInfo, 0));

    ASSERT_FALSE(walkFilter.beforeCompound(unionArrayReflectable->at(1), unionArrayFieldInfo, 1));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(1), unionArrayFieldInfo, 1));

    ASSERT_TRUE(walkFilter.beforeCompound(unionArrayReflectable->at(2), unionArrayFieldInfo, 2));
    ASSERT_TRUE(walkFilter.afterCompound(unionArrayReflectable->at(2), unionArrayFieldInfo, 2));

    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexArrayNoMatch)
{
    RegexWalkFilter regexWalkFilter("^unionArray\\[\\d*\\]\\.te.*");
    IWalkFilter& walkFilter = regexWalkFilter;

    std::vector<WalkerUnion> unionArray;
    unionArray.resize(1);
    unionArray[0].setNestedArray(std::vector<WalkerNested>{{WalkerNested{"nestedArray"}}});
    WalkerObject walkerObject (13, WalkerNested("nested"), "test", std::move(unionArray), NullOpt);
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& unionArrayFieldInfo = walkerObject.typeInfo().getFields()[3];
    IReflectableConstPtr unionArrayReflectable = walkerReflectable->getField("unionArray");
    ASSERT_FALSE(walkFilter.beforeArray(unionArrayReflectable, unionArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(unionArrayReflectable, unionArrayFieldInfo));
}

TEST(RegexWalkFilterTest, regexNullCompoundMatch)
{
    RegexWalkFilter regexWalkFilter("nested");
    IWalkFilter& walkFilter = regexWalkFilter;

    WalkerObject walkerObject = createWalkerObject(0, false);
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& nestedFieldInfo = walkerObject.typeInfo().getFields()[1];
    IReflectableConstPtr nestedReflectable = walkerReflectable->getField("nested");
    ASSERT_EQ(nullptr, nestedReflectable);
    // note that the null compounds are processed as values!
    ASSERT_TRUE(walkFilter.beforeValue(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(RegexWalkFilterTest, regexNullCompoundNoMatch)
{
    RegexWalkFilter regexWalkFilter("^nested\\.text$");
    IWalkFilter& walkFilter = regexWalkFilter;

    WalkerObject walkerObject = createWalkerObject(0, false);
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& nestedFieldInfo = walkerObject.typeInfo().getFields()[1];
    IReflectableConstPtr nestedReflectable = walkerReflectable->getField("nested");
    ASSERT_EQ(nullptr, nestedReflectable);
    // note that the null compounds are processed as values!
    ASSERT_FALSE(walkFilter.beforeValue(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(nestedReflectable, nestedFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(RegexWalkFilterTest, regexNullArrayMatch)
{
    RegexWalkFilter regexWalkFilter("optionalUnionArray");
    IWalkFilter& walkFilter = regexWalkFilter;

    WalkerObject walkerObject = createWalkerObject();
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& optionalUnionArrayFieldInfo = walkerObject.typeInfo().getFields()[4];
    IReflectableConstPtr optionalUnionArrayReflectable = walkerReflectable->getField("optionalUnionArray");
    ASSERT_EQ(nullptr, optionalUnionArrayReflectable);
    // note that the null arrays are processed as values!
    ASSERT_TRUE(walkFilter.beforeValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo,
            WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo,
            WALKER_NOT_ELEMENT));
}

TEST(RegexWalkFilterTest, regexNullArrayNoMatch)
{
    RegexWalkFilter regexWalkFilter("^optionalUnionArray\\.\\[\\d+\\]\\.nestedArray.*");
    IWalkFilter& walkFilter = regexWalkFilter;

    WalkerObject walkerObject = createWalkerObject();
    IReflectableConstPtr walkerReflectable = walkerObject.reflectable();

    const FieldInfo& optionalUnionArrayFieldInfo = walkerObject.typeInfo().getFields()[4];
    IReflectableConstPtr optionalUnionArrayReflectable = walkerReflectable->getField("optionalUnionArray");
    ASSERT_EQ(nullptr, optionalUnionArrayReflectable);
    // note that the null arrays are processed as values!
    ASSERT_FALSE(walkFilter.beforeValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo,
            WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(optionalUnionArrayReflectable, optionalUnionArrayFieldInfo,
            WALKER_NOT_ELEMENT));
}

TEST(ArrayLengthWalkFilterTest, length0)
{
    ArrayLengthWalkFilter arrayLengthWalkFilter(0);
    IWalkFilter& walkFilter = arrayLengthWalkFilter;
    IReflectableConstPtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, 0));
    ASSERT_FALSE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, 0));
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, 1));
    ASSERT_FALSE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, 1));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));

    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, 0));
    ASSERT_FALSE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, 0));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(AndWalkFilterTest, empty)
{
    AndWalkFilter andWalkFilter({});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectableConstPtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(AndWalkFilterTest, trueTrue)
{
    TestWalkFilter trueFilter1;
    TestWalkFilter trueFilter2;
    AndWalkFilter andWalkFilter({std::ref<IWalkFilter>(trueFilter1), std::ref<IWalkFilter>(trueFilter2)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectableConstPtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_TRUE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_TRUE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_TRUE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(AndWalkFilterTest, falseFalse)
{
    TestWalkFilter falseFilter1;
    falseFilter1.beforeArray(false);
    falseFilter1.afterArray(false);
    falseFilter1.beforeCompound(false);
    falseFilter1.afterCompound(false);
    falseFilter1.beforeValue(false);
    falseFilter1.afterValue(false);
    TestWalkFilter falseFilter2;
    falseFilter2.beforeArray(false);
    falseFilter2.afterArray(false);
    falseFilter2.beforeCompound(false);
    falseFilter2.afterCompound(false);
    falseFilter2.beforeValue(false);
    falseFilter2.afterValue(false);
    AndWalkFilter andWalkFilter({std::ref<IWalkFilter>(falseFilter1), std::ref<IWalkFilter>(falseFilter2)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_FALSE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

TEST(AndWalkFilterTest, trueFalse)
{
    TestWalkFilter trueFilter;
    TestWalkFilter falseFilter;
    falseFilter.beforeArray(false);
    falseFilter.afterArray(false);
    falseFilter.beforeCompound(false);
    falseFilter.afterCompound(false);
    falseFilter.beforeValue(false);
    falseFilter.afterValue(false);
    AndWalkFilter andWalkFilter({std::ref<IWalkFilter>(trueFilter), std::ref<IWalkFilter>(falseFilter)});
    IWalkFilter& walkFilter = andWalkFilter;
    IReflectablePtr walkerReflectable = nullptr;
    const FieldInfo& walkerFieldInfo = WalkerObject::typeInfo().getFields()[0];
    const FieldInfo& walkerArrayFieldInfo = WalkerObject::typeInfo().getFields()[3];

    ASSERT_FALSE(walkFilter.beforeArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.afterArray(walkerReflectable, walkerArrayFieldInfo));
    ASSERT_FALSE(walkFilter.beforeCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.afterCompound(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.beforeValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
    ASSERT_FALSE(walkFilter.afterValue(walkerReflectable, walkerFieldInfo, WALKER_NOT_ELEMENT));
}

} // namespace zserio
