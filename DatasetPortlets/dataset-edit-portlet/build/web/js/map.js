/**
 * 
 */

// This application is provided by Kjell Scharning
//  Licensed under the Apache License, Version 2.0;
//  http://www.apache.org/licenses/LICENSE-2.0

function gob(e){if(typeof(e)=='object')return(e);if(document.getElementById)return(document.getElementById(e));return(eval(e))}
var map;
var polyShape;
var startMarker;
var nemarker;
var rectangle;
var southWest;
var northEast;
var startpoint;
var polyPoints = [];
var plmcur = 0;
var lcur = 0;
var pcur = 0;
var placemarks = [];
var polylinestyles = [];
var polygonstyles = [];
function polystyle() {
    this.name = "Lump";
    this.kmlcolor = "CD0000FF";
    this.kmlfill = "9AFF0000";
    this.color = "#FF0000";
    this.fill = "#0000FF";
    this.width = 2;
    this.lineopac = 0.8;
    this.fillopac = 0.6;
}
function linestyle() {
    this.name = "Path";
    this.kmlcolor = "FF0000FF";
    this.color = "#FF0000";
    this.width = 3;
    this.lineopac = 1;
}
function placemarkobject() {
    this.name = "NAME";
    this.desc = "YES";
    this.style = "Path";
    this.stylecur = 0;
    this.tess = 1;
    this.alt = "clampToGround";
    this.plmtext = "";
    this.jstext = "";
    this.jscode = [];
    this.kmlcode = [];
    this.poly = "pl";
    this.shape = null;
    this.point = null;
    this.toolID = 1;
    this.hole = 0;
    this.ID = 0;
}
function createplacemarkobject() {
	var thisplacemark = new placemarkobject();
    placemarks.push(thisplacemark);
}
function createpolygonstyleobject() {
	var polygonstyle = new polystyle();
    polygonstyles.push(polygonstyle);
}
function createlinestyleobject() {
    var polylinestyle = new linestyle();
    polylinestyles.push(polylinestyle);
}
function initmap(){
    var latlng = new google.maps.LatLng(59.914063, 10.737874);//(45.0,7.0);//45.074723, 7.656433
    var myOptions = {
        zoom: 15, //3
        center: latlng,
        draggableCursor: 'default',
        draggingCursor: 'pointer',
        scaleControl: true,
        mapTypeControl: false,
        //mapTypeControlOptions:{style: google.maps.MapTypeControlStyle.DROPDOWN_MENU},
        mapTypeId: google.maps.MapTypeId.SATELLITE,
        streetViewControl: false};
    map = new google.maps.Map(gob('map_canvas'),myOptions);
    polyPoints = new google.maps.MVCArray(); // collects coordinates
    createplacemarkobject();
    createlinestyleobject();
    createpolygonstyleobject();
    preparePolyline(); // create a Polyline object
    newstart();
    google.maps.event.addListener(map, 'click', addLatLng);
}

function preparePolyline(){
	var polyOptions = {
        path: polyPoints,
        strokeColor: polylinestyles[lcur].color,
        strokeOpacity: polylinestyles[lcur].lineopac,
        strokeWeight: polylinestyles[lcur].width};
    polyShape = new google.maps.Polyline(polyOptions);
    polyShape.setMap(map);
}

