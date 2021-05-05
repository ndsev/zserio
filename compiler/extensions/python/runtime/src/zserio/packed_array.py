"""
The module implements abstraction for delta compressed arrays used by Zserio python extension.
"""

import typing

from zserio.array import ObjectArrayTraits
from zserio.bitposition import alignto
from zserio.bitsizeof import bitsizeof_varsize
from zserio.bitreader import BitStreamReader
from zserio.bitwriter import BitStreamWriter
from zserio.hashcode import calc_hashcode, HASH_SEED

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
            for index in range(size):
                self._array_traits.init_context(iter(context_list), self._raw_array[index], index == 0)
            for context in context_list:
                end_bitposition += context.bitsizeof_descriptor(end_bitposition)

            for index in range(size):
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                end_bitposition += self._array_traits.bitsizeof(iter(context_list), end_bitposition,
                                                                self._raw_array[index], index == 0)

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
                self._array_traits.init_context(iter(context_list), self._raw_array[index], index == 0)
            for context in context_list:
                end_bitposition += context.bitsizeof_descriptor(end_bitposition)

            for index in range(size):
                if self._set_offset_method is not None:
                    end_bitposition = alignto(8, end_bitposition)
                    self._set_offset_method(index, end_bitposition)
                end_bitposition = self._array_traits.initialize_offsets(iter(context_list), end_bitposition,
                                                                        self._raw_array[index], index == 0)

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
            for index in range(size):
                self._array_traits.init_context(iter(context_list), self._raw_array[index], index == 0)
            for context in context_list:
                context.write_descriptor(writer)

            for index in range(size):
                if self._check_offset_method is not None:
                    writer.alignto(8)
                    self._check_offset_method(index, writer.bitposition)
                self._array_traits.write(iter(context_list), writer, self._raw_array[index], index == 0)

