#ifndef ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC
#define ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC

#include <algorithm>
#include <iterator>
#include <type_traits>
#include <memory>
#include <vector>

#include "zserio/OptionalHolder.h"
#include "zserio/AnyHolder.h"

namespace zserio
{

/**
 * Helper type for specification of allocator propagation.
 */
struct PropagateAllocatorT
{
    constexpr explicit PropagateAllocatorT() = default;
};

/**
 * Constant used to convenient specification of allocator propagation.
 */
constexpr PropagateAllocatorT PropagateAllocator;

template <typename T, typename ALLOC>
T allocatorPropagatingCopy(const T& source, const ALLOC& allocator);

template <typename T, typename ALLOC, typename ALLOC2>
zserio::AnyHolder<ALLOC> allocatorPropagatingCopy(
    const zserio::AnyHolder<ALLOC>& source, const ALLOC2& allocator);

namespace detail
{

// implementation for std::basic_string from old std. libs, that does not fully conform
// to C++11. [old-compiler-support]
template <typename CharT, typename Traits, typename ALLOC1, typename ALLOC2>
std::basic_string<CharT, Traits, ALLOC1> allocatorPropagatingCopyDefault(std::true_type,
        const std::basic_string<CharT, Traits, ALLOC1>& source, const ALLOC2& allocator)
{
    return std::basic_string<CharT, Traits, ALLOC1>(source.c_str(), source.length(), allocator);
}

// implementation of copy for vectors of bool from old std. libs, that does not fully
// conform to C++11. [old-compiler-support]
template <typename ALLOC, typename ALLOC2>
std::vector<bool, ALLOC> allocatorPropagatingCopyVec(
        std::false_type, const std::vector<bool, ALLOC>& source, const ALLOC2& allocator)
{
     std::vector<bool, ALLOC> ret(allocator);
     ret.reserve(source.size());
     ret.assign(source.begin(), source.end());
     return ret;
}

// implementation of copy for "regular" classes that supports allocator
template <typename T, typename ALLOC>
T allocatorPropagatingCopyDefault(std::true_type, const T& source, const ALLOC& allocator)
{
    return T(source, allocator);
}

// implementation of copy for "regular" classes that does not support allocator
template <typename T, typename ALLOC>
T allocatorPropagatingCopyDefault(std::false_type, const T& source, const ALLOC&)
{
    return source;
}

// implementation of copy for "regular" classes that supports "PropagateAllocator" copy
template <typename T, typename ALLOC>
T allocatorPropagatingCopyPropagating(std::true_type, const T& source, const ALLOC& allocator)
{
    return T(PropagateAllocator, source, allocator);
}

// implementation of copy for "regular" classes that does not support "PropagateAllocator" copy
template <typename T, typename ALLOC>
T allocatorPropagatingCopyPropagating(std::false_type, const T& source, const ALLOC& allocator)
{
    return allocatorPropagatingCopyDefault(std::uses_allocator<T, ALLOC>(), source, allocator);
}

// implementation of copy for vectors containing type that supports allocator
template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyVec(
        std::true_type, const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    std::vector<T, ALLOC> result(allocator);
    result.reserve(source.size());
    std::transform(source.begin(), source.end(), std::back_inserter(result),
                   [&](const T& value){ return allocatorPropagatingCopy(value, allocator); });
    return result;
}

// implementation of copy for vectors containing type that does not support allocator
template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyVec(
        std::false_type, const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    return std::vector<T, ALLOC>(source, allocator);
}

template <typename T, typename ALLOC>
T allocatorPropagatingCopyImpl(const T& source, const ALLOC& allocator)
{
    return allocatorPropagatingCopyPropagating(
            std::is_constructible<T, PropagateAllocatorT, T, ALLOC>(), source, allocator);
}

template <typename T, typename ALLOC, typename ALLOC2>
zserio::HeapOptionalHolder<T, ALLOC> allocatorPropagatingCopyImpl(
        const zserio::HeapOptionalHolder<T, ALLOC>& source, const ALLOC2& allocator)
{
    if (source.hasValue())
        return zserio::HeapOptionalHolder<T, ALLOC>(allocatorPropagatingCopy(*source, allocator), allocator);
    else
        return zserio::HeapOptionalHolder<T, ALLOC>(allocator);
}

template <typename T, typename ALLOC>
zserio::InplaceOptionalHolder<T> allocatorPropagatingCopyImpl(
        const zserio::InplaceOptionalHolder<T>& source, const ALLOC& allocator)
{
    if (source.hasValue())
        return zserio::InplaceOptionalHolder<T>(allocatorPropagatingCopy(*source, allocator));
    else
        return zserio::InplaceOptionalHolder<T>();
}

template <typename T, typename ALLOC, typename ALLOC2>
zserio::AnyHolder<ALLOC> allocatorPropagatingCopyImpl(
        const zserio::AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    if (source.hasValue())
    {
        return zserio::AnyHolder<ALLOC>(allocatorPropagatingCopy(source.template get<T>(), allocator),
                allocator);
    }
    else
    {
        return zserio::AnyHolder<ALLOC>(allocator);
    }
}

template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyImpl(
        const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    return allocatorPropagatingCopyVec(std::uses_allocator<T, ALLOC>(), source, allocator);
}

} // namespace detail

/**
 * Copy the input object, propagating the allocator where needed.
 *
 * \param source Object to copy.
 * \param allocator Allocator to be propagated to the target object type constructor.
 *
 * \return Object copy.
 */
template <typename T, typename ALLOC>
T allocatorPropagatingCopy(const T& source, const ALLOC& allocator)
{
    static_assert(!std::is_same<zserio::AnyHolder<ALLOC>, T>::value, "Cannot be used for AnyHolder!");

    return detail::allocatorPropagatingCopyImpl(source, allocator);
}

/**
 * Copy the input any holder, propagating the allocator where needed.
 *
 * \param source Any holder to copy.
 * \param allocator Allocator to be propagated to the target object type constructor.
 *
 * \return Copy of any holder.
 */
template <typename T, typename ALLOC, typename ALLOC2>
zserio::AnyHolder<ALLOC> allocatorPropagatingCopy(
    const zserio::AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    return detail::allocatorPropagatingCopyImpl<T>(source, allocator);
}

} // namespace zserio

#endif // ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC
