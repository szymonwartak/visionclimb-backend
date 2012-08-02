function Climage() {
	this.routePointsX = new Array();
	this.routePointsY = new Array();
	this.previousX = null;
	this.previousY = null;
	this.Xsize = 4;
	this.image = null;
	this.ctx = $('#canvas')[0].getContext('2d');
};
var climagePrototype = {
	addRoutePoint: function( x,y ) {
		this.setStyle();
		if(!this.previousX && !this.previousY) {
			this.drawRouteStart(x ,y);
		} else {
			this.drawRouteLine(this.previousX, this.previousY, x, y);
		}
		this.previousX = x;
		this.previousY = y;
		this.routePointsX.push(x);
		this.routePointsY.push(y);
	},
	drawRoute: function( name, image, routePointsX, routePointsY ) {
		if( image && routePointsX && routePointsY ) {
			this.routePointsX = routePointsX;
			this.routePointsY = routePointsY;
			this.ctx.clearRect(0,0,300,200);
			$('#routeName').attr('value',name);
			this.drawImage(image);
		}
	},
	drawRoutePoints: function() {
		this.setStyle();
		var previousX, previousY;
		for(var i1=0; i1<this.routePointsX.length; i1++) {
			if(i1==0) {
				this.drawRouteStart(this.routePointsX[0],this.routePointsY[0]);
				previousX = this.routePointsX[0];
				previousY = this.routePointsY[0];
			} else {
				this.drawRouteLine(previousX, previousY, this.routePointsX[i1], this.routePointsY[i1])
				previousX = this.routePointsX[i1];
				previousY = this.routePointsY[i1];
			}
		}
	},
	drawImage: function(image) {
		this.image = new Image();
		var that = this;
		this.image.onload = function() {
			that.ctx.drawImage(that.image,0,0,300,200);
			that.drawRoutePoints();
		};
		this.image.src = image;
	},
	setStyle: function() {
		this.ctx.lineWidth = 2;
		this.ctx.strokeStyle = '#00ff00';
	},
	drawRouteStart: function( x,y ) {
		this.ctx.beginPath();
		this.ctx.moveTo(x-this.Xsize, y-this.Xsize);
		this.ctx.lineTo(x+this.Xsize, y+this.Xsize);
		this.ctx.stroke();
		this.ctx.beginPath();
		this.ctx.moveTo(x+this.Xsize, y-this.Xsize);
		this.ctx.lineTo(x-this.Xsize, y+this.Xsize);
		this.ctx.stroke();
	},
	drawRouteLine: function ( x1,y1,x2,y2 ) {
		this.ctx.beginPath();
		this.ctx.moveTo(x1, y1);
		this.ctx.lineTo(x2, y2);
		this.ctx.stroke();
	},
	setGrade: function ( grade ) {
		this.grade = grade;
	},
	takePicture: function() {
//        var that = this;
//        navigator.camera.getPicture(
//            function(url) {
//                that.image = new Image();
//                that.image.onload = function() {
//                    that.ctx.drawImage(that.image,0,0,300,200);
//                    that.image = $('#canvas')[0].toDataURL();
//                };
//                that.image.src = url;
//            },
//            function(msg) { /* $('#output').append(msg); */ }, // fail
//            { quality: 40, targetWidth: 300, targetHeight: 200 }
//        );

		this.image = new Image();
		var that = this;
		this.image.onload = function() {
			that.ctx.drawImage(that.image,0,0,300,200);
			that.image = $('#canvas')[0].toDataURL();
		};
		this.image.src = 'images/asdf.png';
	},
	save: function() {
		var imageData = $('#canvas')[0].toDataURL();
		$('#output').append(imageData);
	}
};

Climage.prototype = climagePrototype;

