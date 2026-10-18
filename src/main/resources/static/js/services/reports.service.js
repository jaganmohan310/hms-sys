(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('ReportsService', function () {
            var service = {};

            // 1. Available Users for User Collection Report (Exact from Screenshot Page 4)
            service.usersList = [
                'Select',
                'All',
                'GANESH',
                'JAGAN',
                'KANNAIAH',
                'LAXMAN',
                'MADAVI',
                'NANY',
                'NAVEENR',
                'PH.SHARVAN',
                'PHARAMCY',
                'PRASAD',
                'SHRAVAN',
                'SOUNDHARYA',
                'SRAVAN',
                'SRINIVASM'
            ];

            // 2. Modules List
            service.modulesList = [
                'All',
                'Application Mgmt',
                'In Patient',
                'Lab',
                'Out Patient',
                'Pharmacy'
            ];

            // 3. Reports Catalogs by Sub-Action Category (Exact from PDF Screenshots)
            service.catalog = {
                'Application Mgmt': [
                    { id: 'app_my_collection', name: 'My Collection', type: 'date_range' },
                    { id: 'app_mrno_bill', name: 'MRNO wise Bill Details', type: 'mrno_range' },
                    { id: 'app_patient_count', name: 'Patient wise Count', type: 'year_range' },
                    { id: 'app_procedures', name: 'Procedures', type: 'none', label: 'Shows All Procedures' },
                    { id: 'app_user_collection', name: 'User Collection', type: 'user_collection' }
                ],
                'In Patient': [
                    { id: 'ip_daywise', name: 'Daywise IP List', type: 'date_range' },
                    { id: 'ip_discharge_summary', name: 'Discharge Summary', type: 'date_range' },
                    { id: 'ip_doc_room_rent', name: 'Doctor Wise Room Rent', type: 'date_range' },
                    { id: 'ip_doc_visit', name: 'Doctor Wise Visit Report', type: 'date_range' },
                    { id: 'ip_due_collection', name: 'Due Collection', type: 'date_range' },
                    { id: 'ip_from_bill_to_bill', name: 'FromBill To ToBill', type: 'bill_range' },
                    { id: 'ip_admission_cancel', name: 'IP Admission Cancellations', type: 'date_range' },
                    { id: 'ip_advances', name: 'IP Advances', type: 'date_range' },
                    { id: 'ip_advances_refund', name: 'IP Advances Refund Details', type: 'date_range' },
                    { id: 'ip_avail_beds', name: 'IP Available Beds', type: 'none', label: 'Shows All Available Beds' },
                    { id: 'ip_bed_details', name: 'IP Bed Details', type: 'none', label: 'Shows All IP Bed Details' },
                    { id: 'ip_bill_adj', name: 'IP Bill Adjustments', type: 'date_range' },
                    { id: 'ip_bill_details', name: 'IP Bill Details', type: 'mrno_range' },
                    { id: 'ip_day_collection', name: 'IP Day Collection', type: 'date_range' },
                    { id: 'ip_doc_commission', name: 'IP Doctor Commision', type: 'date_range' },
                    { id: 'ip_dues_report', name: 'IP Dues Report', type: 'date_range' },
                    { id: 'ip_gross_day_coll', name: 'IP Gross Day wise Collection', type: 'date_range' },
                    { id: 'ip_patient_list', name: 'IP Patient List', type: 'date_range' },
                    { id: 'ip_proc_commission', name: 'IP Procedure Commision', type: 'date_range' },
                    { id: 'ip_settle_discount', name: 'IP Settlement Discount Given list', type: 'date_range' },
                    { id: 'ip_settlements', name: 'IP Settlements', type: 'date_range' },
                    { id: 'ip_user_collection', name: 'IP User Collection', type: 'user_collection' },
                    { id: 'ip_admissions', name: 'IP_Admissions', type: 'date_range' },
                    { id: 'ip_op_converted', name: 'Op to IP Converted Patients', type: 'date_range' },
                    { id: 'ip_unsettled', name: 'Unsettled IP Patients', type: 'none', label: 'Shows All Currently Unsettled Patients' }
                ],
                'Lab': [
                    { id: 'lab_breakage', name: 'Breakage Or Loss', type: 'date_range' },
                    { id: 'lab_dept_tests', name: 'Department Tests Report', type: 'date_range' },
                    { id: 'lab_doc_test_coll', name: 'Doctor and test wise collection', type: 'date_range' },
                    { id: 'lab_due_coll', name: 'Due Collection', type: 'date_range' },
                    { id: 'lab_from_bill', name: 'FromBill To ToBill', type: 'bill_range' },
                    { id: 'lab_ipno_tests_cancel', name: 'IPNo wise Tests and Cancellations', type: 'date_range' },
                    { id: 'lab_day_coll', name: 'Lab Day Collection', type: 'date_range' },
                    { id: 'lab_detailed_reg', name: 'Lab Detailed Registrations', type: 'date_range' },
                    { id: 'lab_doc_coll', name: 'Lab Doctor Wise Collection', type: 'date_range' },
                    { id: 'lab_due_report', name: 'Lab Due Report', type: 'date_range' },
                    { id: 'lab_reg_cancel', name: 'Lab Registration Cancellations', type: 'date_range' },
                    { id: 'lab_registrations', name: 'Lab Registrations', type: 'date_range' }
                ],
                'Out Patient': [
                    { id: 'op_area_consult', name: 'Area wise Consultations', type: 'date_range' },
                    { id: 'op_cancellation', name: 'Cancellation Report', type: 'date_range' },
                    { id: 'op_doc_validity', name: 'Consultant Doctor Review Validity', type: 'date_range' },
                    { id: 'op_consult_cancel', name: 'Consultation Cancellations', type: 'date_range' },
                    { id: 'op_consult_cancel_sum', name: 'Consultation Cancellations Summary', type: 'date_range' },
                    { id: 'op_consult_with_cancel', name: 'Consultations With Cancellations', type: 'date_range' },
                    { id: 'op_consult_cancel_sum2', name: 'Consultations With Cancellations summary', type: 'date_range' },
                    { id: 'op_due_collection', name: 'Due Collection', type: 'date_range' },
                    { id: 'op_free_reviews', name: 'Free Reviews', type: 'date_range' },
                    { id: 'op_from_bill', name: 'FromBill To ToBill', type: 'bill_range' },
                    { id: 'op_day_collection', name: 'OP Day Collection', type: 'date_range' },
                    { id: 'op_detailed_report', name: 'OP Detailed Report', type: 'date_range' },
                    { id: 'op_discount_given', name: 'OP Discount given patient details', type: 'date_range' },
                    { id: 'op_due_report', name: 'OP Due Report', type: 'date_range' },
                    { id: 'op_gross_day_coll', name: 'OP Gross Day Wise Collection', type: 'date_range' },
                    { id: 'op_ref_doc_comm', name: 'OP Referral Doctor Commision', type: 'date_range' },
                    { id: 'op_tds', name: 'OP TDS', type: 'date_range' },
                    { id: 'op_total_coll', name: 'OP total Collection', type: 'date_range' },
                    { id: 'op_user_coll', name: 'OP User Collection', type: 'user_collection' },
                    { id: 'op_consultations', name: 'OP_Consultations', type: 'date_range' },
                    { id: 'op_consult_summary', name: 'Op_Consultations Summary', type: 'date_range' },
                    { id: 'op_consult_no_fin', name: 'OP_Consultations without financial', type: 'date_range' },
                    { id: 'op_procedures', name: 'OP_Procedures', type: 'date_range' },
                    { id: 'op_procedures_prov', name: 'OP_Procedures(Provisional)', type: 'date_range' },
                    { id: 'op_procedures_cancel', name: 'OP_ProceduresCancellations', type: 'date_range' },
                    { id: 'op_org_consult', name: 'Organization Consultations', type: 'date_range' },
                    { id: 'op_proc_count', name: 'Procedure Count', type: 'date_range' },
                    { id: 'op_proc_with_cancel', name: 'Procedure with cancellation', type: 'date_range' },
                    { id: 'op_ref_consult', name: 'Referral Consultations', type: 'date_range' },
                    { id: 'op_ref_doc_comm2', name: 'Referral Doctor Commission', type: 'date_range' },
                    { id: 'op_ref_doc_comm_rep', name: 'Referral Doctor Commission Report', type: 'date_range' },
                    { id: 'op_ref_doc_consult', name: 'Referral Doctor Consultations', type: 'date_range' },
                    { id: 'op_reg_fee_master', name: 'Registration Fee Master', type: 'none', label: 'Shows Registration Fee Master' },
                    { id: 'op_registrations', name: 'Registrations', type: 'date_range' },
                    { id: 'op_tpa_consult', name: 'TPA Consultations', type: 'date_range' }
                ],
                'Pharmacy': [
                    { id: 'ph_ipno_due_coll', name: 'IPNO Wise Due Collection', type: 'date_range' },
                    { id: 'ph_ipno_dues', name: 'IPNO Wise Dues', type: 'date_range' },
                    { id: 'ph_ipno_sales_ret', name: 'IPNo wise Medicine Sales and Returns', type: 'date_range' },
                    { id: 'ph_coll_prov', name: 'Pharmacy Collection (Provisional)', type: 'date_range' },
                    { id: 'ph_dues_report', name: 'Pharmacy Dues Report', type: 'date_range' },
                    { id: 'ph_dump_stock', name: 'Pharmacy Dump Stock', type: 'none', label: 'Shows Pharmacy Dump Stock' },
                    { id: 'ph_sales', name: 'Pharmacy Sales', type: 'date_range' },
                    { id: 'ph_sales_gen', name: 'Sales', type: 'date_range' },
                    { id: 'ph_sales_detail', name: 'Sales in Detail', type: 'date_range' },
                    { id: 'ph_sales_summary', name: 'Sales Summary', type: 'date_range' },
                    { id: 'ph_sales_returns', name: 'SalesReturns', type: 'date_range' },
                    { id: 'ph_user_sales', name: 'UserWiseSales', type: 'user_collection' }
                ]
            };

            // 4. Sample Mock Generated Report Data Provider
            service.generateReportData = function (category, reportName, params) {
                var rows = [];
                var now = new Date();
                var dateStr = '07/02/2021';

                if (reportName === 'My Collection') {
                    rows = [
                        { col1: 'OP-2021-101', col2: 'RAMESH KUMAR', col3: 'Dr. Srinivas Rao', col4: 'Consultation', col5: 'Cash', col6: '₹400' },
                        { col1: 'OP-2021-102', col2: 'SURESH BABU', col3: 'Dr. Padmaja', col4: 'Procedure (ECG)', col5: 'Cash', col6: '₹250' },
                        { col1: 'OP-2021-103', col2: 'SUNITHA DEVI', col3: 'Dr. Madhavi Latha', col4: 'Consultation', col5: 'Card', col6: '₹400' },
                        { col1: 'OP-2021-104', col2: 'VENKATESHWARLU', col3: 'Dr. V. Ramana', col4: 'Lab Due Collection', col5: 'Cash', col6: '₹650' }
                    ];
                } else if (reportName === 'MRNO wise Bill Details') {
                    rows = [
                        { col1: params.fromMrno || 'MR-1001', col2: 'G. NAGAIAH', col3: 'BILL-8821', col4: 'In-Patient Admission', col5: '₹15,000', col6: 'Paid' },
                        { col1: params.toMrno || 'MR-1002', col2: 'P. ANITHA', col3: 'BILL-8822', col4: 'Diagnostic CT Scan', col5: '₹3,500', col6: 'Paid' }
                    ];
                } else if (reportName === 'Patient wise Count') {
                    rows = [
                        { col1: 'Out-Patient Registrations', col2: '1,420 Patients', col3: '1,580 Patients', col4: '+11.2%', col5: 'Year ' + (params.year1 || '2021'), col6: 'Year ' + (params.year2 || '2021') },
                        { col1: 'In-Patient Admissions', col2: '380 Patients', col3: '410 Patients', col4: '+7.8%', col5: 'Year ' + (params.year1 || '2021'), col6: 'Year ' + (params.year2 || '2021') },
                        { col1: 'Diagnostic Radiology Cases', col2: '560 Patients', col3: '630 Patients', col4: '+12.5%', col5: 'Year ' + (params.year1 || '2021'), col6: 'Year ' + (params.year2 || '2021') }
                    ];
                } else if (reportName === 'Procedures' || reportName === 'Shows All Procedures') {
                    rows = [
                        { col1: 'PR001', col2: 'ECG 12 Lead', col3: 'Cardiology', col4: 'Standard 12 Lead Recording', col5: '₹250', col6: 'Active' },
                        { col1: 'PR002', col2: '2D Echocardiography', col3: 'Cardiology', col4: 'Colour Doppler Echo', col5: '₹1,500', col6: 'Active' },
                        { col1: 'PR003', col2: 'Nebulization', col3: 'General Medicine', col4: 'Per Session With Salbutamol', col5: '₹100', col6: 'Active' },
                        { col1: 'PR004', col2: 'Wound Dressing Large', col3: 'Surgery', col4: 'Sterile surgical redressing', col5: '₹300', col6: 'Active' },
                        { col1: 'PR005', col2: 'Suture Removal', col3: 'Surgery', col4: 'Post-op stitch removal', col5: '₹150', col6: 'Active' }
                    ];
                } else if (reportName === 'User Collection') {
                    var u = (params.userName && params.userName !== 'All') ? params.userName : 'MADAVI';
                    rows = [
                        { col1: u, col2: '07/02/2021', col3: 'OP Consultation', col4: '14 Bills', col5: '₹5,600', col6: 'Verified' },
                        { col1: u, col2: '07/02/2021', col3: 'OP Procedures', col4: '6 Bills', col5: '₹2,450', col6: 'Verified' },
                        { col1: u, col2: '07/02/2021', col3: 'Soft Lab Billing', col4: '18 Bills', col5: '₹9,800', col6: 'Verified' },
                        { col1: u, col2: '07/02/2021', col3: 'Total Collection', col4: '38 Bills', col5: '₹17,850', col6: 'Balanced' }
                    ];
                } else {
                    rows = [
                        { col1: 'REC-001', col2: '07/02/2021', col3: reportName, col4: 'Standard Patient Services', col5: '₹4,500', col6: 'Generated' },
                        { col1: 'REC-002', col2: '07/02/2021', col3: reportName, col4: 'Specialist Clinical Charges', col5: '₹8,200', col6: 'Generated' },
                        { col1: 'REC-003', col2: '07/02/2021', col3: reportName, col4: 'Pharmacy & Diagnostic Bills', col5: '₹3,750', col6: 'Generated' }
                    ];
                }

                return {
                    category: category,
                    reportName: reportName,
                    generatedAt: dateStr + ' ' + (now.toLocaleTimeString()),
                    params: angular.copy(params),
                    rows: rows
                };
            };

            return service;
        });
})();
