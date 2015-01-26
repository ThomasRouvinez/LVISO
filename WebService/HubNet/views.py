from django.shortcuts import render
from django.http import HttpResponse, HttpRequest
from django.views.decorators.csrf import csrf_exempt
from HubNet.models import Record, Event, Sensor, InterestTag, Participant, Marker
from datetime import datetime

import json
import yaml
import datetime
import logging
import urllib
import re

# ---------------------------------------------------------------------------------------------------
# Server information methods.
# ---------------------------------------------------------------------------------------------------

# Get the version of the webservice.
def version(request):
	return HttpResponse("v0.1A")
	
# Get the time of the webservice.
def time(request, diff):
	if 'callback' in request.REQUEST:
		# Send a JSONP response.
		data = '%s({\'timeStamp\' : \'%s\'});' % (request.REQUEST['callback'], (datetime.datetime.now() - datetime.timedelta(seconds=int(diff))).replace(microsecond=0))
		return HttpResponse(data, "text/javascript")

	return HttpResponse(str((datetime.datetime.now() - datetime.timedelta(seconds=int(diff))).replace(microsecond=0)))
	
# ---------------------------------------------------------------------------------------------------
# Inputs methods.
# ---------------------------------------------------------------------------------------------------

# Post a new marker.
@csrf_exempt
def input_marker(request, eventID, label):
	if request.method == 'POST':
		try:
			i_eventID = int(str(unicode(eventID)))
			i_label = str(unicode(label))
			c_timestamp = datetime.datetime.now()
			
			# Acquire relations in database.
			e = Event.objects.get(pk=i_eventID)
			
			# Add new marker.
			Marker.objects.create(label=i_label, timeStamp=c_timestamp, event=e)
			
		except:
			logging.debug('Posting marker failed')
			return HttpResponse('FAILED')

	return HttpResponse("OK")
	
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
			c_timestamp = datetime.datetime.strptime(str(i_timeStamp), "%Y-%m-%d %H:%M:%S")
			# Acquire relations in database.
			e = Event.objects.get(pk=i_eventID)
			s = Sensor.objects.get(identifier=i_sensorID)

			# Write the records.
			for rec in i_records:
				Record.objects.create(event=e, sensor=s, timeStamp=c_timestamp, tagId=rec["tag"], rssi=rec["rssi"])
				
		except:
			logging.debug('record failed')
			return HttpResponse('FAILED')
			
	return HttpResponse("OK")
	
# ---------------------------------------------------------------------------------------------------
# Statistics methods.
# ---------------------------------------------------------------------------------------------------
	
# Get all the markers for a given event.
def output_statMarkers(request, eventID):
	if request.method == 'GET':	
		i_eventID = int(str(unicode(eventID)))
		
		# Get starting time of the event to compute the time difference.
		eventObject = Event.objects.filter(pk=i_eventID)
		eventTime = eventObject[0].startDate
	
		# Gather list of markers.
		markers = Marker.objects.filter(event__pk=i_eventID)
		count = Marker.objects.filter(event__pk=i_eventID).count()
		results = '['
		
		if(count > 0):
			for x in range(0, count):
				# Compute the time difference.
				diff = ((markers[x].timeStamp - eventTime).seconds / 60) +1
			
				# Add the data to the json response.
				if(x != count -1):
					results += '{"time": ' + str(diff) + ', "label": "' + markers[x].label + '"}, '
				else:
					results += '{"time": ' + str(diff) + ', "label": "' + markers[x].label + '"}]'
		else:
			results = '[]'
			
		if 'callback' in request.REQUEST:
				# Send a JSONP response.
				data = '%s(%s);' % (request.REQUEST['callback'], results)
				return HttpResponse(data, "text/javascript")
		
	return HttpResponse(json.dumps(results), "application/json")
	
