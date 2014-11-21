from django.conf.urls import patterns, url

from HubNet import views

urlpatterns = patterns('',
    url(r'^$', views.index, name='index'),
)