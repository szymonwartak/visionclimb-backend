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
		$('#output').append(x+", "+y+"<br/>");
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
		$('#routeName').attr('value',name);
		this.drawImage(image);
		this.setStyle();
		var previousX, previousY;
		for(var i1=0; i1<routePointsX.length; i1++) {
			if(i1==0) {
				this.drawRouteStart(routePointsX[0],routePointsY[0]);
				previousX = routePointsX[0];
				previousY = routePointsY[0];
			} else {
				this.drawRouteLine(previousX, previousY, routePointsX[i1], routePointsY[i1])
				previousX = routePointsX[i1];
				previousY = routePointsY[i1];
			}
		}
	},
	drawImage: function(image) {
		this.image = new Image();
		this.image.src = image;
		this.ctx.drawImage(this.image,0,0);
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
		/* var that = this;
		navigator.camera.getPicture(
			function(url) {
				that.img = new Image();
				that.img.onload = function() {
					$('#canvas')[0].getContext('2d').drawImage(that.img,0,0,300,200);				
				};
				that.img.src = url;
			},
			function(msg) {
				$('#output').append(msg);				
			}, // fail
			{ quality: 50, allowEdit: true, destinationType: navigator.camera.DestinationType.FILE_URI}
		); */
		this.img = new Image();
		var that = this;
		this.img.onload = function() {
			$('#canvas')[0].getContext('2d').drawImage(that.img,0,0,300,200);
			this.image = $('#canvas')[0].toDataURL();
		};
		this.img.src = '/images/asdf.png';
	},
	save: function() {
		var imageData = $('#canvas')[0].toDataURL();
		$('#output').append(imageData);
	}
};

Climage.prototype = climagePrototype;

