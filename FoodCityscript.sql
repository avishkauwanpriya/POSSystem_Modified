create table customer
(
    customerId      varchar(50) not null
        primary key,
    customerName    varchar(50) not null,
    customerAddress varchar(50) not null
);

create table item
(
    itemCode        varchar(50)    not null
        primary key,
    itemDescription varchar(50)    not null,
    unitPrice       decimal(10, 2) not null,
    qtyOnHand       decimal(10, 2) not null
);

create table `order`
(
    orderId    varchar(50)    not null
        primary key,
    orderDate  date           not null,
    customerId varchar(50)    null,
    total      decimal(10, 2) not null,
    constraint order_fk
        foreign key (customerId) references customer (customerId)
            on update cascade on delete cascade
);

create table orderdetails
(
    orderId  varchar(50)    not null,
    itemCode varchar(50)    not null,
    qty      decimal(10, 2) not null,
    primary key (orderId, itemCode),
    constraint orderDetails_fk
        foreign key (itemCode) references item (itemCode)
            on update cascade on delete cascade
);

create table user
(
    username varchar(50) not null
        primary key,
    password varchar(50) null,
    constraint user_password_uindex
        unique (password)
);


