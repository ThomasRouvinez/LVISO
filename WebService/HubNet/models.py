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
		
	class Meta:
		ordering = ('description',)

# DB Participant fields.
class Participant(models.Model):
	tagId = models.CharField(max_length=255)
	gender = models.CharField(max_length=1)
	interestTag = models.ForeignKey(InterestTag, blank=True, null=True)
	
	def __str__(self):
		return self.tagId
		
	class Meta:
		ordering = ('tagId',)
	
# DB Sensor fields.
class Sensor(models.Model):
	identifier = models.IntegerField(unique=True)
	description = models.CharField(max_length=200)
	x = models.FloatField()
	y = models.FloatField()
	radius = models.FloatField()
	
	def __str__(self):
		return "Sensor: " + self.description
		
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
		
# DB recorded activity per sensor.
class Record(models.Model):
	tagId = models.CharField(max_length=255)
	timeStamp = models.DateTimeField('time stamp', null=True)
	rssi = models.FloatField()
	event = models.ForeignKey(Event, null=True)
	sensor = models.ForeignKey(Sensor, null=True)
	
	def __str__(self):
		return self.tagId
		
	class Meta:
		ordering = ('tagId',)
