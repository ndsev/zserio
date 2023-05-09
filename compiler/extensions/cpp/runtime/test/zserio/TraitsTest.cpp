#include "gtest/gtest.h"

#include <memory>
#include <vector>

#include "zserio/Traits.h"
#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/AnyHolder.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
#include "zserio/PackingContext.h"
#include "zserio/Reflectable.h"
#include "zserio/pmr/PolymorphicAllocator.h"

namespace zserio
{

namespace
{

// wrapper functions to prevent macro confusion with ',' in template parameters
void assertTrue(bool value)
{
    ASSERT_TRUE(value);
}

void assertFalse(bool value)
{
    ASSERT_FALSE(value);
}

class DummyObjectInitialize
{
public:
    void initialize() {}
};

class DummyObjectInitializeChildren
{
public:
    void initializeChildren() {}
};

class DummyObjectReflectable
{
public:
    IReflectablePtr reflectable()
    {
        return nullptr;
    }
};

class DummyBitmask
{
public:
    using underlying_type = int;

    int getValue() { return 0; }
};

template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE, typename = void>
struct has_field_ctor : std::false_type
{};

template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
struct has_field_ctor<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE,
        detail::void_t<is_field_constructor_enabled_t<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE>>> :
                std::true_type
{};

} // namespace

TEST(TraitsTest, isAllocator)
{
    ASSERT_TRUE(is_allocator<std::allocator<uint8_t>>::value);
    ASSERT_TRUE(is_allocator<std::allocator<std::vector<uint8_t>>>::value);
    ASSERT_TRUE(is_allocator<pmr::PolymorphicAllocator<uint8_t>>::value);
    ASSERT_TRUE(is_allocator<pmr::PolymorphicAllocator<std::vector<uint8_t>>>::value);
    ASSERT_TRUE(is_allocator<pmr::PropagatingPolymorphicAllocator<uint8_t>>::value);
    ASSERT_TRUE(is_allocator<pmr::PropagatingPolymorphicAllocator<std::vector<uint8_t>>>::value);

    ASSERT_FALSE(is_allocator<int>::value);
    ASSERT_FALSE(is_allocator<uint64_t>::value);
    ASSERT_FALSE(is_allocator<std::vector<uint8_t>>::value);
    ASSERT_FALSE(is_allocator<AnyHolder<>>::value);
}

TEST(TraitsTest, isFirstAllocator)
{
    assertTrue(is_first_allocator<std::allocator<uint8_t>, int>::value);
    assertTrue(is_first_allocator<std::allocator<uint8_t>, std::allocator<uint8_t>>::value);
    assertTrue(is_first_allocator<std::allocator<uint8_t>, std::allocator<uint8_t>, char>::value);
    assertTrue(is_first_allocator<std::allocator<uint8_t>, int, std::allocator<uint8_t>>::value);
    assertTrue(is_first_allocator<pmr::PolymorphicAllocator<uint8_t>, int, std::allocator<uint8_t>>::value);
    assertTrue(is_first_allocator<pmr::PolymorphicAllocator<uint8_t>, char>::value);

    assertFalse(is_first_allocator<int, std::allocator<uint8_t>>::value);
    assertFalse(is_first_allocator<std::vector<uint8_t>, std::allocator<uint8_t>>::value);
    assertFalse(is_first_allocator<char, std::allocator<uint8_t>, std::allocator<uint8_t>>::value);
    assertFalse(is_first_allocator<int, std::allocator<uint8_t>, std::allocator<uint8_t>>::value);
    assertFalse(is_first_allocator<int, pmr::PolymorphicAllocator<uint8_t>, std::allocator<uint8_t>>::value);
    assertFalse(is_first_allocator<char, pmr::PolymorphicAllocator<uint8_t>>::value);
}

TEST(TraitsTest, hasInitialize)
{
    ASSERT_TRUE(has_initialize<DummyObjectInitialize>::value);
    DummyObjectInitialize().initialize();
    ASSERT_FALSE(has_initialize<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_initialize<DummyBitmask>::value);
    ASSERT_FALSE(has_initialize<std::string>::value);
    ASSERT_FALSE(has_initialize<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, hasInitializeChildren)
{
    ASSERT_TRUE(has_initialize_children<DummyObjectInitializeChildren>::value);
    DummyObjectInitializeChildren().initializeChildren();
    ASSERT_FALSE(has_initialize_children<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_initialize_children<DummyBitmask>::value);
    ASSERT_FALSE(has_initialize_children<std::string>::value);
    ASSERT_FALSE(has_initialize_children<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, hasReflectable)
{
    ASSERT_TRUE(has_reflectable<DummyObjectReflectable>::value);
    ASSERT_EQ(nullptr, DummyObjectReflectable().reflectable());
    ASSERT_FALSE(has_reflectable<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_reflectable<DummyBitmask>::value);
    ASSERT_FALSE(has_reflectable<std::string>::value);
    ASSERT_FALSE(has_reflectable<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, isBitmask)
{
    ASSERT_TRUE(is_bitmask<DummyBitmask>::value);
    ASSERT_EQ(0, DummyBitmask().getValue());
    ASSERT_FALSE(is_bitmask<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(is_bitmask<DummyObjectInitialize>::value);
    ASSERT_FALSE(is_bitmask<std::string>::value);
    ASSERT_FALSE(is_bitmask<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, isFieldConstructorEnabled)
{
    assertTrue(has_field_ctor<int, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertTrue(has_field_ctor<std::string, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertTrue(has_field_ctor<
            DummyObjectInitializeChildren, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<DummyObjectInitialize, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<std::allocator<uint8_t>, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<BitStreamReader, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<PropagateAllocatorT, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<PackingContextNode, DummyObjectInitialize, std::allocator<uint8_t>>::value);
}

} // namespace zserio
