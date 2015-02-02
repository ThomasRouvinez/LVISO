// -----------------------------------------------------------
// Variables.
// -----------------------------------------------------------

// Constants.
final int rssiThreshold = 12;	// to be defined with real rests on the readers.
final int width = window.innerWidth;
final int height = window.innerHeight;
final float framerate = 1;
final int tableSize = height / 12.5;
final int firstRawSize = height / 7;

// Lists.
ArrayList sensors;
ArrayList participants;
ArrayList participantsTemp;
ArrayList participantsOld;
ArrayList positioningValues; //([0] = multf)

// Matrices.
int[][] slidingWindow;

// Hashmaps.
HashMap<String,String> participantColor;
HashMap<String,Integer> participantAVGIndex;
HashMap<String,String> interestTags;
HashMap<Integer,Integer> tableCountFirstRow;
HashMap<Integer,Integer> tableDrawnFirstRow;
HashMap<Integer,Integer> tableCountSecondRow;
HashMap<Integer,Integer> tableDrawnSecondRow;

// -----------------------------------------------------------
// Setup & Initialization.
// -----------------------------------------------------------

void setup() {
	size(width,height);
	frameRate(framerate);
	
	sensors = new ArrayList();
	participants = new ArrayList();
	participantsTemp = new ArrayList();
	participantsOld = new ArrayList();
	positioningValues = new ArrayList();
	
	participantColor = new HashMap<String,String>();
	participantAVGIndex = new HashMap<String,Integer>();
	interestTags = new HashMap<String,String>();
	
	tableCountFirstRow = new HashMap<Integer,Integer>();
	tableDrawnFirstRow = new HashMap<Integer,Integer>();
	tableCountSecondRow = new HashMap<Integer,Integer>();
	tableDrawnSecondRow = new HashMap<Integer,Integer>();
}

// -----------------------------------------------------------
// Game loop.
// -----------------------------------------------------------

