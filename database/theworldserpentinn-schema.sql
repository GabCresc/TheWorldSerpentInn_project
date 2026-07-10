-- MySQL dump 10.13  Distrib 8.0.45, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: theserpentinndatabase
-- ------------------------------------------------------
-- Server version	8.0.45

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `campaign`
--

DROP TABLE IF EXISTS `campaign`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaign` (
  `campaignID` int NOT NULL AUTO_INCREMENT,
  `name` varchar(45) NOT NULL,
  `max_players` int NOT NULL,
  `userID` int NOT NULL,
  `time_session` time NOT NULL,
  `mode` varchar(45) NOT NULL,
  `frequency` varchar(45) NOT NULL,
  `city` varchar(45) DEFAULT NULL,
  `start_date` datetime DEFAULT NULL,
  `platform` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`campaignID`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign`
--

LOCK TABLES `campaign` WRITE;
/*!40000 ALTER TABLE `campaign` DISABLE KEYS */;
INSERT INTO `campaign` VALUES (1,'La luce dell\'alba',9,1,'17:00:00','ONLINE','ogni mese',NULL,'2023-11-30 21:00:00','Discord'),(2,'X marks the spot',7,2,'16:00:00','ONLINE','bisettiminale',NULL,'2024-08-22 12:00:00','Discord'),(3,'The Winters Crest',8,3,'21:00:00','OFFLINE','ogni settimana','Pisa','2026-04-30 11:00:00',NULL),(4,'The tournament',9,4,'16:00:00','ONLINE','ogni mese',NULL,'2026-04-30 11:00:00','Roll20'),(5,'Lambent delirium',10,5,'17:00:00','OFFLINE','ogni mese','Latina','2026-04-30 11:00:00',NULL),(6,'House of ghosts',5,6,'22:00:00','OFFLINE','bisetteminale','Roma','2026-10-25 22:00:00',NULL),(7,'The Delian Tomb',4,7,'10:00:00','ONLINE','ogni settimana',NULL,'2026-11-17 10:00:00','Discord'),(8,'The big hunt',6,8,'18:00:00','ONLINE','ogni settimana',NULL,'2026-09-20 16:00:00','Owlbear'),(9,'Eye of a titan',5,9,'17:00:00','OFFLINE','ogni mese','Roma','2026-08-27 15:00:00',NULL),(10,'Monsters sea',3,10,'22:00:00','OFFLINE','ogni mese','Rocca Priora','2026-12-03 18:00:00',NULL),(11,'Marks',4,1,'10:00:00','ONLINE','bisettimanale',NULL,'2026-12-27 10:00:00','Discord'),(12,'FantasyWorld',7,1,'16:00:00','OFFLINE','settimanale','Latina','2027-09-17 16:00:00',NULL),(13,'Marque',5,1,'21:00:00','ONLINE','bisettimanale',NULL,'2026-07-10 21:00:00','Roll20'),(14,'test',4,2,'21:14:30','OFFLINE','settimanale','Roma','2026-08-26 14:30:00',NULL),(15,'test',4,2,'21:14:30','OFFLINE','settimanale','Roma','2026-08-26 14:30:00',NULL),(16,'test',4,2,'21:14:30','OFFLINE','settimanale','Roma','2026-08-26 14:30:00',NULL),(17,'Winds of winter',3,1,'12:00:00','OFFLINE','mensile','Frosinone','2026-11-10 12:00:00',NULL),(18,'Petit Londor',5,1,'17:00:00','ONLINE','settimanale',NULL,'2026-10-12 17:00:00','Discord'),(19,'Snowfall',4,1,'21:00:00','OFFLINE','settimanale','Roma','2026-12-18 21:00:00',NULL),(20,'Monsterland',4,1,'18:00:00','ONLINE','mensile',NULL,'2027-10-12 18:00:00','Owlbear');
/*!40000 ALTER TABLE `campaign` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `campaign_request`
--

DROP TABLE IF EXISTS `campaign_request`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `campaign_request` (
  `campaignID` int NOT NULL,
  `playerID` int NOT NULL,
  `status` varchar(45) NOT NULL,
  PRIMARY KEY (`campaignID`,`playerID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `campaign_request`
--

LOCK TABLES `campaign_request` WRITE;
/*!40000 ALTER TABLE `campaign_request` DISABLE KEYS */;
INSERT INTO `campaign_request` VALUES (1,0,'WAITING'),(1,15,'WAITING'),(3,20,'WAITING'),(4,15,'WAITING'),(5,15,'WAITING'),(12,15,'WAITING'),(15,15,'WAITING'),(18,15,'WAITING'),(19,15,'WAITING');
/*!40000 ALTER TABLE `campaign_request` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notification`
--

DROP TABLE IF EXISTS `notification`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notification` (
  `notificationID` int NOT NULL AUTO_INCREMENT,
  `notifiedID` int NOT NULL,
  `notifierID` int NOT NULL,
  `notification_type` varchar(45) NOT NULL,
  `campaignID` int NOT NULL,
  PRIMARY KEY (`notificationID`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notification`
--

LOCK TABLES `notification` WRITE;
/*!40000 ALTER TABLE `notification` DISABLE KEYS */;
INSERT INTO `notification` VALUES (2,1,15,'REQUEST_PARTICIPATION',18);
/*!40000 ALTER TABLE `notification` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_data`
--

DROP TABLE IF EXISTS `user_data`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_data` (
  `userID` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) DEFAULT NULL,
  `password` varchar(45) DEFAULT NULL,
  `user_type` varchar(45) DEFAULT NULL,
  `email` varchar(45) NOT NULL DEFAULT 'EXTERNAL_AUTH',
  PRIMARY KEY (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_data`
--

LOCK TABLES `user_data` WRITE;
/*!40000 ALTER TABLE `user_data` DISABLE KEYS */;
INSERT INTO `user_data` VALUES (1,'Kyor','12345678','DM','kyor.aetheria@gmail.com'),(2,'yarissa','12334ttr','DM','yarissa.n@gmail.com'),(3,'d\'yana','D7H938er','DM','donut@gmail.com'),(4,'Torin','777948fg','DM','torin@gmail.com'),(5,'Amy','Yo98Prf3','DM','amy@gmail.com'),(6,'AnorLondo','Pot5403g','DM','anor@gmail.com'),(7,'Navi','84374981','DM','greatmaster@gmail.com'),(8,'lyon','bul93hfi','DM','bianca@gmail.com'),(9,'molly','59fkke23','DM','simone@gmail.com'),(10,'Rand','kwfknm32','DM','mario@gmail.com'),(11,'mors67','hweigh8','PLAYER','jordan@gmail.com'),(12,'percy','JDSsjif3','PLAYER','anne@gmail.com'),(13,'Lilith','kopksdSJA','PLAYER','robert@gmail.com'),(14,'Jack','vsnaj33','PLAYER','jon@gmail.com'),(15,'Sally','pas12345','PLAYER','sullivan@gmail.com'),(16,'wserpentinn',NULL,'DM','wserpentinn@gmail.com'),(17,'TEST_PLAYER1782486469669',NULL,'PLAYER','1782486469669@example.com'),(18,'TEST_PLAYER_REMOVE_1782486633919',NULL,'PLAYER','test_remove_1782486633919@example.com'),(19,'TEST_PLAYER_REMOVE1782486812525',NULL,'PLAYER','1782486812525@example.com'),(20,'kobe12','123456789','PLAYER','kobe@gmail.com'),(21,'TestUser1782655771294','123456789','DM','1782655771294example.com'),(22,'Nekomaru','neko12345','PLAYER','neko@gmail.com');
/*!40000 ALTER TABLE `user_data` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-07-06  9:53:47
