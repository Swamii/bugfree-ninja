/**
 * Created by Swami on 21/03/14.
 */

angular.module('map')
    .service('queryService', function($q, $timeout, $log, localStorage) {
        var geocoder = new google.maps.Geocoder();

        var errorTypes = {
            zero: {type: 'zero', message: 'No results for query'},
            overLimit: {type: 'overLimit', messsage: 'Usage limit has been overriden'},
            requestDenied: {type: 'requestDenied', message: 'Request was denied'},
            invalidRequest: {type: 'invalidRequest', message: 'Request was considered invalid.'}
        };

        var queue = [],
            delay = 250;

        var queueQuery = function(query, deferred) {
            var task = {q: query, d: deferred};

            if (queue.length === 0) {
                if (delay > 500) {
                    delay = 500;
                }
                doQuery(task);
            } else {
                queue.push(task);
                $timeout(doQuery, delay);
            }
        };

        var doQuery = function(task) {
            if (!angular.isDefined(task)) {
                task = queue.shift();
            }

            geocoder.geocode({address: task.q}, function(locations, status) {
                switch (status) {
                    case google.maps.GeocoderStatus.OK:

                        var formattedResults = [];
                        angular.forEach(locations, function(location) {
                            this.push({
                                lat: location.geometry.location.lat(),
                                lng: location.geometry.location.lng(),
                                label: location.formatted_address
                            });
                        }, formattedResults);  // the third argument specifies what var will become 'this'

                        $log.debug("Got locations for query:", task.q, "-", formattedResults);

                        localStorage.set(task.q, formattedResults);

                        task.d.resolve(formattedResults);

                        break;

                    case google.maps.GeocoderStatus.ZERO_RESULTS:
                        $log.debug("Got no locations for query: " + task.q);
                        task.d.resolve([]);
                        break;
                    case google.maps.GeocoderStatus.OVER_QUERY_LIMIT:
                        delay += 50;
                        $log.debug("Over query limit! Increasing delay to", delay, "queue.length", queue.length);
                        queue.unshift(task);
                        $timeout(doQuery, delay);
                        break;
                    case google.maps.GeocoderStatus.REQUEST_DENIED:
                        $log.error("Request was denied for query: " + task.q);
                        task.d.reject(errorTypes.requestDenied);
                        break;
                    case google.maps.GeocoderStatus.INVALID_REQUEST:
                        $log.error("Request was considered totally invalid! q: " + task.q);
                        task.d.reject(errorTypes.invalidRequest);
                        break;
                }

            });
        };

        return {
            errorTypes: errorTypes,
            getPos: function(query) {
                var deferred = $q.defer();

                // Try to get from local storage first.
                var possibleValue = localStorage.get(query);
                if (possibleValue !== null) {
                    $timeout(function() {
                        deferred.resolve(possibleValue);
                    });
                } else {
                    queueQuery(query, deferred);
                }

                return deferred.promise;
            }
        };
    })

    .service('localStorage', function($log, $window) {
        var localStorage = $window.localStorage;
        var prefix = 'map';
        var delimiter = ':_:';

        var parsePrefix = function(localPrefix) {
            if (!localPrefix) {
                localPrefix = prefix;
            }
            return localPrefix + delimiter;
        };

        var set = function(key, value, localPrefix) {
            localPrefix = parsePrefix(localPrefix);

            if (typeof value === 'undefined') {
                value = null;

            } else if (angular.isObject(value) || angular.isArray(value)) {
                value = angular.toJson(value);
            }

            key = localPrefix + key.toLowerCase();
            $log.debug("Setting value:", value, "for key", key);

            try {
                localStorage.setItem(key, value);
            } catch (e) {
                $log.error("Error setting key: " + key + ", value: " + value);
            }
        };

        var setAll = function(obj, localPrefix) {
            for (var key in obj) {
                set(key, obj[key], localPrefix);
            }
        };

        var get = function(key, localPrefix) {
            localPrefix = parsePrefix(localPrefix);

            key = localPrefix + key.toLowerCase();
            var value = localStorage.getItem(key);

            $log.debug("Getting value from storage: " + value + " for key: " + key);

            if (!value || value === 'null') {
                value = null;
            }

            else if (value.charAt(0) === "{" || value.charAt(0) === "[") {
                value = angular.fromJson(value);
            }

            return value;
        };

        var getAll = function(keys, localPrefix) {
            var retObj = {};
            angular.forEach(keys, function(key) {
                retObj[key] = get(key, localPrefix);
            });
            return retObj;
        };

        var keys = function(localPrefix) {
            localPrefix = parsePrefix(localPrefix);
            var ks = [];
            for (var key in localStorage) {
                if (key.substr(0, localPrefix.length) === localPrefix) {
                    ks.push(key);
                }
            }
            return ks;
        };

        var clear = function() {
            localStorage.clear();
        };

        return {
            set: set,
            setAll: setAll,
            get: get,
            getAll: getAll,
            keys: keys,
            clear: clear
        };
    })

    .service('wikiService', function(CONFIG, $http, $log) {
        var LOC_URL = CONFIG.BASE_URL + 'wiki';
        var INFO_URL = CONFIG.BASE_URL + 'wiki-info';

        var retrieveLocations = function(lat, lng) {
            var params = {
                lat: lat,
                lng: lng
            };
            return $http.get(LOC_URL, {params: params}).then(null, function(status, data) {
                $log.error('WikiService retrieval returned non 2xx error code:', status, data);
            });
        };

        var retrieveInfo = function(articleId, locale) {
            var params = {articleId: articleId, locale: locale};
            return $http.get(INFO_URL, {params: params}).then(null, function(status, data) {
                $log.error('WikiService info retrieval returned non 2xx error code', status, data);
            });
        };

        return {
            retrieveLocations: retrieveLocations,
            retrieveInfo: retrieveInfo
        };
    })

    .service('eventService', function($log, localStorage, $rootScope, $state) {
        var events = [];
        var storagePrefix = 'map_events';

        var init = function() {
            var oldEvents = localStorage.get('events', storagePrefix);
            if (angular.isArray(oldEvents)) {
                angular.forEach(oldEvents, function(event) {
                    event.onClick = function() {
                        $state.go('home', event.latLng);
                    };
                    events.push(event);
                });
            }
            $log.debug("EVENTS", events);
        };

        var addEvent = function(event) {
            if (angular.isString(event)) {
                event = {message: event};
            }
            if (!event.timestamp) {
                event.timestamp = new Date();
            }
            $log.debug("eventAdded!", event);
            events.unshift(event);
            localStorage.set('events', events, storagePrefix);
            $rootScope.$broadcast('eventAdded', event);
        };

        var deRegister = function() {
            // scope has died, we need to change the callback func to use GET params
            angular.forEach(events, function(event) {
                if (event.onClick) {
                    event.onClick = function() {
                        $state.go('home', event.latLng);
                    };
                }
            });
        };

        return {
            init: init,
            addEvent: addEvent,
            deRegister: deRegister,
            events: events
        };
    });


