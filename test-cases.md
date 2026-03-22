## [TC-US001-001] Verify creation of a personal profile with mandatory fields
**Preconditions:** User is authenticated and has navigated to the profile creation page.

**Steps:**
1. Enter a valid 'Name'.
2. Enter text into the 'Bio' field.
3. Upload a 'Profile Photo'.
4. Click 'Save Profile' or equivalent button.

**Expected Result:** Profile is successfully created. The profile page displays the entered Name, Bio, Profile Photo, and an automatically recorded Join Date. The friend list is empty.

---

## [TC-US001-002] Verify creation of a personal profile with all optional fields
**Preconditions:** User is authenticated and has navigated to the profile creation page.

**Steps:**
1. Enter a valid 'Name'.
2. Enter text into the 'Bio' field.
3. Upload a 'Profile Photo'.
4. Upload a 'Cover Photo'.
5. Specify a 'Location'.
6. Click 'Save Profile' or equivalent button.

**Expected Result:** Profile is successfully created. The profile page displays all entered Name, Bio, Profile Photo, Cover Photo, Location, and an automatically recorded Join Date. The friend list is empty.

---

## [TC-US002-001] Verify viewing own profile with all fields populated
**Preconditions:** User is authenticated and has a complete profile (Name, Profile Photo, Bio, Join Date, Cover Photo, Location) and at least one friend.

**Steps:**
1. Navigate to 'My Profile' section.

**Expected Result:** The user's profile is displayed, showing Name, Profile Photo, Bio, Join Date, Cover Photo, Location, and the current list of friends.

---

## [TC-US002-002] Verify viewing own profile with only mandatory fields populated
**Preconditions:** User is authenticated and has a basic profile (Name, Profile Photo, Bio, Join Date) and no friends.

**Steps:**
1. Navigate to 'My Profile' section.

**Expected Result:** The user's profile is displayed, showing Name, Profile Photo, Bio, Join Date. Cover Photo and Location fields are not displayed or shown as empty. The friend list is empty.

---

## [TC-US003-001] Verify updating all profile information fields
**Preconditions:** User is authenticated and has an existing profile.

**Steps:**
1. Navigate to 'Edit Profile' section.
2. Update 'Name' to a new value.
3. Update 'Bio' to new text.
4. Upload a new 'Profile Photo'.
5. Upload a new 'Cover Photo'.
6. Update 'Location' to a new value.
7. Click 'Save Changes' or equivalent button.

**Expected Result:** All changes are successfully saved. The updated Name, Profile Photo, Bio, Cover Photo, and Location are immediately reflected on the user's profile page.

---

## [TC-US003-002] Verify updating a single profile information field
**Preconditions:** User is authenticated and has an existing profile.

**Steps:**
1. Navigate to 'Edit Profile' section.
2. Update 'Bio' to new text.
3. Click 'Save Changes' or equivalent button.

**Expected Result:** The Bio field is successfully updated. Only the Bio field reflects the new value on the user's profile page, other fields remain unchanged.

---

## [TC-US004-001] Verify viewing another user's profile (friend)
**Preconditions:** User is authenticated and is friends with 'UserB'. 'UserB' has a complete profile (Name, Profile Photo, Bio, Join Date, Cover Photo, Location) and at least one friend.

**Steps:**
1. Navigate to 'UserB's profile via friend list or search.

**Expected Result:** UserB's profile is displayed, showing their Name, Profile Photo, Bio, Join Date, Cover Photo, Location, and their current list of friends.

---

## [TC-US004-002] Verify viewing another user's profile (non-friend)
**Preconditions:** User is authenticated. 'UserC' has a complete profile (Name, Profile Photo, Bio, Join Date, Cover Photo, Location) and is not friends with the current user.

**Steps:**
1. Search for 'UserC'.
2. Navigate to 'UserC's profile from search results.

**Expected Result:** UserC's profile is displayed, showing their Name, Profile Photo, Bio, Join Date, Cover Photo, Location, and their current list of friends. A 'Send Friend Request' button is visible.

---

## [TC-US005-001] Verify sending a friend request to another user
**Preconditions:** User is authenticated. 'UserD' exists and is not a friend.

