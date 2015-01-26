from django.db import models

# Author: Thomas "Raste" Rouvinez
# Creation date: 2014.11.18
# Last modified: 2014.11.19

# DB Color/Interest tags field.
class InterestTag(models.Model):
	description = models.CharField(max_length=100)
	color = models.CharField(max_length=7)
	
	def __str__(self):
		return self.description	
		
	def as_json(self):
		return dict(
			description = str(self.description),
			color = str(self.color))
		
	class Meta:
		ordering = ('description',)

# DB Participant fields.
class Participant(models.Model):
	reference = models.IntegerField(unique=True, blank=True, null=True)
	tagId = models.CharField(max_length=255)
	sex = models.CharField(max_length=1, blank=True, null=True)
	interestTag = models.ForeignKey(InterestTag, blank=True, null=True)
	
	def __str__(self):
		return str(self.reference) + ' | ' + self.tagId
		
	def as_json(self):
		return dict(
			tagID = str(self.tagId),
			sex = str(self.sex),
			interestTag = self.interestTag.as_json())
		
	class Meta:
		ordering = ('tagId',)
	
# DB Sensor fields.
class Sensor(models.Model):
	identifier = models.IntegerField(unique=True)
	description = models.CharField(max_length=200)
	x = models.FloatField()
	y = models.FloatField()
	rssiThreshold = models.FloatField()
	radius = models.FloatField()
	displayable = models.BooleanField(default=True)
	
	def __str__(self):
		return "Sensor: " + self.description
		
	def as_json(self):
		return dict(
			identifier = self.identifier,
			description = str(self.description),
			x = self.x,
			y = self.y,
			rssiThreshold = self.rssiThreshold,
			radius = self.radius)
		
	class Meta:
		ordering = ('description', 'identifier', )
		
# DB Event fields.
class Event(models.Model):
	name = models.CharField(max_length=200)
	startDate = models.DateTimeField('start date')
	stopDate = models.DateTimeField('stop date')
	interestTags = models.ManyToManyField(InterestTag, blank=True, null=True)
	participants = models.ManyToManyField(Participant, blank=True, null=True)
	sensors = models.ManyToManyField(Sensor, blank=True, null=True)

	def __str__(self):
		return self.name	
		
	class Meta:
		ordering = ('name',)

# Temporal markers.
class Marker(models.Model):
	label = models.CharField(max_length=200)
	timeStamp = models.DateTimeField('timeStamp')
	event = models.ForeignKey(Event)
	
	def __str__(self):
		return "Marker: " + self.label
		
	def as_json(self):
		return dict(
			timeStamp = str(self.timeStamp.replace(second=0, microsecond=0))[11:-6],
			label = str(self.label)
		)
		
	class Meta:
		ordering = ('timeStamp', 'label', )
		
class Record(models.Model):
	tagId = models.CharField(max_length=255)
	timeStamp = models.DateTimeField('time stamp', null=True)
	rssi = models.FloatField()
	event = models.ForeignKey(Event, null=True)
	sensor = models.ForeignKey(Sensor, null=True)
	
	def __str__(self):
		return self.tagId
		
	def as_json(self):
		return dict(
			eventID = self.event.pk,
			sensorID = self.sensor.identifier,
			timeStamp = str(self.timeStamp),
			tagID = str(self.tagId),
			rssi = self.rssi)
		
	class Meta:
		ordering = ('tagId',)