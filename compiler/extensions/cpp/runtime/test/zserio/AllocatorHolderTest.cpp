#include <memory>

#include "gtest/gtest.h"

#include "zserio/AllocatorHolder.h"

namespace zserio
{

namespace
{

template <typename ALLOC>
class AllocatorHolderTester : public AllocatorHolder<ALLOC>
{
public:
    using allocator_type = typename AllocatorHolder<ALLOC>::allocator_type;

    explicit AllocatorHolderTester(allocator_type allocator) : AllocatorHolder<allocator_type>(allocator)
    {
    }

    void set_allocator(const allocator_type& allocator)
    {
        AllocatorHolder<ALLOC>::set_allocator(allocator);
    }

    void set_allocator(allocator_type&& allocator)
    {
        AllocatorHolder<ALLOC>::set_allocator(allocator);
    }

    allocator_type& get_allocator_ref()
    {
        return AllocatorHolder<ALLOC>::get_allocator_ref();
    }

    const allocator_type& get_allocator_ref() const
    {
        return AllocatorHolder<ALLOC>::get_allocator_ref();
    }
};

} // namespace

TEST(AllocatorHolderTest, emptyConstructor)
{
    const AllocatorHolder<std::allocator<uint8_t>> holder;
    std::allocator<uint8_t> allocator;
    ASSERT_EQ(allocator, holder.get_allocator());
}

TEST(AllocatorHolderTest, allocatorConstructor)
{
    std::allocator<uint8_t> allocator;
    const AllocatorHolder<std::allocator<uint8_t>> holder(allocator);
    ASSERT_EQ(allocator, holder.get_allocator());
}

TEST(AllocatorHolderTest, allocatorConstructorMove)
{
    std::allocator<uint8_t> allocator;
    const AllocatorHolder<std::allocator<uint8_t>> holder(std::move(allocator));
    ASSERT_EQ(std::allocator<uint8_t>(), holder.get_allocator());
}

TEST(AllocatorHolderTest, setAllocator)
{
    std::allocator<uint8_t> allocator1;
    AllocatorHolderTester<std::allocator<uint8_t>> holder(allocator1);
    std::allocator<uint8_t> allocator2;
    holder.set_allocator(allocator2);
    ASSERT_EQ(allocator2, holder.get_allocator());
}

TEST(AllocatorHolderTest, setAllocatorMove)
{
    std::allocator<uint8_t> allocator1;
    AllocatorHolderTester<std::allocator<uint8_t>> holder(allocator1);
    std::allocator<uint8_t> allocator2;
    holder.set_allocator(std::move(allocator2));
    ASSERT_EQ(std::allocator<uint8_t>(), holder.get_allocator());
}

TEST(AllocatorHolderTest, getAllocatorRef)
{
    std::allocator<uint8_t> allocator;
    AllocatorHolderTester<std::allocator<uint8_t>> holder(allocator);
    ASSERT_EQ(allocator, holder.get_allocator_ref());
}

TEST(AllocatorHolderTest, getAllocatorRefConst)
{
    std::allocator<uint8_t> allocator;
    const AllocatorHolderTester<std::allocator<uint8_t>> holder(allocator);
    ASSERT_EQ(allocator, holder.get_allocator_ref());
}

} // namespace zserio
