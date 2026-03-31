-- liquibase formatted sql
-- changeset marina:1
ALTER TABLE user_sessions
DROP CONSTRAINT IF EXISTS user_sessions_state_check;
ALTER TABLE user_sessions
ADD CONSTRAINT user_sessions_state_check
CHECK (state IN ('WAITING_FOR_SHELTER', 'IN_MAIN_MENU', 'IN_SHELTER_INFO_MENU', 'AWAITING_CONTACT_INFO', 'AWAITING_REPORT', 'VOLUNTEER_CALLED'));