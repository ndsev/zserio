"""
The module implements abstraction for delta compressed arrays used by Zserio python extension.
"""

import typing

from zserio.bitposition import alignto
from zserio.bitsizeof import bitsizeof_varsize
from zserio.bitreader import BitStreamReader
from zserio.bitwriter import BitStreamWriter
from zserio.hashcode import calc_hashcode, HASH_SEED
from zserio.array import (BitFieldArrayTraits, SignedBitFieldArrayTraits, VarUInt16ArrayTraits,
                          VarUInt32ArrayTraits, VarUInt64ArrayTraits, VarInt16ArrayTraits, VarInt32ArrayTraits,
                          VarInt64ArrayTraits, VarUIntArrayTraits, VarSizeArrayTraits, VarIntArrayTraits)

class PackedArray:
    """
    Packed array.

    Compression is determined by the used packed array traits.
    Currently only delta packed arrays are implemented.
    """

    def __init__(self,
                 packed_array_traits: typing.Any,
                 raw_array: typing.Optional[typing.List] = None,
                 *,
                 is_auto: bool = False,
                 set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None,
                 check_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None) -> None:
        """
        Constructor.

        :param packed_array_traits: Packed array traits which specify the array type.
        :param raw_array: Native python list which will be hold by this abstraction.
        :param is_auto: True if mapped Zserio packed array is auto array.
        :param set_offset_method:  Set offset method if mapped Zserio array is indexed offset array.
        :param check_offset_method: Check offset method if mapped Zserio array is indexed offset array.
        """

        self._array_traits = packed_array_traits
        self._raw_array: typing.List = [] if raw_array is None else raw_array
        self._is_auto = is_auto
        self._set_offset_method = set_offset_method
        self._check_offset_method = check_offset_method

    @classmethod
    def from_reader(
            cls: typing.Type['PackedArray'],
            packed_array_traits: typing.Any,
            reader: BitStreamReader,
            size: int = 0,
            *,
            is_auto: bool = False,
            set_offset_method: typing.Optional[typing.Callable[[int, int], None]] = None,
            check_offset_method:
            typing.Optional[typing.Callable[[int, int], None]] = None) -> 'PackedArray':
        """
        Constructs packed array and reads elements from the given bit stream reader.

        :param packed_array_traits: Packed array traits which specify the array type.
        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or None in case of implicit or auto arrays.
        :param raw_array: Native python list which will be hold by this abstraction.
        :param is_auto: True if mapped Zserio array is auto array.
        :param set_offset_method:  Set offset method if mapped Zserio array is indexed offset array.
        :param check_offset_method: Check offset method if mapped Zserio array is indexed offset array.
        :returns: Array instance filled using given bit stream reader.
        """

        instance = cls(packed_array_traits, is_auto=is_auto, set_offset_method=set_offset_method,
                       check_offset_method=check_offset_method)
        instance.read(reader, size)

        return instance

    def __eq__(self, other: object) -> bool:
        # it's enough to check only raw_array because compound types which call this are always the same type
        if isinstance(other, PackedArray):
            return self._raw_array == other._raw_array

        return False

    def __hash__(self) -> int:
        hashcode = HASH_SEED
        for element in self._raw_array:
            hashcode = calc_hashcode(hashcode, hash(element))

        return hashcode

    def __len__(self) -> int:
        return len(self._raw_array)

    def __getitem__(self, key: int) -> typing.Any:
        return self._raw_array[key]

    def __setitem__(self, key: int, value: typing.Any) -> None:
        self._raw_array[key] = value

    @property
    def raw_array(self) -> typing.List:
        """
        Gets the raw array.

        :returns: Native python list which is hold by the array.
        """

        return self._raw_array

    def bitsizeof(self, bitposition: int) -> int:
        """
        Returns length of the packed array stored in the bit stream in bits.

        :param bitposition: Current bit stream position.
        :returns: Length of the array stored in the bit stream in bits.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        if size > 0:
            context_list = self._array_traits.create_context()
            for element in self._raw_array:
                self._array_traits.init_context(iter(context_list), element)
            for context in context_list:
                end_bitposition += context.bitsizeof_descriptor(end_bitposition)

            for element in self._raw_array:
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                end_bitposition += self._array_traits.bitsizeof(iter(context_list), end_bitposition, element)

        return end_bitposition - bitposition

    def initialize_offsets(self, bitposition: int) -> int:
        """
        Initializes indexed offsets for the packed array.

        :param bitposition: Current bit stream position.
        :returns: Updated bit stream position which points to the first bit after the array.
        """

        end_bitposition = bitposition
        size = len(self._raw_array)
        if self._is_auto:
            end_bitposition += bitsizeof_varsize(size)

        if size > 0:
            context_list = self._array_traits.create_context()
            for index in range(size):
                self._array_traits.init_context(iter(context_list), self._raw_array[index])
            for context in context_list:
                end_bitposition += context.bitsizeof_descriptor(end_bitposition)

            for index in range(size):
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                    self._set_offset_method(index, end_bitposition)
                end_bitposition = self._array_traits.initialize_offsets(iter(context_list), end_bitposition,
                                                                        self._raw_array[index])

        return end_bitposition

    def read(self, reader: BitStreamReader, size: int = 0) -> None:
        """
        Reads packed array from the bit stream.

        :param reader: Bit stream from which to read.
        :param size: Number of elements to read or 0 in case of auto arrays.

        :raises PythonRuntimeException: If the array does not have elements with constant bit size.
        """

        self._raw_array.clear()

        if self._is_auto:
            read_size = reader.read_varsize()
        else:
            read_size = size

        if read_size > 0:
            context_list = self._array_traits.create_context()
            for context in context_list:
                context.read_descriptor(reader)

            for index in range(read_size):
                if self._check_offset_method is not None:
                    reader.alignto(8)
                    self._check_offset_method(index, reader.bitposition)
                self._raw_array.append(self._array_traits.read(iter(context_list), reader, index))

    def write(self, writer: BitStreamWriter) -> None:
        """
        Writes packed array to the bit stream.

        :param writer: Bit stream where to write.
        """

        size = len(self._raw_array)

        if self._is_auto:
            writer.write_varsize(size)

        if size > 0:
            context_list = self._array_traits.create_context()
            for element in self._raw_array:
                self._array_traits.init_context(iter(context_list), element)
            for context in context_list:
                context.write_descriptor(writer)

            for index in range(size):
                if self._check_offset_method is not None:
                    writer.alignto(8)
                    self._check_offset_method(index, writer.bitposition)
                self._array_traits.write(iter(context_list), writer, self._raw_array[index])

class PackingContext:
    """
    Base class for packing context. Default implementation doen't pack and just calls provided array traits.
    """

    def __init__(self, array_traits: typing.Any):
        """
        :param array_traits: Standard array traits.
        """

        self._array_traits = array_traits

    def init(self, element: int) -> None:
        """
        Makes initialization step for the provided array element.

        :param element: Current element of the array.
        """

        # default implementation does nothing

    def bitsizeof_descriptor(self, _bitposition: int) -> int:
        """
        Returns length of the descriptor stored in the bit stream in bits.

        :param _bitposition: Current bit stream position.
        :returns: Length of the descriptor stored in the bit stream in bits.
        """

        assert self is not None
        return 1

    def bitsizeof(self, bitposition: int, element: int) -> int:
        """
        Returns length of the element representation stored in the bit stream in bits.

        :param bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Length of the element representation stored in the bit stream in bits.
        """

        if self._array_traits.HAS_BITSIZEOF_CONSTANT:
            return self._array_traits.bitsizeof()
        else:
            return self._array_traits.bitsizeof(bitposition, element)

    def write_descriptor(self, writer: BitStreamWriter) -> None:
        """
        Writes the delta packing descriptor to the bit stream. Called for all contexts before the first element
        is written.

        :param writer: Bit stream writer.
        """

        assert self is not None
        writer.write_bool(False)

    def write(self, writer: BitStreamWriter, element: int) -> None:
        """
        Writes the packed element representation to the bit stream. This is not called for the first element
        since it's written using standard array traits.

        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        self._array_traits.write(writer, element)

    def read_descriptor(self, reader: BitStreamReader) -> None:
        """
        Reads the delta packing descriptor from the bit stream. Called for all context before the first element
        is read.

        :param reader: Bit stream reader.
        """

        assert self is not None
        is_packed = reader.read_bool()
        assert not is_packed

    def read(self, reader: BitStreamReader) -> int:
        """
        Reads the packed element from the bit stream. This is not called for the first element since it's read
        using standard array traits.

        :param reader: Bit stream reader.
        """

        return self._array_traits.read(reader)

