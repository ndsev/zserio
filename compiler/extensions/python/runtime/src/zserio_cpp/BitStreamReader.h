#ifndef ZSERIO_CPP_BIT_STREAM_READER_H_INC
#define ZSERIO_CPP_BIT_STREAM_READER_H_INC

#include "ZserioCpp.h"
#include "zserio/BitStreamReader.h"

namespace zserio_cpp
{

/**
 * Binds C++ bit stream reader to python runtime.
 *
 * \param module Module where to place the binding.
 */
void pybindBitStreamReader(py::module_ module);

} // namespace zserio_cpp

#endif // ZSERIO_CPP_BIT_STREAM_READER_H_INC
