create table if not exists Children(
    parentUuid text not null,
    childUuid text not null,
    ordinal int not null
);