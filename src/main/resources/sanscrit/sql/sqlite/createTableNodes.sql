create table if not exists Nodes(
    uuid text not null primary key,
    title text not null,
    subtitle text not null,
    manuscript text not null,
    description text not null,
    summary text not null,
    notes text not null
);