alter table devices
add column id_user uuid references users(id_users);