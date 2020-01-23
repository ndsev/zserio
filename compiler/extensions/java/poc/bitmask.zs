package bitmask_types.uint8_bitmask;

bitmask uint8 Emotions
{
    SAD     = 0x01,
    CHEERY  = 0x02,
    UNHAPPY = 0x04,
    HAPPY   = 0x08,
    SANE    = 0x10,
    MAD     = 0x20,
    ALIVE   = 0x40,
    // This checks if comma is allowed after the last item.
    DEAD    = 0x80,
};
