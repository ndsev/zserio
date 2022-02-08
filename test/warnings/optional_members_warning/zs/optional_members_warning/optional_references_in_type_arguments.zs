package optional_members_warning.optional_references_in_type_arguments;

enum uint8 BasicColor
{
    BLACK,
    WHITE
};

struct ColorTones(uint8 diffNumBlackTones)
{
    int32 tones[diffNumBlackTones];
};

subtype ColorTones SubtypedColorTones;

struct Another
{
    BasicColor     basicColor;
    uint8          numWhiteTones if basicColor == BasicColor.WHITE;
};

struct Container(Another another)
{
    BasicColor     basicColor;
    uint8          numBlackTones if basicColor == BasicColor.BLACK;
    optional uint8 autoNumBlackTones;
    uint8          numWhiteTones if basicColor == BasicColor.WHITE;

    // These all are ok!
    ColorTones(numWhiteTones) whiteTones if basicColor == BasicColor.WHITE;
    ColorTones(another.numWhiteTones) anotherWhiteTones if another.basicColor == BasicColor.WHITE;

    // These all must fire warning!
    ColorTones(numBlackTones) blackTonesArray1[]; // array
    SubtypedColorTones(numBlackTones) blackTonesArray2[]; // subtyped array

    ColorTones(numBlackTones) blackTones1; // non-array
    SubtypedColorTones(numBlackTones) blackTones2; // subtyped non-array

    ColorTones(autoNumBlackTones) autoBlackTonesArray1[]; // array
    SubtypedColorTones(autoNumBlackTones) autoBlackTonesArray2[]; // subtyped array

    ColorTones(autoNumBlackTones) autoBlackTones1; // non-array
    SubtypedColorTones(autoNumBlackTones) autoBlackTones2; // subtyped non-array

    ColorTones(numBlackTones + numWhiteTones) blackAndWhiteTones if basicColor == BasicColor.BLACK;
    ColorTones(numBlackTones + autoNumBlackTones) mixedTones if basicColor == BasicColor.BLACK;
    optional ColorTones(autoNumBlackTones) mixedTonesArray[];
};
