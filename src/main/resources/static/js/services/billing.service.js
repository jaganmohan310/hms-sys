/**
 * Billing & Financial Computation Service
 * Indus Super Speciality Hospital HMS
 */
(function () {
    'use strict';

    angular.module('hmsApp')
        .factory('BillingService', function () {
            var service = {};

            service.calculateBilling = function (fee, discountAmt, paidAmt) {
                var total = Number(fee) || 0;
                var discount = Number(discountAmt) || 0;
                var paid = Number(paidAmt) || 0;
                var net = Math.max(0, total - discount);
                var due = Math.max(0, net - paid);

                return {
                    totalAmount: total,
                    discount: discount,
                    netAmount: net,
                    paidAmount: paid,
                    dueAmount: due
                };
            };

            service.calculateDiscountAmount = function (fee, discountPercent) {
                var total = Number(fee) || 0;
                var pct = Number(discountPercent) || 0;
                if (total > 0 && pct > 0) {
                    return Math.round((total * pct) / 100);
                }
                return 0;
            };

            service.getDiscountPercentForType = function (discountType) {
                switch (discountType) {
                    case 'Senior Citizen':
                    case 'Employee':
                        return 20;
                    case 'Camp':
                        return 50;
                    case 'VIP':
                        return 15;
                    case 'No Discount':
                    default:
                        return 0;
                }
            };

            service.calculateDueCollection = function (dueAmount, paidAmount) {
                var due = Number(dueAmount) || 0;
                var paid = Number(paidAmount) || 0;
                var balance = Math.max(0, due - paid);
                return balance;
            };

            return service;
        });
})();
