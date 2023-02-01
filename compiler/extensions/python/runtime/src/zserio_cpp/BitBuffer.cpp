#include "BitBuffer.h"

namespace
{

zserio::BitBuffer bitBufferInit(const py::buffer_info& bufferInfo, py::object optionalBitSize)
{
    uint8_t* buffer = static_cast<uint8_t*>(bufferInfo.ptr);
    if (!optionalBitSize.is(py::none()))
    {
        const size_t bitSize = optionalBitSize.cast<size_t>();
        if ((bitSize + 7) / 8 > static_cast<size_t>(bufferInfo.size))
            throw zserio::CppRuntimeException("BitBuffer: Bit size out of range for given buffer!");

        return zserio::BitBuffer(buffer, bitSize);
    }
    else
    {
        return zserio::BitBuffer(buffer, bufferInfo.size * 8);
    }
}

} // namespace

namespace zserio_cpp
{

void pybindBitBuffer(py::module_ module)
{
    py::class_<zserio::BitBuffer>(module, "BitBuffer")
            .def(py::init([](const py::object& buffer, py::object bitsize) {
                return bitBufferInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none())
            .def_property_readonly("buffer", py::cpp_function([](zserio::BitBuffer& self){
                constexpr bool readOnly = false;
                return py::memoryview::from_memory(self.getBuffer(), self.getByteSize(), readOnly);
            }, py::keep_alive<0, 1>()))
            .def_property_readonly("bitsize", &zserio::BitBuffer::getBitSize)
            .def(py::self == py::self)
            .def("__hash__", &zserio::BitBuffer::hashCode)
            ;
}

} // namespace zserio_cpp
