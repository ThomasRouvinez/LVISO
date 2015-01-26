from django.conf.urls import patterns, url
from HubNet import views

urlpatterns = patterns('',
	# ---------------------------------------------------------------------------------------------------
	# Server information methods.
	# ---------------------------------------------------------------------------------------------------

	# General view, returns the version.
    url(r'version', views.version, name='Version'),
	
	# General view, returns the version.
    url(r'time/(?P<diff>\d+)', views.time, name='Time'),
	
	# ---------------------------------------------------------------------------------------------------
	# Inputs methods.
	# ---------------------------------------------------------------------------------------------------
	
	# Inputs record data view, returns success value.
    url(r'irec/$', views.input_record, name='Input Record'),
	
	# Inputs a new time marker.
    url(r'mrkr/(?P<eventID>\d+)/(?P<label>.+).*', views.input_marker, name='Input Marker'),
	
	# ---------------------------------------------------------------------------------------------------
	# Statistics methods.
	# ---------------------------------------------------------------------------------------------------
	
	# Outputs all markers for a given event.
    url(r'statmrkr/(?P<eventID>\d+).*', views.output_statMarkers, name='Output Marker'),
	
	# Outputs all records for a given event and sensor.
    url(r'statdata/(?P<eventID>\d+)/(?P<sensorID>\d+).*', views.output_statData, name='Output Sensor Statistical data'),
	
	# ---------------------------------------------------------------------------------------------------
	# Live visualization methods.
	# ---------------------------------------------------------------------------------------------------
	
	# Outputs distinct last update.
	url(r'^lvd(?P<eventID>\d+)/$', views.output_live_distinct, name='Output Live Distinct'),
	
	# Outputs live feed for the required timestamp.
    url(r'lvf/(?P<eventID>\d+)/(?P<timeStamp>.+).*', views.output_getLiveUpdate, name='Output Live Feed'),
	
	# Outputs configuration for an event.
	url(r'^cfg(?P<eventID>\d+).*', views.output_config, name='Output Configuration'),
	
	# Outputs all records for an event.
	url(r'^arec/(?P<eventID>\d+).*', views.output_all_records, name='Output All Records'),
	
	# Outputs all interest tags for an event.
	url(r'^atag/(?P<eventID>\d+).*', views.output_all_interestTags, name='Output All Interest Tags'),
	
	# Outputs all participants for an event.
	url(r'^apar/(?P<eventID>\d+).*', views.output_all_participants, name='Output All Participants'),
)