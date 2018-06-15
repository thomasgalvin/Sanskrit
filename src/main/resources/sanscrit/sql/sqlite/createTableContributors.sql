create table if not exists Contributors(
    parentUuid text not null,
    name text not null,
    sortByName text not null,
    role int not null,
    ordinal int not null
);