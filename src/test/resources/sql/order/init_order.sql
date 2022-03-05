DROP TABLE IF EXISTS `u_order`;

CREATE TABLE `u_order`
(
    id                 BIGINT   NOT NULL auto_increment,
    status             INT      NULL,
    created_at         datetime NULL,
    payment_expired_at datetime NULL,
    wait_timeout_at    datetime NULL,
    total_price        DOUBLE   NULL,
    CONSTRAINT pk_order PRIMARY KEY (id)
);

DROP TABLE IF EXISTS u_order_payment;
CREATE TABLE u_order_payment
(
    id                 BIGINT       NOT NULL auto_increment,
    order_id           BIGINT       NULL,
    channel            INT          NULL,
    pay_url            VARCHAR(255) NULL,
    created_at         datetime     NULL,
    payment_expired_at datetime     NULL,
    total_price        DOUBLE       NULL,
    CONSTRAINT pk_u_order_payment PRIMARY KEY (id)
);
DROP TABLE IF EXISTS u_order_payment_confirmation;
CREATE TABLE u_order_payment_confirmation
(
    id          BIGINT   NOT NULL auto_increment,
    order_id    BIGINT   NULL,
    confirm_at  datetime NULL,
    channel     INT      NULL,
    total_price DOUBLE   NULL,
    CONSTRAINT pk_u_order_payment_confirmation PRIMARY KEY (id)
);
DROP TABLE IF EXISTS u_payment_task;
CREATE TABLE u_payment_task
(
    id          BIGINT NOT NULL auto_increment,
    order_id    BIGINT NULL,
    status      INT    NULL,
    total_price DOUBLE NULL,
    channel     INT    NULL,
    CONSTRAINT pk_u_payment_task PRIMARY KEY (id)
);
