#include "ZserioCpp.h"

#include "BitBuffer.h"
#include "BitStreamReader.h"
#include "BitStreamWriter.h"

PYBIND11_MODULE(zserio_cpp, module)
{
    zserio_cpp::pybindBitBuffer(module);
    zserio_cpp::pybindBitStreamReader(module);
    zserio_cpp::pybindBitStreamWriter(module);

    // ensure that all standard exceptions will be fired as PythonRuntimeException
    static py::exception<zserio::CppRuntimeException> pythonRuntimeException(module, "PythonRuntimeException");
    py::register_local_exception_translator([](std::exception_ptr exceptionPtr) {
        try
        {
            if (exceptionPtr)
                std::rethrow_exception(exceptionPtr);
        }
        catch (const std::exception& excpt)
        {
            pythonRuntimeException(excpt.what());
        }
    });
}
