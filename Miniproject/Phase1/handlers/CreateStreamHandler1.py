__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.api import users
from handlers.Stream import Stream

#/create_stream
class CreateStreamHandler1(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)




        template = JINJA_ENVIRONMENT.get_template('CreateStreamPage.html')
        self.response.write(template.render())