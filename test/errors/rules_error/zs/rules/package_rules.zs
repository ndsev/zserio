package rules.package_rules;

rule_group PackageRules
{
    /*!
    Special rule in this package.
    !*/
    rule "pkg-rules-01";

    /*!
    Rule which has the same id to rule in different package.
    !*/
    rule "rule-two";
};
