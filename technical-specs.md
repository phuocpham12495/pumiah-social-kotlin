# Pumiah Social Technical Specification

## 1. Introduction

Pumiah Social is designed as a minimalist social networking platform focused on enabling users to connect with friends and share updates without the typical complexities of modern social media. This document outlines the technical architecture, core technologies, and implementation details for building the Pumiah Social application using a Kotlin UI frontend with Antigravity and an existing Supabase backend.

## 2. Architecture Overview

The application will follow a client-server architecture:

*   **Frontend (Client):** Developed using Kotlin UI (Antigravity framework), providing a rich, interactive, and responsive user interface across various devices.
*   **Backend (Server):** Leverages Supabase for database (PostgreSQL), authentication, real-time subscriptions, storage, and serverless functions (Edge Functions).
*   **API:** All communication between the frontend and backend will occur via Supabase's auto-generated RESTful APIs and real-time WebSocket connections.

```mermaid
graph TD
    A[Client Devices: Desktop, Tablet, Mobile] -->|HTTPS/WS| B(Kotlin UI with Antigravity)
    B -->|RESTful API & Realtime Subscriptions| C[Supabase Backend]
    C --> D[PostgreSQL Database]
    C --> E[Supabase Auth]
    C --> F[Supabase Storage]
    C --> G[Supabase Realtime]
    C --> H[Supabase Functions (Edge Functions)]
    H --> I[External Services (e.g., Push Notification Service)]
```

## 3. Key Technologies

*   **Frontend Development Language:** Kotlin
*   **Frontend UI Framework:** Antigravity (Assumed to be a Kotlin-native web UI framework, e.g., similar to Compose for Web or other Kotlin/JS solutions, providing components and layout management for building responsive web UIs).
*   **Backend as a Service (BaaS):** Supabase
    *   **Database:** PostgreSQL (managed by Supabase)
    *   **Authentication:** Supabase Auth (JWT-based)
    *   **Realtime:** Supabase Realtime (WebSockets)
    *   **Storage:** Supabase Storage (for images and files)
    *   **Serverless Functions:** Supabase Functions (Edge Functions for custom logic, e.g., webhooks, PWA push notifications).
*   **Styling:** CSS3, potentially integrated with Antigravity's styling capabilities.
*   **Network Communication:** RESTful API calls and WebSockets.
*   **Version Control:** Git (GitHub).

## 4. Data Model (Supabase PostgreSQL Tables)

The following tables will form the core data structure within the Supabase PostgreSQL database. Row Level Security (RLS) will be extensively used to enforce access control.

*   **`profiles`**: Stores user profile information.
    *   `id` (UUID, PK, references `auth.users.id`)
    *   `username` (TEXT, UNIQUE, NOT NULL)
    *   `name` (TEXT)
    *   `avatar_url` (TEXT)
    *   `cover_photo_url` (TEXT, OPTIONAL)
    *   `bio` (TEXT, OPTIONAL)
    *   `location` (TEXT, OPTIONAL)
    *   `join_date` (TIMESTAMPTZ, DEFAULT NOW(), NOT NULL)
    *   `email` (TEXT, REFERENCES `auth.users.email`)
    *   `date_of_birth` (DATE, OPTIONAL)
    *   `app_settings` (JSONB, OPTIONAL, for user-specific app configurations)
*   **`friend_requests`**: Manages pending friend requests.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `sender_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `receiver_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `status` (ENUM 'pending', 'accepted', 'declined', DEFAULT 'pending', NOT NULL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   *Unique constraint on `(sender_id, receiver_id)` to prevent duplicate requests.*
*   **`friends`**: Stores established friend connections.
    *   `user1_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `user2_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   *Primary Key: `(user1_id, user2_id)` to ensure unique pairs. Implement a check constraint to ensure `user1_id < user2_id` to avoid duplicate `(A, B)` and `(B, A)` entries.*
*   **`posts`**: Stores user-generated posts.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `author_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `content_text` (TEXT, OPTIONAL)
    *   `image_url` (TEXT, OPTIONAL, references Supabase Storage)
    *   `link_url` (TEXT, OPTIONAL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   *At least one of `content_text`, `image_url`, `link_url` must be present.*
*   **`likes`**: Records likes for posts and comments.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `user_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `post_id` (UUID, FK to `posts.id`, OPTIONAL)
    *   `comment_id` (UUID, FK to `comments.id`, OPTIONAL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   *Unique constraint on `(user_id, post_id)` or `(user_id, comment_id)`.*
    *   *Check constraint to ensure only one of `post_id` or `comment_id` is not NULL.*
*   **`comments`**: Stores comments on posts.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `post_id` (UUID, FK to `posts.id`, NOT NULL)
    *   `author_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `content` (TEXT, NOT NULL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
*   **`notifications`**: Stores activity alerts.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `recipient_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `sender_id` (UUID, FK to `profiles.id`, OPTIONAL)
    *   `type` (ENUM 'like_post', 'comment_post', 'friend_request', 'message', etc., NOT NULL)
    *   `entity_id` (UUID, OPTIONAL, ID of the post, comment, or friend_request related to the notification)
    *   `message` (TEXT, human-readable notification text)
    *   `is_read` (BOOLEAN, DEFAULT FALSE)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
*   **`conversations`**: Represents a direct message conversation between two users.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `user1_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `user2_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `created_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   *Primary Key: `(user1_id, user2_id)` similar to friends table.*
*   **`messages`**: Stores individual messages within conversations.
    *   `id` (UUID, PK, DEFAULT gen_random_uuid())
    *   `conversation_id` (UUID, FK to `conversations.id`, NOT NULL)
    *   `sender_id` (UUID, FK to `profiles.id`, NOT NULL)
    *   `content` (TEXT, NOT NULL)
    *   `sent_at` (TIMESTAMPTZ, DEFAULT NOW())
    *   `is_read` (BOOLEAN, DEFAULT FALSE)

## 5. Feature-wise Implementation Details

### 5.1. User Authentication and Authorization

*   **Authentication:** Supabase Auth will be used for email/password-based user registration and login. The Antigravity UI will interact with Supabase client-side SDKs to handle authentication flows.
*   **Authorization:**
    *   Supabase Row Level Security (RLS) policies will be implemented on all tables to ensure users can only access/modify data they own or are authorized to see (e.g., friends' posts, their own comments).
    *   Supabase's JWTs will be automatically managed by the client SDK, providing authenticated requests to the backend.
*   **Profile Settings:** Frontend forms in Antigravity will allow users to update their `profiles` table entries (avatar, email, date of birth, app settings). Access will be restricted by RLS to the authenticated user's own profile.

### 5.2. User Profiles

*   **Creation/Viewing/Editing:** Antigravity UI components will render user profiles, allowing for form-based input for profile fields. Supabase Storage will be used for profile and cover photos, with public URLs stored in the `profiles` table.
*   **Friend List Display:** The profile view will query the `friends` table (or derived view) to display the list of connected friends.

### 5.3. Friend System

*   **Requests:** Antigravity UI will provide buttons to 