from django.contrib import admin
from HubNet.models import Event, InterestTag

# Register your models here.
class EventAdmin(admin.ModelAdmin):
    fieldsets = [
        (None,					{'fields': ['name']}),
        ('Date information',	{'fields': ['startDate', 'stopDate']}),
		('Participants',		{'fields': ['participants']}),
		('Tags of Interest',	{'fields': ['interestTags']}),
		('Sensors',				{'fields': ['sensors']}),
    ]

admin.site.register(Event, EventAdmin)
admin.site.register(InterestTag)