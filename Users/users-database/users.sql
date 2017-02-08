drop table if exists customers;
create table customers (user_id VARCHAR(50) NOT NULL PRIMARY KEY, user_name VARCHAR(100), last_active TIMESTAMP);
insert into customers (user_id, user_name, last_active) values ('chris_rivers', 'Chris Rivers', 20160901101112);
insert into customers (user_id, user_name, last_active) values ('peter_curley', 'Peter Curley', 20160901111112);
insert into customers (user_id, user_name, last_active) values ('garret_heaton', 'Garret Heaton', 20160901121112);
insert into customers (user_id, user_name, last_active) values ('michael_scott', 'Michael Scott', 20160901101012);
insert into customers (user_id, user_name, last_active) values ('jim_halpert', 'Jim Halpert', 20160901101212);
insert into customers (user_id, user_name, last_active) values ('pam_beesly', 'Pam Beesly', 20160901101122);
insert into customers (user_id, user_name, last_active) values ('dwight_schrute', 'Dwight Schrute', 20160901101132);
