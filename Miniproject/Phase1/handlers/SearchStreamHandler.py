__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
from google.appengine.ext import blobstore
from google.appengine.ext import ndb
from google.appengine.api import users
from handlers.Stream import Stream

#/search_streams
class SearchStreamHandler(webapp2.RequestHandler):
    def get(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)
        template = JINJA_ENVIRONMENT.get_template('SearchStreamsPage.html')
        self.response.write(template.render())

    def post(self):
        JINJA_ENVIRONMENT = jinja2.Environment(
        loader=jinja2.FileSystemLoader('templates'),
        extensions=['jinja2.ext.autoescape'],
        autoescape=True)

        query_string = cgi.escape(self.request.get('query_string'))
        search_results = Stream.query(Stream.name == query_string)
        template_values = {
            'query_string':query_string,
            'search_results':search_results
        }

        template = JINJA_ENVIRONMENT.get_template('SearchStreamsPage.html')
        self.response.write(template.render(template_values))