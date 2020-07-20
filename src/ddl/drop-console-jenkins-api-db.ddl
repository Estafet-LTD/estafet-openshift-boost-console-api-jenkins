alter table MICROSERVICE drop constraint MICROSERVICE_TO_ENV_FK;
drop table if exists ENV cascade;
drop table if exists MICROSERVICE cascade;
drop sequence MICROSERVICE_ID_SEQ;