void draw(){
	// Background.
	fill(#ffffff);
	rect(0, 0, width, height-(height / 10));
	
	// Draw sensors backgrounds.
	for(int p=0, end=sensors.size(); p<end; p++){
		Sensor pt = (Sensor) sensors.get(p);
		
		if(tableCountFirstRow.get(pt.id) > 0){
			stroke(0,0,0);
			fill(#000000);
			ellipse(pt.x,pt.y,firstRawSize,firstRawSize);
		}
	}
	
	// Draw all participants.
	for(int p=0, end=participantsOld.size() ; p < end ; p++){
		Participant pa = (Participant) participantsOld.get(p);
		
		// Draw in the same order common participants.
		if(participants.contains(pa) == true){
			participantsTemp.add(pa);
			participants.remove(pa);
			drawParticipant(pa);
		}
	}
	
	// Draw the rest of the participants.
	for(int k=0, end=participants.size() ; k < end ; k++){
		Participant pb = (Participant) participants.get(k);
		participantsTemp.add(pb);
		drawParticipant(pb);
	}
	
	// Draw all sensors last to cover up the colors.
	for(int p=0; p < sensors.size(); p++){
		Sensor pt = (Sensor) sensors.get(p);
		pt.draw();
		
		fill(#ffffff);
		textSize(30);
		text("" + pt.id, pt.x - (textWidth("" + pt.id)/2), pt.y + 10);
	}
	
	clearParticipants();
	noLoop();
}

// -----------------------------------------------------------
// Functions.
// -----------------------------------------------------------

Sensor addSensor(int id, int x, int y, int radius){
	Sensor pt = new Sensor(id, x, y, radius);
    sensors.add(pt);
    return pt;
}

Participant addParticipant(int sensorID, float rssi, String tagID){
	// Update the sliding window and get the smoothed value for the sensorID.
	slidingWindow[0][participantAVGIndex.get(tagID)] = sensorID;
	
	// Add new participant.
	Participant pa = new Participant(getSlidingWindowAVG(participantAVGIndex.get(tagID)), rssi, tagID);
	participants.add(pa);
	return pa;
}

void addParticipantConfig(String tagID, String color){
	String tagColor = color.replace('#','');
	participantColor.put(tagID, tagColor);
}

void addParticipantIndex(String tagID, int index){
	participantAVGIndex.put(tagID, index);
}

void createSlidingWindow(size){
	slidingWindow = new int[5][size];
	
	// Initialize the matrix when first configuring the view.
	for(int i = 0 ; i < slidingWindow.length ; i++){
		for(int j = 0 ; j < size ; j++){
			slidingWindow[i][j] = 0;
		}
	}
}

int getSlidingWindowAVG(int participantIndex){
	// Check if any activity is recorded.
	if(slidingWindow[0][participantIndex] == 0){
		return 0;
	}
	else{
		HashMap<Integer, Integer> cache = new HashMap<Integer, Integer>();
	
		// Compute the number of occurences in the sliding window.
		for(int i = 0 ; i < slidingWindow.length ; i++){
			if(cache.containsKey(slidingWindow[i][participantIndex])){
				cache.put(slidingWindow[i][participantIndex], cache.get(slidingWindow[i][participantIndex]) + 1);
			}
			else{
				cache.put(slidingWindow[i][participantIndex], 1);
			}
		}

		// Select biggest number of occurences.
		int value = 0;
		int occurencesMax = 0;
		
		for (Map.Entry<Integer, Integer> entry : cache.entrySet()) {
			if(entry.getValue() > occurencesMax){
				occurencesMax = entry.getValue();
				value = entry.getKey();
			}
		}
		
		return value;
	}
}

void clearParticipants(){
	// Shift all the values in the sliding window.
	for(int i = slidingWindow.length -1 ; i > 0 ; i--){
		for(int j = 0 ; j < slidingWindow[0].length ; j++){
			slidingWindow[i][j] = slidingWindow[i-1][j];
		}
	}
	
	for(int k = 0 ; k < slidingWindow[0].length ; k++){
		slidingWindow[0][k] = 0;
	}

	// Remove all participants from last iteration.
	participantsOld = participantsTemp;
	participantsTemp = new ArrayList();
	participants = new ArrayList();
	
	for (Map.Entry entry : tableCountFirstRow.entrySet()){
		tableCountFirstRow.put(entry.getKey(), 0);
		tableDrawnFirstRow.put(entry.getKey(), 0);
	}
}

void addPosValue(float value){
	positioningValues.add(value);
}

void addInterestColor(String color, String description){
	if(interestTags.containsKey(color.replace('#','')) == false){
		String tagColor = color.replace('#','');
		interestTags.put(tagColor, description);
	}
}

void drawSegment(int x, int y, int width, int height, float startAngle, float stopAngle){ 
	arc(x, y, width, height, radians(startAngle), radians(stopAngle));
}

void drawParticipant(Participant pa){
	if(pa.sensorID != 0){
		// Compute slice representation.
		float slice = 360.0 / tableCountFirstRow.get(pa.sensorID);
		int drawn = tableDrawnFirstRow.get(pa.sensorID);
		
		// Get appropriate color.
		String tagColor = participantColor.get(pa.tagID);
		tagColor = "FF" + tagColor;
		
		// Draw the participant.
		fill(unhex(tagColor));
		stroke(0, 0, 0);
		drawSegment(pa.sensor.x, pa.sensor.y, firstRawSize, firstRawSize, (drawn * slice)+2, ((drawn + 1) * slice) -2);
		tableDrawnFirstRow.put(pa.sensorID, tableDrawnFirstRow.get(pa.sensorID) + 1);
	}
}

void drawLegendBar(){
	// Compute display.
	int count = interestTags.size();
	int spacing = (width - (width / 100)) / count;
	int leftMargin = (width / 100);
	int topMargin = height -(height / 10);
	int fontSize = width / 90;
	long tabulation = leftMargin;
	
	// Draw the bar.
	fill(#000000);
	rect(0, height-(height / 10), width, (height / 10));
	
	// Draw the legends.
	for (Map.Entry entry : interestTags.entrySet()){
		// Get appropriate color.
		String tagColor = entry.getKey();
		tagColor = "FF" + tagColor;
		
		// Draw color tag and text.
		fill(unhex(tagColor));
		rect(tabulation, topMargin + (height/100), (height / 22), (height / 22));
		
		fill(#ffffff);
		textSize(fontSize);
		text(entry.getValue(), tabulation + (height / 20), topMargin + (height / 25)); 

		tabulation += textWidth(entry.getValue()) + (height / 10);
	}
}

// -----------------------------------------------------------
// Classes.
// -----------------------------------------------------------

class Sensor{
    int id, x, y, radius;
	
    Sensor(int id, int x, int y, int radius){
		this.id=id; 
		this.x=x; 
		this.y=y; 
		this.radius=radius;

		// Initialize the tables.
		tableCountFirstRow.put(this.id, 0);
		tableDrawnFirstRow.put(this.id, 0);
	}
	
    void draw() {
		stroke(255,255,255);
		fill(#000000);
		ellipse(x,y,tableSize,tableSize);
	}
}

class Participant{
	String tagID;
	int sensorID;
	float rssi;
	Sensor sensor;
	
	Participant(int sensorID, float rssi, String tagID){
		this.sensorID=sensorID; 
		this.rssi=rssi;
		this.tagID=tagID;
		
		// Find table x-y coordinates.
		for(int p=0, end=sensors.size(); p<end; p++){
			Sensor pt = (Sensor) sensors.get(p);
			
			if(pt.id == sensorID){
				this.sensor = pt;
				tableCountFirstRow.put(this.sensorID, tableCountFirstRow.get(sensorID) + 1);
				break;
			}
		}
	}
}