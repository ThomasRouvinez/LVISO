from django.db import models

# Author: Thomas "Raste" Rouvinez
# Creation date: 2014.11.18
# Last modified: 2014.11.18

# DB Color/Interest tags field.
class InterestTag(models.Model):
	description = models.CharField(max_length=200)
	color = models.CharField(max_length=7)
	
	def __str__(self):
		return self.description
	
# DB Participant fields.
class Participant(models.Model):
	tagId = models.CharField(max_length=255)
	gender = models.CharField(max_length=1)
	interestTags = models.ForeignKey(InterestTag)
	
# DB recorded activity per sensor.
class Record(models.Model):
	tagId = models.CharField(max_length=255)
	rssi = models.FloatField()
	
# DB Sensor fields.
class Sensor(models.Model):
	identifier = models.IntegerField(unique=True)
	description = models.CharField(max_length=200)
	x = models.FloatField()
	y = models.FloatField()
	radius = models.FloatField()
	records = models.ForeignKey(Record)

# DB Event fields.
class Event(models.Model):
	name = models.CharField(max_length=200)
	startDate = models.DateTimeField('start date')
	stopDate = models.DateTimeField('stop date')
	participants = models.ForeignKey(Participant)
	interestTags = models.ForeignKey(InterestTag)
	sensors = models.ForeignKey(Sensor)
	