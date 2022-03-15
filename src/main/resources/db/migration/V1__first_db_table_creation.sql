create table users(
id_users uuid primary key,
name text not null,
sleeping_time int,
wakeup_time int,
night_charge boolean not null
);

create table devices(
id_device uuid primary key,
name text not null,
charging_time decimal (2,1) not null
);