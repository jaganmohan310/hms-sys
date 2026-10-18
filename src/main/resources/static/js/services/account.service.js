(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('AccountService', function () {
            var service = {};

            // 1. Billing Departments
            service.billingDepartments = [
                'Accounting',
                'Central Pharma',
                'In Patient',
                'Lab',
                'Out Patient',
                'Pharmacy'
            ];

            // 2. Party Types
            service.partyTypes = [
                'Select',
                'Patient',
                'Doctor',
                'Vendor',
                'Hospital Staff',
                'Corporate / TPA',
                'Other'
            ];

            // 3. Hierarchical Account Heads Master
            service.accountHierarchy = {
                'Hospital Revenue': {
                    subHeads: {
                        'Inpatient Services': ['Bed Charges', 'Nursing Care', 'ICU Charges', 'OT Charges'],
                        'Outpatient Services': ['OP Consultation Fees', 'Emergency Registration', 'Day Care'],
                        'Diagnostic Services': ['Biochemistry Lab', 'Pathology Lab', 'Microbiology', 'Radiology X-Ray'],
                        'Pharmacy Sales': ['OP Pharmacy Dispensing', 'IP Pharmacy Meds', 'Consumables']
                    }
                },
                'Operational Expenses': {
                    subHeads: {
                        'Medical Supplies': ['Surgical Consumables', 'Drugs & Pharmaceuticals', 'Diagnostic Reagents'],
                        'Hospital Utility': ['Electricity Charges', 'Water & Sanitation', 'Oxygen & Medical Gases'],
                        'Facility Maintenance': ['Biomedical Equipment AMC', 'Housekeeping Supplies', 'Building Maintenance'],
                        'Administrative Costs': ['Printing & Stationery', 'Internet & Telephony', 'Software AMC']
                    }
                },
                'Salaries & Professional Fees': {
                    subHeads: {
                        'Doctors Fees': ['Consultant Doctor Honorarium', 'Visiting Specialist Retainer', 'Emergency Doctor Duty Fees'],
                        'Staff Payroll': ['Nursing Staff Salary', 'Paramedical Staff', 'Admin & Support Staff']
                    }
                }
            };

            // 4. Sample Transactions Log
            service.transactions = [
                {
                    voucherNo: 'TXN-2021-001',
                    date: '07/02/2021',
                    transactionType: 'Income(Cr)',
                    partyType: 'Patient',
                    billingDepartment: 'In Patient',
                    originalBillNo: 'IPB-2021-982',
                    accountHead: 'Hospital Revenue',
                    accountSubHead: 'Inpatient Services',
                    accountSubSubHead: 'Bed Charges',
                    partyName: 'NAGAIAH G',
                    contactNo: '9515181992',
                    address: 'H.No 4-12, Khaleelwadi, Nizamabad',
                    paymentType: 'Cash',
                    amount: 15000,
                    remarks: 'IP Advance collection towards room & nursing'
                },
                {
                    voucherNo: 'TXN-2021-002',
                    date: '07/02/2021',
                    transactionType: 'Expense(Dr)',
                    partyType: 'Vendor',
                    billingDepartment: 'Central Pharma',
                    originalBillNo: 'INV-PH-8841',
                    accountHead: 'Operational Expenses',
                    accountSubHead: 'Medical Supplies',
                    accountSubSubHead: 'Drugs & Pharmaceuticals',
                    partyName: 'Sri Balaji Medical Agencies',
                    contactNo: '9848011223',
                    address: 'Station Road, Nizamabad',
                    paymentType: 'Cheque',
                    amount: 45000,
                    remarks: 'Monthly IV fluids & emergency antibiotic supplies'
                }
            ];

            // Helper methods
            service.getAccountHeads = function () {
                return Object.keys(service.accountHierarchy);
            };

            service.getSubHeads = function (head) {
                if (!head || !service.accountHierarchy[head]) return [];
                return Object.keys(service.accountHierarchy[head].subHeads);
            };

            service.getSubSubHeads = function (head, subHead) {
                if (!head || !subHead || !service.accountHierarchy[head] || !service.accountHierarchy[head].subHeads[subHead]) return [];
                return service.accountHierarchy[head].subHeads[subHead];
            };

            service.getTransactions = function () {
                return service.transactions;
            };

            service.addTransaction = function (txn) {
                var copy = angular.copy(txn);
                copy.voucherNo = 'TXN-2021-' + Math.floor(100 + Math.random() * 900);
                copy.date = '07/02/2021';
                service.transactions.unshift(copy);
                return copy;
            };

            return service;
        });
})();
