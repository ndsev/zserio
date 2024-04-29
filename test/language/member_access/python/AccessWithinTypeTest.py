import zserio

import MemberAccess


class AccessWithinTypeTest(MemberAccess.TestCase):
    def testRead(self):
        numSentences = 10
        wrongArrayLength = False
        writer = zserio.BitStreamWriter()
        self._writeMessageToStream(writer, numSentences, wrongArrayLength)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        message = self.api.Message.from_reader(reader)
        self._checkMessage(message, numSentences)

    def testReadWrongArrayLength(self):
        numSentences = 10
        wrongArrayLength = True
        writer = zserio.BitStreamWriter()
        self._writeMessageToStream(writer, numSentences, wrongArrayLength)
        reader = zserio.BitStreamReader(writer.byte_array, writer.bitposition)
        with self.assertRaises(zserio.PythonRuntimeException):
            message = self.api.Message.from_reader(reader)
            self._checkMessage(message, numSentences)

    def testWrite(self):
        numSentences = 13
        wrongArrayLength = False
        message = self._createMessage(numSentences, wrongArrayLength)
        bitBuffer = zserio.serialize(message)
        readMessage = zserio.deserialize(self.api.Message, bitBuffer)
        self._checkMessage(readMessage, numSentences)
        self.assertTrue(message == readMessage)

    def testWriteWrongArrayLength(self):
        numSentences = 13
        wrongArrayLength = True
        message = self._createMessage(numSentences, wrongArrayLength)
        writer = zserio.BitStreamWriter()
        with self.assertRaises(zserio.PythonRuntimeException):
            message.write(writer)

    def _writeMessageToStream(self, writer, numSentences, wrongArrayLength):
        writer.write_bits(self.VERSION_VALUE, 16)
        writer.write_bits(numSentences, 16)
        numStrings = numSentences - 1 if wrongArrayLength else numSentences
        for i in range(numStrings):
            writer.write_string(self.SENTENCE_PREFIX + str(i))

    def _checkMessage(self, message, numSentences):
        self.assertEqual(self.VERSION_VALUE, message.header.version)
        self.assertEqual(numSentences, message.header.num_sentences)

        sentences = message.sentences
        self.assertEqual(numSentences, len(sentences))
        for i in range(numSentences):
            expectedSentence = self.SENTENCE_PREFIX + str(i)
            self.assertTrue(sentences[i] == expectedSentence)

    def _createMessage(self, numSentences, wrongArrayLength):
        header = self.api.Header(self.VERSION_VALUE, numSentences)
        numStrings = numSentences - 1 if wrongArrayLength else numSentences
        sentences = [self.SENTENCE_PREFIX + str(i) for i in range(numStrings)]

        return self.api.Message(header, sentences)

    VERSION_VALUE = 0xAB
    SENTENCE_PREFIX = "This is sentence #"
