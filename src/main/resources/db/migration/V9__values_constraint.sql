alter table users
add constraint sleeping_time_check
check (
sleeping_time >= 0
and sleeping_time <= 23
);

alter table users
add constraint wakeup_time_check
check (
wakeup_time >= 0
and wakeup_time <= 23
);

alter table devices
add constraint charging_time_check
check (
charging_time > 0
);