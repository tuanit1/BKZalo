-- phpMyAdmin SQL Dump
-- version 5.1.1
-- https://www.phpmyadmin.net/
--
-- Máy chủ: 127.0.0.1
-- Thời gian đã tạo: Th10 29, 2021 lúc 12:48 PM
-- Phiên bản máy phục vụ: 10.4.21-MariaDB
-- Phiên bản PHP: 8.0.10

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Cơ sở dữ liệu: `bkzalo`
--

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_message`
--

CREATE TABLE `tbl_message` (
  `id` int(11) NOT NULL,
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `message` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `time` datetime NOT NULL,
  `isRemove` tinyint(1) NOT NULL,
  `isSeen` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_participant`
--

CREATE TABLE `tbl_participant` (
  `user_id` int(11) NOT NULL,
  `room_id` int(11) NOT NULL,
  `nickname` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `isAdmin` tinyint(1) NOT NULL,
  `isHide` tinyint(1) NOT NULL,
  `timestamp` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_participant`
--

INSERT INTO `tbl_participant` (`user_id`, `room_id`, `nickname`, `isAdmin`, `isHide`, `timestamp`) VALUES
(1, 1, 'tuan', 0, 0, '2021-10-27 17:05:23'),
(1, 2, 'tuan', 0, 0, '2021-10-27 17:07:06'),
(1, 3, '', 0, 0, '2021-10-28 14:03:20'),
(2, 1, 'Hương Lê', 0, 0, '2021-10-27 17:05:23'),
(2, 2, 'huong', 0, 0, '2021-10-27 17:07:06'),
(3, 2, 'nhi', 0, 0, '2021-10-27 17:07:06'),
(3, 3, '', 0, 0, '2021-10-28 14:03:20');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_relationship`
--

CREATE TABLE `tbl_relationship` (
  `user_id1` int(11) NOT NULL,
  `user_id2` int(11) NOT NULL,
  `requester` int(11) DEFAULT NULL,
  `blocker` int(11) DEFAULT NULL,
  `status` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_relationship`
--

INSERT INTO `tbl_relationship` (`user_id1`, `user_id2`, `requester`, `blocker`, `status`) VALUES
(1, 2, NULL, NULL, 'friend'),
(1, 5, NULL, NULL, 'friend'),
(2, 3, NULL, NULL, 'friend'),
(3, 1, NULL, NULL, 'friend'),
(4, 1, NULL, NULL, 'friend');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_room`
--

CREATE TABLE `tbl_room` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `background` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_room`
--

INSERT INTO `tbl_room` (`id`, `name`, `image`, `background`, `type`) VALUES
(1, 'private_tuan_huong', '', '', 'private'),
(2, 'Đồ án', 'img1.jpg', '', 'group'),
(3, 'private_nhi_tuan', '', '', 'private');

-- --------------------------------------------------------

--
-- Cấu trúc bảng cho bảng `tbl_user`
--

CREATE TABLE `tbl_user` (
  `id` int(11) NOT NULL,
  `name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `image` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `birthday` datetime NOT NULL,
  `phone` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `password` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL,
  `bio` longtext COLLATE utf8mb4_unicode_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Đang đổ dữ liệu cho bảng `tbl_user`
--

INSERT INTO `tbl_user` (`id`, `name`, `image`, `birthday`, `phone`, `password`, `bio`) VALUES
(1, 'Do Thanh Tuan', 'tuan.jpg', '2001-06-04 10:28:12', '1111', 'tuan', 'hello'),
(2, 'Le Thi Thu Huong', 'huong.jpg', '2001-10-10 00:00:00', '1111', 'huong', 'hello'),
(3, 'Luu Yen Nhi', 'nhi.jpg', '2001-12-21 00:00:00', '1111', 'nhi', 'helo'),
(4, 'Huynh Ngoc Hoa', '', '2021-10-29 07:22:38', '049548955', '1', 'sdfsdf'),
(5, 'Nguyen Minh Tri', '', '2021-10-29 07:23:05', '039483984', '1', 'fdjdkfdf');

--
-- Chỉ mục cho các bảng đã đổ
--

--
-- Chỉ mục cho bảng `tbl_message`
--
ALTER TABLE `tbl_message`
  ADD PRIMARY KEY (`id`),
  ADD KEY `fk_user_message` (`user_id`),
  ADD KEY `fk_room_message` (`room_id`);

--
-- Chỉ mục cho bảng `tbl_participant`
--
ALTER TABLE `tbl_participant`
  ADD PRIMARY KEY (`user_id`,`room_id`),
  ADD KEY `fk_room_part` (`room_id`);

--
-- Chỉ mục cho bảng `tbl_relationship`
--
ALTER TABLE `tbl_relationship`
  ADD PRIMARY KEY (`user_id1`,`user_id2`),
  ADD KEY `fk_relation_user2` (`user_id2`),
  ADD KEY `fk_relation_requester` (`requester`),
  ADD KEY `fk_relation_blocker` (`blocker`);

--
-- Chỉ mục cho bảng `tbl_room`
--
ALTER TABLE `tbl_room`
  ADD PRIMARY KEY (`id`);

--
-- Chỉ mục cho bảng `tbl_user`
--
ALTER TABLE `tbl_user`
  ADD PRIMARY KEY (`id`);

--
-- AUTO_INCREMENT cho các bảng đã đổ
--

--
-- AUTO_INCREMENT cho bảng `tbl_message`
--
ALTER TABLE `tbl_message`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT cho bảng `tbl_room`
--
ALTER TABLE `tbl_room`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT cho bảng `tbl_user`
--
ALTER TABLE `tbl_user`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- Các ràng buộc cho các bảng đã đổ
--

--
-- Các ràng buộc cho bảng `tbl_message`
--
ALTER TABLE `tbl_message`
  ADD CONSTRAINT `fk_room_message` FOREIGN KEY (`room_id`) REFERENCES `tbl_room` (`id`),
  ADD CONSTRAINT `fk_user_message` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`);

--
-- Các ràng buộc cho bảng `tbl_participant`
--
ALTER TABLE `tbl_participant`
  ADD CONSTRAINT `fk_room_part` FOREIGN KEY (`room_id`) REFERENCES `tbl_room` (`id`),
  ADD CONSTRAINT `fk_user_part` FOREIGN KEY (`user_id`) REFERENCES `tbl_user` (`id`);

--
-- Các ràng buộc cho bảng `tbl_relationship`
--
ALTER TABLE `tbl_relationship`
  ADD CONSTRAINT `fk_relation_blocker` FOREIGN KEY (`blocker`) REFERENCES `tbl_user` (`id`),
  ADD CONSTRAINT `fk_relation_requester` FOREIGN KEY (`requester`) REFERENCES `tbl_user` (`id`),
  ADD CONSTRAINT `fk_relation_user1` FOREIGN KEY (`user_id1`) REFERENCES `tbl_user` (`id`),
  ADD CONSTRAINT `fk_relation_user2` FOREIGN KEY (`user_id2`) REFERENCES `tbl_user` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
