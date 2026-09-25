CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS courses (
  id UUID PRIMARY KEY DEFAULT gen_random_uuid(),

  title TEXT NOT NULL,
  description TEXT,
  banner_url TEXT,
  university_id UUID,
  location TEXT,
  course_date TIMESTAMPTZ NOT NULL,
  category TEXT,

  total_spots INTEGER NOT NULL DEFAULT 30,
  available_spots INTEGER NOT NULL DEFAULT 30,
  points_awarded INTEGER NOT NULL DEFAULT 100,
  has_certificate BOOLEAN NOT NULL DEFAULT FALSE,
  is_active BOOLEAN NOT NULL DEFAULT TRUE,

  created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
  updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

  CONSTRAINT courses_title_not_blank
    CHECK (char_length(trim(title)) > 0),

  CONSTRAINT courses_total_spots_positive
    CHECK (total_spots > 0),

  CONSTRAINT courses_available_spots_valid
    CHECK (
      available_spots >= 0
      AND available_spots <= total_spots
    ),

  CONSTRAINT courses_points_awarded_valid
    CHECK (points_awarded >= 0)
);

CREATE INDEX IF NOT EXISTS courses_course_date_idx
  ON courses (course_date);

CREATE INDEX IF NOT EXISTS courses_category_idx
  ON courses (category);

CREATE INDEX IF NOT EXISTS courses_is_active_idx
  ON courses (is_active);

