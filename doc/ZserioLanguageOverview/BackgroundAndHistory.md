## Background & History

Zserio is based on the work of
[Godmar Back](http://people.cs.vt.edu/~gback/papers/gback-datascript-gpce2002.pdf) and was called DataScript
at that time.

His work was taken up by the members of the
[Navigation Data Standard Association](https://www.nds-association.org) (an industry consortium of companies
from the digital maps business) and had been developed internally until 2018.

While Back's reference implementation provided a great start, some language extensions were added to better
suit the requirements of the NDS members.

As a major addition to the DataScript language, a relational extension had been introduced, which permits
the definition of hybrid data models, where the high-level access structures are implemented by relational
tables and indices, whereas the bulk data are stored in single columns as BLOBs with a format defined in
DataScript, hence then named Relational DataScript. Since the Relational DataScript was used on top of
a SQLite database also some SQLite specific language elements had been added during that time.

By 2018 the NDS consortium decided to open source the work done since they forked off from Godmar Back's
reference implementation.

Since the name DataScript already was used by other projects for different purposes and the fact that it has
never really been a script language anyhow, a new name needed to be found: zserio. An acronym for zero
serialization overhead and pronounced with a silent "s".

[\[top\]](ZserioLanguageOverview.md#language-guide)
