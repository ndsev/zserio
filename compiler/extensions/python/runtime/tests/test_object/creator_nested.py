# Automatically generated by Zserio Python extension version 2.9.0-pre1.
# Generator setup: writerCode, pubsubCode, serviceCode, sqlCode, typeInfoCode

from __future__ import annotations

import typing
import zserio

import test_object.creator_bitmask
import test_object.creator_enum

class CreatorNested:
    def __init__(
            self,
            param_: int,
            value_: int = int(),
            text_: str = str(),
            extern_data_: typing.Union[zserio.bitbuffer.BitBuffer, None] = None,
            bytes_data_: bytearray = bytearray(),
            creator_enum_: typing.Union[test_object.creator_enum.CreatorEnum, None] = None,
            creator_bitmask_: typing.Union[test_object.creator_bitmask.CreatorBitmask, None] = None) -> None:
        self._param_: int = param_
        self._value_ = value_
        self._text_ = text_
        self._extern_data_ = extern_data_
        self._bytes_data_ = bytes_data_
        self._creator_enum_ = creator_enum_
        self._creator_bitmask_ = creator_bitmask_

    @classmethod
    def from_reader(
            cls: typing.Type['CreatorNested'],
            zserio_reader: zserio.BitStreamReader,
            param_: int) -> 'CreatorNested':
        instance = cls(param_)
        instance.read(zserio_reader)

        return instance

    @classmethod
    def from_reader_packed(
            cls: typing.Type['CreatorNested'],
            zserio_context_node: zserio.array.PackingContextNode,
            zserio_reader: zserio.BitStreamReader,
            param_: int) -> 'CreatorNested':
        instance = cls(param_)
        instance.read_packed(zserio_context_node, zserio_reader)

        return instance

    @staticmethod
    def type_info() -> zserio.typeinfo.TypeInfo:
        field_list: typing.List[zserio.typeinfo.MemberInfo] = [
            zserio.typeinfo.MemberInfo(
                'value', zserio.typeinfo.TypeInfo('uint32', int),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'value'
                }
            ),
            zserio.typeinfo.MemberInfo(
                'text', zserio.typeinfo.TypeInfo('string', str),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'text'
                }
            ),
            zserio.typeinfo.MemberInfo(
                'externData', zserio.typeinfo.TypeInfo('extern', zserio.bitbuffer.BitBuffer),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'extern_data'
                }
            ),
            zserio.typeinfo.MemberInfo(
                'bytesData', zserio.typeinfo.TypeInfo('bytes', bytearray),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'bytes_data'
                }
            ),
            zserio.typeinfo.MemberInfo(
                'creatorEnum', test_object.creator_enum.CreatorEnum.type_info(),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'creator_enum'
                }
            ),
            zserio.typeinfo.MemberInfo(
                'creatorBitmask', test_object.creator_bitmask.CreatorBitmask.type_info(),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'creator_bitmask'
                }
            )
        ]
        parameter_list: typing.List[zserio.typeinfo.MemberInfo] = [
            zserio.typeinfo.MemberInfo(
                'param', zserio.typeinfo.TypeInfo('uint32', int),
                attributes={
                    zserio.typeinfo.MemberAttribute.PROPERTY_NAME : 'param'
                }
            )
        ]
        attribute_list = {
            zserio.typeinfo.TypeAttribute.FIELDS : field_list,
            zserio.typeinfo.TypeAttribute.PARAMETERS : parameter_list
        }

        return zserio.typeinfo.TypeInfo("test_object.CreatorNested", CreatorNested, attributes=attribute_list)

    def __eq__(self, other: object) -> bool:
        if isinstance(other, CreatorNested):
            return (self._param_ == other._param_ and
                    (self._value_ == other._value_) and
                    (self._text_ == other._text_) and
                    (self._extern_data_ == other._extern_data_) and
                    (self._bytes_data_ == other._bytes_data_) and
                    (self._creator_enum_ == other._creator_enum_) and
                    (self._creator_bitmask_ == other._creator_bitmask_))

        return False

    def __hash__(self) -> int:
        result = zserio.hashcode.HASH_SEED
        result = zserio.hashcode.calc_hashcode_int32(result, self._param_)
        result = zserio.hashcode.calc_hashcode_int32(result, self._value_)
        result = zserio.hashcode.calc_hashcode_string(result, self._text_)
        result = zserio.hashcode.calc_hashcode_object(result, self._extern_data_)
        result = zserio.hashcode.calc_hashcode_bytes(result, self._bytes_data_)
        result = zserio.hashcode.calc_hashcode_object(result, self._creator_enum_)
        result = zserio.hashcode.calc_hashcode_object(result, self._creator_bitmask_)

        return result

    @property
    def param(self) -> int:
        return self._param_

    @property
    def value(self) -> int:
        return self._value_

    @value.setter
    def value(self, value_: int) -> None:
        self._value_ = value_

    @property
    def text(self) -> str:
        return self._text_

    @text.setter
    def text(self, text_: str) -> None:
        self._text_ = text_

    @property
    def extern_data(self) -> typing.Union[zserio.bitbuffer.BitBuffer, None]:
        return self._extern_data_

    @extern_data.setter
    def extern_data(self, extern_data_: typing.Union[zserio.bitbuffer.BitBuffer, None]) -> None:
        self._extern_data_ = extern_data_

    @property
    def bytes_data(self) -> bytearray:
        return self._bytes_data_

    @bytes_data.setter
    def bytes_data(self, bytes_data_: bytearray) -> None:
        self._bytes_data_ = bytes_data_

    @property
    def creator_enum(self) -> typing.Union[test_object.creator_enum.CreatorEnum, None]:
        return self._creator_enum_

    @creator_enum.setter
    def creator_enum(self, creator_enum_: typing.Union[test_object.creator_enum.CreatorEnum, None]) -> None:
        self._creator_enum_ = creator_enum_

    @property
    def creator_bitmask(self) -> typing.Union[test_object.creator_bitmask.CreatorBitmask, None]:
        return self._creator_bitmask_

    @creator_bitmask.setter
    def creator_bitmask(self, creator_bitmask_: typing.Union[test_object.creator_bitmask.CreatorBitmask, None]) -> None:
        self._creator_bitmask_ = creator_bitmask_

    @staticmethod
    def create_packing_context(zserio_context_node: zserio.array.PackingContextNode) -> None:
        zserio_context_node.create_child().create_context()
        zserio_context_node.create_child()
        zserio_context_node.create_child()
        zserio_context_node.create_child()
        test_object.creator_enum.CreatorEnum.create_packing_context(zserio_context_node.create_child())
        test_object.creator_bitmask.CreatorBitmask.create_packing_context(zserio_context_node.create_child())

    def init_packing_context(self, zserio_context_node: zserio.array.PackingContextNode) -> None:
        zserio_context_node.children[0].context.init(zserio.array.BitFieldArrayTraits(32), self._value_)
        self._creator_enum_.init_packing_context(zserio_context_node.children[4])
        self._creator_bitmask_.init_packing_context(zserio_context_node.children[5])

    def bitsizeof(self, bitposition: int = 0) -> int:
        end_bitposition = bitposition
        end_bitposition += 32
        end_bitposition += zserio.bitsizeof.bitsizeof_string(self._text_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bitbuffer(self._extern_data_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bytes(self._bytes_data_)
        end_bitposition += self._creator_enum_.bitsizeof(end_bitposition)
        end_bitposition += self._creator_bitmask_.bitsizeof(end_bitposition)

        return end_bitposition - bitposition

    def bitsizeof_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                         bitposition: int = 0) -> int:
        end_bitposition = bitposition
        end_bitposition += zserio_context_node.children[0].context.bitsizeof(zserio.array.BitFieldArrayTraits(32), self._value_)
        end_bitposition += zserio.bitsizeof.bitsizeof_string(self._text_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bitbuffer(self._extern_data_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bytes(self._bytes_data_)
        end_bitposition += self._creator_enum_.bitsizeof_packed(zserio_context_node.children[4], end_bitposition)
        end_bitposition += self._creator_bitmask_.bitsizeof_packed(zserio_context_node.children[5], end_bitposition)

        return end_bitposition - bitposition

    def initialize_offsets(self, bitposition: int = 0) -> int:
        end_bitposition = bitposition
        end_bitposition += 32
        end_bitposition += zserio.bitsizeof.bitsizeof_string(self._text_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bitbuffer(self._extern_data_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bytes(self._bytes_data_)
        end_bitposition = self._creator_enum_.initialize_offsets(end_bitposition)
        end_bitposition = self._creator_bitmask_.initialize_offsets(end_bitposition)

        return end_bitposition

    def initialize_offsets_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                                  bitposition: int) -> int:
        end_bitposition = bitposition
        end_bitposition += zserio_context_node.children[0].context.bitsizeof(zserio.array.BitFieldArrayTraits(32), self._value_)
        end_bitposition += zserio.bitsizeof.bitsizeof_string(self._text_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bitbuffer(self._extern_data_)
        end_bitposition += zserio.bitsizeof.bitsizeof_bytes(self._bytes_data_)
        end_bitposition = self._creator_enum_.initialize_offsets_packed(zserio_context_node.children[4], end_bitposition)
        end_bitposition = self._creator_bitmask_.initialize_offsets_packed(zserio_context_node.children[5], end_bitposition)

        return end_bitposition

    def read(self, zserio_reader: zserio.BitStreamReader) -> None:
        self._value_ = zserio_reader.read_bits(32)
        self._text_ = zserio_reader.read_string()
        self._extern_data_ = zserio_reader.read_bitbuffer()
        self._bytes_data_ = zserio_reader.read_bytes()
        self._creator_enum_ = test_object.creator_enum.CreatorEnum.from_reader(zserio_reader)
        self._creator_bitmask_ = test_object.creator_bitmask.CreatorBitmask.from_reader(zserio_reader)

    def read_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                    zserio_reader: zserio.BitStreamReader) -> None:
        self._value_ = zserio_context_node.children[0].context.read(zserio.array.BitFieldArrayTraits(32), zserio_reader)

        self._text_ = zserio_reader.read_string()

        self._extern_data_ = zserio_reader.read_bitbuffer()

        self._bytes_data_ = zserio_reader.read_bytes()

        self._creator_enum_ = test_object.creator_enum.CreatorEnum.from_reader_packed(zserio_context_node.children[4], zserio_reader)

        self._creator_bitmask_ = test_object.creator_bitmask.CreatorBitmask.from_reader_packed(zserio_context_node.children[5], zserio_reader)

    def write(self, zserio_writer: zserio.BitStreamWriter) -> None:
        zserio_writer.write_bits(self._value_, 32)
        zserio_writer.write_string(self._text_)
        zserio_writer.write_bitbuffer(self._extern_data_)
        zserio_writer.write_bytes(self._bytes_data_)
        self._creator_enum_.write(zserio_writer)
        self._creator_bitmask_.write(zserio_writer)

    def write_packed(self, zserio_context_node: zserio.array.PackingContextNode,
                     zserio_writer: zserio.BitStreamWriter) -> None:
        zserio_context_node.children[0].context.write(zserio.array.BitFieldArrayTraits(32), zserio_writer, self._value_)

        zserio_writer.write_string(self._text_)

        zserio_writer.write_bitbuffer(self._extern_data_)

        zserio_writer.write_bytes(self._bytes_data_)

        self._creator_enum_.write_packed(zserio_context_node.children[4], zserio_writer)

        self._creator_bitmask_.write_packed(zserio_context_node.children[5], zserio_writer)