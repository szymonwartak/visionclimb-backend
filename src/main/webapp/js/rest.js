var comm = (function() {
	return {
		server: (testEnv!=false ? "localhost:8080" : "www.visionclimb.com"),
		saveRoute: function(saveFunction, climageData) {
			$.ajax({
				type: 'POST',
				url: 'http://'+this.server+'/api/route/'+saveFunction,
				data: climageData,
				success: function(data) {
					alert('saved')
					// update the current state: 1) routes on map, and 2) routes on current image
					geo.markAreaRoutes(currentClimage.areaId)
					currentClimage.routes.push(data.routeId)
					comm.getRoutesAndImage(currentClimage.routes, data.imageId)
				},
				failure: function(data) {
					alert('save failed')
				},
				contentType: 'application/x-www-form-urlencoded'
			});
		},
		getRoutesAndImage: function(routeNums, imageId) {
			currentClimage.reset()
			$.ajax({
				type: 'POST',
				url: 'http://'+this.server+'/api/route/getRoutesAndImage',
				data: {
					routeIds: JSON.stringify(routeNums),
					imageId: imageId 
				},
				success: function(data) {
					currentClimage.set(data.imageId, data.image, data.routes)
				},
				failure: function(data) {
					alert('get failed')
				},
				contentType: 'application/x-www-form-urlencoded'
			});
		}
	}
})()