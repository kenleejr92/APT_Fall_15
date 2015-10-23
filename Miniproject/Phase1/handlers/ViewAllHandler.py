__author__ = 'kenlee'

import webapp2
import json
import os
import cgi
import jinja2
import datetime
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.ext.webapp import blobstore_handlers
from google.appengine.api import images
from google.appengine.api import users
from Stream import Stream
from Stream import Photo
from BaseHandler import BaseHandler

class ViewAllHandler(webapp2.RequestHandler,BaseHandler):
    def get(self):
        self.cache('view')

        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        user = users.get_current_user()
        user_id = user.user_id()

        #Get the list of streams
        my_streams = Stream.query(Stream.owner_id == user_id).order(Stream.timestamp)
        template_values = {
            'my_streams':my_streams,
        }

        template = JINJA_ENVIRONMENT.get_template('ViewAllStreamsPage.html')
        self.response.write(template.render(template_values))