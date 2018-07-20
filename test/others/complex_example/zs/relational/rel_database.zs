package relational.rel_database;

import relational.rel_table.*;

sql_database PoiDatabase
{
    PoiServiceLocationTable     serviceLocationTable;
    PoiVirtualTileTable         virtualTileTable;
    PoiAddress                  ftsAddressSearch;
    BoundingBoxTable            boundingBox1;
    BoundingBoxTable            boundingBox2;
    PoiDataTable                poiData;
};
