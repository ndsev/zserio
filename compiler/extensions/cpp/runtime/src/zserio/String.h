#ifndef ZSERIO_STRING_H_INC
#define ZSERIO_STRING_H_INC

#include <string>

namespace zserio
{

template <typename ALLOC = std::allocator<char>>
using string = std::basic_string<char, std::char_traits<char>, ALLOC>;

} // namespace zserio

#endif // ZSERIO_STRING_H_INC
