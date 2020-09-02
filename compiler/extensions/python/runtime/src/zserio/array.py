"""
The module implements abstraction for arrays used by Zserio python extension.
"""

import typing

from zserio.bitposition import alignTo
from zserio.bitsizeof import (getBitSizeOfVarUInt16, getBitSizeOfVarUInt32,
                              getBitSizeOfVarUInt64, getBitSizeOfVarUInt,
                              getBitSizeOfVarSize, getBitSizeOfVarInt16,
                              getBitSizeOfVarInt32, getBitSizeOfVarInt64,
                              getBitSizeOfVarInt, getBitSizeOfString,
                              getBitSizeOfBitBuffer)
from zserio.bitreader import BitStreamReader
from zserio.bitwriter import BitStreamWriter
from zserio.bitbuffer import BitBuffer
from zserio.hashcode import calcHashCode, HASH_SEED
from zserio.exception import PythonRuntimeException

class Array:
    """
    Abstraction for arrays to which Zserio arrays are mapped in python.
    """

    def __init__(self,
                 arrayTraits: typing.Any,
                 rawArray: typing.Optional[typing.List] = None,
                 *,
                 isAuto: bool = False,
                 isImplicit: bool = False,
                 setOffsetMethod: typing.Optional[typing.Callable[[int, int], None]] = None,
                 checkOffsetMethod: typing.Optional[typing.Callable[[int, int], None]] = None) -> None:
        """
        Constructor.

        :param arrayTraits: Array traits which specify the array type.
        :param rawArray: Native python list which will be hold by this abstraction.
        :param isAuto: True if mapped Zserio array is auto array.
        :param isImplicit: True if mapped Zserio array is implicit array.
        :param setOffsetMethod:  Set offset method if mapped Zserio array is indexed offset array.
        :param checkOffsetMethod: Check offset method if mapped Zserio array is indexed offset array.
        """

        self._rawArray = [] if rawArray is None else rawArray # type: typing.List
        self._arrayTraits = arrayTraits # type: typing.Any
        self._isAuto = isAuto # type: bool
        self._isImplicit = isImplicit # type: bool
        self._setOffsetMethod = setOffsetMethod # typing.Optional[typing.Callable[[int, int], None]]
        self._checkOffsetMethod = checkOffsetMethod # typing.Optional[typing.Callable[[int, int], None]]

    @classmethod
    def fromReader(cls: typing.Type['Array'],
                   arrayTraits: typing.Any,
                   reader: BitStreamReader,
                   size: int = 0,
                   *,
                   isAuto: bool = False,
                   isImplicit: bool = False,
                   setOffsetMethod: typing.Optional[typing.Callable[[int, int], None]] = None,
                   checkOffsetMethod: typing.Optional[typing.Callable[[int, int], None]] = None) -> 'Array':
        """
        Constructs array and reads elements from the given bit stream reader.

        :param arrayTraits: Array traits which specify the array type.
        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.
        :param rawArray: Native python list which will be hold by this abstraction.
        :param isAuto: True if mapped Zserio array is auto array.
        :param isImplicit: True if mapped Zserio array is implicit array.
        :param setOffsetMethod:  Set offset method if mapped Zserio array is indexed offset array.
        :param checkOffsetMethod: Check offset method if mapped Zserio array is indexed offset array.
        :returns: Array instance filled using given bit stream reader.
        """

        instance = cls(arrayTraits, isAuto=isAuto, isImplicit=isImplicit, setOffsetMethod=setOffsetMethod,
                       checkOffsetMethod=checkOffsetMethod)
        instance.read(reader, size)

        return instance

    def __eq__(self, other: object) -> bool:
        # it's enough to check only rawArray because compound types which call this are always the same type
        if isinstance(other, Array):
            return self._rawArray == other._rawArray

        return False

    def __hash__(self) -> int:
        hashCode = HASH_SEED
        for element in self._rawArray:
            hashCode = calcHashCode(hashCode, hash(element))

        return hashCode

    def __len__(self) -> int:
        return len(self._rawArray)

    def __getitem__(self, key: int) -> typing.Any:
        return self._rawArray[key]

    def __setitem__(self, key: int, value: typing.Any) -> None:
        self._rawArray[key] = value

    def getRawArray(self) -> typing.List:
        """
        Gets raw array.

        :returns: Native python list which is hold by the array.
        """

        return self._rawArray


    def bitSizeOf(self, bitPosition: int) -> int:
        """
        Returns length of array stored in the bit stream in bits.

        :param bitPosition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarSize(size)

        if self._arrayTraits.HAS_BITSIZEOF_CONSTANT and size > 0:
            elementSize = self._arrayTraits.bitSizeOf()
            if self._setOffsetMethod is None:
                endBitPosition += size * elementSize
            else:
                endBitPosition += elementSize + (size - 1) * alignTo(8, elementSize)
        else:
            for element in self._rawArray:
                if self._setOffsetMethod is not None:
                    endBitPosition = alignTo(8, endBitPosition)
                endBitPosition += self._arrayTraits.bitSizeOf(endBitPosition, element)

        return endBitPosition - bitPosition

    def initializeOffsets(self, bitPosition: int) -> int:
        """
        Initializes indexed offsets for the array.

        :param bitPosition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarSize(size)

        for index in range(size):
            if self._setOffsetMethod is not None:
                endBitPosition = alignTo(8, endBitPosition)
                self._setOffsetMethod(index, endBitPosition)
            endBitPosition = self._arrayTraits.initializeOffsets(endBitPosition, self._rawArray[index])

        return endBitPosition

    def read(self, reader: BitStreamReader, size: int = 0) -> None:
        """
        Reads array from the bit stream.

        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.

        :raises PythonRuntimeException: If the array does not have elements with constant bit size.
        """

        self._rawArray.clear()

        if self._isImplicit:
            if not self._arrayTraits.HAS_BITSIZEOF_CONSTANT:
                raise PythonRuntimeException("Array: Implicit array elements must have constant bit size!")

            elementSize = self._arrayTraits.bitSizeOf()
            remainingBits = reader.getBufferBitSize() - reader.getBitPosition()
            readSize = remainingBits // elementSize
            for index in range(readSize):
                self._rawArray.append(self._arrayTraits.read(reader, index))
        else:
            if self._isAuto:
                readSize = reader.readVarSize()
            else:
                readSize = size

            for index in range(readSize):
                if self._checkOffsetMethod is not None:
                    reader.alignTo(8)
                    self._checkOffsetMethod(index, reader.getBitPosition())
                self._rawArray.append(self._arrayTraits.read(reader, index))

    def write(self, writer: BitStreamWriter) -> None:
        """
        Writes array to the bit stream.

        :param writer: Bit stream where to write.
        """

        size = len(self._rawArray)
        if self._isAuto:
            writer.writeVarSize(size)

        for index in range(size):
            if self._checkOffsetMethod is not None:
                writer.alignTo(8)
                self._checkOffsetMethod(index, writer.getBitPosition())
            self._arrayTraits.write(writer, self._rawArray[index])

