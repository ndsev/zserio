#include <pybind11/pybind11.h>

#include "zserio/BitPositionUtil.h"
#include "zserio/BitStreamReader.h"
#include "zserio/BitStreamWriter.h"

namespace py = pybind11;

PYBIND11_MODULE(zserio_cpp, m)
{
    py::class_<zserio::BitStreamReader>(m, "BitStreamReader")
            // constructor from bytes
            .def(py::init([](const py::bytes& bytes) {
                py::buffer_info info(py::buffer(bytes).request());
                return std::make_unique<zserio::BitStreamReader>(
                        static_cast<const uint8_t*>(info.ptr), info.size);
            }), py::keep_alive<1, 2>())
            // constructor from bytearray
            .def(py::init([](const py::bytearray& bytearray) {
                PyObject* obj = bytearray.ptr();
                const char* ptr = PyByteArray_AsString(obj);
                const size_t size = PyByteArray_Size(obj);

                return std::make_unique<zserio::BitStreamReader>(
                        reinterpret_cast<const uint8_t*>(ptr), size);
            }), py::keep_alive<1, 2>())
            // constructor from memoryview
            .def(py::init([](const py::memoryview& memoryview) {
                py::buffer_info info(py::buffer(memoryview).request());
                return std::make_unique<zserio::BitStreamReader>(
                        static_cast<const uint8_t*>(info.ptr), info.size);
            }), py::keep_alive<1, 2>())
            .def("read_bits", &zserio::BitStreamReader::readBits64)
            .def("read_signed_bits", &zserio::BitStreamReader::readSignedBits64)
            .def("read_varint16", &zserio::BitStreamReader::readVarInt16)
            .def("read_varint32", &zserio::BitStreamReader::readVarInt32)
            .def("read_varint64", &zserio::BitStreamReader::readVarInt64)
            .def("read_varint", &zserio::BitStreamReader::readVarInt)
            .def("read_varuint16", &zserio::BitStreamReader::readVarUInt16)
            .def("read_varuint32", &zserio::BitStreamReader::readVarUInt32)
            .def("read_varuint64", &zserio::BitStreamReader::readVarUInt64)
            .def("read_varuint", &zserio::BitStreamReader::readVarUInt)
            .def("read_varsize", &zserio::BitStreamReader::readVarSize)
            .def("read_float16", &zserio::BitStreamReader::readFloat16)
            .def("read_float32", &zserio::BitStreamReader::readFloat32)
            .def("read_float64", &zserio::BitStreamReader::readFloat64)
            //.def("read_bytes", &zserio::BitStreamReader::readBytes)
            //.def("read_string", &zserio::BitStreamReader::readString)
            .def("read_bool", &zserio::BitStreamReader::readBool)
            //.def("read_bitbuffer", &zserio::BitStreamReader::readBitBuffer)
            .def("alignto", &zserio::BitStreamReader::alignTo)
            .def_property("bitposition",
                    &zserio::BitStreamReader::getBitPosition,
                    &zserio::BitStreamReader::setBitPosition)
            .def_property_readonly("buffer_bitsize",
                    &zserio::BitStreamReader::getBufferBitSize)
            ;

    py::class_<zserio::BitStreamWriter>(m, "BitStreamWriter")
            .def(py::init<zserio::BitBuffer&>(), py::keep_alive<1, 2>())
            .def_property_readonly("byte_array", [](const zserio::BitStreamWriter& writer){
                constexpr bool readOnly = true;
                const void* buffer = writer.getWriteBuffer();
                return py::memoryview::from_memory(
                        const_cast<void*>(buffer), (writer.getBufferBitSize() + 7) / 8, readOnly);
            }, py::keep_alive<0, 1>())
            ;

    py::class_<zserio::BitBuffer>(m, "BitBuffer")
            .def(py::init<size_t>())
            .def_property_readonly("buffer", [](zserio::BitBuffer& bitBuffer){
                constexpr bool readOnly = false;
                return py::memoryview::from_memory(
                        bitBuffer.getBuffer(), bitBuffer.getByteSize(), readOnly);
            }, py::keep_alive<0, 1>())
            .def_property_readonly("bitsize", &zserio::BitBuffer::getBitSize)
            ;
}
