function Climage() {
	this.routePointsX = new Array();
	this.routePointsY = new Array();
	this.previousX = null;
	this.previousY = null;
	this.Xsize = 4;
};
var climagePrototype = {
	addRoutePoint: function( x,y ) {
		$('#output').append(x+", "+y+"<br/>");
		var ctx = $('#canvas')[0].getContext('2d');
		ctx.lineWidth = 2; 
		ctx.strokeStyle = '#00ff00';
		if(!this.previousX && !this.previousY) {
			ctx.beginPath();
			ctx.moveTo(x-this.Xsize, y-this.Xsize);
			ctx.lineTo(x+this.Xsize, y+this.Xsize);
			ctx.stroke();
			ctx.beginPath();
			ctx.moveTo(x+this.Xsize, y-this.Xsize);
			ctx.lineTo(x-this.Xsize, y+this.Xsize);
			ctx.stroke();
		} else {
			ctx.beginPath();
			ctx.moveTo(this.previousX, this.previousY);
			ctx.lineTo(x, y);
			ctx.stroke();
		}
		this.previousX = x;
		this.previousY = y;
		this.routePointsX.push(x);
		this.routePointsY.push(y);
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
		this.img.src = '/images/asdf.png';
		$('#canvas')[0].getContext('2d').drawImage(this.img,0,0,300,200);
	},
	save: function() {
		var imageData = $('#canvas')[0].toDataURL();
		$('#output').append(imageData);
	}
};

Climage.prototype = climagePrototype;

