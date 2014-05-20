# Add Ad-stuff

# --- !Ups

create table ad (
  id serial not null,
  ad_type varchar(255),
  brand varchar(255),
  condition varchar(255),
  description varchar(2047),
  location varchar(255),
  name varchar(255),
  price float8,
  size varchar(255),
  ad_category bigint,
  primary key (id)
);
  
create table ad_category (
  id serial not null,
  level integer,
  name varchar(255),
  primary key (id)
);
  
create table ad_category_ad_category (
  ad_category_id bigint not null,
  subCategories_id bigint not null
);
  
create table ad_color (
  Ad_id bigint not null,
  color varchar(255)
);
  
create table ad_picture (
  id serial not null,
  index integer,
  path varchar(255),
  ad_id bigint,
  primary key (id)
);

alter table ad
add constraint FK_k716j1v3jwdl17w0np6bypv13
foreign key (ad_category)
references ad_category;

alter table ad_category_ad_category
add constraint FK_mbyp6y3qrl55enfvnihm12r0v
foreign key (subCategories_id)
references ad_category;

alter table ad_category_ad_category
add constraint FK_n1mphu0ej5o9d5da7ax8tdt9b
foreign key (ad_category_id)
references ad_category;

alter table ad_color
add constraint FK_4vcf2cwu4oxpjasu10cfccywm
foreign key (Ad_id)
references ad;

alter table ad_picture
add constraint FK_i04bmy24u5ifpqnx8key5vnvw
foreign key (ad_id)
references ad;

# --- !Downs

drop table if exists ad_picture;
drop table if exists ad_color;
drop table if exists ad_category_ad_category;
drop table if exists ad_category;
drop table if exists ad;
drop table if exists picture;