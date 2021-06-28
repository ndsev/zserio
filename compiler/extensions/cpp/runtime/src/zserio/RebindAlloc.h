#ifndef ZSERIO_REBIND_ALLOC_H_INC
#define ZSERIO_REBIND_ALLOC_H_INC

#include <memory>

namespace zserio
{

template <typename ALLOC, typename T>
using RebindAlloc = typename std::allocator_traits<ALLOC>::template rebind_alloc<T>;

} // namespace zserio

#endif // ZSERIO_REBIND_ALLOC_H_INC
