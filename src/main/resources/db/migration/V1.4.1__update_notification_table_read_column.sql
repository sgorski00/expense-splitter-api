ALTER TABLE notifications RENAME COLUMN read TO is_read;
DROP INDEX IF EXISTS idx_notifications_read;
CREATE INDEX idx_notifications_is_read ON notifications(is_read) WHERE is_read = false;

