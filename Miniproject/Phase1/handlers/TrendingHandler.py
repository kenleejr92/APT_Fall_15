__author__ = 'kenlee'

import webapp2
import jinja2
import os
from Stream import Stream
from google.appengine.api import users

class TrendingHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        #Get the list of streams
        streams = Stream.query().order(-Stream.views)

        template_values = {
            'streams':streams
        }

        template = JINJA_ENVIRONMENT.get_template('TrendingStreamsPage.html')
        self.response.write(template.render(template_values))