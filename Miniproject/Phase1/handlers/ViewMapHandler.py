__author__ = 'kenlee'

import webapp2
import jinja2
import json
from Stream import Stream
from google.appengine.api import users

class ViewMapHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        template = JINJA_ENVIRONMENT.get_template('ViewMapPage.html')
        self.response.write(template.render())