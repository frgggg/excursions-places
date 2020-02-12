create table places (id int8 not null, address varchar(100) not null, info varchar(200) not null, name varchar(90) not null, primary key (id));

alter table if exists places add constraint UK8ojgqwd8wuu1sh8w09nathx11 unique (name, address);

create sequence hibernate_sequence start 1 increment 1;