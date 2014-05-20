/**
 * Created by swami on 10/04/14.
 */

angular.module('map')

    .directive('minimize', function($log, $window) {

        var link = function(scope, element, attrs) {
//            var className = scope.minimizeClass,
            var minimizeSize = parseInt(scope.minimize);

            $log.debug('linking minimize', scope, element, attrs);

            scope.$watch('minimizeOn', function(newVal, oldVal) {
                $log.debug("$watch.minimizeOn", newVal);
                $log.debug($window.innerWidth, element.width());
                if (angular.isDefined(newVal) && newVal !== oldVal) {
                    if (newVal) {
                        element.css('width', (element.width() - minimizeSize) + 'px');
//                        element.addClass(className);
                    } else {
                        element.css('width', '');
//                        element.removeClass(className);
                    }
                }
            });
        };

        return {
            restrict: 'A',
            link: link,
            scope: {
                minimize: '@',
                minimizeOn: '=',
                minimizeClass: '@'
            }
        };
    });

//    .directive('resizable', function($log, window) {
//        'use strict';
//
//        var link = function(scope, element, attrs) {
//            element.on('mousedown', function(event) {
//                event.preventDefault();
//
//                $document.on('mousemove', mousemove);
//                $document.on('mouseup', mouseup);
//            });
//
//            function mousemove(event) {
//                var x = event.pageX;
//                var y = window.innerHeight - event.pageY;
//
//                element.css({
//                    left: x + 'px',
//                    bottom: y + 'px'
//                });
//            }
//
//            function mouseup() {
//                $document.unbind('mousemove', mousemove);
//                $document.unbind('mouseup', mouseup);
//            }
//        };
//    });
//    .directive('sidebar', function(CONFIG, $log) {
//        'use strict';
//
//        var link = function(scope, element, attrs) {
//            var body = angular.element('body'),
//                toggleBtn = element.find('.toggle'),
//                toggleIcon = toggleBtn.find('i.fa');
////            scope.hidden = true;
//            $log.debug("Linking sidebar");
//            $log.debug("Scope ->", scope);
//            $log.debug("Element ->", element);
//            $log.debug("Attrs ->", attrs);
//
//            var toggle = function() {
//                if (scope.hidden) {
////                    toggleIcon.removeClass('fa-angle-left');
////                    toggleIcon.addClass('fa-angle-right');
//                    element.removeClass('sidebar-open');
//                    element.addClass('sidebar-closed');
//                    body.removeClass('sidebared');
//                    body.addClass('unsidebared');
//                } else {
////                    toggleIcon.removeClass('fa-angle-right');
////                    toggleIcon.addClass('fa-angle-left');
//                    element.removeClass('sidebar-closed');
//                    element.addClass('sidebar-open');
//                    body.removeClass('unsidebared');
//                    body.addClass('sidebared');
//                }
//            };
//
//            toggleBtn.mouseup(function(e) {
//                e.preventDefault();
//                $log.debug("hidden", scope.hidden);
//                scope.hidden = !scope.hidden;
//                toggle();
//            });
//        };
//
//        var controller = function($scope) {
//            $scope.hidden = true;
//            $log.debug("Sidebar Ctrl online", $scope);
//        };
//
//        return {
//            restrict: 'E',
//            templateUrl: CONFIG.templateUrl('sidebar.html'),
//            replace: true,
//            scope: {},
//            link: link,
//            controller: controller
//        };
//    });
