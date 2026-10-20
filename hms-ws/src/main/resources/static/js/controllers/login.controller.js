/**
 * Login & Sign-up Controller
 * PulseSquare / HealthB4U HMS - OpenAPI 3.0
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .controller('LoginController', ['$scope', '$window', '$timeout', 'AuthService', function ($scope, $window, $timeout, AuthService) {
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
                password: '',
                department: '',
                contactNumber: ''
            };

            $scope.availableRoles = [
                { id: 'PATIENT', label: 'Patient' },
                { id: 'DOCTOR', label: 'Doctor / Clinician' },
                { id: 'NURSE', label: 'Nurse / Ward Staff' },
                { id: 'RECEPTIONIST', label: 'Receptionist / Front Desk' },
                { id: 'PHARMACIST', label: 'Pharmacist' },
                { id: 'LAB_TECH', label: 'Lab Technician' },
                { id: 'ADMIN', label: 'Hospital Administrator' }
            ];

            $scope.isLoading = false;
            $scope.errorMessage = '';
            $scope.successMessage = '';
            $scope.showPassword = false;

            // Force-logout via ?logout=1 URL param (clears stuck sessions)
            var urlParams = new URLSearchParams($window.location.search);
            if (urlParams.get('logout') === '1') {
                AuthService.clearSession();
            }

            // Auto-redirect to dashboard if already logged in
            if (AuthService.isLoggedIn()) {
                $window.location.href = 'dashboard.html';
                return;
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

            $scope.resetForm = function () {
                $scope.loginData.username = '';
                $scope.loginData.password = '';
                $scope.errorMessage = '';
                $scope.successMessage = '';
            };

            $scope.fillDemo = function (username, password) {
                $scope.activeView = 'login';
                $scope.loginData.username = username;
                $scope.loginData.password = password;
                $scope.errorMessage = '';
                $scope.successMessage = 'Demo credentials loaded for ' + username + '. Click Login to continue.';
            };

            $scope.submitLogin = function (isValid) {
                $scope.errorMessage = '';
                $scope.successMessage = '';

                if (!isValid) {
                    $scope.errorMessage = 'Please provide both username and password.';
                    return;
                }

                $scope.isLoading = true;

                AuthService.login($scope.loginData.username, $scope.loginData.password, $scope.loginData.rememberMe)
                    .then(function (data) {
                        $scope.isLoading = false;
                        $scope.successMessage = 'Login successful! Redirecting to HMS Dashboard...';

                        $timeout(function () {
                            $window.location.href = 'dashboard.html';
                        }, 750);
                    })
                    .catch(function (error) {
                        $scope.isLoading = false;
                        if (error.data && error.data.message) {
                            $scope.errorMessage = error.data.message;
                        } else if (error.status === 401) {
                            $scope.errorMessage = 'Invalid username or password.';
                        } else {
                            $scope.errorMessage = 'Authentication request failed. Please check server connection.';
                        }
                    });
            };

            $scope.submitSignup = function (isValid) {
                $scope.errorMessage = '';
                $scope.successMessage = '';

                if (!isValid) {
                    $scope.errorMessage = 'Please fill all required registration fields correctly.';
                    return;
                }

                $scope.isLoading = true;

                AuthService.register($scope.signupData)
                    .then(function (response) {
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
                        } else if (error.data && error.data.validationErrors) {
                            var fieldMsgs = Object.values(error.data.validationErrors).join(', ');
                            $scope.errorMessage = 'Validation error: ' + fieldMsgs;
                        } else {
                            $scope.errorMessage = 'Registration failed. Please check details or server connection.';
                        }
                    });
            };
        }]);
})();
