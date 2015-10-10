__author__ = 'kenlee'

import webapp2
import jinja2
import cgi
import json
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

        #test search header
        searchHead = JINJA_ENVIRONMENT.get_template('Header.html')
        self.response.write(searchHead.render(current = 'search'))

        #Get the list of streams
        streams = Stream.query()

        streamNames = []
        for stream in streams:
            streamNames.append(str(stream.name))

        template_values = {
            'streams':streams
        }

        template = JINJA_ENVIRONMENT.get_template('Search.html')
        self.response.write(template.render(streams = json.dumps(streamNames)))

    def post(self):
        query_string = cgi.escape(self.request.get('query_string'))
        queryStream = Stream.query(Stream.name == query_string)
        entity = queryStream.get()
        if entity is not None:
            goToStream = "/view_stream/?stream_name=" + query_string
            self.redirect(goToStream)
        else:
            self.response.write("That stream does not exist")

