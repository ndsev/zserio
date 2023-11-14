#include "gtest/gtest.h"

#include <memory>
#include <vector>

#include "zserio/Traits.h"
#include "zserio/AllocatorPropagatingCopy.h"
#include "zserio/AnyHolder.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"
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

class DummyOwner
{};

class DummyObjectWithOwnerType
{
public:
    using OwnerType = DummyOwner;
};

class DummyObjectWithAllocator
{
public:
    using allocator_type = std::allocator<uint8_t>;
};

class DummyObjectReflectable
{
public:
    IReflectablePtr reflectable()
    {
        return nullptr;
    }
};

class DummyObjectWithPackingContext
{
public:
    struct ZserioPackingContext
    {};
};

class DummyObjectInitializeOffset
{
public:
    using OwnerType = DummyOwner;

    void initializeOffset(OwnerType&, size_t, size_t) {}
};

class DummyObjectCheckOffset
{
public:
    using OwnerType = DummyOwner;

    void checkOffset(const OwnerType&, size_t, size_t) {}
};

class DummyObjectInitializeElement
{
public:
    using OwnerType = DummyOwner;

    void initializeElement(OwnerType&, DummyObjectInitializeChildren&, size_t) {}
};

enum class DummyEnum : uint8_t
{
    ONE,
    TWO
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
    ASSERT_FALSE(is_allocator<DummyObjectWithAllocator>::value);
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
    assertFalse(is_first_allocator<DummyObjectWithAllocator, pmr::PolymorphicAllocator<uint8_t>>::value);
}

TEST(TraitsTest, hasOwnerType)
{
    ASSERT_TRUE(has_owner_type<DummyObjectWithOwnerType>::value);
    ASSERT_TRUE(has_owner_type<DummyObjectInitializeOffset>::value);
    ASSERT_TRUE(has_owner_type<DummyObjectCheckOffset>::value);
    ASSERT_TRUE(has_owner_type<DummyObjectInitializeElement>::value);
    ASSERT_FALSE(has_owner_type<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_owner_type<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_owner_type<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_owner_type<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_owner_type<DummyEnum>::value);
    ASSERT_FALSE(has_owner_type<DummyBitmask>::value);
    ASSERT_FALSE(has_owner_type<std::string>::value);
    ASSERT_FALSE(has_owner_type<std::vector<uint8_t>>::value);
    ASSERT_FALSE(has_owner_type<std::vector<DummyObjectWithOwnerType>>::value);
}

TEST(TraitsTest, hasZserioPackingContext)
{
    ASSERT_TRUE(has_zserio_packing_context<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyEnum>::value);
    ASSERT_FALSE(has_zserio_packing_context<DummyBitmask>::value);
    ASSERT_FALSE(has_zserio_packing_context<std::string>::value);
    ASSERT_FALSE(has_zserio_packing_context<std::vector<uint8_t>>::value);
    ASSERT_FALSE(has_zserio_packing_context<std::vector<DummyObjectWithPackingContext>>::value);
}

TEST(TraitsTest, hasAllocator)
{
    ASSERT_TRUE(has_allocator<DummyObjectWithAllocator>::value);
    ASSERT_TRUE(has_allocator<std::string>::value);
    ASSERT_TRUE(has_allocator<std::vector<uint8_t>>::value);
    ASSERT_TRUE(has_allocator<std::vector<DummyObjectWithAllocator>>::value);
    ASSERT_FALSE(has_allocator<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_allocator<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_allocator<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_allocator<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_allocator<DummyEnum>::value);
    ASSERT_FALSE(has_allocator<DummyBitmask>::value);
}

