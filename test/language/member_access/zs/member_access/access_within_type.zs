package member_access.access_within_type;

struct Header
{
    uint16  version;
    uint16  numSentences;
};

struct Message
{
    Header  header;
    string  sentences[header.numSentences];
};
