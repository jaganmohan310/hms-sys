/**
 * Authentication & Session Management Service
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('AuthService', ['$http', function ($http) {
            var service = {};
            var STORAGE_KEY_USER = 'pulsesquare_user';
            var STORAGE_KEY_TOKEN = 'pulsesquare_token';

            service.login = function (username, password) {
                return $http.post('/api/auth/login', {
                    username: username,
                    password: password
                }).then(function (response) {
                    if (response.data && response.data.success) {
                        service.saveSession(response.data);
                    }
                    return response.data;
                });
            };

            service.register = function (userData) {
                return $http.post('/api/auth/register', userData).then(function (response) {
                    return response.data;
                });
            };

            service.logout = function () {
                return $http.post('/api/auth/logout', {}).finally(function () {
                    service.clearSession();
                });
            };

            service.saveSession = function (authData) {
                var userData = {
                    username: authData.username,
                    fullName: authData.fullName,
                    role: authData.role,
                    email: authData.email
                };
                sessionStorage.setItem(STORAGE_KEY_USER, JSON.stringify(userData));
                sessionStorage.setItem(STORAGE_KEY_TOKEN, authData.token);
            };

            service.getCurrentUser = function () {
                var userJson = sessionStorage.getItem(STORAGE_KEY_USER);
                if (!userJson) {
                    userJson = localStorage.getItem(STORAGE_KEY_USER);
                }
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
        }]);
})();
