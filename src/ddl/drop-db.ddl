alter table ENV drop constraint ENV_TO_PRODUCT_FK;
alter table MICROSERVICE drop constraint MICROSERVICE_TO_ENV_FK;
drop table if exists ENV cascade;
drop table if exists MICROSERVICE cascade;
drop table if exists Product cascade;
drop sequence ENV_ID_SEQ;
drop sequence MICROSERVICE_ID_SEQ;
