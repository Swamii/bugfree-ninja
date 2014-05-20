# Add WikiArticle

# --- !Ups
create table wiki_article (
    id serial not null,
    article_id integer not null,
    content TEXT,
    timestamp timestamp with time zone,
    locale character varying(255),
    primary key (id)
);

# --- !Downs
drop table if exists wiki_article;