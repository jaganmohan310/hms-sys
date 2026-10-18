/**
 * Application Management (App Mgmt) Service
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('AppMgmtService', function () {
            var service = {};

            // 1. Procedures Master (Matching screenshot fields)
            service.procedures = [
                { id: 'PROC-101', code: 'ECG-12', name: 'ECG (12 Lead)', procDescription: '12-Lead Electrocardiogram', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: false },
                { id: 'PROC-102', code: 'XRAY-CH', name: 'X-Ray Chest PA View', procDescription: 'Chest Radiograph PA View', ccUnitId: 'Per Case', comments: '', active: true, isDoctorProcedure: false },
                { id: 'PROC-103', code: 'SUTR-30', name: 'Wound Dressing & Suturing', procDescription: 'Minor surgical wound repair', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: true },
                { id: 'PROC-104', code: 'NEB-01', name: 'Nebulization', procDescription: 'Respiratory nebulization therapy', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: false },
                { id: 'PROC-105', code: 'IV-INF', name: 'IV Infusion / Injection', procDescription: 'Intravenous medication infusion', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: false },
                { id: 'PROC-106', code: 'GRBS-01', name: 'Blood Sugar GRBS', procDescription: 'Capillary glucose measurement', ccUnitId: 'Per Case', comments: '', active: true, isDoctorProcedure: false },
                { id: 'PROC-107', code: 'CATH-01', name: 'Catheterization', procDescription: 'Urinary catheter insertion', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: true },
                { id: 'PROC-108', code: 'ECHO-2D', name: 'Echocardiography (2D Echo)', procDescription: 'Transthoracic echocardiogram', ccUnitId: 'Hourly', comments: '', active: true, isDoctorProcedure: true },
                { id: 'PROC-109', code: 'USG-ABD', name: 'Ultrasonography (USG Abdomen)', procDescription: 'Whole abdominal ultrasound scan', ccUnitId: 'Per Case', comments: '', active: true, isDoctorProcedure: true }
            ];

            service.searchProcedure = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.procedures.find(function (p) {
                    return (p.code && p.code.toLowerCase().indexOf(q) !== -1) ||
                           (p.name && p.name.toLowerCase().indexOf(q) !== -1) ||
                           (p.id && p.id.toLowerCase() === q);
                });
            };

            // 2. Services Master (Matching screenshot fields)
            service.services = [
                { id: 'SRV-001', code: 'OPD-GEN', name: 'OP Consultation - General', description: 'General Out-Patient Physician Consultation', serviceType: 'Out Patient', comments: '', chargeUnit: 'Hourly', active: true },
                { id: 'SRV-002', code: 'OPD-SPEC', name: 'OP Consultation - Specialist', description: 'Specialist Doctor Consultation', serviceType: 'Out Patient', comments: '', chargeUnit: 'Hourly', active: true },
                { id: 'SRV-003', code: 'EMG-TRI', name: 'Emergency Triage & Care', description: 'Casualty Emergency Medical Triage', serviceType: 'Emergency', comments: '', chargeUnit: 'Daily', active: true },
                { id: 'SRV-004', code: 'GEN-BED', name: 'Nursing & Bed Charge (General Ward)', description: 'General Ward Nursing & Bed Service', serviceType: 'In Patient', comments: '', chargeUnit: 'Daily', active: true },
                { id: 'SRV-005', code: 'ICU-CARE', name: 'ICU Monitoring & Critical Care', description: 'Intensive Critical Care Monitoring', serviceType: 'Critical Care', comments: '', chargeUnit: 'Hourly', active: true },
                { id: 'SRV-006', code: 'AMB-LCL', name: 'Ambulance Transport (Local)', description: 'Local Patient Ambulance Transport', serviceType: 'Transport', comments: '', chargeUnit: 'Package', active: true }
            ];

            service.searchService = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.services.find(function (s) {
                    return (s.code && s.code.toLowerCase().indexOf(q) !== -1) ||
                           (s.name && s.name.toLowerCase().indexOf(q) !== -1) ||
                           (s.id && s.id.toLowerCase() === q);
                });
            };

            service.addService = function (srv) {
                if (!srv.id) srv.id = 'SRV-' + Math.floor(100 + Math.random() * 900);
                service.services.push(srv);
                return srv;
            };

            // 3. Referral Doctors Master (Matching screenshot fields)
            service.referralDoctors = [
                { id: 'RD-001', firstName: 'A. K.', middleName: '', lastName: 'Verma', institute: 'Verma Clinic', address: 'Khaleelwadi', country: 'INDIA', state: 'TELANGANA', city: 'NIZAMABAD', area: 'Select', landline: '08462-221144', mobile: '9848011221', email: 'dr.verma@gmail.com', active: true },
                { id: 'RD-002', firstName: 'S. K.', middleName: '', lastName: 'Gupta', institute: 'City Ortho Hospital', address: 'Subhash Nagar', country: 'INDIA', state: 'TELANGANA', city: 'NIZAMABAD', area: 'Select', landline: '08462-223355', mobile: '9848022334', email: 'dr.gupta@gmail.com', active: true },
                { id: 'RD-003', firstName: 'N.', middleName: '', lastName: 'Rao', institute: 'Children Care Center', address: 'Housing Board', country: 'INDIA', state: 'TELANGANA', city: 'NIZAMABAD', area: 'Select', landline: '08462-224466', mobile: '9848033445', email: 'dr.rao@gmail.com', active: true },
                { id: 'RD-004', firstName: 'Rajesh', middleName: '', lastName: 'Sharma', institute: 'Apex Heart Care', address: 'Tilak Garden', country: 'INDIA', state: 'TELANGANA', city: 'NIZAMABAD', area: 'Select', landline: '08462-225577', mobile: '9848044556', email: 'dr.sharma@gmail.com', active: true },
                { id: 'RD-005', firstName: 'Ananya', middleName: '', lastName: 'Roy', institute: 'Maternity Health Clinic', address: 'Armur Road', country: 'INDIA', state: 'TELANGANA', city: 'NIZAMABAD', area: 'Select', landline: '08462-226688', mobile: '9848055667', email: 'dr.ananya@gmail.com', active: true }
            ];

            service.searchReferralDoctor = function (query) {
                if (!query) return null;
                var q = query.trim().toLowerCase();
                return service.referralDoctors.find(function (d) {
                    return (d.firstName && d.firstName.toLowerCase().indexOf(q) !== -1) ||
                           (d.lastName && d.lastName.toLowerCase().indexOf(q) !== -1) ||
                           (d.institute && d.institute.toLowerCase().indexOf(q) !== -1) ||
                           (d.mobile && d.mobile.indexOf(q) !== -1) ||
                           (d.id && d.id.toLowerCase() === q);
                });
            };

            // 4. Procedure Charges Master (Matching screenshots media_1792160800340_4bb72d48.png, media_1792160818863_6a30371a.png, media_1792160871188_21bcbe0c.png)
            service.procedureChargeList = [
                'BIOPSY SMALL',
                'CASUVALITY CHARGES',
                'C-PAP',
                'EEG',
                'INCUBATION CHARGES',
                'LUMBAR PUNCTURE',
                'MANIPULATION CHARGES',
                'MEDICAL CERTIFICATE CHARGES',
                'MLC',
                'NCS 4 Limbs',
                'NCS Lower Lmbs',
                'NCS Upper Limbs',
                'NEBULIZATION CHARGES (PER EACH)',
                'OBSERVATIOIN CHARGES',
                'OXYGEN CHARGES(PER HOUR)',
                'PHYSIOTHERAPY',
                'PLEURAL TAP',
                'POP CHARGES',
                'POP REMOVAL CHARGES(L)'
            ];

            service.procedureChargeRates = {
                'OBSERVATIOIN CHARGES': { hospitalShare: 500, doctorShare: 200, hospitalPercentage: 30, totalAmount: 700 },
                'BIOPSY SMALL': { hospitalShare: 800, doctorShare: 1200, hospitalPercentage: 40, totalAmount: 2000 },
                'CASUVALITY CHARGES': { hospitalShare: 350, doctorShare: 150, hospitalPercentage: 30, totalAmount: 500 },
                'C-PAP': { hospitalShare: 600, doctorShare: 200, hospitalPercentage: 25, totalAmount: 800 },
                'EEG': { hospitalShare: 900, doctorShare: 600, hospitalPercentage: 40, totalAmount: 1500 },
                'INCUBATION CHARGES': { hospitalShare: 1200, doctorShare: 800, hospitalPercentage: 40, totalAmount: 2000 },
                'LUMBAR PUNCTURE': { hospitalShare: 700, doctorShare: 800, hospitalPercentage: 45, totalAmount: 1500 },
                'MANIPULATION CHARGES': { hospitalShare: 400, doctorShare: 400, hospitalPercentage: 50, totalAmount: 800 },
                'MEDICAL CERTIFICATE CHARGES': { hospitalShare: 200, doctorShare: 100, hospitalPercentage: 33, totalAmount: 300 },
                'MLC': { hospitalShare: 500, doctorShare: 500, hospitalPercentage: 50, totalAmount: 1000 },
                'NCS 4 Limbs': { hospitalShare: 1000, doctorShare: 800, hospitalPercentage: 45, totalAmount: 1800 },
                'NCS Lower Lmbs': { hospitalShare: 600, doctorShare: 500, hospitalPercentage: 45, totalAmount: 1100 },
                'NCS Upper Limbs': { hospitalShare: 600, doctorShare: 500, hospitalPercentage: 45, totalAmount: 1100 },
                'NEBULIZATION CHARGES (PER EACH)': { hospitalShare: 150, doctorShare: 50, hospitalPercentage: 25, totalAmount: 200 },
                'OXYGEN CHARGES(PER HOUR)': { hospitalShare: 120, doctorShare: 30, hospitalPercentage: 20, totalAmount: 150 },
                'PHYSIOTHERAPY': { hospitalShare: 250, doctorShare: 250, hospitalPercentage: 50, totalAmount: 500 },
                'PLEURAL TAP': { hospitalShare: 600, doctorShare: 600, hospitalPercentage: 50, totalAmount: 1200 },
                'POP CHARGES': { hospitalShare: 500, doctorShare: 500, hospitalPercentage: 50, totalAmount: 1000 },
                'POP REMOVAL CHARGES(L)': { hospitalShare: 200, doctorShare: 100, hospitalPercentage: 33, totalAmount: 300 }
            };

            service.getProcedureChargeRate = function (procName) {
                if (!procName) return null;
                return service.procedureChargeRates[procName] || {
                    hospitalShare: 400,
                    doctorShare: 200,
                    hospitalPercentage: 30,
                    totalAmount: 600
                };
            };

            service.saveProcedureChargeRate = function (procName, rateData) {
                if (!procName) return;
                service.procedureChargeRates[procName] = angular.copy(rateData);
                return service.procedureChargeRates[procName];
            };

            service.applyPercentageToAll = function (pct) {
                var factor = 1 + (Number(pct) || 0) / 100;
                Object.keys(service.procedureChargeRates).forEach(function (key) {
                    var item = service.procedureChargeRates[key];
                    item.hospitalShare = Math.round(item.hospitalShare * factor);
                    item.doctorShare = Math.round(item.doctorShare * factor);
                    item.totalAmount = Math.round(item.totalAmount * factor);
                });
            };

            service.procedureCharges = [
                { id: 'PC-101', procName: 'ECG (12 Lead)', generalWard: 250, semiPrivate: 300, privateRoom: 350, icuRate: 400 },
                { id: 'PC-102', procName: 'X-Ray Chest PA View', generalWard: 350, semiPrivate: 400, privateRoom: 450, icuRate: 500 },
                { id: 'PC-103', procName: 'Wound Dressing & Suturing', generalWard: 300, semiPrivate: 350, privateRoom: 400, icuRate: 450 },
                { id: 'PC-104', procName: 'Nebulization', generalWard: 200, semiPrivate: 230, privateRoom: 260, icuRate: 300 },
                { id: 'PC-105', procName: '2D Echo', generalWard: 1200, semiPrivate: 1400, privateRoom: 1600, icuRate: 1800 },
                { id: 'PC-106', procName: 'USG Abdomen', generalWard: 800, semiPrivate: 950, privateRoom: 1100, icuRate: 1300 }
            ];

            // 5. User Screen Settings Master (Categorized by Module)
            service.screenSettings = [
                // Out Patient
                { module: 'Out Patient', screen: 'Registration', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'Out Patient', screen: 'Consultation', admin: true, receptionist: true, doctor: true, cashier: false },
                { module: 'Out Patient', screen: 'Consultation Cancellation', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Out Patient', screen: 'Due Collection', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Out Patient', screen: 'Procedure', admin: true, receptionist: true, doctor: true, cashier: false },
                { module: 'Out Patient', screen: 'Procedure Cancellation', admin: true, receptionist: false, doctor: false, cashier: true },

                // App Mgmt
                { module: 'App Mgmt', screen: 'Procedures', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'Service', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'Referral Doctor', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'Procedure Charges', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'User Screen Settings', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'Service Charge', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'App Mgmt', screen: 'User Report Settings', admin: true, receptionist: false, doctor: false, cashier: false },

                // Soft Lab
                { module: 'Soft Lab', screen: 'Lab Test Booking', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'Soft Lab', screen: 'Sample Collection', admin: true, receptionist: false, doctor: false, cashier: false },
                { module: 'Soft Lab', screen: 'Lab Results Entry', admin: true, receptionist: false, doctor: true, cashier: false },
                { module: 'Soft Lab', screen: 'Report Dispatch', admin: true, receptionist: true, doctor: false, cashier: false },

                // In Patient
                { module: 'In Patient', screen: 'IP Admission', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'In Patient', screen: 'Bed Allocation', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'In Patient', screen: 'Daily Nursing Chart', admin: true, receptionist: false, doctor: true, cashier: false },
                { module: 'In Patient', screen: 'IP Discharge Summary', admin: true, receptionist: false, doctor: true, cashier: true },

                // Accounting
                { module: 'Accounting', screen: 'Voucher Entry', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Accounting', screen: 'Ledger View', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Accounting', screen: 'Daily Cash Closing', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Accounting', screen: 'Bank Reconciliation', admin: true, receptionist: false, doctor: false, cashier: false },

                // Reports
                { module: 'Reports', screen: 'OP Collection Summary', admin: true, receptionist: false, doctor: false, cashier: true },
                { module: 'Reports', screen: 'Doctor Wise Report', admin: true, receptionist: false, doctor: true, cashier: false },
                { module: 'Reports', screen: 'Daily Patient Census', admin: true, receptionist: true, doctor: true, cashier: false },
                { module: 'Reports', screen: 'Revenue Analytics', admin: true, receptionist: false, doctor: false, cashier: false },

                // Radiology
                { module: 'Radiology', screen: 'X-Ray Booking', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'Radiology', screen: 'Scan Scheduling', admin: true, receptionist: true, doctor: false, cashier: false },
                { module: 'Radiology', screen: 'Radiology Reporting', admin: true, receptionist: false, doctor: true, cashier: false }
            ];

            service.getScreenSettingsByModule = function (mod) {
                if (!mod || mod === 'Select') return [];
                return service.screenSettings.filter(function (s) {
                    return s.module === mod;
                });
            };

            // 6. Service Charges Master (Matching screenshots media_1792299794041_5df08e6a.png, media_1792299799929_c8e7c257.png, media_1792299806567_735e1d7e.png, media_1792299813705_427100fe.png, media_1792299819618_423a9e65.png)
            service.serviceChargeLists = {
                'Doctor Service': [
                    'Select',
                    'All',
                    'CONSULTATION CHARGES',
                    'DAILY VISIT CHARGES',
                    'EMERGENCY VISIT',
                    'ICU VISIT CHARGES',
                    'SPECIALIST CONSULTATION',
                    'SURGEON CHARGES'
                ],
                'Room Service': [
                    'Select',
                    'All',
                    'DMO CHARGES',
                    'General Ward',
                    'NURSING CHARGES',
                    'Special Room'
                ],
                'General Service': [
                    'Select',
                    'All',
                    'C-PAP',
                    'ICU BED CHARGES',
                    'IMPLANT CHARGES',
                    'INFUSION PUMP',
                    'IP ADMISSION CHARGES',
                    'MLC CHARGES',
                    'MONITORING',
                    'NIV CHARGES',
                    'OT CHARGES',
                    'OXYGEN PER DAY .',
                    'STEP DOWN ICU',
                    'VENTILATOR (PER DAY)'
                ]
            };

            service.roomTypes = [
                'Select',
                'All',
                'ER WARD',
                'GENERAL WARD',
                'ICU',
                'SPECIAL ROOMS',
                'STEP DOWN ICU'
            ];

            service.serviceChargeRates = {
                // Room Service
                'DMO CHARGES': { hospitalShare: 300, doctorShare: 200, hospitalPercentage: 60, totalAmount: 500 },
                'General Ward': { hospitalShare: 500, doctorShare: 250, hospitalPercentage: 65, totalAmount: 750 },
                'NURSING CHARGES': { hospitalShare: 350, doctorShare: 150, hospitalPercentage: 70, totalAmount: 500 },
                'Special Room': { hospitalShare: 1200, doctorShare: 600, hospitalPercentage: 65, totalAmount: 1800 },
                
                // General Service
                'INFUSION PUMP': { hospitalShare: 200, doctorShare: 100, hospitalPercentage: 65, totalAmount: 300 },
                'ICU BED CHARGES': { hospitalShare: 2500, doctorShare: 1000, hospitalPercentage: 70, totalAmount: 3500 },
                'VENTILATOR (PER DAY)': { hospitalShare: 3000, doctorShare: 1500, hospitalPercentage: 65, totalAmount: 4500 },
                'OT CHARGES': { hospitalShare: 4000, doctorShare: 2500, hospitalPercentage: 60, totalAmount: 6500 },
                'OXYGEN PER DAY .': { hospitalShare: 500, doctorShare: 150, hospitalPercentage: 75, totalAmount: 650 },
                'MONITORING': { hospitalShare: 400, doctorShare: 200, hospitalPercentage: 65, totalAmount: 600 },
                'NIV CHARGES': { hospitalShare: 800, doctorShare: 400, hospitalPercentage: 65, totalAmount: 1200 },
                'STEP DOWN ICU': { hospitalShare: 1500, doctorShare: 700, hospitalPercentage: 68, totalAmount: 2200 },
                'C-PAP': { hospitalShare: 600, doctorShare: 200, hospitalPercentage: 75, totalAmount: 800 },
                'IMPLANT CHARGES': { hospitalShare: 5000, doctorShare: 2000, hospitalPercentage: 70, totalAmount: 7000 },
                'IP ADMISSION CHARGES': { hospitalShare: 750, doctorShare: 250, hospitalPercentage: 75, totalAmount: 1000 },
                'MLC CHARGES': { hospitalShare: 500, doctorShare: 500, hospitalPercentage: 50, totalAmount: 1000 },

                // Doctor Service
                'CONSULTATION CHARGES': { hospitalShare: 150, doctorShare: 350, hospitalPercentage: 30, totalAmount: 500 },
                'DAILY VISIT CHARGES': { hospitalShare: 200, doctorShare: 400, hospitalPercentage: 33, totalAmount: 600 },
                'EMERGENCY VISIT': { hospitalShare: 300, doctorShare: 700, hospitalPercentage: 30, totalAmount: 1000 },
                'ICU VISIT CHARGES': { hospitalShare: 400, doctorShare: 800, hospitalPercentage: 33, totalAmount: 1200 },
                'SPECIALIST CONSULTATION': { hospitalShare: 250, doctorShare: 550, hospitalPercentage: 30, totalAmount: 800 },
                'SURGEON CHARGES': { hospitalShare: 1500, doctorShare: 3500, hospitalPercentage: 30, totalAmount: 5000 }
            };

            service.getServiceChargeRates = function (srvName) {
                if (!srvName || srvName === 'Select') return null;
                return service.serviceChargeRates[srvName] || {
                    hospitalShare: 300,
                    doctorShare: 150,
                    hospitalPercentage: 40,
                    totalAmount: 450
                };
            };

            service.saveServiceChargeRate = function (srvName, rateData) {
                if (!srvName) return;
                service.serviceChargeRates[srvName] = angular.copy(rateData);
                return service.serviceChargeRates[srvName];
            };

            service.applyServicePercentageToAll = function (pct) {
                var factor = 1 + (Number(pct) || 0) / 100;
                Object.keys(service.serviceChargeRates).forEach(function (key) {
                    var item = service.serviceChargeRates[key];
                    item.hospitalShare = Math.round(item.hospitalShare * factor);
                    item.doctorShare = Math.round(item.doctorShare * factor);
                    item.totalAmount = Math.round(item.totalAmount * factor);
                });
            };

            service.serviceCharges = [
                { category: 'Out-Patient Services', baseDiscountPct: 0, taxPct: 0, surchargePct: 0 },
                { category: 'Diagnostic & Lab Services', baseDiscountPct: 5, taxPct: 0, surchargePct: 0 },
                { category: 'In-Patient & Nursing', baseDiscountPct: 0, taxPct: 5, surchargePct: 2 },
                { category: 'Emergency & Casualty', baseDiscountPct: 0, taxPct: 0, surchargePct: 5 }
            ];

            // 7. User Report Settings
            service.reportSettings = {
                hospitalHeader: 'INDUS SUPER SPECIALITY HOSPITAL',
                headerTagline: 'Excellence in Healthcare & Patient Centered Services',
                headerAddress: 'Khaleelwadi, Nizamabad - 503001, Telangana',
                contactNumber: '08462 - 224455 / 224466',
                email: 'info@indushospital.org',
                showLogo: true,
                paperSize: 'A4',
                orientation: 'Portrait',
                footerDisclaimer: 'This is a computer generated medical record. No physical signature required.'
            };

            // User Reports Master for User Report Settings (Exact Match to Screenshots)
            service.userReportsMaster = {
                'Admin': [
                    'My Collection',
                    'MRNO wise Bill Details',
                    'Patient wise Count',
                    'Procedures',
                    'User Collection'
                ],
                'In Patient': [
                    'Daywise IP List',
                    'Discharge Summary',
                    'Doctor Wise Room Rent',
                    'Doctor Wise Visit Report',
                    'Due Collection',
                    'FromBill To ToBill',
                    'IP Admission Cancellations',
                    'IP Advances',
                    'IP Advances Refund Details',
                    'IP Available Beds',
                    'IP Bed Details',
                    'IP Bill Adjustments',
                    'IP Bill Details',
                    'IP Day Collection',
                    'IP Doctor Commision',
                    'IP Dues Report',
                    'IP Gross Day wise Collection',
                    'IP Patient List',
                    'IP Procedure Commision',
                    'IP Settlement Discount Given list',
                    'IP Settlements',
                    'IP User Collection'
                ],
                'Lab': [
                    'Breakage Or Loss',
                    'Department Tests Report',
                    'Doctor and test wise collection',
                    'Due Collection',
                    'FromBill To ToBill',
                    'IPNo wise Tests and Cancellations',
                    'Lab Day Collection',
                    'Lab Detailed Registrations',
                    'Lab Doctor Wise Collection',
                    'Lab Due Report',
                    'Lab Registration Cancellations',
                    'Lab Registrations'
                ],
                'Out Patient': [
                    'Area wise Consultaions',
                    'Cancellation Report',
                    'Consultant Doctor Review Validity',
                    'Consultation Cancellations',
                    'Consultation Cancellations Summary',
                    'Consultations With Cancellations',
                    'Consultations With Cancellations summary',
                    'Due Collection',
                    'Free Reviews',
                    'FromBill To ToBill',
                    'OP Day Collection',
                    'OP Detailed Report',
                    'OP Discount given patient details',
                    'OP Due Report',
                    'OP Gross Day Wise Collection',
                    'OP Referral Doctor Commision',
                    'OP TDS',
                    'OP total Collection',
                    'OP User Collection',
                    'OP_Consultations',
                    'OP_Consultations Summary',
                    'OP_Consultations without financial'
                ],
                'Pharmacy': [
                    'IPNO Wise Due Collection',
                    'IPNO Wise Dues',
                    'IPNo wise Medicine Sales and Returns',
                    'Pharmacy Collection (Provisional)',
                    'Pharmacy Dues Report',
                    'Pharmacy Dump Stock',
                    'Pharmacy Sales',
                    'Sales',
                    'Sales in Detail',
                    'Sales Summary',
                    'SalesReturns',
                    'UserWiseSales'
                ]
            };

            service.getUserReports = function (moduleName) {
                if (!moduleName || moduleName === 'Select') return [];
                return angular.copy(service.userReportsMaster[moduleName] || []);
            };

            service.saveUserReportsOrder = function (moduleName, reportsList) {
                if (!moduleName || moduleName === 'Select') return;
                service.userReportsMaster[moduleName] = angular.copy(reportsList);
                return service.userReportsMaster[moduleName];
            };

            // Getters & Mutation Methods
            service.getProcedures = function () { return service.procedures; };
            service.getServices = function () { return service.services; };
            service.getReferralDoctors = function () { return service.referralDoctors; };
            service.getProcedureCharges = function () { return service.procedureCharges; };
            service.getProcedureChargeList = function () { return service.procedureChargeList; };
            service.getScreenSettings = function () { return service.screenSettings; };
            service.getScreenSettingsByModule = service.getScreenSettingsByModule;
            service.getServiceCharges = function () { return service.serviceCharges; };
            service.getServiceChargeLists = function () { return service.serviceChargeLists; };
            service.getRoomTypes = function () { return service.roomTypes; };
            service.getReportSettings = function () { return service.reportSettings; };

            service.addProcedure = function (item) {
                if (!item.id) item.id = 'PROC-' + Math.floor(110 + Math.random() * 890);
                service.procedures.push(item);
                return item;
            };

            service.addReferralDoctor = function (doc) {
                if (!doc.id) doc.id = 'RD-' + Math.floor(100 + Math.random() * 900);
                service.referralDoctors.push(doc);
                return doc;
            };

            return service;
        });
})();
