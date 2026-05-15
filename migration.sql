USE `incubator_db`;

-- Patch existing schema to match latest app code.
ALTER TABLE `users`
  ADD COLUMN IF NOT EXISTS `sex` varchar(20) DEFAULT NULL,
  ADD COLUMN IF NOT EXISTS `nid_no` varchar(40) DEFAULT NULL;

ALTER TABLE `projects`
  ADD COLUMN IF NOT EXISTS `progress` int(11) NOT NULL DEFAULT 0;
