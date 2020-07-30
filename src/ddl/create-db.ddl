create sequence ENV_ID_SEQ start 1 increment 1;
create sequence MICROSERVICE_ID_SEQ start 1 increment 1;
create table ENV (ENV_ID int8 not null, LIVE boolean not null, ENV_NAME varchar(255) not null, NEXT varchar(255), UPDATED_DATE varchar(255) not null, PRODUCT_ID varchar(255) not null, primary key (ENV_ID));
create table MICROSERVICE (MICROSERVICE_ID int8 not null, MICROSERVICE varchar(255) not null, ENV_ID int8 not null, primary key (MICROSERVICE_ID));
create table Product (PRODUCT_ID varchar(255) not null, primary key (PRODUCT_ID));
alter table MICROSERVICE add constraint MICROSERVICE_KEY unique (ENV_ID, MICROSERVICE);
alter table ENV add constraint ENV_TO_PRODUCT_FK foreign key (PRODUCT_ID) references Product;
alter table MICROSERVICE add constraint MICROSERVICE_TO_ENV_FK foreign key (ENV_ID) references ENV;
