#ifndef ZSERIO_STRING_H_INC
#define ZSERIO_STRING_H_INC

#include <string>
#include "zserio/RebindAlloc.h"

namespace zserio
{

/**
 * Typedef to std::string provided for convenience - using std::allocator<uint8_t>.
 *
 * Automatically rebinds the given allocator.
 */
template <typename ALLOC = std::allocator<char>>
using string = std::basic_string<char, std::char_traits<char>, RebindAlloc<ALLOC, char>>;

} // namespace zserio

#endif // ZSERIO_STRING_H_INC
