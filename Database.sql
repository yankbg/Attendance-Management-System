-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 20, 2025 at 05:29 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `attendancesystem`
--

-- --------------------------------------------------------

--
-- Table structure for table `attendance`
--

CREATE TABLE `attendance` (
  `id` int(11) NOT NULL,
  `student_id` int(11) NOT NULL,
  `student_name` varchar(255) NOT NULL,
  `date` date NOT NULL,
  `time` time NOT NULL,
  `marked_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `attendance`
--

INSERT INTO `attendance` (`id`, `student_id`, `student_name`, `date`, `time`, `marked_at`) VALUES
(1, 1224, 'bwenge muhima', '2025-06-13', '15:46:00', '2025-06-13 12:46:55'),
(2, 1234, 'caleb nz', '2025-06-13', '15:56:00', '2025-06-13 12:56:58'),
(3, 4321, 'yannick kd', '2025-06-13', '16:02:00', '2025-06-13 13:03:08'),
(4, 1423, 'sawasawa jos', '2025-06-13', '16:17:00', '2025-06-13 13:17:18'),
(5, 0, 'caleb', '2025-06-13', '16:21:00', '2025-06-13 13:21:18'),
(6, 54321, 'jean de la fontaine', '2025-06-13', '17:01:00', '2025-06-13 14:02:09'),
(7, 1224, 'bwenge muhima', '2025-06-15', '20:44:00', '2025-06-15 17:44:29'),
(8, 0, 'caleb', '2025-06-15', '20:53:00', '2025-06-15 17:53:54'),
(9, 1234, 'caleb nz', '2025-06-15', '21:08:00', '2025-06-15 18:09:07'),
(10, 4321, 'yannick kd', '2025-06-15', '21:10:00', '2025-06-15 18:10:20'),
(11, 54321, 'jean de la fontaine', '2025-06-15', '21:20:00', '2025-06-15 18:20:32'),
(12, 1423, 'sawasawa jos', '2025-06-15', '21:29:00', '2025-06-15 18:29:17'),
(13, 1111, 'yan kbg', '2025-06-15', '22:25:00', '2025-06-15 19:28:03'),
(14, 4567, 'sawasawa joseph', '2025-06-15', '22:32:00', '2025-06-15 19:32:45'),
(15, 1224, 'bwenge muhima', '2025-06-16', '12:28:00', '2025-06-16 09:34:02'),
(16, 1224, 'bwenge muhima', '2025-06-18', '21:11:00', '2025-06-18 18:18:58'),
(17, 1111, 'yan kbg', '2025-06-18', '21:19:00', '2025-06-18 18:20:32');

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` varchar(50) NOT NULL,
  `name` varchar(100) NOT NULL,
  `image_path` varchar(255) NOT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `name`, `image_path`, `created_at`) VALUES
('0000', 'Caleb', 'uploads/img_684c25b28a16a.jpg', '2025-06-13 13:20:50'),
('1111', 'yan kbg', 'uploads/img_684f1ba6a0ceb.jpg', '2025-06-15 19:14:46'),
('1224', 'bwenge muhima', 'uploads/img_684ab1105bd54.jpg', '2025-06-12 10:50:56'),
('1234', 'Caleb nz', 'uploads/img_684b297c6a63d.jpg', '2025-06-12 19:24:44'),
('1423', 'sawasawa jos', 'uploads/img_684c24a9a09be.jpg', '2025-06-13 13:16:25'),
('4321', 'Yannick kd', 'uploads/img_684b29bbbfa75.jpg', '2025-06-12 19:25:47'),
('4567', 'Sawasawa joseph', 'uploads/img_684f1fb0c3bed.jpg', '2025-06-15 19:32:00'),
('54321', 'Jean de la fontaine', 'uploads/img_684c2f133f133.jpg', '2025-06-13 14:00:51');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `attendance`
--
ALTER TABLE `attendance`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unique_attendance` (`student_id`,`date`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `attendance`
--
ALTER TABLE `attendance`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=18;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
