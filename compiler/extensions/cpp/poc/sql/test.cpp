#include <stdio.h>
#include "Database.h"

int main(int argc, char* argv[])
{
    const char* dbName = "test.sqlite";
    remove(dbName);
    Database db(dbName);
    db.createSchema();
    Table tbl = db.getTbl();
    tbl.write({
            {0, Data{2, {1, 2}}},
            {1, Data{3, {3, 2, 1}}},
            {2, zserio::NullOpt},
            {3, Data{0, {}}}
    });

    Table::Reader reader = tbl.createReader();
    cursor = tbl.createCursor();
    while (cursor.hasNext())
    {
        rows.emplace_back(cursor); // A
        auto row = Table::Row(reader); // B
        auto row = reader.next(); // C

        std::cout << "{" << *row.getPk() << ", ";

        if (row.getData())
        {
            std::cout << "Data{" << row.getData()->getLen() << ", {";
            const auto& array = row.getData()->getArray();
            for (size_t i = 0; i < array.size(); ++i)
                std::cout << array.at(i) << (i != array.size() - 1 ? ", " : "");
            std::cout << "}}";
        }
        else
        {
            std::cout << "NULL";
        }

        std::cout << "}" << std::endl;
    }
}
