var server = "www.visionclimb.com"
function postClimage() {
    $.ajax({
        type: 'POST',
        url: 'http://'+server+'/api/route/postworks',
        data: {
            name: $('#routeName')[0].value,
            routePointsX: JSON.stringify(currentClimage.routePointsX),
            routePointsY: JSON.stringify(currentClimage.routePointsY),
            image: currentClimage.image
        },
        success: function(data) {
            alert(data);
        },
        contentType: 'application/x-www-form-urlencoded'
    });
}


function getRoute(routeNum) {
    $.getJSON('http://'+server+'/api/route/get/'+routeNum, function(data) {
        currentClimage.drawRoute(data.name, data.image, $.parseJSON(data.routePointsX), $.parseJSON(data.routePointsY));
    });
}