**Steps:**
1. Search for 'UserD'.
2. Navigate to 'UserD's profile.
3. Click the 'Send Friend Request' button.

**Expected Result:** A confirmation message indicates the friend request has been sent. 'UserD' receives a notification about the friend request.

---

## [TC-US006-001] Verify accepting a friend request
**Preconditions:** User is authenticated and has received a friend request from 'UserE'.

**Steps:**
1. Navigate to the 'Notifications' or 'Friend Requests' section.
2. Locate the pending friend request from 'UserE'.
3. Click the 'Accept' button for 'UserE's request.

**Expected Result:** The friend request from 'UserE' is accepted. 'UserE' is added to the user's friend list, and the user is added to 'UserE's friend list. The notification is dismissed.

---

## [TC-US007-001] Verify declining a friend request
**Preconditions:** User is authenticated and has received a friend request from 'UserF'.

**Steps:**
1. Navigate to the 'Notifications' or 'Friend Requests' section.
2. Locate the pending friend request from 'UserF'.
3. Click the 'Decline' button for 'UserF's request.

**Expected Result:** The friend request from 'UserF' is declined. 'UserF' is not added to the user's friend list. The notification is dismissed.

---

## [TC-US008-001] Verify viewing a list of friends
**Preconditions:** User is authenticated and has at least two friends, 'FriendA' and 'FriendB'.

**Steps:**
1. Navigate to the dedicated 'Friends' section of the application.

**Expected Result:** A list is displayed showing 'FriendA' and 'FriendB', each with their name and profile photo. All accepted friends are visible.

---

## [TC-US009-001] Verify removing a friend from the friend list
**Preconditions:** User is authenticated and is friends with 'FriendC'.

**Steps:**
1. Navigate to the dedicated 'Friends' section.
2. Locate 'FriendC' in the list.
3. Click the 'Remove Friend' option next to 'FriendC's entry.
4. Confirm the removal if prompted.

**Expected Result:** 'FriendC' is removed from the user's friend list. The user is also removed from 'FriendC's friend list.

---

## [TC-US010-001] Verify creating a text-only post
**Preconditions:** User is authenticated.

**Steps:**
1. Navigate to the 'Create Post' section.
2. Enter text content into the post editor.
3. Click 'Post'.

**Expected Result:** The text-only post is successfully created and appears on the user's own feed and the feeds of their friends.

---

## [TC-US010-002] Verify creating a post with an image and optional text
**Preconditions:** User is authenticated.

**Steps:**
1. Navigate to the 'Create Post' section.
2. Upload an image file.
3. Optionally, enter text content into the post editor.
4. Click 'Post'.

**Expected Result:** The post with the image (and optional text) is successfully created and appears on the user's own feed and the feeds of their friends.

---

## [TC-US010-003] Verify creating a post with a link and optional text
**Preconditions:** User is authenticated.

**Steps:**
1. Navigate to the 'Create Post' section.
2. Paste a valid URL into the post editor.
3. Optionally, enter additional text content.
4. Click 'Post'.

**Expected Result:** The post with the link (and optional text) is successfully created and appears on the user's own feed and the feeds of their friends. The link should be clickable.

---

## [TC-US011-001] Verify feed displays posts only from friends in chronological order
**Preconditions:** User is authenticated. 'Friend1' and 'Friend2' have both posted, and a 'NonFriend' has also posted. 'Friend2' posted more recently than 'Friend1'.

**Steps:**
1. Navigate to the main 'Feed' section.

**Expected Result:** The feed displays posts only from 'Friend1' and 'Friend2'. Posts from 'NonFriend' are not visible. 'Friend2's post appears above 'Friend1's post. All post content (text, image, link) is fully visible.

---

## [TC-US012-001] Verify liking a friend's post
**Preconditions:** User is authenticated and has a friend ('PostCreator') who has an existing post on the feed. The user has not yet liked this post.

**Steps:**
1. Locate a post by 'PostCreator' on the feed.
2. Click the 'Like' button on the post.

**Expected Result:** The 'Like' button changes state (e.g., color, icon). The post's like count increments by one. 'PostCreator' receives a notification that the user liked their post.

---

