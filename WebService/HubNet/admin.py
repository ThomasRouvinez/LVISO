from django.contrib import admin
from HubNet.models import InterestTag, Event, Sensor, Participant, Record

# Author: Thomas "Raste" Rouvinez
# Creation date: 2014.11.18
# Last modified: 2014.11.19

# --------------------------------------------------
# Interest Tags.
# --------------------------------------------------

class InterestTagInline(admin.TabularInline):
	model = Event.interestTags.through
	extra = 1

class InterestTagAdmin(admin.ModelAdmin):
	fieldsets = [
		(None, {'fields': ['description']}),
		(None, {'fields': ['color']}),
	]
	
	list_display = ('description', 'color')
	search_fields = ['description', 'color']
	
# --------------------------------------------------
# Participants.
# --------------------------------------------------

class ParticipantsInline(admin.TabularInline):
	model = Event.participants.through
	extra = 1
	
class ParticipantsAdmin(admin.ModelAdmin):
	fieldsets = [
		(None, {'fields': ['tagId']}),
		(None, {'fields': ['gender']}),
		(None, {'fields': ['interestTag']}),
	]
	
# --------------------------------------------------
# Sensors.
# --------------------------------------------------

class SensorsInline(admin.TabularInline):
	model = Event.sensors.through
	extra = 1

class SensorsAdmin(admin.ModelAdmin):
	fieldsets = [
		('Sensor Identification', {'fields': ['identifier', 'description']}),
		('Properties', {'fields': ['x', 'y', 'radius']}),
	]
	
# --------------------------------------------------
# Events.
# --------------------------------------------------

class EventAdmin(admin.ModelAdmin):
	fieldsets = [
		(None, {'fields': ['name']}),
		(None, {'fields': ['startDate', 'stopDate']}),
	]

	inlines = [InterestTagInline, SensorsInline, ParticipantsInline]
	list_display = ('name', 'startDate')
	search_fields = ['name', 'startDate']
	
admin.site.register(Event, EventAdmin)
admin.site.register(InterestTag, InterestTagAdmin)
admin.site.register(Participant, ParticipantsAdmin)
admin.site.register(Sensor, SensorsAdmin)