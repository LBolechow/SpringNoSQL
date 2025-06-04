package com.lukbol.ProjectNoSQL.Utils;

public class SecurityEndpoints {
    public static final String[] PUBLIC_ENDPOINTS = {
            "/user/register", "/login", "/loginPage", "/",
            "/user/resetPasswordEmail/**", "/user/resetSite",
            "/user/resetPassword", "/activate/**",
            "/registerPage", "/error"
    };

    public static final String[] ADMIN_ENDPOINTS = {
            "/project/createProject",
            "/project/updateProject/{id}",
            "/project/deleteProject/{id}"
    };

    public static final String[] CLIENT_AND_ADMIN_ENDPOINTS = {
            "/user/deleteUser", "/user/apply", "/userDetails",
            "/user/login-history", "/user/logout",
            "/project/getAllProjects", "/project/getProjectById/{id}",
            "/task/getAllTasks", "/task/getTaskById/{id}",
            "/task/createTask", "/task/updateTask/{id}", "/task/deleteTask/{id}",
            "/task/by-project/{projectId}",
            "/attachment/upload", "/attachment/getAll",
            "/attachment/download/{id}", "/attachment/delete/{id}",
            "/attachment/by-task/{taskId}", "/user/getAllUsers", "/user/getUserById/{id}",
            "/user/byTask/{taskId}", "/user/byProject/{projectId}"
    };
}
