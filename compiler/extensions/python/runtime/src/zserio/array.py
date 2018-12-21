"""
The module implements abstraction for arrays used by Zserio python extension.
"""

from zserio.bitposition import alignTo
from zserio.bitsizeof import (getBitSizeOfVarUInt16, getBitSizeOfVarUInt32,
                              getBitSizeOfVarUInt64, getBitSizeOfVarUInt,
                              getBitSizeOfVarInt16, getBitSizeOfVarInt32,
                              getBitSizeOfVarInt64, getBitSizeOfVarInt,
                              getBitSizeOfString)
from zserio.hashcode import calcHashCode, HASH_SEED
from zserio.exception import PythonRuntimeException

class Array():
    """
    Abstraction for arrays to which Zserio arrays are mapped in python.
    """

    def __init__(self, arrayTraits, rawArray=None, *, isAuto=None, isImplicit=None, setOffsetMethod=None,
                 checkOffsetMethod=None):
        """
        Constructor.

        :param arrayTraits: Array traits which specify the array type.
        :param rawArray: Native python list which will be hold by this abstraction.
        :param isAuto: True if mapped Zserio array is auto array.
        :param isImplicit: True if mapped Zserio array is implicit array.
        :param setOffsetMethod:  Set offset method if mapped Zserio array is indexed offset array.
        :param checkOffsetMethod: Check offset method if mapped Zserio array is indexed offset array.
        """

        if rawArray is None:
            self._rawArray = []
        else:
            self._rawArray = rawArray
        self._arrayTraits = arrayTraits
        self._isAuto = isAuto
        self._isImplicit = isImplicit
        self._setOffsetMethod = setOffsetMethod
        self._checkOffsetMethod = checkOffsetMethod

    @classmethod
    def fromReader(cls, arrayTraits, reader, size=None, *, isAuto=None, isImplicit=None, setOffsetMethod=None,
                   checkOffsetMethod=None):
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

    def __eq__(self, other):
        if isinstance(other, Array):
            return (self._rawArray == other._rawArray and
                    self._arrayTraits == other._arrayTraits and
                    self._isAuto == other._isAuto and
                    self._isImplicit == other._isImplicit and
                    self._setOffsetMethod == other._setOffsetMethod and
                    self._checkOffsetMethod == other._checkOffsetMethod)

        return False

    def __hash__(self):
        hashCode = HASH_SEED
        for element in self._rawArray:
            hashCode = calcHashCode(hashCode, hash(element))

        return hashCode

    def __len__(self):
        return len(self._rawArray)

    def __getitem__(self, key):
        return self._rawArray[key]

    def __setitem__(self, key, value):
        self._rawArray[key] = value

    def getRawArray(self):
        """
        Gets raw array.

        :returns: Native python list which is hold by the array.
        """

        return self._rawArray


    def bitSizeOf(self, bitPosition):
        """
        Returns length of array stored in the bit stream in bits.

        :param bitPosition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarUInt64(size)

        if self._arrayTraits.IS_BITSIZEOF_CONSTANT and size > 0:
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

    def initializeOffsets(self, bitPosition):
        """
        Initializes indexed offsets for the array.

        :param bitPosition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        if not self._arrayTraits.NEEDS_INITIALIZE_OFFSETS:
            return bitPosition + self.bitSizeOf(bitPosition)

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarUInt64(size)

        for index in range(size):
            if self._setOffsetMethod is not None:
                endBitPosition = alignTo(8, endBitPosition)
                self._setOffsetMethod(index, endBitPosition)
            endBitPosition = self._arrayTraits.initializeOffsets(endBitPosition, self._rawArray[index])

        return endBitPosition

    def read(self, reader, size=None):
        """
        Reads array from the bit stream.

        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.
        """

        self._rawArray.clear()

        if self._isImplicit:
            index = 0
            while True:
                try:
                    bitPosition = reader.getBitPosition()
                    self._rawArray.append(self._arrayTraits.read(reader, index))
                except PythonRuntimeException:
                    # set exact end bit position in the stream avoiding padding at the end
                    reader.setBitPosition(bitPosition)
                    break
                index += 1
        else:
            if self._isAuto:
                size = reader.readVarUInt64()

            for index in range(size):
                if self._checkOffsetMethod is not None:
                    reader.alignTo(8)
                    self._checkOffsetMethod(index, reader.getBitPosition())
                self._rawArray.append(self._arrayTraits.read(reader, index))

    def write(self, writer):
        """
        Writes array to the bit stream.

        :param writer: Bit stream where to write.
        """

        size = len(self._rawArray)
        if self._isAuto:
            writer.writeVarUInt64(size)

        for index in range(size):
            if self._checkOffsetMethod is not None:
                writer.alignTo(8)
                self._checkOffsetMethod(index, writer.getBitPosition())
            self._arrayTraits.write(writer, self._rawArray[index])

