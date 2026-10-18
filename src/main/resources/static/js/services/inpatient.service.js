(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('InPatientService', function () {
            var service = {};

            // 1. In Patient Master List (Exact match to Page 1 bottom table)
            service.inPatientList = [
                { sno: 1, ipNo: '535', doa: '06/02/2021 21:38:50', days: 1, patientName: 'NAGAIAH G', age: '46 Yrs', gender: 'Male', bed: 'ICU-01-06', room: 'ICU', referType: 'Direct', doctor: 'Dr. Madhav Rao', mrNo: '1919', mobile: '9515181992' },
                { sno: 2, ipNo: '534', doa: '06/02/2021 18:05:11', days: 1, patientName: 'BAIRI GANGADHAR', age: '65 Yrs', gender: 'Male', bed: 'ICU-01-05', room: 'ICU', referType: 'Direct', doctor: 'Dr. Rajesh Sharma', mrNo: '1918', mobile: '8897575783' },
                { sno: 3, ipNo: '530', doa: '06/02/2021 00:24:30', days: 1, patientName: 'UZER BEE', age: '35 Yrs', gender: 'Female', bed: 'ICU-01-04', room: 'ICU', referType: 'Direct', doctor: 'Dr. Priya Varma', mrNo: '1899', mobile: '9440884323' },
                { sno: 4, ipNo: '529', doa: '05/02/2021 22:04:16', days: 2, patientName: 'RAMESH V', age: '50 Yrs', gender: 'Male', bed: 'ICU-01-03', room: 'ICU', referType: 'Direct', doctor: 'Dr. Madhav Rao', mrNo: '1888', mobile: '9848123456' },
                { sno: 5, ipNo: '526', doa: '05/02/2021 18:20:49', days: 2, patientName: 'THAMPULURI RAMADEVI', age: '45 Yrs', gender: 'Female', bed: 'ICU-01-02', room: 'ICU', referType: 'Direct', doctor: 'Dr. Suresh Babu', mrNo: '1875', mobile: '9603905858' },
                { sno: 6, ipNo: '520', doa: '04/02/2021 11:56:22', days: 4, patientName: 'NAINIVAR GANGARAM', age: '65 Yrs', gender: 'Male', bed: 'ICU-01-01', room: 'ICU', referType: 'Direct', doctor: 'Dr. Rajesh Sharma', mrNo: '1849', mobile: '9553917601' },
                { sno: 7, ipNo: '517', doa: '03/02/2021 11:54:39', days: 5, patientName: 'LAXMAN MALLE', age: '21 Yrs', gender: 'Male', bed: 'GW-01-04', room: 'GENERAL WARD', referType: 'Direct', doctor: 'Dr. Priya Varma', mrNo: '1822', mobile: '9988776655' },
                { sno: 8, ipNo: '499', doa: '28/01/2021 18:23:20', days: 10, patientName: 'NANGI MUTHYAM', age: '40 Yrs', gender: 'Male', bed: 'ICU-02-04', room: 'ICU', referType: 'Direct', doctor: 'Dr. Madhav Rao', mrNo: '1798', mobile: '9848998562' },
                { sno: 9, ipNo: '483', doa: '25/01/2021 23:11:06', days: 13, patientName: 'BHUKYA NANDYA', age: '56 Yrs', gender: 'Male', bed: 'GW-01-01', room: 'GENERAL WARD', referType: 'Direct', doctor: 'Dr. Suresh Babu', mrNo: '1756', mobile: '8500212124' },
                { sno: 10, ipNo: '463', doa: '19/01/2021 10:00:16', days: 20, patientName: 'CHAVAN AKASH', age: '20 Yrs', gender: 'Male', bed: 'SP-205', room: 'SPECIAL ROOMS', referType: 'Direct', doctor: 'Dr. Rajesh Sharma', mrNo: '1701', mobile: '9123456780' },
                { sno: 11, ipNo: '438', doa: '11/01/2021 17:41:02', days: 28, patientName: 'ALAVALA THIRUPATHAMMA', age: '70 Yrs', gender: 'Female', bed: 'GW-01-08', room: 'GENERAL WARD', referType: 'Direct', doctor: 'Dr. Madhav Rao', mrNo: '1650', mobile: '9391227858' }
            ];

            // 2. IP Admission Pre-loaded Search Patients (Page 2 top dropdown)
            service.admissionSearchList = [
                { name: 'ANKAM BOJANNA', opNo: '2327', mrNo: '1909', mobile: '9441185972', age: '52', gender: 'Male', address: 'H.No 3-45, Ashoknagar, Nizamabad', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503001' },
                { name: 'AR SUNIL KUMAR', opNo: '2313', mrNo: '1898', mobile: '9951172331', age: '38', gender: 'Male', address: 'Plot 12, Housing Board Colony', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503002' },
                { name: 'BUDARI AMBAVVA', opNo: '--', mrNo: '1883', mobile: '8185916448', age: '60', gender: 'Female', address: 'Khaleelwadi, Main Road', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503001' },
                { name: 'AGARVAL PQUSH', opNo: '2290', mrNo: '1879', mobile: '9030258983', age: '45', gender: 'Male', address: 'Bank Colony, Subhashnagar', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503002' },
                { name: 'K ANIL', opNo: '2284', mrNo: '1874', mobile: '6304411661', age: '29', gender: 'Male', address: 'Near Bus Stand, Bodhan Road', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503003' },
                { name: 'AMIR BEK', opNo: '2244', mrNo: '1848', mobile: '6303747778', age: '34', gender: 'Male', address: 'Barkatpura, Nizamabad', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503001' },
                { name: 'ENNEVAR ANIL', opNo: '2217', mrNo: '1831', mobile: '9912217277', age: '41', gender: 'Male', address: 'Gouthamnagar', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503002' },
                { name: 'ATTIMALLA RAJANNA', opNo: '2197', mrNo: '1820', mobile: '9550431995', age: '55', gender: 'Male', address: 'Armoor Road, Nizamabad', city: 'Nizamabad', state: 'Telangana', country: 'India', zip: '503003' }
            ];

            // 3. IP Wise Test Results (Page 8 top exact match)
            service.testResults = [
                { sno: 1, labNo: '2175', testName: '2D ECHO', date: '20/01/2021 10:48:47 AM', selected: false },
                { sno: 2, labNo: '2245', testName: '2D ECHO', date: '22/01/2021 01:03:38 PM', selected: false },
                { sno: 3, labNo: '2174', testName: 'ABG (ARTERIAL BLOOD GASES ANALYSIS)', date: '20/01/2021 08:52:17 AM', selected: false },
                { sno: 4, labNo: '2232', testName: 'ABG (ARTERIAL BLOOD GASES ANALYSIS)', date: '22/01/2021 08:05:01 AM', selected: false },
                { sno: 5, labNo: '2204', testName: 'COMPLETE BLOOD PICTURE', date: '21/01/2021 07:36:18 AM', selected: false },
                { sno: 6, labNo: '2232', testName: 'COMPLETE BLOOD PICTURE', date: '22/01/2021 08:05:01 AM', selected: false },
                { sno: 7, labNo: '2253', testName: 'COMPLETE BLOOD PICTURE', date: '23/01/2021 07:58:13 AM', selected: false },
                { sno: 8, labNo: '2174', testName: 'MAJOR SURGICAL PROFILE', date: '20/01/2021 08:52:17 AM', selected: false },
                { sno: 9, labNo: '2204', testName: 'SERUM CREATININE', date: '21/01/2021 07:36:18 AM', selected: false },
                { sno: 10, labNo: '2204', testName: 'SERUM ELECTROLYTES (IP)', date: '21/01/2021 07:36:18 AM', selected: false },
                { sno: 11, labNo: '2232', testName: 'SERUM ELECTROLYTES (IP)', date: '22/01/2021 08:05:01 AM', selected: false }
            ];

            // Helper methods
            service.getInPatients = function () {
                return service.inPatientList;
            };

            service.searchPatient = function (q) {
                if (!q || !q.trim()) return null;
                var lower = q.toLowerCase().trim();
                return service.inPatientList.find(function (p) {
                    return p.patientName.toLowerCase().indexOf(lower) !== -1 ||
                           p.ipNo === lower ||
                           p.mrNo === lower ||
                           p.mobile.indexOf(lower) !== -1;
                }) || service.admissionSearchList.find(function (p) {
                    return p.name.toLowerCase().indexOf(lower) !== -1 ||
                           p.mrNo === lower ||
                           p.mobile.indexOf(lower) !== -1;
                });
            };

            service.getTestResults = function () {
                return angular.copy(service.testResults);
            };

            return service;
        });
})();
