-- =============================================
-- Schema for xavierarbat-back
-- PostgreSQL with slug-based PKs and JSONB
-- =============================================

-- =============================================
-- Blogs
-- =============================================
CREATE TABLE IF NOT EXISTS blog (
    slug        VARCHAR(255) PRIMARY KEY,
    date        DATE         NOT NULL,
    title       JSONB        NOT NULL,  -- {"es": "...", "ca": "...", "en": "..."}
    description JSONB        NOT NULL,  -- {"es": "...", "ca": "...", "en": "..."}
    content     JSONB        NOT NULL   -- {"es": "markdown...", "ca": "...", "en": "..."}
);

-- =============================================
-- Contacts
-- =============================================
CREATE TABLE IF NOT EXISTS contact (
    name            VARCHAR(255) PRIMARY KEY,
    display         JSONB        NOT NULL,  -- {"es": "...", "ca": "...", "en": "..."}
    value           VARCHAR(512) NOT NULL DEFAULT '',
    link            VARCHAR(512),
    show_in_footer  BOOLEAN      NOT NULL DEFAULT false
);

-- =============================================
-- Projects
-- =============================================
CREATE TABLE IF NOT EXISTS project (
    slug           VARCHAR(255) PRIMARY KEY,
    date           DATE         NOT NULL,
    image          VARCHAR(512) NOT NULL,
    title          JSONB        NOT NULL,  -- {"es": "...", "ca": "...", "en": "..."}
    description    JSONB        NOT NULL,  -- {"es": "...", "ca": "...", "en": "..."}
    content        JSONB        NOT NULL,  -- {"es": "markdown...", "ca": "...", "en": "..."}
    image_display  VARCHAR(50)  NOT NULL DEFAULT 'COVER',
    aspect_ratio   VARCHAR(50)  NOT NULL DEFAULT 'FOURTHIRDS',
    alt_images     JSONB        NOT NULL DEFAULT '[]'  -- ["/images/...", "/images/..."]
);

-- =============================================
-- Project tags (EnumSet stored as separate rows)
-- =============================================
CREATE TABLE IF NOT EXISTS project_tag (
    project_slug VARCHAR(255) NOT NULL REFERENCES project(slug) ON DELETE CASCADE,
    tag          VARCHAR(50)  NOT NULL,
    PRIMARY KEY (project_slug, tag)
);
