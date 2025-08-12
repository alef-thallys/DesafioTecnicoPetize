INSERT INTO sub_tasks (id, title, status, task_id, created_at, updated_at)
VALUES (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a21'), 'Implement User Authentication', 'DONE',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), NOW(), NOW()),
       (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a22'), 'Design Database Schema', 'TODO',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'), NOW(), NOW()),
       (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a23'), 'Setup CI/CD Pipeline', 'DONE',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'), NOW(), NOW()),
       (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a24'), 'Write API Documentation', 'TODO',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'), NOW(), NOW()),
       (UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a25'), 'Refactor Legacy Code', 'DONE',
        UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'), NOW(), NOW());
