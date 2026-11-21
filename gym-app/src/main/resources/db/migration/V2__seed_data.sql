INSERT INTO members (full_name, email) VALUES
    ('John Doe', 'john.doe@example.com'),
    ('Jane Doe', 'jane.doe@example.com'),
    ('Michael Scott', 'michael.scott@example.com');

INSERT INTO membership_plans (name, billing_interval, price_cents, active) VALUES
    ('Monthly Unlimited', 'MONTHLY', 6500, TRUE),
    ('10-Class Pack', 'PACK', 8000, TRUE),
    ('Student Monthly', 'MONTHLY', 4500, TRUE);

INSERT INTO memberships (member_id, plan_id, status, start_date, end_date, frozen_at)
SELECT m.id, p.id, 'ACTIVE', CURRENT_DATE - INTERVAL '15 days', CURRENT_DATE + INTERVAL '45 days', NULL
FROM members m, membership_plans p
WHERE m.email = 'john.doe@example.com' AND p.name = 'Monthly Unlimited';

INSERT INTO memberships (member_id, plan_id, status, start_date, end_date, frozen_at)
SELECT m.id, p.id, 'FROZEN', CURRENT_DATE - INTERVAL '30 days', CURRENT_DATE + INTERVAL '30 days', CURRENT_TIMESTAMP - INTERVAL '3 days'
FROM members m, membership_plans p
WHERE m.email = 'jane.doe@example.com' AND p.name = '10-Class Pack';

INSERT INTO memberships (member_id, plan_id, status, start_date, end_date, frozen_at)
SELECT m.id, p.id, 'ACTIVE', CURRENT_DATE - INTERVAL '5 days', CURRENT_DATE + INTERVAL '25 days', NULL
FROM members m, membership_plans p
WHERE m.email = 'michael.scott@example.com' AND p.name = 'Student Monthly';

INSERT INTO class_templates (title, instructor_name, day_of_week, start_time, duration_minutes, default_capacity, active) VALUES
    ('Morning HIIT', 'Michael Jackson', 'MONDAY', '07:30:00', 45, 16, TRUE),
    ('Evening Mobility & Yoga', 'Nicolas Cage', 'WEDNESDAY', '18:00:00', 60, 20, TRUE),
    ('Strength Foundations', 'Tupac Shakur', 'FRIDAY', '17:00:00', 50, 12, TRUE);

INSERT INTO class_instances (class_template_id, title, instructor_name, starts_at, ends_at, capacity, status)
SELECT id, title, instructor_name,
       (CURRENT_DATE + INTERVAL '1 day') + TIME '07:30:00',
       (CURRENT_DATE + INTERVAL '1 day') + TIME '08:15:00',
       default_capacity, 'SCHEDULED'
FROM class_templates
WHERE title = 'Morning HIIT';

INSERT INTO class_instances (class_template_id, title, instructor_name, starts_at, ends_at, capacity, status)
SELECT id, title, instructor_name,
       (CURRENT_DATE + INTERVAL '3 days') + TIME '18:00:00',
       (CURRENT_DATE + INTERVAL '3 days') + TIME '19:00:00',
       default_capacity, 'SCHEDULED'
FROM class_templates
WHERE title = 'Evening Mobility & Yoga';

INSERT INTO bookings (member_id, class_instance_id, status, booked_at)
SELECT m.id, ci.id, 'CONFIRMED', CURRENT_TIMESTAMP
FROM members m
JOIN class_instances ci ON ci.title = 'Morning HIIT'
WHERE m.email = 'john.doe@example.com';

INSERT INTO bookings (member_id, class_instance_id, status, booked_at)
SELECT m.id, ci.id, 'WAITLISTED', CURRENT_TIMESTAMP
FROM members m
JOIN class_instances ci ON ci.title = 'Morning HIIT'
WHERE m.email = 'jane.doe@example.com';