class BitFieldArrayTraits():
    """
    Array traits for unsigned fixed integer Zserio types (uint16, uint32, uint64, bit:5, etc...).
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __init__(self, numBits):
        """
        Constructor.

        :param numBits: Number of bits for unsigned fixed integer Zserio type.
        """

        self._numBits = numBits

    def __eq__(self, other):
        if isinstance(other, BitFieldArrayTraits):
            return self._numBits == other._numBits

        return False

    def bitSizeOf(self):
        """
        Returns length of unsigned fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of unsigned fixed integer Zserio type in bits.
        """

        return self._numBits

    def read(self, reader, _index):
        """
        Reads unsigned fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readBits(self._numBits)

    def write(self, writer, value):
        """
        Writes unsigned fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Unsigned fixed integer Zserio type to write.
        """

        writer.writeBits(value, self._numBits)

class SignedBitFieldArrayTraits():
    """
    Array traits for signed fixed integer Zserio types (int16, int32, int64, int:5, etc...).
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __init__(self, numBits):
        """
        Constructor.

        :param numBits: Number of bits for signed fixed integer Zserio type.
        """

        self._numBits = numBits

    def __eq__(self, other):
        if isinstance(other, SignedBitFieldArrayTraits):
            return self._numBits == other._numBits

        return False

    def bitSizeOf(self):
        """
        Returns length of signed fixed integer Zserio type stored in the bit stream in bits.

        :returns: Length of signed fixed integer Zserio type in bits.
        """

        return self._numBits

    def read(self, reader, _index):
        """
        Reads signed fixed integer Zserio type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readSignedBits(self._numBits)

    def write(self, writer, value):
        """
        Writes signed fixed integer Zserio type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Signed fixed integer Zserio type to write.
        """

        writer.writeSignedBits(value, self._numBits)

