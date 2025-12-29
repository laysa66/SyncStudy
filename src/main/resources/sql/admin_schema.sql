-- SyncStudy Admin Module - Database Schema
-- Run this script to set up admin functionality

-- Extend users table with admin fields
ALTER TABLE users ADD COLUMN IF NOT EXISTS email VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS full_name VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS university VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS department VARCHAR(255);
ALTER TABLE users ADD COLUMN IF NOT EXISTS profile_photo VARCHAR(500);
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_blocked BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS is_admin BOOLEAN DEFAULT FALSE;
ALTER TABLE users ADD COLUMN IF NOT EXISTS registration_date TIMESTAMP DEFAULT NOW();
ALTER TABLE users ADD COLUMN IF NOT EXISTS last_login TIMESTAMP;

-- Create block_records table
CREATE TABLE IF NOT EXISTS block_records (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    block_date TIMESTAMP DEFAULT NOW(),
    unblock_date TIMESTAMP,
    reason TEXT NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- Create admin_logs table for audit trail
CREATE TABLE IF NOT EXISTS admin_logs (
    id SERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    action VARCHAR(100) NOT NULL,
    target_user_id BIGINT,
    details TEXT,
    created_at TIMESTAMP DEFAULT NOW(),
    FOREIGN KEY (admin_id) REFERENCES users(id)
);

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_is_blocked ON users(is_blocked);
CREATE INDEX IF NOT EXISTS idx_users_is_admin ON users(is_admin);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);
CREATE INDEX IF NOT EXISTS idx_block_records_user_id ON block_records(user_id);
CREATE INDEX IF NOT EXISTS idx_block_records_is_active ON block_records(is_active);
CREATE INDEX IF NOT EXISTS idx_admin_logs_admin_id ON admin_logs(admin_id);
CREATE INDEX IF NOT EXISTS idx_admin_logs_created_at ON admin_logs(created_at);

-- Set default admin user
UPDATE users SET is_admin = TRUE WHERE username = 'admin';

-- Sample data (optional - for testing)
-- INSERT INTO users (username, password_hash, email, full_name, university, is_admin)
-- VALUES ('admin', '$2a$10$...', 'admin@syncstudy.com', 'Administrator', 'SyncStudy', TRUE);

-- Create some test users (optional)
-- INSERT INTO users (username, password_hash, email, full_name, university, department)
-- VALUES
--     ('john.doe', '$2a$10$...', 'john@example.com', 'John Doe', 'MIT', 'Computer Science'),
--     ('jane.smith', '$2a$10$...', 'jane@example.com', 'Jane Smith', 'Stanford', 'Mathematics');

COMMENT ON TABLE block_records IS 'Stores history of user account blocks and unblocks';
COMMENT ON TABLE admin_logs IS 'Audit trail for all admin actions';
COMMENT ON COLUMN users.is_blocked IS 'Whether the user account is currently blocked';
COMMENT ON COLUMN users.is_admin IS 'Whether the user has admin privileges';

