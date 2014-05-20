
angular.module('map', ['ngAnimate', 'ngSanitize', 'ui.router', 'leaflet-directive', 'mgcrea.ngStrap'])
    .constant('CONFIG', {
        BASE_URL: '/api/',
        templateUrl: function templateUrl(urlFragment) {
            return 'assets/partials/' + urlFragment;
        }
    })

    .config(function($stateProvider, $urlRouterProvider, CONFIG) {
        'use strict';

        $urlRouterProvider.otherwise('/');

        $stateProvider
            .state('home', {
                url: '/?lat&lng&zoom',
                templateUrl: 'map.html',  // inline ng-template in index.scala.html
                controller: 'HomeCtrl',
                controllerAs: 'home'
            })

            .state('about', {
                url: '/about',
                templateUrl: CONFIG.templateUrl('about.html')
            })

            .state('contact', {
                url: '/contact',
                templateUrl: CONFIG.templateUrl('contact.html')
            });

    })

    .run(function(eventService, localStorage, $rootScope) {
        $rootScope.sidebarOpen = false;
        eventService.init();
//        localStorage.clear();
    });