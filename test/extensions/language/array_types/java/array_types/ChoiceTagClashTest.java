package choice_tag_clash;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import zserio.runtime.io.BitBuffer;
import zserio.runtime.io.SerializeUtil;

public class ChoiceTagClashTest
{
    @Test
    public void writeRead()
    {
        final ChoiceTagClash choiceTagClash = new ChoiceTagClash(createChoices(), createUnions());

        final BitBuffer bitBuffer = SerializeUtil.serialize(choiceTagClash);
        final ChoiceTagClash readChoiceTagClash = SerializeUtil.deserialize(ChoiceTagClash.class, bitBuffer);

        assertEquals(choiceTagClash, readChoiceTagClash);
    }

    private TestChoice[] createChoices()
    {
        final TestChoice[] choices = new TestChoice[NUM_CHOICES];
        for (int i = 0; i < NUM_CHOICES; ++i)
        {
            choices[i] = new TestChoice(i % 2 == 0);
            if (i % 2 == 0)
            {
                choices[i].setChoiceTag(i);
            }
            else
            {
                choices[i].setStringField("text " + i);
            }
        }
        return choices;
    }

    private TestUnion[] createUnions()
    {
        final TestUnion[] unions = new TestUnion[NUM_UNIONS];
        for (int i = 0; i < NUM_UNIONS; ++i)
        {
            unions[i] = new TestUnion();
            if (i % 2 == 0)
            {
                unions[i].setChoiceTag(i);
            }
            else
            {
                unions[i].setStringField("text " + i);
            }
        }
        return unions;
    }

    private static final int NUM_CHOICES = 10;
    private static final int NUM_UNIONS = 13;
}