## [TC-US013-001] Verify adding a comment to a friend's post
**Preconditions:** User is authenticated and has a friend ('PostCreator') who has an existing post on the feed.

**Steps:**
1. Locate a post by 'PostCreator' on the feed.
2. Click on the 'Comment' icon or field.
3. Type a comment into the input field.
4. Click 'Submit Comment' or press Enter.

**Expected Result:** The comment is successfully added and appears beneath the post, attributed to the current user. 'PostCreator' receives a notification that the user commented on their post.

---

## [TC-US014-001] Verify liking a comment
**Preconditions:** User is authenticated. There is a post with an existing comment by 'Commenter' on the feed. The user has not yet liked this comment.

**Steps:**
1. Locate a comment by 'Commenter' on a post.
2. Click the 'Like' button on the comment.

**Expected Result:** The 'Like' button for the comment changes state. The comment's like count increments by one. 'Commenter' receives a notification that the user liked their comment.

---

## [TC-US015-001] Verify deleting own comment on a post
**Preconditions:** User is authenticated and has previously posted a comment on a friend's post.

**Steps:**
1. Locate the user's own comment on a post.
2. Click the 'Delete' option (e.g., trash icon, ellipsis menu > Delete).
3. Confirm deletion if prompted.

**Expected Result:** The comment is successfully removed from the post and is no longer visible to any users.

---

## [TC-US015-002] Verify user cannot delete another user's comment
**Preconditions:** User is authenticated. There is a comment posted by 'AnotherUser' on a friend's post.

**Steps:**
1. Locate a comment posted by 'AnotherUser' on a post.
2. Attempt to find and click a 'Delete' option for 'AnotherUser's comment.

**Expected Result:** No 'Delete' option is available or visible for comments posted by 'AnotherUser'.

---

## [TC-US016-001] Verify receiving notification for a post like
**Preconditions:** User is authenticated and has an existing post. 'FriendX' likes the user's post.

**Steps:**
1. Log in as 'FriendX' and like the user's post.
2. Log in as the original user.
3. Navigate to the 'Notifications' section.

**Expected Result:** A notification is displayed in the 'Notifications' section stating that 'FriendX' liked the user's post.

---

## [TC-US016-002] Verify receiving notification for a post comment
**Preconditions:** User is authenticated and has an existing post. 'FriendY' comments on the user's post.

**Steps:**
1. Log in as 'FriendY' and comment on the user's post.
2. Log in as the original user.
3. Navigate to the 'Notifications' section.

**Expected Result:** A notification is displayed in the 'Notifications' section stating that 'FriendY' commented on the user's post.

---

## [TC-US016-003] Verify receiving notification for a friend request
**Preconditions:** User is authenticated. 'UserZ' sends a friend request to the user.

**Steps:**
1. Log in as 'UserZ' and send a friend request to the user.
2. Log in as the original user.
3. Navigate to the 'Notifications' section.

**Expected Result:** A notification is displayed in the 'Notifications' section stating that 'UserZ' sent a friend request.

---

## [TC-US017-001] Verify sending a direct message to a friend
**Preconditions:** User is authenticated and is friends with 'FriendM'.

**Steps:**
1. Navigate to 'FriendM's profile or the 'Messages' section.
2. Initiate a new message conversation with 'FriendM'.
3. Type a message (e.g., 'Hello FriendM!').
4. Click 'Send'.

**Expected Result:** The message is successfully sent and appears in the conversation window. 'FriendM' receives the message in real-time in their messaging interface.

---

## [TC-US018-001] Verify viewing a message conversation with a friend
**Preconditions:** User is authenticated and has an existing message conversation history with 'FriendN'.

**Steps:**
1. Navigate to the 'Messages' section.
2. Select the conversation with 'FriendN'.

**Expected Result:** The entire message history with 'FriendN' is displayed, with messages appearing in chronological order (oldest at top, newest at bottom).

---

## [TC-US019-001] Verify successful user registration with valid credentials
**Preconditions:** User is on the sign-up page.

**Steps:**
1. Enter a unique and valid email address.
2. Enter a strong password (meeting complexity requirements).
3. Confirm the password.
4. Click 'Sign Up' or 'Register'.

