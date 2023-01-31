#include "ZserioCpp.h"

#include "BitBuffer.h"
#include "BitStreamReader.h"
#include "BitStreamWriter.h"

PYBIND11_MODULE(zserio_cpp, module)
{
    zserio_cpp::pybindBitBuffer(module);
    zserio_cpp::pybindBitStreamReader(module);
    zserio_cpp::pybindBitStreamWriter(module);

    // exception handlers must be at the end to prevent cyclic imports!
    py::register_local_exception<zserio::CppRuntimeException>(module, "CppRuntimeException",
            py::module_::import("zserio.exception").attr("PythonRuntimeException"));
    // ensure that all standard exceptions will be fired as CppRuntimeException (caught by the first handler)
    py::register_local_exception_translator([](std::exception_ptr exceptionPtr) {
        try
        {
            if (exceptionPtr)
                std::rethrow_exception(exceptionPtr);
        }
        catch (const std::exception& excpt)
        {
            throw zserio::CppRuntimeException(excpt.what());
        }
    });
}
