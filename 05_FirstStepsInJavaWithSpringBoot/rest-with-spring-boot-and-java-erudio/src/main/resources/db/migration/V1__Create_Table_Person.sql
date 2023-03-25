
CREATE TABLE `person` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `address` varchar(255) DEFAULT NULL,
  `first_name` varchar(80) NOT NULL,
  `gender` varchar(255) DEFAULT NULL,
  `last_name` varchar(80) NOT NULL,
  PRIMARY KEY (`id`)
);
