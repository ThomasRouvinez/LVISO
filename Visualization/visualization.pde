// -----------------------------------------------------------
// Variables.
// -----------------------------------------------------------

final int width = window.innerWidth;
final int height = window.innerHeight;
final float framerate = 60;

// Lists.
ArrayList sensors;
ArrayList positioningValues; //([0] = multf)

// -----------------------------------------------------------
// Setup.
// -----------------------------------------------------------

void setup() {
	size(width,height);
	frameRate(framerate);
	sensors = new ArrayList();
	positioningValues = new ArrayList();
	
	// Initial setup.
	background(200);
	fill(#000000);
	rect(0, height-70, width, 70);
}

// -----------------------------------------------------------
// Draw.
// -----------------------------------------------------------

void draw(){
	// Draw all sensors.
	for(int p=0, end=sensors.size(); p<end; p++) {
		Sensor pt = (Sensor) sensors.get(p);
		pt.draw(); 
	}
}

// -----------------------------------------------------------
// Events.
// -----------------------------------------------------------

void mouseClicked() {
	//addSensor(2, mouseX, mouseY, 75);
}

// -----------------------------------------------------------
// Functions.
// -----------------------------------------------------------

Point addSensor(int id, int x, int y, int radius) {
	Sensor pt = new Sensor(id, x, y, radius);
    sensors.add(pt);
    return pt;
}

void addPosValue(float value){
	positioningValues.add(value);
}

// -----------------------------------------------------------
// Classes.
// -----------------------------------------------------------

class Sensor{
    int id, x, y, radius;
	
    Sensor(int id, int x, int y, int radius) {this.id=id; this.x=x; this.y=y; this.radius=radius}
	
    void draw() {
		stroke(255,255,255);
		fill(#000000);
		ellipse(x,y,60,60);
	}
}