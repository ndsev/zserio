package bitfield_cyclic_definition_error;

const bit<ColorNumBits> ColorBitSize = 10;
const bit<ColorBitSize> ColorNumBits = 10; // cycle in bitfield!
