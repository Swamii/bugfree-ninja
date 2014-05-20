# Add User

# --- !Ups
create table "user"
(
  id serial not null,
  email character varying(255),
  device_uuid character varying(255) not null unique,
  api_key character varying(255),
  secret character varying(255),
  first_name character varying(255),
  last_name character varying(255),
  constraint user_pkey primary key (id)
);

# --- !Downs
drop table if exists "user";