(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('LabService', function () {
            var service = {};

            // 1. Departments Master
            service.departments = [
                'BIOCHEMISTRY',
                'CARDIOLOGY',
                'CYTOLOGY',
                'HEAMATOLOGY',
                'HISTOPATHOLOGY',
                'IMMUNOASSAYS',
                'MICROBIOLOGY',
                'PATHOLOGY',
                'SEROLOGY'
            ];

            // 2. Tests catalog by Department (Extracted exactly from Pages 6-9)
            service.testsByDepartment = {
                'BIOCHEMISTRY': [
                    'ANTI THROMBIN III',
                    'ALPHA FETO PROTIEN',
                    'ALPHA FETO PROTIEN(AFP)',
                    'AMMONIA',
                    'AMMONIA ACID',
                    'AMOEBIC SEROLOGY',
                    'ANEMIYA PROFILE',
                    'ANGIOTENSIIN CONVERTING ENZYME',
                    'ANTENATAL PROFILE',
                    'ANTI CARDIO LIPINE IGG',
                    'ANTI CARDIO LIPINE IGM',
                    'ANTI CCP ANTI BODY',
                    'ANTI DIUERETIC HORMONE(ADH)',
                    'ANTI HBS',
                    'ANTI LKM ANTIBODY',
                    'ANTI NMO ANTIBODIES',
                    'ANTI SPERM ANTI BODIES (ASAB)',
                    'ANTI TB IGM',
                    'ANTI THROMBPIN III ACTIVITY',
                    'ANTI THYRO GLOBULIN'
                ],
                'CARDIOLOGY': [
                    '2D ECHO',
                    '2D ECHO BEDSIDE',
                    '2D ECHO EMERGENCY',
                    'BAL FLUID FOR GRAM STAIN',
                    'BED SIDE 2D ECHO',
                    'CAD CAG+RAG+PAG',
                    'CAR ECG',
                    'CAR EEG',
                    'CAR EEG BED SIDE',
                    'CAR HOLTER ANALYSIS',
                    'CAR PREDIATIC ECHOCARDIOGRAPHY',
                    'CAR RENAL ANGIOGRAPHY',
                    'CAR STRESS THALIUM TEST',
                    'CAR TEE',
                    'CAR TMT/GXT',
                    'CARDIAC NGM',
                    'CARPFT',
                    'DOOPLER STUDY OF LOWER LIMB (ARTERIAL+VENOUS)',
                    'ECG'
                ],
                'CYTOLOGY': [
                    'ASCITIC FLUID FOR CYTOLOGY',
                    'ASCTIC FLUID FOR CYTOLOGY',
                    'BONE MARROW ASPIRATION',
                    'CSF FOR CYTOLOGY',
                    'FNAC',
                    'LEURAL FLUID FOR CYTOLOGYY',
                    'LIVER ASPIRATION FLUID FOR CYTOLOGY',
                    'PAP SMEAR',
                    'PERITONIAL FLUID FOR CYTOLOGY',
                    'PLEURAL FLUID FOR CYTOLOGY',
                    'SYNOVIAL FLUID FOR CYTOLOGY'
                ],
                'HEAMATOLOGY': [
                    'ABSOLUTE EOSINOPHIL COUNT',
                    'ABSOLUTE NEUTROPHIL COUNT',
                    'ANTI THYROID ANTIBODIES(ATG&AMA)',
                    'ANTI TPO',
                    'APOLIPOPROTEIN - B/A1 RATIO',
                    'BAEPS',
                    'BLOOD FOR ANEROBIC CULTURE',
                    'BLOOD GLUCOSE(F & PP)',
                    'BLOOD GLUCOSE(F&PP)',
                    'C.E.A',
                    'CARBAMAZEPINE/TERTERTOL',
                    'CEA',
                    'CELL COUNT',
                    'COLONOSCOPY',
                    'CT CONTRAST_1',
                    'DIFFERENTIAL COUNT (DC)',
                    'DI-HYDROTESTOSTERONE(DHT)',
                    'EPINEPHRINE/ADRENALINE',
                    'ERYTHROSITE SEDIMENTATION RATE'
                ],
                'HISTOPATHOLOGY': [
                    'BIOPSY SMALL',
                    'BIOPSY MEDIUM',
                    'BIOPSY LARGE',
                    'RADICAL SPECIMEN'
                ],
                'IMMUNOASSAYS': [
                    'ANTI MULLARIAN HORMONE (AMH)',
                    'SERUM IGE LEVEL ESTIMATION'
                ],
                'MICROBIOLOGY': [
                    'BLOOD CULTURE & SENSITIVITY',
                    'SPUTUM AFB',
                    'URINE CULTURE & SENSITIVITY',
                    'STOOL ROUTINE & MICROSCOPY'
                ],
                'PATHOLOGY': [
                    'ACITIC FLUID',
                    'ANTI HAV IGM',
                    'ANTI HEP E IGM',
                    'ANTI HEV (IGG-IGM)',
                    'ANTI MITOCHONDRIAL ANTI BODY',
                    'ANTI PHOSPHOLIPID ANTIBODIES-IGG',
                    'ANTI PHOSPHOLIPID ANTIBODY IGG-IGM',
                    'ANTI SMOOTH MUSCLE ANTIBODY(ASMA)',
                    'APOLIPOPROTEIN-A1(APO-A1)',
                    'ASCITIC FLUID EXAMINATION',
                    'ASCITIC FLUID FOR CELL TYPE AND CELL COUNT',
                    'ASCITIC FLUID FOR CYTOLOG',
                    'ASCITIC FLUID FOR TC & DC',
                    'ASCITIC FULID FOR CELLCOUNT',
                    'B12',
                    'BICORBONATE',
                    'BIG BIOPSY',
                    'BIOPSY LARGE',
                    'BIOPSY(LARGE)'
                ],
                'SEROLOGY': [
                    'ASO TITRE',
                    'DENGUE NS1 ANTIGEN',
                    'HIV 1 & 2 RAPID',
                    'RA FACTOR',
                    'TYPHIDOT RAPID IgM',
                    'VDRL / RPR'
                ]
            };

            // 3. Searchable Lab Tests for Registration Autocomplete (Page 2 & 3)
            service.availableTests = [
                { code: 'CA125', name: 'CA 125', rate: 1200.00, dept: 'BIOCHEMISTRY' },
                { code: 'CBP', name: 'COMPLETE BLOOD PICTURE', rate: 300.00, dept: 'HEAMATOLOGY' },
                { code: 'HBA1C', name: 'GLYCOSYLATED HEMOGLOBIN (HBA1C)', rate: 600.00, dept: 'BIOCHEMISTRY' },
                { code: 'HBPERCENT', name: 'HEMOGLOBIN PERCENTAGE(HB%)', rate: 100.00, dept: 'HEAMATOLOGY' },
                { code: 'HBSAGELISA', name: 'HBSAG ELISA', rate: 400.00, dept: 'SEROLOGY' },
                { code: 'HCVGEN', name: 'HCV GENOTYPING', rate: 2500.00, dept: 'PATHOLOGY' },
                { code: 'HEAMO', name: 'HEAMOGRAM', rate: 350.00, dept: 'HEAMATOLOGY' },
                { code: 'HIP', name: 'X-RAY HIP AP/LAT', rate: 500.00, dept: 'CARDIOLOGY' },
                { code: 'HIVE', name: 'HIV (ELISA)', rate: 450.00, dept: 'SEROLOGY' },
                { code: 'SE_CR', name: 'SERUM CREATININE', rate: 250.00, dept: 'BIOCHEMISTRY' },
                { code: 'SE_EL', name: 'SERUM ELECTROLYTES (IP)', rate: 450.00, dept: 'BIOCHEMISTRY' },
                { code: 'CRP', name: 'CRP (C-REACTIVE PROTEIN)', rate: 400.00, dept: 'BIOCHEMISTRY' },
                { code: 'ESR', name: 'ERYTHROSITE SEDIMENTATION RATE', rate: 150.00, dept: 'HEAMATOLOGY' },
                { code: 'ECG_BED', name: 'ECG BED SIDED', rate: 300.00, dept: 'CARDIOLOGY' },
                { code: 'MIN_SURG', name: 'MINOR SURGICAL PROFILE', rate: 1800.00, dept: 'PATHOLOGY' },
                { code: 'XR_CHEST', name: 'X-RAY CHEST AP VIEW FOR RIB', rate: 350.00, dept: 'CARDIOLOGY' },
                { code: 'XR_CERV', name: 'X-RAY CERVICAL SPINE AP&LAT VIEW', rate: 400.00, dept: 'CARDIOLOGY' }
            ];

            // 4. Lab Patients Pre-loaded for Quick Lookup (Page 1 bottom)
            service.registeredPatients = [
                { id: '1922', mrNo: 'MR-1922', name: 'BASSI SUNITHA', firstName: 'SUNITHA', middleName: '', lastName: 'BASSI', title: 'Mrs', mobile: '7893377413', age: '32', gender: 'Female' },
                { id: '1921', mrNo: 'MR-1921', name: 'JELA SATEVVA', firstName: 'SATEVVA', middleName: '', lastName: 'JELA', title: 'Mrs', mobile: '9494265334', age: '45', gender: 'Female' },
                { id: '1920', mrNo: 'MR-1920', name: 'MAMAI SHIVABAI', firstName: 'SHIVABAI', middleName: '', lastName: 'MAMAI', title: 'Mrs', mobile: '9440884323', age: '50', gender: 'Female' },
                { id: '1919', mrNo: 'MR-1919', name: 'NAGAIAH G', firstName: 'NAGAIAH', middleName: '', lastName: 'G', title: 'Mr', mobile: '9515181992', age: '58', gender: 'Male' },
                { id: '1918', mrNo: 'MR-1918', name: 'BAIRI GANGADHAR', firstName: 'GANGADHAR', middleName: '', lastName: 'BAIRI', title: 'Mr', mobile: '8897575783', age: '42', gender: 'Male' },
                { id: '1917', mrNo: 'MR-1917', name: 'GANTHA PUSHPALATHA', firstName: 'PUSHPALATHA', middleName: '', lastName: 'GANTHA', title: 'Mrs', mobile: '9603905858', age: '28', gender: 'Female' },
                { id: '1916', mrNo: 'MR-1916', name: 'P VINODA', firstName: 'VINODA', middleName: '', lastName: 'P', title: 'Mrs', mobile: '9866497318', age: '36', gender: 'Female' },
                { id: '1915', mrNo: 'MR-1915', name: 'T VIMALA', firstName: 'VIMALA', middleName: '', lastName: 'T', title: 'Mrs', mobile: '9391227858', age: '30', gender: 'Female' }
            ];

            // 5. Lab Due Records (Page 3 bottom)
            service.dueRecords = [
                {
                    patientName: 'BASSI SUNITHA',
                    mrNo: 'MR-1922',
                    labNo: 'L-2570',
                    mobile: '7893377413',
                    gender: 'Female',
                    address: 'H No 4-22, Khaleelwadi, Nizamabad',
                    totalAmount: 1500,
                    discPercent: 0,
                    discount: 0,
                    netAmount: 1500,
                    totalReceived: 800,
                    dueAmount: 700
                },
                {
                    patientName: 'BAIRI GANGADHAR',
                    mrNo: 'MR-1918',
                    labNo: 'L-2568',
                    mobile: '8897575783',
                    gender: 'Male',
                    address: 'Subhashnagar, Nizamabad',
                    totalAmount: 1200,
                    discPercent: 0,
                    discount: 0,
                    netAmount: 1200,
                    totalReceived: 500,
                    dueAmount: 700
                }
            ];

            // 6. Report Status Data (Page 4 bottom exact match)
            service.reportStatuses = [
                { labNo: '2570', patientName: 'THAMPULURI RAMADEVI', testName: 'ECG BED SIDED', status: 'Sample Collected' },
                { labNo: '2569', patientName: 'PULLENI SUPRIYA', testName: 'MINOR SURGICAL PROFILE', status: 'Registered' },
                { labNo: '2568', patientName: 'BAIRI GANGADHAR', testName: 'X-RAY CHEST AP VIEW FOR RIB', status: 'Registered' },
                { labNo: '2567', patientName: 'JELA SATEVVA', testName: 'X-RAY CERVICAL SPINE AP&LAT VIEW', status: 'Registered' },
                { labNo: '2566', patientName: 'ANKAM BOJANNA', testName: 'CRP (C-REACTIVE PROTEIN)', status: 'Sample Collected' },
                { labNo: '2566', patientName: 'ANKAM BOJANNA', testName: 'ERYTHROSITE SEDIMENTATION RATE', status: 'Sample Collected' },
                { labNo: '2565', patientName: 'MAMAI SHIVABAI', testName: 'ECG BED SIDED', status: 'Sample Collected' },
                { labNo: '2564', patientName: 'NANGI MUTHYAM', testName: 'COMPLETE BLOOD PICTURE', status: 'Sample Collected' },
                { labNo: '2564', patientName: 'NANGI MUTHYAM', testName: 'SERUM CREATININE', status: 'Sample Collected' },
                { labNo: '2564', patientName: 'NANGI MUTHYAM', testName: 'SERUM ELECTROLYTES (IP)', status: 'Registered' },
                { labNo: '2563', patientName: 'UZER BEE', testName: 'COMPLETE BLOOD PICTURE', status: 'Sample Collected' },
                { labNo: '2563', patientName: 'UZER BEE', testName: 'SERUM ELECTROLYTES (IP)', status: 'Registered' },
                { labNo: '2562', patientName: 'THAMPULURI RAMADEVI', testName: 'COMPLETE BLOOD PICTURE', status: 'Sample Collected' }
            ];

            // 7. Configured Test Field Definitions (Page 6-9)
            service.testFields = [
                { department: 'BIOCHEMISTRY', testName: 'ALPHA FETO PROTIEN(AFP)', fieldName: 'AFP LEVEL', unit: 'ng/mL', type: 'Quantitative' },
                { department: 'CARDIOLOGY', testName: 'ECG', fieldName: 'HEART RATE', unit: 'bpm', type: 'Quantitative' },
                { department: 'HEAMATOLOGY', testName: 'COMPLETE BLOOD PICTURE', fieldName: 'HEMOGLOBIN', unit: 'gm/dL', type: 'Quantitative' }
            ];

            // Helper methods
            service.getDepartments = function () {
                return service.departments;
            };

            service.getTestsByDepartment = function (dept) {
                if (!dept || dept === 'Select') return [];
                return service.testsByDepartment[dept] || [];
            };

            service.searchTests = function (q) {
                if (!q || !q.trim()) return [];
                var lower = q.toLowerCase();
                return service.availableTests.filter(function (t) {
                    return t.name.toLowerCase().indexOf(lower) !== -1 ||
                           t.code.toLowerCase().indexOf(lower) !== -1;
                });
            };

            service.searchPatient = function (q) {
                if (!q || !q.trim()) return null;
                var lower = q.toLowerCase().trim();
                return service.registeredPatients.find(function (p) {
                    return p.name.toLowerCase().indexOf(lower) !== -1 ||
                           p.mobile.indexOf(lower) !== -1 ||
                           p.id === lower;
                });
            };

            service.searchDueRecord = function (q) {
                if (!q || !q.trim()) return null;
                var lower = q.toLowerCase().trim();
                return service.dueRecords.find(function (d) {
                    return d.patientName.toLowerCase().indexOf(lower) !== -1 ||
                           d.mrNo.toLowerCase().indexOf(lower) !== -1 ||
                           d.labNo.toLowerCase().indexOf(lower) !== -1 ||
                           d.mobile.indexOf(lower) !== -1;
                });
            };

            service.addTestField = function (field) {
                service.testFields.push(angular.copy(field));
                return field;
            };

            service.getTestFields = function () {
                return service.testFields;
            };

            return service;
        });
})();
