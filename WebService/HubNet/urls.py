from django.conf.urls import patterns, url
from HubNet import views

urlpatterns = patterns('',
	# General view, returns the version.
    url(r'version', views.version, name='Version'),
	
	# Input record data view, returns success value.
    url(r'irec/$', views.input_record, name='Input Record'),
	
	# Output live feed for the required timestamp.
    url(r'olvf$', views.output_getLiveUpdate, name='Output Live Feed'),
)