#include "gtest/gtest.h"

#include "zserio/pmr/MemoryResource.h"

namespace zserio
{

class TestResource : public zserio::pmr::MemoryResource
{
public:
    explicit TestResource(size_t instanceId) :
        m_instanceId(instanceId)
    {}

    size_t numAllocs() const
    {
        return m_numAllocs;
    }

    size_t numDeallocs() const
    {
        return m_numDeallocs;
    }

private:
    void* doAllocate(size_t bytes, size_t) override
    {
        ++m_numAllocs;
        return ::operator new(bytes);
    }

    void doDeallocate(void* p, size_t, size_t) override
    {
        ++m_numDeallocs;
        ::operator delete(p);
    }

    bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        auto otherPtr = dynamic_cast<const TestResource*>(&other);
        return otherPtr != nullptr && m_instanceId == otherPtr->m_instanceId;
    }

    size_t m_numAllocs = 0;
    size_t m_numDeallocs = 0;
    size_t m_instanceId;
};

TEST(MemoryResourceTest, allocateDeallocate)
{
    TestResource res(1);
    auto p = res.allocate(10);
    EXPECT_EQ(1, res.numAllocs());
    res.deallocate(p, 10);
    EXPECT_EQ(1, res.numDeallocs());
}

TEST(MemoryResourceTest, isEqual)
{
    TestResource res1(1);
    TestResource res1_1(1);
    TestResource res2(2);
    ASSERT_TRUE(res1.isEqual(res1));
    ASSERT_TRUE(res1.isEqual(res1_1));
    ASSERT_FALSE(res1.isEqual(res2));
}

TEST(MemoryResourceTest, equalOp)
{
    TestResource res1(1);
    TestResource res1_1(1);
    TestResource res2(2);
    ASSERT_TRUE(res1 == res1);
    ASSERT_TRUE(res1 == res1_1);
    ASSERT_FALSE(res1 == res2);
}

TEST(MemoryResourceTest, nonEqualOp)
{
    TestResource res1(1);
    TestResource res1_1(1);
    TestResource res2(2);
    ASSERT_FALSE(res1 != res1);
    ASSERT_FALSE(res1 != res1_1);
    ASSERT_TRUE(res1 != res2);
}

TEST(MemoryResourceTest, setGetDefaultResource)
{
    TestResource res(1);
    auto origRes = zserio::pmr::setDefaultResource(&res);
    ASSERT_EQ(&res, zserio::pmr::getDefaultResource());
    ASSERT_EQ(&res, zserio::pmr::setDefaultResource(origRes));
    zserio::pmr::setDefaultResource(nullptr);
    ASSERT_EQ(origRes, zserio::pmr::getDefaultResource());
}

} // namespace zserio