class VarUInt16ArrayTraits():
    """
    Array traits for Zserio varuint16 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarUInt16ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varuint16 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint16 type value.
        :returns: Length of given Zserio varuint16 type in bits.
        """

        return getBitSizeOfVarUInt16(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varuint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt16()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varuint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint16 type to write.
        """

        writer.writeVarUInt16(value)

class VarUInt32ArrayTraits():
    """
    Array traits for Zserio varuint32 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarUInt32ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varuint32 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint32 type value.
        :returns: Length of given Zserio varuint32 type in bits.
        """

        return getBitSizeOfVarUInt32(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varuint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt32()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varuint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint32 type to write.
        """

        writer.writeVarUInt32(value)

class VarUInt64ArrayTraits():
    """
    Array traits for Zserio varuint64 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarUInt64ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varuint64 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint64 type value.
        :returns: Length of given Zserio varuint64 type in bits.
        """

        return getBitSizeOfVarUInt64(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varuint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt64()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varuint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint64 type to write.
        """

        writer.writeVarUInt64(value)

class VarUIntArrayTraits():
    """
    Array traits for Zserio varuint type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarUIntArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varuint type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varuint type value.
        :returns: Length of given Zserio varuint type in bits.
        """

        return getBitSizeOfVarUInt(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varuint type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarUInt()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varuint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varuint type to write.
        """

        writer.writeVarUInt(value)

class VarInt16ArrayTraits():
    """
    Array traits for Zserio varint16 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarInt16ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varint16 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint16 type value.
        :returns: Length of given Zserio varint16 type in bits.
        """

        return getBitSizeOfVarInt16(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varint16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt16()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varint16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint16 type to write.
        """

        writer.writeVarInt16(value)

class VarInt32ArrayTraits():
    """
    Array traits for Zserio varint32 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarInt32ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varint32 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint32 type value.
        :returns: Length of given Zserio varint32 type in bits.
        """

        return getBitSizeOfVarInt32(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varint32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt32()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varint32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint32 type to write.
        """

        writer.writeVarInt32(value)

class VarInt64ArrayTraits():
    """
    Array traits for Zserio varint64 type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarInt64ArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varint64 type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint64 type value.
        :returns: Length of given Zserio varint64 type in bits.
        """

        return getBitSizeOfVarInt64(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varint64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt64()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varint64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint64 type to write.
        """

        writer.writeVarInt64(value)

class VarIntArrayTraits():
    """
    Array traits for Zserio varint type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, VarIntArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio varint type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio varint type value.
        :returns: Length of given Zserio varint type in bits.
        """

        return getBitSizeOfVarInt(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio varint type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readVarInt()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio varint type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio varint type to write.
        """

        writer.writeVarInt(value)

class Float16ArrayTraits():
    """
    Array traits for Zserio float16 type.
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, Float16ArrayTraits)

    @staticmethod
    def bitSizeOf():
        """
        Returns length of Zserio float16 type stored in the bit stream in bits.

        :returns: Length of Zserio float16 type in bits.
        """

        return 16

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio float16 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat16()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio float16 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float16 type to write.
        """

        writer.writeFloat16(value)

class Float32ArrayTraits():
    """
    Array traits for Zserio float32 type.
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, Float32ArrayTraits)

    @staticmethod
    def bitSizeOf():
        """
        Returns length of Zserio float32 type stored in the bit stream in bits.

        :returns: Length of Zserio float32 type in bits.
        """

        return 32

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio float32 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat32()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio float32 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float32 type to write.
        """

        writer.writeFloat32(value)

class Float64ArrayTraits():
    """
    Array traits for Zserio float64 type.
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, Float64ArrayTraits)

    @staticmethod
    def bitSizeOf():
        """
        Returns length of Zserio float64 type stored in the bit stream in bits.

        :returns: Length of Zserio float64 type in bits.
        """

        return 64

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio float64 type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readFloat64()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio float64 type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio float64 type to write.
        """

        writer.writeFloat64(value)

class StringArrayTraits():
    """
    Array traits for Zserio string type.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, StringArrayTraits)

    @staticmethod
    def bitSizeOf(_bitPosition, value):
        """
        Returns length of Zserio string type stored in the bit stream in bits.

        :param _bitPosition: Not used.
        :param value: Zserio string type value.
        :returns: Length of given Zserio string type in bits.
        """

        return getBitSizeOfString(value)

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio string type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readString()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio string type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio string type to write.
        """

        writer.writeString(value)

class BoolArrayTraits():
    """
    Array traits for Zserio bool type.
    """

    IS_BITSIZEOF_CONSTANT = True
    NEEDS_INITIALIZE_OFFSETS = False

    def __eq__(self, other):
        return isinstance(other, BoolArrayTraits)

    @staticmethod
    def bitSizeOf():
        """
        Returns length of Zserio bool type stored in the bit stream in bits.

        :returns: Length of Zserio bool type in bits.
        """

        return 1

    @staticmethod
    def read(reader, _index):
        """
        Reads Zserio bool type from the bit stream.

        :param reader: Bit stream from which to read.
        :param _index: Not used.
        """

        return reader.readBool()

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio bool type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio bool type to write.
        """

        writer.writeBool(value)

class ObjectArrayTraits():
    """
    Array traits for Zserio structure, choice, union and enum types.
    """

    IS_BITSIZEOF_CONSTANT = False
    NEEDS_INITIALIZE_OFFSETS = True

    def __init__(self, objectCreator):
        """
        Constructor.

        :param objectCreator: Creator which creates object from the element index.
        """

        self._objectCreator = objectCreator

    def __eq__(self, other):
        if isinstance(other, ObjectArrayTraits):
            # checks the name of the object creators (= name of the class and bound method)
            return (self._objectCreator.__self__.__class__ == other._objectCreator.__self__.__class__ and
                    self._objectCreator.__name__ == other._objectCreator.__name__)

        return False

    @staticmethod
    def bitSizeOf(bitPosition, value):
        """
        Returns length of Zserio object type stored in the bit stream in bits.

        :param bitPosition: Current bit position in bit stream.
        :param value: Zserio object type value.
        :returns: Length of given Zserio object type in bits.
        """

        return value.bitSizeOf(bitPosition)

    @staticmethod
    def initializeOffsets(bitPosition, value):
        """
        Initializes indexed offsets for the Zserio object type.

        :param bitPosition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the Zserio object type.
        """

        return value.initializeOffsets(bitPosition)

    def read(self, reader, index):
        """
        Reads Zserio object type from the bit stream.

        :param reader: Bit stream from which to read.
        :param index: Element index in the array.
        """

        return self._objectCreator(reader, index)

    @staticmethod
    def write(writer, value):
        """
        Writes Zserio object type to the bit stream.

        :param writer: Bit stream where to write.
        :param value: Zserio object type to write.
        """

        value.write(writer)
