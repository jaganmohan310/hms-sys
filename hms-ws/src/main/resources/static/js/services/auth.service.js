/**
 * Enterprise Authentication & Session Management Service
 * PulseSquare / HealthB4U HMS - OpenAPI 3.0 Integration
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('AuthService', ['$http', function ($http) {
            var service = {};
            var STORAGE_KEY_USER = 'pulsesquare_user';
            var STORAGE_KEY_TOKEN = 'pulsesquare_token';

            /**
             * Authenticate user credentials against OpenAPI endpoint
             */
            service.login = function (username, password, rememberMe) {
                return $http.post('/api/v1/auth/login', {
                    username: username,
                    password: password,
                    rememberMe: !!rememberMe
                }).then(function (response) {
                    if (response.data && response.data.success) {
                        service.saveSession(response.data, rememberMe);
                    }
                    return response.data;
                });
            };

            /**
             * Register new staff member or patient against OpenAPI endpoint
             */
            service.register = function (userData) {
                return $http.post('/api/v1/auth/signup', userData).then(function (response) {
                    return response.data;
                });
            };

            /**
             * Retrieve profile of currently authenticated user using JWT
             */
            service.getCurrentUserProfile = function () {
                var token = service.getToken();
                var headers = {};
                if (token) {
                    headers['Authorization'] = 'Bearer ' + token;
                }
                return $http.get('/api/v1/auth/me', { headers: headers }).then(function (response) {
                    return response.data;
                });
            };

            /**
             * End active user session and record audit log.
             * Always clears local session even if server call fails.
             */
            service.logout = function () {
                var token = service.getToken();
                var headers = {};
                if (token) {
                    headers['Authorization'] = 'Bearer ' + token;
                }
                return $http.post('/api/v1/auth/logout', {}, { headers: headers })
                    .finally(function () {
                        service.clearSession();
                        window.location.href = 'login.html';
                    });
            };

            /**
             * Persist session context (JWT + User Profile)
             */
            service.saveSession = function (authData, rememberMe) {
                var userProfile = authData.user || {
                    username: authData.username,
                    fullName: authData.fullName,
                    role: authData.role,
                    email: authData.email
                };

                var serializedUser = JSON.stringify(userProfile);
                var token = authData.token;

                // Persist in sessionStorage for session scope
                sessionStorage.setItem(STORAGE_KEY_USER, serializedUser);
                if (token) {
                    sessionStorage.setItem(STORAGE_KEY_TOKEN, token);
                }

                // If remember me is requested, also persist to localStorage
                if (rememberMe) {
                    localStorage.setItem(STORAGE_KEY_USER, serializedUser);
                    if (token) {
                        localStorage.setItem(STORAGE_KEY_TOKEN, token);
                    }
                }
            };

            service.getCurrentUser = function () {
                var userJson = sessionStorage.getItem(STORAGE_KEY_USER) || localStorage.getItem(STORAGE_KEY_USER);
                return userJson ? JSON.parse(userJson) : null;
            };

            service.getToken = function () {
                return sessionStorage.getItem(STORAGE_KEY_TOKEN) || localStorage.getItem(STORAGE_KEY_TOKEN);
            };

            service.isLoggedIn = function () {
                return !!service.getToken();
            };

            service.clearSession = function () {
                sessionStorage.removeItem(STORAGE_KEY_USER);
                sessionStorage.removeItem(STORAGE_KEY_TOKEN);
                localStorage.removeItem(STORAGE_KEY_USER);
                localStorage.removeItem(STORAGE_KEY_TOKEN);
            };

            return service;
        }])
        .config(['$httpProvider', function ($httpProvider) {
            // Enterprise HTTP Interceptor to automatically attach JWT Bearer token
            $httpProvider.interceptors.push(function () {
                return {
                    request: function (config) {
                        var token = sessionStorage.getItem('pulsesquare_token') || localStorage.getItem('pulsesquare_token');
                        if (token && !config.headers['Authorization']) {
                            config.headers['Authorization'] = 'Bearer ' + token;
                        }
                        return config;
                    }
                };
            });
        }]);
})();
