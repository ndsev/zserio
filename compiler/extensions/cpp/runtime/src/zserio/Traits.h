#ifndef ZSERIO_TRAITS_H_INC
#define ZSERIO_TRAITS_H_INC

#include <type_traits>

namespace zserio
{

// forward declaration
class BitStreamReader;
struct PropagateAllocatorT;

/**
 * Trait used to enable field constructor only for suitable compound types (using SFINAE).
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
using is_field_constructor_enabled = std::enable_if<
        !std::is_same<typename std::decay<FIELD_TYPE>::type, ALLOCATOR_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, zserio::BitStreamReader>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, COMPOUND_TYPE>::value &&
        !std::is_same<typename std::decay<FIELD_TYPE>::type, zserio::PropagateAllocatorT>::value,
        int>;

/**
 * Helper type used for convenient use of is_field_constructor_enabled.
 */
template <typename FIELD_TYPE, typename COMPOUND_TYPE, typename ALLOCATOR_TYPE>
using is_field_constructor_enabled_t =
        typename is_field_constructor_enabled<FIELD_TYPE, COMPOUND_TYPE, ALLOCATOR_TYPE>::type;

} // namespace zserio

#endif // ifndef ZSERIO_TRAITS_H_INC
