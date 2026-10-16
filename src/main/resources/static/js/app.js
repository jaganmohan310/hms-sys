/**
 * PulseSquare Healthcare Management System (HMS)
 * AngularJS Application Module
 */
(function () {
    'use strict';

    var app = angular.module('hmsApp', ['ngAnimate', 'ngMessages']);

    // -------------------------------------------------------------
    // Authentication Service
    // -------------------------------------------------------------
    app.factory('AuthService', ['$http', function ($http) {
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

    // -------------------------------------------------------------
    // Login & Registration Controller
    // -------------------------------------------------------------
    app.controller('LoginController', ['$scope', '$http', '$window', '$timeout', 'AuthService', function ($scope, $http, $window, $timeout, AuthService) {
        $scope.activeView = 'login'; // 'login' or 'signup'

        $scope.loginData = {
            username: '',
            password: '',
            rememberMe: false
        };

        $scope.signupData = {
            role: 'PATIENT',
            fullName: '',
            email: '',
            username: '',
            password: ''
        };

        $scope.isLoading = false;
        $scope.errorMessage = '';
        $scope.successMessage = '';
        $scope.showPassword = false;

        // Check if user is already logged in
        var existingUser = AuthService.getCurrentUser();
        if (existingUser && AuthService.isLoggedIn()) {
            $scope.currentUser = existingUser;
        }

        $scope.setView = function (view) {
            $scope.activeView = view;
            $scope.errorMessage = '';
            $scope.successMessage = '';
        };

        $scope.selectRole = function (role) {
            $scope.signupData.role = role;
        };

        $scope.togglePasswordVisibility = function () {
            $scope.showPassword = !$scope.showPassword;
        };

        $scope.submitLogin = function (isValid) {
            $scope.errorMessage = '';
            $scope.successMessage = '';

            if (!isValid) {
                $scope.errorMessage = 'Please provide both username and password.';
                return;
            }

            $scope.isLoading = true;

            AuthService.login($scope.loginData.username, $scope.loginData.password)
                .then(function (data) {
                    $scope.isLoading = false;
                    $scope.successMessage = 'Login successful! Redirecting to PulseSquare Portal...';

                    if ($scope.loginData.rememberMe) {
                        localStorage.setItem('pulsesquare_user', JSON.stringify(AuthService.getCurrentUser()));
                        localStorage.setItem('pulsesquare_token', AuthService.getToken());
                    }

                    $timeout(function () {
                        $window.location.href = '/dashboard';
                    }, 800);
                })
                .catch(function (error) {
                    $scope.isLoading = false;
                    if (error.data && error.data.message) {
                        $scope.errorMessage = error.data.message;
                    } else if (error.status === 401) {
                        $scope.errorMessage = 'Invalid username or password.';
                    } else {
                        $scope.errorMessage = 'Authentication request failed. Please check server logs.';
                    }
                });
        };

        $scope.submitSignup = function (isValid) {
            $scope.errorMessage = '';
            $scope.successMessage = '';

            if (!isValid) {
                $scope.errorMessage = 'Please fill all required registration fields.';
                return;
            }

            $scope.isLoading = true;

            AuthService.register($scope.signupData)
                .then(function (data) {
                    $scope.isLoading = false;
                    $scope.successMessage = 'Account created successfully! Please sign in with your credentials.';
                    $scope.loginData.username = $scope.signupData.username;
                    $scope.loginData.password = '';
                    $scope.activeView = 'login';
                })
                .catch(function (error) {
                    $scope.isLoading = false;
                    if (error.data && error.data.message) {
                        $scope.errorMessage = error.data.message;
                    } else {
                        $scope.errorMessage = 'Registration failed. Please check details or server connection.';
                    }
                });
        };
    }]);

    // -------------------------------------------------------------
    // Dashboard Controller
    // -------------------------------------------------------------
    app.controller('DashboardController', ['$scope', '$window', 'AuthService', function ($scope, $window, AuthService) {
        $scope.user = AuthService.getCurrentUser();

        // Redirect to login if unauthenticated
        if (!$scope.user) {
            $window.location.href = '/login';
            return;
        }

        $scope.activeTab = 'overview';

        $scope.stats = {
            todayAppointments: 42,
            activeDoctors: 18,
            inpatients: 124,
            availableBeds: 36
        };

        $scope.recentPatients = [
            { id: 'P-1082', name: 'Robert Fox', age: 45, doctor: 'Dr. Rajesh Sharma', department: 'Cardiology', status: 'In Consultation', time: '10:30 AM' },
            { id: 'P-1083', name: 'Emily Davis', age: 29, doctor: 'Dr. Ananya Roy', department: 'Gynecology', status: 'Waiting', time: '10:45 AM' },
            { id: 'P-1084', name: 'Michael Brown', age: 62, doctor: 'Dr. Vikrant Mehta', department: 'Neurology', status: 'Completed', time: '09:15 AM' },
            { id: 'P-1085', name: 'Sophia Miller', age: 8, doctor: 'Dr. Priya Joshi', department: 'Pediatrics', status: 'Admitted', time: '08:00 AM' }
        ];

        $scope.logout = function () {
            AuthService.logout().then(function () {
                $window.location.href = '/login';
            });
        };
    }]);

})();
