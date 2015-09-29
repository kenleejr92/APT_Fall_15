__author__ = 'kenlee'

import webapp2
import jinja2
import os
from Stream import Stream
from google.appengine.api import users

class MainPageHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        login_url = users.create_login_url('/management')

        if user :
            self.redirect('/management')

        #Get the list of streams
        streams = Stream.query()

        template_values = {
            'login_url':login_url,
        }

        template = JINJA_ENVIRONMENT.get_template('MainPage.html')
        self.response.write(template.render(template_values))