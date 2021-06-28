#include "gtest/gtest.h"

#include "zserio/pmr/PolymorphicAllocator.h"
#include "zserio/pmr/NewDeleteResource.h"

namespace zserio
{

namespace
{

class TestResource : public zserio::pmr::MemoryResource
{
public:
    virtual void* doAllocate(size_t bytes, size_t) override
    {
        ++m_numAllocations;
        return ::operator new(bytes);
    }

    virtual void doDeallocate(void* p, size_t, size_t) override
    {
        --m_numAllocations;
        ::operator delete(p);
    }

    virtual bool doIsEqual(const MemoryResource& other) const noexcept override
    {
        return this == &other;
    }

    size_t getNumAllocations() const
    {
        return m_numAllocations;
    }

private:
    size_t m_numAllocations = 0;
};

} // namespace

TEST(PolymorphicAllocatorTest, constructorAndResource)
{
    pmr::PolymorphicAllocator<> allocator;
    ASSERT_EQ(pmr::getDefaultResource(), allocator.resource());

    pmr::PolymorphicAllocator<> allocatorNull(nullptr);
    ASSERT_EQ(pmr::getDefaultResource(), allocatorNull.resource());

    TestResource resource;

    pmr::PolymorphicAllocator<> allocator2(&resource);
    ASSERT_EQ(&resource, allocator2.resource());

    pmr::PolymorphicAllocator<> allocator2Copy(allocator2);
    ASSERT_EQ(&resource, allocator2Copy.resource());

    pmr::PolymorphicAllocator<> allocator2Moved(std::move(allocator2));
    ASSERT_EQ(&resource, allocator2Moved.resource());
}

TEST(PropagatingPolymorphicAllocatorTest, constructorAndResource)
{
    pmr::PropagatingPolymorphicAllocator<> allocator;
    ASSERT_EQ(pmr::getDefaultResource(), allocator.resource());

    pmr::PropagatingPolymorphicAllocator<> allocatorNull(nullptr);
    ASSERT_EQ(pmr::getDefaultResource(), allocatorNull.resource());

    TestResource resource;

    pmr::PropagatingPolymorphicAllocator<> allocator2(&resource);
    ASSERT_EQ(&resource, allocator2.resource());

    pmr::PropagatingPolymorphicAllocator<> allocator2Copy(allocator2);
    ASSERT_EQ(&resource, allocator2Copy.resource());

    pmr::PropagatingPolymorphicAllocator<> allocator2Moved(allocator2);
    ASSERT_EQ(&resource, allocator2Moved.resource());
}

TEST(PolymorphicAllocatorTest, constructorRebind)
{
    TestResource resource;
    pmr::PolymorphicAllocator<> allocator(&resource);
    pmr::PolymorphicAllocator<int> allocatorRebind(allocator);
    ASSERT_EQ(&resource, allocatorRebind.resource());
}

TEST(PropagatingPolymorphicAllocatorTest, constructorRebind)
{
    TestResource resource;
    pmr::PropagatingPolymorphicAllocator<> allocator(&resource);
    pmr::PolymorphicAllocator<int> allocatorRebind(allocator);
    ASSERT_EQ(&resource, allocatorRebind.resource());
}

TEST(PolymorphicAllocatorTest, assignment)
{
    TestResource resource;
    pmr::PolymorphicAllocator<> allocator(&resource);
    pmr::PolymorphicAllocator<int> allocatorRebind;
    allocatorRebind = allocator;
    ASSERT_EQ(&resource, allocatorRebind.resource());
}

TEST(PropagatingPolymorphicAllocatorTest, assignment)
{
    TestResource resource;
    pmr::PropagatingPolymorphicAllocator<> allocator(&resource);
    pmr::PropagatingPolymorphicAllocator<int> allocatorRebind;
    allocatorRebind = allocator;
    ASSERT_EQ(&resource, allocatorRebind.resource());
}

TEST(PolymorphicAllocatorTest, allocations)
{
    TestResource resource;
    pmr::PolymorphicAllocator<> allocator(&resource);
    ASSERT_EQ(0, resource.getNumAllocations());

    const auto ptr = allocator.allocate(1);
    ASSERT_EQ(1, resource.getNumAllocations());

    const auto ptr2 = allocator.allocate(10);
    ASSERT_EQ(2, resource.getNumAllocations());

    allocator.deallocate(ptr, 1);
    ASSERT_EQ(1, resource.getNumAllocations());

    allocator.deallocate(ptr2, 10);
    ASSERT_EQ(0, resource.getNumAllocations());
}

TEST(PropagatingPolymorphicAllocatorTest, allocations)
{
    TestResource resource;
    pmr::PropagatingPolymorphicAllocator<> allocator(&resource);
    ASSERT_EQ(0, resource.getNumAllocations());

    const auto ptr = allocator.allocate(1);
    ASSERT_EQ(1, resource.getNumAllocations());

    const auto ptr2 = allocator.allocate(10);
    ASSERT_EQ(2, resource.getNumAllocations());

    allocator.deallocate(ptr, 1);
    ASSERT_EQ(1, resource.getNumAllocations());

    allocator.deallocate(ptr2, 10);
    ASSERT_EQ(0, resource.getNumAllocations());
}

TEST(PolymorphicAllocatorTest, select_on_container_copy_construction)
{
    TestResource resource;
    pmr::PolymorphicAllocator<> allocator(&resource);

    std::vector<int, pmr::PolymorphicAllocator<int>> vector(allocator);
    vector.assign({ 0, 13, 42 });
    std::vector<int, pmr::PolymorphicAllocator<int>> vectorCopy(vector);
    ASSERT_NE(&resource, vectorCopy.get_allocator().resource());
}

TEST(PropagatingPolymorphicAllocatorTest, select_on_container_copy_construction)
{
    TestResource resource;
    pmr::PropagatingPolymorphicAllocator<> allocator(&resource);

    std::vector<int, pmr::PropagatingPolymorphicAllocator<int>> vector(allocator);
    vector.assign({ 0, 13, 42 });
    std::vector<int, pmr::PropagatingPolymorphicAllocator<int>> vectorCopy(vector);
    ASSERT_EQ(&resource, vectorCopy.get_allocator().resource());
}

TEST(PolymorphicAllocatorTest, compare)
{
    pmr::PolymorphicAllocator<> allocator;
    pmr::PolymorphicAllocator<> allocator2;
    ASSERT_TRUE(allocator == allocator2);
    ASSERT_FALSE(allocator != allocator2);

    TestResource resource;

    pmr::PolymorphicAllocator<> allocatorRes(&resource);
    pmr::PolymorphicAllocator<> allocatorRes2(&resource);
    ASSERT_TRUE(allocatorRes == allocatorRes2);
    ASSERT_FALSE(allocatorRes != allocatorRes2);
    ASSERT_FALSE(allocatorRes == allocator);
    ASSERT_TRUE(allocatorRes != allocator);
}

TEST(PropagatingPolymorphicAllocatorTest, compare)
{
    pmr::PropagatingPolymorphicAllocator<> allocator;
    pmr::PropagatingPolymorphicAllocator<> allocator2;
    ASSERT_TRUE(allocator == allocator2);
    ASSERT_FALSE(allocator != allocator2);

    TestResource resource;

    pmr::PropagatingPolymorphicAllocator<> allocatorRes(&resource);
    pmr::PropagatingPolymorphicAllocator<> allocatorRes2(&resource);
    ASSERT_TRUE(allocatorRes == allocatorRes2);
    ASSERT_FALSE(allocatorRes != allocatorRes2);
    ASSERT_FALSE(allocatorRes == allocator);
    ASSERT_TRUE(allocatorRes != allocator);
}

} // namespace zserio
