
/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`chat` /*!40100 DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci */ /*!80016 DEFAULT ENCRYPTION='N' */;

USE `chat`;

/*Table structure for table `group` */

DROP TABLE IF EXISTS `group`;

CREATE TABLE `group` (
  `gid` int NOT NULL AUTO_INCREMENT,
  `gname` varchar(12) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci DEFAULT NULL,
  PRIMARY KEY (`gid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb3;

/*Data for the table `group` */

insert  into `group`(`gid`,`gname`) values 
(2,'聊天灌水群'),
(3,'祖国花朵交流群'),
(4,'java讨论群');

/*Table structure for table `message` */

DROP TABLE IF EXISTS `message`;

CREATE TABLE `message` (
  `mid` int NOT NULL AUTO_INCREMENT,
  `username` varchar(12) DEFAULT NULL,
  `message` varchar(200) DEFAULT NULL,
  `gid` int DEFAULT NULL,
  PRIMARY KEY (`mid`)
) ENGINE=InnoDB AUTO_INCREMENT=269 DEFAULT CHARSET=utf8mb3;

/*Data for the table `message` */

insert  into `message`(`mid`,`username`,`message`,`gid`) values 
(244,'小贵','哈哈哈',2),
(245,'data','呵呵呵额呵呵',2),
(246,'data','花朵',3),
(247,'data','zzzz',4),
(248,'123123','123123',3),
(249,'123123','哈哈哈哈',2),
(250,'data','eeee',2),
(251,'123123','呃呃呃呃',2),
(252,'data','哈哈哈哈',3),
(253,'123123','0.0',3),
(254,'data','嘻嘻嘻嘻嘻嘻嘻',2),
(255,'123123','学java',4),
(258,'111111','sfsfsfsf',4),
(259,'123123','qqqqqq',4),
(260,'222222','呵呵呵呵呵',3),
(261,'data','33333',3),
(262,'123123','1111111111',2),
(263,'111111','学java真的好有意思',2),
(264,'123123','哈哈哈哈',3),
(265,'data','？？？？？？',3),
(266,'123123','111111',3),
(267,'data','22222',3),
(268,'123456','哈哈哈',2);

/*Table structure for table `user` */

DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `username` varchar(12) NOT NULL,
  `password` varchar(12) DEFAULT NULL,
  `qqNum` varchar(12) DEFAULT NULL,
  `isonline` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;

/*Data for the table `user` */

insert  into `user`(`username`,`password`,`qqNum`,`isonline`) values 
('111111','123123','61574564',0),
('123123','123321','2771268198',0),
('123456','123321','5435356',0),
('222222','123123','45224663',0),
('data','321321','4102349',0),
('xiaogui','xiaogui','2771268198',0),
('小贵','123123','491705331',0);

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
