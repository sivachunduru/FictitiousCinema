drop table if exists orders;
create table orders (user_id VARCHAR(50), show_date DATE, cinema_id VARCHAR(50));
insert into orders (user_id, show_date, cinema_id) values ('chris_rivers', '2015-12-01', '267eedb8-0f5d-42d5-8f43-72426b9fb3e6');
insert into orders (user_id, show_date, cinema_id) values ('garret_heaton', '2015-12-01', '267eedb8-0f5d-42d5-8f43-72426b9fb3e6');
insert into orders (user_id, show_date, cinema_id) values ('garret_heaton', '2015-12-02', '276c79ec-a26a-40a6-b3d3-fb242a5947b6');
insert into orders (user_id, show_date, cinema_id) values ('dwight_schrute', '2015-12-01', '7daf7208-be4d-4944-a3ae-c1c2f516f3e6');
insert into orders (user_id, show_date, cinema_id) values ('dwight_schrute', '2015-12-01', '267eedb8-0f5d-42d5-8f43-72426b9fb3e6');
insert into orders (user_id, show_date, cinema_id) values ('dwight_schrute', '2015-12-05', 'a8034f44-aee4-44cf-b32c-74cf452aaaae');
insert into orders (user_id, show_date, cinema_id) values ('dwight_schrute', '2015-12-05', '276c79ec-a26a-40a6-b3d3-fb242a5947b6');
