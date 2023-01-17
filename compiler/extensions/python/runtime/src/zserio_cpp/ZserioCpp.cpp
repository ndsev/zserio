#include <pybind11/pybind11.h>
#include <pybind11/stl.h>
#include <pybind11/operators.h>

#include "zserio/BitPositionUtil.h"
#include "zserio/BitStreamReader.h"

namespace py = pybind11;

namespace
{
    std::unique_ptr<zserio::BitStreamReader> bitStreamReaderInit(const py::buffer_info& bufferInfo,
            std::optional<py::int_> optionalBitSize)
    {
        const uint8_t* buffer = static_cast<const uint8_t*>(bufferInfo.ptr);
        if (optionalBitSize)
        {
            const size_t bitSize = *optionalBitSize;
            if (bitSize < 0)
                throw zserio::CppRuntimeException("BitStreamReader: Bit size cannot be negative!");
            if ((bitSize + 7) / 8 > static_cast<size_t>(bufferInfo.size))
                throw zserio::CppRuntimeException("BitStreamReader: Bit size out of range for given buffer!");

            return std::make_unique<zserio::BitStreamReader>(buffer, bitSize, zserio::BitsTag());
        }
        else
        {
            return std::make_unique<zserio::BitStreamReader>(buffer, bufferInfo.size);
        }
    }

    zserio::BitBuffer bitBufferInit(const py::buffer_info& bufferInfo, std::optional<py::int_> optionalBitSize)
    {
        uint8_t* buffer = static_cast<uint8_t*>(bufferInfo.ptr);
        if (optionalBitSize)
        {
            const size_t bitSize = *optionalBitSize;
            if (bitSize < 0)
                throw zserio::CppRuntimeException("BitBuffer: Bit size cannot be negative!");
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

PYBIND11_MODULE(zserio_cpp, m)
{
    py::class_<zserio::BitStreamReader>(m, "BitStreamReader")
            // constructor from bytes
            .def(py::init([](const py::bytes& buffer, std::optional<py::int_> bitsize) {
                return bitStreamReaderInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none(), py::keep_alive<1, 2>())
            // constructor from bytearray
            .def(py::init([](const py::bytearray& buffer, std::optional<py::int_> bitsize) {
                return bitStreamReaderInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none(), py::keep_alive<1, 2>())
            // constructor from memoryview
            .def(py::init([](const py::memoryview& buffer, std::optional<py::int_> bitsize) {
                return bitStreamReaderInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none(), py::keep_alive<1, 2>())
            .def_static("from_bitbuffer", [](const zserio::BitBuffer& bitBuffer){
                return std::make_unique<zserio::BitStreamReader>(bitBuffer);
            }, py::keep_alive<0, 1>())
            .def("read_bits", [](zserio::BitStreamReader& self, py::int_ numbits){
                if (numbits < 0)
                    throw zserio::CppRuntimeException("BitStreamReader: Reading negative number of bits!");
                return self.readBits64(numbits);
            })
            .def("read_signed_bits", [](zserio::BitStreamReader& self, py::int_ numbits){
                if (numbits < 0)
                    throw zserio::CppRuntimeException("BitStreamReader: Reading negative number of bits!");
                return self.readSignedBits64(numbits);
            })
            .def("read_bits_unchecked", [](zserio::BitStreamReader& self, py::int_ numbits){
                if (numbits < 0)
                    throw zserio::CppRuntimeException("BitStreamReader: Reading negative number of bits!");
                return self.readBits64(numbits);
            })
            .def("read_signed_bits_unchecked", [](zserio::BitStreamReader& self, py::int_ numbits){
                if (numbits < 0)
                    throw zserio::CppRuntimeException("BitStreamReader: Reading negative number of bits!");
                return self.readSignedBits64(numbits);
            })
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
            .def("read_bytes", [](zserio::BitStreamReader& self){
                auto bytes = self.readBytes();
                return py::bytearray(reinterpret_cast<char*>(bytes.data()), bytes.size());
            })
            .def("read_string", [](zserio::BitStreamReader& self){
                return self.readString();
            })
            .def("read_bool", &zserio::BitStreamReader::readBool)
            .def("read_bitbuffer", [](zserio::BitStreamReader& self) {
                return self.readBitBuffer();
            })
            .def("alignto", &zserio::BitStreamReader::alignTo)
            .def_property("bitposition",
                    &zserio::BitStreamReader::getBitPosition,
                    [](zserio::BitStreamReader& self, py::int_ bitposition) {
                        if (bitposition < 0)
                        {
                            throw zserio::CppRuntimeException(
                                    "BitStreamReader: Cannot set negative bit position!");
                        }
                        self.setBitPosition(bitposition);
                    })
            .def_property_readonly("buffer_bitsize",
                    &zserio::BitStreamReader::getBufferBitSize)
            ;

    py::class_<zserio::BitBuffer>(m, "BitBuffer")
            // constructor from bytes
            .def(py::init([](const py::bytes& buffer, std::optional<py::int_> bitsize) {
                return bitBufferInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none())
            // constructor from bytearray
            .def(py::init([](const py::bytearray& buffer, std::optional<py::int_> bitsize) {
                return bitBufferInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none())
            // constructor from memoryview
            .def(py::init([](const py::memoryview& buffer, std::optional<py::int_> bitsize) {
                return bitBufferInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none())
            .def_property_readonly("buffer", [](zserio::BitBuffer& self){
                constexpr bool readOnly = false;
                return py::memoryview::from_memory(self.getBuffer(), self.getByteSize(), readOnly);
            }, py::keep_alive<0, 1>())
            .def_property_readonly("bitsize", &zserio::BitBuffer::getBitSize)
            .def(py::self == py::self)
            .def("__hash__", &zserio::BitBuffer::hashCode)
            ;

    // must be at the end to prevent cyclic imports!
    py::register_local_exception<zserio::CppRuntimeException>(m, "CppRuntimeException",
            py::module_::import("zserio.exception").attr("PythonRuntimeException"));
}
