from zserio.bitsizeof import (getBitSizeOfVarUInt16, getBitSizeOfVarUInt32,
                              getBitSizeOfVarUInt64, getBitSizeOfVarUInt,
                              getBitSizeOfVarInt16, getBitSizeOfVarInt32,
                              getBitSizeOfVarInt64, getBitSizeOfVarInt,
                              getBitSizeOfString, alignTo)
from zserio.hashcode import calcHashCode, HASH_SEED
from zserio.exception import PythonRuntimeException

class Array():
    """
    Abstraction for arrays to which Zserio arrays are mapped in python.
    """

    def __init__(self, arrayTraits, rawArray=[], *, isAuto=None, isImplicit=None, setOffsetMethod=None,
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

        self._rawArray = rawArray
        self._arrayTraits = arrayTraits
        self._isAuto = isAuto
        self._isImplicit = isImplicit
        self._setOffsetMethod = setOffsetMethod
        self._checkOffsetMethod = checkOffsetMethod

    def __eq__(self, other):
        """
        Checks if the given array is different.

        :param other: Array to check.
        :returns: True if the given array is the same.
        """
        return (self._rawArray == other._rawArray and
                self._arrayTraits == other._arrayTraits and
                self._isAuto == other._isAuto and
                self._isImplicit == other._isImplicit and
                self._setOffsetMethod == other._setOffsetMethod and
                self._checkOffsetMethod == other._checkOffsetMethod)

    def __hash__(self):
        """
        Calculates hash code of the array.
        """

        hashCode = HASH_SEED
        for element in self._rawArray:
            hashCode = calcHashCode(hashCode, hash(element))

        return hashCode

    def getRawArray(self):
        """
        Gets raw array.

        :returns: Native python list which is hold by the array.
        """

        return self._rawArray

    def sum(self):
        """
        Calculates sum of all elements stored in the array.

        :returns: Sum of all array elements.
        """
        return sum(self._rawArray)

    def bitSizeOf(self, bitPosition):
        """
        Returns length of the array stored in the bit stream in bits.

        :param bitPosition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarUInt64(size)

        if self._arrayTraits.isBitSizeOfConstant == True and size > 0:
            elementSize = self._arrayTraits.bitSizeOf(endBitPosition, self._rawArray[0])
            if self._setOffsetMethod == None:
                endBitPosition += size * elementSize
            else:
                endBitPosition += elementSize + (size - 1) * alignTo(8, elementSize)
        else:
            for element in self._rawArray:
                if self._setOffsetMethod != None:
                    endBitPosition = alignTo(8, endBitPosition)
                endBitPosition += self._arrayTraits.bitSizeOf(endBitPosition, element)

        return endBitPosition - bitPosition

    def initializeOffsets(self, bitPosition):
        """
        Initializes indexed offsets for the array.

        :param bitPosition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        endBitPosition = bitPosition
        size = len(self._rawArray)
        if self._isAuto:
            endBitPosition += getBitSizeOfVarUInt64(size)

        if self._arrayTraits.isBitSizeOfConstant == True and self._setOffsetMethod == None and size > 0:
            endBitPosition += size * self._arrayTraits.bitSizeOf(endBitPosition, self._rawArray[0])
        else:
            for index in range(size):
                if self._setOffsetMethod != None:
                    endBitPosition = self._setOffsetMethod(index, endBitPosition)
                endBitPosition = self._arrayTraits.initializeOffsets(endBitPosition, self._rawArray[index])

        return endBitPosition

    def read(self, reader, size=None):
        """
        Reads a given number of elements from bit stream applying offset checking.

        :param reader: Bit stream reader to construct from.
        :param size: Number of element to read or None in case of implicit or auto arrays.
        """

        self._rawArray.clear();

        if self._isImplicit:
            index = 0
            while (True):
                try:
                    bitPosition = reader.getBitPosition()
                    self._rawArray.append(self._arrayTraits.read(reader, index))
                except PythonRuntimeException:
                    # set exact end bit position in the stream avoiding padding at the end
                    reader.setBitPosition(bitPosition);
                    break
                index += 1
        else:
            if self._isAuto:
                size = reader.readVarUInt64()

            for index in range(size):
                if self._checkOffsetMethod != None:
                    reader.alignTo(8)
                    self._checkOffsetMethod(index)
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
            if self._checkOffsetMethod != None:
                writer.alignTo(8)
                self._checkOffsetMethod(index)
            self._arrayTraits.write(writer, self._rawArray[index])

class BitFieldArrayTraits():
    """
    Array traits for unsigned fixed integer Zserio types (uint16, uint32, uint64, bit:5, etc...). 
    """

    def __init__(self, numBits):
        self._numBits = numBits

    def __eq__(self, other):
        return self._numBits == other._numBits

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return self._numBits

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readBits(self._numBits)

    def write(self, writer, value):
        writer.writeBits(value, self._numBits)

class SignedBitFieldArrayTraits():
    """
    Array traits for signed fixed integer Zserio types (int16, int32, int64, int:5, etc...). 
    """

    def __init__(self, numBits):
        self._numBits = numBits

    def __eq__(self, other):
        return self._numBits == other._numBits

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return self._numBits

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readSignedBits(self._numBits)

    def write(self, writer, value):
        writer.writeSignedBits(value, self._numBits)

class VarUInt16ArrayTraits():
    """
    Array traits for Zserio varuint16 type. 
    """
    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarUInt16(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarUInt16()

    def write(self, writer, value):
        writer.writeVarUInt16(value)

class VarUInt32ArrayTraits():
    """
    Array traits for Zserio varuint32 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarUInt32(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarUInt32()

    def write(self, writer, value):
        writer.writeVarUInt32(value)

class VarUInt64ArrayTraits():
    """
    Array traits for Zserio varuint64 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarUInt64(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarUInt64()

    def write(self, writer, value):
        writer.writeVarUInt64(value)

class VarUIntArrayTraits():
    """
    Array traits for Zserio varuint type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarUInt(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarUInt()

    def write(self, writer, value):
        writer.writeVarUInt(value)

class VarInt16ArrayTraits():
    """
    Array traits for Zserio varint16 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarInt16(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarInt16()

    def write(self, writer, value):
        writer.writeVarInt16(value)

class VarInt32ArrayTraits():
    """
    Array traits for Zserio varint32 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarInt32(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarInt32()

    def write(self, writer, value):
        writer.writeVarInt32(value)

class VarInt64ArrayTraits():
    """
    Array traits for Zserio varint64 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarInt64(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarInt64()

    def write(self, writer, value):
        writer.writeVarInt64(value)

class VarIntArrayTraits():
    """
    Array traits for Zserio varint type. 
    """
    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfVarInt(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readVarInt()

    def write(self, writer, value):
        writer.writeVarInt(value)

class Float16ArrayTraits():
    """
    Array traits for Zserio float16 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return 16

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readFloat16()

    def write(self, writer, value):
        writer.writeFloat16(value)

class Float32ArrayTraits():
    """
    Array traits for Zserio float32 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return 32

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readFloat32()

    def write(self, writer, value):
        writer.writeFloat32(value)

class Float64ArrayTraits():
    """
    Array traits for Zserio float64 type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return 64

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readFloat64()

    def write(self, writer, value):
        writer.writeFloat64(value)

class StringArrayTraits():
    """
    Array traits for Zserio string type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return getBitSizeOfString(value)

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readString()

    def write(self, writer, value):
        writer.writeString(value)

class BoolArrayTraits():
    """
    Array traits for Zserio bool type. 
    """

    def __eq__(self, other):
        return True

    def isBitSizeOfConstant(self):
        return True

    def bitSizeOf(self, bitPosition, value):
        return 1

    def initializeOffsets(self, bitPosition, value):
        return bitPosition + self.bitSizeOf(bitPosition, value)

    def read(self, reader, index):
        return reader.readBool()

    def write(self, writer, value):
        writer.writeBool(value)

class ObjectArrayTraits():
    """
    Array traits for Zserio structure, choice, union and enum types. 
    """

    def __init__(self, objectCreator):
        self._objectCreator = objectCreator

    def __eq__(self, other):
        return self._objectCreator == other._objectCreator

    def isBitSizeOfConstant(self):
        return False

    def bitSizeOf(self, bitPosition, value):
        return value.bitSizeOf(bitPosition)

    def initializeOffsets(self, bitPosition, value):
        return value.initializeOffsets(bitPosition)

    def read(self, reader, index):
        createdObject = self._objectCreator(index)
        createdObject.read(reader)

        return createdObject

    def write(self, writer, value):
        value.write(writer)
