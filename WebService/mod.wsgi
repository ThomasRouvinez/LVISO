import os, sys
sys.path.append('C:\\xampp\\htdocs\\WebService')
os.environ['DJANGO_SETTINGS_MODULE'] = 'WebService.settings'

import django.core.handlers.wsgi
applicatoin = django.core.handlers.wsgi.WSGIHandler()