function activateRectangle() {
    rectangle = new google.maps.Rectangle({
        map: map,
        strokeColor: polygonstyles[pcur].color,
        strokeOpacity: polygonstyles[pcur].lineopac,
        strokeWeight: polygonstyles[pcur].width,
        fillColor: polygonstyles[pcur].fill,
        fillOpacity: polygonstyles[pcur].fillopac
    });
}
function addLatLng(point){

    // Rectangle and circle can't collect points with getPath. solved by letting Polyline collect the points and then erase Polyline
    polyPoints = polyShape.getPath();
    polyPoints.insertAt(polyPoints.length, point.latLng); // or: polyPoints.push(point.latLng)
    if(polyPoints.length == 1) {
        startpoint = point.latLng;
        placemarks[plmcur].point = startpoint; // stored because it's to be used when the shape is clicked on as a stored shape
        setstartMarker(startpoint);
    }
    if(polyPoints.length == 2) createrectangle(point);
}
function setstartMarker(point){
	startMarker = new google.maps.Marker({
        position: point,
        map: map});
    startMarker.setTitle("#" + polyPoints.length);
}
function createrectangle(point) {
	// startMarker is southwest point. now set northeast
    nemarker = new google.maps.Marker({
        position: point.latLng,
        draggable: true,
        raiseOnDrag: false,
        title: "Draggable",
        map: map});
    google.maps.event.addListener(startMarker, 'drag', drawRectangle);
    google.maps.event.addListener(nemarker, 'drag', drawRectangle);
    startMarker.setDraggable(true);
    startMarker.setAnimation(null);
    startMarker.setTitle("Draggable");
    polyShape.setMap(null); // remove the Polyline that has collected the points
    polyPoints = [];
    drawRectangle();
}
function drawRectangle() {
    southWest = startMarker.getPosition();
    northEast = nemarker.getPosition();
    var latLngBounds = new google.maps.LatLngBounds(
        southWest,
        northEast
    );
    rectangle.setBounds(latLngBounds);
    // the Rectangle was created in activateRectangle(), called from newstart(), which may have been called from setTool()
    var northWest = new google.maps.LatLng(southWest.lat(), northEast.lng());
    var southEast = new google.maps.LatLng(northEast.lat(), southWest.lng());
    gob('metadataForm:northBoundingLatitude').value = northEast.lat().toFixed(6);
    gob('metadataForm:eastBoundingLongitude').value = northEast.lng().toFixed(6);
    gob('metadataForm:southBoundingLatitude').value = southWest.lat().toFixed(6);
    gob('metadataForm:westBoundingLongitude').value = southWest.lng().toFixed(6);
    polyPoints = [];
    polyPoints.push(southWest);
    polyPoints.push(northWest);
    polyPoints.push(northEast);
    polyPoints.push(southEast);
}
// Clear current Map
function clearMap(){
    if(polyShape) polyShape.setMap(null); // polyline or polygon
    if(rectangle) rectangle.setMap(null);
    plmcur = 0;
    newstart();
    placemarks = [];
    createplacemarkobject();
}
function newstart() {
    polyPoints = [];
    destinations = [];
    if(startMarker) startMarker.setMap(null);
    if(nemarker) nemarker.setMap(null);
    placemarks[plmcur].style = polygonstyles[polygonstyles.length-1].name;
    placemarks[plmcur].stylecur = polygonstyles.length-1;
    preparePolyline(); // use Polyline to collect clicked point
    activateRectangle();
}

function color_html2kml(color){
    var newcolor ="FFFFFF";
    if(color.length == 7) newcolor = color.substring(5,7)+color.substring(3,5)+color.substring(1,3);
    return newcolor;
}
function color_hex2dec(color) {
    var deccolor = "255,0,0";
    var dec1 = parseInt(color.substring(1,3),16);
    var dec2 = parseInt(color.substring(3,5),16);
    var dec3 = parseInt(color.substring(5,7),16);
    if(color.length == 7) deccolor = dec1+','+dec2+','+dec3;
    return deccolor;
}
function getopacityhex(opa){
    var hexopa = "66";
    if(opa == 0) hexopa = "00";
    if(opa == .0) hexopa = "00";
    if(opa >= .1) hexopa = "1A";
    if(opa >= .2) hexopa = "33";
    if(opa >= .3) hexopa = "4D";
    if(opa >= .4) hexopa = "66";
    if(opa >= .5) hexopa = "80";
    if(opa >= .6) hexopa = "9A";
    if(opa >= .7) hexopa = "B3";
    if(opa >= .8) hexopa = "CD";
    if(opa >= .9) hexopa = "E6";
    if(opa == 1.0) hexopa = "FF";
    if(opa == 1) hexopa = "FF";
    return hexopa;
}

function redrawRectangle() {
	try
	{
		clearMap();
		point = new Object();
		point.latLng = new google.maps.LatLng(gob('metadataForm:southBoundingLatitude').value,gob('metadataForm:westBoundingLongitude').value);
		addLatLng(point);
		temp = new Object();
		temp.latLng = new google.maps.LatLng(gob('metadataForm:northBoundingLatitude').value,gob('metadataForm:eastBoundingLongitude').value);
		addLatLng(temp);
		map.setCenter(point.latLng);
	}
	catch (e)
	{
		
	}
}