**Expected Result:** User is successfully registered, authenticated, and redirected to the profile creation page (US001).

---

## [TC-US019-002] Verify user registration with an already registered email
**Preconditions:** User is on the sign-up page. An account with 'existing@example.com' already exists.

**Steps:**
1. Enter 'existing@example.com'.
2. Enter a password.
3. Confirm the password.
4. Click 'Sign Up'.

**Expected Result:** An error message is displayed indicating that the email address is already registered. User remains on the sign-up page.

---

## [TC-US019-003] Verify user registration with invalid email format
**Preconditions:** User is on the sign-up page.

**Steps:**
1. Enter an invalid email address (e.g., 'invalid-email', 'test@').
2. Enter a password.
3. Confirm the password.
4. Click 'Sign Up'.

**Expected Result:** An error message is displayed indicating an invalid email format. User remains on the sign-up page.

---

## [TC-US020-001] Verify successful user login with valid credentials
**Preconditions:** User has a registered account (e.g., 'test@example.com', 'password123') and is on the login page.

**Steps:**
1. Enter 'test@example.com' into the email field.
2. Enter 'password123' into the password field.
3. Click 'Log In' button.

**Expected Result:** User is successfully logged in and redirected to their feed or profile page.

---

## [TC-US020-002] Verify user login with incorrect password
**Preconditions:** User has a registered account and is on the login page.

**Steps:**
1. Enter the correct email address.
2. Enter an incorrect password.
3. Click 'Log In' button.

**Expected Result:** An error message is displayed indicating invalid credentials (e.g., 'Incorrect email or password'). User remains on the login page.

---

## [TC-US020-003] Verify user login with unregistered email
**Preconditions:** User is on the login page.

**Steps:**
1. Enter an email address not associated with any account.
2. Enter any password.
3. Click 'Log In' button.

**Expected Result:** An error message is displayed indicating invalid credentials (e.g., 'Incorrect email or password'). User remains on the login page.

---

## [TC-US021-001] Verify updating avatar (profile photo) in settings
**Preconditions:** User is authenticated and has navigated to the 'Profile Settings' page.

**Steps:**
1. Click on the option to change 'Avatar' or 'Profile Photo'.
2. Upload a new image file.
3. Click 'Save Changes'.

**Expected Result:** The avatar is successfully updated and reflected on the profile and other relevant areas.

---

## [TC-US021-002] Verify updating email address in settings
**Preconditions:** User is authenticated and has navigated to the 'Profile Settings' page.

**Steps:**
1. Enter a new, unique, and valid email address into the 'Email' field.
2. Enter current password for verification (if required).
3. Click 'Save Changes'.

**Expected Result:** The email address is successfully updated. User might need to re-verify the new email or re-login. A confirmation message is displayed.

---

## [TC-US021-003] Verify updating date of birth in settings
**Preconditions:** User is authenticated and has navigated to the 'Profile Settings' page.

**Steps:**
1. Select a new 'Date of Birth' from the date picker.
2. Click 'Save Changes'.

**Expected Result:** The date of birth is successfully updated and reflected in the profile data.

---

## [TC-US021-004] Verify adjusting notification preferences in settings
**Preconditions:** User is authenticated and has navigated to the 'Application Settings' or 'Notification Settings' page.

**Steps:**
1. Toggle a specific notification preference (e.g., 'Receive email for likes') from ON to OFF.
2. Toggle another preference (e.g., 'Receive push notifications for comments') from OFF to ON.
3. Click 'Save Changes'.

**Expected Result:** The notification preferences are successfully updated. Future notifications will adhere to the new settings.

---

## [TC-US022-001] Verify UI responsiveness and functionality on desktop
**Preconditions:** User is authenticated and accessing the app via a desktop web browser (e.g., Chrome).

**Steps:**
1. Navigate through various pages (Feed, Profile, Messages, Settings).
2. Resize the browser window from full-width to a smaller desktop size.
3. Interact with features (post, like, comment, send message).

**Expected Result:** The UI elements adapt gracefully to different desktop screen sizes without breaking layouts or losing functionality. All features remain accessible and usable.

---

## [TC-US022-002] Verify UI responsiveness and functionality on tablet
**Preconditions:** User is authenticated and accessing the app via a tablet device (e.g., iPad, Android tablet) in both portrait and landscape orientations.

