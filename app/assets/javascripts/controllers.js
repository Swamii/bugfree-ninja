angular.module('map')
    .controller('HomeCtrl', function($scope, $log, $timeout, queryService, wikiService, eventService, $state, localStorage) {
        'use strict';
        $log.debug($state);

        var scope = this,
            MAX_MARKERS = 1000,
            USER_LOCATION_KEY = 'userLocation';

        scope.userLocation = localStorage.get(USER_LOCATION_KEY);
        scope.autoDiscoverActivated = false;
        scope.searchValue = '';
        scope.lookingForUser = false;
        scope.locations = [];

        scope.markers = {};
        scope.defaults = {scrollWheelZoom: false};
        scope.center = {zoom: 15};

        // Check GET-params
        if ($state.params && $state.params.lat && $state.params.lng) {
            $timeout(function() {
                var zoom = $state.params.zoom;
                scope.center = {
                    lat: parseFloat($state.params.lat),
                    lng: parseFloat($state.params.lng),
                    zoom: zoom && parseInt(zoom) || scope.center.zoom
                };
            });
        }

        scope.doCenter = function(label) {
            $log.debug("Got label", label);
            queryService.getPos(label).then(function(results) {
                var result = results[0];
                scope.center = {
                    lat: result.lat,
                    lng: result.lng,
                    zoom: 14
                };
                $log.debug("center updated", scope.center);
            });
        };

        scope.centerOnUser = function() {
            var scopeZoom = scope.center.zoom;
            $log.debug(scopeZoom);
            scope.center = {
                zoom: scopeZoom > 14 ? 14 : scopeZoom,
                lat: scope.userLocation.lat,
                lng: scope.userLocation.lng
            };
            $log.debug("scope.centerOnUser()", scope.center);
        };

        $scope.$on('$typeahead.select', function(event, label) {
            $log.debug('$on.$typeahead.select', label);
            scope.doCenter(label);
            scope.searchValue = '';
        });

        scope.getAddress = function(value) {
            if (!value || value.length < 3) {
                return [];
            }
            return queryService.getPos(value).then(function(result) {
                return result;
            });
        };

        scope.activateAutoDiscover = function() {
            scope.center.autoDiscover = true;

            // For some reason autoDiscover is reset, so scope is needed
            scope.autoDiscoverActivated = true;
            scope.lookingForUser = true;
            $log.debug("AutoDiscover is activated");
        };

        var addLocations = function(locations) {
            var allLocs = scope.locations;
            var allMarkers = scope.markers;
            var testToPrune = allLocs.length + locations.length - MAX_MARKERS;
            $log.debug("TO prune?", testToPrune);

            angular.forEach(locations, function(location) {
                if (!allMarkers[location.articleId]) {

                    if (allLocs.length > MAX_MARKERS) {
                        // prune old markers.
                        var removed = allLocs.shift();
                        delete allMarkers[removed.articleId];
                    }

                    allLocs.push(location);
                    var title = '<a href="'  + location.url + '" target="_blank"><b>' + location.title + '</b></a>',
                        message = title + '<br> Locale: ' + location.locale;

                    allMarkers[location.articleId] = {
                        lat: location.lat,
                        lng: location.lng,
                        title: title,
                        message: message,
                        locale: location.locale
                    };
                }
            });
        };

        // Core $watch-functions

        var shouldUpdate = function(newCenter, oldCenter) {
            var newLat = newCenter.lat,
                newLng = newCenter.lng,
                oldLat = oldCenter.lat,
                oldLng = oldCenter.lng;
            return (newCenter.zoom > 13 && newLat && newLng && (newLat !== oldLat || newLng !== oldLng));
        };

        var updateCenter = function(center, oldCenter) {
            $log.debug("center", center, "shouldUpdate ->", shouldUpdate(center, oldCenter));
            if (shouldUpdate(center, oldCenter)) {

                wikiService.retrieveLocations(center.lat, center.lng)
                    .then(function (response, wut) {
                        $log.debug(response, wut);
                        var locations = response.data;
                        $log.debug('WikiService retrieval returned:', locations);
                        addLocations(locations);
                    });
            }
        };

        $scope.$watch(function() { return scope.center; }, updateCenter);

        if (angular.isDefined(scope.userLocation)) {
            scope.centerOnUser();
        }

        // EVENT LISTENERS ------->

        $scope.$on('$destroy', function() {
            eventService.deRegister();
        });

        $scope.$on('leafletDirectiveMap.locationfound', function(e, locData, x) {
            scope.lookingForUser = false;
            scope.userLocation = {
                lat: locData.leafletEvent.latitude,
                lng: locData.leafletEvent.longitude
            };
            localStorage.set(USER_LOCATION_KEY, scope.userLocation);
            $log.debug(e, locData, scope.userLocation);
        });

        $scope.$on('leafletDirectiveMarker.popupopen', function(e, markerRef) {
            var marker = scope.markers[markerRef.markerName];

            eventService.addEvent({
                message: "Popup: " + marker.title,
                latLng: {lat: marker.lat, lng: marker.lng, zoom: 16},
                onClick: function() {
                    scope.center = this.latLng;
                    $log.debug("CLICK!", this.latLng, scope);
                }
            });

            $log.debug('Popen', e, markerRef, marker);
            if (!marker.added) {
                wikiService.retrieveInfo(markerRef.markerName, marker.locale)
                    .then(function(response) {
                        marker.message = marker.message + '<br>' + response.data.content;
                        marker.added = true;
                    });
            }
        });

    })

    .controller('SidebarCtrl', function($scope, $rootScope, $log, $timeout, $state, eventService, leafletData, leafletEvents) {
        'use strict';
        $log.debug("hello sidebar");
        $rootScope.sidebarOpen = false;
        $scope.animateAdded = false;

        $scope.events = eventService.events;

        $scope.toggleSidebar = function() {
            $log.debug("toggling sidebar");
            $timeout(function() {
                $rootScope.sidebarOpen = !$rootScope.sidebarOpen;

                // This will trigger some css changes, which we have to wait a bit for before we can reset the map.
                $timeout(function() {
                    leafletData.getMap().then(function(map) {
                        map.invalidateSize(false);
                    });
                }, 100);
            });
        };

        $scope.$on('eventAdded', function(e) {
            $scope.animateAdded = true;
            $timeout(function() {
                $scope.animateAdded = false;
            }, 1000);
        });

        var mapEvents = leafletEvents.getAvailableMapEvents();
        $log.debug("leaflet events!", mapEvents);

    });
