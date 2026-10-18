/**
 * Main Dashboard Controller
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .controller('DashboardController', [
            '$scope',
            '$window',
            '$interval',
            '$timeout',
            'AuthService',
            'PatientService',
            'BillingService',
            'ProcedureService',
            'AppMgmtService',
            'LabService',
            'InPatientService',
            'AccountService',
            'ReportsService',
            'RadiologyService',
            function ($scope, $window, $interval, $timeout, AuthService, PatientService, BillingService, ProcedureService, AppMgmtService, LabService, InPatientService, AccountService, ReportsService, RadiologyService) {

                // 1. Session & User
                $scope.user = AuthService.getCurrentUser() || {
                    username: 'MADAVI',
                    fullName: 'Madavi',
                    role: 'ADMIN',
                    email: 'madavi@indushospital.org'
                };

                // 2. Header Collapse
                $scope.headerCollapsed = false;
                $scope.toggleHeader = function () {
                    $scope.headerCollapsed = !$scope.headerCollapsed;
                };

                // 3. Module & Sub-Action Navigation
                $scope.modules = [
                    'App Mgmt',
                    'Out Patient',
                    'Soft Lab',
                    'In Patient',
                    'Accounting',
                    'Reports',
                    'Radiology'
                ];

                $scope.moduleSubActions = {
                    'App Mgmt': [
                        'Procedures',
                        'Service',
                        'Referral Doctor',
                        'Procedure Charges',
                        'User Screen Settings',
                        'Service Charge',
                        'User Report Settings'
                    ],
                    'Out Patient': [
                        'Consultation',
                        'Consultation Cancellation',
                        'Due Collection',
                        'Procedure',
                        'Procedure Cancellation',
                        'Registration'
                    ],
                    'Soft Lab': [
                        'Charges',
                        'Lab Due Collection',
                        'Registration',
                        'Report Status',
                        'Test Cancellation',
                        'Test Details'
                    ],
                    'In Patient': [
                        'In Patient List',
                        'IP Admission',
                        'Advance Payment',
                        'Transfer Doctor',
                        'Transfer Bed',
                        'Discharge Summary',
                        'IP Bill Details',
                        'Settlement Bill',
                        'IP wise Test Results',
                        'Medical Certificate',
                        'Due Collection',
                        'Discharge Cancellation'
                    ],
                    'Accounting': [
                        'Transactions'
                    ],
                    'Reports': [
                        'Application Mgmt',
                        'In Patient',
                        'Lab',
                        'Out Patient',
                        'Pharmacy'
                    ],
                    'Radiology': [
                        'Charges',
                        'Diagnostic Test'
                    ]
                };

                // Default to 'Reports' -> 'Application Mgmt' (or 'Radiology')
                $scope.activeModule = 'Reports';
                $scope.subActions = $scope.moduleSubActions['Reports'];
                $scope.activeAction = 'Application Mgmt'; 

                $scope.selectModule = function (mod) {
                    $scope.activeModule = mod;
                    $scope.subActions = $scope.moduleSubActions[mod] || [];
                    if (mod === 'Soft Lab') {
                        $scope.activeAction = 'Registration';
                    } else if (mod === 'App Mgmt') {
                        $scope.activeAction = 'User Report Settings';
                    } else if (mod === 'Out Patient') {
                        $scope.activeAction = 'Procedure Cancellation';
                    } else if (mod === 'Accounting') {
                        $scope.activeAction = 'Transactions';
                    } else if (mod === 'Reports') {
                        $scope.activeAction = 'Application Mgmt';
                        if ($scope.loadReportListForCategory) {
                            $scope.loadReportListForCategory('Application Mgmt');
                        }
                    } else if (mod === 'Radiology') {
                        $scope.activeAction = 'Charges';
                    } else {
                        $scope.activeAction = $scope.subActions[0] || '';
                    }
                    $scope.showToast('Switched to ' + mod + ' module');
                };

                $scope.selectAction = function (act) {
                    $scope.activeAction = act;
                    if ($scope.activeModule === 'Reports' && $scope.loadReportListForCategory) {
                        $scope.loadReportListForCategory(act);
                    }
                    $scope.showToast('Selected: ' + act);
                };

                // App Mgmt -> Procedures Form Model (Exact match to screenshot)
                function getInitialAppProcForm() {
                    return {
                        procedureCode: '',
                        procedureName: '',
                        procDescription: '',
                        ccUnitId: 'Hourly',
                        comments: '',
                        active: true,
                        isDoctorProcedure: false
                    };
                }
                $scope.appProcForm = getInitialAppProcForm();

                $scope.lookupAppProcedure = function () {
                    var q = $scope.appProcForm.procedureCode || $scope.appProcForm.procedureName;
                    if (!q) {
                        $scope.showToast('Please enter Procedure Code or Name to search.');
                        return;
                    }
                    var match = AppMgmtService.searchProcedure(q);
                    if (match) {
                        $scope.appProcForm.procedureCode = match.code;
                        $scope.appProcForm.procedureName = match.name;
                        $scope.appProcForm.procDescription = match.procDescription || '';
                        $scope.appProcForm.ccUnitId = match.ccUnitId || 'Hourly';
                        $scope.appProcForm.comments = match.comments || '';
                        $scope.appProcForm.active = match.active !== false;
                        $scope.appProcForm.isDoctorProcedure = match.isDoctorProcedure === true;
                        $scope.showToast('Loaded procedure: ' + match.code + ' (' + match.name + ')');
                    } else {
                        $scope.showToast('No procedure found for "' + q + '"');
                    }
                };

                // App Mgmt -> Service Form Model (Exact match to latest screenshot media_1792159092101_27d857a2.png)
                function getInitialAppServiceForm() {
                    return {
                        serviceCode: '',
                        serviceName: '',
                        description: '',
                        serviceType: 'Select',
                        comments: '',
                        chargeUnit: 'Hourly',
                        active: true
                    };
                }
                $scope.appServiceForm = getInitialAppServiceForm();

                $scope.lookupAppService = function () {
                    var q = $scope.appServiceForm.serviceCode || $scope.appServiceForm.serviceName;
                    if (!q) {
                        $scope.showToast('Please enter Service Code or Name to search.');
                        return;
                    }
                    var match = AppMgmtService.searchService(q);
                    if (match) {
                        $scope.appServiceForm.serviceCode = match.code;
                        $scope.appServiceForm.serviceName = match.name;
                        $scope.appServiceForm.description = match.description || '';
                        $scope.appServiceForm.serviceType = match.serviceType || 'Select';
                        $scope.appServiceForm.comments = match.comments || '';
                        $scope.appServiceForm.chargeUnit = match.chargeUnit || 'Hourly';
                        $scope.appServiceForm.active = match.active !== false;
                        $scope.showToast('Loaded service: ' + match.code + ' (' + match.name + ')');
                    } else {
                        $scope.showToast('No service found for "' + q + '"');
                    }
                };

                // App Mgmt -> Referral Doctor Form Model (Exact match to screenshot media_1792159880004_a5bbd1a4.png)
                function getInitialAppRefDocForm() {
                    return {
                        id: '',
                        firstName: '',
                        middleName: '',
                        lastName: '',
                        institute: '',
                        address: '',
                        country: 'INDIA',
                        landline: '',
                        state: 'TELANGANA',
                        mobile: '',
                        city: 'NIZAMABAD',
                        email: '',
                        area: 'Select',
                        active: true
                    };
                }
                $scope.appRefDocForm = getInitialAppRefDocForm();

                $scope.lookupAppRefDoctor = function () {
                    var q = $scope.appRefDocForm.firstName || $scope.appRefDocForm.lastName || $scope.appRefDocForm.institute || $scope.appRefDocForm.mobile;
                    if (!q) {
                        $scope.showToast('Please enter Name, Institute, or Mobile to search.');
                        return;
                    }
                    var match = AppMgmtService.searchReferralDoctor(q);
                    if (match) {
                        $scope.appRefDocForm.id = match.id || '';
                        $scope.appRefDocForm.firstName = match.firstName || '';
                        $scope.appRefDocForm.middleName = match.middleName || '';
                        $scope.appRefDocForm.lastName = match.lastName || '';
                        $scope.appRefDocForm.institute = match.institute || '';
                        $scope.appRefDocForm.address = match.address || '';
                        $scope.appRefDocForm.country = match.country || 'INDIA';
                        $scope.appRefDocForm.landline = match.landline || '';
                        $scope.appRefDocForm.state = match.state || 'TELANGANA';
                        $scope.appRefDocForm.mobile = match.mobile || '';
                        $scope.appRefDocForm.city = match.city || 'NIZAMABAD';
                        $scope.appRefDocForm.email = match.email || '';
                        $scope.appRefDocForm.area = match.area || 'Select';
                        $scope.appRefDocForm.active = match.active !== false;
                        $scope.showToast('Loaded Referral Doctor: Dr. ' + match.firstName + ' ' + (match.lastName || ''));
                    } else {
                        $scope.showToast('No referral doctor found for "' + q + '"');
                    }
                };

                // App Mgmt -> Procedure Charges Form Model (Matching screenshots media_1792160800340_4bb72d48.png, media_1792160818863_6a30371a.png, media_1792160871188_21bcbe0c.png)
                $scope.procChargeList = AppMgmtService.getProcedureChargeList();

                function getInitialProcChargeForm() {
                    return {
                        procedureType: 'General Procedure', // 'General Procedure' | 'Doctor Procedure'
                        procedure: 'Select',
                        consultantDoctor: 'Select',
                        organizationType: 'Hospital',
                        indicator: 'Select',
                        copyTo: false,
                        copyToOrgType: 'Hospital',
                        hospitalShare: '',
                        doctorShare: '',
                        hospitalPercentage: '',
                        totalAmount: '',
                        incrDecrPct: 0
                    };
                }
                $scope.procChargeForm = getInitialProcChargeForm();

                $scope.onProcChargeProcedureChange = function () {
                    var pName = $scope.procChargeForm.procedure;
                    if (!pName || pName === 'Select') {
                        $scope.procChargeForm.hospitalShare = '';
                        $scope.procChargeForm.doctorShare = '';
                        $scope.procChargeForm.hospitalPercentage = '';
                        $scope.procChargeForm.totalAmount = '';
                        return;
                    }
                    var rate = AppMgmtService.getProcedureChargeRate(pName);
                    if (rate) {
                        $scope.procChargeForm.hospitalShare = rate.hospitalShare;
                        $scope.procChargeForm.doctorShare = rate.doctorShare;
                        $scope.procChargeForm.hospitalPercentage = rate.hospitalPercentage;
                        $scope.procChargeForm.totalAmount = rate.totalAmount;
                        $scope.showToast('Loaded rates for ' + pName);
                    }
                };

                $scope.applyProcChargeIncrDecr = function () {
                    var pct = Number($scope.procChargeForm.incrDecrPct) || 0;
                    if (pct === 0) {
                        $scope.showToast('Please enter an Incr./Decr. percentage.');
                        return;
                    }
                    var factor = 1 + (pct / 100);
                    if ($scope.procChargeForm.procedureType === 'General Procedure') {
                        if ($scope.procChargeForm.hospitalShare) {
                            $scope.procChargeForm.hospitalShare = Math.round(Number($scope.procChargeForm.hospitalShare) * factor);
                        }
                        if ($scope.procChargeForm.doctorShare) {
                            $scope.procChargeForm.doctorShare = Math.round(Number($scope.procChargeForm.doctorShare) * factor);
                        }
                    } else {
                        if ($scope.procChargeForm.totalAmount) {
                            $scope.procChargeForm.totalAmount = Math.round(Number($scope.procChargeForm.totalAmount) * factor);
                        }
                    }
                    $scope.showToast('Applied ' + pct + '% change to current rates.');
                };

                $scope.applyProcChargeToAll = function () {
                    var pct = Number($scope.procChargeForm.incrDecrPct) || 0;
                    AppMgmtService.applyPercentageToAll(pct);
                    $scope.applyProcChargeIncrDecr();
                    $scope.showToast('Applied ' + pct + '% change to ALL procedure charge records.');
                };

                // App Mgmt -> User Screen Settings Form Model (Matching screenshot media_1792299367458_8da6dd74.png)
                function getInitialUserScreenForm() {
                    return {
                        selectedModule: 'Select',
                        moduleScreens: []
                    };
                }
                $scope.userScreenForm = getInitialUserScreenForm();

                $scope.onUserScreenModuleChange = function () {
                    var mod = $scope.userScreenForm.selectedModule;
                    if (!mod || mod === 'Select') {
                        $scope.userScreenForm.moduleScreens = [];
                        return;
                    }
                    $scope.userScreenForm.moduleScreens = AppMgmtService.getScreenSettingsByModule(mod);
                    $scope.showToast('Loaded screen settings for ' + mod);
                };

                // App Mgmt -> Service Charge Form Model (Matching screenshots media_1792299794041_5df08e6a.png, media_1792299799929_c8e7c257.png, etc.)
                $scope.serviceChargeLists = AppMgmtService.getServiceChargeLists();
                $scope.serviceRoomTypes = AppMgmtService.getRoomTypes();

                function getInitialServiceChargeForm() {
                    return {
                        servicesType: 'Doctor Service', // 'Doctor Service' | 'Room Service' | 'General Service'
                        service: 'Select',
                        consultantDoctor: 'Select',
                        roomType: 'Select',
                        organizationType: 'Hospital',
                        indicator: 'Select',
                        copyTo: false,
                        copyToOrgType: 'Hospital',
                        hospitalShare: '',
                        doctorShare: '',
                        hospitalPercentage: '',
                        totalAmount: '',
                        incrDecrPct: 0
                    };
                }
                $scope.serviceChargeForm = getInitialServiceChargeForm();

                $scope.getAvailableServices = function () {
                    return $scope.serviceChargeLists[$scope.serviceChargeForm.servicesType] || ['Select'];
                };

                $scope.onServicesTypeChange = function () {
                    $scope.serviceChargeForm.service = 'Select';
                    $scope.serviceChargeForm.hospitalShare = '';
                    $scope.serviceChargeForm.doctorShare = '';
                    $scope.serviceChargeForm.hospitalPercentage = '';
                    $scope.serviceChargeForm.totalAmount = '';
                };

                $scope.onServiceChargeServiceChange = function () {
                    var srv = $scope.serviceChargeForm.service;
                    if (!srv || srv === 'Select') {
                        $scope.serviceChargeForm.hospitalShare = '';
                        $scope.serviceChargeForm.doctorShare = '';
                        $scope.serviceChargeForm.hospitalPercentage = '';
                        $scope.serviceChargeForm.totalAmount = '';
                        return;
                    }
                    var rate = AppMgmtService.getServiceChargeRates(srv);
                    if (rate) {
                        $scope.serviceChargeForm.hospitalShare = rate.hospitalShare;
                        $scope.serviceChargeForm.doctorShare = rate.doctorShare;
                        $scope.serviceChargeForm.hospitalPercentage = rate.hospitalPercentage;
                        $scope.serviceChargeForm.totalAmount = rate.totalAmount;
                        $scope.showToast('Loaded rates for ' + srv);
                    }
                };

                $scope.applyServiceChargeIncrDecr = function () {
                    var pct = Number($scope.serviceChargeForm.incrDecrPct) || 0;
                    if (pct === 0) {
                        $scope.showToast('Please enter an Incr./Decr. percentage.');
                        return;
                    }
                    var factor = 1 + (pct / 100);
                    if ($scope.serviceChargeForm.servicesType === 'Doctor Service') {
                        if ($scope.serviceChargeForm.totalAmount) {
                            $scope.serviceChargeForm.totalAmount = Math.round(Number($scope.serviceChargeForm.totalAmount) * factor);
                        }
                    } else {
                        if ($scope.serviceChargeForm.hospitalShare) {
                            $scope.serviceChargeForm.hospitalShare = Math.round(Number($scope.serviceChargeForm.hospitalShare) * factor);
                        }
                        if ($scope.serviceChargeForm.doctorShare) {
                            $scope.serviceChargeForm.doctorShare = Math.round(Number($scope.serviceChargeForm.doctorShare) * factor);
                        }
                    }
                    $scope.showToast('Applied ' + pct + '% change to current service charge.');
                };

                $scope.applyServiceChargeToAll = function () {
                    var pct = Number($scope.serviceChargeForm.incrDecrPct) || 0;
                    AppMgmtService.applyServicePercentageToAll(pct);
                    $scope.applyServiceChargeIncrDecr();
                    $scope.showToast('Applied ' + pct + '% change to ALL service charge records.');
                };

                // =========================================================
                // USER REPORT SETTINGS (Exact Match to Screenshots)
                // =========================================================
                function getInitialUserReportForm() {
                    return {
                        module: 'Select',
                        alphabeticalOrder: false,
                        reports: [],
                        selectedIndex: -1,
                        savedOrder: []
                    };
                }
                $scope.userReportForm = getInitialUserReportForm();

                $scope.onReportModuleChange = function () {
                    if (!$scope.userReportForm.module || $scope.userReportForm.module === 'Select') {
                        $scope.userReportForm.reports = [];
                        $scope.userReportForm.selectedIndex = -1;
                        $scope.userReportForm.alphabeticalOrder = false;
                        $scope.userReportForm.savedOrder = [];
                        return;
                    }
                    var list = AppMgmtService.getUserReports($scope.userReportForm.module);
                    $scope.userReportForm.reports = list;
                    $scope.userReportForm.savedOrder = angular.copy(list);
                    $scope.userReportForm.selectedIndex = list.length > 0 ? 0 : -1;
                    $scope.userReportForm.alphabeticalOrder = false;
                };

                $scope.selectReportItem = function (index) {
                    $scope.userReportForm.selectedIndex = index;
                };

                $scope.onAlphabeticalOrderToggle = function () {
                    if ($scope.userReportForm.alphabeticalOrder) {
                        $scope.userReportForm.savedOrder = angular.copy($scope.userReportForm.reports);
                        $scope.userReportForm.reports.sort(function (a, b) {
                            return a.localeCompare(b);
                        });
                    } else {
                        if ($scope.userReportForm.savedOrder && $scope.userReportForm.savedOrder.length > 0) {
                            $scope.userReportForm.reports = angular.copy($scope.userReportForm.savedOrder);
                        }
                    }
                };

                $scope.moveReportTop = function () {
                    var idx = $scope.userReportForm.selectedIndex;
                    if (idx > 0) {
                        var item = $scope.userReportForm.reports.splice(idx, 1)[0];
                        $scope.userReportForm.reports.unshift(item);
                        $scope.userReportForm.selectedIndex = 0;
                        $scope.userReportForm.alphabeticalOrder = false;
                    }
                };

                $scope.moveReportUp = function () {
                    var idx = $scope.userReportForm.selectedIndex;
                    if (idx > 0) {
                        var temp = $scope.userReportForm.reports[idx];
                        $scope.userReportForm.reports[idx] = $scope.userReportForm.reports[idx - 1];
                        $scope.userReportForm.reports[idx - 1] = temp;
                        $scope.userReportForm.selectedIndex = idx - 1;
                        $scope.userReportForm.alphabeticalOrder = false;
                    }
                };

                $scope.moveReportDown = function () {
                    var idx = $scope.userReportForm.selectedIndex;
                    if (idx >= 0 && idx < $scope.userReportForm.reports.length - 1) {
                        var temp = $scope.userReportForm.reports[idx];
                        $scope.userReportForm.reports[idx] = $scope.userReportForm.reports[idx + 1];
                        $scope.userReportForm.reports[idx + 1] = temp;
                        $scope.userReportForm.selectedIndex = idx + 1;
                        $scope.userReportForm.alphabeticalOrder = false;
                    }
                };

                $scope.moveReportBottom = function () {
                    var idx = $scope.userReportForm.selectedIndex;
                    if (idx >= 0 && idx < $scope.userReportForm.reports.length - 1) {
                        var item = $scope.userReportForm.reports.splice(idx, 1)[0];
                        $scope.userReportForm.reports.push(item);
                        $scope.userReportForm.selectedIndex = $scope.userReportForm.reports.length - 1;
                        $scope.userReportForm.alphabeticalOrder = false;
                    }
                };

                // =========================================================
                // SOFT LAB MODULE MODELS & HANDLERS (Exact Match to PDF)
                // =========================================================
                $scope.labDepartments = LabService.getDepartments();
                $scope.labReportStatuses = angular.copy(LabService.reportStatuses);
                $scope.labRegisteredPatients = LabService.registeredPatients;
                $scope.labAvailableTests = LabService.availableTests;

                // 1. Lab Registration Form (Pages 1, 2, 3)
                function getInitialLabRegForm() {
                    return {
                        patientType: 'Direct',
                        patientSearchQuery: '',
                        title: 'Mr',
                        firstName: '',
                        middleName: '',
                        lastName: '',
                        ageYears: '',
                        ageMonths: '',
                        ageDays: '',
                        gender: 'Male',
                        mobile: '',
                        consDoctor: 'Select',
                        referralDoctor: 'Select',
                        pro: 'Select',
                        testSearch: '',
                        testOrPackage: 'Test',
                        selectedTests: [
                            { code: 'CA125', name: 'CA 125', rate: 1200.00, dept: 'BIOCHEMISTRY' },
                            { code: 'CBP', name: 'COMPLETE BLOOD PICTURE', rate: 300.00, dept: 'HEAMATOLOGY' }
                        ],
                        totalAmount: 1500,
                        discType: 'No Discount',
                        discountAmount: 0,
                        discPercent: 0,
                        netAmount: 1500,
                        paidAmount: 1500,
                        dueAmount: 0,
                        remarks: '',
                        paymentType: 'Cash'
                    };
                }
                $scope.labRegForm = getInitialLabRegForm();
                $scope.showLabPatientDropdown = false;
                $scope.showTestDropdown = false;

                $scope.lookupLabPatient = function () {
                    var q = $scope.labRegForm.patientSearchQuery;
                    var p = LabService.searchPatient(q);
                    if (p) {
                        $scope.selectLabPatient(p);
                    } else {
                        $scope.showToast('No matching patient found for: ' + q);
                    }
                };

                $scope.selectLabPatient = function (p) {
                    $scope.labRegForm.title = p.title || 'Mr';
                    $scope.labRegForm.firstName = p.firstName || '';
                    $scope.labRegForm.middleName = p.middleName || '';
                    $scope.labRegForm.lastName = p.lastName || '';
                    $scope.labRegForm.ageYears = p.age || '';
                    $scope.labRegForm.gender = p.gender || 'Male';
                    $scope.labRegForm.mobile = p.mobile || '';
                    $scope.labRegForm.patientSearchQuery = p.name + ' | ' + p.id + ' | ' + p.mobile;
                    $scope.showLabPatientDropdown = false;
                    $scope.showToast('Loaded patient details for ' + p.name);
                };

                $scope.addLabTest = function (test) {
                    var exists = $scope.labRegForm.selectedTests.some(function (t) {
                        return t.code === test.code;
                    });
                    if (exists) {
                        $scope.showToast('Test [' + test.name + '] is already added.');
                        return;
                    }
                    $scope.labRegForm.selectedTests.push(angular.copy(test));
                    $scope.labRegForm.testSearch = '';
                    $scope.showTestDropdown = false;
                    $scope.calculateLabTotals();
                    $scope.showToast('Added test: ' + test.name);
                };

                $scope.removeLabTest = function (index) {
                    $scope.labRegForm.selectedTests.splice(index, 1);
                    $scope.calculateLabTotals();
                };

                $scope.calculateLabTotals = function () {
                    var total = 0;
                    $scope.labRegForm.selectedTests.forEach(function (t) {
                        total += Number(t.rate) || 0;
                    });
                    $scope.labRegForm.totalAmount = total;

                    var disc = 0;
                    if ($scope.labRegForm.discType === 'Percentage') {
                        var pct = Number($scope.labRegForm.discPercent) || 0;
                        disc = Math.round((total * pct) / 100);
                        $scope.labRegForm.discountAmount = disc;
                    } else if ($scope.labRegForm.discType === 'Fixed Amount') {
                        disc = Number($scope.labRegForm.discountAmount) || 0;
                        $scope.labRegForm.discPercent = total > 0 ? Math.round((disc / total) * 100) : 0;
                    } else {
                        $scope.labRegForm.discountAmount = 0;
                        $scope.labRegForm.discPercent = 0;
                        disc = 0;
                    }

                    var net = Math.max(0, total - disc);
                    $scope.labRegForm.netAmount = net;

                    var paid = Number($scope.labRegForm.paidAmount) || 0;
                    $scope.labRegForm.dueAmount = Math.max(0, net - paid);
                };

                // 2. Lab Due Collection Form (Page 3 bottom)
                function getInitialLabDueForm() {
                    return {
                        searchQuery: '',
                        totalAmount: 1500,
                        discPercent: 0,
                        discount: 0,
                        netAmount: 1500,
                        totalAmountReceived: 800,
                        dueAmount: 700,
                        patientName: 'BASSI SUNITHA',
                        labNo: 'L-2570',
                        mobileNo: '7893377413',
                        gender: 'Female',
                        address: 'Khaleelwadi, Nizamabad',
                        partyType: 'Patient',
                        paidAmount: 700,
                        balanceAmount: 0,
                        paymentType: 'Cash',
                        remarks: ''
                    };
                }
                $scope.labDueForm = getInitialLabDueForm();

                $scope.lookupLabDue = function () {
                    var q = $scope.labDueForm.searchQuery;
                    var rec = LabService.searchDueRecord(q) || LabService.searchPatient(q);
                    if (rec) {
                        $scope.labDueForm.patientName = rec.patientName || rec.name;
                        $scope.labDueForm.labNo = rec.labNo || ('L-' + Math.floor(2500 + Math.random() * 90));
                        $scope.labDueForm.mobileNo = rec.mobile || rec.mobileNo || '';
                        $scope.labDueForm.gender = rec.gender || 'Female';
                        $scope.labDueForm.address = rec.address || 'Nizamabad';
                        $scope.labDueForm.totalAmount = rec.totalAmount || 1500;
                        $scope.labDueForm.netAmount = rec.netAmount || 1500;
                        $scope.labDueForm.totalAmountReceived = rec.totalReceived || 800;
                        $scope.labDueForm.dueAmount = rec.dueAmount || 700;
                        $scope.labDueForm.paidAmount = rec.dueAmount || 700;
                        $scope.labDueForm.balanceAmount = 0;
                        $scope.showToast('Loaded Lab Due details for ' + $scope.labDueForm.patientName);
                    } else {
                        $scope.showToast('No lab due record found for query: ' + q);
                    }
                };

                // 3. Lab Charges / ChargeCode Details Form (Page 4 top)
                function getInitialLabChargesForm() {
                    return {
                        investigationType: 'Select',
                        investigation: 'Select',
                        department: 'Select',
                        organizationType: 'Hospital',
                        copyTo: false,
                        targetOrgType: 'Hospital',
                        hospitalPercentage: 30,
                        totalAmount: 500,
                        incrDecrPct: 0
                    };
                }
                $scope.labChargesForm = getInitialLabChargesForm();

                $scope.applyLabChargesIncrDecr = function () {
                    var pct = Number($scope.labChargesForm.incrDecrPct) || 0;
                    if (pct === 0) {
                        $scope.showToast('Please enter an Incr./Decr. percentage.');
                        return;
                    }
                    var factor = 1 + (pct / 100);
                    $scope.labChargesForm.totalAmount = Math.round($scope.labChargesForm.totalAmount * factor);
                    $scope.showToast('Applied ' + pct + '% change to Total Amount.');
                };

                $scope.applyLabChargesToAll = function () {
                    var pct = Number($scope.labChargesForm.incrDecrPct) || 0;
                    LabService.availableTests.forEach(function (t) {
                        t.rate = Math.round(t.rate * (1 + (pct / 100)));
                    });
                    $scope.applyLabChargesIncrDecr();
                    $scope.showToast('Applied ' + pct + '% adjustment to ALL Lab Charges.');
                };

                // 4. Lab Report Status (Page 4 bottom)
                $scope.reportStatusFilter = 'ALL';
                $scope.toggleLabReportStatus = function (item) {
                    if (item.status === 'Registered') {
                        item.status = 'Sample Collected';
                    } else if (item.status === 'Sample Collected') {
                        item.status = 'Completed';
                    } else {
                        item.status = 'Registered';
                    }
                    $scope.showToast('Report status for [' + item.patientName + ' - ' + item.testName + '] changed to: ' + item.status);
                };

                // 5. Test Cancellation Form (Page 5)
                function getInitialLabCancelForm() {
                    return {
                        searchQuery: '',
                        date: '07/02/2021',
                        totalAmount: 1500,
                        discAmt: 0,
                        netAmount: 1500,
                        paidAmount: 1500,
                        dueAmount: 0,
                        discPercent: 0,
                        refundedAmt: 1500,
                        discAdjAmt: 0,
                        patientType: 'Direct',
                        title: 'Mr',
                        firstName: 'BAIRI',
                        middleName: '',
                        lastName: 'GANGADHAR',
                        age: '42',
                        gender: 'Male',
                        mobile: '8897575783',
                        address: 'Subhashnagar, Nizamabad',
                        consDoctor: 'Select',
                        refType: 'Direct',
                        refName: 'Self',
                        referralDoctor: 'Select',
                        items: [
                            { selected: true, code: 'CA125', name: 'CA 125', amount: 1200 },
                            { selected: true, code: 'CBP', name: 'COMPLETE BLOOD PICTURE', amount: 300 }
                        ],
                        totalRefundAmt: 1500,
                        discountAdjusted: 0,
                        netRefundableAmount: 1500,
                        adjustments: 0,
                        payableAmount: 1500,
                        refundPaidAmount: 1500,
                        balanceAmount: 0,
                        authorization: 'Select',
                        paymentType: 'Cash',
                        remarks: ''
                    };
                }
                $scope.labCancelForm = getInitialLabCancelForm();

                $scope.lookupLabCancellation = function () {
                    var q = $scope.labCancelForm.searchQuery;
                    var p = LabService.searchPatient(q);
                    if (p) {
                        $scope.labCancelForm.firstName = p.firstName;
                        $scope.labCancelForm.lastName = p.lastName;
                        $scope.labCancelForm.mobile = p.mobile;
                        $scope.labCancelForm.gender = p.gender;
                        $scope.labCancelForm.title = p.title;
                        $scope.showToast('Loaded booking for cancellation: ' + p.name);
                    } else {
                        $scope.showToast('Patient booking loaded for cancellation.');
                    }
                };

                // 6. Test Details Form (Pages 6, 7, 8, 9)
                function getInitialLabTestDetailsForm() {
                    return {
                        department: 'BIOCHEMISTRY',
                        testName: 'Select',
                        fieldName: '',
                        unit: '',
                        fieldType: 'Qualitative',
                        availableTests: LabService.getTestsByDepartment('BIOCHEMISTRY'),
                        testFields: LabService.getTestFields()
                    };
                }
                $scope.labTestDetailsForm = getInitialLabTestDetailsForm();

                $scope.onLabDeptChange = function () {
                    var dept = $scope.labTestDetailsForm.department;
                    var tests = LabService.getTestsByDepartment(dept);
                    $scope.labTestDetailsForm.availableTests = tests;
                    $scope.labTestDetailsForm.testName = 'Select';
                };

                $scope.addLabTestField = function () {
                    if (!$scope.labTestDetailsForm.fieldName) {
                        $scope.showToast('Please enter Field Name.');
                        return;
                    }
                    var f = {
                        department: $scope.labTestDetailsForm.department,
                        testName: $scope.labTestDetailsForm.testName,
                        fieldName: $scope.labTestDetailsForm.fieldName,
                        unit: $scope.labTestDetailsForm.unit,
                        type: $scope.labTestDetailsForm.fieldType
                    };
                    LabService.addTestField(f);
                    $scope.labTestDetailsForm.testFields = LabService.getTestFields();
                    $scope.labTestDetailsForm.fieldName = '';
                    $scope.labTestDetailsForm.unit = '';
                    $scope.showToast('Test Field [' + f.fieldName + '] added for ' + f.testName);
                };

                // ==========================================
                // IN PATIENT MODULE STATE & METHODS
                // ==========================================

                // 1. In Patient List (Page 1 bottom)
                $scope.inPatientList = InPatientService.getInPatients();
                $scope.ipListFilter = { query: '', type: 'IP No' };

                // 2. IP Admission (Pages 2 & 3 top)
                $scope.ipAdmissionSearchList = InPatientService.admissionSearchList;
                $scope.showIpAdmissionDropdown = false;
                function getInitialIpAdmissionForm() {
                    return {
                        searchQuery: '',
                        opPatientNo: 'Select',
                        admissionDate: '07/02/2021',
                        admissionTime: '04:35:15',
                        patientName: '',
                        ageYears: '',
                        ageMonths: '',
                        ageDays: '',
                        gender: 'Male',
                        mobile: '',
                        address: '',
                        country: 'India',
                        state: 'Telangana',
                        city: 'Nizamabad',
                        area: 'Nizamabad',
                        zipCode: '503001',
                        email: '',
                        attendantRelation: 'Select',
                        attendantName: '',
                        attendantMobile: '',
                        doctor: 'Select',
                        secondaryDoctor: 'Select',
                        specialization: 'Select',
                        patientType: 'Direct',
                        department: 'Select',
                        referralName: 'Select',
                        icdCode: 'Select',
                        insuranceCovered: 'NA',
                        pro: 'Select',
                        referralDoctor: 'Select',
                        floor: '1st Floor',
                        roomType: 'ICU',
                        bed: 'ICU-01-01',
                        bedCharge: 2500,
                        foodPreference: 'Veg',
                        mlc: false,
                        vip: false,
                        quarantine: false
                    };
                }
                $scope.ipAdmissionForm = getInitialIpAdmissionForm();

                $scope.selectIpAdmissionPatient = function (p) {
                    $scope.ipAdmissionForm.searchQuery = p.name + ' | ' + (p.opNo || '') + ' | ' + p.mrNo + ' | ' + p.mobile;
                    $scope.ipAdmissionForm.patientName = p.name;
                    $scope.ipAdmissionForm.opPatientNo = p.opNo || 'Select';
                    $scope.ipAdmissionForm.ageYears = p.age || '45';
                    $scope.ipAdmissionForm.gender = p.gender || 'Male';
                    $scope.ipAdmissionForm.mobile = p.mobile;
                    $scope.ipAdmissionForm.address = p.address || 'Nizamabad';
                    $scope.ipAdmissionForm.city = p.city || 'Nizamabad';
                    $scope.ipAdmissionForm.state = p.state || 'Telangana';
                    $scope.ipAdmissionForm.country = p.country || 'India';
                    $scope.ipAdmissionForm.zipCode = p.zip || '503001';
                    $scope.showIpAdmissionDropdown = false;
                    $scope.showToast('Selected patient for IP Admission: ' + p.name);
                };

                // 3. Advance Payment (Pages 3 bottom & 4 top)
                function getInitialIpAdvanceForm() {
                    return {
                        mode: 'Advance', // 'Advance' or 'Refund'
                        searchQuery: '',
                        provisionalAmount: 15000,
                        netPayableAmount: 12500,
                        patientName: 'NAGAIAH G',
                        gender: 'Male',
                        age: '46 Yrs',
                        mobile: '9515181992',
                        address: 'H.No 4-12, Khaleelwadi, Nizamabad',
                        advanceReceived: 5000,
                        advanceRefunded: 0,
                        netAdvanceReceived: 5000,
                        advanceAmount: 2500,
                        netRefundableAmount: 5000,
                        refundAmount: 2000,
                        comments: '',
                        authorization: 'Select',
                        paymentType: 'Cash'
                    };
                }
                $scope.ipAdvanceForm = getInitialIpAdvanceForm();

                $scope.lookupIpAdvance = function () {
                    var p = InPatientService.searchPatient($scope.ipAdvanceForm.searchQuery);
                    if (p) {
                        $scope.ipAdvanceForm.patientName = p.patientName || p.name;
                        $scope.ipAdvanceForm.gender = p.gender || 'Male';
                        $scope.ipAdvanceForm.age = p.age ? (p.age + ' Yrs') : '45 Yrs';
                        $scope.ipAdvanceForm.mobile = p.mobile;
                        $scope.showToast('Patient loaded for Advance Payment: ' + $scope.ipAdvanceForm.patientName);
                    }
                };

                // 4. Transfer Doctor (Page 4 bottom)
                function getInitialIpTransferDocForm() {
                    return {
                        searchQuery: '',
                        patientName: 'NAGAIAH G',
                        floor: '1st Floor',
                        gender: 'Male',
                        roomType: 'ICU',
                        age: '46 Yrs',
                        bedNo: 'ICU-01-06',
                        doa: '06/02/2021',
                        currentPrimaryDoctor: 'Dr. Madhav Rao',
                        currentPrimarySpec: 'Cardiology',
                        currentPrimaryDept: 'CARDIOLOGY',
                        currentSecondaryDoctor: 'Dr. Rajesh Sharma',
                        currentSecondarySpec: 'General Medicine',
                        currentSecondaryDept: 'PATHOLOGY',
                        transferPrimaryDoctor: 'Select',
                        transferPrimarySpec: 'Select',
                        transferPrimaryDept: 'Select',
                        transferSecondaryDoctor: 'Select',
                        transferSecondarySpec: 'Select',
                        transferSecondaryDept: 'Select'
                    };
                }
                $scope.ipTransferDocForm = getInitialIpTransferDocForm();

                $scope.lookupIpTransferDoc = function () {
                    var p = InPatientService.searchPatient($scope.ipTransferDocForm.searchQuery);
                    if (p) {
                        $scope.ipTransferDocForm.patientName = p.patientName || p.name;
                        $scope.ipTransferDocForm.gender = p.gender || 'Male';
                        $scope.ipTransferDocForm.age = p.age ? (p.age + ' Yrs') : '45 Yrs';
                        $scope.ipTransferDocForm.bedNo = p.bed || 'ICU-01-06';
                        $scope.ipTransferDocForm.roomType = p.room || 'ICU';
                        $scope.showToast('Loaded patient for doctor transfer: ' + $scope.ipTransferDocForm.patientName);
                    }
                };

                // 5. Transfer Bed (Page 5 top)
                function getInitialIpTransferBedForm() {
                    return {
                        searchQuery: '',
                        patientName: 'NAGAIAH G',
                        gender: 'Male',
                        mobile: '9515181992',
                        age: '46 Yrs',
                        presentBedNo: 'ICU-01-06',
                        presentRoomType: 'ICU',
                        presentFloor: '1st Floor',
                        presentDateTime: '06/02/2021 21:38:50',
                        targetBedNo: 'SP-205',
                        targetRoomType: 'SPECIAL ROOMS',
                        targetFloor: '2nd Floor',
                        transferDate: '07/02/2021',
                        transferTime: '05:31:01 PM'
                    };
                }
                $scope.ipTransferBedForm = getInitialIpTransferBedForm();

                $scope.lookupIpTransferBed = function () {
                    var p = InPatientService.searchPatient($scope.ipTransferBedForm.searchQuery);
                    if (p) {
                        $scope.ipTransferBedForm.patientName = p.patientName || p.name;
                        $scope.ipTransferBedForm.gender = p.gender || 'Male';
                        $scope.ipTransferBedForm.mobile = p.mobile;
                        $scope.ipTransferBedForm.age = p.age ? (p.age + ' Yrs') : '45 Yrs';
                        $scope.ipTransferBedForm.presentBedNo = p.bed || 'ICU-01-06';
                        $scope.ipTransferBedForm.presentRoomType = p.room || 'ICU';
                        $scope.showToast('Loaded patient for bed transfer: ' + $scope.ipTransferBedForm.patientName);
                    }
                };

                // 6. Discharge Summary (Pages 5 bottom & 6)
                $scope.showIpDischargeDropdown = false;
                function getInitialIpDischargeForm() {
                    return {
                        searchQuery: '',
                        dischargeDate: '07/02/2021 05:31 PM',
                        patientName: 'NAGAIAH G',
                        gender: 'Male',
                        address: 'H.No 4-12, Khaleelwadi, Nizamabad',
                        consultantDoctor: 'Dr. Madhav Rao',
                        dischargeBy: 'Dr. Madhav Rao',
                        nextVisitDate: '14/02/2021',
                        templateType: 'Discharge Template',
                        conditionAtAdmission: 'Patient Condition at admission was stable with intermittent chest pain',
                        conditionAtDischarge: 'Discharge Condition: Hemodynamically stable, afebrile',
                        mainComplaints: 'Chest pain, shortness of breath, perspiration',
                        diagnosis: 'Acute Coronary Syndrome - NSTEMI',
                        surgery: 'Coronary Angiography performed on 04/02/2021',
                        treatmentGiven: 'Dual antiplatelet therapy, Statins, ACE inhibitors, IV fluids',
                        treatmentAdvised: 'Tab. Aspirin 75mg OD, Tab. Clopidogrel 75mg OD, Tab. Atorvastatin 40mg HS',
                        investigationsDone: 'ECG, 2D ECHO, Serum Electrolytes, Cardiac Enzymes, CBC',
                        nextVisitDateText: 'Review in OPD after 1 week or SOS in Emergency'
                    };
                }
                $scope.ipDischargeForm = getInitialIpDischargeForm();

                $scope.selectIpDischargePatient = function (p) {
                    $scope.ipDischargeForm.searchQuery = p.patientName + ' | ' + p.ipNo + ' | ' + p.mrNo + ' | ' + p.mobile;
                    $scope.ipDischargeForm.patientName = p.patientName;
                    $scope.ipDischargeForm.gender = p.gender;
                    $scope.ipDischargeForm.consultantDoctor = p.doctor || 'Dr. Madhav Rao';
                    $scope.ipDischargeForm.dischargeBy = p.doctor || 'Dr. Madhav Rao';
                    $scope.showIpDischargeDropdown = false;
                    $scope.showToast('Selected patient for Discharge Summary: ' + p.patientName);
                };

                // 7. Settlement Bill (Page 7)
                function getInitialIpSettlementForm() {
                    return {
                        searchQuery: '',
                        patientName: 'NAGAIAH G',
                        gender: 'Male',
                        mobile: '9515181992',
                        age: '46 Yrs',
                        address: 'Khaleelwadi, Nizamabad',
                        directOrgTpa: 'Direct',
                        patientType: 'Direct',
                        totalAmount: 38500,
                        discountType: 'No Discount',
                        discountPercent: 0,
                        flatAmount: 0,
                        netAmount: 38500,
                        advancePaidAmount: 15000,
                        pharmacyDueAmount: 3200,
                        labDueAmount: 2100,
                        refundableAmount: 0,
                        refundAmount: 0,
                        ipDueAmount: 23500,
                        totalDueAmount: 28800,
                        comments: '',
                        paymentType: 'Cash'
                    };
                }
                $scope.ipSettlementForm = getInitialIpSettlementForm();

                $scope.calculateIpSettlement = function () {
                    var total = Number($scope.ipSettlementForm.totalAmount) || 0;
                    var discType = $scope.ipSettlementForm.discountType;
                    var discAmt = 0;
                    if (discType === 'Percentage') {
                        var pct = Number($scope.ipSettlementForm.discountPercent) || 0;
                        discAmt = (total * pct) / 100;
                    } else if (discType === 'Flat Amount') {
                        discAmt = Number($scope.ipSettlementForm.flatAmount) || 0;
                    }
                    var net = Math.max(0, total - discAmt);
                    $scope.ipSettlementForm.netAmount = net;

                    var adv = Number($scope.ipSettlementForm.advancePaidAmount) || 0;
                    var ipDue = Math.max(0, net - adv);
                    $scope.ipSettlementForm.ipDueAmount = ipDue;

                    if (adv > net) {
                        $scope.ipSettlementForm.refundableAmount = adv - net;
                    } else {
                        $scope.ipSettlementForm.refundableAmount = 0;
                    }

                    var pharm = Number($scope.ipSettlementForm.pharmacyDueAmount) || 0;
                    var lab = Number($scope.ipSettlementForm.labDueAmount) || 0;
                    $scope.ipSettlementForm.totalDueAmount = ipDue + pharm + lab;
                };

                $scope.lookupIpSettlement = function () {
                    var p = InPatientService.searchPatient($scope.ipSettlementForm.searchQuery);
                    if (p) {
                        $scope.ipSettlementForm.patientName = p.patientName || p.name;
                        $scope.ipSettlementForm.gender = p.gender || 'Male';
                        $scope.ipSettlementForm.mobile = p.mobile;
                        $scope.ipSettlementForm.age = p.age ? (p.age + ' Yrs') : '45 Yrs';
                        $scope.ipSettlementForm.address = p.address || 'Nizamabad';
                        $scope.showToast('Loaded patient for Settlement Bill: ' + $scope.ipSettlementForm.patientName);
                    }
                };

                // 8. IP Wise Test Results (Page 8 top)
                $scope.ipTestSearchInput = '465';
                $scope.ipTestResultsPatient = {
                    name: 'RODOLLA DEVANAND',
                    ipNo: '465',
                    mrNo: '1667',
                    age: '44 Yrs 5 Mth 14 D',
                    gender: 'Male'
                };
                $scope.ipTestResults = InPatientService.getTestResults();
                $scope.selectAllIpTests = false;
                $scope.toggleSelectAllIpTests = function () {
                    $scope.selectAllIpTests = !$scope.selectAllIpTests;
                    angular.forEach($scope.ipTestResults, function (t) {
                        t.selected = $scope.selectAllIpTests;
                    });
                };

                $scope.lookupIpWiseTests = function () {
                    var q = ($scope.ipTestSearchInput || '').trim();
                    if (!q) return;

                    if (q === '465' || q.toLowerCase().indexOf('rodolla') !== -1 || q.toLowerCase().indexOf('devanand') !== -1) {
                        $scope.ipTestResultsPatient = {
                            name: 'RODOLLA DEVANAND',
                            ipNo: '465',
                            mrNo: '1667',
                            age: '44 Yrs 5 Mth 14 D',
                            gender: 'Male'
                        };
                        $scope.ipTestResults = InPatientService.getTestResults();
                        $scope.showToast('Fetched test results for RODOLLA DEVANAND (IP No: 465)');
                        return;
                    }

                    var p = InPatientService.searchPatient(q);
                    if (p) {
                        $scope.ipTestResultsPatient = {
                            name: p.patientName || p.name,
                            ipNo: p.ipNo || q,
                            mrNo: p.mrNo || '1919',
                            age: p.age ? (p.age.indexOf('Yrs') !== -1 ? p.age : (p.age + ' Yrs')) : '45 Yrs',
                            gender: p.gender || 'Male'
                        };
                        // Fetch test results for this patient
                        $scope.ipTestResults = [
                            { sno: 1, labNo: '2570', testName: 'COMPLETE BLOOD PICTURE', date: '06/02/2021 09:30:00 AM', selected: false },
                            { sno: 2, labNo: '2564', testName: 'SERUM CREATININE', date: '06/02/2021 09:30:00 AM', selected: false },
                            { sno: 3, labNo: '2564', testName: 'SERUM ELECTROLYTES (IP)', date: '06/02/2021 09:30:00 AM', selected: false },
                            { sno: 4, labNo: '2565', testName: 'ECG BED SIDED', date: '06/02/2021 10:15:20 AM', selected: false },
                            { sno: 5, labNo: '2566', testName: 'CRP (C-REACTIVE PROTEIN)', date: '06/02/2021 11:00:15 AM', selected: false }
                        ];
                        $scope.showToast('Fetched test results for ' + $scope.ipTestResultsPatient.name + ' (IP No: ' + $scope.ipTestResultsPatient.ipNo + ')');
                    } else {
                        $scope.ipTestResultsPatient = {
                            name: 'PATIENT ' + q.toUpperCase(),
                            ipNo: q,
                            mrNo: '1800',
                            age: '40 Yrs',
                            gender: 'Male'
                        };
                        $scope.ipTestResults = InPatientService.getTestResults();
                        $scope.showToast('Loaded test results for IP No: ' + q);
                    }
                };

                // 9. Medical Certificate (Pages 8-12)
                $scope.medicalCertTypes = [
                    'Fitness Certificate',
                    'Rest Advice Certificate',
                    'Medical Leave Certificate',
                    'Emergency Certificate',
                    'Essentiality Certificate',
                    'Birth Certificate',
                    'Death Certificate',
                    'Genuine Certificate'
                ];
                $scope.activeMedicalCertType = 'Fitness Certificate';

                function getInitialIpMedicalCertForm() {
                    return {
                        ipNo: '535',
                        opNo: '2327',
                        disease: 'Viral Pyrexia',
                        fitDate: '10/02/2021',
                        occupation: 'Govt Service',
                        birthDetails: 'Normal Full Term Male Child, Wt: 3.2 kg',
                        birthDate: '05/02/2021',
                        deathTime: '03:15 PM',
                        deathDate: '06/02/2021',
                        bill1: 'Inj. Ceftriaxone 1g - Rs. 350.00',
                        bill2: 'IV Fluids NS 500ml - Rs. 120.00',
                        bill3: 'Tab. Paracetamol 650mg - Rs. 30.00',
                        bill4: 'Syringe & Infusion Set - Rs. 85.00'
                    };
                }
                $scope.ipMedicalCertForm = getInitialIpMedicalCertForm();

                $scope.setMedicalCertType = function (type) {
                    $scope.activeMedicalCertType = type;
                    $scope.showToast('Switched to certificate: ' + type);
                };

                $scope.generateMedicalCertificate = function () {
                    $scope.showToast($scope.activeMedicalCertType + ' generated successfully for IP: ' + $scope.ipMedicalCertForm.ipNo);
                };

                // 10. Due Collection (Page 13)
                function getInitialIpDueForm() {
                    return {
                        searchQuery: '',
                        patientName: 'NAGAIAH G',
                        mrNo: '1919',
                        mobile: '9515181992',
                        gender: 'Male',
                        address: 'Khaleelwadi',
                        city: 'Nizamabad',
                        referralType: 'Direct',
                        referralName: 'Direct',
                        ipReceivable: 38500,
                        ipReceived: 15000,
                        orgBillNo: 'IPB-2021-982',
                        labReceivable: 2100,
                        labReceived: 0,
                        labDueAmount: 2100,
                        pharmacyReceivable: 3200,
                        pharmacyReceived: 0,
                        pharmacyDueAmount: 3200,
                        summaryReceivable: 43800,
                        summaryReceived: 15000,
                        summaryTotalDue: 28800,
                        partyType: 'Patient',
                        paymentType: 'Cash'
                    };
                }
                $scope.ipDueForm = getInitialIpDueForm();

                $scope.calculateIpDue = function () {
                    var ipRec = Number($scope.ipDueForm.ipReceivable) || 0;
                    var ipRcv = Number($scope.ipDueForm.ipReceived) || 0;
                    var labRec = Number($scope.ipDueForm.labReceivable) || 0;
                    var labRcv = Number($scope.ipDueForm.labReceived) || 0;
                    var pharmRec = Number($scope.ipDueForm.pharmacyReceivable) || 0;
                    var pharmRcv = Number($scope.ipDueForm.pharmacyReceived) || 0;

                    $scope.ipDueForm.labDueAmount = Math.max(0, labRec - labRcv);
                    $scope.ipDueForm.pharmacyDueAmount = Math.max(0, pharmRec - pharmRcv);

                    $scope.ipDueForm.summaryReceivable = ipRec + labRec + pharmRec;
                    $scope.ipDueForm.summaryReceived = ipRcv + labRcv + pharmRcv;
                    $scope.ipDueForm.summaryTotalDue = Math.max(0, $scope.ipDueForm.summaryReceivable - $scope.ipDueForm.summaryReceived);
                };

                $scope.lookupIpDue = function () {
                    var p = InPatientService.searchPatient($scope.ipDueForm.searchQuery);
                    if (p) {
                        $scope.ipDueForm.patientName = p.patientName || p.name;
                        $scope.ipDueForm.mrNo = p.mrNo;
                        $scope.ipDueForm.mobile = p.mobile;
                        $scope.ipDueForm.gender = p.gender || 'Male';
                        $scope.ipDueForm.address = p.address || 'Nizamabad';
                        $scope.ipDueForm.city = p.city || 'Nizamabad';
                        $scope.showToast('Loaded patient for Due Collection: ' + $scope.ipDueForm.patientName);
                    }
                };

                // 11. Discharge Cancellation (Page 14 top)
                function getInitialIpDischargeCancelForm() {
                    return {
                        searchQuery: '',
                        patientName: 'NAGAIAH G',
                        age: '46 Yrs',
                        gender: 'Male',
                        lastBed: 'ICU-01-06',
                        newBedAllotted: 'ICU-01-06',
                        doa: '06/02/2021',
                        dod: '07/02/2021'
                    };
                }
                $scope.ipDischargeCancelForm = getInitialIpDischargeCancelForm();

                $scope.lookupIpDischargeCancel = function () {
                    var p = InPatientService.searchPatient($scope.ipDischargeCancelForm.searchQuery);
                    if (p) {
                        $scope.ipDischargeCancelForm.patientName = p.patientName || p.name;
                        $scope.ipDischargeCancelForm.age = p.age ? (p.age + ' Yrs') : '46 Yrs';
                        $scope.ipDischargeCancelForm.gender = p.gender || 'Male';
                        $scope.ipDischargeCancelForm.lastBed = p.bed || 'ICU-01-06';
                        $scope.ipDischargeCancelForm.newBedAllotted = p.bed || 'ICU-01-06';
                        $scope.ipDischargeCancelForm.doa = p.doa || '06/02/2021';
                        $scope.showToast('Loaded discharged patient for cancellation: ' + $scope.ipDischargeCancelForm.patientName);
                    }
                };

                // App Mgmt Data Models
                $scope.appMgmt = {
                    procedures: AppMgmtService.getProcedures(),
                    services: AppMgmtService.getServices(),
                    referralDoctors: AppMgmtService.getReferralDoctors(),
                    procedureCharges: AppMgmtService.getProcedureCharges(),
                    screenSettings: AppMgmtService.getScreenSettings(),
                    serviceCharges: AppMgmtService.getServiceCharges(),
                    reportSettings: AppMgmtService.getReportSettings(),
                    newProcedure: { code: '', name: '', dept: 'General Medicine', rate: '', status: 'Active' },
                    newReferralDoc: { name: '', specialization: 'General Physician', hospital: '', mobile: '', commission: 10 }
                };

                $scope.addAppProcedure = function () {
                    if (!$scope.appMgmt.newProcedure.name || !$scope.appMgmt.newProcedure.rate) {
                        $scope.showToast('Please enter Procedure Name and Rate');
                        return;
                    }
                    var p = AppMgmtService.addProcedure(angular.copy($scope.appMgmt.newProcedure));
                    $scope.appMgmt.newProcedure = { code: '', name: '', dept: 'General Medicine', rate: '', status: 'Active' };
                    $scope.showToast('Procedure [' + p.name + '] added successfully!');
                };

                $scope.addAppReferralDoc = function () {
                    if (!$scope.appMgmt.newReferralDoc.name) {
                        $scope.showToast('Please enter Doctor Name');
                        return;
                    }
                    var d = AppMgmtService.addReferralDoctor(angular.copy($scope.appMgmt.newReferralDoc));
                    $scope.appMgmt.newReferralDoc = { name: '', specialization: 'General Physician', hospital: '', mobile: '', commission: 10 };
                    $scope.showToast('Referral Doctor [' + d.name + '] added successfully!');
                };

                // 4. Live Clock & Formatted Date
                function pad(n) { return n < 10 ? '0' + n : n; }
                function refreshClock() {
                    var now = new Date();
                    $scope.currentDate = pad(now.getDate()) + '/' + pad(now.getMonth() + 1) + '/' + now.getFullYear();
                    $scope.loginTime = pad(now.getHours()) + ':' + pad(now.getMinutes()) + ':' + pad(now.getSeconds());
                    var hours = now.getHours();
                    var minutes = pad(now.getMinutes());
                    var ampm = hours >= 12 ? 'PM' : 'AM';
                    hours = hours % 12;
                    hours = hours ? hours : 12;
                    $scope.formattedTime = hours + ':' + minutes + ' ' + ampm;
                }
                refreshClock();
                var clockTimer = $interval(refreshClock, 1000);
                $scope.$on('$destroy', function () {
                    $interval.cancel(clockTimer);
                });

                // 5. Patient Queue
                $scope.patientQueue = PatientService.getPatients();
                $scope.selectPatient = function (p) {
                    $scope.showToast('Selected patient: ' + p.name + ' (' + p.id + ')');
                };

                // -------------------------------------------------------------
                // 6. DUE COLLECTION FORM (Matching latest screenshot)
                // -------------------------------------------------------------
                function getInitialDueForm() {
                    return {
                        searchQuery: '',
                        // Bill Details
                        totalAmount: '',
                        discPercent: '',
                        discount: '',
                        netAmount: '',
                        totalReceived: '',
                        dueAmount: '',
                        // Patient Details
                        patientName: '',
                        mrNo: '',
                        mobileNo: '',
                        gender: '',
                        address: '',
                        // Payment Details
                        partyType: 'Patient',
                        paidAmount: '',
                        balanceAmount: 0,
                        paymentType: 'Cash',
                        remarks: ''
                    };
                }
                $scope.dueForm = getInitialDueForm();

                $scope.lookupDuePatient = function () {
                    if (!$scope.dueForm.searchQuery || !$scope.dueForm.searchQuery.trim()) {
                        $scope.showToast('Please enter Name, MR, OP, Bill, or Mobile to search.');
                        return;
                    }
                    var match = PatientService.searchPatient($scope.dueForm.searchQuery);
                    if (match) {
                        $scope.dueForm.totalAmount = match.totalAmt || 500;
                        $scope.dueForm.discount = match.discount || 0;
                        $scope.dueForm.discPercent = match.discount ? Math.round((match.discount / match.totalAmt) * 100) : 0;
                        $scope.dueForm.netAmount = match.netAmt || 500;
                        $scope.dueForm.totalReceived = match.receivedAmt || 200;
                        $scope.dueForm.dueAmount = match.dueAmt || 300;

                        $scope.dueForm.patientName = match.name;
                        $scope.dueForm.mrNo = match.mrNo || 'MR-9021';
                        $scope.dueForm.mobileNo = match.mobile || '9876543210';
                        $scope.dueForm.gender = match.gender || 'Male';
                        $scope.dueForm.address = match.address || 'Housing Board Colony, Nizamabad';

                        $scope.dueForm.paidAmount = '';
                        $scope.dueForm.balanceAmount = match.dueAmt || 300;
                        $scope.showToast('Loaded due bill for ' + match.name + ' (Due: ₹' + match.dueAmt + ')');
                    } else {
                        // Prefill dummy record for demonstration
                        $scope.dueForm.totalAmount = 500;
                        $scope.dueForm.discPercent = 0;
                        $scope.dueForm.discount = 0;
                        $scope.dueForm.netAmount = 500;
                        $scope.dueForm.totalReceived = 200;
                        $scope.dueForm.dueAmount = 300;

                        $scope.dueForm.patientName = $scope.dueForm.searchQuery;
                        $scope.dueForm.mrNo = 'MR-' + Math.floor(1000 + Math.random() * 9000);
                        $scope.dueForm.mobileNo = '9848012345';
                        $scope.dueForm.gender = 'Male';
                        $scope.dueForm.address = 'Nizamabad';

                        $scope.dueForm.paidAmount = '';
                        $scope.dueForm.balanceAmount = 300;
                        $scope.showToast('Loaded due record for "' + $scope.dueForm.searchQuery + '"');
                    }
                };

                $scope.calcDueBalance = function () {
                    var due = Number($scope.dueForm.dueAmount) || 0;
                    var paid = Number($scope.dueForm.paidAmount) || 0;
                    $scope.dueForm.balanceAmount = BillingService.calculateDueCollection(due, paid);
                };

                // -------------------------------------------------------------
                // 7. CONSULTATION FORM
                // -------------------------------------------------------------
                function getInitialConsultForm() {
                    return {
                        dob: '07/04/1985',
                        gender: 'Male',
                        visitNo: '1',
                        attendantMobile: '',
                        attendantRelation: 'Select',
                        attendantName: '',
                        consultantDoctor: 'Select',
                        patientType: 'Select',
                        department: 'Select',
                        referralName: 'Select',
                        specialization: 'Select',
                        insuranceCovered: 'Select',
                        referralDoctor: 'Select',
                        pro: 'Select',
                        weight: '',
                        bp: '',
                        temperature: '',
                        fee: 0,
                        discountType: 'No Discount',
                        discountPercent: 0,
                        discountAmt: 0,
                        netAmount: 0,
                        paidAmount: 0,
                        dueAmount: 0,
                        paymentType: 'Cash',
                        remarks: ''
                    };
                }
                $scope.consultForm = getInitialConsultForm();

                $scope.onDoctorSelect = function () {
                    var doc = PatientService.doctors[$scope.consultForm.consultantDoctor];
                    if (doc) {
                        $scope.consultForm.department = doc.dept;
                        $scope.consultForm.specialization = doc.spec;
                        $scope.consultForm.fee = doc.fee;
                        $scope.calcConsultBilling();
                        $scope.showToast('Selected ' + $scope.consultForm.consultantDoctor + ' (' + doc.spec + ')');
                    }
                };

                $scope.calcConsultBilling = function () {
                    var b = BillingService.calculateBilling($scope.consultForm.fee, $scope.consultForm.discountAmt, $scope.consultForm.paidAmount);
                    $scope.consultForm.netAmount = b.netAmount;
                    $scope.consultForm.dueAmount = b.dueAmount;
                };

                $scope.calcConsultDiscountPct = function () {
                    $scope.consultForm.discountAmt = BillingService.calculateDiscountAmount($scope.consultForm.fee, $scope.consultForm.discountPercent);
                    $scope.calcConsultBilling();
                };

                $scope.onConsultDiscountType = function () {
                    $scope.consultForm.discountPercent = BillingService.getDiscountPercentForType($scope.consultForm.discountType);
                    $scope.calcConsultDiscountPct();
                };

                $scope.addReferralDoctor = function () {
                    var name = prompt('Enter New Referral Doctor Name:');
                    if (name && name.trim()) {
                        $scope.consultForm.referralDoctor = name.trim();
                        $scope.showToast('Referral Doctor added: ' + name.trim());
                    }
                };

                // -------------------------------------------------------------
                // 8. CONSULTATION CANCELLATION FORM
                // -------------------------------------------------------------
                function getInitialCancelForm() {
                    return {
                        searchQuery: '',
                        consultantDoctor: 'Dr. Rajesh Sharma',
                        totalAmount: '500',
                        discount: '0',
                        netAmount: '500',
                        totalAmtReceived: '500',
                        dueAmount: '0',
                        discPercent: '0',
                        title: 'Mr',
                        firstName: 'Robert',
                        middleName: '',
                        lastName: 'Fox',
                        visitNo: '1',
                        mobile: '9876543210',
                        gender: 'Male',
                        age: '45',
                        referralDoctor: 'Dr. A. K. Verma',
                        refType: 'Hospital',
                        refName: 'Care Hospital',
                        totalRefundAmt: '500',
                        discountAdjusted: '0',
                        netRefundableAmt: '500',
                        adjustment: '0',
                        payableAmount: '500',
                        refundPaidAmount: '500'
                    };
                }
                $scope.cancelForm = getInitialCancelForm();

                $scope.lookupCancelPatient = function () {
                    if (!$scope.cancelForm.searchQuery || !$scope.cancelForm.searchQuery.trim()) {
                        $scope.showToast('Please enter Name, MR, OP, or Mobile to search.');
                        return;
                    }
                    var match = PatientService.searchPatient($scope.cancelForm.searchQuery);
                    if (match) {
                        $scope.cancelForm.firstName = match.firstName || match.name;
                        $scope.cancelForm.lastName = match.lastName || '';
                        $scope.cancelForm.mobile = match.mobile;
                        $scope.cancelForm.gender = match.gender;
                        $scope.cancelForm.age = match.age;
                        $scope.showToast('Loaded details for ' + match.name);
                    } else {
                        $scope.showToast('Loaded consultation bill details for "' + $scope.cancelForm.searchQuery + '"');
                    }
                };

                // -------------------------------------------------------------
                // 9. REGISTRATION FORM
                // -------------------------------------------------------------
                function getInitialRegForm() {
                    var today = new Date();
                    var nextYear = new Date();
                    nextYear.setFullYear(today.getFullYear() + 1);

                    return {
                        regDate: pad(today.getDate()) + '/' + pad(today.getMonth() + 1) + '/' + today.getFullYear(),
                        regExpiryDate: pad(nextYear.getDate()) + '/' + pad(nextYear.getMonth() + 1) + '/' + nextYear.getFullYear(),
                        title: 'Mr',
                        firstName: '',
                        middleName: '',
                        lastName: '',
                        religion: 'Select',
                        dob: '',
                        ageYears: '',
                        ageMonths: '',
                        ageDays: '',
                        gender: 'Male',
                        bloodGroup: 'Select',
                        mobileNo: '',
                        landline: '',
                        email: '',
                        maritalStatus: 'Select',
                        address: 'NIZAMABAD',
                        zipCode: '',
                        country: 'INDIA',
                        state: 'TELANGANA',
                        city: 'NIZAMABAD',
                        area: 'NIZAMABAD',
                        educationLevel: 'Select',
                        occupation: 'Select',
                        birthMark: '',
                        comments: '',
                        regFee: 0,
                        discountType: 'No Discount',
                        discountPercent: 0,
                        discountAmt: 0,
                        netAmount: 0,
                        paidAmount: 0,
                        dueAmount: 0,
                        paymentType: 'Cash'
                    };
                }
                $scope.regForm = getInitialRegForm();
                $scope.photoDataUrl = null;
                $scope.photoFileName = '';

                $scope.calculateAgeFromDob = function () {
                    var ageObj = PatientService.calculateAgeFromDob($scope.regForm.dob);
                    if (ageObj) {
                        $scope.regForm.ageYears = ageObj.years;
                        $scope.regForm.ageMonths = ageObj.months;
                        $scope.regForm.ageDays = ageObj.days;
                    }
                };

                $scope.recalculateBilling = function () {
                    var b = BillingService.calculateBilling($scope.regForm.regFee, $scope.regForm.discountAmt, $scope.regForm.paidAmount);
                    $scope.regForm.netAmount = b.netAmount;
                    $scope.regForm.dueAmount = b.dueAmount;
                };

                $scope.discountPercentChanged = function () {
                    $scope.regForm.discountAmt = BillingService.calculateDiscountAmount($scope.regForm.regFee, $scope.regForm.discountPercent);
                    $scope.recalculateBilling();
                };

                $scope.discountTypeChanged = function () {
                    $scope.regForm.discountPercent = BillingService.getDiscountPercentForType($scope.regForm.discountType);
                    $scope.discountPercentChanged();
                };

                $scope.photoSelected = function (input) {
                    if (input.files && input.files[0]) {
                        var file = input.files[0];
                        $scope.photoFileName = file.name;
                        var reader = new FileReader();
                        reader.onload = function (e) {
                            $scope.$apply(function () {
                                $scope.photoDataUrl = e.target.result;
                                $scope.showToast('Photograph loaded: ' + file.name);
                            });
                        };
                        reader.readAsDataURL(file);
                    }
                };

                $scope.previewPhoto = function () {
                    if ($scope.photoDataUrl) {
                        var win = $window.open();
                        win.document.write('<img src="' + $scope.photoDataUrl + '" style="max-width:100%; height:auto;">');
                    } else {
                        $scope.showToast('No photograph selected to preview.');
                    }
                };

                $scope.removePhoto = function () {
                    $scope.photoDataUrl = null;
                    $scope.photoFileName = '';
                    var input = document.getElementById('patientPhotoInput');
                    if (input) input.value = '';
                    $scope.showToast('Photograph removed.');
                };

                // -------------------------------------------------------------
                // 10. PROCEDURE FORM (Booked vs Not Booked)
                // -------------------------------------------------------------
                function getInitialProcedureForm(mode) {
                    var isBooked = (mode !== 'Not Booked');
                    return {
                        bookingMode: isBooked ? 'Booked' : 'Not Booked',
                        searchQuery: isBooked ? '1133' : '',
                        title: isBooked ? 'Mr' : 'Select',
                        firstName: isBooked ? 'SHIVA LINGAM' : '',
                        middleName: isBooked ? 'CHELIMELA' : '',
                        lastName: '',
                        ageYears: isBooked ? '60' : '',
                        ageMonths: isBooked ? '00' : '',
                        ageDays: isBooked ? '00' : '',
                        gender: 'Male',
                        address: isBooked ? 'MARAMPALLY' : '',
                        mobileNo: isBooked ? '9766173106' : '',
                        patientType: 'Direct',
                        referralName: 'Self',
                        consultantDoctor: 'Non-Doctor',
                        procedureName: isBooked ? 's' : '',
                        pro: 'Select',
                        referralDoctor: 'Select',
                        totalAmount: '',
                        discountType: 'No Discount',
                        discountPercent: '',
                        discountAmt: '',
                        netAmount: '',
                        paidAmount: '',
                        dueAmount: '',
                        paymentType: 'Cash'
                    };
                }
                $scope.procedureForm = getInitialProcedureForm('Booked');

                $scope.onProcedureModeChange = function (mode) {
                    $scope.procedureForm.bookingMode = mode;
                    if (mode === 'Not Booked') {
                        $scope.procedureForm = getInitialProcedureForm('Not Booked');
                        $scope.showToast('Switched to Not Booked procedure mode (Manual Entry).');
                    } else {
                        $scope.procedureForm = getInitialProcedureForm('Booked');
                        $scope.showToast('Switched to Booked procedure mode (Patient Lookup).');
                    }
                };

                $scope.lookupProcedurePatient = function () {
                    if (!$scope.procedureForm.searchQuery || !$scope.procedureForm.searchQuery.trim()) {
                        $scope.showToast('Please enter Name, OP, MR, or Mobile to search.');
                        return;
                    }
                    var match = ProcedureService.searchBookedPatient($scope.procedureForm.searchQuery) ||
                                PatientService.searchPatient($scope.procedureForm.searchQuery);
                    if (match) {
                        $scope.procedureForm.title = match.title || 'Mr';
                        $scope.procedureForm.firstName = match.firstName || match.name;
                        $scope.procedureForm.middleName = match.middleName || '';
                        $scope.procedureForm.lastName = match.lastName || '';
                        $scope.procedureForm.ageYears = match.ageYears || match.age || '45';
                        $scope.procedureForm.ageMonths = match.ageMonths || '00';
                        $scope.procedureForm.ageDays = match.ageDays || '00';
                        $scope.procedureForm.gender = match.gender || 'Male';
                        $scope.procedureForm.address = match.address || 'Nizamabad';
                        $scope.procedureForm.mobileNo = match.mobile || match.mobileNo || '';
                        $scope.procedureForm.procedureName = match.procedureName || 'ECG (12 Lead)';
                        $scope.procedureForm.totalAmount = match.totalAmount || match.totalAmt || 250;
                        $scope.procedureForm.netAmount = $scope.procedureForm.totalAmount;
                        $scope.procedureForm.paidAmount = $scope.procedureForm.totalAmount;
                        $scope.procedureForm.dueAmount = 0;
                        $scope.showToast('Loaded booked patient: ' + $scope.procedureForm.firstName);
                    } else {
                        $scope.showToast('No booking found for "' + $scope.procedureForm.searchQuery + '"');
                    }
                };

                $scope.calcProcedureBilling = function () {
                    var total = Number($scope.procedureForm.totalAmount) || 0;
                    var disc = Number($scope.procedureForm.discountAmt) || 0;
                    var paid = Number($scope.procedureForm.paidAmount) || 0;
                    var net = Math.max(0, total - disc);
                    $scope.procedureForm.netAmount = net;
                    $scope.procedureForm.dueAmount = Math.max(0, net - paid);
                };

                $scope.calcProcedureDiscount = function () {
                    var total = Number($scope.procedureForm.totalAmount) || 0;
                    var pct = Number($scope.procedureForm.discountPercent) || 0;
                    if (pct > 0 && total > 0) {
                        $scope.procedureForm.discountAmt = Math.round((total * pct) / 100);
                    } else if (pct === 0) {
                        $scope.procedureForm.discountAmt = 0;
                    }
                    $scope.calcProcedureBilling();
                };

                // -------------------------------------------------------------
                // 11. PROCEDURE CANCELLATION FORM (Matching latest screenshot)
                // -------------------------------------------------------------
                function getInitialProcCancelForm() {
                    return {
                        searchQuery: '',
                        billNo: '',
                        totalAmount: '',
                        discountAmt: '',
                        netAmount: '',
                        totalReceived: '',
                        refundedAmt: '',
                        discAdjusted: '',
                        dueAmount: '',
                        discountPercent: '',
                        discountType: 'No Discount',
                        title: 'Mr',
                        firstName: '',
                        middleName: '',
                        lastName: '',
                        age: '',
                        mobile: '',
                        gender: 'Male',
                        refType: 'Select',
                        refName: 'Select',
                        referralDoctor: 'Select'
                    };
                }
                $scope.procCancelForm = getInitialProcCancelForm();

                $scope.lookupProcCancelPatient = function () {
                    if (!$scope.procCancelForm.searchQuery || !$scope.procCancelForm.searchQuery.trim()) {
                        $scope.showToast('Please enter Name, MR, OP, BillNo, or Mobile to search.');
                        return;
                    }
                    var q = $scope.procCancelForm.searchQuery.trim();
                    var match = ProcedureService.searchProcedureBill(q) ||
                                ProcedureService.searchBookedPatient(q) ||
                                PatientService.searchPatient(q);
                    if (match) {
                        $scope.procCancelForm.billNo = match.billNo || ('PB-' + (match.opNo || Math.floor(1000 + Math.random() * 9000)));
                        $scope.procCancelForm.totalAmount = match.totalAmount || match.totalAmt || 250;
                        $scope.procCancelForm.discountAmt = match.discountAmt || match.discount || 0;
                        $scope.procCancelForm.netAmount = match.netAmount || match.netAmt || 250;
                        $scope.procCancelForm.totalReceived = match.totalReceived || match.paidAmount || match.receivedAmt || 250;
                        $scope.procCancelForm.refundedAmt = match.refundedAmt || match.totalReceived || match.paidAmount || 250;
                        $scope.procCancelForm.discAdjusted = match.discAdjusted || 0;
                        $scope.procCancelForm.dueAmount = match.dueAmount || match.dueAmt || 0;
                        $scope.procCancelForm.discountPercent = match.discountPercent || 0;
                        $scope.procCancelForm.discountType = match.discountType || 'No Discount';

                        $scope.procCancelForm.title = match.title || 'Mr';
                        $scope.procCancelForm.firstName = match.firstName || match.name || '';
                        $scope.procCancelForm.middleName = match.middleName || '';
                        $scope.procCancelForm.lastName = match.lastName || '';
                        $scope.procCancelForm.age = match.age || (match.ageYears ? (match.ageYears + '/' + (match.ageMonths || '00') + '/' + (match.ageDays || '00')) : '45');
                        $scope.procCancelForm.mobile = match.mobile || match.mobileNo || '';
                        $scope.procCancelForm.gender = match.gender || 'Male';
                        $scope.procCancelForm.refType = match.refType || match.patientType || 'Direct';
                        $scope.procCancelForm.refName = match.refName || match.referralName || 'Self';
                        $scope.procCancelForm.referralDoctor = match.referralDoctor || 'Select';

                        $scope.showToast('Loaded Bill ' + $scope.procCancelForm.billNo + ' for ' + $scope.procCancelForm.firstName);
                    } else {
                        // Prefill demo procedure cancellation record for query
                        $scope.procCancelForm.billNo = 'PB-9102';
                        $scope.procCancelForm.totalAmount = 250;
                        $scope.procCancelForm.discountAmt = 0;
                        $scope.procCancelForm.netAmount = 250;
                        $scope.procCancelForm.totalReceived = 250;
                        $scope.procCancelForm.refundedAmt = 250;
                        $scope.procCancelForm.discAdjusted = 0;
                        $scope.procCancelForm.dueAmount = 0;
                        $scope.procCancelForm.discountPercent = 0;
                        $scope.procCancelForm.discountType = 'No Discount';

                        $scope.procCancelForm.title = 'Mr';
                        $scope.procCancelForm.firstName = q;
                        $scope.procCancelForm.middleName = '';
                        $scope.procCancelForm.lastName = '';
                        $scope.procCancelForm.age = '50/00/00';
                        $scope.procCancelForm.mobile = '9848011223';
                        $scope.procCancelForm.gender = 'Male';
                        $scope.procCancelForm.refType = 'Select';
                        $scope.procCancelForm.refName = 'Select';
                        $scope.procCancelForm.referralDoctor = 'Select';

                        $scope.showToast('Loaded procedure record for "' + q + '"');
                    }
                };

                // -------------------------------------------------------------
                // 11. ACCOUNTING MODULE (Transactions)
                // -------------------------------------------------------------
                function getInitialAccountTxnForm() {
                    return {
                        transactionType: 'Income(Cr)',
                        partyType: 'Select',
                        patientDetailsChecked: false,
                        billingDepartment: 'Accounting',
                        originalBillNo: '',
                        accountHead: 'Hospital Revenue',
                        accountSubHead: 'Inpatient Services',
                        accountSubSubHead: 'Bed Charges',
                        partyName: '',
                        contactNo: '',
                        address: '',
                        paymentType: 'Cash',
                        amount: '',
                        remarks: ''
                    };
                }

                $scope.billingDepartments = AccountService.billingDepartments;
                $scope.partyTypes = AccountService.partyTypes;
                $scope.accountHeads = AccountService.getAccountHeads();
                $scope.accountSubHeads = AccountService.getSubHeads('Hospital Revenue');
                $scope.accountSubSubHeads = AccountService.getSubSubHeads('Hospital Revenue', 'Inpatient Services');

                $scope.accountTxnForm = getInitialAccountTxnForm();
                $scope.accountTransactions = AccountService.getTransactions();
                $scope.showAccountTxnSearchModal = false;

                $scope.onAccountHeadChange = function () {
                    $scope.accountSubHeads = AccountService.getSubHeads($scope.accountTxnForm.accountHead);
                    if ($scope.accountSubHeads && $scope.accountSubHeads.length > 0) {
                        $scope.accountTxnForm.accountSubHead = $scope.accountSubHeads[0];
                    } else {
                        $scope.accountTxnForm.accountSubHead = '';
                    }
                    $scope.onAccountSubHeadChange();
                };

                $scope.onAccountSubHeadChange = function () {
                    $scope.accountSubSubHeads = AccountService.getSubSubHeads($scope.accountTxnForm.accountHead, $scope.accountTxnForm.accountSubHead);
                    if ($scope.accountSubSubHeads && $scope.accountSubSubHeads.length > 0) {
                        $scope.accountTxnForm.accountSubSubHead = $scope.accountSubSubHeads[0];
                    } else {
                        $scope.accountTxnForm.accountSubSubHead = '';
                    }
                };

                $scope.onPartyTypeChange = function () {
                    if ($scope.accountTxnForm.partyType === 'Patient') {
                        $scope.accountTxnForm.patientDetailsChecked = true;
                    }
                };

                $scope.lookupAccountTransaction = function () {
                    var q = ($scope.accountTxnForm.originalBillNo || '').trim().toLowerCase();
                    var match = null;
                    if (q) {
                        match = $scope.accountTransactions.find(function (t) {
                            return (t.originalBillNo && t.originalBillNo.toLowerCase().includes(q)) ||
                                   (t.voucherNo && t.voucherNo.toLowerCase().includes(q)) ||
                                   (t.partyName && t.partyName.toLowerCase().includes(q));
                        });
                    }
                    if (match) {
                        $scope.loadAccountTransaction(match);
                        $scope.showToast('Transaction loaded: ' + match.voucherNo + ' (' + match.partyName + ')');
                    } else {
                        $scope.showAccountTxnSearchModal = true;
                    }
                };

                $scope.loadAccountTransaction = function (t) {
                    $scope.accountTxnForm.transactionType = t.transactionType || 'Income(Cr)';
                    $scope.accountTxnForm.partyType = t.partyType || 'Select';
                    $scope.accountTxnForm.billingDepartment = t.billingDepartment || 'Accounting';
                    $scope.accountTxnForm.originalBillNo = t.originalBillNo || '';
                    $scope.accountTxnForm.accountHead = t.accountHead || 'Hospital Revenue';
                    $scope.accountSubHeads = AccountService.getSubHeads($scope.accountTxnForm.accountHead);
                    $scope.accountTxnForm.accountSubHead = t.accountSubHead || ($scope.accountSubHeads[0] || '');
                    $scope.accountSubSubHeads = AccountService.getSubSubHeads($scope.accountTxnForm.accountHead, $scope.accountTxnForm.accountSubHead);
                    $scope.accountTxnForm.accountSubSubHead = t.accountSubSubHead || ($scope.accountSubSubHeads[0] || '');
                    $scope.accountTxnForm.partyName = t.partyName || '';
                    $scope.accountTxnForm.contactNo = t.contactNo || '';
                    $scope.accountTxnForm.address = t.address || '';
                    $scope.accountTxnForm.paymentType = t.paymentType || 'Cash';
                    $scope.accountTxnForm.amount = t.amount || '';
                    $scope.accountTxnForm.remarks = t.remarks || '';
                    $scope.showAccountTxnSearchModal = false;
                };

                // -------------------------------------------------------------
                // 12. REPORTS MODULE
                // -------------------------------------------------------------
                $scope.reportsCatalog = ReportsService.catalog;
                $scope.reportsUsersList = ReportsService.usersList;
                $scope.reportsModulesList = ReportsService.modulesList;
                $scope.currentCategoryReports = ReportsService.catalog['Application Mgmt'] || [];
                $scope.selectedReport = $scope.currentCategoryReports[0] || null;
                $scope.generatedReport = null;

                $scope.reportParams = {
                    fromDate: '07/02/2021',
                    fromTime: '12:00:00 AM',
                    toDate: '07/02/2021',
                    toTime: '05:58:00 PM',
                    fromMrno: '',
                    toMrno: '',
                    year1: '2021',
                    year2: '2021',
                    userName: 'All',
                    module: 'All',
                    fromBill: '',
                    toBill: ''
                };

                $scope.loadReportListForCategory = function (category) {
                    $scope.currentCategoryReports = ReportsService.catalog[category] || [];
                    if ($scope.currentCategoryReports.length > 0) {
                        $scope.selectReportRadio($scope.currentCategoryReports[0]);
                    } else {
                        $scope.selectedReport = null;
                    }
                    $scope.generatedReport = null;
                };

                $scope.selectReportRadio = function (rep) {
                    $scope.selectedReport = rep;
                    $scope.generatedReport = null;
                };

                $scope.getReport = function () {
                    if (!$scope.selectedReport) {
                        $scope.showToast('Please select a report first.');
                        return;
                    }
                    $scope.generatedReport = ReportsService.generateReportData($scope.activeAction, $scope.selectedReport.name, $scope.reportParams);
                    $scope.showToast('Generated ' + $scope.selectedReport.name + ' report successfully.');
                };

                $scope.cancelReport = function () {
                    $scope.reportParams.fromMrno = '';
                    $scope.reportParams.toMrno = '';
                    $scope.reportParams.fromBill = '';
                    $scope.reportParams.toBill = '';
                    $scope.reportParams.userName = 'All';
                    $scope.reportParams.module = 'All';
                    $scope.reportParams.year1 = '2021';
                    $scope.reportParams.year2 = '2021';
                    $scope.generatedReport = null;
                    $scope.showToast('Report parameters cleared.');
                };

                // -------------------------------------------------------------
                // 13. RADIOLOGY MODULE (Charges & Diagnostic Test)
                // -------------------------------------------------------------
                $scope.radDepartments = RadiologyService.departments;
                $scope.radInvestigations = RadiologyService.investigations;
                $scope.radOrgTypes = RadiologyService.organizationTypes;
                $scope.radReagentsList = RadiologyService.reagentsList;
                $scope.radChargesList = RadiologyService.getCharges();
                $scope.radTestsList = RadiologyService.getDiagnosticTests();

                function getInitialRadChargesForm() {
                    return {
                        department: 'RADIOLOGY',
                        investigation: 'X-RAY ABDOMEN ERECT POSITION',
                        orgType: 'Hospital',
                        copyToChecked: false,
                        copyToOrgType: 'Hospital',
                        hospitalShare: 350,
                        doctorShare: 150,
                        incrDecrPercent: 0
                    };
                }

                function getInitialRadTestForm() {
                    return {
                        testCode: 'xrayches',
                        testName: 'x-ray chest pa view',
                        description: 'x-ray chest pa view',
                        instruction: 'Patient in erect position with deep inspiration',
                        department: 'RADIOLOGY',
                        active: true,
                        reagents: [
                            { reagentName: 'X-Ray Film 12x15', quantity: 1 }
                        ]
                    };
                }

                $scope.radChargesForm = getInitialRadChargesForm();
                $scope.radTestForm = getInitialRadTestForm();
                $scope.showRadTestSearchModal = false;

                $scope.onRadInvestigationChange = function () {
                    var match = $scope.radChargesList.find(function (c) {
                        return c.investigation === $scope.radChargesForm.investigation &&
                               c.department === $scope.radChargesForm.department;
                    });
                    if (match) {
                        $scope.radChargesForm.hospitalShare = match.hospitalShare;
                        $scope.radChargesForm.doctorShare = match.doctorShare;
                        $scope.radChargesForm.orgType = match.orgType || 'Hospital';
                    }
                };

                $scope.applyToAllCharges = function () {
                    var hShare = Number($scope.radChargesForm.hospitalShare) || 0;
                    var dShare = Number($scope.radChargesForm.doctorShare) || 0;
                    $scope.radChargesList.forEach(function (c) {
                        if (c.department === $scope.radChargesForm.department) {
                            c.hospitalShare = hShare;
                            c.doctorShare = dShare;
                            c.totalCharge = hShare + dShare;
                        }
                    });
                    $scope.showToast('Applied Hospital: ₹' + hShare + ', Doctor: ₹' + dShare + ' to all ' + $scope.radChargesForm.department + ' investigations.');
                };

                $scope.applyIncrDecr = function () {
                    var pct = Number($scope.radChargesForm.incrDecrPercent) || 0;
                    if (pct === 0) {
                        $scope.showToast('Please specify a non-zero percentage.');
                        return;
                    }
                    var factor = 1 + (pct / 100);
                    $scope.radChargesForm.hospitalShare = Math.round(Number($scope.radChargesForm.hospitalShare || 0) * factor);
                    $scope.radChargesForm.doctorShare = Math.round(Number($scope.radChargesForm.doctorShare || 0) * factor);
                    $scope.showToast('Applied ' + pct + '% adjustment to current shares.');
                };

                $scope.saveRadCharges = function () {
                    if (!$scope.radChargesForm.investigation || $scope.radChargesForm.investigation === 'Select') {
                        $scope.showToast('Please select an Investigation.');
                        return;
                    }
                    var saved = RadiologyService.saveCharge($scope.radChargesForm);
                    $scope.radChargesList = RadiologyService.getCharges();
                    $scope.showToast('Radiology Charge for [' + saved.investigation + '] saved! Total: ₹' + saved.totalCharge);
                };

                $scope.loadRadCharge = function (c) {
                    $scope.radChargesForm.department = c.department;
                    $scope.radChargesForm.investigation = c.investigation;
                    $scope.radChargesForm.orgType = c.orgType || 'Hospital';
                    $scope.radChargesForm.hospitalShare = c.hospitalShare;
                    $scope.radChargesForm.doctorShare = c.doctorShare;
                    $scope.showToast('Loaded charges for ' + c.investigation);
                };

                $scope.addRadReagent = function () {
                    $scope.radTestForm.reagents.push({
                        reagentName: 'Select',
                        quantity: 1
                    });
                };

                $scope.removeRadReagent = function (index) {
                    if ($scope.radTestForm.reagents.length > 1) {
                        $scope.radTestForm.reagents.splice(index, 1);
                    } else {
                        $scope.radTestForm.reagents[0] = { reagentName: 'Select', quantity: 1 };
                    }
                };

                $scope.saveRadTest = function () {
                    if (!$scope.radTestForm.testCode || !$scope.radTestForm.testCode.trim()) {
                        $scope.showToast('Validation Error: Please enter Test Code.');
                        return;
                    }
                    if (!$scope.radTestForm.testName || !$scope.radTestForm.testName.trim()) {
                        $scope.showToast('Validation Error: Please enter Test Name.');
                        return;
                    }
                    var saved = RadiologyService.saveDiagnosticTest($scope.radTestForm);
                    $scope.radTestsList = RadiologyService.getDiagnosticTests();
                    $scope.showToast('Diagnostic Test [' + saved.testCode + ' - ' + saved.testName + '] saved successfully.');
                };

                $scope.lookupRadTest = function () {
                    var q = ($scope.radTestForm.testCode || '').trim().toLowerCase();
                    var match = $scope.radTestsList.find(function (t) {
                        return (t.testCode && t.testCode.toLowerCase().includes(q)) ||
                               (t.testName && t.testName.toLowerCase().includes(q));
                    });
                    if (match) {
                        $scope.loadRadTest(match);
                        $scope.showToast('Diagnostic test loaded: ' + match.testCode);
                    } else {
                        $scope.showRadTestSearchModal = true;
                    }
                };

                $scope.loadRadTest = function (t) {
                    $scope.radTestForm = angular.copy(t);
                    if (!$scope.radTestForm.reagents || $scope.radTestForm.reagents.length === 0) {
                        $scope.radTestForm.reagents = [{ reagentName: 'Select', quantity: 1 }];
                    }
                    $scope.showRadTestSearchModal = false;
                    $scope.showToast('Loaded Diagnostic Test: ' + t.testCode);
                };

                // -------------------------------------------------------------
                // 14. TOAST & GLOBAL ACTIONS
                // -------------------------------------------------------------
                $scope.toastMsg = '';
                var toastTimeout = null;
                $scope.showToast = function (msg) {
                    $scope.toastMsg = msg;
                    if (toastTimeout) {
                        $timeout.cancel(toastTimeout);
                    }
                    toastTimeout = $timeout(function () {
                        $scope.toastMsg = '';
                    }, 3000);
                };

                $scope.searchQuery = '';
                $scope.performSearch = function () {
                    if ($scope.searchQuery && $scope.searchQuery.trim().length > 0) {
                        $scope.showToast('Searching patient: "' + $scope.searchQuery.trim() + '"');
                    } else {
                        $scope.showToast('Please enter Patient Name or UHID to search');
                    }
                };

                $scope.actionClick = function (actionName) {
                    if (actionName === 'Save') {
                        $scope.saveRecord();
                    } else if (actionName === 'Cancel') {
                        $scope.cancelRecord();
                    } else if (actionName === 'Print') {
                        $scope.printRecord();
                    } else if (actionName === 'Edit') {
                        $scope.showToast('Editing mode activated for current record.');
                    } else if (actionName === 'Search') {
                        if ($scope.activeModule === 'In Patient') {
                            if ($scope.activeAction === 'IP Admission') {
                                $scope.showIpAdmissionDropdown = true;
                            } else if ($scope.activeAction === 'Advance Payment') {
                                $scope.lookupIpAdvance();
                            } else if ($scope.activeAction === 'Transfer Doctor') {
                                $scope.lookupIpTransferDoc();
                            } else if ($scope.activeAction === 'Transfer Bed') {
                                $scope.lookupIpTransferBed();
                            } else if ($scope.activeAction === 'Discharge Summary') {
                                $scope.showIpDischargeDropdown = true;
                            } else if ($scope.activeAction === 'Settlement Bill') {
                                $scope.lookupIpSettlement();
                            } else if ($scope.activeAction === 'IP wise Test Results') {
                                $scope.lookupIpWiseTests();
                            } else if ($scope.activeAction === 'Due Collection') {
                                $scope.lookupIpDue();
                            } else if ($scope.activeAction === 'Discharge Cancellation') {
                                $scope.lookupIpDischargeCancel();
                            } else {
                                $scope.performSearch();
                            }
                        } else if ($scope.activeModule === 'Soft Lab') {
                            if ($scope.activeAction === 'Registration') {
                                $scope.lookupLabPatient();
                            } else if ($scope.activeAction === 'Lab Due Collection') {
                                $scope.lookupLabDue();
                            } else if ($scope.activeAction === 'Test Cancellation') {
                                $scope.lookupLabCancellation();
                            } else {
                                $scope.performSearch();
                            }
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Report Settings') {
                            $scope.onReportModuleChange();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service Charge') {
                            $scope.onServiceChargeServiceChange();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Screen Settings') {
                            $scope.onUserScreenModuleChange();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedure Charges') {
                            $scope.onProcChargeProcedureChange();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Referral Doctor') {
                            $scope.lookupAppRefDoctor();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service') {
                            $scope.lookupAppService();
                        } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedures') {
                            $scope.lookupAppProcedure();
                        } else if ($scope.activeModule === 'Accounting') {
                            $scope.lookupAccountTransaction();
                        } else if ($scope.activeModule === 'Reports') {
                            $scope.getReport();
                        } else if ($scope.activeModule === 'Radiology') {
                            if ($scope.activeAction === 'Charges') {
                                $scope.onRadInvestigationChange();
                            } else if ($scope.activeAction === 'Diagnostic Test') {
                                $scope.lookupRadTest();
                            } else {
                                $scope.performSearch();
                            }
                        } else {
                            $scope.performSearch();
                        }
                    } else if (actionName === 'Delete') {
                        if (confirm('Are you sure you want to delete the current record?')) {
                            $scope.cancelRecord();
                            $scope.showToast('Record deleted.');
                        }
                    } else {
                        $scope.showToast(actionName + ' executed for [' + $scope.activeAction + ']');
                    }
                };

                $scope.saveRecord = function () {
                    if ($scope.activeAction === 'Procedure') {
                        if (!$scope.procedureForm.firstName || !$scope.procedureForm.firstName.trim()) {
                            $scope.showToast('Validation Error: Please enter or lookup patient name.');
                            return;
                        }
                        var procId = 'PR-' + Math.floor(1000 + Math.random() * 9000);
                        $scope.showToast('Procedure Order saved for ' + $scope.procedureForm.firstName + '! (Order #' + procId + ')');
                    } else if ($scope.activeAction === 'Due Collection') {
                        if (!$scope.dueForm.patientName) {
                            $scope.showToast('Please search and select a patient first.');
                            return;
                        }
                        var paid = Number($scope.dueForm.paidAmount) || 0;
                        if (paid <= 0) {
                            $scope.showToast('Please enter a valid Paid Amount.');
                            return;
                        }
                        var receiptNo = 'REC-' + Math.floor(1000 + Math.random() * 9000);
                        $scope.showToast('Due Collection ₹' + paid + ' saved! Receipt: ' + receiptNo);
                    } else if ($scope.activeAction === 'Registration') {
                        if (!$scope.regForm.firstName || !$scope.regForm.firstName.trim()) {
                            $scope.showToast('Validation Error: Please enter First Name.');
                            return;
                        }
                        var generatedUhid = 'INDUS-' + Math.floor(100000 + Math.random() * 900000);
                        var newPatient = {
                            id: generatedUhid,
                            name: $scope.regForm.title + ' ' + $scope.regForm.firstName + ' ' + ($scope.regForm.lastName || ''),
                            dept: 'Out-Patient',
                            status: 'Waiting',
                            time: $scope.loginTime
                        };
                        PatientService.addPatient(newPatient);
                        $scope.showToast('Patient Registered Successfully! UHID: ' + generatedUhid);
                    } else if ($scope.activeAction === 'Consultation') {
                        if ($scope.consultForm.consultantDoctor === 'Select') {
                            $scope.showToast('Please select a Consultant Doctor.');
                            return;
                        }
                        var tokenNo = Math.floor(10 + Math.random() * 90);
                        $scope.showToast('Consultation Saved! Token #' + tokenNo + ' assigned to ' + $scope.consultForm.consultantDoctor);
                    } else if ($scope.activeAction === 'Consultation Cancellation') {
                        $scope.showToast('Consultation Cancelled & Refund of ₹' + $scope.cancelForm.netRefundableAmt + ' processed successfully.');
                    } else if ($scope.activeAction === 'Procedure Cancellation') {
                        var bNo = $scope.procCancelForm.billNo || 'PB-8801';
                        var ref = $scope.procCancelForm.refundedAmt || $scope.procCancelForm.totalReceived || 0;
                        ProcedureService.cancelBill(bNo);
                        $scope.showToast('Procedure Bill ' + bNo + ' Cancelled! Refund of ₹' + ref + ' processed successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedures') {
                        if (!$scope.appProcForm.procedureCode && !$scope.appProcForm.procedureName) {
                            $scope.showToast('Please enter Procedure Code or Procedure Name.');
                            return;
                        }
                        var pCode = $scope.appProcForm.procedureCode || ('PROC-' + Math.floor(100 + Math.random() * 900));
                        var pName = $scope.appProcForm.procedureName || pCode;
                        AppMgmtService.addProcedure({
                            code: pCode,
                            name: pName,
                            procDescription: $scope.appProcForm.procDescription,
                            ccUnitId: $scope.appProcForm.ccUnitId,
                            comments: $scope.appProcForm.comments,
                            active: $scope.appProcForm.active,
                            isDoctorProcedure: $scope.appProcForm.isDoctorProcedure
                        });
                        $scope.showToast('Procedure [' + pCode + ' - ' + pName + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service') {
                        if (!$scope.appServiceForm.serviceCode && !$scope.appServiceForm.serviceName) {
                            $scope.showToast('Please enter Service Code or Service Name.');
                            return;
                        }
                        var sCode = $scope.appServiceForm.serviceCode || ('SRV-' + Math.floor(100 + Math.random() * 900));
                        var sName = $scope.appServiceForm.serviceName || sCode;
                        AppMgmtService.addService({
                            code: sCode,
                            name: sName,
                            description: $scope.appServiceForm.description,
                            serviceType: $scope.appServiceForm.serviceType,
                            comments: $scope.appServiceForm.comments,
                            chargeUnit: $scope.appServiceForm.chargeUnit,
                            active: $scope.appServiceForm.active
                        });
                        $scope.showToast('Service [' + sCode + ' - ' + sName + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Referral Doctor') {
                        if (!$scope.appRefDocForm.firstName && !$scope.appRefDocForm.lastName && !$scope.appRefDocForm.institute) {
                            $scope.showToast('Please enter Doctor First Name, Last Name, or Institute.');
                            return;
                        }
                        var savedDoc = AppMgmtService.addReferralDoctor({
                            id: $scope.appRefDocForm.id || ('RD-' + Math.floor(100 + Math.random() * 900)),
                            firstName: $scope.appRefDocForm.firstName,
                            middleName: $scope.appRefDocForm.middleName,
                            lastName: $scope.appRefDocForm.lastName,
                            institute: $scope.appRefDocForm.institute,
                            address: $scope.appRefDocForm.address,
                            country: $scope.appRefDocForm.country,
                            landline: $scope.appRefDocForm.landline,
                            state: $scope.appRefDocForm.state,
                            mobile: $scope.appRefDocForm.mobile,
                            city: $scope.appRefDocForm.city,
                            email: $scope.appRefDocForm.email,
                            area: $scope.appRefDocForm.area,
                            active: $scope.appRefDocForm.active
                        });
                        $scope.showToast('Referral Doctor [Dr. ' + (savedDoc.firstName || '') + ' ' + (savedDoc.lastName || '') + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedure Charges') {
                        if (!$scope.procChargeForm.procedure || $scope.procChargeForm.procedure === 'Select') {
                            $scope.showToast('Please select a Procedure first.');
                            return;
                        }
                        AppMgmtService.saveProcedureChargeRate($scope.procChargeForm.procedure, {
                            hospitalShare: Number($scope.procChargeForm.hospitalShare) || 0,
                            doctorShare: Number($scope.procChargeForm.doctorShare) || 0,
                            hospitalPercentage: Number($scope.procChargeForm.hospitalPercentage) || 0,
                            totalAmount: Number($scope.procChargeForm.totalAmount) || 0
                        });
                        $scope.showToast('Procedure Charges for [' + $scope.procChargeForm.procedure + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Screen Settings') {
                        if (!$scope.userScreenForm.selectedModule || $scope.userScreenForm.selectedModule === 'Select') {
                            $scope.showToast('Please select a Module first.');
                            return;
                        }
                        $scope.showToast('User Screen Settings for [' + $scope.userScreenForm.selectedModule + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service Charge') {
                        if (!$scope.serviceChargeForm.service || $scope.serviceChargeForm.service === 'Select') {
                            $scope.showToast('Please select a Service first.');
                            return;
                        }
                        AppMgmtService.saveServiceChargeRate($scope.serviceChargeForm.service, {
                            hospitalShare: Number($scope.serviceChargeForm.hospitalShare) || 0,
                            doctorShare: Number($scope.serviceChargeForm.doctorShare) || 0,
                            hospitalPercentage: Number($scope.serviceChargeForm.hospitalPercentage) || 0,
                            totalAmount: Number($scope.serviceChargeForm.totalAmount) || 0
                        });
                        $scope.showToast('Service Charge for [' + $scope.serviceChargeForm.service + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Report Settings') {
                        if (!$scope.userReportForm.module || $scope.userReportForm.module === 'Select') {
                            $scope.showToast('Please select a Module to save User Report Settings.');
                            return;
                        }
                        AppMgmtService.saveUserReportsOrder($scope.userReportForm.module, $scope.userReportForm.reports);
                        $scope.showToast('User Reports Order for [' + $scope.userReportForm.module + '] saved successfully.');
                    } else if ($scope.activeModule === 'Soft Lab') {
                        if ($scope.activeAction === 'Registration') {
                            if (!$scope.labRegForm.firstName || !$scope.labRegForm.firstName.trim()) {
                                $scope.showToast('Validation Error: Please enter or lookup patient name.');
                                return;
                            }
                            var labNo = 'L-' + Math.floor(2580 + Math.random() * 90);
                            $scope.showToast('Lab Registration saved successfully! Lab No: ' + labNo + ' for ' + $scope.labRegForm.firstName);
                        } else if ($scope.activeAction === 'Lab Due Collection') {
                            var recNo = 'LREC-' + Math.floor(1000 + Math.random() * 9000);
                            $scope.showToast('Lab Due Collection of ₹' + $scope.labDueForm.paidAmount + ' saved! Receipt: ' + recNo);
                        } else if ($scope.activeAction === 'Charges') {
                            $scope.showToast('Lab ChargeCode Details saved successfully.');
                        } else if ($scope.activeAction === 'Report Status') {
                            $scope.showToast('Report Status updates saved successfully.');
                        } else if ($scope.activeAction === 'Test Cancellation') {
                            $scope.showToast('Lab Test Cancellation processed successfully. Refund: ₹' + $scope.labCancelForm.totalRefundAmt);
                        } else if ($scope.activeAction === 'Test Details') {
                            $scope.showToast('Test Details configuration saved successfully.');
                        } else {
                            $scope.showToast('Lab record saved successfully.');
                        }
                    } else if ($scope.activeModule === 'Accounting') {
                        if (!$scope.accountTxnForm.amount || Number($scope.accountTxnForm.amount) <= 0) {
                            $scope.showToast('Validation Error: Please enter a valid Transaction Amount.');
                            return;
                        }
                        if (!$scope.accountTxnForm.partyName || !$scope.accountTxnForm.partyName.trim()) {
                            $scope.showToast('Validation Error: Please enter Name Of The Party.');
                            return;
                        }
                        var savedTxn = AccountService.addTransaction($scope.accountTxnForm);
                    } else if ($scope.activeModule === 'Accounting') {
                        if (!$scope.accountTxnForm.amount || Number($scope.accountTxnForm.amount) <= 0) {
                            $scope.showToast('Validation Error: Please enter a valid Transaction Amount.');
                            return;
                        }
                        if (!$scope.accountTxnForm.partyName || !$scope.accountTxnForm.partyName.trim()) {
                            $scope.showToast('Validation Error: Please enter Name Of The Party.');
                            return;
                        }
                        var savedTxn = AccountService.addTransaction($scope.accountTxnForm);
                        $scope.accountTransactions = AccountService.getTransactions();
                        $scope.showToast('Transaction Saved Successfully! Voucher No: ' + savedTxn.voucherNo + ' (₹' + savedTxn.amount + ')');
                    } else if ($scope.activeModule === 'Reports') {
                        $scope.getReport();
                    } else if ($scope.activeModule === 'Radiology') {
                        if ($scope.activeAction === 'Charges') {
                            $scope.saveRadCharges();
                        } else if ($scope.activeAction === 'Diagnostic Test') {
                            $scope.saveRadTest();
                        } else {
                            $scope.showToast('Radiology record saved successfully.');
                        }
                    } else if ($scope.activeModule === 'In Patient') {
                        $scope.showToast('In Patient: [' + $scope.activeAction + '] saved successfully.');
                    } else if ($scope.activeModule === 'App Mgmt') {
                        $scope.showToast('App Mgmt: ' + ($scope.activeAction || 'Configuration') + ' saved successfully.');
                    } else {
                        $scope.showToast('Record saved successfully.');
                    }
                };

                $scope.cancelRecord = function () {
                    if ($scope.activeModule === 'Reports') {
                        $scope.cancelReport();
                    } else if ($scope.activeModule === 'Radiology') {
                        if ($scope.activeAction === 'Charges') {
                            $scope.radChargesForm = getInitialRadChargesForm();
                            $scope.showToast('Radiology Charges form reset.');
                        } else if ($scope.activeAction === 'Diagnostic Test') {
                            $scope.radTestForm = getInitialRadTestForm();
                            $scope.showToast('Diagnostic Test form reset.');
                        } else {
                            $scope.showToast('Radiology form reset.');
                        }
                    } else if ($scope.activeModule === 'Accounting') {
                        $scope.accountTxnForm = getInitialAccountTxnForm();
                        $scope.accountSubHeads = AccountService.getSubHeads('Hospital Revenue');
                        $scope.accountSubSubHeads = AccountService.getSubSubHeads('Hospital Revenue', 'Inpatient Services');
                        $scope.showToast('Transactions form reset.');
                    } else if ($scope.activeModule === 'In Patient') {
                        if ($scope.activeAction === 'IP Admission') {
                            $scope.ipAdmissionForm = getInitialIpAdmissionForm();
                            $scope.showToast('IP Admission form reset.');
                        } else if ($scope.activeAction === 'Advance Payment') {
                            $scope.ipAdvanceForm = getInitialIpAdvanceForm();
                            $scope.showToast('Advance Payment form reset.');
                        } else if ($scope.activeAction === 'Transfer Doctor') {
                            $scope.ipTransferDocForm = getInitialIpTransferDocForm();
                            $scope.showToast('Transfer Doctor form reset.');
                        } else if ($scope.activeAction === 'Transfer Bed') {
                            $scope.ipTransferBedForm = getInitialIpTransferBedForm();
                            $scope.showToast('Transfer Bed form reset.');
                        } else if ($scope.activeAction === 'Discharge Summary') {
                            $scope.ipDischargeForm = getInitialIpDischargeForm();
                            $scope.showToast('Discharge Summary form reset.');
                        } else if ($scope.activeAction === 'Settlement Bill') {
                            $scope.ipSettlementForm = getInitialIpSettlementForm();
                            $scope.showToast('Settlement Bill form reset.');
                        } else if ($scope.activeAction === 'Medical Certificate') {
                            $scope.ipMedicalCertForm = getInitialIpMedicalCertForm();
                            $scope.showToast('Medical Certificate form reset.');
                        } else if ($scope.activeAction === 'Due Collection') {
                            $scope.ipDueForm = getInitialIpDueForm();
                            $scope.showToast('Due Collection form reset.');
                        } else if ($scope.activeAction === 'Discharge Cancellation') {
                            $scope.ipDischargeCancelForm = getInitialIpDischargeCancelForm();
                            $scope.showToast('Discharge Cancellation form reset.');
                        } else {
                            $scope.showToast('In Patient view refreshed.');
                        }
                    } else if ($scope.activeModule === 'Soft Lab') {
                        if ($scope.activeAction === 'Registration') {
                            $scope.labRegForm = getInitialLabRegForm();
                            $scope.showToast('Lab Registration form reset.');
                        } else if ($scope.activeAction === 'Lab Due Collection') {
                            $scope.labDueForm = getInitialLabDueForm();
                            $scope.showToast('Lab Due Collection form reset.');
                        } else if ($scope.activeAction === 'Charges') {
                            $scope.labChargesForm = getInitialLabChargesForm();
                            $scope.showToast('Lab Charges form reset.');
                        } else if ($scope.activeAction === 'Test Cancellation') {
                            $scope.labCancelForm = getInitialLabCancelForm();
                            $scope.showToast('Test Cancellation form reset.');
                        } else if ($scope.activeAction === 'Test Details') {
                            $scope.labTestDetailsForm = getInitialLabTestDetailsForm();
                            $scope.showToast('Test Details form reset.');
                        } else {
                            $scope.showToast('Lab form reset.');
                        }
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Report Settings') {
                        $scope.userReportForm = getInitialUserReportForm();
                        $scope.showToast('User Report Settings form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service Charge') {
                        $scope.serviceChargeForm = getInitialServiceChargeForm();
                        $scope.showToast('Service Charge form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'User Screen Settings') {
                        $scope.userScreenForm = getInitialUserScreenForm();
                        $scope.showToast('User Screen Settings form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedure Charges') {
                        $scope.procChargeForm = getInitialProcChargeForm();
                        $scope.showToast('Procedure Charges form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Referral Doctor') {
                        $scope.appRefDocForm = getInitialAppRefDocForm();
                        $scope.showToast('Referral Doctor form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Service') {
                        $scope.appServiceForm = getInitialAppServiceForm();
                        $scope.showToast('Service form reset.');
                    } else if ($scope.activeModule === 'App Mgmt' && $scope.activeAction === 'Procedures') {
                        $scope.appProcForm = getInitialAppProcForm();
                        $scope.showToast('Procedures form reset.');
                    } else if ($scope.activeModule === 'App Mgmt') {
                        $scope.activeAction = '';
                        $scope.showToast('Returned to App Mgmt overview.');
                    } else if ($scope.activeAction === 'Procedure') {
                        $scope.procedureForm = getInitialProcedureForm($scope.procedureForm.bookingMode);
                        $scope.showToast('Procedure form reset.');
                    } else if ($scope.activeAction === 'Procedure Cancellation') {
                        $scope.procCancelForm = getInitialProcCancelForm();
                        $scope.showToast('Procedure Cancellation form reset.');
                    } else if ($scope.activeAction === 'Due Collection') {
                        $scope.dueForm = getInitialDueForm();
                        $scope.showToast('Due Collection form reset.');
                    } else if ($scope.activeAction === 'Registration') {
                        $scope.regForm = getInitialRegForm();
                        $scope.removePhoto();
                        $scope.showToast('Registration form reset to defaults.');
                    } else if ($scope.activeAction === 'Consultation') {
                        $scope.consultForm = getInitialConsultForm();
                        $scope.showToast('Consultation form reset.');
                    } else if ($scope.activeAction === 'Consultation Cancellation') {
                        $scope.cancelForm = getInitialCancelForm();
                        $scope.showToast('Cancellation form reset.');
                    }
                };

                $scope.printRecord = function () {
                    $window.print();
                };

                $scope.goHome = function () {
                    $scope.activeModule = 'Soft Lab';
                    $scope.subActions = $scope.moduleSubActions['Soft Lab'];
                    $scope.activeAction = 'Registration';
                    $scope.showToast('Navigated to Home (Lab Registration)');
                };

                $scope.changePassword = function () {
                    $scope.showToast('Change Password dialog for User: ' + $scope.user.username);
                };

                $scope.logout = function () {
                    AuthService.logout().then(function () {
                        $window.location.href = 'login.html';
                    });
                };
            }
        ]);
})();
