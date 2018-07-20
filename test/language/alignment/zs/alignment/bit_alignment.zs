package alignment.bit_alignment;

struct BitAlignment
{
    // See precalculated alignments and positions in comments for start positions 0 || 78
    // a:alignment pos:position || a:alignment pos:position
align(1):
    // a:0 pos:1 || a:0 pos:78
    bit:1   aligned1Field;

align(2):
    // a:1 pos:2 || a:1 pos:80
    bit:2   aligned2Field;

align(3):
    // a:2 pos:6 || a:2 pos:84
    bit:3   aligned3Field;

align(4):
    // a:3 pos:12 || a:1 pos:88
    bit:4   aligned4Field;

align(5):
    // a:4 pos:20 || a:3 pos:95
    bit:5   aligned5Field;

align(6):
    // a:5 pos:30 || a:2 pos:102
    bit:6   aligned6Field;

align(7):
    // a:6 pos:42 || a:4 pos:112
    bit:7   aligned7Field;

align(8):
    // a:7 pos:56 || a:1 pos:120
    bit:8   aligned8Field;

    bit:1   alignment16Break;
align(16):
    // a:15 pos:80 || a:15 pos:144
    uint16   aligned16Field;

    bit:1   alignment32Break;
align(32):
    // a:31 pos:128 || a:31 pos:192
    uint32   aligned32Field;

    bit:33   alignment64Break;
align(64):
    // a:63 pos:256 || a:63 pos:320
    uint64   aligned64Field;
};
