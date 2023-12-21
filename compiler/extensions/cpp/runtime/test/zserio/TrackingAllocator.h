#ifndef ZSERIO_TRACKING_ALLOCATOR_H_INC
#define ZSERIO_TRACKING_ALLOCATOR_H_INC

#include <memory>
#include <set>

#include "gtest/gtest.h"

namespace zserio
{

namespace detail
{

class AllocationTracker
{
public:
    AllocationTracker() = default;
    ~AllocationTracker()
    {
        EXPECT_TRUE(m_allocations.empty());
    }

    AllocationTracker(const AllocationTracker&) = default;
    AllocationTracker& operator=(const AllocationTracker&) = default;

    AllocationTracker(AllocationTracker&&) = default;
    AllocationTracker& operator=(AllocationTracker&&) = default;

    void allocated(void* ptr)
    {
        m_allocations.insert(ptr);
    }

    void deallocated(void* ptr)
    {
        EXPECT_TRUE(m_allocations.count(ptr) > 0);
        m_allocations.erase(ptr);
    }

    size_t numAllocs() const
    {
        return m_allocations.size();
    }

private:
    std::set<void*> m_allocations;
};

} // namespace detail

/**
 * Stateful allocator that tracks the allocations. Used in unit tests.
 * Every copy of an allocator must be able to deallocate what any of the other copies
 * allocated. This allocator simulates this behavior, all copies shares the same tracker,
 * so it can check if the allocators are used correctly (ie. no allocations leaks,
 * every allocation is deallocated by the same allocator or its copy).
 */
template <typename T>
class TrackingAllocatorImpl
{
public:
    using value_type = typename std::allocator<T>::value_type;
    using pointer = typename std::allocator<T>::pointer;

    TrackingAllocatorImpl() :
            m_tracker(std::make_shared<detail::AllocationTracker>())
    {}
    ~TrackingAllocatorImpl() = default;

    TrackingAllocatorImpl(const TrackingAllocatorImpl&) = default;
    TrackingAllocatorImpl& operator=(const TrackingAllocatorImpl&) = default;

    TrackingAllocatorImpl(TrackingAllocatorImpl&&) = delete;
    TrackingAllocatorImpl& operator=(TrackingAllocatorImpl&&) = delete;

    template <typename Other>
    TrackingAllocatorImpl(const TrackingAllocatorImpl<Other>& other) :
            m_allocator(other.m_allocator),
            m_tracker(other.m_tracker)
    {}

    value_type* allocate(std::size_t n)
    {
        const auto ptr = m_allocator.allocate(n);
        m_tracker->allocated(ptr);
        return ptr;
    }

    void deallocate(value_type* p, std::size_t n) noexcept
    {
        m_tracker->deallocated(p);
        m_allocator.deallocate(p, n);
    }

    bool operator==(const TrackingAllocatorImpl& other) const
    {
        return std::tie(m_tracker, m_allocator) == std::tie(other.m_tracker, other.m_allocator);
    }

    bool operator!=(const TrackingAllocatorImpl& other) const
    {
        return !(*this == other);
    }

    size_t numAllocs() const
    {
        return m_tracker->numAllocs();
    }

    template <typename U>
    friend class TrackingAllocatorImpl;

private:
    std::allocator<T> m_allocator;
    std::shared_ptr<detail::AllocationTracker> m_tracker;
};

/**
 * Propagating allocator. Ie. container copy/move propagates also
 * the allocator.
 */
template <typename T>
class TrackingAllocator : public TrackingAllocatorImpl<T>
{
public:
    using propagate_on_container_copy_assignment = std::true_type;
    using propagate_on_container_move_assignment = std::true_type;

    using TrackingAllocatorImpl<T>::TrackingAllocatorImpl;

    TrackingAllocator select_on_container_copy_construction() const
    {
        return *this;
    }
};

/**
 * Non-propagating allocator. Ie. container copy/move does not propagate
 * the allocator.
 */
template <typename T>
class TrackingAllocatorNonProp : public TrackingAllocatorImpl<T>
{
public:
    using propagate_on_container_copy_assignment = std::false_type;
    using propagate_on_container_move_assignment = std::false_type;

    using TrackingAllocatorImpl<T>::TrackingAllocatorImpl;

    TrackingAllocatorNonProp select_on_container_copy_construction() const
    {
        return TrackingAllocatorNonProp();
    }
};

} // namespace zserio

#endif // ZSERIO_TRACKING_ALLOCATOR_H_INC
