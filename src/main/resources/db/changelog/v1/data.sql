--liquibase formatted sql

--changeset senioravanti:1.1
--comment заполняю таблицы users & authorities
--users
INSERT
    INTO users (username, password)
    -- password: 12345
    VALUES ('senioravanti', '$2a$10$bmQia/dSD8TWntyLPqykBe8q23q.SFBJfBb7/ieuEGZXfH30rT0Du')
;
INSERT
    INTO users (username, password)
    -- password: 54321
    VALUES ('luck0koks', '$2a$10$j472rmc53ZewifGhQiyoEeypklq11w9OA.JrLlMZWdL0nmhj6FK.6')
;

--authorities
INSERT
    INTO authorities (authority)
    VALUES ('ROLE_USER')
;
INSERT
    INTO authorities (authority)
    VALUES ('ROLE_ADMIN')
;

--changeset senioravanti:1.2
--comment заполняю таблицу связей user_authorities с помощью вложенного подзапроса
--user_authorities
INSERT
    INTO user_authorities
    SELECT authority_id, user_id
    FROM authorities, users
    WHERE authorities.authority <> 'ROLE_ADMIN' OR users.username <> 'luck0koks'
;