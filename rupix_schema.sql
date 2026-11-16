
USE rupix_db;

-- ==========================================
-- 1. USERS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(100) NOT NULL,
    vpa VARCHAR(100) NOT NULL UNIQUE,
    user_type ENUM(
        'CUSTOMER',
        'MERCHANT',
        'CLIENT',
        'FREELANCER'
    ) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ==========================================
-- 2. WALLETS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS wallets (
    wallet_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    CHECK (balance >= 0)
);

-- ==========================================
-- 3. TRANSACTIONS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS transactions (
    transaction_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    request_id VARCHAR(100) NOT NULL UNIQUE,
    sender_wallet_id BIGINT,
    receiver_wallet_id BIGINT,
    amount DECIMAL(15,2) NOT NULL,

    transaction_type ENUM(
        'P2P',
        'MERCHANT',
        'ESCROW_DEPOSIT',
        'ESCROW_RELEASE',
        'SETTLEMENT'
    ) NOT NULL,

    status ENUM(
        'SUCCESS',
        'FAILED'
    ) NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (sender_wallet_id)
        REFERENCES wallets(wallet_id),

    FOREIGN KEY (receiver_wallet_id)
        REFERENCES wallets(wallet_id),

    CHECK (amount > 0)
);

-- ==========================================
-- 4. MERCHANTS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS merchants (
    merchant_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL UNIQUE,
    business_name VARCHAR(150) NOT NULL,
    mdr_rate DECIMAL(5,2) NOT NULL DEFAULT 2.00,

    FOREIGN KEY (user_id)
        REFERENCES users(user_id),

    CHECK (mdr_rate >= 0 AND mdr_rate <= 100)
);

-- ==========================================
-- 5. SETTLEMENTS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS settlements (
    settlement_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id BIGINT NOT NULL UNIQUE,
    merchant_id BIGINT NOT NULL,
    gross_amount DECIMAL(15,2) NOT NULL,
    mdr_amount DECIMAL(15,2) NOT NULL,
    net_amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (transaction_id)
        REFERENCES transactions(transaction_id),

    FOREIGN KEY (merchant_id)
        REFERENCES merchants(merchant_id),

    CHECK (gross_amount > 0),
    CHECK (mdr_amount >= 0),
    CHECK (net_amount >= 0)
);

-- ==========================================
-- 6. ESCROW PROJECTS TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS escrow_projects (
    project_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    freelancer_id BIGINT NOT NULL,
    project_title VARCHAR(150) NOT NULL,

    escrow_balance DECIMAL(15,2)
        NOT NULL DEFAULT 0.00,

    status ENUM(
        'ACTIVE',
        'COMPLETED',
        'CANCELLED'
    ) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (client_id)
        REFERENCES users(user_id),

    FOREIGN KEY (freelancer_id)
        REFERENCES users(user_id),

    CHECK (escrow_balance >= 0),
    CHECK (client_id <> freelancer_id)
);

-- ==========================================
-- 7. MILESTONES TABLE
-- ==========================================

CREATE TABLE IF NOT EXISTS milestones (
    milestone_id BIGINT AUTO_INCREMENT PRIMARY KEY,
    project_id BIGINT NOT NULL,
    title VARCHAR(150) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,

    status ENUM(
        'PENDING',
        'SUBMITTED',
        'APPROVED',
        'RELEASED'
    ) NOT NULL DEFAULT 'PENDING',

    released_at TIMESTAMP NULL,

    FOREIGN KEY (project_id)
        REFERENCES escrow_projects(project_id),

    CHECK (amount > 0)
);
