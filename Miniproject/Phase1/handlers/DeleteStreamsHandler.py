__author__ = 'kenlee'

import webapp2
import cgi
from Stream import Stream
from google.appengine.ext import ndb

class DeleteStreamsHandler(webapp2.RequestHandler):
    def post(self):
        stream_names = self.request.get_all('deleted_streams')
        for stream_name in stream_names:
            stream_key = ndb.Key(Stream,stream_name)
            stream_key.delete()
        self.redirect('/management')