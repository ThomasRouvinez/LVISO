from django.shortcuts import render
from django.http import HttpResponse, HttpRequest
from django.views.decorators.csrf import csrf_exempt
from HubNet.models import Record, Event, Sensor, InterestTag, Participant
from datetime import datetime

import json
import yaml
import datetime
import logging
import urllib
import re

# --------------------------------------------------------------
# Webservice inputs/outputs.
# --------------------------------------------------------------

# Get the version of the webservice.
def version(request):
	return HttpResponse("v0.1A")

# Post a new record for an event & sensor.
@csrf_exempt
def input_record(request):	
	if request.method == 'POST':
		try:
			# Read data from the JSON payload.
			raw = yaml.load(request.body)
			str_data = str(raw).replace("'", '"')
			data = json.loads(str_data)
	
			i_eventID = data["eventID"]
			i_sensorID = data["sensorID"]
			i_timeStamp = data["timeStamp"]
			i_records = data["records"]
			
			# Convert time stamps correctly.
			c_timestamp = datetime.datetime.strptime(str(i_timeStamp), "%Y/%m/%d %H:%M:%S")
			
			# Acquire relations in database.
			e = Event.objects.get(pk=i_eventID)
			s = Sensor.objects.get(pk=i_sensorID)
			
			# Write the records.
			for rec in i_records:
				Record.objects.create(event=e, sensor=s, timeStamp=c_timestamp, tagId=rec["tag"], rssi=rec["rssi"])
			
		except:
			logging.debug('record failed')
			return HttpResponse('FAILED')
			
	return HttpResponse("OK")

# Get updated live feed.
@csrf_exempt
def output_getLiveUpdate(request):
	if request.method == 'GET':
		try:
			# Read data from the JSON payload.
			raw = yaml.load(request.body)
			str_data = str(raw).replace("'", '"')
			data = json.loads(str_data)
			
			i_eventID = data["eventID"]
			i_timeStamp = data["timeStamp"]
			
			# Prepare request context.
			c_timestamp = datetime.datetime.strptime(str(i_timeStamp), "%Y/%m/%d %H:%M:%S")
			
			# Gather list of records.
			list = Record.objects.filter(event_pk=i_eventID).filter(timeStamp=c_timestamp)
			
			results = [ob.as_json() for ob in list]
			
		except:
			logging.debug('request record failed')
			return HttpResponse('FAILED')
		
		return HttpResponse(json.dumps(results), "application/json")
		
# Get distinct live records.
def output_live_distinct(request, eventID):
	if request.method == 'GET':
		try:
			# Identify latest created record (closest to liveness).
			lastRecord = Record.objects.filter(event__pk=eventID).latest(field_name="timeStamp")
		
			# Get all records from most live timeStamp with distinct tagID.
			records = Record.objects.filter(timeStamp=lastRecord.timeStamp)
		
			distinctRecords = []
			seen = set()
		
			# Get distinct tagIDs.
			for rec in records:
				if rec.tagId not in seen:
					distinctRecords.append(rec)
					seen.add(rec.tagId)
		
			results = [ob.as_json() for ob in distinctRecords]
			
		except:
			logging.debug('request live distinct failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")

		
# Get config file.
def output_config(request, eventID):
	if request.method == 'GET':
		try:
			sensors = Sensor.objects.filter(event__pk = eventID)
			results = [ob.as_json() for ob in sensors]
			
		except:
			logging.debug('request config failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")
	
# Get All records for an event.
def output_all_records(request, eventID):
	if request.method == 'GET':
		try:
			records = Record.objects.filter(event__pk=eventID)
			results = [ob.as_json() for ob in records]
			
		except:
			logging.debug('request all records failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")
	
# Get All records for an event.
def output_all_interestTags(request, eventID):
	if request.method == 'GET':
		try:
			tags = InterestTag.objects.filter(event__pk=eventID)
			results = [ob.as_json() for ob in tags]
			
		except:
			logging.debug('request all interest tags failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")
	
# Get All records for an event.
def output_all_participants(request, eventID):
	if request.method == 'GET':
		try:
			participants = Participant.objects.filter(event__pk=eventID)
			results = [ob.as_json() for ob in participants]
			
		except:
			logging.debug('request all interest tags failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")