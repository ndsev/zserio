#ifndef ZSERIO_CPP_BIT_BUFFER_H_INC
#define ZSERIO_CPP_BIT_BUFFER_H_INC

#include "ZserioCpp.h"
#include "zserio/BitBuffer.h"

namespace zserio_cpp
{

/**
 * Binds C++ bit buffer to python runtime.
 *
 * \param module Module where to place the binding.
 */
void pybindBitBuffer(py::module_ module);

} // namespace zserio_cpp

#endif // ZSERIO_CPP_BIT_BUFFER_H_INC
