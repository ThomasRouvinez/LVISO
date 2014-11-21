from django.shortcuts import render
from django.http import HttpResponse

# Get the version of the webservice.
def version(request):
	return HttpResponse("v0.1A")

# Post a new record for an event & sensor.
def input_record(request):
	return HttpResponse("Input recording")

# Get updated live feed.
def output_getLiveUpdate(request):
	return HttpResponse("Live Feed")