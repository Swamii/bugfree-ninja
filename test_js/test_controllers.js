describe('Unit: HomeCtrl', function() {
    var LAT = 1.000,
        LON = 0.1111;

    beforeEach(function() {
        module('map');

        inject(function($templateCache) {
            $templateCache.put('map.html', '');
        })
    });

    var ctrl, $scope;

    // Inject the $controller and $rootScope services
    // in the beforeEach block
    beforeEach(inject(function($controller, $rootScope, $timeout, $q) {
        $scope = $rootScope.$new();
        ctrl = $controller('HomeCtrl', {
            $scope: $scope,
            queryService: {
                getPos: function(query) {
                    var deferred = $q.defer();
                    $timeout(function() {
                        deferred.resolve([{lat: LAT, lng: LON}]);
                    });
                    return deferred.promise;
                }
            }
        });
    }));

    it('should should be defined', function() {
        expect(ctrl).toBeDefined();
        expect($scope).toBeDefined();
    });

    it('should update latitude and longitude', inject(function($timeout, $httpBackend) {
        $httpBackend.expect('GET', '/api/wiki?lat=1&lng=0.1111')
            .respond({
                'success': true,
                'data': []
            });

        ctrl.doCenter();
        $timeout.flush();
        expect(ctrl.center.lat).toEqual(LAT);
        expect(ctrl.center.lng).toEqual(LON);
    }));
});