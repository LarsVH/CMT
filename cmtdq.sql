-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: cmt
-- ------------------------------------------------------
-- Server version	5.7.11-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `action`
--

DROP TABLE IF EXISTS `action`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action` (
  `idaction` int(11) NOT NULL,
  `action_name` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`idaction`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `action`
--

LOCK TABLES `action` WRITE;
/*!40000 ALTER TABLE `action` DISABLE KEYS */;
/*!40000 ALTER TABLE `action` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `action_actionfields`
--

DROP TABLE IF EXISTS `action_actionfields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `action_actionfields` (
  `idaction` int(11) NOT NULL,
  `idfield` int(11) NOT NULL,
  PRIMARY KEY (`idaction`,`idfield`),
  KEY `field_id_fields_idx` (`idfield`),
  CONSTRAINT `action_id_fields` FOREIGN KEY (`idaction`) REFERENCES `action` (`idaction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_fields` FOREIGN KEY (`idfield`) REFERENCES `actionfield` (`idactionfield`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `action_actionfields`
--

LOCK TABLES `action_actionfields` WRITE;
/*!40000 ALTER TABLE `action_actionfields` DISABLE KEYS */;
/*!40000 ALTER TABLE `action_actionfields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `actionfield`
--

DROP TABLE IF EXISTS `actionfield`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actionfield` (
  `idactionfield` int(11) NOT NULL,
  `actionfield_name` varchar(65) DEFAULT NULL,
  `actionfield_format` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`idactionfield`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actionfield`
--

LOCK TABLES `actionfield` WRITE;
/*!40000 ALTER TABLE `actionfield` DISABLE KEYS */;
/*!40000 ALTER TABLE `actionfield` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `actionfield_options`
--

DROP TABLE IF EXISTS `actionfield_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actionfield_options` (
  `idactionfield` int(11) NOT NULL,
  `option` varchar(65) NOT NULL,
  PRIMARY KEY (`idactionfield`,`option`),
  CONSTRAINT `actionfield_id_options` FOREIGN KEY (`idactionfield`) REFERENCES `actionfield` (`idactionfield`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actionfield_options`
--

LOCK TABLES `actionfield_options` WRITE;
/*!40000 ALTER TABLE `actionfield_options` DISABLE KEYS */;
/*!40000 ALTER TABLE `actionfield_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categories`
--

DROP TABLE IF EXISTS `categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categories` (
  `categoryName` varchar(65) NOT NULL,
  PRIMARY KEY (`categoryName`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categories`
--

LOCK TABLES `categories` WRITE;
/*!40000 ALTER TABLE `categories` DISABLE KEYS */;
INSERT INTO `categories` VALUES ('Code'),('My Situations');
/*!40000 ALTER TABLE `categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_limits_format`
--

DROP TABLE IF EXISTS `event_limits_format`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_limits_format` (
  `idevent_limits_format` int(11) NOT NULL AUTO_INCREMENT,
  `facttype_name` varchar(65) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `format` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`idevent_limits_format`),
  KEY `facttype_idx` (`facttype_name`),
  KEY `field_idx` (`field_id`),
  CONSTRAINT `facttype_name` FOREIGN KEY (`facttype_name`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_limit` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_limits_format`
--

LOCK TABLES `event_limits_format` WRITE;
/*!40000 ALTER TABLE `event_limits_format` DISABLE KEYS */;
INSERT INTO `event_limits_format` VALUES (1,'ItIsAfter',15,'00:00'),(2,'CisaMeeting',16,'00:00');
/*!40000 ALTER TABLE `event_limits_format` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_limits_list`
--

DROP TABLE IF EXISTS `event_limits_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_limits_list` (
  `idevent_limits_list` int(11) NOT NULL AUTO_INCREMENT,
  `facttype_name` varchar(65) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`idevent_limits_list`),
  KEY `facttypeName_idx` (`facttype_name`),
  KEY `fieldId_idx` (`field_id`),
  CONSTRAINT `facttypeName1` FOREIGN KEY (`facttype_name`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fieldId1` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_limits_list`
--

LOCK TABLES `event_limits_list` WRITE;
/*!40000 ALTER TABLE `event_limits_list` DISABLE KEYS */;
INSERT INTO `event_limits_list` VALUES (1,'CisaMeeting',17);
/*!40000 ALTER TABLE `event_limits_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_limits_list_options`
--

DROP TABLE IF EXISTS `event_limits_list_options`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_limits_list_options` (
  `id_event_list` int(11) NOT NULL,
  `option_list` varchar(65) NOT NULL,
  PRIMARY KEY (`id_event_list`,`option_list`),
  CONSTRAINT `forkey` FOREIGN KEY (`id_event_list`) REFERENCES `event_limits_list` (`idevent_limits_list`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_limits_list_options`
--

LOCK TABLES `event_limits_list_options` WRITE;
/*!40000 ALTER TABLE `event_limits_list_options` DISABLE KEYS */;
INSERT INTO `event_limits_list_options` VALUES (1,'today'),(1,'tomorrow'),(1,'yesterday');
/*!40000 ALTER TABLE `event_limits_list_options` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fact_field_values`
--

DROP TABLE IF EXISTS `fact_field_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fact_field_values` (
  `factId` int(11) NOT NULL,
  `fieldId` int(11) NOT NULL,
  `value` int(11) DEFAULT NULL,
  PRIMARY KEY (`factId`,`fieldId`),
  KEY `fieldId_fields_idx` (`fieldId`),
  KEY `value_factId_idx` (`value`),
  CONSTRAINT `factId_fact` FOREIGN KEY (`factId`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fieldId_fields` FOREIGN KEY (`fieldId`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `value_factId` FOREIGN KEY (`value`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fact_field_values`
--

LOCK TABLES `fact_field_values` WRITE;
/*!40000 ALTER TABLE `fact_field_values` DISABLE KEYS */;
INSERT INTO `fact_field_values` VALUES (15,13,14);
/*!40000 ALTER TABLE `fact_field_values` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fact_field_values_string`
--

DROP TABLE IF EXISTS `fact_field_values_string`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fact_field_values_string` (
  `fact_id` int(11) NOT NULL,
  `field_id` int(11) NOT NULL,
  `value` text,
  PRIMARY KEY (`fact_id`,`field_id`),
  KEY `field_id_val_string_idx` (`field_id`),
  CONSTRAINT `fact_id_val_string` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_val_string` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fact_field_values_string`
--

LOCK TABLES `fact_field_values_string` WRITE;
/*!40000 ALTER TABLE `fact_field_values_string` DISABLE KEYS */;
INSERT INTO `fact_field_values_string` VALUES (14,11,'Living'),(15,12,'Sandra');
/*!40000 ALTER TABLE `fact_field_values_string` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facts`
--

DROP TABLE IF EXISTS `facts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facts` (
  `idfacts` int(11) NOT NULL AUTO_INCREMENT,
  `facttype` varchar(65) NOT NULL,
  PRIMARY KEY (`idfacts`),
  KEY `facttype_idx` (`facttype`),
  CONSTRAINT `facttype_fact` FOREIGN KEY (`facttype`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facts`
--

LOCK TABLES `facts` WRITE;
/*!40000 ALTER TABLE `facts` DISABLE KEYS */;
INSERT INTO `facts` VALUES (14,'Location'),(15,'Person');
/*!40000 ALTER TABLE `facts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facttype`
--

DROP TABLE IF EXISTS `facttype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facttype` (
  `facttypeName` varchar(65) NOT NULL,
  `facttypeCategory` varchar(65) DEFAULT 'Default',
  `isCustom` tinyint(1) NOT NULL DEFAULT '0',
  `facttypeType` enum('fact','activity','code') DEFAULT 'fact',
  `urifield` int(11) DEFAULT NULL,
  PRIMARY KEY (`facttypeName`),
  KEY `category_idx` (`facttypeCategory`),
  KEY `urifield_idx` (`urifield`),
  CONSTRAINT `category` FOREIGN KEY (`facttypeCategory`) REFERENCES `categories` (`categoryName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `urifield` FOREIGN KEY (`urifield`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facttype`
--

LOCK TABLES `facttype` WRITE;
/*!40000 ALTER TABLE `facttype` DISABLE KEYS */;
INSERT INTO `facttype` VALUES ('CisaMeeting','Code',1,'activity',NULL),('ItIsAfter','Code',0,'activity',NULL),('java.lang.String','Code',0,'fact',10),('Location','Code',0,'fact',11),('Person','Code',0,'fact',12),('Rule5','My Situations',1,'activity',NULL),('Rule6','My Situations',1,'activity',NULL);
/*!40000 ALTER TABLE `facttype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facttype_fields`
--

DROP TABLE IF EXISTS `facttype_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `facttype_fields` (
  `facttype` varchar(65) NOT NULL,
  `idfield` int(11) NOT NULL,
  PRIMARY KEY (`facttype`,`idfield`),
  KEY `fieldid_idx` (`idfield`),
  CONSTRAINT `facttype` FOREIGN KEY (`facttype`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fieldid` FOREIGN KEY (`idfield`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facttype_fields`
--

LOCK TABLES `facttype_fields` WRITE;
/*!40000 ALTER TABLE `facttype_fields` DISABLE KEYS */;
INSERT INTO `facttype_fields` VALUES ('java.lang.String',10),('Location',11),('Person',12),('Person',13),('ItIsAfter',15),('CisaMeeting',16),('CisaMeeting',17),('CisaMeeting',18),('Rule6',19);
/*!40000 ALTER TABLE `facttype_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fields`
--

DROP TABLE IF EXISTS `fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `fields` (
  `idfields` int(11) NOT NULL AUTO_INCREMENT,
  `fieldName` varchar(65) NOT NULL,
  `fieldType` varchar(65) NOT NULL,
  `isVar` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idfields`),
  KEY `fieldFacttype_idx` (`fieldType`),
  CONSTRAINT `fieldFacttype` FOREIGN KEY (`fieldType`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fields`
--

LOCK TABLES `fields` WRITE;
/*!40000 ALTER TABLE `fields` DISABLE KEYS */;
INSERT INTO `fields` VALUES (10,'value','java.lang.String',0),(11,'room','java.lang.String',0),(12,'name','java.lang.String',0),(13,'loc','Location',0),(14,'hour','java.lang.String',1),(15,'hour','java.lang.String',1),(16,'day','java.lang.String',1),(17,'day1','java.lang.String',1),(18,'day2','java.lang.String',0),(19,'day11','java.lang.String',0);
/*!40000 ALTER TABLE `fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function`
--

DROP TABLE IF EXISTS `function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `function` (
  `idfunction` int(11) NOT NULL AUTO_INCREMENT,
  `function_name` varchar(65) DEFAULT NULL,
  `encap_class` text,
  PRIMARY KEY (`idfunction`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function`
--

LOCK TABLES `function` WRITE;
/*!40000 ALTER TABLE `function` DISABLE KEYS */;
INSERT INTO `function` VALUES (7,'InBed','Func2'),(8,'noMovement','Func2'),(9,'personsInLocation','Func');
/*!40000 ALTER TABLE `function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `function_parameters`
--

DROP TABLE IF EXISTS `function_parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `function_parameters` (
  `function_id` int(11) NOT NULL,
  `parameter_id` int(11) NOT NULL,
  PRIMARY KEY (`function_id`,`parameter_id`),
  KEY `par_id_idx` (`parameter_id`),
  CONSTRAINT `func_id` FOREIGN KEY (`function_id`) REFERENCES `function` (`idfunction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `par_id` FOREIGN KEY (`parameter_id`) REFERENCES `parameters` (`idfunction_parameters`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `function_parameters`
--

LOCK TABLES `function_parameters` WRITE;
/*!40000 ALTER TABLE `function_parameters` DISABLE KEYS */;
INSERT INTO `function_parameters` VALUES (7,1),(8,2),(9,3),(9,4);
/*!40000 ALTER TABLE `function_parameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifblock_event`
--

DROP TABLE IF EXISTS `ifblock_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifblock_event` (
  `idifblock_event` int(11) NOT NULL AUTO_INCREMENT,
  `template_position` int(11) NOT NULL,
  `facttype_event` varchar(65) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `index_input` int(11) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  `operator` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idifblock_event`),
  KEY `facttype_event_idx` (`facttype_event`),
  KEY `field_id_event_idx` (`field_id`),
  KEY `temp_id_event_idx` (`template_id`),
  CONSTRAINT `facttype_event` FOREIGN KEY (`facttype_event`) REFERENCES `facttype` (`facttypeName`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `field_id_event` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `temp_id_event` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifblock_event`
--

LOCK TABLES `ifblock_event` WRITE;
/*!40000 ALTER TABLE `ifblock_event` DISABLE KEYS */;
INSERT INTO `ifblock_event` VALUES (2,0,'CisaMeeting',NULL,0,2,''),(3,0,'CisaMeeting',NULL,0,3,''),(4,0,'CisaMeeting',NULL,0,5,'');
/*!40000 ALTER TABLE `ifblock_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifblock_function`
--

DROP TABLE IF EXISTS `ifblock_function`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifblock_function` (
  `idifblock_function` int(11) NOT NULL AUTO_INCREMENT,
  `template_position` int(11) DEFAULT NULL,
  `function_id` int(11) DEFAULT NULL,
  `template_id` int(11) DEFAULT NULL,
  `operator` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`idifblock_function`),
  KEY `func_id_ifbl_idx` (`function_id`),
  KEY `temp_idx` (`template_id`),
  CONSTRAINT `func_id_ifbl` FOREIGN KEY (`function_id`) REFERENCES `function` (`idfunction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `temp` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifblock_function`
--

LOCK TABLES `ifblock_function` WRITE;
/*!40000 ALTER TABLE `ifblock_function` DISABLE KEYS */;
INSERT INTO `ifblock_function` VALUES (1,1,8,3,'AND'),(2,2,7,3,'AND'),(3,0,8,4,''),(4,1,8,5,'AND');
/*!40000 ALTER TABLE `ifblock_function` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifblock_function_filledin_fact`
--

DROP TABLE IF EXISTS `ifblock_function_filledin_fact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifblock_function_filledin_fact` (
  `ifblock_id` int(11) NOT NULL,
  `parameter_id` int(11) NOT NULL,
  `fact_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `index_input` int(11) DEFAULT NULL,
  PRIMARY KEY (`ifblock_id`,`parameter_id`),
  KEY `fact_id_filledin_fact_idx` (`fact_id`),
  KEY `field_id_filledin_fact_idx` (`field_id`),
  KEY `par_id_fact_idx` (`parameter_id`),
  CONSTRAINT `fact_id_filledin_fact` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_filledin_fact` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_filled_fact` FOREIGN KEY (`ifblock_id`) REFERENCES `ifblock_function` (`idifblock_function`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `par_id_fact` FOREIGN KEY (`parameter_id`) REFERENCES `parameters` (`idfunction_parameters`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifblock_function_filledin_fact`
--

LOCK TABLES `ifblock_function_filledin_fact` WRITE;
/*!40000 ALTER TABLE `ifblock_function_filledin_fact` DISABLE KEYS */;
INSERT INTO `ifblock_function_filledin_fact` VALUES (1,2,14,NULL,2),(4,2,14,NULL,2);
/*!40000 ALTER TABLE `ifblock_function_filledin_fact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifblock_function_filledin_facttype`
--

DROP TABLE IF EXISTS `ifblock_function_filledin_facttype`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifblock_function_filledin_facttype` (
  `ifblock_id` int(11) NOT NULL,
  `parameter_id` int(11) NOT NULL,
  `facttype_id` varchar(65) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `index_input` int(11) DEFAULT NULL,
  PRIMARY KEY (`ifblock_id`,`parameter_id`),
  KEY `facttype_id_filledIn_idx` (`facttype_id`),
  KEY `field_id_filledin_idx` (`field_id`),
  KEY `par_id_filledin_idx` (`parameter_id`),
  CONSTRAINT `facttype_id_filledin` FOREIGN KEY (`facttype_id`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_filledin` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_filled_facttype` FOREIGN KEY (`ifblock_id`) REFERENCES `ifblock_function` (`idifblock_function`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `par_id_filledin` FOREIGN KEY (`parameter_id`) REFERENCES `parameters` (`idfunction_parameters`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='keeps pointer to the facttype declared in a template. facttype can also refer to a event facttype used in another ifblock. In this case go via that ifblock to query the limits if there are any.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifblock_function_filledin_facttype`
--

LOCK TABLES `ifblock_function_filledin_facttype` WRITE;
/*!40000 ALTER TABLE `ifblock_function_filledin_facttype` DISABLE KEYS */;
INSERT INTO `ifblock_function_filledin_facttype` VALUES (2,1,'Location',NULL,4),(3,2,'Location',NULL,0);
/*!40000 ALTER TABLE `ifblock_function_filledin_facttype` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `limits_template_ifblock_event`
--

DROP TABLE IF EXISTS `limits_template_ifblock_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `limits_template_ifblock_event` (
  `idlimits_template_ifblock_event` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) DEFAULT NULL,
  `ifblock_event_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `operator` varchar(65) DEFAULT NULL,
  `value` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`idlimits_template_ifblock_event`),
  KEY `field_id_idx` (`field_id`),
  KEY `temp_id_idx` (`template_id`),
  KEY `ifblock_ev_lim_idx` (`ifblock_event_id`),
  CONSTRAINT `field_id` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_ev_lim` FOREIGN KEY (`ifblock_event_id`) REFERENCES `ifblock_event` (`idifblock_event`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `temp_id` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='if you selected limits in the input event fields when making a template. Value is in string eg ''Tomorrow'' of according to format. ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `limits_template_ifblock_event`
--

LOCK TABLES `limits_template_ifblock_event` WRITE;
/*!40000 ALTER TABLE `limits_template_ifblock_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `limits_template_ifblock_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `parameters`
--

DROP TABLE IF EXISTS `parameters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `parameters` (
  `idfunction_parameters` int(11) NOT NULL AUTO_INCREMENT,
  `function_parameter_name` varchar(65) DEFAULT NULL,
  `function_parameter_position` int(11) DEFAULT NULL,
  `function_parameter_type` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`idfunction_parameters`),
  KEY `parameter_type_idx` (`function_parameter_type`),
  CONSTRAINT `parameter_type` FOREIGN KEY (`function_parameter_type`) REFERENCES `facttype` (`facttypeName`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `parameters`
--

LOCK TABLES `parameters` WRITE;
/*!40000 ALTER TABLE `parameters` DISABLE KEYS */;
INSERT INTO `parameters` VALUES (1,'room',0,'Location'),(2,'room',0,'Location'),(3,'person1',0,'Person'),(4,'person2',1,'Person');
/*!40000 ALTER TABLE `parameters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_actions`
--

DROP TABLE IF EXISTS `rule_actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_actions` (
  `rule_id` int(11) NOT NULL,
  `action_id` int(11) NOT NULL,
  PRIMARY KEY (`rule_id`,`action_id`),
  KEY `action_id_actions_idx` (`action_id`),
  CONSTRAINT `action_id_actions` FOREIGN KEY (`action_id`) REFERENCES `action` (`idaction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_act` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_actions`
--

LOCK TABLES `rule_actions` WRITE;
/*!40000 ALTER TABLE `rule_actions` DISABLE KEYS */;
/*!40000 ALTER TABLE `rule_actions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_actions_fieldvalues`
--

DROP TABLE IF EXISTS `rule_actions_fieldvalues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_actions_fieldvalues` (
  `rule_id` int(11) NOT NULL,
  `action_id` int(11) NOT NULL,
  `field_id` int(11) NOT NULL,
  `value` varchar(65) DEFAULT NULL,
  `operator` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`rule_id`,`action_id`,`field_id`),
  KEY `action_id_actions_field_idx` (`action_id`),
  KEY `field_id_actions_field_idx` (`field_id`),
  CONSTRAINT `action_id_actions_field` FOREIGN KEY (`action_id`) REFERENCES `action` (`idaction`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_actions_field` FOREIGN KEY (`field_id`) REFERENCES `actionfield` (`idactionfield`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_field` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_actions_fieldvalues`
--

LOCK TABLES `rule_actions_fieldvalues` WRITE;
/*!40000 ALTER TABLE `rule_actions_fieldvalues` DISABLE KEYS */;
/*!40000 ALTER TABLE `rule_actions_fieldvalues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_limits_ifblock_event`
--

DROP TABLE IF EXISTS `rule_limits_ifblock_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_limits_ifblock_event` (
  `idlimits_rule_ifblock_event` int(11) NOT NULL AUTO_INCREMENT,
  `rule_id` int(11) DEFAULT NULL,
  `ifblock_event_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `operator` varchar(65) DEFAULT NULL,
  `value` varchar(65) DEFAULT NULL,
  `index_input` int(11) DEFAULT NULL,
  PRIMARY KEY (`idlimits_rule_ifblock_event`),
  KEY `field_idx` (`field_id`),
  KEY `ifblock_ev_idx` (`ifblock_event_id`),
  KEY `rule_id_ifbl_idx` (`rule_id`),
  CONSTRAINT `field` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_ev` FOREIGN KEY (`ifblock_event_id`) REFERENCES `ifblock_event` (`idifblock_event`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_ifbl` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_limits_ifblock_event`
--

LOCK TABLES `rule_limits_ifblock_event` WRITE;
/*!40000 ALTER TABLE `rule_limits_ifblock_event` DISABLE KEYS */;
INSERT INTO `rule_limits_ifblock_event` VALUES (1,6,4,17,'==','tomorrow',0),(2,6,4,16,'!=','21:00',0),(3,6,4,18,'','',0);
/*!40000 ALTER TABLE `rule_limits_ifblock_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_param_filledin_event`
--

DROP TABLE IF EXISTS `rule_param_filledin_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_param_filledin_event` (
  `rule_id` int(11) NOT NULL,
  `param_id` int(11) NOT NULL,
  `facttype_id` varchar(65) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `ifblock_id` int(11) NOT NULL,
  PRIMARY KEY (`rule_id`,`param_id`,`ifblock_id`),
  KEY `facttype_id_filledinrule_event_idx` (`facttype_id`),
  KEY `field_id_filledinrule_event_idx` (`field_id`),
  KEY `par_id_event_idx` (`param_id`),
  KEY `ifblock_rule_event_idx` (`ifblock_id`),
  CONSTRAINT `facttype_id_filledinrule_event` FOREIGN KEY (`facttype_id`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_filledinrule_event` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_rule_event` FOREIGN KEY (`ifblock_id`) REFERENCES `ifblock_function` (`idifblock_function`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `par_id_event` FOREIGN KEY (`param_id`) REFERENCES `parameters` (`idfunction_parameters`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_event` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_param_filledin_event`
--

LOCK TABLES `rule_param_filledin_event` WRITE;
/*!40000 ALTER TABLE `rule_param_filledin_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `rule_param_filledin_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_param_filledin_fact`
--

DROP TABLE IF EXISTS `rule_param_filledin_fact`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_param_filledin_fact` (
  `rule_id` int(11) NOT NULL,
  `ifblock_id` int(11) NOT NULL,
  `param_id` int(11) NOT NULL,
  `fact_id` int(11) NOT NULL,
  `field_id` int(11) DEFAULT NULL,
  `index_input` int(11) DEFAULT NULL,
  PRIMARY KEY (`rule_id`,`ifblock_id`,`param_id`),
  KEY `fact_id_filledin_fact_idx` (`fact_id`),
  KEY `field_id_filledin_fact_idx` (`field_id`),
  KEY `par_id_rule_fact_idx` (`param_id`),
  KEY `ifblock_rule_fact_idx` (`ifblock_id`),
  CONSTRAINT `fact_id_filledinrule_fact` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_filledinrule_fact` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ifblock_rule_fact` FOREIGN KEY (`ifblock_id`) REFERENCES `ifblock_function` (`idifblock_function`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `par_id_rule_fact` FOREIGN KEY (`param_id`) REFERENCES `parameters` (`idfunction_parameters`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_fact` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='if query is null then param points to an event input which hasd to be declared in another ifblock. If field id is null then it means that the whole fact is used as parameter in function X.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_param_filledin_fact`
--

LOCK TABLES `rule_param_filledin_fact` WRITE;
/*!40000 ALTER TABLE `rule_param_filledin_fact` DISABLE KEYS */;
INSERT INTO `rule_param_filledin_fact` VALUES (2,3,2,14,NULL,0),(3,3,2,14,NULL,0),(4,3,2,14,NULL,0),(5,3,2,14,NULL,0),(6,4,2,14,NULL,2);
/*!40000 ALTER TABLE `rule_param_filledin_fact` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_situation`
--

DROP TABLE IF EXISTS `rule_situation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_situation` (
  `rule_id` int(11) NOT NULL,
  `facttype_id` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`rule_id`),
  KEY `facttype_id_situ_rule_idx` (`facttype_id`),
  CONSTRAINT `facttype_id_situ_rule` FOREIGN KEY (`facttype_id`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `rule_id_situ` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='when this rule true then the new situation will be fired';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_situation`
--

LOCK TABLES `rule_situation` WRITE;
/*!40000 ALTER TABLE `rule_situation` DISABLE KEYS */;
INSERT INTO `rule_situation` VALUES (5,'rule5'),(6,'rule6');
/*!40000 ALTER TABLE `rule_situation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rule_situation_fields`
--

DROP TABLE IF EXISTS `rule_situation_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rule_situation_fields` (
  `rule_id` int(11) NOT NULL,
  `situ_field_id` int(11) NOT NULL,
  PRIMARY KEY (`rule_id`,`situ_field_id`),
  KEY `situ_field_data_idx` (`situ_field_id`),
  CONSTRAINT `rule_id_field_data` FOREIGN KEY (`rule_id`) REFERENCES `rules` (`idrules`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `situ_field_data` FOREIGN KEY (`situ_field_id`) REFERENCES `template_situation_output_fields` (`id_temp_situ_output_fields`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rule_situation_fields`
--

LOCK TABLES `rule_situation_fields` WRITE;
/*!40000 ALTER TABLE `rule_situation_fields` DISABLE KEYS */;
/*!40000 ALTER TABLE `rule_situation_fields` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `rules`
--

DROP TABLE IF EXISTS `rules`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `rules` (
  `idrules` int(11) NOT NULL AUTO_INCREMENT,
  `template_id` int(11) DEFAULT NULL,
  `rule_name` varchar(65) DEFAULT NULL,
  `rule_drl` text,
  PRIMARY KEY (`idrules`),
  KEY `temp_id_rule_idx` (`template_id`),
  CONSTRAINT `temp_id_rule` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `rules`
--

LOCK TABLES `rules` WRITE;
/*!40000 ALTER TABLE `rules` DISABLE KEYS */;
INSERT INTO `rules` VALUES (2,4,'ruleRule2','import function be.ac.vub.wise.cmtserver.functions.Func2.noMovement\nimport be.ac.vub.wise.cmtserver.events.Rule2\nimport be.ac.vub.wise.cmtserver.facts.Location\nrule \"ruleRule2\" \n  when \n $0:Location(room== \"Living\" ) \n eval(noMovement($0)) \n\n then \ninsert( new Rule2()); \n end \n '),(3,4,'rulerule3','import function be.ac.vub.wise.cmtserver.functions.Func2.noMovement\nimport be.ac.vub.wise.cmtserver.events.Rule3\nimport be.ac.vub.wise.cmtserver.facts.Location\nrule \"rulerule3\" \n  when \n $0:Location(room== \"Living\" ) \n eval(noMovement($0)) \n\n then \ninsert( new Rule3()); \n end \n '),(4,4,'rulerule4','import be.ac.vub.wise.cmtserver.events.Rule4\nimport function be.ac.vub.wise.cmtserver.functions.Func2.noMovement\nimport be.ac.vub.wise.cmtserver.facts.Location\nrule \"rulerule4\" \n  when \n $0:Location(room== \"Living\" ) \n eval(noMovement($0)) \n\n then \ninsert( new Rule4()); \n end \n '),(5,4,'rulerule5','import function be.ac.vub.wise.cmtserver.functions.Func2.noMovement\nimport be.ac.vub.wise.cmtserver.events.Rule5\nimport be.ac.vub.wise.cmtserver.facts.Location\nrule \"rulerule5\" \n  when \n $0:Location(room== \"Living\" ) \n eval(noMovement($0)) \n\n then \ninsert( new Rule5()); \n end \n '),(6,5,'rulerule6','import function be.ac.vub.wise.cmtserver.functions.Func2.noMovement\nimport be.ac.vub.wise.cmtserver.events.CisaMeeting\nimport be.ac.vub.wise.cmtserver.events.Rule6\nimport be.ac.vub.wise.cmtserver.facts.Location\nrule \"rulerule6\" \n  when \n $0:CisaMeeting( $00:day1==\"tomorrow\", $01:day!=\"21:00\", $02:day2) \n$2:Location(room== \"Living\" ) \n eval(noMovement($2)) \n\n then \nRule6 rule6Rule6= new Rule6(); \n rule6Rule6.setDay11( $0.day1); \ninsert(rule6Rule6); \n end \n');
/*!40000 ALTER TABLE `rules` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template`
--

DROP TABLE IF EXISTS `template`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template` (
  `idtemplate` int(11) NOT NULL AUTO_INCREMENT,
  `template_name` varchar(65) DEFAULT NULL,
  `template_category` varchar(65) DEFAULT NULL,
  `isSituationTemplate` tinyint(1) DEFAULT '0',
  PRIMARY KEY (`idtemplate`),
  KEY `temp_category_idx` (`template_category`),
  CONSTRAINT `temp_category` FOREIGN KEY (`template_category`) REFERENCES `template_categories` (`idtemplate_categories`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8 COMMENT='				';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template`
--

LOCK TABLES `template` WRITE;
/*!40000 ALTER TABLE `template` DISABLE KEYS */;
INSERT INTO `template` VALUES (2,'Temp','Default',1),(3,'Temp2','Default',1),(4,'TemLoc','Default',1),(5,'Temp3','Default',1);
/*!40000 ALTER TABLE `template` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_categories`
--

DROP TABLE IF EXISTS `template_categories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template_categories` (
  `idtemplate_categories` varchar(65) NOT NULL,
  PRIMARY KEY (`idtemplate_categories`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template_categories`
--

LOCK TABLES `template_categories` WRITE;
/*!40000 ALTER TABLE `template_categories` DISABLE KEYS */;
INSERT INTO `template_categories` VALUES ('Default');
/*!40000 ALTER TABLE `template_categories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_situation_output`
--

DROP TABLE IF EXISTS `template_situation_output`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template_situation_output` (
  `template_id` int(11) NOT NULL,
  `situ_name` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`template_id`),
  CONSTRAINT `temp_id_situ` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='output becomes situation after rule is made!! So here we just keep data to compile new situation.';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template_situation_output`
--

LOCK TABLES `template_situation_output` WRITE;
/*!40000 ALTER TABLE `template_situation_output` DISABLE KEYS */;
INSERT INTO `template_situation_output` VALUES (2,'Ok'),(3,'Enter activity name'),(4,'Enter activity name'),(5,'Enter activity name');
/*!40000 ALTER TABLE `template_situation_output` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `template_situation_output_fields`
--

DROP TABLE IF EXISTS `template_situation_output_fields`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `template_situation_output_fields` (
  `template_id` int(11) DEFAULT NULL,
  `field_id` int(11) DEFAULT NULL,
  `output_field_label` varchar(65) DEFAULT NULL,
  `facttype_name` varchar(65) DEFAULT NULL,
  `fact_id` int(11) DEFAULT NULL,
  `id_temp_situ_output_fields` int(11) NOT NULL AUTO_INCREMENT,
  `index_input` int(11) DEFAULT NULL,
  `facttypeField` varchar(65) DEFAULT NULL,
  PRIMARY KEY (`id_temp_situ_output_fields`),
  KEY `field_id_situ_field_idx` (`field_id`),
  KEY `facttype_situ_field_idx` (`facttype_name`),
  KEY `fact_id_situ_field_idx` (`fact_id`),
  KEY `temp_id_situ_field_idx` (`template_id`),
  KEY `ftn_idx` (`facttypeField`),
  CONSTRAINT `fact_id_situ_field` FOREIGN KEY (`fact_id`) REFERENCES `facts` (`idfacts`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `facttype_situ_field` FOREIGN KEY (`facttype_name`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `field_id_situ_field` FOREIGN KEY (`field_id`) REFERENCES `fields` (`idfields`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ftn` FOREIGN KEY (`facttypeField`) REFERENCES `facttype` (`facttypeName`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `temp_id_situ_field` FOREIGN KEY (`template_id`) REFERENCES `template` (`idtemplate`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `template_situation_output_fields`
--

LOCK TABLES `template_situation_output_fields` WRITE;
/*!40000 ALTER TABLE `template_situation_output_fields` DISABLE KEYS */;
INSERT INTO `template_situation_output_fields` VALUES (2,18,'day21','CisaMeeting',NULL,2,0,'java.lang.String'),(3,17,'day11','CisaMeeting',NULL,3,0,'java.lang.String'),(5,17,'day11','CisaMeeting',NULL,4,0,'java.lang.String');
/*!40000 ALTER TABLE `template_situation_output_fields` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-05-21 20:47:50
