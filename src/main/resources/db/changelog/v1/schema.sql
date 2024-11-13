--liquibase formatted sql
--changeset senioravanti:1.0
--comment первая версия схемы БД
DROP TABLE IF EXISTS user_authorities;
DROP TABLE IF EXISTS refresh_tokens;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS authorities;

DROP INDEX IF EXISTS used_FK;
DROP INDEX IF EXISTS granted_FK;
DROP INDEX IF EXISTS authorities_PK;
DROP INDEX IF EXISTS users_PK;
DROP INDEX IF EXISTS refresh_tokens_PK;
DROP INDEX IF EXISTS issued_FK;

/*==============================================================*/
/* Table: authorities                                           */
/*==============================================================*/
create table authorities (
   authority_id         SMALLSERIAL               not null,
   authority            VARCHAR(255)         not null unique,
   constraint PK_AUTHORITIES primary key (authority_id)
);


/*==============================================================*/
/* Table: refresh_tokens                                        */
/*==============================================================*/
create table refresh_tokens (
   refresh_token_id     SERIAL               not null,
   user_id              BIGINT                not null unique,
   refresh_token        CHAR(36)             not null unique,
   expiration_date      TIMESTAMP WITHOUT TIME ZONE                 not null,
   constraint PK_REFRESH_TOKENS primary key (refresh_token_id)
);

/*==============================================================*/
/* Index: refresh_tokens_PK                                     */
/*==============================================================*/
create unique index refresh_tokens_PK on refresh_tokens (
refresh_token_id
);

/*==============================================================*/
/* Index: issued_FK                                             */
/*==============================================================*/
create  index issued_FK on refresh_tokens (
user_id
);

/*==============================================================*/
/* Table: user_authorities                                        */
/*==============================================================*/
create table user_authorities (
   authority_id         SMALLINT                 not null,
   user_id              BIGINT                 not null,
   constraint PK_user_authorities primary key (authority_id, user_id)
);

/*==============================================================*/
/* Index: granted_FK                                            */
/*==============================================================*/
create  index granted_FK on user_authorities (
user_id
);

/*==============================================================*/
/* Index: used_FK                                               */
/*==============================================================*/
create  index used_FK on user_authorities (
authority_id
);

/*==============================================================*/
/* Table: users                                                 */
/*==============================================================*/
create table users (
   user_id              BIGSERIAL               not null,
   username             VARCHAR(255)         not null unique,
   password             CHAR(60)             not null,
   constraint PK_USERS primary key (user_id)
);

/*==============================================================*/
/* Index: users_PK                                              */
/*==============================================================*/
create unique index users_PK on users (
user_id
);


alter table refresh_tokens
   add constraint FK_REFRESH__RELATIONS_USERS foreign key (user_id)
      references users (user_id)
      on delete cascade on update restrict;


alter table user_authorities
   add constraint FK_USER_AUT_RELATIONS_AUTHORIT foreign key (authority_id)
      references authorities (authority_id)
      on delete restrict on update restrict;


alter table user_authorities
   add constraint FK_USER_AUT_RELATIONS_USERS foreign key (user_id)
      references users (user_id)
      on delete restrict on update restrict;