class BitFieldArrayTraits:
    """
    Array traits for unsigned fixed integer Zserio types (uint16, uint32, uint64, bit:5, etc...).
    """

    HAS_BITSIZEOF_CONSTANT = True

    def __init__(self, numBits: int) -> None:
        """
        Constructor.

        :param numBits: Number of bits for unsigned fixed integer Zserio type.
        """

        self._numBits = numBits # type: int

    def bitSizeOf(self) -> int:
        """
        Returns length of unsigned fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of unsigned fixed integer Zserio type in bits.
        """

        return self._numBits

    def initializeOffsets(self, bitPosition: int, _value: int) -> int:
        """
        Initializes indexed offsets for unsigned fixed integer Zserio type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after unsigned fixed integer type.
        """

        return bitPosition + self.bitSizeOf()

    def read(self, reader: BitStreamReader, _index: int) -> int:
        """
        Reads unsigned fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readBits(self._numBits)

    def write(self, writer: BitStreamWriter, value: int) -> None:
        """
        Writes unsigned fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Unsigned fixed integer Zserio type to write.
        """

        writer.writeBits(value, self._numBits)

class SignedBitFieldArrayTraits:
    """
    Array traits for signed fixed integer Zserio types (int16, int32, int64, int:5, etc...).
    """

    HAS_BITSIZEOF_CONSTANT = True

    def __init__(self, numBits: int) -> None:
        """
        Constructor.

        :param numBits: Number of bits for signed fixed integer Zserio type.
        """

        self._numBits = numBits

    def bitSizeOf(self) -> int:
        """
        Returns length of signed fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of signed fixed integer Zserio type in bits.
        """

        return self._numBits

    def initializeOffsets(self, bitPosition: int, _value: int) -> int:
        """
        Initializes indexed offsets for signed fixed integer Zserio type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after signed fixed integer type.
        """

        return bitPosition + self.bitSizeOf()

    def read(self, reader: BitStreamReader, _index: int) -> int:
        """
        Reads signed fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readSignedBits(self._numBits)

    def write(self, writer: BitStreamWriter, value: int) -> None:
        """
        Writes signed fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Signed fixed integer Zserio type to write.
        """

        writer.writeSignedBits(value, self._numBits)