TEST(TraitsTest, hasInitialize)
{
    ASSERT_TRUE(has_initialize<DummyObjectInitialize>::value);
    DummyObjectInitialize().initialize();
    ASSERT_FALSE(has_initialize<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_initialize<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_initialize<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_initialize<DummyEnum>::value);
    ASSERT_FALSE(has_initialize<DummyBitmask>::value);
    ASSERT_FALSE(has_initialize<std::string>::value);
    ASSERT_FALSE(has_initialize<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, hasInitializeChildren)
{
    ASSERT_TRUE(has_initialize_children<DummyObjectInitializeChildren>::value);
    DummyObjectInitializeChildren().initializeChildren();
    ASSERT_FALSE(has_initialize_children<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_initialize_children<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_initialize_children<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_initialize_children<DummyEnum>::value);
    ASSERT_FALSE(has_initialize_children<DummyBitmask>::value);
    ASSERT_FALSE(has_initialize_children<std::string>::value);
    ASSERT_FALSE(has_initialize_children<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, hasReflectable)
{
    ASSERT_TRUE(has_reflectable<DummyObjectReflectable>::value);
    ASSERT_EQ(nullptr, DummyObjectReflectable().reflectable());
    ASSERT_FALSE(has_reflectable<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_reflectable<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_reflectable<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(has_reflectable<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_reflectable<DummyEnum>::value);
    ASSERT_FALSE(has_reflectable<DummyBitmask>::value);
    ASSERT_FALSE(has_reflectable<std::string>::value);
    ASSERT_FALSE(has_reflectable<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, hasInitializeOffset)
{
    ASSERT_TRUE(has_initialize_offset<DummyObjectInitializeOffset>::value);
    DummyOwner owner;
    DummyObjectInitializeOffset().initializeOffset(owner, 0, 0);
    ASSERT_FALSE(has_initialize_offset<DummyObjectCheckOffset>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectInitializeElement>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectReflectable>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_initialize_offset<DummyObjectWithPackingContext>::value);
}

TEST(TraitsTest, hasCheckOffset)
{
    ASSERT_TRUE(has_check_offset<DummyObjectCheckOffset>::value);
    DummyObjectCheckOffset().checkOffset(DummyOwner(), 0, 0);
    ASSERT_FALSE(has_check_offset<DummyObjectInitializeOffset>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectInitializeElement>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectReflectable>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_check_offset<DummyObjectWithPackingContext>::value);
}

TEST(TraitsTest, hasInitializeElement)
{
    ASSERT_TRUE(has_initialize_element<DummyObjectInitializeElement>::value);
    DummyOwner owner;
    DummyObjectInitializeChildren element;
    DummyObjectInitializeElement().initializeElement(owner, element, 0);
    ASSERT_FALSE(has_initialize_element<DummyObjectInitializeOffset>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectCheckOffset>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectInitialize>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectReflectable>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(has_initialize_element<DummyObjectWithPackingContext>::value);
}

TEST(TraitsTest, isBitmask)
{
    ASSERT_TRUE(is_bitmask<DummyBitmask>::value);
    ASSERT_EQ(0, DummyBitmask().getValue());
    ASSERT_FALSE(is_bitmask<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(is_bitmask<DummyObjectInitialize>::value);
    ASSERT_FALSE(is_bitmask<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(is_bitmask<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(is_bitmask<DummyObjectWithAllocator>::value);
    ASSERT_FALSE(is_bitmask<DummyEnum>::value);
    ASSERT_FALSE(is_bitmask<std::string>::value);
    ASSERT_FALSE(is_bitmask<std::vector<uint8_t>>::value);
}

TEST(TraitsTest, isSpan)
{
    ASSERT_TRUE(is_span<zserio::Span<uint8_t>>::value);
    ASSERT_TRUE(is_span<zserio::Span<const uint8_t>>::value);
    ASSERT_TRUE(is_span<zserio::Span<char* const>>::value);
    ASSERT_TRUE(is_span<zserio::Span<char*>>::value);
    assertFalse(is_span<std::array<uint8_t, 2>>::value);
    ASSERT_FALSE(is_span<std::vector<uint8_t>>::value);
    ASSERT_FALSE(is_span<uint8_t>::value);
    ASSERT_FALSE(is_span<std::string>::value);
    ASSERT_FALSE(is_span<DummyObjectInitializeChildren>::value);
    ASSERT_FALSE(is_span<DummyObjectInitialize>::value);
    ASSERT_FALSE(is_span<DummyObjectWithOwnerType>::value);
    ASSERT_FALSE(is_span<DummyObjectWithPackingContext>::value);
    ASSERT_FALSE(is_span<DummyObjectWithAllocator>::value);
}

TEST(TraitsTest, isFieldConstructorEnabled)
{
    assertTrue(has_field_ctor<int, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertTrue(has_field_ctor<std::string, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertTrue(has_field_ctor<
            DummyObjectInitializeChildren, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertTrue(has_field_ctor<DummyObjectWithPackingContext::ZserioPackingContext,
            DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<DummyObjectInitialize, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<std::allocator<uint8_t>, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<BitStreamReader, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<PropagateAllocatorT, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<NoInitT, DummyObjectInitialize, std::allocator<uint8_t>>::value);
    assertFalse(has_field_ctor<DummyObjectWithPackingContext::ZserioPackingContext,
            DummyObjectWithPackingContext, std::allocator<uint8_t>>::value);
}

} // namespace zserio
