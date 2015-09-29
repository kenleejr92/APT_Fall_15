__author__ = 'kenlee'

import webapp2
import jinja2
import os
from Stream import Stream
from google.appengine.api import users

class ViewAllHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()

        #Get the list of streams
        my_streams = Stream.query(Stream.owner_id == user_id)
        template_values = {
            'my_streams':my_streams,
        }

        template = JINJA_ENVIRONMENT.get_template('ViewAllStreamsPage.html')
        self.response.write(template.render(template_values))