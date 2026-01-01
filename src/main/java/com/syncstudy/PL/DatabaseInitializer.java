package com.syncstudy.PL;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Database Initializer
 * Creates necessary tables if they don't exist
 */
public class DatabaseInitializer {

    /**
     * Initialize the database by creating all necessary tables
     */
    public static void initialize() {
        String sql = """
            -- Create users table
            CREATE TABLE IF NOT EXISTS users (
                id SERIAL PRIMARY KEY,
                username VARCHAR(255) UNIQUE NOT NULL,
                password_hash VARCHAR(255) NOT NULL,
                email VARCHAR(255),
                full_name VARCHAR(255),
                university VARCHAR(255),
                department VARCHAR(255),
                profile_photo VARCHAR(500),
                is_blocked BOOLEAN DEFAULT FALSE,
                is_admin BOOLEAN DEFAULT FALSE,
                registration_date TIMESTAMP DEFAULT NOW(),
                last_login TIMESTAMP
            );

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

            -- Create admin_logs table
            CREATE TABLE IF NOT EXISTS admin_logs (
                id SERIAL PRIMARY KEY,
                admin_id BIGINT NOT NULL,
                action VARCHAR(100) NOT NULL,
                target_user_id BIGINT,
                details TEXT,
                created_at TIMESTAMP DEFAULT NOW(),
                FOREIGN KEY (admin_id) REFERENCES users(id)
            );
            """;

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {

            stmt.execute(sql);
            System.out.println(" Database tables created successfully !!!! here here ");

            // Note: Default users are created by UserDAOPostgres with correct BCrypt hashes

        } catch (Exception e) {
            System.err.println(" Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

