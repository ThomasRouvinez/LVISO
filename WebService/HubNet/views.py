from django.shortcuts import render
from django.http import HttpResponse, HttpRequest
from django.views.decorators.csrf import csrf_exempt

import logging

# Get the version of the webservice.
def version(request):
	return HttpResponse("v0.1A")

# Post a new record for an event & sensor.
@csrf_exempt
def input_record(request):
	if request.is_ajax():
		if request.method == 'POST':
			try:
				data=json.loads(request.body)
				i_timestamp=data['timestamp']
				i_sensor=data['sensorID']
				i_event=data['eventID']
				i_valuesArray=data['values']
				
			except:
				logging.debug('record failed')
			
	return HttpResponse('OK')

# Get updated live feed.
def output_getLiveUpdate(request):
	return HttpResponse("Live Feed")