class VarUInt16ArrayTraits:
    """
    Array traits for Zserio varuint16 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varuint16 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint16 type value.
        :returns: Length of given Zserio varuint16 type in bits.
        """

        return getBitSizeOfVarUInt16(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint16 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varuint16 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint16 type.
        """

        return bitPosition + VarUInt16ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varuint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt16()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint16 type to write.
        """

        writer.writeVarUInt16(value)

class VarUInt32ArrayTraits:
    """
    Array traits for Zserio varuint32 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varuint32 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint32 type value.
        :returns: Length of given Zserio varuint32 type in bits.
        """

        return getBitSizeOfVarUInt32(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint32 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varuint32 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint32 type.
        """

        return bitPosition + VarUInt32ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varuint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt32()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint32 type to write.
        """

        writer.writeVarUInt32(value)

class VarUInt64ArrayTraits:
    """
    Array traits for Zserio varuint64 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varuint64 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint64 type value.
        :returns: Length of given Zserio varuint64 type in bits.
        """

        return getBitSizeOfVarUInt64(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint64 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varuint64 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint64 type.
        """

        return bitPosition + VarUInt64ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varuint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt64()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint64 type to write.
        """

        writer.writeVarUInt64(value)

class VarUIntArrayTraits:
    """
    Array traits for Zserio varuint type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varuint type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint type value.
        :returns: Length of given Zserio varuint type in bits.
        """

        return getBitSizeOfVarUInt(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varuint type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varuint type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varuint type.
        """

        return bitPosition + VarUIntArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varuint type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varuint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint type to write.
        """

        writer.writeVarUInt(value)

class VarSizeArrayTraits:
    """
    Array traits for Zserio varsize type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varsize type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varsize type value.
        :returns: Length of given Zserio varsize type in bits.
        """

        return getBitSizeOfVarSize(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varsize type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varsize type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varsize type.
        """

        return bitPosition + VarSizeArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varsize type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarSize()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varsize type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varsize type to write.
        """

        writer.writeVarSize(value)

class VarInt16ArrayTraits:
    """
    Array traits for Zserio varint16 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varint16 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint16 type value.
        :returns: Length of given Zserio varint16 type in bits.
        """

        return getBitSizeOfVarInt16(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint16 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varint16 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint16 type.
        """

        return bitPosition + VarInt16ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt16()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint16 type to write.
        """

        writer.writeVarInt16(value)

class VarInt32ArrayTraits:
    """
    Array traits for Zserio varint32 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varint32 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint32 type value.
        :returns: Length of given Zserio varint32 type in bits.
        """

        return getBitSizeOfVarInt32(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint32 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varint32 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint32 type.
        """

        return bitPosition + VarInt32ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt32()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint32 type to write.
        """

        writer.writeVarInt32(value)

class VarInt64ArrayTraits:
    """
    Array traits for Zserio varint64 type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varint64 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint64 type value.
        :returns: Length of given Zserio varint64 type in bits.
        """

        return getBitSizeOfVarInt64(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint64 type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varint64 type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint64 type.
        """

        return bitPosition + VarInt64ArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt64()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint64 type to write.
        """

        writer.writeVarInt64(value)

class VarIntArrayTraits:
    """
    Array traits for Zserio varint type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: int) -> int:
        """
        Returns length of Zserio varint type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint type value.
        :returns: Length of given Zserio varint type in bits.
        """

        return getBitSizeOfVarInt(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: int) -> int:
        """
        Initializes indexed offsets for Zserio varint type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio varint type value.
        :returns: Updated bit stream position which points to the first bit after Zserio varint type.
        """

        return bitPosition + VarIntArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> int:
        """
        Reads Zserio varint type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt()

    @staticmethod
    def write(writer: BitStreamWriter, value: int) -> None:
        """
        Writes Zserio varint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint type to write.
        """

        writer.writeVarInt(value)

class Float16ArrayTraits:
    """
    Array traits for Zserio float16 type.
    """

    HAS_BITSIZEOF_CONSTANT = True

    @staticmethod
    def bitSizeOf() -> int:
        """
        Returns length of Zserio float16 type stored in the bit stream in bits.

        :returns: Length of Zserio float16 type in bits.
        """

        return 16

    @staticmethod
    def initializeOffsets(bitPosition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float16 type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float16 type.
        """

        return bitPosition + Float16ArrayTraits.bitSizeOf()

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> float:
        """
        Reads Zserio float16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat16()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float16 type to write.
        """

        writer.writeFloat16(value)

class Float32ArrayTraits:
    """
    Array traits for Zserio float32 type.
    """

    HAS_BITSIZEOF_CONSTANT = True

    @staticmethod
    def bitSizeOf() -> int:
        """
        Returns length of Zserio float32 type stored in the bit stream in bits.

        :returns: Length of Zserio float32 type in bits.
        """

        return 32

    @staticmethod
    def initializeOffsets(bitPosition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float32 type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float32 type.
        """

        return bitPosition + Float32ArrayTraits.bitSizeOf()

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> float:
        """
        Reads Zserio float32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat32()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float32 type to write.
        """

        writer.writeFloat32(value)

class Float64ArrayTraits:
    """
    Array traits for Zserio float64 type.
    """

    HAS_BITSIZEOF_CONSTANT = True

    @staticmethod
    def bitSizeOf() -> int:
        """
        Returns length of Zserio float64 type stored in the bit stream in bits.

        :returns: Length of Zserio float64 type in bits.
        """

        return 64

    @staticmethod
    def initializeOffsets(bitPosition: int, _value: float) -> int:
        """
        Initializes indexed offsets for Zserio float64 type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio float64 type.
        """

        return bitPosition + Float64ArrayTraits.bitSizeOf()

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> float:
        """
        Reads Zserio float64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat64()

    @staticmethod
    def write(writer: BitStreamWriter, value: float) -> None:
        """
        Writes Zserio float64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float64 type to write.
        """

        writer.writeFloat64(value)

class StringArrayTraits:
    """
    Array traits for Zserio string type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition, value: str) -> int:
        """
        Returns length of Zserio string type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio string type value.
        :returns: Length of given Zserio string type in bits.
        """

        return getBitSizeOfString(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: str) -> int:
        """
        Initializes indexed offsets for Zserio string type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio string type value.
        :returns: Updated bit stream position which points to the first bit after Zserio string type.
        """

        return bitPosition + StringArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> str:
        """
        Reads Zserio string type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readString()

    @staticmethod
    def write(writer: BitStreamWriter, value: str) -> None:
        """
        Writes Zserio string type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio string type to write.
        """

        writer.writeString(value)

class BoolArrayTraits:
    """
    Array traits for Zserio bool type.
    """

    HAS_BITSIZEOF_CONSTANT = True

    @staticmethod
    def bitSizeOf() -> int:
        """
        Returns length of Zserio bool type stored in the bit stream in bits.

        :returns: Length of Zserio bool type in bits.
        """

        return 1

    @staticmethod
    def initializeOffsets(bitPosition: int, _value: bool) -> int:
        """
        Initializes indexed offsets for Zserio bool type.

        :param bitPosition: Current bit stream position.
        :param _value: Not used.
        :returns: Updated bit stream position which points to the first bit after Zserio bool type.
        """

        return bitPosition + BoolArrayTraits.bitSizeOf()

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> bool:
        """
        Reads Zserio bool type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readBool()

    @staticmethod
    def write(writer: BitStreamWriter, value: bool) -> None:
        """
        Writes Zserio bool type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio bool type to write.
        """

        writer.writeBool(value)

class BitBufferArrayTraits:
    """
    Array traits for Zserio extern bit buffer type.
    """

    HAS_BITSIZEOF_CONSTANT = False

    @staticmethod
    def bitSizeOf(_bitPosition: int, value: BitBuffer) -> int:
        """
        Returns length of Zserio extern bit buffer type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio extern bit buffer type value.
        :returns: Length of given Zserio string type in bits.
        """

        return getBitSizeOfBitBuffer(value)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: BitBuffer) -> int:
        """
        Initializes indexed offsets for Zserio extern bit buffer type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio extern bit buffer type value.
        :returns: Updated bit stream position which points to the first bit after Zserio extern bit buffer type.
        """

        return bitPosition + BitBufferArrayTraits.bitSizeOf(bitPosition, value)

    @staticmethod
    def read(reader: BitStreamReader, _index: int) -> BitBuffer:
        """
        Reads Zserio extern bit buffer type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readBitBuffer()

    @staticmethod
    def write(writer: BitStreamWriter, value: BitBuffer) -> None:
        """
        Writes Zserio extern bit buffer type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio extern bit buffer type to write.
        """

        writer.writeBitBuffer(value)

class ObjectArrayTraits:
    """
    Array traits for Zserio structure, choice, union and enum types.
    """

    HAS_BITSIZEOF_CONSTANT = False

    def __init__(self, objectCreator: typing.Callable[[BitStreamReader, int], typing.Any]) -> None:
        """
        Constructor.

        :param objectCreator: Creator which creates object from the element index.
        """

        self._objectCreator = objectCreator # type: typing.Callable[[BitStreamReader, int], typing.Any]

    @staticmethod
    def bitSizeOf(bitPosition: int, value: typing.Any) -> int:
        """
        Returns length of Zserio object type stored in the bit stream in bits.

        :param bitPosition: Current bit position in bit stream.
        :param value: Zserio object type value.
        :returns: Length of given Zserio object type in bits.
        """

        return value.bitSizeOf(bitPosition)

    @staticmethod
    def initializeOffsets(bitPosition: int, value: typing.Any) -> int:
        """
        Initializes indexed offsets for the Zserio object type.

        :param bitPosition: Current bit stream position.
        :param value: Zserio object type value.
        :returns: Updated bit stream position which points to the first bit after the Zserio object type.
        """

        return value.initializeOffsets(bitPosition)

    def read(self, reader: BitStreamReader, index: int) -> typing.Any:
        """
        Reads Zserio object type from the bit stream.

        :param reader: Bit stream from which to read.
        :param index: Element index in the array.
        """

        return self._objectCreator(reader, index)

    @staticmethod
    def write(writer: BitStreamWriter, value: typing.Any) -> None:
        """
        Writes Zserio object type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio object type to write.
        """

        value.write(writer)
