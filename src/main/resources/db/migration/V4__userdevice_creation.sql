alter table devices
add column username text references users(name);