**Steps:**
1. Navigate through various pages (Feed, Profile, Messages, Settings).
2. Switch between portrait and landscape orientations.
3. Interact with features (post, like, comment, send message).

**Expected Result:** The UI elements adapt gracefully to tablet screen sizes and orientations without breaking layouts or losing functionality. All features remain accessible and usable.

---

## [TC-US022-003] Verify UI responsiveness and functionality on smartphone
**Preconditions:** User is authenticated and accessing the app via a smartphone device (e.g., iPhone, Android phone) in both portrait and landscape orientations.

**Steps:**
1. Navigate through various pages (Feed, Profile, Messages, Settings).
2. Switch between portrait and landscape orientations.
3. Interact with features (post, like, comment, send message).

**Expected Result:** The UI elements adapt gracefully to smartphone screen sizes and orientations without breaking layouts or losing functionality. All features remain accessible and usable.

---

## [TC-US023-001] Verify viewing previously loaded content when offline
**Preconditions:** User is authenticated and has recently viewed their feed with several posts. The device has an active internet connection.

**Steps:**
1. Navigate to the main 'Feed' to ensure content is loaded.
2. Disable the device's internet connection (Wi-Fi/mobile data).
3. Attempt to scroll through the feed or navigate to a cached profile.

**Expected Result:** The previously loaded feed posts and profile information are viewable. The app displays a clear visual indicator (e.g., banner, icon) that it is currently offline.

---

## [TC-US023-002] Verify app behavior when attempting actions offline
**Preconditions:** User is authenticated. The device is offline. Previously loaded content is visible.

**Steps:**
1. Attempt to 'Like' a post.
2. Attempt to 'Comment' on a post.
3. Attempt to send a 'Direct Message'.

**Expected Result:** The app prevents the actions from being completed. An appropriate message is displayed (e.g., 'No internet connection', 'Action failed, please try again when online').

---

## [TC-US024-001] Verify prompt for push notification permission
**Preconditions:** User is a new user or has not yet granted push notification permission.

**Steps:**
1. Launch the application for the first time or navigate to 'Notification Settings' if not prompted automatically.

**Expected Result:** A system-level dialog or in-app prompt appears, asking the user to allow push notifications.

---

## [TC-US024-002] Verify receiving and clicking a push notification for a post like
**Preconditions:** User is authenticated and has enabled push notifications. The app is in the background or closed. 'FriendA' likes the user's post.

**Steps:**
1. Ensure 'FriendA' likes the user's post.
2. Observe the device's notification tray/bar.

**Expected Result:** A push notification appears on the device (even if the app is closed) stating 'FriendA liked your post'. Clicking the notification opens the app and navigates directly to the liked post or the notifications section.

---

## [TC-US024-003] Verify receiving and clicking a push notification for a friend request
**Preconditions:** User is authenticated and has enabled push notifications. The app is in the background or closed. 'UserB' sends a friend request.

**Steps:**
1. Ensure 'UserB' sends a friend request to the user.
2. Observe the device's notification tray/bar.

**Expected Result:** A push notification appears on the device stating 'UserB sent you a friend request'. Clicking the notification opens the app and navigates directly to the friend requests section.

---

## [TC-US025-001] Verify initial application loading time
**Preconditions:** Application is not running or has been recently force-closed. Device has a stable internet connection.

**Steps:**
1. Launch the application.
2. Measure the time from launch to the display of the first interactive content (e.g., login screen, feed).

**Expected Result:** The application should load within acceptable industry standards (e.g., under 3-5 seconds for initial load, depending on complexity).

---

## [TC-US025-002] Verify subsequent application loading and content display speed
**Preconditions:** User is authenticated. Application has been opened before.

**Steps:**
1. Close the app (not force-close, allow background processes if applicable).
2. Relaunch the application.
3. Measure time to full content display on the feed.
4. Navigate to other sections (profile, messages) and measure content display time.

**Expected Result:** The application should load significantly faster on subsequent visits (e.g., under 1-2 seconds). Content within sections should appear rapidly without noticeable delays or spinners, providing a smooth user experience.