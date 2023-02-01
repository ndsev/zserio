#include "BitStreamWriter.h"

namespace zserio_cpp
{

void pybindBitStreamWriter(py::module_ module)
{
    py::class_<zserio_cpp::BitStreamWriter>(module, "BitStreamWriter")
            .def(py::init())
            .def("write_bits", [](zserio_cpp::BitStreamWriter& self, py::int_ value, py::int_ numbits) {
                self.write(&zserio::BitStreamWriter::writeBits64,
                        value.cast<uint64_t>(), numbits.cast<uint8_t>());
            })
            .def("write_bits_unchecked", [](zserio_cpp::BitStreamWriter& self,
                    py::int_ value, py::int_ numbits) {
                self.write(&zserio::BitStreamWriter::writeBits64,
                        value.cast<uint64_t>(), numbits.cast<uint8_t>());
            })
            .def("write_signed_bits", [](zserio_cpp::BitStreamWriter& self, py::int_ value, py::int_ numbits) {
                self.write(&zserio::BitStreamWriter::writeSignedBits64,
                        value.cast<int64_t>(), numbits.cast<uint8_t>());
            })
            .def("write_signed_bits_unchecked", [](zserio_cpp::BitStreamWriter& self,
                    py::int_ value, py::int_ numbits) {
                self.write(&zserio::BitStreamWriter::writeSignedBits64,
                        value.cast<int64_t>(), numbits.cast<uint8_t>());
            })
            .def("write_varint16", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarInt16, value.cast<int16_t>());
            })
            .def("write_varint32", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarInt32, value.cast<int32_t>());
            })
            .def("write_varint64", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarInt64, value.cast<int64_t>());
            })
            .def("write_varint", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarInt, value.cast<int64_t>());
            })
            .def("write_varuint16", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarUInt16, value.cast<uint16_t>());
            })
            .def("write_varuint32", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarUInt32, value.cast<uint32_t>());
            })
            .def("write_varuint64", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarUInt64, value.cast<uint64_t>());
            })
            .def("write_varuint", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarUInt, value.cast<uint64_t>());
            })
            .def("write_varsize", [](zserio_cpp::BitStreamWriter& self, py::int_ value) {
                self.write(&zserio::BitStreamWriter::writeVarSize, value.cast<uint32_t>());
            })
            .def("write_float16", [](zserio_cpp::BitStreamWriter& self, py::object value) {
                self.write(&zserio::BitStreamWriter::writeFloat16, value.cast<float>());
            })
            .def("write_float32", [](zserio_cpp::BitStreamWriter& self, py::object value) {
                self.write(&zserio::BitStreamWriter::writeFloat32, value.cast<float>());
            })
            .def("write_float64", [](zserio_cpp::BitStreamWriter& self, py::object value) {
                self.write(&zserio::BitStreamWriter::writeFloat64, value.cast<double>());
            })
            .def("write_bytes", [](zserio_cpp::BitStreamWriter& self, const py::object& value) {
                py::buffer_info bufferInfo = py::buffer(value).request();
                self.write(&zserio::BitStreamWriter::writeBytes,
                        zserio::Span<const uint8_t>(
                                static_cast<const uint8_t*>(bufferInfo.ptr), bufferInfo.size));
            })
            .def("write_string", [](zserio_cpp::BitStreamWriter& self, const std::string& value) {
                self.write(&zserio::BitStreamWriter::writeString, value);
            })
            .def("write_bool", [](zserio_cpp::BitStreamWriter& self, py::bool_ value) {
                self.write(&zserio::BitStreamWriter::writeBool, value.cast<bool>());
            })
            .def("write_bitbuffer", [](zserio_cpp::BitStreamWriter& self, const zserio::BitBuffer& value) {
                self.write(&zserio::BitStreamWriter::writeBitBuffer<std::allocator<uint8_t>>, value);
            })
            .def_property_readonly("byte_array", py::cpp_function([](zserio_cpp::BitStreamWriter& self) {
                constexpr bool readOnly = true;
                return py::memoryview::from_memory(self.getWriteBuffer(), self.getBufferByteSize(), readOnly);
            }, py::keep_alive<0, 1>()))
            .def_property_readonly("bitposition", &zserio_cpp::BitStreamWriter::getBitPosition)
            .def("alignto", [](zserio_cpp::BitStreamWriter& self, py::int_ alignment) {
                self.write(&zserio::BitStreamWriter::alignTo, alignment.cast<size_t>());
            })
            .def("to_file", &zserio_cpp::BitStreamWriter::toFile)
            ;
}

} // namespace zserio_cpp
