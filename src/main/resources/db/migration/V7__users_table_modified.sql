alter table users drop name;
alter table users add column chat_id bigint unique not null;