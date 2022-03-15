create table electricity_rate(
datadate date primary key,
_0_1 decimal (5,2) not null,
_1_2 decimal (5,2) not null,
_2_3 decimal (5,2) not null,
_3_4 decimal (5,2) not null,
_4_5 decimal (5,2) not null,
_5_6 decimal (5,2) not null,
_6_7 decimal (5,2) not null,
_7_8 decimal (5,2) not null,
_8_9 decimal (5,2) not null,
_9_10 decimal (5,2) not null,
_10_11 decimal (5,2) not null,
_11_12 decimal (5,2) not null,
_12_13 decimal (5,2) not null,
_13_14 decimal (5,2) not null,
_14_15 decimal (5,2) not null,
_15_16 decimal (5,2) not null,
_16_17 decimal (5,2) not null,
_17_18 decimal (5,2) not null,
_18_19 decimal (5,2) not null,
_19_20 decimal (5,2) not null,
_20_21 decimal (5,2) not null,
_21_22 decimal (5,2) not null,
_22_23 decimal (5,2) not null,
_23_0 decimal (5,2) not null
)

create table users(
id_users uuid primary key,
name text not null,
sleeping_time int,
wakeup_time int,
night_charge boolean not null
)

create table devices(
id_device uuid primary key,
name text not null,
charging_time decimal (2,1) not null
)