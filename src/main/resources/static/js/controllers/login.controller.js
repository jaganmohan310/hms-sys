/**
 * Login & Sign-up Controller
 * Indus Super Speciality Hospital / HealthB4U HMS
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

            $scope.resetForm = function () {
                $scope.loginData.username = '';
                $scope.loginData.password = '';
                $scope.errorMessage = '';
                $scope.successMessage = '';
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
                        $scope.successMessage = 'Login successful! Redirecting to HMS Dashboard...';

                        if ($scope.loginData.rememberMe) {
                            localStorage.setItem('pulsesquare_user', JSON.stringify(AuthService.getCurrentUser()));
                            localStorage.setItem('pulsesquare_token', AuthService.getToken());
                        }

                        $timeout(function () {
                            $window.location.href = 'dashboard.html';
                        }, 800);
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
})();