class DeltaContext:
    """
    Context for delta packing created for each packable field.
    """

    def __init__(self, packed_array_traits : typing.Any) -> None:
        """
        Delta context constructor.

        :param packed_array_traits: Standard array traits used for the first element or
                             in case that packing is not used.
        """

        self._array_traits = packed_array_traits
        self._max_bit_number = 0
        self._previous_element = 0
        self._is_packed = True

    def init(self, element : int, is_first : bool) -> None:
        """
        Makes initialization step for the provided array element.

        :param element: Current element of the array.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        if not is_first:
            delta = element - self._previous_element
            max_bit_number = delta.bit_length()
            if max_bit_number > self._max_bit_number:
                self._max_bit_number = max_bit_number
                if max_bit_number > self._MAX_BIT_NUMBER_LIMIT:
                    self._is_packed = False
        self._previous_element = element

    def bitsizeof_descriptor(self, _bitposition : int) -> int:
        """
        Returns length of the descriptor stored in the bit stream in bits.

        :param _bitposition: Current bit stream position.
        :returns: Length of the descriptor stored in the bit stream in bits.
        """

        if self._is_packed:
            return 1 + self._MAX_BIT_NUMBER_BITS
        else:
            return 1

    def bitsizeof(self, bitposition : int, element : int) -> int:
        """
        Returns length of the element representation stored in the bit stream in bits.

        :param bitposition: Current bit stream position.
        :param element: Current element.
        :returns: Length of the element representation stored in the bit stream in bits.
        """

        if self._is_packed:
            return self._max_bit_number + 1
        else:
            if self._array_traits.HAS_BITSIZEOF_CONSTANT:
                return self._array_traits.bitsizeof()
            else:
                return self._array_traits.bitsizeof(bitposition, element)

    def write_descriptor(self, writer : BitStreamWriter) -> None:
        """
        Writes the delta packing descriptor to the bit stream. Called for all contexts before the first element
        is written.

        :param writer: Bit stream writer.
        """

        writer.write_bool(self._is_packed)
        if self._is_packed:
            writer.write_signed_bits(self._max_bit_number, self._MAX_BIT_NUMBER_BITS)

    def write(self, writer : BitStreamWriter, element : int) -> None:
        """
        Writes the packed element representation to the bit stream. This is not called for the first element
        since it's written using standard array traits.

        :param writer: Bit stream writer.
        :param element: Element to write.
        """

        if self._is_packed:
            delta = element - self._previous_element
            writer.write_signed_bits(delta, self._max_bit_number + 1)
            self._previous_element = element
        else:
            self._array_traits.write(writer, element)

    def read_descriptor(self, reader : BitStreamReader) -> None:
        """
        Reads the delta packing descriptor from the bit stream. Called for all context before the first element
        is read.

        :param reader: Bit stream reader.
        """

        self._is_packed = reader.read_bool()
        if self._is_packed:
            self._max_bit_number = reader.read_bits(self._MAX_BIT_NUMBER_BITS)

    def read(self, reader : BitStreamReader, index: int) -> int:
        """
        Reads the packed element from the bit stream. This is not called for the first element since it's read
        using standard array traits.

        :param reader: Bit stream reader.
        :param index: Index of the element which is just read.
        """

        if self._is_packed:
            delta = reader.read_signed_bits(self._max_bit_number + 1)
            self._previous_element += delta
            return self._previous_element
        else:
            return self._array_traits.read(reader, index)

    _MAX_BIT_NUMBER_BITS = 6
    _MAX_BIT_NUMBER_LIMIT = 63

class DeltaContextBuilder:
    """
    Delta context builder used to separate generated API from the particular packing implementation.
    """

    def __init__(self) -> None:
        """
        Constructor.
        """

        self._context_list : typing.List[DeltaContext] = []

    def add_context(self, packed_array_traits : typing.Any) -> 'DeltaContextBuilder':
        """
        Adds a packable field.

        :param packed_array_traits: Standard array traits which determine type of the packable field.
        :returns: Self for convenient context building concatenation.
        """

        self._context_list.append(DeltaContext(packed_array_traits))
        return self

    def build(self) -> typing.List[DeltaContext]:
        """
        Returns built list of delta packing contexts.
        """

        return self._context_list

class DeltaArrayTraits:
    """
    Delta array traits.

    Delta array traits are used for all builtin packable types.
    Note that only integral types are packable using delta packing.
    """

    def __init__(self, packed_array_traits : typing.Any) -> None:
        """
        Constructor.

        :param packed_array_traits: Standard array traits.
        """

        self._array_traits = packed_array_traits

    def create_context(self) -> typing.List[DeltaContext]:
        """
        Creates delta packing context.

        :returns: List of delta packing contexts.
        """

        return DeltaContextBuilder().add_context(self._array_traits).build()

    @staticmethod
    def init_context(context_iterator : typing.Iterator[DeltaContext],
                     element : int, is_first : bool) -> None:
        """
        Calls context initialization step for the current element.

        :param context_iterator: Delta context iterator.
        :param element: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        context = next(context_iterator)
        context.init(element, is_first)

    def bitsizeof(self, context_iterator : typing.Iterator[DeltaContext], bitposition : int,
                  element : int, is_first : bool) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_iterator: Delta context iterator.
        :param bitposition: Current bit stream position.
        :param elemnet: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        context = next(context_iterator)
        if is_first:
            context.init(element, is_first=True)
            if self._array_traits.HAS_BITSIZEOF_CONSTANT:
                return self._array_traits.bitsizeof()
            else:
                return self._array_traits.bitsizeof(bitposition, element)
        else:
            return context.bitsizeof(bitposition, element)

    def initialize_offsets(self, context_iterator : typing.Iterator[DeltaContext],
                           bitposition : int, element : int, is_first : bool) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_iterator: Delta context iterator.
        :param bitposition: Current bit stream position.
        :param element: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        context = next(context_iterator)
        if is_first:
            context.init(element, is_first=True)
            return self._array_traits.initialize_offsets(bitposition, element)
        else:
            return bitposition + context.bitsizeof(bitposition, element)

    def write(self, context_iterator : typing.Iterator[DeltaContext],
              writer : BitStreamWriter, element : int, is_first : bool) -> None:
        """
        Writes the element to the bit stream.

        :param context_iterator: Delta context iterator.
        :param writer: Bit stream writer.
        :param element: Element to write.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        context = next(context_iterator)
        if is_first:
            context.init(element, is_first=True)
            self._array_traits.write(writer, element)
        else:
            context.write(writer, element)

    def read(self, context_iterator : typing.Iterator[DeltaContext],
             reader : BitStreamReader, index : int) -> int:
        """
        Read an element from the bit stream.

        :param context_iterator: Delta context iterator.
        :param reader: Bit stream reader.
        :param index: Index of the current element.
        :returns: Read element value.
        """

        context = next(context_iterator)
        if index == 0:
            element = self._array_traits.read(reader, index)
            context.init(element, is_first=True)
            return element
        else:
            return context.read(reader, index)

class ObjectDeltaArrayTraits:
    """
    Delta array traits for Zserio objects.

    This traits are used for Zserio objects which must implement special *_packed methods used to handle
    packable fields.
    """

    def __init__(self, array_traits : ObjectArrayTraits, object_class : typing.Type):
        """
        Constructor.

        :param array_traits: Standard array traits.
        :param object_class: Zserio object class.
        """

        self._array_traits = array_traits
        self._object_class = object_class

    def create_context(self) -> typing.List[DeltaContext]:
        """
        Creates delta packing context.

        :returns: List of delta packing contexts.
        """

        context_builder = DeltaContextBuilder()
        self._object_class.create_packed_context(context_builder)
        return context_builder.build()

    @staticmethod
    def init_context(context_iterator : typing.Iterator[DeltaContext],
                     element : typing.Any, is_first : bool) -> None:
        """
        Calls context initialization step for the current element.

        :param context_iterator: Delta context iterator.
        :param element: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        element.init_packed_context(context_iterator, is_first)

    def bitsizeof(self, context_iterator : typing.Iterator[DeltaContext],
                  bitposition : int, element : typing.Any, is_first : bool) -> int:
        """
        Returns length of the array element stored in the bit stream in bits.

        :param context_iterator: Delta context iterator.
        :param bitposition: Current bit stream position.
        :param elemnet: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        :returns: Length of the array element stored in the bit stream in bits.
        """

        if is_first:
            element.init_packed_context(context_iterator, is_first=True)
            return self._array_traits.bitsizeof(bitposition, element)
        else:
            return element.bitsizeof_packed(context_iterator, bitposition)

    def initialize_offsets(self, context_iterator : typing.Iterator[DeltaContext],
                           bitposition : int, element : typing.Any, is_first : bool) -> int:
        """
        Calls indexed offsets initialization for the current element.

        :param context_iterator: Delta context iterator.
        :param bitposition: Current bit stream position.
        :param element: Current element.
        :param is_first: Denotes whether this element is the first element of the array.
        :returns: Updated bit stream position which points to the first bit after this element.
        """

        if is_first:
            element.init_packed_context(context_iterator, is_first=True)
            return self._array_traits.initialize_offsets(bitposition, element)
        else:
            return element.initialize_offsets_packed(context_iterator, bitposition)

    @staticmethod
    def write(context_iterator : typing.Iterator[DeltaContext], writer : BitStreamWriter,
              element : typing.Any, is_first : bool) -> None:
        """
        Writes the element to the bit stream.

        :param context_iterator: Delta context iterator.
        :param writer: Bit stream writer.
        :param element: Element to write.
        :param is_first: Denotes whether this element is the first element of the array.
        """

        if is_first:
            element.init_packed_context(context_iterator, is_first=True)
            element.write(writer)
        else:
            element.write_packed(context_iterator, writer)

    def read(self, context_iterator : typing.Iterator[DeltaContext], reader : BitStreamReader,
             index : int) -> typing.Any:
        """
        Read an element from the bit stream.

        :param context_iterator: Delta context iterator.
        :param reader: Bit stream reader.
        :param index: Index of the current element.
        :returns: Read element value.
        """

        if index == 0:
            element = self._array_traits.read(reader, index)
            element.init_packed_context(context_iterator, is_first=True)
            return element
        else:
            element = self._object_class()
            element.read_packed(context_iterator, reader, index)
            return element
