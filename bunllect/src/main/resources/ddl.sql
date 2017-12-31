CREATE TABLE `bunllect` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` char(15) NOT NULL,
`host` char(15) NOT NULL,
`instance` char(50) NOT NULL,
`ip` char(15) NOT NULL,
`life` int(11) NOT NULL,
`time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
`host_ip` char(21) NULL,
PRIMARY KEY (`id`),
INDEX `idx_time` (`time`, `name`)
)
;

CREATE TABLE `bunllect_online` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`host` char(16) NULL,
`wave` int(10) UNSIGNED NULL,
`online` int(10) UNSIGNED NULL,
`updated` datetime NULL ON UPDATE CURRENT_TIMESTAMP,
PRIMARY KEY (`id`)
)
;

CREATE TABLE `bunllect_total` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`name` char(16) NOT NULL,
`uuid` char(36) NOT NULL,
`latest_ip` char(15) NULL,
`life` bigint(20) NOT NULL,
`latest_quit` datetime NULL,
`latest_join` datetime NULL,
PRIMARY KEY (`id`),
UNIQUE INDEX `name` (`name`) 
)
;

