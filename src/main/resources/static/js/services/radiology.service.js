(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('RadiologyService', function () {
            var service = {};

            // 1. Departments List (Exact from Screenshot Page 9 & 10)
            service.departments = [
                'Select',
                'AUDIOLOGY',
                'CT SCAN',
                'MRI',
                'NUEROLOGY',
                'RADIOLOGY',
                'ULTRASONOGRAPHY',
                'ULTRASOUND'
            ];

            // 2. Investigations List (Exact from Screenshot Page 9 Bottom)
            service.investigations = [
                'Select',
                'USG HIP JOINT (IP)',
                'USG MAMOGRAM (BOTH BREAST)',
                'USG MAMOGRAM (SINGLE BREAST)',
                'USG NUCLEAR EC SCAN',
                'USG OBST SCAN WITH DOPPLER',
                'USG PELVIC RECTAL CONTRAST',
                'USG SCAN GUIDED PHERIAL TAPPING',
                'USG SCROTUM',
                'USG SMALL PART',
                'USG SOUND SCREENING',
                'USG THERAPEUTIC ASPIRATION',
                'USG USG CYSTOGRAM',
                'USGSCROTUM',
                'X RAY WRIST AP/LAT',
                'X-RAY',
                'X-RAY ABDOMEN ERECT POSITION',
                'X-RAY ABDOMEN ERECT POSTITION [ BED SIDE]',
                'X-RAY ANKLE AP VIEW',
                'X-RAY ANKLE AP/LAT',
                'X-RAY ANKLE JOINT AP/LAT [BEDSIDE]'
            ];

            // 3. Organization Types
            service.organizationTypes = [
                'Hospital',
                'Corporate',
                'Insurance',
                'TPA',
                'Arogyasri'
            ];

            // 4. Available Reagents / Consumables
            service.reagentsList = [
                'Select',
                'X-Ray Film 10x12',
                'X-Ray Film 12x15',
                'Barium Sulfate Suspension',
                'Iohexol Contrast Medium',
                'Ultrasound Transmission Gel 250ml',
                'Thermal Recording Paper',
                'CT Contrast Injector Syringe',
                'Lead Apron Disposable Cover'
            ];

            // 5. Pre-stored Radiology Charges
            service.chargesData = [
                {
                    department: 'RADIOLOGY',
                    investigation: 'X-RAY ABDOMEN ERECT POSITION',
                    orgType: 'Hospital',
                    hospitalShare: 350,
                    doctorShare: 150,
                    totalCharge: 500
                },
                {
                    department: 'RADIOLOGY',
                    investigation: 'X-RAY ANKLE AP/LAT',
                    orgType: 'Hospital',
                    hospitalShare: 300,
                    doctorShare: 150,
                    totalCharge: 450
                },
                {
                    department: 'ULTRASONOGRAPHY',
                    investigation: 'USG OBST SCAN WITH DOPPLER',
                    orgType: 'Hospital',
                    hospitalShare: 900,
                    doctorShare: 600,
                    totalCharge: 1500
                },
                {
                    department: 'CT SCAN',
                    investigation: 'USG NUCLEAR EC SCAN',
                    orgType: 'Hospital',
                    hospitalShare: 2400,
                    doctorShare: 1100,
                    totalCharge: 3500
                }
            ];

            // 6. Pre-stored Diagnostic Tests (Matching Page 10 demo: xrayches)
            service.diagnosticTests = [
                {
                    testCode: 'xrayches',
                    testName: 'x-ray chest pa view',
                    description: 'x-ray chest pa view',
                    instruction: 'Patient in erect position with deep inspiration',
                    department: 'RADIOLOGY',
                    active: true,
                    reagents: [
                        { reagentName: 'X-Ray Film 12x15', quantity: 1 },
                        { reagentName: 'Lead Apron Disposable Cover', quantity: 1 }
                    ]
                },
                {
                    testCode: 'usgob01',
                    testName: 'USG OBST SCAN WITH DOPPLER',
                    description: 'Obstetric scan with foetal umbilical doppler evaluation',
                    instruction: 'Full bladder recommended for 1st trimester evaluation',
                    department: 'ULTRASONOGRAPHY',
                    active: true,
                    reagents: [
                        { reagentName: 'Ultrasound Transmission Gel 250ml', quantity: 1 },
                        { reagentName: 'Thermal Recording Paper', quantity: 2 }
                    ]
                },
                {
                    testCode: 'cthead01',
                    testName: 'CT Brain Plain',
                    description: 'Plain non-contrast high-resolution spiral brain tomography',
                    instruction: 'Keep head still during acquisition',
                    department: 'CT SCAN',
                    active: true,
                    reagents: [
                        { reagentName: 'Lead Apron Disposable Cover', quantity: 1 }
                    ]
                }
            ];

            // Helper Methods
            service.getCharges = function () {
                return service.chargesData;
            };

            service.saveCharge = function (chargeItem) {
                var idx = service.chargesData.findIndex(function (c) {
                    return c.department === chargeItem.department && 
                           c.investigation === chargeItem.investigation && 
                           c.orgType === chargeItem.orgType;
                });
                var copy = angular.copy(chargeItem);
                copy.totalCharge = (Number(copy.hospitalShare) || 0) + (Number(copy.doctorShare) || 0);
                if (idx >= 0) {
                    service.chargesData[idx] = copy;
                } else {
                    service.chargesData.push(copy);
                }
                return copy;
            };

            service.getDiagnosticTests = function () {
                return service.diagnosticTests;
            };

            service.saveDiagnosticTest = function (testItem) {
                var idx = service.diagnosticTests.findIndex(function (t) {
                    return t.testCode.toLowerCase() === (testItem.testCode || '').toLowerCase();
                });
                var copy = angular.copy(testItem);
                if (idx >= 0) {
                    service.diagnosticTests[idx] = copy;
                } else {
                    service.diagnosticTests.push(copy);
                }
                return copy;
            };

            return service;
        });
})();
