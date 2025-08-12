INSERT INTO tasks (id, title, description, due_date, status, priority, created_at, updated_at) VALUES
(UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a11'), 'Implement User Authentication', 'Develop and integrate user authentication using Spring Security.', '2025-08-15', 'DONE', 'HIGH', NOW(), NOW()),
(UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a12'), 'Design Database Schema', 'Create the initial database schema for the project.', '2025-08-20', 'TODO', 'HIGH', NOW(), NOW()),
(UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a13'), 'Setup CI/CD Pipeline', 'Configure a continuous integration and deployment pipeline on GitHub Actions.', '2025-08-25', 'DONE', 'MEDIUM', NOW(), NOW()),
(UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a14'), 'Write API Documentation', 'Document all API endpoints using Swagger/OpenAPI.', '2025-09-01', 'TODO', 'LOW', NOW(), NOW()),
(UUID_TO_BIN('a0eebc99-9c0b-4ef8-bb6d-6bb9bd380a15'), 'Refactor Legacy Code', 'Refactor the old data processing module for better performance.', '2025-09-10', 'DONE', 'MEDIUM', NOW(), NOW());

