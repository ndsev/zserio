#ifndef ZSERIO_ALIGNED_STORAGE_H_INC
#define ZSERIO_ALIGNED_STORAGE_H_INC

#include <cstddef>

#include "Types.h"

namespace zserio
{

namespace detail
{

struct A1 { uint8_t a; };
struct A2 { uint16_t a; };
struct A4 { uint32_t a; };
struct A8 { uint64_t a; };

template <size_t A> struct aligned;
template<> struct aligned<1> { typedef A1 type; };
template<> struct aligned<2> { typedef A2 type; };
template<> struct aligned<4> { typedef A4 type; };
template<> struct aligned<8> { typedef A8 type; };

template <size_t A>
struct aligned
{
    static const size_t nextA = A > 8 ? 8 : A + 1;
    typedef typename aligned<nextA>::type type;
};

template <typename T>
struct alignment_of_hack
{
    char c;
    T t;
    alignment_of_hack();
};

template <unsigned A, unsigned S>
struct alignment_logic
{
    static const size_t value = A < S ? A : S;
};

template <typename T>
struct alignment_of
{
    static const size_t value = alignment_logic< sizeof(alignment_of_hack<T>) - sizeof(T), sizeof(T) >::value;
};

} // namespace detail

template <typename T>
struct AlignedStorage
{
    union type
    {
        unsigned char data[sizeof(T)];
        typename detail::aligned<detail::alignment_of<T>::value>::type align;
    };
};

} // namespace zserio

#endif // ifndef ZSERIO_ALIGNED_STORAGE_H_INC
