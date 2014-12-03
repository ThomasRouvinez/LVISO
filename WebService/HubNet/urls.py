from django.conf.urls import patterns, url
from HubNet import views

urlpatterns = patterns('',
	# General view, returns the version.
    url(r'version', views.version, name='Version'),
	
	# Inputs record data view, returns success value.
    url(r'irec/$', views.input_record, name='Input Record'),
	
	# Outputs live feed for the required timestamp.
    url(r'olvf/$', views.output_getLiveUpdate, name='Output Live Feed'),
	
	# Outputs configuration for an event.
	url(r'^cfg/(?P<eventID>\d+)/$', views.output_config, name='Output Configuration'),
	
	# Outputs all records for an event.
	url(r'^arec/(?P<eventID>\d+)/$', views.output_all_records, name='Output All Records'),
)