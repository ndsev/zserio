#ifndef ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC
#define ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC

#include <algorithm>
#include <iterator>
#include <memory>
#include <type_traits>
#include <vector>

#include "zserio/AnyHolder.h"
#include "zserio/NoInit.h"
#include "zserio/OptionalHolder.h"

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
AnyHolder<ALLOC> allocatorPropagatingCopy(const AnyHolder<ALLOC>& source, const ALLOC2& allocator);

template <typename T, typename ALLOC>
T allocatorPropagatingCopy(NoInitT, const T& source, const ALLOC& allocator);

template <typename T, typename ALLOC, typename ALLOC2>
AnyHolder<ALLOC> allocatorPropagatingCopy(NoInitT, const AnyHolder<ALLOC>& source, const ALLOC2& allocator);

namespace detail
{

// implementation for std::basic_string from old std. libs, that does not fully conform
// to C++11. [old-compiler-support]
template <typename CharT, typename Traits, typename ALLOC1, typename ALLOC2>
std::basic_string<CharT, Traits, ALLOC1> allocatorPropagatingCopyDefault(
        std::true_type, const std::basic_string<CharT, Traits, ALLOC1>& source, const ALLOC2& allocator)
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

// implementation of copy for "regular" classes that supports "PropagateAllocator" copy
template <typename T, typename ALLOC>
T allocatorPropagatingCopyPropagating(std::true_type, NoInitT, const T& source, const ALLOC& allocator)
{
    return T(PropagateAllocator, NoInit, source, allocator);
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
    std::transform(source.begin(), source.end(), std::back_inserter(result), [&](const T& value) {
        return allocatorPropagatingCopy(value, allocator);
    });
    return result;
}

// implementation of copy for vectors containing type that supports allocator
template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyVec(
        std::true_type, NoInitT, const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    std::vector<T, ALLOC> result(allocator);
    result.reserve(source.size());
    std::transform(source.begin(), source.end(), std::back_inserter(result), [&](const T& value) {
        return allocatorPropagatingCopy(NoInit, value, allocator);
    });
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

template <typename T, typename ALLOC>
T allocatorPropagatingCopyImpl(NoInitT, const T& source, const ALLOC& allocator)
{
    static_assert(std::is_constructible<T, NoInitT, T>::value, "Can be used only for parameterized compounds!");

    return allocatorPropagatingCopyPropagating(
            std::is_constructible<T, PropagateAllocatorT, NoInitT, T, ALLOC>(), NoInit, source, allocator);
}

template <typename T, typename ALLOC, typename ALLOC2>
HeapOptionalHolder<T, ALLOC> allocatorPropagatingCopyImpl(
        const HeapOptionalHolder<T, ALLOC>& source, const ALLOC2& allocator)
{
    if (source.hasValue())
    {
        return HeapOptionalHolder<T, ALLOC>(allocatorPropagatingCopy(*source, allocator), allocator);
    }
    else
    {
        return HeapOptionalHolder<T, ALLOC>(allocator);
    }
}

template <typename T, typename ALLOC, typename ALLOC2>
HeapOptionalHolder<T, ALLOC> allocatorPropagatingCopyImpl(
        NoInitT, const HeapOptionalHolder<T, ALLOC>& source, const ALLOC2& allocator)
{
    static_assert(std::is_constructible<T, NoInitT, T>::value, "Can be used only for parameterized compounds!");

    if (source.hasValue())
    {
        return HeapOptionalHolder<T, ALLOC>(
                NoInit, allocatorPropagatingCopy(NoInit, *source, allocator), allocator);
    }
    else
    {
        return HeapOptionalHolder<T, ALLOC>(allocator);
    }
}

template <typename T, typename ALLOC>
InplaceOptionalHolder<T> allocatorPropagatingCopyImpl(
        const InplaceOptionalHolder<T>& source, const ALLOC& allocator)
{
    if (source.hasValue())
    {
        return InplaceOptionalHolder<T>(allocatorPropagatingCopy(*source, allocator));
    }
    else
    {
        return InplaceOptionalHolder<T>();
    }
}

template <typename T, typename ALLOC>
InplaceOptionalHolder<T> allocatorPropagatingCopyImpl(
        NoInitT, const InplaceOptionalHolder<T>& source, const ALLOC& allocator)
{
    static_assert(std::is_constructible<T, NoInitT, T>::value, "Can be used only for parameterized compounds!");

    if (source.hasValue())
    {
        return InplaceOptionalHolder<T>(NoInit, allocatorPropagatingCopy(NoInit, *source, allocator));
    }
    else
    {
        return InplaceOptionalHolder<T>();
    }
}

template <typename T, typename ALLOC, typename ALLOC2>
AnyHolder<ALLOC> allocatorPropagatingCopyImpl(const AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    if (source.hasValue())
    {
        return AnyHolder<ALLOC>(allocatorPropagatingCopy(source.template get<T>(), allocator), allocator);
    }
    else
    {
        return AnyHolder<ALLOC>(allocator);
    }
}

template <typename T, typename ALLOC, typename ALLOC2>
AnyHolder<ALLOC> allocatorPropagatingCopyImpl(NoInitT, const AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    if (source.hasValue())
    {
        return AnyHolder<ALLOC>(
                NoInit, allocatorPropagatingCopy(NoInit, source.template get<T>(), allocator), allocator);
    }
    else
    {
        return AnyHolder<ALLOC>(allocator);
    }
}

template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyImpl(const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    return allocatorPropagatingCopyVec(std::uses_allocator<T, ALLOC>(), source, allocator);
}

template <typename T, typename ALLOC, typename ALLOC2>
std::vector<T, ALLOC> allocatorPropagatingCopyImpl(
        NoInitT, const std::vector<T, ALLOC>& source, const ALLOC2& allocator)
{
    static_assert(std::is_constructible<T, NoInitT, T>::value, "Can be used only for parameterized compounds!");

    return allocatorPropagatingCopyVec(std::uses_allocator<T, ALLOC>(), NoInit, source, allocator);
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
    static_assert(!std::is_same<AnyHolder<ALLOC>, T>::value, "Cannot be used for AnyHolder!");

    return detail::allocatorPropagatingCopyImpl(source, allocator);
}

/**
 * Copy the input object, propagating the allocator where needed and prevents initialization.
 *
 * \param source Object to copy.
 * \param allocator Allocator to be propagated to the target object type constructor.
 *
 * \return Object copy.
 */
template <typename T, typename ALLOC>
T allocatorPropagatingCopy(NoInitT, const T& source, const ALLOC& allocator)
{
    static_assert(!std::is_same<AnyHolder<ALLOC>, T>::value, "Cannot be used for AnyHolder!");

    return detail::allocatorPropagatingCopyImpl(NoInit, source, allocator);
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
AnyHolder<ALLOC> allocatorPropagatingCopy(const AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    return detail::allocatorPropagatingCopyImpl<T>(source, allocator);
}

/**
 * Copy the input any holder, propagating the allocator where needed and prevents initialization.
 *
 * \param source Any holder to copy.
 * \param allocator Allocator to be propagated to the target object type constructor.
 *
 * \return Copy of any holder.
 */
template <typename T, typename ALLOC, typename ALLOC2>
AnyHolder<ALLOC> allocatorPropagatingCopy(NoInitT, const AnyHolder<ALLOC>& source, const ALLOC2& allocator)
{
    static_assert(std::is_constructible<T, NoInitT, T>::value, "Can be used only for parameterized compounds!");

    return detail::allocatorPropagatingCopyImpl<T>(NoInit, source, allocator);
}

} // namespace zserio

#endif // ZSERIO_ALLOCATOR_PROPAGATING_COPY_H_INC
