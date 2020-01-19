-- --------------------------------------------------------
-- Host:                         127.0.0.1
-- Versión del servidor:         5.6.26-log - MySQL Community Server (GPL)
-- SO del servidor:              Win64
-- HeidiSQL Versión:             10.3.0.5771
-- --------------------------------------------------------

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET NAMES utf8 */;
/*!50503 SET NAMES utf8mb4 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;


-- Volcando estructura de base de datos para appbienes
CREATE DATABASE IF NOT EXISTS `appbienes` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `appbienes`;

-- Volcando estructura para tabla appbienes.estaciones
CREATE TABLE IF NOT EXISTS `estaciones` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `imagen` varchar(255) DEFAULT NULL,
  `lugar` varchar(255) DEFAULT NULL,
  `ubicacion` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8;

-- Volcando datos para la tabla appbienes.estaciones: ~28 rows (aproximadamente)
/*!40000 ALTER TABLE `estaciones` DISABLE KEYS */;
INSERT INTO `estaciones` (`id`, `imagen`, `lugar`, `ubicacion`) VALUES
	(1, 'logoUce.jpg', 'BAÑO', 'BAÑO'),
	(2, 'logoUce.jpg', 'CARTON 3', 'BODEGA'),
	(3, 'logoUce.jpg', 'CUARTO', 'BODEGA'),
	(4, 'logoUce.jpg', 'CARTON 1', 'OFICINA 2'),
	(5, 'logoUce.jpg', 'CIVIL', 'CIVIL'),
	(6, 'logoUce.jpg', 'CORREDOR 1', 'CORREDOR 1'),
	(7, 'logoUce.jpg', 'CUBICULO 1', 'CUBICULOS'),
	(8, 'logoUce.jpg', 'CUBICULO 2', 'CUBICULOS'),
	(9, 'logoUce.jpg', 'CUBICULO 3', 'CUBICULOS'),
	(10, 'logoUce.jpg', 'CUBICULO 4', 'CUBICULOS'),
	(11, 'logoUce.jpg', 'CUBICULO 5', 'CUBICULOS'),
	(12, 'logoUce.jpg', 'CUBICULO 6', 'CUBICULOS'),
	(13, 'logoUce.jpg', 'CAJON', 'OFICINA 1'),
	(14, 'logoUce.jpg', 'COORDINACION 2', 'OFICINA 1'),
	(15, 'logoUce.jpg', 'COORDINACION 1', 'OFICINA 1'),
	(16, 'logoUce.jpg', 'ARCHIVADOR', 'OFICINA 1'),
	(17, 'logoUce.jpg', 'OFICINA 1', 'OFICINA 1'),
	(18, 'logoUce.jpg', 'CARTON', 'OFICINA 2'),
	(19, 'logoUce.jpg', 'COORDINACION', 'OFICINA 2'),
	(20, 'logoUce.jpg', 'OFICINA 2', 'OFICINA 2'),
	(21, 'logoUce.jpg', 'PASILLO', 'PASILLO'),
	(22, 'logoUce.jpg', 'SALA A', 'EDIFICIO DE COMPUTO'),
	(23, 'logoUce.jpg', 'SALA B', 'EDIFICIO DE COMPUTO'),
	(24, 'logoUce.jpg', 'SALA C', 'EDIFICIO DE COMPUTO'),
	(25, 'logoUce.jpg', 'SALA D', 'EDIFICIO DE COMPUTO'),
	(26, 'logoUce.jpg', 'SALA E', 'EDIFICIO DE COMPUTO'),
	(27, 'logoUce.jpg', 'VITRINA FUNDA', 'VITRINA'),
	(28, 'logoUce.jpg', 'VITRINA', 'VITRINA');
/*!40000 ALTER TABLE `estaciones` ENABLE KEYS */;

/*!40101 SET SQL_MODE=IFNULL(@OLD_SQL_MODE, '') */;
/*!40014 SET FOREIGN_KEY_CHECKS=IF(@OLD_FOREIGN_KEY_CHECKS IS NULL, 1, @OLD_FOREIGN_KEY_CHECKS) */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
