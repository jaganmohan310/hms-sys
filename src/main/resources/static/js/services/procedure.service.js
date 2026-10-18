/**
 * Procedure Management Service
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('ProcedureService', function () {
            var service = {};

            // Procedure Catalog
            service.catalog = [
                { id: 'PROC-101', name: 'ECG (12 Lead)', fee: 250 },
                { id: 'PROC-102', name: 'X-Ray Chest PA View', fee: 350 },
                { id: 'PROC-103', name: 'Wound Dressing & Suturing', fee: 300 },
                { id: 'PROC-104', name: 'Nebulization', fee: 200 },
                { id: 'PROC-105', name: 'IV Infusion / Injection', fee: 150 },
                { id: 'PROC-106', name: 'Blood Sugar GRBS', fee: 100 },
                { id: 'PROC-107', name: 'Catheterization', fee: 400 },
                { id: 'PROC-108', name: 'Echocardiography (2D Echo)', fee: 1200 },
                { id: 'PROC-109', name: 'Ultrasonography (USG Abdomen)', fee: 800 }
            ];

            // Pre-booked Procedure Patients
            service.bookedPatients = [
                {
                    opNo: '1133',
                    mrNo: 'MR-5521',
                    mobile: '9766173106',
                    title: 'Mr',
                    firstName: 'SHIVA LINGAM',
                    middleName: 'CHELIMELA',
                    lastName: '',
                    ageYears: '60',
                    ageMonths: '00',
                    ageDays: '00',
                    gender: 'Male',
                    address: 'MARAMPALLY',
                    patientType: 'Direct',
                    referralName: 'Self',
                    consultantDoctor: 'Non-Doctor',
                    procedureName: 'ECG (12 Lead)',
                    pro: 'Select',
                    referralDoctor: 'Select',
                    totalAmount: 250,
                    discountType: 'No Discount',
                    discountPercent: 0,
                    discountAmt: 0,
                    netAmount: 250,
                    paidAmount: 250,
                    dueAmount: 0,
                    paymentType: 'Cash'
                },
                {
                    opNo: '1134',
                    mrNo: 'MR-5522',
                    mobile: '9848011223',
                    title: 'Mrs',
                    firstName: 'LAXMI',
                    middleName: 'G',
                    lastName: '',
                    ageYears: '48',
                    ageMonths: '00',
                    ageDays: '00',
                    gender: 'Female',
                    address: 'NIZAMABAD',
                    patientType: 'Direct',
                    referralName: 'Self',
                    consultantDoctor: 'Non-Doctor',
                    procedureName: 'X-Ray Chest PA View',
                    pro: 'Select',
                    referralDoctor: 'Select',
                    totalAmount: 350,
                    discountType: 'No Discount',
                    discountPercent: 0,
                    discountAmt: 0,
                    netAmount: 350,
                    paidAmount: 350,
                    dueAmount: 0,
                    paymentType: 'Cash'
                }
            ];

            // Procedure Bills for Cancellation
            service.procedureBills = [
                {
                    billNo: 'PB-8801',
                    opNo: '1133',
                    mrNo: 'MR-5521',
                    mobile: '9766173106',
                    title: 'Mr',
                    firstName: 'SHIVA LINGAM',
                    middleName: 'CHELIMELA',
                    lastName: '',
                    age: '60/00/00',
                    gender: 'Male',
                    refType: 'Direct',
                    refName: 'Self',
                    referralDoctor: 'Select',
                    totalAmount: 250,
                    discountAmt: 0,
                    netAmount: 250,
                    totalReceived: 250,
                    refundedAmt: 250,
                    discAdjusted: 0,
                    dueAmount: 0,
                    discountPercent: 0,
                    discountType: 'No Discount',
                    status: 'Active'
                },
                {
                    billNo: 'PB-8802',
                    opNo: '1134',
                    mrNo: 'MR-5522',
                    mobile: '9848011223',
                    title: 'Mrs',
                    firstName: 'LAXMI',
                    middleName: 'G',
                    lastName: '',
                    age: '48/00/00',
                    gender: 'FeMale',
                    refType: 'Select',
                    refName: 'Select',
                    referralDoctor: 'Select',
                    totalAmount: 350,
                    discountAmt: 0,
                    netAmount: 350,
                    totalReceived: 350,
                    refundedAmt: 350,
                    discAdjusted: 0,
                    dueAmount: 0,
                    discountPercent: 0,
                    discountType: 'No Discount',
                    status: 'Active'
                }
            ];

            service.searchBookedPatient = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.bookedPatients.find(function (p) {
                    return (p.opNo && p.opNo.toLowerCase() === q) ||
                           (p.mrNo && p.mrNo.toLowerCase() === q) ||
                           (p.mobile && p.mobile.indexOf(q) !== -1) ||
                           (p.firstName && p.firstName.toLowerCase().indexOf(q) !== -1);
                });
            };

            service.searchProcedureBill = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.procedureBills.find(function (b) {
                    return (b.billNo && b.billNo.toLowerCase() === q) ||
                           (b.opNo && b.opNo.toLowerCase() === q) ||
                           (b.mrNo && b.mrNo.toLowerCase() === q) ||
                           (b.mobile && b.mobile.indexOf(q) !== -1) ||
                           (b.firstName && b.firstName.toLowerCase().indexOf(q) !== -1);
                });
            };

            service.cancelBill = function (billNo) {
                var bill = service.procedureBills.find(function (b) {
                    return b.billNo && b.billNo.toLowerCase() === (billNo || '').toLowerCase();
                });
                if (bill) {
                    bill.status = 'Cancelled';
                    return true;
                }
                return false;
            };

            service.getCatalog = function () {
                return service.catalog;
            };

            return service;
        });
})();