PackingContextIterator = typing.Iterator[PackingContext]

class DeltaContext(PackingContext):
    """
    Context for delta packing created for each packable field.

    Contexts are always newly created for each array operation (bitsizeof, initialize_offsets, read, write).
    They must be initialized at first via calling the init method for each packable element present in the
    array. After the full initialization, only a single method (bitsizeof, read, write) can be repeatedly
    called for exactly the same sequence of packable elements.

    Note that *_descriptor methods doesn't change context's internal state and can be called as needed. They
    are designed to be called once for each context before the actual operation.

    Example:
    context = DeltaContext(array_traits)
    for element in array:
        context.init(element) # initialization step, needed for max_bit_number calculation
    context.write_descriptor(writer)
    for element in array:
        context.write(writer, element)
    """

    def __init__(self, array_traits: typing.Any) -> None:
        super().__init__(array_traits)
        self._is_packed = False
        self._max_bit_number = 0
        self._previous_element: typing.Optional[int] = None
        self._processing_started = False

    def init(self, element: int) -> None:
        if self._previous_element is None:
            self._previous_element = element
            self._is_packed = True
        else:
            delta = element - self._previous_element
            max_bit_number = delta.bit_length()
            # if delta is negative, we need one bit more because of sign
            # if delta is positive, we need one bit more because delta are treated as signed number
            # if delta is zero, we need one bit more because bit_length() returned zero
            if max_bit_number > self._max_bit_number:
                self._max_bit_number = max_bit_number
                if max_bit_number > self._MAX_BIT_NUMBER_LIMIT:
                    self._is_packed = False
            self._previous_element = element

    def bitsizeof_descriptor(self, _bitposition: int) -> int:
        if self._is_packed:
            return 1 + self._MAX_BIT_NUMBER_BITS
        else:
            return 1

    def bitsizeof(self, bitposition: int, element: int) -> int:
        if not self._processing_started or not self._is_packed:
            self._processing_started = True
            return super().bitsizeof(bitposition, element)
        else: # packed and not first
            return self._max_bit_number + 1

    def write_descriptor(self, writer: BitStreamWriter) -> None:
        writer.write_bool(self._is_packed)
        if self._is_packed:
            writer.write_signed_bits(self._max_bit_number, self._MAX_BIT_NUMBER_BITS)

    def write(self, writer: BitStreamWriter, element: int) -> None:
        if not self._processing_started or not self._is_packed:
            self._processing_started = True
            self._previous_element = element
            super().write(writer, element)
        else: # packed and not first
            assert self._previous_element is not None
            delta = element - self._previous_element
            writer.write_signed_bits(delta, self._max_bit_number + 1)
            self._previous_element = element

    def read_descriptor(self, reader: BitStreamReader) -> None:
        self._is_packed = reader.read_bool()
        if self._is_packed:
            self._max_bit_number = reader.read_bits(self._MAX_BIT_NUMBER_BITS)

    def read(self, reader: BitStreamReader) -> int:
        if not self._processing_started or not self._is_packed:
            self._processing_started = True
            element = super().read(reader)
            self._previous_element = element
            return element
        else: # packed and not first
            assert self._previous_element is not None
            delta = reader.read_signed_bits(self._max_bit_number + 1)
            self._previous_element += delta
            return self._previous_element

    _MAX_BIT_NUMBER_BITS = 6
    _MAX_BIT_NUMBER_LIMIT = 63

