#include "BitStreamReader.h"

namespace
{

std::unique_ptr<zserio::BitStreamReader> bitStreamReaderInit(const py::buffer_info& bufferInfo,
            py::object optionalBitSize)
{
    const uint8_t* buffer = static_cast<const uint8_t*>(bufferInfo.ptr);
    if (!optionalBitSize.is(py::none()))
    {
        const size_t bitSize = optionalBitSize.cast<size_t>();
        if ((bitSize + 7) / 8 > static_cast<size_t>(bufferInfo.size))
            throw zserio::CppRuntimeException("BitStreamReader: Bit size out of range for given buffer!");

        return std::make_unique<zserio::BitStreamReader>(buffer, bitSize, zserio::BitsTag());
    }
    else
    {
        return std::make_unique<zserio::BitStreamReader>(buffer, bufferInfo.size);
    }
}

} // namespace

namespace zserio_cpp
{

void pybindBitStreamReader(py::module_ module)
{
    py::class_<zserio::BitStreamReader>(module, "BitStreamReader")
            .def(py::init([](const py::object& buffer, py::object bitsize) {
                return bitStreamReaderInit(py::buffer(buffer).request(), bitsize);
            }), py::arg("buffer"), py::arg("bitsize") = py::none(), py::keep_alive<1, 2>())
            .def_static("from_bitbuffer", [](const zserio::BitBuffer& bitBuffer){
                return std::make_unique<zserio::BitStreamReader>(bitBuffer);
            }, py::keep_alive<0, 1>())
            .def("read_bits", [](zserio::BitStreamReader& self, py::int_ numbits){
                return self.readBits64(numbits.cast<uint8_t>());
            })
            .def("read_signed_bits", [](zserio::BitStreamReader& self, py::int_ numbits){
                return self.readSignedBits64(numbits.cast<uint8_t>());
            })
            .def("read_bits_unchecked", [](zserio::BitStreamReader& self, py::int_ numbits){
                return self.readBits64(numbits.cast<uint8_t>());
            })
            .def("read_signed_bits_unchecked", [](zserio::BitStreamReader& self, py::int_ numbits){
                return self.readSignedBits64(numbits.cast<uint8_t>());
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
            .def("alignto", [](zserio::BitStreamReader& self, py::int_ alignment) {
                self.alignTo(alignment.cast<size_t>());
            })
            .def_property("bitposition",
                    &zserio::BitStreamReader::getBitPosition,
                    [](zserio::BitStreamReader& self, py::int_ bitposition) {
                        self.setBitPosition(bitposition.cast<zserio::BitStreamReader::BitPosType>());
                    })
            .def_property_readonly("buffer_bitsize",
                    &zserio::BitStreamReader::getBufferBitSize)
            ;
}

} // namespace zserio_cpp
