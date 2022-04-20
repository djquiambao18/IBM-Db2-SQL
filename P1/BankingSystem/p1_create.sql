--
-- CS157A 
-- Project #1
-- 
--

-- db2 -tvf p1_create.sql

connect to cs157a;

drop table p1.account;
drop table p1.customer;

create table p1.customer (
  id      integer     not null generated by default as identity (start WITH 100, increment by 1, no cache),
  name    varchar(15) not null,
  gender  char        not null check (gender = 'M' or gender = 'F'),
  age     integer     not null check (age >= 0),
  pin     integer     not null check (pin >= 0),
  constraint pk_id primary key (id)
);

create table p1.account (
  number  integer     not null generated by default as identity (start WITH 1000, increment by 1, no cache),
  id      integer     not null,
  balance integer     not null check (balance >= 0),
  type    char        not null check (type = 'C' or type = 'S'),
  status  char        not null check (status = 'A' or status = 'I'),
  constraint pk_number primary key (number),
  constraint fk_id foreign key (id) references p1.customer(id) on delete cascade
);

terminate;