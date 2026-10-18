/**
 * Patient Management & Lookup Service
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('PatientService', function () {
            var service = {};

            // Doctor directory
            service.doctors = {
                'Dr. Rajesh Sharma': { dept: 'Cardiology', spec: 'Cardiologist', fee: 500 },
                'Dr. Ananya Roy': { dept: 'Gynecology', spec: 'General Physician', fee: 450 },
                'Dr. Vikrant Mehta': { dept: 'Neurology', spec: 'Neurologist', fee: 600 },
                'Dr. Priya Joshi': { dept: 'Pediatrics', spec: 'Pediatrician', fee: 400 },
                'Dr. Ramesh Kumar': { dept: 'Orthopedics', spec: 'Orthopedic Surgeon', fee: 550 },
                'Dr. Sneha Reddy': { dept: 'General Medicine', spec: 'General Physician', fee: 350 }
            };

            // Patient Queue / Master Records
            service.patients = [
                {
                    id: 'OP-4102',
                    mrNo: 'MR-9021',
                    billNo: 'B-7701',
                    name: 'Robert Fox',
                    firstName: 'Robert',
                    lastName: 'Fox',
                    mobile: '9876543210',
                    gender: 'Male',
                    age: '45',
                    address: 'Plot 14, Housing Board Colony, Nizamabad',
                    dept: 'General Medicine',
                    doctor: 'Dr. Rajesh Sharma',
                    totalAmt: 500,
                    discount: 0,
                    netAmt: 500,
                    receivedAmt: 200,
                    dueAmt: 300,
                    status: 'In Consultation',
                    time: '04:15 PM'
                },
                {
                    id: 'OP-4103',
                    mrNo: 'MR-9022',
                    billNo: 'B-7702',
                    name: 'Emily Davis',
                    firstName: 'Emily',
                    lastName: 'Davis',
                    mobile: '9848012345',
                    gender: 'Female',
                    age: '29',
                    address: 'Flat 202, Sri Krishna Towers, Nizamabad',
                    dept: 'Cardiology',
                    doctor: 'Dr. Rajesh Sharma',
                    totalAmt: 600,
                    discount: 60,
                    netAmt: 540,
                    receivedAmt: 400,
                    dueAmt: 140,
                    status: 'Waiting',
                    time: '04:30 PM'
                },
                {
                    id: 'OP-4104',
                    mrNo: 'MR-9023',
                    billNo: 'B-7703',
                    name: 'Michael Brown',
                    firstName: 'Michael',
                    lastName: 'Brown',
                    mobile: '9988776655',
                    gender: 'Male',
                    age: '62',
                    address: 'Subhash Nagar, Nizamabad',
                    dept: 'Orthopedics',
                    doctor: 'Dr. Ramesh Kumar',
                    totalAmt: 550,
                    discount: 0,
                    netAmt: 550,
                    receivedAmt: 250,
                    dueAmt: 300,
                    status: 'Waiting',
                    time: '04:45 PM'
                },
                {
                    id: 'OP-4105',
                    mrNo: 'MR-9024',
                    billNo: 'B-7704',
                    name: 'Sophia Miller',
                    firstName: 'Sophia',
                    lastName: 'Miller',
                    mobile: '9123456780',
                    gender: 'Female',
                    age: '8',
                    address: 'Khaleelwadi, Nizamabad',
                    dept: 'Pediatrics',
                    doctor: 'Dr. Priya Joshi',
                    totalAmt: 400,
                    discount: 0,
                    netAmt: 400,
                    receivedAmt: 400,
                    dueAmt: 0,
                    status: 'Waiting',
                    time: '05:00 PM'
                }
            ];

            service.getPatients = function () {
                return service.patients;
            };

            service.addPatient = function (p) {
                service.patients.unshift(p);
            };

            service.searchPatient = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.patients.find(function (p) {
                    return (p.name && p.name.toLowerCase().indexOf(q) !== -1) ||
                           (p.id && p.id.toLowerCase().indexOf(q) !== -1) ||
                           (p.mrNo && p.mrNo.toLowerCase().indexOf(q) !== -1) ||
                           (p.billNo && p.billNo.toLowerCase().indexOf(q) !== -1) ||
                           (p.mobile && p.mobile.indexOf(q) !== -1);
                });
            };

            service.calculateAgeFromDob = function (dobStr) {
                if (!dobStr) return null;
                var parts = dobStr.split('/');
                if (parts.length === 3) {
                    var d = parseInt(parts[0], 10);
                    var m = parseInt(parts[1], 10) - 1;
                    var y = parseInt(parts[2], 10);
                    var birthDate = new Date(y, m, d);
                    var now = new Date();
                    if (!isNaN(birthDate.getTime())) {
                        var years = now.getFullYear() - birthDate.getFullYear();
                        var months = now.getMonth() - birthDate.getMonth();
                        var days = now.getDate() - birthDate.getDate();
                        if (days < 0) {
                            months--;
                            days += 30;
                        }
                        if (months < 0) {
                            years--;
                            months += 12;
                        }
                        return {
                            years: Math.max(0, years),
                            months: Math.max(0, months),
                            days: Math.max(0, days)
                        };
                    }
                }
                return null;
            };

            return service;
        });
})();
