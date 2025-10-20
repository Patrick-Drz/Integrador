-- MySQL dump 10.13  Distrib 8.0.43, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: deliveryutp
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

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
-- Table structure for table `administradores`
--

DROP TABLE IF EXISTS `administradores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `administradores` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `contrasena` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK7316o5l539qjngk19733jdgxm` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `administradores`
--

LOCK TABLES `administradores` WRITE;
/*!40000 ALTER TABLE `administradores` DISABLE KEYS */;
/*!40000 ALTER TABLE `administradores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carritos`
--

DROP TABLE IF EXISTS `carritos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carritos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `estado` varchar(255) NOT NULL,
  `fecha_actualizacion` datetime(6) DEFAULT NULL,
  `fecha_creacion` datetime(6) DEFAULT NULL,
  `id_usuario` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKho22vgt039p7r4wvto1j38mbe` (`id_usuario`),
  CONSTRAINT `FKho22vgt039p7r4wvto1j38mbe` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carritos`
--

LOCK TABLES `carritos` WRITE;
/*!40000 ALTER TABLE `carritos` DISABLE KEYS */;
INSERT INTO `carritos` VALUES (1,'ACTIVO','2025-07-09 21:18:35.000000','2025-06-07 21:58:19.000000',1),(2,'ACTIVO','2025-06-08 15:31:38.000000','2025-06-08 00:06:09.000000',2),(3,'ACTIVO','2025-07-09 21:51:56.000000','2025-07-09 21:25:11.000000',4),(4,'ACTIVO','2025-07-09 21:54:33.000000','2025-07-09 21:54:29.000000',6),(5,'ACTIVO','2025-10-19 15:09:29.000000','2025-10-19 15:09:29.000000',3),(6,'ACTIVO','2025-10-19 19:10:53.000000','2025-10-19 17:40:35.000000',8),(7,'ACTIVO','2025-10-19 17:42:41.000000','2025-10-19 17:42:41.000000',9);
/*!40000 ALTER TABLE `carritos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalles_orden_venta`
--

DROP TABLE IF EXISTS `detalles_orden_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalles_orden_venta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `precio_unitario_al_momento` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) NOT NULL,
  `id_orden_venta` bigint(20) NOT NULL,
  `id_producto` bigint(20) DEFAULT NULL,
  `id_oferta` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_detalle_orden` (`id_orden_venta`),
  KEY `FK_detalle_producto` (`id_producto`),
  KEY `FK_detalle_oferta` (`id_oferta`),
  CONSTRAINT `FK_detalle_oferta` FOREIGN KEY (`id_oferta`) REFERENCES `ofertas` (`id`),
  CONSTRAINT `FK_detalle_orden` FOREIGN KEY (`id_orden_venta`) REFERENCES `ordenes_venta` (`id`),
  CONSTRAINT `FK_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalles_orden_venta`
--

LOCK TABLES `detalles_orden_venta` WRITE;
/*!40000 ALTER TABLE `detalles_orden_venta` DISABLE KEYS */;
INSERT INTO `detalles_orden_venta` VALUES (1,2,15.00,30.00,1,NULL,1),(2,2,8.00,16.00,1,1,NULL),(3,3,8.00,24.00,2,1,NULL),(4,1,8.00,8.00,3,1,NULL),(5,1,15.00,15.00,3,NULL,1),(6,1,20.00,20.00,4,NULL,2),(7,1,8.00,8.00,4,1,NULL),(8,1,5.00,5.00,4,3,NULL),(9,1,8.00,8.00,5,1,NULL),(10,1,13.00,13.00,5,NULL,1),(11,1,13.00,13.00,6,NULL,1),(12,1,9.00,9.00,7,1,NULL),(13,1,20.00,20.00,7,NULL,2);
/*!40000 ALTER TABLE `detalles_orden_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `items_carrito`
--

DROP TABLE IF EXISTS `items_carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `items_carrito` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `cantidad` int(11) NOT NULL,
  `precio_unitario_al_momento` decimal(10,2) NOT NULL,
  `id_carrito` bigint(20) NOT NULL,
  `id_oferta` bigint(20) DEFAULT NULL,
  `id_producto` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrjklg8mcynueldgl17aq9yx76` (`id_carrito`),
  KEY `FKsppg9c27mr5wj4u423asfo40n` (`id_oferta`),
  KEY `FKohh8pmo6fyfy0jonnx5a3efp1` (`id_producto`),
  CONSTRAINT `FKohh8pmo6fyfy0jonnx5a3efp1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id`),
  CONSTRAINT `FKrjklg8mcynueldgl17aq9yx76` FOREIGN KEY (`id_carrito`) REFERENCES `carritos` (`id`),
  CONSTRAINT `FKsppg9c27mr5wj4u423asfo40n` FOREIGN KEY (`id_oferta`) REFERENCES `ofertas` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=63 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `items_carrito`
--

LOCK TABLES `items_carrito` WRITE;
/*!40000 ALTER TABLE `items_carrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `items_carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ofertas`
--

DROP TABLE IF EXISTS `ofertas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ofertas` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activa` bit(1) NOT NULL,
  `codigo_oferta` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha_fin` date NOT NULL,
  `fecha_inicio` date NOT NULL,
  `nombre_oferta` varchar(255) NOT NULL,
  `precio_oferta` decimal(10,2) NOT NULL,
  `precio_regular` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKd8hl9i43oa2hnwqvtcme20l1g` (`codigo_oferta`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ofertas`
--

LOCK TABLES `ofertas` WRITE;
/*!40000 ALTER TABLE `ofertas` DISABLE KEYS */;
INSERT INTO `ofertas` VALUES (1,_binary '','OROD001','Hamburguesa mas papas fritas y gaseosa','2025-06-10','2025-06-09','Oferta Especial',13.00,20.00),(2,_binary '','OROD002','Hamburguesa mas papas fritas, helado y gaseosa','2025-07-15','2025-07-09','Oferta Especial',20.00,25.00),(3,_binary '','OROD003','Pizza , pan al ajo, gaseosa y postre de chocolate','2025-11-19','2025-10-19','Oferta Especial',22.00,30.00);
/*!40000 ALTER TABLE `ofertas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ordenes_venta`
--

DROP TABLE IF EXISTS `ordenes_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ordenes_venta` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `fecha_orden` datetime(6) NOT NULL,
  `total` decimal(10,2) NOT NULL,
  `id_usuario` bigint(20) NOT NULL,
  `id_ubicacion_entrega` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_orden_usuario` (`id_usuario`),
  KEY `FK_orden_ubicacion` (`id_ubicacion_entrega`),
  CONSTRAINT `FK_orden_ubicacion` FOREIGN KEY (`id_ubicacion_entrega`) REFERENCES `ubicaciones` (`id`),
  CONSTRAINT `FK_orden_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ordenes_venta`
--

LOCK TABLES `ordenes_venta` WRITE;
/*!40000 ALTER TABLE `ordenes_venta` DISABLE KEYS */;
INSERT INTO `ordenes_venta` VALUES (1,'2025-06-07 23:25:58.000000',46.00,1,1),(2,'2025-06-08 00:06:34.000000',24.00,2,2),(3,'2025-06-08 15:31:45.000000',23.00,2,2),(4,'2025-07-09 21:18:36.000000',33.00,1,1),(5,'2025-07-09 21:56:03.000000',21.00,6,8),(6,'2025-10-19 18:07:41.000000',13.00,8,9),(7,'2025-10-19 19:10:58.000000',29.00,8,9);
/*!40000 ALTER TABLE `ordenes_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo_producto` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `nombre` varchar(255) NOT NULL,
  `precio` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL,
  `ruta_imagen` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1nk4ttgpqciqys08mooa4ruo` (`codigo_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (1,'PROD001','Hamburguesa deliciosa y jugosa','Hamburguesa',9.00,45,'/uploads/1749518839180_hamburguesa.jpg'),(3,'PROD002','Papas Fritas deliciosas','Papas Fritas',5.00,25,'/uploads/1749518530224_papas-fritas.jpg'),(4,'PROD003','Gaseosa helada y frescaa','Gaseosa',5.00,30,'/uploads/1760913907369_Gaseosas.jpg');
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reclamacion`
--

DROP TABLE IF EXISTS `reclamacion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reclamacion` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `nombre_completo` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `tipo_reclamacion` varchar(255) NOT NULL,
  `descripcion` text NOT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reclamacion`
--

LOCK TABLES `reclamacion` WRITE;
/*!40000 ALTER TABLE `reclamacion` DISABLE KEYS */;
INSERT INTO `reclamacion` VALUES (1,'Patrick Del Aguila','u22327322@utp.edu.pe','SUGERENCIA','Mejoren su Pagina','2025-06-08 18:54:18'),(2,'Patrick Del Aguila','u22327322@utp.edu.pe','RECLAMACION','No me carga los productos que selecciono en el carrito','2025-06-08 18:58:09'),(3,'Franco Torres','u13467985@utp.edu.pe','QUEJA','No me gusta su pagina','2025-06-08 20:33:01'),(4,'Patrick Del Aguila','u22327322@utp.edu.pe','SUGERENCIA','Me gustaria que mejoren la parte de la compra de porductos','2025-07-10 02:10:48'),(5,'Estefano Rodriguez','u11223344@utp.edu.pe','QUEJA','Tuve inconvenientes al momento de iniciar sesion\n','2025-10-19 22:52:54');
/*!40000 ALTER TABLE `reclamacion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ubicaciones`
--

DROP TABLE IF EXISTS `ubicaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ubicaciones` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `codigo_aula` varchar(255) NOT NULL,
  `piso` varchar(255) NOT NULL,
  `id_usuario` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKtfwdl9o24f1hg0hd0tibohf1b` (`codigo_aula`),
  KEY `FK2wdvpv38jav6g8d71p5dbf2o9` (`id_usuario`),
  CONSTRAINT `FK2wdvpv38jav6g8d71p5dbf2o9` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ubicaciones`
--

LOCK TABLES `ubicaciones` WRITE;
/*!40000 ALTER TABLE `ubicaciones` DISABLE KEYS */;
INSERT INTO `ubicaciones` VALUES (1,'A0103','Primer Piso',1),(2,'A0307','Segundo Piso',2),(3,'B0203','Segundo Piso',4),(8,'C0303','Tercer Piso',6),(9,'A0303','Tercer Piso',8);
/*!40000 ALTER TABLE `ubicaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `activo` bit(1) DEFAULT NULL,
  `codigo_estudiante` varchar(255) NOT NULL,
  `contrasena` varchar(255) NOT NULL,
  `correo` varchar(255) NOT NULL,
  `fecha_registro` datetime(6) DEFAULT NULL,
  `nombre_completo` varchar(255) NOT NULL,
  `rol` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UKlvr0c4stpxvs8ebo13c10xcli` (`codigo_estudiante`),
  UNIQUE KEY `UKcdmw5hxlfj78uf4997i3qyyw5` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,_binary '','u22327322','$2a$10$0XyYPITL2U2cbYlUjr7nAO8lyAf//T3vbM7R84KByfW5yUBVXave.','u22327322@utp.edu.pe','2025-06-07 21:57:42.000000','Patrick Del Aguila','ROLE_USER'),(2,_binary '','u13467985','$2a$10$PqWqOZL0gqiJ21ezf2bKxuV0cd1.KhtOA0iWdw.XuTRN/2caJi9lq','u13467985@utp.edu.pe','2025-06-08 00:05:53.000000','Franco Torres','ROLE_USER'),(3,_binary '','ADMIN001','$2a$10$o7djiHZf45SOfrMbVV.bl.0p0xy9boxb1xS3rDINoMu.XdmlKstmy','admin123@utp.edu.pe','2025-07-09 17:18:32.000000','Admin Principal','ROLE_ADMIN'),(4,_binary '','u15975312','$2a$10$85q4zUp5DVxbGSfL7JKPQe5IZyBnHphmtj8BJQs3bfvCOX1HOBoRW','u15975312@utp.edu.pe','2025-07-09 18:03:07.000000','Piero Mesa','ROLE_USER'),(5,_binary '','u98745632','$2a$10$KdPF2qP0/i8MOaOh4OvSJ.YvK6uPg4Dnj3TRC49MOxPdqk5RseG0m','u98745632@utp.edu.pe','2025-07-09 18:05:51.000000','Camila Perez','ROLE_USER'),(6,_binary '','u14725896','$2a$10$5s0Q4STYpi65vlW4dH4WlOkswZ/OcMrtwqKuXoeo/NXEKTZRGDvAi','u14725896@utp.edu.pe','2025-07-09 18:11:34.000000','Fernanda Rojas','ROLE_USER'),(8,_binary '','u11223344','$2a$10$LDU4dMO2ujqUPb48.EtSXuqScUpkVi8W9T9W.JmgnZzInWcYPEsoG','u11223344@utp.edu.pe','2025-10-19 17:39:22.000000','Estefano Rodriguez','ROLE_USER'),(9,_binary '','u77889944','$2a$10$9G55Qb4lpvyKgRZVM4Hha.q2P355Ne9dj1qp62xSsWG/GhLbkN/GG','u77889944@utp.edu.pe','2025-10-19 17:42:11.000000','Andrea Hernandez','ROLE_USER');
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-19 19:32:21
