package expressions.question_mark;

struct QuestionMarkExpression
{
    bit:7   firstValue;
    bit:7   secondValue;
    bool    isFirstValueValid;

    function bit:7 validValue()
    {
        return (isFirstValueValid == true) ? firstValue : secondValue;
    }
};
