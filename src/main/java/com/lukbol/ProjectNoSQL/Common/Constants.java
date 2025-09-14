package com.lukbol.ProjectNoSQL.Common;


public final class Constants {

    private Constants() {}

    public static final String USER_NOT_FOUND = "User not found with username: %s";
    public static final String USER_NOT_FOUND_BY_EMAIL = "No user found with this email address.";
    public static final String EMAIL_ALREADY_EXISTS = "A user with this email address already exists.";
    public static final String USERNAME_ALREADY_EXISTS = "A user with this username already exists.";
    public static final String PHONE_ALREADY_EXISTS = "A user with this phone number already exists.";
    public static final String INVALID_PASSWORD = "The password must meet the security requirements.";
    public static final String PASSWORDS_DO_NOT_MATCH = "Passwords do not match.";
    public static final String PASSWORD_SAME_AS_OLD = "The new password is the same as the old one.";
    public static final String EMPTY_FIELDS = "All fields must be filled.";
    public static final String AUTHENTICATED_SUCCESSFULLY = "Successfully authenticated. Token: %s";
    public static final String ACCOUNT_CREATED = "Account created successfully.";
    public static final String PROFILE_UPDATED = "Profile updated successfully.";
    public static final String ACCOUNT_DELETED = "Account deleted successfully.";
    public static final String LOGGED_OUT = "Logged out successfully.";
    public static final String TOKEN_NOT_FOUND = "Token not found.";

    public static final String USER_NOT_AUTHORIZED = "User is not authorized!";


}
