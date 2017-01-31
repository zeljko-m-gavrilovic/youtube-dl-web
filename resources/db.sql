CREATE TABLE IF NOT EXISTS `tracks` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `url` varchar(256) NOT NULL,
  `title` varchar(256) DEFAULT NULL,
  `note` varchar(256) DEFAULT NULL,
  `downloaded_at` datetime DEFAULT NULL,
  `convert_to_mp3` tinyint(1) DEFAULT '1',
  `status` varchar(32) DEFAULT NULL,
  `track_duration` int(11) DEFAULT NULL COMMENT 'duration in seconds',
  `thumbnail` varchar(256) DEFAULT NULL,
  `directory` varchar(256) DEFAULT NULL,
  `download_duration` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`)
);
