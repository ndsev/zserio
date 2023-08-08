#ifndef ZSERIO_TRAITS_H_INC
#define ZSERIO_TRAITS_H_INC

#include <type_traits>
#include "zserio/RebindAlloc.h"

namespace zserio
{

// forward declaration
class BitStreamReader;
struct PropagateAllocatorT;
template <typename>
class BasicPackingContextNode;
template <typename, std::size_t>
class Span;

namespace detail
{

// These decltype's wrappers are needed because of old MSVC compiler 2015.
template <typename T, typename U = decltype(&T::initialize)>
struct decltype_initialize
{
    using type = U;
};

template <typename T, typename U = decltype(&T::initializeChildren)>
struct decltype_initialize_children
{
    using type = U;
};

template <typename T, typename U = decltype(std::declval<T>().reflectable())>
struct decltype_reflectable
{
    using type = U;
};

// declval is needed because otherwise MSVC 2015 states that std::allocator<int> does NOT have allocate method!
template <typename T, typename U = decltype(std::declval<T>().allocate(0))>
struct decltype_allocate
{
    using type = U;
};

template <typename T, typename U = decltype(std::declval<T>().deallocate(nullptr, 0))>
struct decltype_deallocate
{
    using type = U;
};

template <typename T, typename U = decltype(std::declval<T>().getValue())>
struct decltype_get_value
{
    using type = U;
};

template <typename T, typename U = decltype(&T::initializeOffset)>
struct decltype_initialize_offset
{
    using type = U;
};

template <typename T, typename U = decltype(&T::checkOffset)>
struct decltype_check_offset
{
    using type = U;
};

template <typename T, typename U = decltype(&T::initializeElement)>
struct decltype_initialize_element
{
    using type = U;
};

template <typename ...T>
using void_t = void;

} // namespace detail

/**
 * Trait used to check whether the type T is an allocator.
 * \{
 */
template <typename T, typename = void>
struct is_allocator : std::false_type
{};

template <typename T>
struct is_allocator<T, detail::void_t<typename detail::decltype_allocate<T>::type,
        typename detail::decltype_deallocate<T>::type>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the first type of ARGS is an allocator.
 * \{
 */
template <typename ...ARGS>
struct is_first_allocator : std::false_type
{};

template <typename T, typename ...ARGS>
struct is_first_allocator<T, ARGS...> : is_allocator<T>
{};
/** \} */

/**
 * Trait used to check whether the type has an OwnerType.
 * \{
 */
template <typename T, typename = void>
struct has_owner_type : std::false_type
{};

template <typename T>
struct has_owner_type<T, detail::void_t<typename T::OwnerType>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type has an ZserioPackingContext.
 * \{
 */
template <typename T, typename = void>
struct has_zserio_packing_context : std::false_type
{};

template <typename T>
struct has_zserio_packing_context<T, detail::void_t<typename T::ZserioPackingContext>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type has an allocator_type.
 * \{
 */
template <typename T, typename = void>
struct has_allocator : std::false_type
{};

template <typename T>
struct has_allocator<T, detail::void_t<typename T::allocator_type>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type T has initialize method.
 * \{
 */
template <typename T, typename = void>
struct has_initialize : std::false_type
{};

template <typename T>
struct has_initialize<T, detail::void_t<typename detail::decltype_initialize<T>::type>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type T has initializeChildren method.
 * \{
 */
template <typename T, typename = void>
struct has_initialize_children : std::false_type
{};

template <typename T>
struct has_initialize_children<T,
        detail::void_t<typename detail::decltype_initialize_children<T>::type>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type T has reflectable method.
 * \{
 */
template <typename T, typename = void>
struct has_reflectable : std::false_type
{};

template <typename T>
struct has_reflectable<T, detail::void_t<typename detail::decltype_reflectable<T>::type>> : std::true_type
{};
/** \} */

/**
 * Trait used to check whether the type T has initializeOffset method.
 * \{
 */
template <typename T, typename = void>
struct has_initialize_offset : std::false_type
{};

template <typename T>
struct has_initialize_offset<T,
        detail::void_t<typename detail::decltype_initialize_offset<T>::type>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to check whether the type T has checkOffset method.
 * \{
 */
template <typename T, typename = void>
struct has_check_offset : std::false_type
{};

template <typename T>
struct has_check_offset<T,
        detail::void_t<typename detail::decltype_check_offset<T>::type>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to check whether the type T has initializeElement method.
 * \{
 */
template <typename T, typename = void>
struct has_initialize_element : std::false_type
{};

template <typename T>
struct has_initialize_element<T,
        detail::void_t<typename detail::decltype_initialize_element<T>::type>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to check whether the type T is a zserio bitmask.
 * \{
 */
template <typename T, typename = void>
struct is_bitmask : std::false_type
{};

template <typename T>
struct is_bitmask<T, detail::void_t<typename detail::decltype_get_value<T>::type,
        typename T::underlying_type>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to check whether the type T is a Span.
 * \{
 */
template <typename>
struct is_span : std::false_type
{};

template <typename T, size_t Extent>
struct is_span<Span<T, Extent>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to enable field constructor only for suitable compound types (using SFINAE).
 * \{
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE, typename = void>
struct is_field_constructor_enabled : std::enable_if<
        !std::is_same<typename std::decay<FIELD_TYPE>::type, ALLOCATOR_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, BitStreamReader>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, COMPOUND_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, PropagateAllocatorT>::value,
        int>
{};

template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
struct is_field_constructor_enabled<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE,
        detail::void_t<typename COMPOUND_TYPE::ZserioPackingContext>> : std::enable_if<
        !std::is_same<typename std::decay<FIELD_TYPE>::type, ALLOCATOR_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, BitStreamReader>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, COMPOUND_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, PropagateAllocatorT>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type,
                typename COMPOUND_TYPE::ZserioPackingContext>::value,
        int>
{};
/** \} */

/**
 * Helper type used for convenient use of is_field_constructor_enabled.
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
using is_field_constructor_enabled_t =
        typename is_field_constructor_enabled<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE>::type;

} // namespace zserio

#endif // ifndef ZSERIO_TRAITS_H_INC
