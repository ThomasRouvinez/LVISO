from django.conf.urls import patterns, url
from HubNet import views

urlpatterns = patterns('',
	# General view, returns the version.
    url(r'version', views.version, name='Version'),
	
	# Inputs record data view, returns success value.
    url(r'irec/$', views.input_record, name='Input Record'),
	
	# Outputs live feed for the required timestamp.
    url(r'lvf/$', views.output_getLiveUpdate, name='Output Live Feed'),
	
	# Outputs distinct last update.
	url(r'^lvd/(?P<eventID>\d+)/$', views.output_live_distinct, name='Output Live Distinct'),
	
	# Outputs configuration for an event.
	url(r'^cfg(?P<eventID>\d+).*', views.output_config, name='Output Configuration'),
	
	# Outputs all records for an event.
	url(r'^arec/(?P<eventID>\d+)/$', views.output_all_records, name='Output All Records'),
	
	# Outputs all interest tags for an event.
	url(r'^atag/(?P<eventID>\d+)/$', views.output_all_interestTags, name='Output All Interest Tags'),
	
	# Outputs all participants for an event.
	url(r'^apar/(?P<eventID>\d+)/$', views.output_all_participants, name='Output All Participants'),
)