# Get all records for a given event and sensor.
def output_statData(request, eventID, sensorID):
	if request.method == 'GET':	
		try:
			# Gather required data.
			i_eventID = int(str(unicode(eventID)))
			i_sensorID = int(str(unicode(sensorID)))
			
			eventObject = Event.objects.filter(pk=i_eventID)
			eventTime = eventObject[0].startDate
			
			records = Record.objects.filter(event__pk=i_eventID).filter(sensor__identifier=i_sensorID).order_by('timeStamp')
			count = Record.objects.filter(event__pk=i_eventID).filter(sensor__identifier=i_sensorID).count()
			
			# Determine the number of minutes of the records.
			if(count > 1):
				tdelta = records[count-1].timeStamp.replace(second=0, microsecond=0) - records[0].timeStamp.replace(second=0, microsecond=0)
				minutes = (tdelta.seconds/60) +1
				statistics = [0] * minutes
				
				# Crunch the data.
				for x in range(0, count):
					index = ((records[x].timeStamp.replace(second=0, microsecond=0) - records[0].timeStamp.replace(second=0, microsecond=0)).seconds/60)
					statistics[index] += 1;
					
				# Format the values to json.
				results = '['
				
				minutesCount = 0
				startTime = records[0].timeStamp.replace(second=0, microsecond=0)
				eventTmpTime = eventTime.replace(second=0, microsecond=0)
				timeDiffEventBegin = (startTime - eventTmpTime).seconds /60
				
				for z in range(0, timeDiffEventBegin):
					results += '{"time": ' + str(minutesCount) + ', "value": 0}, '
					minutesCount += 1
				
				for y in range(0, minutes):
					# Add the data to the json response.
					if(y != minutes -1):
						results += '{"time": ' + str(minutesCount) + ', "value": ' + str(statistics[y]) + '}, '
						minutesCount += 1
					else:
						results += '{"time": ' + str(minutesCount) + ', "value": ' + str(statistics[y]) + '}]'

				if 'callback' in request.REQUEST:
					# Send a JSONP response.
					data = '%s(%s);' % (request.REQUEST['callback'], results)
					return HttpResponse(data, "text/javascript")
					
			else:
				return HttpResponse('NONE')
		except:
			logging.debug('request record failed')
			return HttpResponse('FAILED')
			
	#return HttpResponse(json.dumps(results), "application/json")
	return HttpResponse(results)

# ---------------------------------------------------------------------------------------------------
# Live visualization methods.
# ---------------------------------------------------------------------------------------------------
	
# Get updated live feed.
@csrf_exempt
def output_getLiveUpdate(request, eventID, timeStamp):
	if request.method == 'GET':
		try:
			i_eventID = int(str(unicode(eventID)))
			i_timeStamp = str(unicode(timeStamp))
			
			# Prepare request context.
			c_timestamp = datetime.datetime.strptime(i_timeStamp, "%Y-%m-%d %H:%M:%S")
			
			# Gather list of records.
			records = Record.objects.filter(event__pk=i_eventID).filter(timeStamp=c_timestamp).exclude(sensor__displayable=False)
			
			# Sort distinct results.
			distinctRecords = []
			seen = set()
		
			for rec in records:
				if rec.tagId not in seen:
					distinctRecords.append(rec)
					seen.add(rec.tagId)
		
			results = [ob.as_json() for ob in distinctRecords]
			print results
			
			if 'callback' in request.REQUEST:
                # Send a JSONP response.
				data = '%s(%s);' % (request.REQUEST['callback'], results)
				return HttpResponse(data, "text/javascript")
			
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
			
			if 'callback' in request.REQUEST:
                # Send a JSONP response.
				data = '%s(%s);' % (request.REQUEST['callback'], results)
				return HttpResponse(data, "text/javascript")
			
		except:
			logging.debug('request live distinct failed')
			return HttpResponse('{[]}', "application/json")
				
	return HttpResponse(json.dumps(results), "application/json")

		
# Get config file.
@csrf_exempt
def output_config(request, eventID):
	if request.method == 'GET':
		try:
			sensors = Sensor.objects.filter(event__pk = eventID).exclude(displayable=False)
			results = [ob.as_json() for ob in sensors]
			
			if 'callback' in request.REQUEST:
                # Send a JSONP response.
				data = '%s(%s);' % (request.REQUEST['callback'], results)
				return HttpResponse(data, "text/javascript")
				
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
			
			if 'callback' in request.REQUEST:
                # Send a JSONP response.
				data = '%s(%s);' % (request.REQUEST['callback'], results)
				return HttpResponse(data, "text/javascript")
			
		except:
			logging.debug('request all interest tags failed')
			return HttpResponse('FAILED')
				
	return HttpResponse(json.dumps(results), "application/json")