class PackingContextBuilder:
    """
    Context builder used to separate generated API from the particular packing implementation.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """

        self._context_list: typing.List[PackingContext] = []

    def add_context(self, array_traits: typing.Any) -> 'PackingContextBuilder':
        """
        Adds a packable field.

        :param array_traits: Standard array traits which determine type of the packable field.
        :returns: Self for convenient context building concatenation.
        """

        if (isinstance(array_traits, (BitFieldArrayTraits,
                                      SignedBitFieldArrayTraits,
                                      VarUInt16ArrayTraits,
                                      VarUInt32ArrayTraits,
                                      VarUInt64ArrayTraits,
                                      VarUIntArrayTraits,
                                      VarSizeArrayTraits,
                                      VarInt16ArrayTraits,
                                      VarInt32ArrayTraits,
                                      VarInt64ArrayTraits,
                                      VarIntArrayTraits))):
            self._context_list.append(DeltaContext(array_traits))
        else:
            self._context_list.append(PackingContext(array_traits))

        return self

    def build(self) -> typing.List[PackingContext]:
        """
        Returns built list of delta packing contexts.
        """

        return self._context_list

class PackedArrayTraits:
    """
    Packed array traits.

    Packed array traits are used for all built-in types. Packing context builder creates an appropriate
    packing context for concrete types.
    """

    def __init__(self, array_traits: typing.Any) -> None:
        """
        Constructor.

        :param array_traits: Standard array traits.
        """

        self._array_traits = array_traits

    def create_context(self) -> typing.List[PackingContext]:
        """
        Creates packing context.

        :returns: List of packing contexts.
        """

        return PackingContextBuilder().add_context(self._array_traits).build()

    @staticmethod
    def init_context(context_iterator: PackingContextIterator, element: int) -> None:
        """
        Calls context initialization step for the current element.

        :param context_iterator: Packing context iterator.
        :param element: Current element.
        """

        context = next(context_iterator)
        context.init(element)

    @staticmethod
    def bitsizeof(context_iterator: PackingContextIterator, bitposition: int, element: int) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_iterator: Packing context iterator.
        :param bitposition: Current bit stream position.
        :param elemnet: Current element.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        context = next(context_iterator)
        return context.bitsizeof(bitposition, element)

    @staticmethod
    def initialize_offsets(context_iterator: PackingContextIterator, bitposition: int, element: int) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_iterator: Delta context iterator.
        :param bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        context = next(context_iterator)
        return bitposition + context.bitsizeof(bitposition, element)

    @staticmethod
    def write(context_iterator: PackingContextIterator, writer: BitStreamWriter, element: int) -> None:
        """
        Writes the element to the bit stream.

        :param context_iterator: Delta context iterator.
        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        context = next(context_iterator)
        context.write(writer, element)

    @staticmethod
    def read(context_iterator: PackingContextIterator, reader: BitStreamReader, _index: int) -> int:
        """
        Read an element from the bit stream.

        :param context_iterator: Delta context iterator.
        :param reader: Bit stream reader.
        :param _index: Not used.
        :returns: Read element value.
        """

        context = next(context_iterator)
        return context.read(reader)

class ObjectPackedArrayTraits:
    """
    Packed array traits for Zserio objects.

    This traits are used for Zserio objects which must implement special *_packed methods to allow itself
    to be used in a packed array.
    """

    def __init__(self, packed_object_creator: typing.Callable[[PackingContextIterator, BitStreamReader, int],
                                                              typing.Any],
                 packed_context_creator: typing.Callable[[PackingContextBuilder], None]):
        """
        Constructor.

        :param packed_object_creator: Creator which creates packed object from the element index.
        :param packed_context_creator: Creator which creates contexts for object packing.
        """

        self._packed_object_creator = packed_object_creator
        self._packed_context_creator = packed_context_creator

    def create_context(self) -> typing.List[PackingContext]:
        """
        Creates packing context.

        :returns: List of delta packing contexts.
        """

        context_builder = PackingContextBuilder()
        self._packed_context_creator(context_builder)
        return context_builder.build()

    @staticmethod
    def init_context(context_iterator: PackingContextIterator, element: typing.Any) -> None:
        """
        Calls context initialization step for the current element.

        :param context_iterator: Packing context iterator.
        :param element: Current element.
        """

        element.init_packing_context(context_iterator)

    @staticmethod
    def bitsizeof(context_iterator: PackingContextIterator, bitposition: int, element: typing.Any) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_iterator: Packing context iterator.
        :param bitposition: Current bit stream position.
        :param elemnet: Current element.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        return element.bitsizeof_packed(context_iterator, bitposition)

    @staticmethod
    def initialize_offsets(context_iterator: PackingContextIterator,
                           bitposition: int, element: typing.Any) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_iterator: Packing context iterator.
        :param bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        return element.initialize_offsets_packed(context_iterator, bitposition)

    @staticmethod
    def write(context_iterator: PackingContextIterator, writer: BitStreamWriter, element: typing.Any) -> None:
        """
        Writes the element to the bit stream.

        :param context_iterator: Delta context iterator.
        :param writer: Bit stream writer.
        :param element: Element to write.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        element.write_packed(context_iterator, writer)

    def read(self, context_iterator: PackingContextIterator, reader: BitStreamReader, index: int) -> typing.Any:
        """
        Read an element from the bit stream.

        :param context_iterator: Packing context iterator.
        :param reader: Bit stream reader.
        :param index: Index of the current element.
        :returns: Read element value.
        """

        return self._packed_object_creator(context_iterator, reader, index)
