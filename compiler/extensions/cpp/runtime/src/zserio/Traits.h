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
 * Trait used to check whether the type T has getValue method - i.e. whether it's a bitmask.
 * \{
 */
template <typename T, typename = void>
struct has_get_value : std::false_type
{};

template <typename T>
struct has_get_value<T, detail::void_t<typename detail::decltype_get_value<T>::type>> : std::true_type
{};
/**
 * \}
 */

/**
 * Trait used to enable field constructor only for suitable compound types (using SFINAE).
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
using is_field_constructor_enabled = std::enable_if<
        !std::is_same<typename std::decay<FIELD_TYPE>::type, ALLOCATOR_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, BitStreamReader>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, COMPOUND_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, PropagateAllocatorT>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type,
                BasicPackingContextNode<RebindAlloc<ALLOCATOR_TYPE, uint8_t>>>::value,
        int>;

/**
 * Helper type used for convenient use of is_field_constructor_enabled.
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
using is_field_constructor_enabled_t =
        typename is_field_constructor_enabled<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE>::type;

} // namespace zserio

#endif // ifndef ZSERIO_TRAITS_H_INC
