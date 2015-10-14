/**
 * Created by kenlee on 10/12/15.
 */

var photo_objs=[];
var markers = [];
var map;



$(document).ready(function() {


        function maxDate(){
            var dates = [];
            for(var i=0; i<photo_objs.length; i++) {
                dates.push(photo_objs[i].date);
            }
            return new Date(Math.max.apply(null, dates));
        }

        function minDate(){
            var dates = [];
            for(var i=0; i<photo_objs.length; i++) {
                dates.push(photo_objs[i].date);
            }
            return new Date(Math.min.apply(null, dates));
        }
        // parse a date in yyyy-mm-dd format
        function parseDate(input) {
            var parts = input.split('-');
            // new Date(year, month [, day [, hours[, minutes[, seconds[, ms]]]]])
            return new Date(parts[0], parts[1]-1, parts[2]); // Note: months are 0-based
        }

         $('.stream_img').each(function(){
                photo_objs.push({
                    lat: parseFloat($(this).attr('lat')),
                    lng: parseFloat($(this).attr('lng')),
                    photo_url: $(this).attr('src'),
                    date: parseDate($(this).attr('date'))
                });
            });

		function initMap() {
		  $('#map-canvas').gmap({'zoom': 2, 'disableDefaultUI':true}).bind('init', function(evt, map) {
            console.log(photo_objs);
			var bounds = map.getBounds();
			var southWest = bounds.getSouthWest();
			var northEast = bounds.getNorthEast();
			var lngSpan = northEast.lng() - southWest.lng();
			var latSpan = northEast.lat() - southWest.lat();
			for(var i=0; i<photo_objs.length; i++) {
				var lat = southWest.lat() + latSpan * Math.random();
				var lng = southWest.lng() + lngSpan * Math.random();
				var point = new google.maps.LatLng(lat,lng);
				var marker = new google.maps.Marker({map:map, position:point, clickable:true});
				var infowindow = new google.maps.InfoWindow();
                var photo_url = photo_objs[i].photo_url;
				google.maps.event.addListener(marker,'click',(function(marker, photo_url, infowindow) {
					return function() {
						infowindow.setContent("<img width=\"50\" height=\"50\" src=" + photo_url + "/>");
						infowindow.open(map,marker);
					};
				})(marker, photo_url, infowindow));

				markers.push(marker);
			}

			var markerCluster = new MarkerClusterer(map, markers);
		  });
		}

        $("#dateSlider").dateRangeSlider({
            bounds: {min: minDate(), max:maxDate()},
            defaultValues: {min:minDate(), max:maxDate()},
            min: minDate(),
            max: maxDate()
        });

        $("#dateSlider").bind("valuesChanged", function(e, data){
            var min = data.values.min;
            var max = data.values.max;
            console.log("Values just changed. min: " + data.values.min + " max: " + data.values.max);
            markers[0].setMap(null);

        });

		initMap();
});

