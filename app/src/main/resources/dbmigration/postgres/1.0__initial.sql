-- apply changes
create table urls (
  id                            bigint generated by default as identity not null,
  name                          varchar(255),
  created_at                    varchar(255),
  when_created                  timestamptz not null,
  constraint pk_urls primary key (id)
);
